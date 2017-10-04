package com.sqs.myhome.dao;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.sqs.myhome.vo.Seed;

public interface SeedDao {

	@Select("select id,view_url as viewUrl,enable ,create_time as createTime,update_time as updateTime from seed where enable = 1")
	public List<Seed> getSeedList();
}
