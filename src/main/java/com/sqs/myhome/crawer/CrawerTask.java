package com.sqs.myhome.crawer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

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

	private class ParseHouseInfoTask implements Runnable {

		private final String detailUrl;

		public  ParseHouseInfoTask(String detailUrl) {
			this.detailUrl = detailUrl;
		}

		@Override
		public void run() {
			CrawerTask.LOG.info("begin to get house info from " + detailUrl);
		}

	}

	public static void main(String[] args) {
		CrawerTask crawerTask = new CrawerTask();
		System.out.println(crawerTask.getHouseIdFromUrl("https://bj.lianjia.com/ershoufang/101101858879.html"));
	}
}
