package com.sqs.myhome.crawer;

import java.math.BigDecimal;
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
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sqs.myhome.vo.House;

/**
 * 
 * @author songqingshan
 *         https://bj.lianjia.com/ershoufang/housestat?hid=101101844835&rid={小区id}
 *         得到小区信息，如经纬度等<br>
 *         https://bj.lianjia.com/tools/calccost?house_code=101101844835 房贷计算
 */
public class CrawerTask {
	private static final Logger LOG = LoggerFactory.getLogger(CrawerTask.class);

	private static final CrawerTask INSTANCE = new CrawerTask();

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

	private CrawerTask() {

	}

	public static CrawerTask instance() {
		return INSTANCE;
	}

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

		Elements price = overview.select("div.prive");
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

	}

	/**
	 * 解析小区信息,从小区信息获取经纬度
	 * 
	 * @param elements
	 *            div.overview
	 * @param house
	 */
	private void parseCourt(Elements elements, House house) {
		Elements courtElement = elements.select("div.content").select("div.aroundInfo").select("communityName")
				.select("a.info");

		// <a href="/xiaoqu/1111027379321/" target="_blank" class="info ">世纪村东区</a>
		house.setCourtName(courtElement.text());

		String courtInfo = courtElement.attr("href");
		if (courtInfo.endsWith("/")) {
			courtInfo = courtInfo.substring(0, courtInfo.length() - 1);
		}
		if (courtInfo.startsWith("/xiaoqu/")) {
			courtInfo = courtInfo.substring(courtInfo.indexOf("/xiaoqu"));
		}

		// courtInfo已经解析为小区id,通过rest接口获取小区信息
		String url = String.format(GET_HOUSE_XIAOQU_INFO_URL, house.getHouseCode(), courtInfo);

		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
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
			try {
				Document document = Jsoup.connect(detailUrl).get();

				// 职责链模式解析网页内容
				final House house = new House();

				house.setHouseCode(getHouseIdFromUrl(detailUrl));

				parseHouseInfo(document, house);

			} catch (Exception e) {
				CrawerTask.LOG.info("ParseHouseInfoTask,run error,thread:" + Thread.currentThread().getName(), e);
			}
		}

	}

	public static void main(String[] args) {
		CrawerTask crawerTask = new CrawerTask();
		System.out.println(crawerTask.getHouseIdFromUrl("https://bj.lianjia.com/ershoufang/101101858879.html"));
	}
}
