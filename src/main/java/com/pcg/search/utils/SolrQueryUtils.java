package com.pcg.search.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import uk.me.jstott.jcoord.LatLng;

import com.pcg.db.mongo.dao.SAAddressDAO;
import com.pcg.db.mongo.model.SAAddress;
import com.pcg.db.mongo.model.SAAddress.Coverage;
import com.pcg.search.api.beans.CareHomeBean;
import com.pcg.search.api.beans.CareHomeBeans;
import com.pcg.search.api.beans.PaginatorBean;

import com.pcg.search.api.beans.ResultBean;
import com.pcg.utli.Cleaner;
import com.pcg.utli.Utils;

/**
 * 
 * @author oonyimadu
 *
 */
public class SolrQueryUtils 
{
	private static Log log = LogFactory.getLog(SolrQueryUtils .class);	
	@Autowired
	private QueryParameters queryParameters;
	@Autowired 
	private SAAddressDAO addressMasterUKDAO;
	
	public QueryParameters  getQueryParameters () {
		return queryParameters;
	}

	public void setQueryParameters (QueryParameters  queryParameters) {
		this.queryParameters = queryParameters;
	}
	
	/**
	 */
	public ResultBean doGenericSearch(String careProvided, String admissions, String typeOfCareHome, Map<String,Object> qParamStartRow)
	{
		ResultBean resultBean = null;
		CareHomeBeans careHomeBeans = null;
		QueryResponse response = null;
		
		String url = queryParameters.getSolrQueryUrl();
		
		PaginatorBean originalPaginatorBean = null;
		
		HashMap<String, List<String>> mapStr = queryParameters.getQuery();
		List<String> listOfFields = mapStr.get("generalSearch");
		
		String queryLetter = listOfFields.get(0);
		String queryString = listOfFields.get(1);
		String fl = listOfFields.get(2);
		String careProvFields = listOfFields.get(3);
		String admissionsFields = listOfFields.get(4);
		String homeTypeFields = listOfFields.get(5);
		
		SolrClient  server = new HttpSolrClient(url);
		
		
		SolrQuery query = new SolrQuery();
		
		query.setQuery(queryString);
		
		if(StringUtils.isNotBlank(careProvided)||StringUtils.isNotBlank(admissions)||StringUtils.isNotBlank(typeOfCareHome))
		{
			List<String> filters = new ArrayList<String>();
			if(StringUtils.isNotBlank(careProvided))
			{
				String[] careProvided_Filters = Cleaner.splitUnderscore(careProvided);
				List<String> list = Cleaner.mergeListForFiler(careProvFields, careProvided_Filters);
				filters.addAll(list);
			}
			
			if(StringUtils.isNotBlank(admissions))
			{
				String[] admission_Filters = Cleaner.splitUnderscore(admissions);
				List<String> list = Cleaner.mergeListForFiler(admissionsFields, admission_Filters);
				filters.addAll(list);
			}
			
			if(StringUtils.isNotBlank(typeOfCareHome))
			{
				String[] typeOfCareHome_Filters = Cleaner.splitUnderscore(typeOfCareHome);
				List<String> list = Cleaner.mergeListForFiler(homeTypeFields, typeOfCareHome_Filters);
				filters.addAll(list);
			}
			String filteredStrings = Cleaner.createFilterParam(filters);
			query.setParam("fq", filteredStrings);
		}
		
		query.setParam("fl", fl);
		
		
		if(MapUtils.isNotEmpty(qParamStartRow))
		{
			Integer qStart = (Integer)qParamStartRow.get("queryStart");
			Integer qRows = (Integer)qParamStartRow.get("queryRows");
			originalPaginatorBean = (PaginatorBean)qParamStartRow.get("originalPaginatorBean");
			
			query.setStart(qStart);
			query.setRows(qRows);
		}
		
		try
		{
			response = server.query(query);
			Long resultSize = response.getResults().getNumFound();
			if(resultSize>0)
			{
				careHomeBeans = new CareHomeBeans();
				List<CareHomeBean> careHomeBeanList = new ArrayList<CareHomeBean>();
				Integer numberOfResults = (int)(long)resultSize;
				resultBean = new ResultBean();
				SolrDocumentList solrList = response.getResults();
				if(CollectionUtils.isNotEmpty(solrList))
				{
					for(SolrDocument solrDoc:solrList)
					{
						CareHomeBean chb = Utils.doSearchResultBean(solrDoc);
						careHomeBeanList.add(chb);
					}
					
					careHomeBeans.setCareHomeBean(careHomeBeanList);
					
					PaginatorBean resultPBean = null;
					
					if(originalPaginatorBean!=null)
					{
						resultPBean = PaginationUtils.doPaginationTest(numberOfResults, originalPaginatorBean);
						careHomeBeans.setPaginatorBean(resultPBean);
					}
					
					resultBean.setCareHomeBeans(careHomeBeans);
					resultBean.setTotalNumberOfResults(numberOfResults);
					resultBean.setStart(query.getStart());
					
					
				}
			}
			server.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			log.error("Solr Exception " + ex.getMessage());
		}
		
		return resultBean;
		
	}
	
	public ResultBean doNameSearch(String name, String town, Map<String,Object> qParamStartRow)
	{
		ResultBean resultBean = null;
		CareHomeBeans careHomeBeans = null;
		QueryResponse response = null;
		
		String url = queryParameters.getSolrQueryUrl();
		
		PaginatorBean originalPaginatorBean = null;
		
		HashMap<String, List<String>> mapStr = queryParameters.getQuery();
		
		List<String> listOfFields = mapStr.get("nameSearch");
		
		String queryLetter = listOfFields.get(0);
		String queryStringStart = listOfFields.get(1);
		String queryStringEnd = listOfFields.get(2);
		String fl = listOfFields.get(3);
		String filterField = listOfFields.get(4);
		
		SolrClient  server = new HttpSolrClient(url);
		SolrQuery query = new SolrQuery();
		
		if(StringUtils.isNotBlank(name))
		{
			name = StringUtils.normalizeSpace(name);
			String qString = queryStringStart+name+queryStringEnd;
			query.setQuery(qString);
			query.setParam("fl", fl);
			
			if(StringUtils.isNotBlank(town))
			{
				town = StringUtils.normalizeSpace(town);
				query.setParam("fq", town);
			}
			
			if(MapUtils.isNotEmpty(qParamStartRow))
			{
				Integer qStart = (Integer)qParamStartRow.get("queryStart");
				Integer qRows = (Integer)qParamStartRow.get("queryRows");
				originalPaginatorBean = (PaginatorBean)qParamStartRow.get("originalPaginatorBean");
				
				query.setStart(qStart);
				query.setRows(qRows);
			}
		}
		
		try
		{
			response = server.query(query);
			Long resultSize = response.getResults().getNumFound();
			if(resultSize>0)
			{
				careHomeBeans = new CareHomeBeans();
				List<CareHomeBean> careHomeBeanList = new ArrayList<CareHomeBean>();
				Integer numberOfResults = (int)(long)resultSize;
				resultBean = new ResultBean();
				SolrDocumentList solrList = response.getResults();
				if(CollectionUtils.isNotEmpty(solrList))
				{
					for(SolrDocument solrDoc:solrList)
					{
						CareHomeBean chb = Utils.doSearchResultBean(solrDoc);
						careHomeBeanList.add(chb);
					}
					
					careHomeBeans.setCareHomeBean(careHomeBeanList);
					
					PaginatorBean resultPBean = null;
					
					if(originalPaginatorBean!=null)
					{
						resultPBean = PaginationUtils.doPaginationTest(numberOfResults, originalPaginatorBean);
						careHomeBeans.setPaginatorBean(resultPBean);
					}
					
					resultBean.setCareHomeBeans(careHomeBeans);
					resultBean.setTotalNumberOfResults(numberOfResults);
					resultBean.setStart(query.getStart());
					
					
				}
			}
			server.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		
			log.error("Solr Exception " + ex.getMessage());
		}
		
		
		return resultBean;
		
		
		
	}
	
	public ResultBean doLocationSearch(String locationType, String locationOrPostcode, String radius, 
			String careProvided, String admissions, String careHomeType, Map<String,Object> qParamStartRow)
	{
		String coordinates = "";
		ResultBean resultBean = null;
		CareHomeBeans careHomeBeans = null;
		QueryResponse response = null;
		
		String url = queryParameters.getSolrQueryUrl();
		
		PaginatorBean originalPaginatorBean = null;
		
		SolrClient  server = new HttpSolrClient(url);
		SolrQuery query = new SolrQuery();
		
		HashMap<String, List<String>> mapStr = queryParameters.getQuery();
		
		if(locationType.equalsIgnoreCase("postcode"))
		{
			String latitude = null;
			String longitude = null;
			List<SAAddress> listAddresses = addressMasterUKDAO.findByPostcode(locationOrPostcode, Coverage.UK);
			if (listAddresses != null && listAddresses.size() > 0 ) 
			{
				// just use the first?
				SAAddress addressmaster = listAddresses.get(0);
				latitude = Double.toString(addressmaster.getLatitude());
				longitude =  Double.toString(addressmaster.getLongtitude());
			} else {
				LatLng latLng = addressMasterUKDAO.findLatLngByPostcode(locationOrPostcode, Coverage.UK);
				if (latLng != null) {
    	        	Double lat = latLng.getLatitude();
    	          	Double lng = latLng.getLongitude();
    	       
    				latitude = Double.toString(lat);
					longitude =  Double.toString(lng);
					latitude = StringUtils.normalizeSpace(latitude);
					longitude = StringUtils.normalizeSpace(longitude);
    	        }
			}
			if(StringUtils.isNotBlank(latitude)&&StringUtils.isNotBlank(longitude))
			{
				coordinates = latitude+","+longitude;
			}
		}
		else if(locationType.equalsIgnoreCase("town"))
		{
			coordinates = queryParameters.getCoordinates().get(locationOrPostcode);
		}			
		
		if(StringUtils.isNotBlank(coordinates))
		{
				List<String> listOfFields = mapStr.get("townPcSearch");
				
				String queryLetter = listOfFields.get(0); //q
				String aQuery = listOfFields.get(1); //*:*
				String fq = listOfFields.get(2); //fq
				String geofilt = listOfFields.get(3); //{!geofilt}
				String sfield = listOfFields.get(4); //sfield
				String sfieldValue = listOfFields.get(5); //location
				String score = listOfFields.get(6); //score
				String pt = listOfFields.get(7); //pt
				String d = listOfFields.get(8); //d
				String sort = listOfFields.get(9); //sort
				String geodistSort = listOfFields.get(10); //geodist() asc
				String flist = listOfFields.get(11); //field list
				String miles = listOfFields.get(12); //miles
				String careProvFields = listOfFields.get(13); //miles
				String admissionsFields = listOfFields.get(14); //miles
				String homeTypeFields = listOfFields.get(15); //miles
				String fl = listOfFields.get(16); //fl
				
				query.setParam(d,radius);
				query.setParam(pt,coordinates);
				query.setParam(score,miles);
				query.setParam(sort, geodistSort);
				query.setParam(sfield, sfieldValue);
				query.setParam(fl, flist);
				query.setParam(queryLetter, aQuery);
				
				if(StringUtils.isNotBlank(careProvided)||StringUtils.isNotBlank(admissions)||StringUtils.isNotBlank(careHomeType))
				{
					List<String> filters = new ArrayList<String>();
					if(StringUtils.isNotBlank(careProvided))
					{
						String[] careProvided_Filters = Cleaner.splitUnderscore(careProvided);
						List<String> list = Cleaner.mergeListForFiler(careProvFields, careProvided_Filters);
						filters.addAll(list);
					}
					
					if(StringUtils.isNotBlank(admissions))
					{
						String[] admission_Filters = Cleaner.splitUnderscore(admissions);
						List<String> list = Cleaner.mergeListForFiler(admissionsFields, admission_Filters);
						filters.addAll(list);
					}
					
					if(StringUtils.isNotBlank(careHomeType))
					{
						String[] typeOfCareHome_Filters = Cleaner.splitUnderscore(careHomeType);
						List<String> list = Cleaner.mergeListForFiler(homeTypeFields, typeOfCareHome_Filters);
						filters.addAll(list);
					}
					filters.add(geofilt);
					String filteredStrings = Cleaner.createFilterParam(filters);
					filteredStrings = "("+filteredStrings+")";
					query.setParam(fq, filteredStrings);
				}
				else if(StringUtils.isBlank(careProvided)&& StringUtils.isBlank(admissions)&& StringUtils.isBlank(careHomeType))
				{
					query.setParam(fq, geofilt);
				}
	
				
			}

		
		
		if(MapUtils.isNotEmpty(qParamStartRow))
		{
			Integer qStart = (Integer)qParamStartRow.get("queryStart");
			Integer qRows = (Integer)qParamStartRow.get("queryRows");
			originalPaginatorBean = (PaginatorBean)qParamStartRow.get("originalPaginatorBean");
			
			query.setStart(qStart);
			query.setRows(qRows);
		}
		
		
		try
		{
			response = server.query(query);
			Long resultSize = response.getResults().getNumFound();
			if(resultSize>0)
			{
				careHomeBeans = new CareHomeBeans();
				List<CareHomeBean> careHomeBeanList = new ArrayList<CareHomeBean>();
				Integer numberOfResults = (int)(long)resultSize;
				resultBean = new ResultBean();
				SolrDocumentList solrList = response.getResults();
				if(CollectionUtils.isNotEmpty(solrList))
				{
					for(SolrDocument solrDoc:solrList)
					{
						CareHomeBean chb = Utils.doSearchResultBean(solrDoc);
						careHomeBeanList.add(chb);
					}
					
					careHomeBeans.setCareHomeBean(careHomeBeanList);
					
					PaginatorBean resultPBean = null;
					
					if(originalPaginatorBean!=null)
					{
						resultPBean = PaginationUtils.doPaginationTest(numberOfResults, originalPaginatorBean);
						careHomeBeans.setPaginatorBean(resultPBean);
					}
					
					resultBean.setCareHomeBeans(careHomeBeans);
					resultBean.setTotalNumberOfResults(numberOfResults);
					resultBean.setStart(query.getStart());
					
				
					
				}
			}
			server.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			log.error("Solr Exception " + ex.getMessage());
		}
		
		
		return resultBean;
		
		
		
	}
	
	
}


