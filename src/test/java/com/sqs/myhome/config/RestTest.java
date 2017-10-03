package com.sqs.myhome.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RestTest {

	@Autowired
	private RestTemplate restTemplate;

	private static final Logger LOG = LoggerFactory.getLogger(RestTest.class);

	@Test
	public void testCall() {
		ResponseEntity<String> response = restTemplate.getForEntity("http://www.baidu.com", String.class);
		LOG.info("testCall:" + response.getBody());
	}
}
