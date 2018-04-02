package com.pcg.db.mongo.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.pcg.db.mongo.dao.AuthorityDAO;
import com.pcg.db.mongo.dao.SAAddressDAOCustom;
import com.pcg.db.mongo.model.Authority;
import com.pcg.db.mongo.model.SAAddress;
import com.pcg.db.mongo.model.SAAddress.Coverage;

import uk.me.jstott.jcoord.LatLng;


public class SAAddressDAOImpl implements SAAddressDAOCustom {

	@Autowired	@Qualifier("mongoTemplateAddress") MongoOperations mongoOps;
	@Autowired	AuthorityDAO authorityDAO;
	
	
	private static Log m_log = LogFactory.getLog(SAAddressDAOImpl.class);

	public SAAddressDAOImpl() {
		
	}
	
	/**
	 * Find a postcode constrained by the given coverage area
	 * A empty list is returned if the postcode either can't be found or is outside the coverage area
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> findLocalGovtCodeByPostcode(String postcode, Coverage coverage) {
		
		DBCollection dbCollection = mongoOps.getCollection(mongoOps.getCollectionName(SAAddress.class));
		
		String pcShort = getPCShort(postcode);
		String query = getPostCodeCoverageQuery(pcShort, coverage);
		BasicDBObject dbQuery = (BasicDBObject) JSON.parse(query);
		
		return dbCollection.distinct("localGovtCode", dbQuery);
	}
	
	
	/**
	 * Find a postcode constrained by the given coverage area
	 * A empty list is returned if the postcode either can't be found or is outside the coverage area, or is < 5 chars
	 */
	@Override
	public List<SAAddress> findByPostcode(String postcode, Coverage coverage) {
		
		String pcShort = getPCShort(postcode);
		
		if (pcShort.length() >= 5){
			
			String query = "";
			
			try {
				query = getPostCodeCoverageQuery(pcShort, coverage);
				return mongoOps.find(new BasicQuery(query), SAAddress.class);
			} catch (Exception e) {
				m_log.error("Error executing Mongo query : " + query, e);
			}
		}

		return new ArrayList<SAAddress>();
	}

	/**
	 * Find a lat lng constrained by the given coverage area
	 * A empty list is returned if the postcode either can't be found or is outside the coverage area, or is < 3 chars
	 */
	@Override
	public LatLng findLatLngByPostcode(String postcode, Coverage coverage) {
		
		String pcShort = getPCShort(postcode);
		
		if (pcShort.length() >= 2){
			try {
				DBCollection dbCollection = mongoOps.getCollection("sAAddress");
				
			    Pattern regex = Pattern.compile("^"+pcShort); 
			   
			    DBObject query =  new BasicDBObject("pcshort", regex);
			    DBObject match = new BasicDBObject("$match", query);
			     
             
			    // build the $projection operation
			    DBObject fields = new BasicDBObject("latitude", 1);
			    fields.put("longtitude", 1);
			    fields.put("_id", 0);
			    DBObject project = new BasicDBObject("$project", fields );

			    
                DBObject groupFields = new BasicDBObject( "_id", "$pcshort");
                groupFields.put("latitudeAvg", new BasicDBObject( "$avg", "$latitude"));
                groupFields.put("longtitudeAvg", new BasicDBObject( "$avg", "$longtitude"));
                DBObject group = new BasicDBObject("$group", groupFields);
            
			    AggregationOutput aggOutput = dbCollection.aggregate(match, project, group);
			    
			    
			    Iterable<DBObject> results = aggOutput.results();
			    if (results != null && results.iterator().hasNext()) {
			    	DBObject result = results.iterator().next();
			    	Double lat = (Double)result.get("latitudeAvg");
			    	Double lng = (Double)result.get("longtitudeAvg");
			    	
			    	LatLng latLng = new LatLng(lat, lng);
			    	return latLng;
			    }
			
			} catch (Exception e) {
				m_log.error("Error executing Mongo query : ", e);
			}
		}

		return null;
	}

	private String getPostCodeCoverageQuery(String pcShort, Coverage coverage){

		//Defensive code - expect at least 2 characters (i.e. a full outward postcode part)
		//That should restrict the regex to 1-200000 addresses. Any fewer characters than that 
		//and we're talking millions
		
		if (pcShort.length() < 2){
			pcShort = "XXX"; // will return 0 results
		}
		
		
		String query = "{pcshort:{$regex:'^" + pcShort + "'}";

		if (coverage.equals(Coverage.UK)) {
			query += "}";
		} else if (coverage.equals(Coverage.LOCALAUTHORITIES)) {

			StringBuilder jsonCommand = new StringBuilder(query);
			jsonCommand.append(",");

			StringBuilder jsonAuths = new StringBuilder();

			Iterable<Authority> auths = authorityDAO.getEnabledAuthorities();
			int authCount = 1;

			for (Authority authority : auths) {
				
				if (authCount > 1) {
					jsonAuths.append(",");
				}

				jsonAuths.append("{localGovtCode:{$regex:'^");
				jsonAuths.append(authority.getSnacCode());
				jsonAuths.append("'}}");
				authCount++;
			}

			if (authCount == 1) {
				jsonCommand.append(jsonAuths);
				jsonCommand.append("}");
			} else {
				jsonCommand.append("$or:[");
				jsonCommand.append(jsonAuths);
				jsonCommand.append("]}");
			}
			query = jsonCommand.toString();
		}
		
		return query;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<String> findFirstPartPostcodeByLocalGovtCode(String localGovtCode) {
		
		DBCollection dbCollection = mongoOps.getCollection("sAAddress");
		BasicDBObject q = new BasicDBObject();
		
		//q.put("localGovtCode", localGovtCode);
		q.put("localGovtCode", java.util.regex.Pattern.compile("^"+localGovtCode+"", Pattern.CASE_INSENSITIVE));
		
		List<String> shortPostcodes = dbCollection.distinct("pcshort", q);
		
		// get first part of postcodes
		HashMap <String, String> firstPcs = new HashMap<String, String>();
		
		for (String shortPostcode : shortPostcodes) {
			int len = shortPostcode.length();
			// get first part
			String pcfirst = shortPostcode.substring(0, len-3);
			firstPcs.put(pcfirst, pcfirst);
		}
		
		return firstPcs.values();

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<String> findFirstPartPostcodeByCounty(String county) {
		
		DBCollection dbCollection = mongoOps.getCollection("sAAddress");
		BasicDBObject q = new BasicDBObject();
		q.put("county", county);

		List<String> shortPostcodes = dbCollection.distinct("pcshort", q);
		
		// get first part of postcodes
		HashMap <String, String> firstPcs = new HashMap<String, String>();
		
		for (String shortPostcode : shortPostcodes) {
			int len = shortPostcode.length();
			// get first part
			String pcfirst = shortPostcode.substring(0, len-3);
			firstPcs.put(pcfirst, pcfirst);
		}
		
		return firstPcs.values();

	}
	
	private String getPCShort(String pc) {
		if (StringUtils.isBlank(pc)) {
			return "";
		} else {
			return StringUtils.replace(pc, " ", "").toUpperCase();
		}
	}

	@Override
	public boolean isPostcodeInLA(String postcode, Coverage coverage) {
		DBCollection dbCollection = mongoOps.getCollection(mongoOps.getCollectionName(SAAddress.class));
		
		String pcShort = getPCShort(postcode);
		String query = getPostCodeCoverageQuery(pcShort, coverage);
		BasicDBObject dbQuery = (BasicDBObject) JSON.parse(query);
		
		if (dbCollection.findOne(dbQuery) != null) {
			return true;
		}
		
		return false;
	}

	@Override
	public SAAddress findFirstAddressByLocalGovtCode(String localGovtCode) {
		//DBCollection dbCollection = mongoOps.getCollection("sAAddress");
		BasicDBObject q = new BasicDBObject();
		
		//q.put("localGovtCode", localGovtCode);
		q.put("localGovtCode", java.util.regex.Pattern.compile("^"+localGovtCode+"", Pattern.CASE_INSENSITIVE));
		return mongoOps.findOne(new BasicQuery(q), SAAddress.class);
	}
	
	@Override
	public Long findMaxId() {
		Query query = new Query();
		query.with(new Sort(Sort.Direction.DESC, "_id"));		
		//query.sort().on("_id", Order.DESCENDING);
		query.limit(1);
		SAAddress address = mongoOps.findOne(query, SAAddress.class);

		if (address != null) {
			return address.getId();
		}
		return null;
	}

	
}
