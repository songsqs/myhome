package com.sqs.myhome.crawer;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CrawerTest {

	private static final Logger LOG = LoggerFactory.getLogger(CrawerTest.class);

	@Autowired
	private RestTemplate restTemplate;

	private static final String seedUrl = "https://bj.lianjia.com/ershoufang/101101858879.html";

	@Test
	public void getPrice() {
		String url = "https://bj.lianjia.com/ershoufang/101102102226.html";

		try {
			Document document = Jsoup.connect(url).get();
			Elements overview = document.select("div.overview");
			LOG.info("overview:" + overview);

			Elements price = overview.select("div.content").select("div.price");
			LOG.info("price:" + price);
			Elements totalPrice = price.select("span.total");
			LOG.info("totalPrice:" + totalPrice);
			String totalPriceText = totalPrice.text();

			LOG.info("totalPriceText:" + totalPriceText);
		} catch (Exception e) {
		}

	}

	@Test
	public void getCourtId() {
		String url = "https://bj.lianjia.com/ershoufang/101102077819.html";
		try {
			Document document = Jsoup.connect(url).get();
			Elements overview = document.select("div.overview");

			Elements courtElement = overview.select("div.content").select("div.aroundInfo").select("div.communityName")
					.select("a.info");

			LOG.info("courtElement:" + courtElement);

			String courtInfo = courtElement.attr("href");
			if (courtInfo.endsWith("/")) {
				courtInfo = courtInfo.substring(0, courtInfo.length() - 1);
			}
			if (courtInfo.startsWith("/xiaoqu/")) {
				courtInfo = courtInfo.substring(courtInfo.indexOf("/xiaoqu/") + 8);
			}

			LOG.info("courtInfo:" + courtInfo);
		} catch (Exception e) {
			LOG.error("error when getCourtId", e);
		}
	}

	@Test
	public void getCourtInfo() {
		String url = "https://bj.lianjia.com/ershoufang/housestat?hid=101101858879&rid=1111027379321";

		ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

		String content = response.getBody();
		JSONObject json = JSON.parseObject(content);

		String resblockPosition = json.getJSONObject("data").getString("resblockPosition");

		LOG.info("resblockPosition:" + resblockPosition);
	}

	@Test
	public void getBaseInfo() {
		try {
			Document document = Jsoup.connect(seedUrl).get();
			Elements introductionElement = document.select("div.m-content").select("div.box-l")
					.select("#introduction > div").select("div.introContent");

			LOG.info("introductionElement:" + introductionElement.toString());

			parseBaseInfo(introductionElement);
		} catch (IOException e) {
			LOG.error("error", e);
		}
	}

	private void parseBaseInfo(Elements elements) {
		Elements ulElement = elements.select("div.base").select("div.content > ul");
		Elements liElement = ulElement.select("li");

		for (int i = 0; i < liElement.size(); i++) {
			Element temp = liElement.get(i);
			Elements span = temp.select("span.label");
			String label = span.text();
			String tempString = temp.html();
			if ("房屋户型".equals(label)) {
				tempString = tempString.substring(tempString.indexOf("span>") + 5, tempString.length());
				LOG.info("房屋户型:" + tempString);
			} else if ("建筑面积".equals(label)) {
				tempString = tempString.substring(tempString.indexOf("span>") + 5, tempString.length());
				LOG.info("建筑面积:" + getNumber(tempString));
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
}
