package com.sqs.myhome.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.sqs.myhome.crawer.Crawer;
import com.sqs.myhome.dao.SeedDao;
import com.sqs.myhome.vo.Seed;

@RestController
public class TestController {

	@Autowired
	private Crawer crawer;

	@Autowired
	private SeedDao seedDao;

	@RequestMapping(value = "/testCrewer")
	public String testCrawer(@RequestParam("seed") String seedUrl) {
		crawer.crawerFormSeed(seedUrl);

		return "ok";
	}

	@RequestMapping(value = "/testGetSeed")
	public String testGetSeed() {
		List<Seed> seedList = seedDao.getSeedList();
		
		return JSON.toJSONString(seedList);
	}
}
