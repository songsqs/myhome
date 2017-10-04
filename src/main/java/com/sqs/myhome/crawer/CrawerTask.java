package com.sqs.myhome.crawer;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sqs.myhome.dao.HouseDao;
import com.sqs.myhome.vo.House;

/**
 * 
 * @author songqingshan
 *         https://bj.lianjia.com/ershoufang/housestat?hid=101101844835&rid={小区id}
 *         得到小区信息，如经纬度等<br>
 *         https://bj.lianjia.com/tools/calccost?house_code=101101844835 房贷计算
 */
@Service
public class CrawerTask {
	private static final Logger LOG = LoggerFactory.getLogger(CrawerTask.class);

	private static final ExecutorService EXEC = new ThreadPoolExecutor(10, 20, 30, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>(1024));

	/**
	 * 房子小区信息，主要获取经纬度
	 */
	private static final String GET_HOUSE_XIAOQU_INFO_URL = "https://bj.lianjia.com/ershoufang/housestat?hid=%s&rid=%s";
	/**
	 * 房贷计算器
	 */
	private static final String GET_HOUSE_CAL_INFO_URL = "https://bj.lianjia.com/tools/calccost?house_code=%s";

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private HouseDao houseDao;


	/**
	 * 添加房子详细信息url task
	 * 
	 * @param detailUrl
	 */
	public void addTask(String detailUrl) {
		try {
			EXEC.execute(new ParseHouseInfoTask(detailUrl));
		} catch (Exception e) {
			LOG.error("addTask error", e);
		}
	}

	/**
	 * 从url获取houseid<br>
	 * https://bj.lianjia.com/ershoufang/101101858879.html
	 * 
	 * @param detailUrl
	 * @return
	 */
	private String getHouseIdFromUrl(String detailUrl) {
		if (StringUtils.isEmpty(detailUrl)) {
			return null;
		}

		if (!detailUrl.endsWith(".html")) {
			return null;
		}

		return detailUrl.substring(detailUrl.lastIndexOf("/") + 1, detailUrl.indexOf(".html"));

	}

	/**
	 * 解析房屋基本信息
	 * 
	 * @param document
	 * @param house
	 */
	private void parseHouseInfo(Document document, House house) {

		// 获取价格信息
		Elements overview = document.select("div.overview");

		Elements price = overview.select("div.content").select("div.price");
		Elements totalPrice = price.select("span.total");
		String totalPriceText = totalPrice.text();
		house.setPrice(new BigDecimal(totalPriceText));
		
		Elements unitPrice = price.select("div.unitPrice").select("span.unitPriceValue");
		String unitPriceValue = unitPrice.html();

		house.setUnitPrice(new BigDecimal(unitPriceValue.substring(0, unitPriceValue.indexOf("<"))));

		parseCourt(overview, house);

		Elements introContentElement = document.select("div.m-content").select("div.box-l")
				.select("#introduction > div").select("div.introContent");

		parseBaseInfo(introContentElement, house);
		parseTranInfo(introContentElement, house);
		parseLoanInfo(house);

	}

	/**
	 * 解析小区信息,从小区信息获取经纬度
	 * 
	 * @param elements
	 *            div.overview
	 * @param house
	 */
	private void parseCourt(Elements elements, House house) {
		Elements courtElement = elements.select("div.content").select("div.aroundInfo").select("div.communityName")
				.select("a.info");

		// <a href="/xiaoqu/1111027379321/" target="_blank" class="info ">世纪村东区</a>
		house.setCourtName(courtElement.text());

		String courtInfo = courtElement.attr("href");
		if (courtInfo.endsWith("/")) {
			courtInfo = courtInfo.substring(0, courtInfo.length() - 1);
		}
		if (courtInfo.startsWith("/xiaoqu/")) {
			courtInfo = courtInfo.substring(courtInfo.indexOf("/xiaoqu/") + 8);
		}

		// courtInfo已经解析为小区id,通过rest接口获取小区信息
		String url = String.format(GET_HOUSE_XIAOQU_INFO_URL, house.getHouseCode(), courtInfo);

		LOG.info("get court info from " + url);

		ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
		String content = response.getBody();
		JSONObject json = JSON.parseObject(content);

		String resblockPosition = json.getJSONObject("data").getString("resblockPosition");
		if (resblockPosition.indexOf(",") > 0) {
			String[] position = resblockPosition.split(",");
			house.setLongitude(position[0]);
			house.setLatitude(position[1]);
		}

	}

	/**
	 * 解析房屋基本属性
	 * 
	 * @param elements
	 *            div.introContent
	 * @param house
	 */
	private void parseBaseInfo(Elements elements, House house) {
		Elements liElement = elements.select("div.base").select("div.content > ul").select("li");

		for (int i = 0; i < liElement.size(); i++) {
			Element temp = liElement.get(i);
			Elements span = temp.select("span.label");
			String label = span.text();
			String tempString = temp.html();
			tempString = tempString.substring(tempString.indexOf("span>") + 5, tempString.length());
			if ("房屋户型".equals(label)) {
				house.setHouseType(tempString);
			} else if ("建筑面积".equals(label)) {
				house.setBuildUpArea(new BigDecimal(getNumber(tempString)));
			} else if ("房屋朝向".equals(label)) {
				house.setOrientation(tempString);
			} else if ("产权年限".equals(label)) {
				house.setPropertyRight(tempString);
			}
		}
	}

	/**
	 * 解析房屋交易属性
	 * 
	 * @param elements
	 *            div.transaction
	 * @param house
	 */
	private void parseTranInfo(Elements elements, House house) {
		Elements liElement = elements.select("div.transaction").select("div.content > ul").select("li");

		for (int i = 0; i < liElement.size(); i++) {
			Element temp = liElement.get(i);
			Elements span = temp.select("span.label");
			String label = span.text();
			String tempString = temp.html();
			tempString = tempString.substring(tempString.indexOf("span>") + 5, tempString.length());
			if ("挂牌时间".equals(label)) {
				house.setListingTime(getDate(tempString));
			} else if ("交易权属".equals(label)) {
				house.setTransferType(tempString);
			} else if ("上次交易".equals(label)) {
				house.setLastTransactionTime(getDate(tempString));
			} else if ("房屋用途".equals(label)) {
				house.setPurpose(tempString);
			} else if ("房屋年限".equals(label)) {
				house.setLimitYear(tempString);
			}
		}
	}

	private String getNumber(String src) {
		if (StringUtils.isEmpty(src)) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < src.length(); i++) {
			char c = src.charAt(i);
			if (Character.isDigit(c) || '.' == c) {
				sb.append(c);
			} else if (i > 0) {
				break;
			}
		}
		return sb.toString();
	}

	private Date getDate(String src) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(src);
		} catch (Exception e) {
			LOG.error("error when getDate", e);
			return new Date();
		}
	}

	/**
	 * 解析贷款信息
	 * 
	 * @param house
	 */
	private void parseLoanInfo(House house) {
		String url = String.format(GET_HOUSE_CAL_INFO_URL, house.getHouseCode());

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
			String content = response.getBody();

			JSONObject json = JSON.parseObject(content);
			if (json.containsKey("errorCode") == false) {
				return;
			}

			Integer errorCode = json.getInteger("errorCode");
			if (Integer.valueOf(0).equals(errorCode) == false) {
				return;
			}

			JSONObject dataJson = json.getJSONObject("data");
			JSONObject paymentJson = dataJson.getJSONObject("payment");
			BigDecimal downPayment = paymentJson.getBigDecimal("cost_house");
			house.setDownPayment(downPayment);

			BigDecimal monthlyPayment = paymentJson.getJSONObject("loan_info").getJSONObject("elp").getBigDecimal("mp");
			house.setMonthlyPayment(monthlyPayment);
		} catch (Exception e) {
			LOG.error("error when parseLoanInfo", e);
		}
	}

	private void inertOrUpdateHouseInfo(House house) {
		House dbHouse = houseDao.selectHouseByHouseCode(house.getHouseCode());
		if (dbHouse == null) {
			// insert
			houseDao.addHouse(house);
		} else {
			// update
			houseDao.updateHouseByHouseCode(house);
		}
	}

	private class ParseHouseInfoTask implements Runnable {

		private final String detailUrl;

		public  ParseHouseInfoTask(String detailUrl) {
			this.detailUrl = detailUrl;
		}

		@Override
		public void run() {
			CrawerTask.LOG.info("begin to get house info from " + detailUrl);
			// 获取网页内容

			Document document = null;

			try {
				document = Jsoup.connect(detailUrl).get();

				// 职责链模式解析网页内容
				final House house = new House();

				house.setHouseCode(getHouseIdFromUrl(detailUrl));
				house.setViewUrl(detailUrl);

				Date now = new Date();
				house.setCreateTime(now);
				house.setUpdateTime(now);

				parseHouseInfo(document, house);

				inertOrUpdateHouseInfo(house);

			} catch (Exception e) {
				CrawerTask.LOG.info(
						"ParseHouseInfoTask,run error,thread:" + Thread.currentThread().getName() + ",url:" + detailUrl
								+ ",content:" + document,
						e);
			}
		}

	}

	public static void main(String[] args) {
		CrawerTask crawerTask = new CrawerTask();
		System.out.println(crawerTask.getHouseIdFromUrl("https://bj.lianjia.com/ershoufang/101101858879.html"));
	}
}
