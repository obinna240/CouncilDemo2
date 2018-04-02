package com.pcg.db.mongo.model;

import java.util.Date;

public class SearchLogger  implements java.io.Serializable {

	private Long id;
	private String searchText;
	private String searchType;
	private Long resultCount;
	private Long web;
	private Long user;
	private Long carer;
	private Long broker;
	private Date logDate;

	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getResultCount() {
		return resultCount;
	}


	public void setResultCount(Long resultCount) {
		this.resultCount = resultCount;
	}


	public Long getWeb() {
		return web;
	}


	public void setWeb(Long web) {
		this.web = web;
	}


	public Long getUser() {
		return user;
	}


	public void setUser(Long user) {
		this.user = user;
	}


	public Long getCarer() {
		return carer;
	}


	public void setCarer(Long carer) {
		this.carer = carer;
	}


	public Long getBroker() {
		return broker;
	}


	public void setBroker(Long broker) {
		this.broker = broker;
	}

    public String getSearchText() {
        return this.searchText;
    }
    
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
    public String getSearchType() {
        return this.searchType;
    }
    
    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }
    public Date getLogDate() {
        return this.logDate;
    }
    
    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }




}


