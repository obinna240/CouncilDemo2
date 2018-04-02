package com.pcg.db.mongo.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.pcg.db.mongo.dao.AddressDAOCustom;
import com.pcg.db.mongo.model.Address;

public class AddressDAOImpl extends CustomDAOImpl<Address, Long> implements AddressDAOCustom {

	private static Log m_log = LogFactory.getLog(AddressDAOImpl.class);

	public AddressDAOImpl() {
		super(Address.class);
	}

	@Override
	public Address findAddressByPostCode(String address1, String town,
			String postCode) {
		Query query = new Query();

		try {
			if(address1 != null){
				query.addCriteria(Criteria.where("address1").is(address1));
			}
			if(town != null){
				query.addCriteria(Criteria.where("town").is(town));
			}
			 
			query.addCriteria(Criteria.where("postcode").is(postCode));
			

			return mongoOps.findOne(query, Address.class);

		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
		}
		return null;
	}

}
