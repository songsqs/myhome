package com.sqs.myhome.crawer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 链家网页爬虫
 * @author songqingshan
 *
 */

@Service
public class Crawer {

	private Logger LOG = LoggerFactory.getLogger(Crawer.class);
	
	/**
	 * 从种子url获取数据
	 * 
	 * @param url
	 */
	public void crawerFormSeed(String url) {
		try {
			Document document = Jsoup.connect(url).get();
			Elements content = document.select("ul.sellListContent");// 获取房屋list
			Elements urlList = content.select("a.img");
			for (int i = 0; i < urlList.size(); i++) {

			}

		} catch (Exception e) {
			LOG.error("error when crawerFormSeed", e);
		}
	}

	public static void main(String[] args) throws IOException {
		// String url = "https://bj.lianjia.com/ershoufang/sf1/";
		// Document document = Jsoup.connect(url).get();
		// // System.out.println(document);
		//
		// String url2 = "https://bj.lianjia.com/ershoufang/101101917569.html";
		// document = Jsoup.connect(url2).get();
		//
		// String path = "/Users/songqingshan/works/detail.html";
		//
		// File file = new File(path);
		//
		// BufferedOutputStream bs = new BufferedOutputStream(new
		// FileOutputStream(file));
		//
		// bs.write(document.toString().getBytes("UTF-8"));
		//
		// bs.close();

		File file = new File("/Users/songqingshan/works/home.html");

		StringBuilder sb = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Document document = Jsoup.parse(sb.toString());
		Elements content = document.select("ul.sellListContent");// 获取房屋list



		// System.out.println(content);

		Elements urlList = content.select("a.img");
		// System.out.println(urlList);
		for (int i = 0; i < urlList.size(); i++) {
			if (i <= 10) {
				System.out.println(urlList.get(i).attr("href"));
			}
		}
	}
}
