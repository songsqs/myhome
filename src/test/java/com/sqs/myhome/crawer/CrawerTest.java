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
		String source = "<html><body><div class=\"price\"><span class=\"total\">620</span><div class=\"text\"><div class=\"unitPrice\"><span class=\"unitPriceValue\">77792<i>元/平米</i></span></div><div class=\"tax\" id=\"tax-text\">首付及贷款情况请咨询经纪人</div></div></div></body></html>";
		// LOG.info("source:" + source);
		Document document = Jsoup.parse(source);
		LOG.info("document:" + document.toString());
		Elements price = document.select("div.price");
		LOG.info("price:" + price.toString());
		Elements totalPrice = price.select("span.total");
		LOG.info("totalPrice:" + totalPrice.toString());
		String totalPriceText = totalPrice.text();

		LOG.info("totalPrice:" + totalPriceText);

		Elements unitPrice = price.select("div.unitPrice").select("span.unitPriceValue");
		String unitPriceValue = unitPrice.html();

		LOG.info("unitPriceValue:" + unitPriceValue);

		LOG.info("unitPriceValue2:" + unitPriceValue.substring(0, unitPriceValue.indexOf("<")));

	}

	@Test
	public void getCourtInfo() {
		String url = "https://bj.lianjia.com/ershoufang/housestat?hid=101101858879&rid=1111027379321";

		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
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

			LOG.info("temp" + i + ":" + temp.toString());

			String tempString = temp.html();
			LOG.info("info" + i + ":" + tempString.substring(tempString.indexOf("span>") + 5, tempString.length()));
		}
	}
}
