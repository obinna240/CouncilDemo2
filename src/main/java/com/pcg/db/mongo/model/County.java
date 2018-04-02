package com.pcg.db.mongo.model;

public class County implements java.io.Serializable {

	private Long id;
	private String county;

	public County() {
	}
	
	public County(String county) {
		setCounty(county);
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCounty() {
		return this.county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

}
