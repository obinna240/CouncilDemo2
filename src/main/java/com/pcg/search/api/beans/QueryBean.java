package com.pcg.search.api.beans;

/**
 * QueryBean used in processing results
 * @author oonyimadu
 *
 */
public class QueryBean 
{
	private String queryType;
	private String nameQuery;
	private String admissions;
	private String typeOfCareHome;
	private String careProvided;
	private String town;
	private String postcode;
	private Integer radius;
	
	public String getCareProvided() {
		return careProvided;
	}
	public void setCareProvided(String careProvided) {
		this.careProvided = careProvided;
	}
	public Integer getRadius() {
		return radius;
	}
	public void setRadius(Integer radius) {
		this.radius = radius;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public String getTown() {
		return town;
	}
	public void setTown(String town) {
		this.town = town;
	}
	public String getTypeOfCareHome() {
		return typeOfCareHome;
	}
	public void setTypeOfCareHome(String typeOfCareHome) {
		this.typeOfCareHome = typeOfCareHome;
	}
	public String getAdmissions() {
		return admissions;
	}
	public void setAdmissions(String admissions) {
		this.admissions = admissions;
	}
	public String getNameQuery() {
		return nameQuery;
	}
	public void setNameQuery(String nameQuery) {
		this.nameQuery = nameQuery;
	}
	public String getQueryType() {
		return queryType;
	}
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	
	
}
