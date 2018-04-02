package com.pcg.search.api.beans;

import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * 
 * @author oonyimadu
 *
 */
public class CareHomeBean 
{
	private String id;
	private String name; 
	private String website;
	private String publicEmail;
	

	private String address;
	
	private String town;
	
	private String full_postcode;
	private String postcode1;
	private String postcode2;
	
	private String phone;
	private String location;
					
	private List<String> careProvided;
	private List<String> admissions;
	private String homeType;
			
	private Date dateOfIndex;
	
	/**
	 * 
	 * 
	 * 
	private String typeOfHome;
	private Integer numberOfBeds;
	private String hca;
	private String rnha;
	private String  nca;
	private String  ce;
	private String  ncf;
	private String docType;
	private String localAuthority;
	private String linkToCQCRating;
	private String lastReviewed;
	private String lastReviewedAsString;
	private Date lastReviewedAsDate;
	*/
	
	@XmlAttribute
	public String getId() {
		return id;
	}

	 
	public void setId(String id) {
		this.id = id;
	}

	
	public String getName() {
		return name;
	}
	
	public String getFull_postcode() {
		return full_postcode;
	}


	public void setFull_postcode(String full_postcode) {
		this.full_postcode = full_postcode;
	}


	public String getPostcode1() {
		return postcode1;
	}


	public void setPostcode1(String postcode1) {
		this.postcode1 = postcode1;
	}


	public String getPostcode2() {
		return postcode2;
	}


	public void setPostcode2(String postcode2) {
		this.postcode2 = postcode2;
	}

	 
	public void setName(String name) {
		this.name = name;
	}

	public String getWebsite() {
		return website;
	}
	
	 
	public void setWebsite(String website) {
		this.website = website;
	}

	

	public String getAddress() {
		return address;
	}
	
	 
	public void setAddress(String address) {
		this.address = address;
	}

	

	public String getTown() {
		return town;
	}
	
	 
	public void setTown(String town) {
		this.town = town;
	}

	

	public String getPhone() {
		return phone;
	}
	
	 
	public void setPhone(String phone) {
		this.phone = phone;
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

	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public Date getDateOfIndex() {
		return dateOfIndex;
	}


	public void setDateOfIndex(Date dateOfIndex) {
		this.dateOfIndex = dateOfIndex;
	}


	

	public String getPublicEmail() {
		return publicEmail;
	}


	public void setPublicEmail(String publicEmail) {
		this.publicEmail = publicEmail;
	}


	public String getHomeType() {
		return homeType;
	}


	public void setHomeType(String homeType) {
		this.homeType = homeType;
	}

	
}
