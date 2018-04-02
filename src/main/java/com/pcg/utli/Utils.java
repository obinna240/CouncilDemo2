package com.pcg.utli;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrDocument;

import com.pcg.search.api.beans.CareHomeBean;

/**
 * 
 * @author oonyimadu
 *
 */
public class Utils {
	
	public static CareHomeBean doSearchResultBean(SolrDocument solrDoc)
	{
		CareHomeBean bean = new CareHomeBean();
		if(solrDoc != null)
		{
			String id = (String) solrDoc.getFieldValue("id");
			bean.setId(id);
			
			
			String website = (String) solrDoc.getFieldValue("website");
			if(StringUtils.isNotBlank(website))
			{
				bean.setWebsite(website);
			}
			
			String name = (String) solrDoc.getFieldValue("name");
			if(StringUtils.isNotBlank(name))
			{
				bean.setName(name);
			}
			
			String homeType = (String) solrDoc.getFieldValue("homeType");
			if(StringUtils.isNotBlank(homeType))
			{
				bean.setHomeType(homeType);
			}
			
			
			
			String publicEmail = (String) solrDoc.getFieldValue("publicEmail");
			if(StringUtils.isNotBlank(publicEmail))
			{
				bean.setPublicEmail(publicEmail);
			}
			
			String address = (String) solrDoc.getFieldValue("address");
			if(StringUtils.isNotBlank(address))
			{
				bean.setAddress(address);
			}
				
			
			String town = (String) solrDoc.getFieldValue("town");
			if(StringUtils.isNotBlank(town))
			{
				bean.setTown(town);
			}
			
			String full_postcode = (String) solrDoc.getFieldValue("full_postcode");
			if(StringUtils.isNotBlank(full_postcode))
			{
				bean.setFull_postcode(full_postcode);
			}
			
			String postcode1 = (String) solrDoc.getFieldValue("postcode1");
			if(StringUtils.isNotBlank(postcode1))
			{
				bean.setPostcode1(postcode1);
			}
			
			String postcode2 = (String) solrDoc.getFieldValue("postcode2");
			if(StringUtils.isNotBlank(postcode2))
			{
				bean.setPostcode2(postcode2);
			}
			
			String phone = (String) solrDoc.getFieldValue("phone");
			if(StringUtils.isNotBlank(phone))
			{
				bean.setPhone(phone);
			}
			
			String location = (String) solrDoc.getFieldValue("location");
			if(StringUtils.isNotBlank(location))
			{
				bean.setLocation(location);
			}
			
			
			
			Object careProvidedObject = solrDoc.getFieldValue("careProvided");
			List<String> clist = checkObjAsList(careProvidedObject);
			if(CollectionUtils.isNotEmpty(clist))
			{
				bean.setCareProvided(clist);
			}
			
			Object admissionsObject = solrDoc.getFieldValue("admissions");
			List<String> adList = checkObjAsList(admissionsObject);
			if(CollectionUtils.isNotEmpty(adList))
			{
				bean.setAdmissions(adList);
			}
			
			
			
			Date dateOfIndex = (Date) solrDoc.getFieldValue("dateOfIndex");
			if(dateOfIndex!=null)
			{
				bean.setDateOfIndex(dateOfIndex);
			}
			
		
				
		}
		return bean;
	}
	
	/**
	 * 
	 * @param obj
	 * @return List<String>
	 */
	public static List<String> checkObjAsList(Object obj)
	{
		List<String> val = null;
		if(obj!=null)
		{
			if(obj instanceof List)
			{
				val = (List<String>)obj;
			}
			else if(obj instanceof String)
			{
				val = new ArrayList<String>();
				val.add((String)obj);
			}
		}
		return val;
	}

}
