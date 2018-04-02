package com.pcg.search.api.beans;

import java.util.List;

/**
 * 
 * @author oonyimadu
 *
 */
public class CareHomeObject 
{	
	private String id;
	private String name; 
	private String url;
	
	private String description;
	private String address1;
	private String address2;
	private String address3;
	private String town;
	
	private String postcode;
	private String phone;
	
	private String vacancies;
	private String easting;
	private String northing;
	private String easting_northing;
		
	private List<String> careProvided;
	private List<String> admissions;
	private List<String> homeType;
	
	private String docType;
	private String localAuthority;
	private String linkToCQCRating;
	private String lastReviewed;
	
	private String lastReviewedAsString;
	private Integer numberOfBeds;
	
	private Boolean hca;
	private Boolean rnha;
	private Boolean nca;
	private Boolean ce;
	private Boolean ncf;
	
	private String typeOfHome;
	
	
	public String getId() {
		return id;
	}

	 
	public void setId(String id) {
		this.id = id;
	}

	
	public String getName() {
		return name;
	}
	
	 
	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}
	
	 
	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	 
	public void setDescription(String description) {
		this.description = description;
	}

	public String getAddress1() {
		return address1;
	}
	
	 
	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}
	
	 
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}
	
	 
	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getTown() {
		return town;
	}
	
	 
	public void setTown(String town) {
		this.town = town;
	}

	public String getPostcode() {
		return postcode;
	}
	
	 
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getPhone() {
		return phone;
	}
	
	 
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getVacancies() {
		return vacancies;
	}
	
	
	public void setVacancies(String vacancies) {
		this.vacancies = vacancies;
	}

	public String getEasting() {
		return easting;
	}
	
	
	public void setEasting(String easting) {
		this.easting = easting;
	}

	public String getNorthing() {
		return northing;
	}

	 
	public void setNorthing(String northing) {
		this.northing = northing;
	}

	public String getEasting_northing() {
		return easting_northing;
	}

	 
	public void setEasting_northing(String easting_northing) {
		this.easting_northing = easting_northing;
	}

	public List<String> getCareProvided() {
		return careProvided;
	}

	 
	public void setCareProvided(List<String> careProvided) {
		this.careProvided = careProvided;
	}

	public List<String> getAdmissions() {
		return admissions;
	}

	 
	public void setAdmissions(List<String> admissions) {
		this.admissions = admissions;
	}

	public List<String> getHomeType() {
		return homeType;
	}

	 
	public void setHomeType(List<String> homeType) {
		this.homeType = homeType;
	}

	public String getDocType() {
		return docType;
	}

	 
	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getLocalAuthority() {
		return localAuthority;
	}

	 
	public void setLocalAuthority(String localAuthority) {
		this.localAuthority = localAuthority;
	}

	public String getLinkToCQCRating() {
		return linkToCQCRating;
	}

	 
	public void setLinkToCQCRating(String linkToCQCRating) {
		this.linkToCQCRating = linkToCQCRating;
	}

	public String getLastReviewed() {
		return lastReviewed;
	}
	
	
	public void setLastReviewed(String lastReviewed) {
		this.lastReviewed = lastReviewed;
	}

	public String getLastReviewedAsString() {
		return lastReviewedAsString;
	}
	
	 
	public void setLastReviewedAsString(String lastReviewedAsString) {
		this.lastReviewedAsString = lastReviewedAsString;
	}

	public Integer getNumberOfBeds() {
		return numberOfBeds;
	}
	
	 
	public void setNumberOfBeds(Integer numberOfBeds) {
		this.numberOfBeds = numberOfBeds;
	}

	public Boolean getHca() {
		return hca;
	}
	
	 
	public void setHca(Boolean hca) {
		this.hca = hca;
	}

	public Boolean getRnha() {
		return rnha;
	}
	
	 
	public void setRnha(Boolean rnha) {
		this.rnha = rnha;
	}

	public Boolean getNca() {
		return nca;
	}
	
	 
	public void setNca(Boolean nca) {
		this.nca = nca;
	}

	public Boolean getCe() {
		return ce;
	}
	
	 
	public void setCe(Boolean ce) {
		this.ce = ce;
	}

	public Boolean getNcf() {
		return ncf;
	}
	
	 
	public void setNcf(Boolean ncf) {
		this.ncf = ncf;
	}

	public String getTypeOfHome() {
		return typeOfHome;
	}
	
	 
	public void setTypeOfHome(String typeOfHome) {
		this.typeOfHome = typeOfHome;
	}

	
}
