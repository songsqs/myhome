package com.sqs.myhome.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sqs.myhome.crawer.Crawer;

@RestController
public class TestController {

	@Autowired
	private Crawer crawer;

	@RequestMapping(value = "/testCrewer")
	public String testCrawer(@RequestParam("seed") String seedUrl) {
		crawer.crawerFormSeed(seedUrl);

		return "ok";
	}
}
