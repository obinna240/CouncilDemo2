package com.pcg.db.mongo.model;

import java.io.Serializable;
import java.util.Date;

public class CmsBaseUserObject implements Serializable {
	
	private Long id;	
	private Date dateCreated;
	private Date dateLastUpdated;
	
	//Fields set when any error occurred during submit
	private Date dateSubmitted;
	
	private String errorMessage;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Date getDateCreated() {
		return dateCreated;
	}
	
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public Date getDateLastUpdated() {
		return dateLastUpdated;
	}
	
	public void setDateLastUpdated(Date dateLastUpdated) {
		this.dateLastUpdated = dateLastUpdated;
	}
	
	
	public Date getDateSubmitted() {
		return dateSubmitted;
	}

	public void setDateSubmitted(Date dateSubmitted) {
		this.dateSubmitted = dateSubmitted;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	
}
