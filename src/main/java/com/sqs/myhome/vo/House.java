package com.sqs.myhome.vo;

import java.math.BigDecimal;
import java.util.Date;

public class House {

	private Integer id;
	// 房屋唯一编码
	private String houseCode;
	// 房屋总价(单位万元)
	private BigDecimal price;
	// 建筑面积(单位平方米)
	private BigDecimal buildUpArea;
	// 房屋朝向
	private String orientation;
	// 小区名称
	private String courtName;
	// 经度
	private String longitude;
	// 纬度
	private String latitude;
	// 单价(单位元)
	private BigDecimal unitPrice;
	// 户型
	private String houseType;
	// 交易权属
	private String transferType;
	// 首付(单位万元)
	private BigDecimal downPayment;
	// 月供(单位元)
	private BigDecimal monthlyPayment;
	// 产权
	private String propertyRight;
	// 供暖方式
	private String heatingType;
	// 房屋年限(如:满五年)
	private String limitYear;
	// 建筑时间
	private Date buildTime;
	// 上次交易时间
	private Date lastTransactionTime;
	// 挂牌时间
	private Date listingTime;
	// 房屋用途
	private String purpose;
	// 房子信息地址
	private String viewUrl;

	private Date createTime;

	private Date updateTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getHouseCode() {
		return houseCode;
	}

	public void setHouseCode(String houseCode) {
		this.houseCode = houseCode;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getBuildUpArea() {
		return buildUpArea;
	}

	public void setBuildUpArea(BigDecimal buildUpArea) {
		this.buildUpArea = buildUpArea;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public String getCourtName() {
		return courtName;
	}

	public void setCourtName(String courtName) {
		this.courtName = courtName;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	public String getTransferType() {
		return transferType;
	}

	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}

	public BigDecimal getDownPayment() {
		return downPayment;
	}

	public void setDownPayment(BigDecimal downPayment) {
		this.downPayment = downPayment;
	}

	public BigDecimal getMonthlyPayment() {
		return monthlyPayment;
	}

	public void setMonthlyPayment(BigDecimal monthlyPayment) {
		this.monthlyPayment = monthlyPayment;
	}

	public String getPropertyRight() {
		return propertyRight;
	}

	public void setPropertyRight(String propertyRight) {
		this.propertyRight = propertyRight;
	}

	public String getHeatingType() {
		return heatingType;
	}

	public void setHeatingType(String heatingType) {
		this.heatingType = heatingType;
	}

	public String getLimitYear() {
		return limitYear;
	}

	public void setLimitYear(String limitYear) {
		this.limitYear = limitYear;
	}

	public Date getBuildTime() {
		return buildTime;
	}

	public void setBuildTime(Date buildTime) {
		this.buildTime = buildTime;
	}

	public Date getLastTransactionTime() {
		return lastTransactionTime;
	}

	public void setLastTransactionTime(Date lastTransactionTime) {
		this.lastTransactionTime = lastTransactionTime;
	}

	public Date getListingTime() {
		return listingTime;
	}

	public void setListingTime(Date listingTime) {
		this.listingTime = listingTime;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getViewUrl() {
		return viewUrl;
	}

	public void setViewUrl(String viewUrl) {
		this.viewUrl = viewUrl;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
