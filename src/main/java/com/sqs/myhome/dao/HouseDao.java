package com.sqs.myhome.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.sqs.myhome.vo.House;

public interface HouseDao {

	@Select("select * from house where house_code = #{houseCode}")
	public House selectHouseByHouseCode(@Param("houseCode") String houseCode);

	@Insert("insert into house (house_code,price,build_up_area,orientation,court_name,longitude,latitude,unit_price,house_type,transfer_type,down_payment,monthly_payment,property_right,"
			+ "heating_type,limit_year,build_time,last_transaction_time,listing_time,purpose,view_url,create_time,update_time) values"
			+ "(#{houseCode},#{price},#{buildUpArea},#{orientation},#{courtName},#{longitude},#{latitude},#{unitPrice},#{houseType},#{transferType},#{downPayment},#{monthlyPayment},"
			+ "#{propertyRight},#{heatingType},#{limitYear},#{buildTime},#{lastTransactionTime},#{listingTime},#{purpose},#{viewUrl},#{createTime},#{updateTime})")
	public int addHouse(House house);

	@Update({
			"update house set price = #{price},build_up_area=#{buildUpArea},orientation=#{orientation},court_name=#{courtName},",
			"longitude=#{longitude},latitude=#{latitude},unit_price=#{unitPrice},house_type=#{houseType},",
			"transfer_type=#{transferType},down_payment=#{downPayment},monthly_payment=#{monthlyPayment},",
			"property_right=#{propertyRight},heating_type=#{heatingType},limit_year=#{limitYear},build_time=#{buildTime},",
			"last_transaction_time=#{lastTransactionTime},listing_time=#{listingTime},purpose=#{purpose},",
			"view_url=#{viewUrl},update_time=#{updateTime}", "where house_code = #{houseCode}" })
	public int updateHouseByHouseCode(House house);
}
