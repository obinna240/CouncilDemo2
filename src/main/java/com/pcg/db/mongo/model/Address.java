package com.pcg.db.mongo.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class Address implements java.io.Serializable {

	private Long id;
	private String address1;
	private String address2;
	private String address3;
	private String address4;
	private String address5;
	private String town;
	private String county;
	private String country;
	private String postcode;
	private Long easting;
	private Long northing;
	private Double latitude;
	private Double longitude;
	private String localAuthorityCode;
	
	
	public Address() {
	}

	public Address(String address1, String address2,
			String address3, String address4, String address5, String town,
			String county, String country, String postcode, Long easting,
			Long northing, Double latitude, Double longitude,
			String localAuthorityCode) {
		this.address1 = address1;
		this.address2 = address2;
		this.address3 = address3;
		this.address4 = address4;
		this.address5 = address5;
		this.town = town;
		this.county = county;
		this.country = country;
		this.postcode = postcode;
		this.easting = easting;
		this.northing = northing;
		this.latitude = latitude;
		this.longitude = longitude;
		this.localAuthorityCode = localAuthorityCode;
//		this.orderheadersForDeliveryAddressId = orderheadersForDeliveryAddressId;
//		this.userdetailtoaddresses = userdetailtoaddresses;
//		this.orderheadersForInvoiceAddressId = orderheadersForInvoiceAddressId;
	
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long addressId) {
		this.id = addressId;
	}

	public String getAddress1() {
		return WordUtils.capitalizeFully(this.address1);
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return WordUtils.capitalizeFully(this.address2);
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return WordUtils.capitalizeFully(this.address3);
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getAddress4() {
		return WordUtils.capitalizeFully(this.address4);
	}

	public void setAddress4(String address4) {
		this.address4 = address4;
	}

	public String getAddress5() {
		return WordUtils.capitalizeFully(this.address5);
	}

	public void setAddress5(String address5) {
		this.address5 = address5;
	}

	public String getTown() {
		return WordUtils.capitalizeFully(this.town);
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getCounty() {
		return WordUtils.capitalizeFully(this.county);
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostcode() {
		if (postcode != null) {
			return this.postcode.toUpperCase();
		}
		return this.postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
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

	public Double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getLocalAuthorityCode() {
		return this.localAuthorityCode;
	}

	public void setLocalAuthorityCode(String localAuthorityCode) {
		this.localAuthorityCode = localAuthorityCode;
	}

	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public static Address fromJsonToAddress(String json) {
        return new JSONDeserializer<Address>().use(null, Address.class).deserialize(json);
    }

	public static String toJsonArray(Collection<Address> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static Collection<Address> fromJsonArrayToAddresses(String json) {
        return new JSONDeserializer<List<Address>>().use(null, ArrayList.class).use("values", Address.class).deserialize(json);
    }
	
	public String getFullAddress() {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotEmpty(this.address1)) {
			sb.append(this.address1);
		}
		if (StringUtils.isNotEmpty(this.address2)) {
			if (StringUtils.isNotEmpty(sb.toString())) {
				sb.append(", ");
			}
			sb.append(this.address2);
		}
		if (StringUtils.isNotEmpty(this.address3)) {
			if (StringUtils.isNotEmpty(sb.toString())) {
				sb.append(", ");
			}
			sb.append(this.address3);
		}
		if (StringUtils.isNotEmpty(this.address4)) {
			if (StringUtils.isNotEmpty(sb.toString())) {
				sb.append(", ");
			}
			sb.append(this.address4);
		}
		if (StringUtils.isNotEmpty(this.address5)) {
			if (StringUtils.isNotEmpty(sb.toString())) {
				sb.append(", ");
			}
			sb.append(this.address5);
		}
		if (StringUtils.isNotEmpty(this.town)) {
			if (StringUtils.isNotEmpty(sb.toString())) {
				sb.append(", ");
			}
			sb.append(this.town);
		}
		if (StringUtils.isNotEmpty(this.county)) {
			if (StringUtils.isNotEmpty(sb.toString())) {
				sb.append(", ");
			}
			sb.append(this.county);
		}
		if (StringUtils.isNotEmpty(this.postcode)) {
			if (StringUtils.isNotEmpty(sb.toString())) {
				sb.append(", ");
			}
			sb.append(this.postcode);
		}
		
		
		return sb.toString();
		
	}

}
