package com.pcg.db.mongo.dao;

import java.util.Collection;
import java.util.List;

import com.pcg.db.mongo.model.SAAddress;
import com.pcg.db.mongo.model.SAAddress.Coverage;

import uk.me.jstott.jcoord.LatLng;

public interface SAAddressDAOCustom {
	List<SAAddress> findByPostcode(String postcode, Coverage coverage);
	List<String> findLocalGovtCodeByPostcode(String postcode, Coverage coverage);
	Collection<String> findFirstPartPostcodeByLocalGovtCode(String localGovtCode);
	Collection<String> findFirstPartPostcodeByCounty(String county);
	LatLng findLatLngByPostcode(String postcode, Coverage coverage);
	boolean isPostcodeInLA(String postcode, Coverage coverage);
	SAAddress findFirstAddressByLocalGovtCode(String localGovtCode);
	Long findMaxId();
}
