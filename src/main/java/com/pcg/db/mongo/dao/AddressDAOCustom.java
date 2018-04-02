package com.pcg.db.mongo.dao;

import com.pcg.db.mongo.model.Address;

public interface AddressDAOCustom extends CustomDAO<Address, Long>{
	
	public Address findAddressByPostCode(String address1,String town,String postCode);
}
