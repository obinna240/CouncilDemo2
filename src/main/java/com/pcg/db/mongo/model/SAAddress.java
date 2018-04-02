package com.pcg.db.mongo.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.WordUtils;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class SAAddress implements java.io.Serializable {

	private Long id;
	private String address1;
	private String address2;
	private String address3;
	private String address4;
	private String address5;
	private String town;
	private String pc;
	private Long easting;
	private Long northing;
	private Double latitude;
	private Double longtitude;
	private String localGovtCode;
	private String pcshort;
	private String county;
	
	public enum Coverage {
		UK,
		LOCALAUTHORITIES
	}

	public SAAddress() {
	}

	public SAAddress(String pc) {
		this.pc = pc;
	}

	public SAAddress(String address1, String address2, String address3,
			String address4, String address5, String town, String pc,
			Long easting, Long northing, Double latitude, Double longtitude,
			String localGovtCode, String pcshort, String county) {
		this.address1 = address1;
		this.address2 = address2;
		this.address3 = address3;
		this.address4 = address4;
		this.address5 = address5;
		this.town = town;
		this.pc = pc;
		this.easting = easting;
		this.northing = northing;
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.localGovtCode = localGovtCode;
		this.pcshort = pcshort;
		this.county = county;
	}

//	public Integer getAddressmasterId() {
//		return this.addressmasterId;
//	}
//
//	public void setAddressmasterId(Integer addressmasterId) {
//		this.addressmasterId = addressmasterId;
//	}

	public String getAddress1() {
		return  WordUtils.capitalizeFully(this.address1);
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return  WordUtils.capitalizeFully(this.address2);
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return  WordUtils.capitalizeFully(this.address3);
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getAddress4() {
		return  WordUtils.capitalizeFully(this.address4);
	}

	public void setAddress4(String address4) {
		this.address4 = address4;
	}

	public String getAddress5() {
		return  WordUtils.capitalizeFully(this.address5);
	}

	public void setAddress5(String address5) {
		this.address5 = address5;
	}

	public String getTown() {
		return  WordUtils.capitalizeFully(this.town);
	}

	public void setTown(String town) {
		this.town = town;
	}
	
	public String getCounty() {
		return  WordUtils.capitalizeFully(this.county);
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getPc() {
		return this.pc;
	}

	public void setPc(String pc) {
		this.pc = pc;
	}
	
	public String getPcshort() {
		return this.pcshort;
	}

	public void setPcshort(String pcshort) {
		this.pcshort = pcshort;
	}

	public Long getEasting() {
		return this.easting;
	}

	public void setEasting(Long easting) {
		this.easting = easting;
	}

	public Long getNorthing() {
		return this.northing;
	}

	public void setNorthing(Long northing) {
		this.northing = northing;
	}

	public Double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongtitude() {
		return this.longtitude;
	}

	public void setLongtitude(Double longtitude) {
		this.longtitude = longtitude;
	}

	public String getLocalGovtCode() {
		return this.localGovtCode;
	}

	public void setLocalGovtCode(String localGovtCode) {
		this.localGovtCode = localGovtCode;
	}

	
	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public static SAAddress fromJsonToAttribute(String json) {
        return new JSONDeserializer<SAAddress>().use(null, SAAddress.class).deserialize(json);
    }

	public static String toJsonArray(Collection<SAAddress> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static Collection<SAAddress> fromJsonArrayToAttributes(String json) {
        return new JSONDeserializer<List<SAAddress>>().use(null, ArrayList.class).use("values", SAAddress.class).deserialize(json);
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
