package com.pcg.search.api.beans;

/**
 * 
 */
public class APIStatusResponse {
	
	public enum StatusCode {
		OK,
		ERROR
	}
	
	private StatusCode status;
	private String message;
	private String value;
	
	
	public APIStatusResponse(StatusCode status, String message, String value) {
		super();
		this.status = status;
		this.message = message;
		this.value = value;
	}
	
	public StatusCode getStatus() {
		return status;
	}
	public void setStatus(StatusCode status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
