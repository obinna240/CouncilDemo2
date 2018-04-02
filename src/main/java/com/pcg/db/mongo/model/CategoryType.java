package com.pcg.db.mongo.model;

public class CategoryType implements java.io.Serializable {

	private String id;
	
	private Long categoryId;
	private String name;
	private boolean applicableToCMS = true;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Long getCategoryId() {
		return categoryId;
	}
	
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public boolean isApplicableToCMS() {
		return applicableToCMS;
	}

	public void setApplicableToCMS(boolean applicableToCMS) {
		this.applicableToCMS = applicableToCMS;
	}
	

}
