package com.sqs.myhome.exception;

public class BusinessException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer errorCode;
	private String desc;

	public BusinessException(Integer errorCode, String desc) {
		this.errorCode = errorCode;
		this.desc = desc;
	}

	public BusinessException(String desc) {
		this(-1, desc);
	}

	public BusinessException() {
		this("系统忙");
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
