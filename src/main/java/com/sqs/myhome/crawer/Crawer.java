package com.sqs.myhome.crawer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.sqs.myhome.dao.SeedDao;
import com.sqs.myhome.vo.Seed;

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

	@Autowired
	private CrawerTask crawerTask;

	@Autowired
	private SeedDao seedDao;

	public void crawerFormSeed(String url) {
		try {
			Document document = Jsoup.connect(url).get();
			Elements content = document.select("ul.sellListContent");// 获取房屋list
			Elements urlList = content.select("a.img");
			for (int i = 0; i < urlList.size(); i++) {
				LOG.info(urlList.get(i).attr("href"));
				crawerTask.addTask(urlList.get(i).attr("href"));
			}

		} catch (Exception e) {
			LOG.error("error when crawerFormSeed", e);
		}
	}

	@Scheduled(cron = "0 0 0/2 * * ?")
	public void doTask() {
		LOG.info("doTask at " + new Date());

		List<Seed> seedList = seedDao.getSeedList();
		LOG.info("get from seed dao ,seedList:" + seedList);

		// 随机选取一个,每小时抓取一个url,防止流量监控

		if (CollectionUtils.isEmpty(seedList)) {
			return;
		}

		Random random = new Random();
		int index = random.nextInt(seedList.size());

		Seed seed = seedList.get(index);

		crawerFormSeed(seed.getViewUrl());
	}

	public static void main(String[] args) throws IOException {

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
