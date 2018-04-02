package com.pcg.search.query;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import com.pcg.db.mongo.dao.AuthorityDAO;
import com.pcg.db.mongo.dao.SAAddressDAO;
import com.pcg.db.mongo.dao.SystemConfigDAO;
import com.pcg.db.mongo.model.Authority;
import com.pcg.db.mongo.model.SystemConfig;
import com.pcg.db.mongo.model.SAAddress.Coverage;
import com.pcg.search.api.beans.APIQueryResponse;
import com.pcg.search.api.beans.SearchResult;
import com.sa.assist.utils.AddressUtils;
import com.sa.assist.utils.SnacCode;


public class SolrSearcher {

	private static Log m_log = LogFactory.getLog(SolrSearcher.class);
	private SolrConfig solrConfig;
	private int defaultPageSize = 10;
	private int navPageCount;
	
	private HttpSolrClient solrClient;
	
//	@Autowired private VendorDAO vendorDAO;
//	@Autowired private AreaDAO areaDAO;
	@Autowired private AuthorityDAO authorityDAO;
//	@Autowired private OrganisationTypeDAO organisationTypeDAO;
	@Autowired private SystemConfigDAO systemConfigDAO;

	
	@Autowired SAAddressDAO addressMasterUKDAO;
	
	public SolrClient getSolrClient(){

		if (solrClient == null){
			try {
				m_log.info("Connecting to search server at " + solrConfig.getSolrURL());
				solrClient = new HttpSolrClient(solrConfig.getQueryURL());
				m_log.info("Connection successful");
				
			} catch (Exception e) {
				m_log.error("Search system initilisation failed");
				solrClient = null;//this will force a retry next time round
			}
		}
		return this.solrClient;
	}


	public void setSolrConfig(SolrConfig solrConfig) {
		this.solrConfig = solrConfig;
	}

	public SolrConfig getSolrConfig() {
		return solrConfig;
	}
	
//	private void addPostcodeFilter(String searchPC, SolrQuery q, SearchType searchType) {
//
//		if (StringUtils.isNotBlank(searchPC) && !searchPC.equalsIgnoreCase("POSTCODE")){
//		
//			String pc = searchPC.trim();
//					
//			if (searchType == SearchType.PRODUCT || 
//					searchType == SearchType.SERVICE || 
//					searchType == SearchType.PA || 
//					searchType == SearchType.CHILDCARE || 
//					searchType == SearchType.PROVIDER || 
//					searchType == SearchType.ADULT_PROVIDER || 
//					searchType == SearchType.CHILD_PROVIDER || 				
//					searchType == null) {
//							 
//				List<String> localGovtCodes = addressMasterUKDAO.findLocalGovtCodeByPostcode(searchPC, Coverage.LOCALAUTHORITIES);
//				
//				if (localGovtCodes.size()  > 0) {  			
//					StringBuilder filterBuff = new StringBuilder();
//					filterBuff.append("+authorities:("); 
//					for (String laSNAC : localGovtCodes) {
//						Authority authority = null;
//						if (laSNAC != null) {
//							 authority = authorityDAO.findBySnacCode(SnacCode.getMainAuthoritySNAC(laSNAC));
//						}
//						if (authority == null) {
//							 authority = authorityDAO.findBySnacCode(laSNAC);
//						}
//						if (authority != null) {
//							filterBuff.append(" ");
//							filterBuff.append(authority.getId());
//						}
//					}
//					filterBuff.append(")");
//					
//					q.addFilterQuery(filterBuff.toString());
//					
//					String outboundPostCode = AddressUtils.getPostcodeOutbound(pc);
//					List<Area> searchAreas = areaDAO.findByOutwardPart(outboundPostCode);
//					if (searchAreas.size() > 0){
//						filterBuff.append(" AND areascovered:(0"); //add in area 0 = 'all' for all searches
//						for (Area area : searchAreas) {
//							filterBuff.append(" ");
//							filterBuff.append(area.getId());
//						}
//						filterBuff.append(")");
//						
//						q.addFilterQuery(filterBuff.toString());
//					}
//					else {
//						//No areas match the postcode and/or the postcode entered was bogus. Filter out everything.
//						q.addFilterQuery("+areascovered:(-1)");
//					}
//				}
//				else {
//					//No authority matches the postcode 
//					q.addFilterQuery("+authorities:(-1)");
//				}
//			}
//			else {
//					
//				StringBuilder filterBuff = new StringBuilder("(type:CMS_* OR (type:vendor AND vendortype:" + VendorType.CAREHOME.getTypeId() + ") OR ((type:vendor OR type:service OR type:product) ");
//				
//				//TODO: Filter products and services by postcode here and leave the rest unfiltered.
//				List<String> localGovtCodes = addressMasterUKDAO.findLocalGovtCodeByPostcode(searchPC, Coverage.LOCALAUTHORITIES);
//				if (localGovtCodes.size()  > 0) {  	
//					filterBuff.append("AND authorities:("); 
//					for (String laSNAC : localGovtCodes) {
//						Authority authority = null;
//						if (laSNAC != null) {
//							 authority = authorityDAO.findBySnacCode(SnacCode.getMainAuthoritySNAC(laSNAC));
//						}
//						if (authority == null) {
//							 authority = authorityDAO.findBySnacCode(laSNAC);
//						}
//						if (authority != null) {
//							filterBuff.append(" ");
//							filterBuff.append(authority.getId());
//						}
//					}
//					filterBuff.append(")");
//					
//					String outboundPostCode = AddressUtils.getPostcodeOutbound(pc);
//					List<Area> searchAreas = areaDAO.findByOutwardPart(outboundPostCode);
//					
//					if (searchAreas.size() > 0){
//						filterBuff.append(" AND areascovered:(0"); //add in area 0 = 'all' for all searches
//						for (Area area : searchAreas) {
//							filterBuff.append(" ");
//							filterBuff.append(area.getId());
//						}
//						filterBuff.append(")");
//						
//					}
//					else {
//						//No areas match the postcode and/or the postcode entered was bogus. Filter out everything.
//						filterBuff.append(" AND areascovered:(-1)");
//					}
//					
//				}
//				else {
//					//No authority matches the postcode 
//					filterBuff.append("AND authorities:(-1)"); 
//				}
//				filterBuff.append("))");
//				q.addFilterQuery(filterBuff.toString());
//			}
//		}
//	}

	
//	private void addExclusionFilters(SolrQuery q) {
//		SystemConfig sc =  getSystemConfig();
//		boolean eventsEnabled = false;
////		if(sc.getModules().isEventsEnabled()){
////			eventsEnabled = true;
////			
////		}
//		StringBuilder filterBuff = new StringBuilder();
//		filterBuff.append("(type:CMS_* OR");
//		
//		if(eventsEnabled){
//		filterBuff.append("(type:event) OR");
//		}
//		filterBuff.append(" ((type:product OR type:service) AND productapproved:true AND vendorapproved:true)");
//		filterBuff.append(" OR (type:vendor AND vendorapproved:true)");		
//		
//		filterBuff.append(")");
//		
//		q.addFilterQuery(filterBuff.toString());
//	}
	
	// new search
	private void addGeoSearch(SolrQuery q, String latitude, String longitude, Float radius) {
		q.addFilterQuery("type:CMS_* OR {!geofilt}");
		q.setParam("sfield", "location");
		q.setParam("pt", latitude + "," +  longitude);
		q.setParam("d", radius.toString());
	
		//Sort by nearest first 
		q.setParam("sort", "geodist() asc");
		// return distance as score
		//q.setParam("sort", "score asc");
	}
	
	
//	
//	public APIQueryResponse getQueryResults(SearchInfo searchInfo) {
//		
//		APIQueryResponse response = new APIQueryResponse();
//    	
//    	response.setSearchText(searchInfo.getSearchText());
//    	
//    	//TODO Solr search here
//    	response.setTotalResults(3);
//
//    	List<SearchResult> results = new ArrayList<SearchResult>();
//    	
//    	for (int i=0; i < 3; i++){
//    		SearchResult sr = new SearchResult();
//    		
//    		sr.setTitle("Title #" + i);
//    		sr.setUrl("http://somedomain.com/" + i);
//    		sr.setDescription("Description of search result #" + i);
//    		
//    		results.add(sr);
//    	}
//    	
//    	response.setSearchResults(results);
//    	
//    	return response;
//	}
	
	
	
	
	
//	@Override
//	public SearchResult getSearchResults(String searchText, Integer searchInfo.getPageNum(), Integer searchInfo.getPageSize(), String sortOption, 
//			Long vendorId, boolean vendorSearch, boolean atoz, 
//			List<FacetSearchType> facetSearchTypeList, List<Long> categoryFacetList, List<Long> needList, 
//			boolean commissionedOnly, boolean kitemarkOnly, boolean managedVendorsOnly, boolean registeredVendorsOnly, List<Long> contextList, 
//			String searchPC, String latitude, String longitude, Float radius, String randSort, boolean isSmartSuggest,
//			boolean isApplySmartSuggestFilter, Date fromDate, Date toDate, boolean isBedSearch, boolean categoryORSearch) {
//	
//		SearchResult result = new SearchResult();
	
	public APIQueryResponse getQueryResults(SearchInfo searchInfo) {
		
		APIQueryResponse response = new APIQueryResponse();
    	
    	response.setSearchText(searchInfo.getSearchText());
		SolrQuery q = new SolrQuery();
		
		q.setQuery(searchInfo.getSearchText());

//		//Page counting starts at 1
//	    if (searchInfo.getPageNum() == null || searchInfo.getPageNum() < 1){
//	    	searchInfo.getPageNum() = 1;
//	    }
//	    
//		if (searchInfo.getPageSize() == null || searchInfo.getPageSize() < 1){
//			searchInfo.getPageSize() = defaultPageSize;
//		}
		
		q.setStart((searchInfo.getPageNum()-1) * searchInfo.getPageSize());
	    q.setRows(searchInfo.getPageSize());
	
//	    //
//	    //Vendor filtering
//	    //
//	    if (vendorId != null){
//			//
//			// Vendor search - they have their own search type and can only see their own products
//			//
//			q.setRequestHandler("/assist_vendor");
//			
//		    String vendorFilter = "+vendorid:" + vendorId;
//		    q.addFilterQuery(vendorFilter);
//	
//		    if (vendorSearch == false && isBedSearch == false){
//		    	//addExclusionFilters(q, false, true, false);
//		    	q.addFilterQuery("+(type:product OR type:service) AND productapproved:true");
//		    	q.addFilterQuery("+enabled:true");
//		    	q.addFilterQuery("+suspended:false");
//		    	q.addFilterQuery("+vendorapproved:true");
//			}
//		} else if (atoz == true){
//			q.setRequestHandler("/assist_atoz");
//			addExclusionFilters(q);
//		} else if (isBedSearch){				
//			q.setRequestHandler("/assist_carehome_bed");
//			//addCarehomeBedExclusionFilters(q);
//		}
//		else {
//			//
//			//Standard (user) search - 
//			//
			q.setRequestHandler("/pcg_std");
//			addExclusionFilters(q);
//		}
	    
//	    // facets
//	    q.setFacet(true);
//	    q.addFacetField("{!ex=foo}categorytree");
//		//q.addFacetField("{!ex=foo2}condition");
//		q.addFacetField("{!ex=foo3}type");
//		q.addFacetField("{!ex=foo4}vendortype");
//		q.addFacetField("atoz");
//		q.setFacetLimit(-1); //By default solr returns only 100 facets. Setting this to -ve number will return unlimited number of facets.	   
//		
//		FacetSearchType otherType = null;
//		boolean applyPostcodeFilter = true;
//		
//		// apply selected facets
//		if (facetSearchTypeList != null && facetSearchTypeList.size() > 0) {
//			StringBuffer fqType = new StringBuffer("");
//			StringBuffer fqVendorType = new StringBuffer("");
//			// do we have a vendorType?
//			for (FacetSearchType type : facetSearchTypeList) {
//				String fType = type.getType();
//				String fSubType = type.getSubType();
//				
//				/*if (type.name().startsWith("CMS_")){
//					applyPostcodeFilter = false;
//				}*/
//				
//				if (FacetSearchType.PROVIDER.getType().equalsIgnoreCase(fType) && StringUtils.isNotBlank(fSubType)) {
//	    			if (fqVendorType.length() > 0) {
//	    				fqVendorType.append(" OR ");
//		    		}
////	    			VendorType vt = VendorType.findByName(fSubType);
////	    			if (vt != null) {
////		    			Long vendorTypeId = vt.getTypeId();
////		    			fqVendorType.append(vendorTypeId.toString());	
////	    			}
//	    		}
//				//else if (FacetSearchType.CMS_EVENT.getType().equalsIgnoreCase(fType)) {
//					// this should be done on a schedule to remove past events
//				//	fType = getCmsEventReplaceStr();
//				//}
//	    		else {
//					if (fqType.length() > 0) {
//		    			fqType.append(" OR ");
//		    		}
//					
//					fqType.append(fType);	
//	    		}
//				
//				// set other type if applicable
//				if (vendorId != null && vendorSearch == true) {
//					if (facetSearchTypeList.size() == 1 ) {
//						if (fType.equalsIgnoreCase(FacetSearchType.SERVICE.getType())) {
//							otherType = FacetSearchType.PRODUCT;
//						} else if (fType.equalsIgnoreCase(FacetSearchType.PRODUCT.getType())) {
//							otherType = FacetSearchType.SERVICE;
//						}
//					}
//				}
//    		}
//			
//			if (fqVendorType.length() > 0) {
//				StringBuffer filterQuery = new StringBuffer("{!tag=foo3,foo4}(");
//				if (fqType.length() > 0) {
//					filterQuery.append("type:(" + fqType.toString()  + ") OR ");
//				}
//				filterQuery.append("vendortype:(" + fqVendorType.toString()+")");
//				filterQuery.append(")");
//				q.addFilterQuery(filterQuery.toString());
//		    		
//			} else if (fqType.length() > 0) {
//				q.addFilterQuery("{!tag=foo3,foo4}(type:(" + fqType.toString() + "))");				
//			}
//		}
		
//	    boolean needsBoost = false;
//		
//		if (isBedSearch){							
//			q.addFacetPivotField("vendorid,availabledates");
//			SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss'Z'");
//			q.addFilterQuery("availabledates:["+ sdfOut.format(fromDate) +" TO "+sdfOut.format(toDate)+"]");					
//		}
//		
//		// contexts
//		boolean contextRequired = false;
//		if (contextList != null && contextList.size() > 0) {
//			contextRequired = true;
//	    	StringBuffer fq = new StringBuffer("(");
//	    	for (Long id : contextList) {
//	    		if (fq.length() > 1) {
//	    			fq.append(" OR ");
//	    		}
//	    		fq.append(id.toString());	
//	    	}
//	    	fq.append(")");    	
//	      	q.addFilterQuery("contextgroup:" + fq.toString());
//	    }
//		
//		if (!isSmartSuggest) {
//			if (categoryFacetList != null && categoryFacetList.size() > 0) {
//		    	//tag=foo thing comes from Solr 1.4 book p155
//    			String categoryOperator = " AND ";
//    			
//				if (categoryORSearch == true) {
//					categoryOperator = " OR ";
//    			}
//				
//				StringBuffer fq = new StringBuffer("(");
//		    	for (Long id : categoryFacetList) {
//		    		if (id != null)
//		    		{
//			    		if (contextRequired == true) {
//			    			if (fq.length() > 1) {
//				    			fq.append(categoryOperator);
//				    		}
//			    			fq.append("(");
//			    			fq.append(id.toString());
//			    			for (Long contextId : contextList) {
//		    	    			fq.append(" OR ");
//			    	    		fq.append(contextId.toString() + "_" + id.toString());	
//			    	    	}
//			    			fq.append(")");
//			    		}
//			    		else {
//			    			if (fq.length() > 1) {
//				    			fq.append(categoryOperator);
//				    		}
//				    		
//				    		fq.append(id.toString());
//			    		}
//		    		}
//		    	}
//		    	fq.append(")");    	
//		      	q.addFilterQuery("{!tag=foo}categorytree:" + fq.toString());
//		    	
//		    }
//			else {
//				//add in category 0 = 'all' for all searches
//				q.addFilterQuery("{!tag=foo}categorytree:(0)");
//			}
//		
//		    //
//		    //Needs Boost
//		    //
//			
//		    if (needList != null && needList.size() > 0) {
//		    	needsBoost = true;
//			
//		    	StringBuffer  sbNeedFilter = new StringBuffer("(");
//		    	for (Long needId : needList) {
//		    		if (sbNeedFilter.length() > 1) {
//		    			sbNeedFilter.append(" OR ");
//		    		}
//		    		sbNeedFilter.append(needId.toString());	
//		    	}
//		    	sbNeedFilter.append(")");
//		    	q.set("defType", "edismax");
//				q.set("bq", "needtree:" + sbNeedFilter.toString() + "^1000");
//
////		    	q.addFilterQuery("needtree:" + sbNeedFilter.toString());
//		    }
//		    
//		    
//	    	// ManagedVendorsOnlyFilter
//		    if (managedVendorsOnly == true && registeredVendorsOnly == true) {
//		    	addManagedVendorsOnlyFilter(q);
//		    }
//			
//			// RegisteredVendorsOnly
//		    if (managedVendorsOnly == false && registeredVendorsOnly == true) {
//		    	addRegisteredVendorsOnly(q);
//		    }
//		    
//			// commissionedOnly
//		    if (commissionedOnly == true) {
//				addContractedFilter(q);
//			}
//		    
//		    //  kitemarkOnly
//		    if (kitemarkOnly == true) {
//				addKitemarkFilter(q);
//			}
//		}
		
//		//
//		//Spatial search
//		//Solr 3.x  geofilter takes the form
//		//fq={!geofilt}&sfield=location&pt=52.636147,-1.127472&d=0.5
//		//the distance/radius d is in km
//		//See http://wiki.apache.org/solr/SpatialSearch
//		//
	    boolean geoSearch = false;
//		boolean geoSearch = StringUtils.isNotBlank(latitude) && StringUtils.isNotBlank(longitude) && radius != null; 
//		if (geoSearch){
//			addGeoSearch(q, latitude, longitude, radius);
//		} else {
//			//Postcode filtering 
//			//Filters out vendors (and their products/services) for areas which they don't cover
//			//or if they aren't approved for the authority covering the postcode
//			//ignore for care homes as this is distance only
//			
//			//No point applying postcode filter to content items.
//			if (StringUtils.isNotBlank(searchPC) && applyPostcodeFilter) {
//				addPostcodeFilter(searchPC, q, isBedSearch);
//			}
//			// sorting 
//			addSortOption(q, result, sortOption, randSort, searchText, vendorId, vendorSearch, needsBoost);
//		}
			
		try {
			
			String context = getSystemConfig().getId();
	    	
	    	//Do the query
			QueryResponse solrResponse = makeSolrQuery(q);
			SolrDocumentList solrResponseDocs = solrResponse.getResults();
			
//			extractFacets(result, solrResponse, atoz, searchPC, contextList);
//			
//			if (isBedSearch){ //These will only be available for beds search
//				extractFacetPivots(result, solrResponse);
//			}
			
//			result.setProducts(new ArrayList<DisplayItem>());
			
			List<SearchResult> results = new ArrayList<SearchResult>();
			response.setSearchResults(results);
			
			int hitLen = solrResponseDocs.size();
			
//			//Query for a spelling suggestion / did you mean option if we have got no hits
//			if (hitLen == 0 && StringUtils.isNotBlank(searchInfo.getSearchText())) {
//				if (getSystemConfig().isSearchDidYouMean()) {
//					List<Suggestion> suggestionList = getSuggesterSearchResults(searchInfo.getSearchText(), 1);
//					
//					if (suggestionList.size() > 0) {
//						
//						String dymSearchText = suggestionList.get(0).getAlternatives().get(0);
//						
//						if (StringUtils.isNotBlank(dymSearchText)) {
//							//We have a suggested word, but will it return any results?
//							//If the word appears in a record which is disbled, then it won't, so we have to run the query
//							
//							SearchResult dymResult = getSearchResults(dymSearchText, 0, 1, null, 
//									null, false, false,  null, null, null, false, false, false, false, null, 
//									null, null, null, null, null, false, false, null, null, false, false);
//							
//							if (dymResult.getTotalResults() > 0) {
//								result.setDidYouMean(dymSearchText);
//							}
//						}
//					}
//				}
//			}
		
			//Extract the results
//			HashMap<String, String> otMap = organisationTypeDAO.getOrganisationTypesAsMap();
			for (int i=0; i < hitLen; i++) {
				SolrDocument doc = solrResponseDocs.get(i);
				SearchResult p = getResult(hitLen, i, doc, null, geoSearch, context); 
				response.getSearchResults().add(p);
			}
			
			//Populate pagination & other search metadata
			int totalResults = (int)solrResponseDocs.getNumFound();
			response.setTotalResults(totalResults);
			response.setSearchText(searchInfo.getSearchText());
			//response.setSearchType(searchType.getSearchParam());
//			response.setSortOption(searchInfo.getSortOption());
			
			// postcode
			// should no longer be need as in searchInfo
			//response.setSearchPostcode(searchPC);
			
			response.setCurrentPage(searchInfo.getPageNum());
			response.setPageSize(searchInfo.getPageSize());
			int lastPage = (totalResults / searchInfo.getPageSize()) + ((totalResults % searchInfo.getPageSize()) > 0 ? 1 : 0);
			response.setLastPage(lastPage);
			
			int firstNavPage = Math.max(searchInfo.getPageNum() - navPageCount/2, 1);
			int lastNavPage = Math.min(lastPage, firstNavPage + navPageCount - 1);
			response.setFirstNavPage(firstNavPage);
			response.setLastNavPage(lastNavPage);
			
			int firstResult = ((searchInfo.getPageNum() - 1) * searchInfo.getPageSize()) + 1;
			response.setFirstResult(firstResult);
			response.setLastResult(Math.min(firstResult + searchInfo.getPageSize() - 1, totalResults));		
			
//			if (otherType != null) {
//				doOtherSearch(q, solrResponse, result, otherType);
//			}
			
		} catch (Exception e) {
			m_log.error("Product search failed ", e);
		}
		
//		
//    	List<SearchResult> results = new ArrayList<SearchResult>();
//    	
//    	for (int i=0; i < 3; i++){
//    		SearchResult sr = new SearchResult();
//    		
//    		sr.setTitle("Title #" + i);
//    		sr.setUrl("http://somedomain.com/" + i);
//    		sr.setDescription("Description of search result #" + i);
//    		
//    		results.add(sr);
//    	}
//    	
    	
    	
    	return response;
		
		
//		return result;
	}
	
//	private void addCarehomeBedExclusionFilters(SolrQuery q) {
//		q.addFilterQuery("(type:carehome_bed AND vendorapproved:true)");
//		
//	}
//
//
//	private void extractFacetPivots(SearchResult result,
//			QueryResponse solrResponse) {
//		HashMap<Long,HashMap<LocalDate, Integer>> map = new HashMap<Long,HashMap<LocalDate, Integer>>();
//		NamedList<List<PivotField>> lst = solrResponse.getFacetPivot();
//							
//		List<PivotField> list = lst.get("vendorid,availabledates");
//		Iterator<PivotField> it = list.iterator();
//		while (it.hasNext()){
//			PivotField obj = it.next();
//			Long vendorId = Long.parseLong(obj.getValue().toString());
//			HashMap<LocalDate, Integer> dtMap = new HashMap<LocalDate, Integer>();
//			List<PivotField> avDates = obj.getPivot();
//			for (PivotField p:avDates){
//				dtMap.put(new LocalDate((Date)p.getValue()), p.getCount());
//			}
//			map.put(vendorId, dtMap);
//		}
//		
////		result.setBedAvailabilityMap(map);
//		
//	}
//

//	private void extractFacets(SearchResult result, QueryResponse solrResponse, boolean atoz, String searchPC, List<Long> contextList) {
//		//Extract the results
//		List<FacetField> catFacetFields = solrResponse.getFacetFields();
//		List<FacetField> searchTypeFacetFields = solrResponse.getFacetFields();
//		List<FacetField> vendorTypeFacetFields = solrResponse.getFacetFields();
//		List<FacetField> atoZFacetFields = solrResponse.getFacetFields();
//				
//		//Category facets
//		Map<Long, Long> categoryFacets = result.getCategoryFacets();
//		if (catFacetFields != null){	
//			for (FacetField facetField : catFacetFields) {
//				if (facetField.getName().equals("categorytree")){
//
//					List<Count> values = facetField.getValues();
//					if (values != null) {
//						for (Count count : values) {
//							String name  = count.getName();
//							
//							if (name.indexOf("_") != -1) {
//								String[] split = name.split("_");
//								Long contextId = Long.parseLong(split[0]);
//								Long catId = Long.parseLong(split[1]);
//								boolean contextOK = true;
//								if (contextList != null && contextList.size() > 0) {
//									contextOK = false;
//									for (Long cid : contextList) {
//										if (cid.equals(contextId)) {
//											contextOK = true;
//											break;
//										}
//									}
//								}
//								if (contextOK == true) {
//									categoryFacets.put(catId, count.getCount());
//								}
//							} else {
//								categoryFacets.put(Long.parseLong(count.getName()), count.getCount());
//							}
//						}
//					}
//				}
//			}
//		}
//		
//		//SearchType facets
//		Map<String, Long> searchtypeFacets = result.getSearchTypeFacets();
//		if (searchTypeFacetFields != null){	
//			for (FacetField facetField : searchTypeFacetFields) {
//				if (facetField.getName().equals("type")){
//					List<Count> values = facetField.getValues();
//					if (values != null) {
//						for (Count count : values) {
//							searchtypeFacets.put(count.getName(), count.getCount());
//						}
//					}
//				}
//			}
//		}
//		
//		//vendorType facets
//		Map<Long, Long> vendortypeFacets = result.getVendorTypeFacets();
//		if (vendortypeFacets != null){	
//			for (FacetField facetField : vendorTypeFacetFields) {
//				if (facetField.getName().equals("vendortype")){
//					List<Count> values = facetField.getValues();
//					if (values != null) {
//						for (Count count : values) {
//							vendortypeFacets.put(Long.parseLong(count.getName()), count.getCount());
//						}
//					}
//				}
//			}
//		}
//		
//		if (atoz){
//			//If A to Z search (i.e. we are drilling down to a specific letter) 
//			//then we get the facets from a separate query. 
//			SolrQuery atozCountQuery = new SolrQuery();
//			//atozCountQuery.addFilterQuery(typeFilter);
//       		atozCountQuery.setRequestHandler("/assist_atoz");
//       		atozCountQuery.addFacetField("atoz");
//       		addVendorExclusionFilter(atozCountQuery);
//       		
//       		if (StringUtils.isNotBlank(searchPC)) {
//       			addPostcodeFilter(searchPC, atozCountQuery, false);
//       		}
//    	
//    		try {
//    			QueryResponse solrAtoZResponse = makeSolrQuery(atozCountQuery);
//    			atoZFacetFields = solrAtoZResponse.getFacetFields();
//    		} catch (Exception e) {
//    			atoZFacetFields = null;
//    			m_log.error("Product search failed ", e);
//    		}
//		}
//		
//		//A to Z facets
//		Map<String, Long> atozFacets = result.getAtozFacets();
//		if (atoZFacetFields != null){
//			for (FacetField facetField : atoZFacetFields) {
//				if (facetField.getName().equals("atoz")){
//					List<Count> values = facetField.getValues();
//					if (values != null) {
//						for (Count count : values) {
//							atozFacets.put(count.getName(), count.getCount());
//						}
//					}
//				}
//			}
//		}						
//	}
	
//	private void addSortOption(SolrQuery q, SearchResult result, String sortOption, String randSort, String searchText,  Long vendorId, boolean vendorSearch, boolean needsBoost) {
//		 //Sorting options
//    	result.setRandomSortID(null);
//
//    	String sortField = null;
//	    ORDER sortOrder = null;
//	    boolean sortRequired = true;
//        
//        if (StringUtils.equalsIgnoreCase(sortOption, "price")) {
//        	sortField = "price";
//        	sortOrder = ORDER.asc;
//        } else if (StringUtils.equalsIgnoreCase(sortOption, "rating")) {
//        	sortField = "rating";
//        	sortOrder = ORDER.desc;
//        } else if (StringUtils.equalsIgnoreCase(sortOption, "supplier")) {
//        	sortField = "vendorname";
//        	sortOrder = ORDER.asc;
//        } else if (StringUtils.indexOf(sortOption, "random_") == 0) {
//        	sortField = sortOption;
//        	sortOrder = ORDER.asc;
//        }
//        else if (StringUtils.isBlank(searchText) && needsBoost == false){
//        	//If no search text the the user is browsing, and no sort has been specified yet. 
//        	
//        	if (vendorId != null){
//        		//Vendor browse - sort by title_display for alphabetical consistancy
//            	sortField = "title_display";
//            	sortOrder = ORDER.asc;
//        	}
//        	else if (vendorSearch == true) {
//        		sortField = "vendorname";
//            	sortOrder = ORDER.asc;
//        	}
//        	else {
//	        	//Catalogue / shopping browse - we create a randomly named sort field which will mix up the product list
//	        	//but will do so repeatably so that pagination will still work etc as long as we 
//	        	//continue to sort by the same randomly-named field
//        		
//        		if (randSort != null){
//        			//eLog 11122 - this should only happen if the user has got to the page via the back button and does not have
//        			//a sort parameter in their URL
//        			sortField = randSort;
//        		}
//        		else {
//        			sortField = "random_" + new Random().nextInt(10000);
//        		}
//	        	sortOrder = ORDER.asc;
//	        	sortOption = sortField;
//        		
//	        	//eLog 10700 - remember random order for use in back button link
//	        	result.setRandomSortID(sortField);
//        		
//        	}
//        }
//        else {
//        	//Leave as default relevancy
//        	sortRequired = false;
//        }
//
//	    if (sortRequired){
//	    	SortClause sortClause = new SortClause(sortField, sortOrder);
//	    	q.setSort(sortClause);
//	    }
//	}
	
//	private String getCmsEventReplaceStr() {
//		// this should be done on a schedule to remove past events
//		Date d = new Date();
//		SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss'Z'");
//		String checkDate = sdfOut.format(d); 
//		String cmsEventReplaceStr = "(type:CMS_* AND !type:CMS_EVENT) OR (type:CMS_EVENT AND eventdate:[" + checkDate + " TO *])";
//				 
//		return cmsEventReplaceStr;
//	}
	
//	//
//	//Secondary search to get counts for the other type of searchable item
//	//We run the same query as the main search but across all types, and requesting 0 results
//	//as we just want the facet counts
//	//
//	private void doOtherSearch(SolrQuery q, QueryResponse solrResponse, SearchResult result, FacetSearchType otherType) {
//		try {
//			int otherTypeCount = 0;
//			
//			//if we are only trying to get a count we may have the facet already!
//			boolean facetFound = false;
//			List<FacetField> catFacetFields = solrResponse.getFacetFields();
//			for (FacetField facetField : catFacetFields) {
//				if (facetField.getName().equals("type")) {
//					List<Count> values = facetField.getValues();
//					
//					for (Count count : values) {
//						if (count.getName().equalsIgnoreCase(otherType.getType())){
//							otherTypeCount = (int)count.getCount();
//							break;
//						}
//					}
//					facetFound = true;
//					break;
//				}
//
//			}
//			
//			if (facetFound == true) {
//				result.setOtherSearchType(otherType.getName());			
//				result.setOtherResultCount(otherTypeCount);
//			}			
//			else {
//		
//				 // facets
//			    q.setFacet(true);
//			    q.removeFacetField("{!ex=foo}categorytree");
//				//q.removeFacetField("{!ex=foo2}condition");
//				q.removeFacetField("{!ex=foo4}vendortype");
//				q.removeFacetField("atoz");
//				q.setFacetLimit(-1); //By default solr returns only 100 facets. Setting this to -ve number will return unlimited number of facets.	   
//			
//				q.add("facet.field", "type");
//				q.setRows(0);
//				
//				solrResponse = makeSolrQuery(q);
//				
//				catFacetFields = solrResponse.getFacetFields();
//				
//				if (catFacetFields != null){
//					for (FacetField facetField : catFacetFields) {
//						if (facetField.getName().equals("type")){
//							List<Count> values = facetField.getValues();
//							
//							for (Count count : values) {
//								if (count.getName().equalsIgnoreCase(otherType.getType())){
//									otherTypeCount = (int)count.getCount();
//									break;
//								}
//							}
//							break;
//						}
//					}
//				}
//		
//				result.setOtherSearchType(otherType.getName());			
//				
//				result.setOtherResultCount(otherTypeCount);
//			}
//		} catch (Exception e) {
//			m_log.error("Product search failed ", e);
//		}
//		
//	}
	

	
	private SearchResult getResult(int hitLen, int i, SolrDocument doc, HashMap<String, String> otMap, boolean geoSearch, String context) {
		SearchResult sr = new SearchResult();
//		sr.setScore(hitLen-i);
//		sr.setType((String)doc.getFieldValue("type"));
		sr.setTitle((String)doc.getFieldValue("title_display"));
		sr.setDescription((String)doc.getFieldValue("description_display"));
		
		boolean cmsContent = false;
		if (doc.containsKey("pageUrl")) {
			if (doc.containsKey("type")) {
				String type = (String)doc.getFieldValue("type");
				if (StringUtils.startsWith(type, "CMS_")) {
					cmsContent = true;
				}
			} 
			
			if (cmsContent == true) {
				sr.setPageUrl((String)doc.getFieldValue("pageUrl"));
			}
			else {
				sr.setPageUrl("/"+ context + (String)doc.getFieldValue("pageUrl"));
			}
		}
		
		// remove first letter of id if not cms
		String sId = (String)doc.getFieldValue("id");
		if (cmsContent == false) {
			if (Character.isLetter(sId.charAt(0))) {
				sId = sId.substring(1);
			}
		}
//		sr.setId(sId);
//		
//		if (doc.containsKey("type")) {
//			sr.setType((String)doc.getFieldValue("type"));
//		}
//	
//		
//		 
//		if (doc.containsKey("imagepath")) {
//			sr.setImagePath((String)doc.getFieldValue("imagepath"));
//		}
//		
//		if (doc.containsKey("enabled")) {
//			sr.setEnabled((Boolean)doc.getFieldValue("enabled"));
//		}
//		
//		if (doc.containsKey("vendormanaged")) {
//			sr.setVendorManaged((Boolean)doc.getFieldValue("vendormanaged"));
//		}
//		
//		if (doc.containsKey("vendorid")) {
//		sr.setVendorId((Integer)doc.getFieldValue("vendorid"));
//		}
//		
//		if (doc.containsKey("vendorparentname")) {
//			sr.setVendorParentName((String)doc.getFieldValue("vendorparentname"));
//		}
//		
//		if (doc.containsKey("vendorparentid")) {
//			sr.setVendorParentId((Integer)doc.getFieldValue("vendorparentid"));
//		}
//		
//		if (doc.containsKey("vendorname")) {
//			sr.setVendorBusinessName((String)doc.getFieldValue("vendorname"));
//		}
//		
//		if (doc.containsKey("vendortype")) {
//			sr.setVendorTypeId((Integer)doc.getFieldValue("vendortype"));
//		}
//		
//		if (doc.containsKey("vendoremail")) {
//			sr.setVendorEmail((String)doc.getFieldValue("vendoremail"));
//		}
//		
//		if (doc.containsKey("vendorwebsite")) {
//			sr.setVendorWebsite((String)doc.getFieldValue("vendorwebsite"));
//		}
//		
//		if (doc.containsKey("vendortelephone")) {
//			sr.setVendorTelephone((String)doc.getFieldValue("vendortelephone"));
//		}
//		
//		if (doc.containsKey("rating")) {
//			sr.setStarRating((Integer)doc.getFieldValue("rating"));
//		}
//		
//		if (doc.containsKey("reviewcount")) {
//			sr.setReviewCount((Integer)doc.getFieldValue("reviewcount"));
//		}
//		if (doc.containsKey("productCount")) {
//			sr.setProductCount((Integer)doc.getFieldValue("productCount"));
//		}
//		if (doc.containsKey("serviceCount")) {
//			sr.setServiceCount((Integer)doc.getFieldValue("serviceCount"));
//		}
//	
//		if (doc.containsKey("location_0_coordinate")) {
//			sr.setLatitude((Double)doc.getFieldValue("location_0_coordinate"));
//		}
//		
//		if (doc.containsKey("location_1_coordinate")) {
//			sr.setLongtitude((Double)doc.getFieldValue("location_1_coordinate"));
//		}

		//if (geoSearch == true){
		//  WE ARE SORtING BY DISPAL7Y NOT RETURNING THE DISTANCE
		//	populateResultDistance(doc, p);
		//}
		
//		if (doc.containsKey("productmanaged")) {
//			p.setProductManaged((Boolean)doc.getFieldValue("productmanaged"));
//		}
//				
//		if (doc.containsKey("price")) {
//			p.setPrice(Double.parseDouble(doc.getFieldValue("price").toString()));
//		}
//		
//		if (doc.containsKey("pricefrom")) {
//			p.setPriceFrom(Double.parseDouble(doc.getFieldValue("pricefrom").toString()));
//		}
//		
//		if (doc.containsKey("priceto")) {
//			p.setPriceTo(Double.parseDouble(doc.getFieldValue("priceto").toString()));
//		}
//		
//		if (doc.containsKey("pricedescription")) {
//			p.setPriceDescription((String)doc.getFieldValue("pricedescription"));
//		}
//		
//		if (doc.getFieldValue("quote") != null) {
//			p.setQuote((Boolean)doc.getFieldValue("quote"));
//		}
//		
//		if (doc.getFieldValue("vendoraccredited") != null) {
//			p.setVendorAccredited((Boolean)doc.getFieldValue("vendoraccredited"));
//		}
//		
//		if (doc.getFieldValue("stocklevel") != null) {
//			p.setStockLevel((Integer)doc.getFieldValue("stocklevel"));
//		}
//		
//		if (doc.containsKey("contracted")) {
//			p.setContracted((Boolean)doc.getFieldValue("contracted"));
//		}
//		
//		if (doc.containsKey("kitemark")) {
//			p.setKitemark((Boolean)doc.getFieldValue("kitemark"));
//		}
		
//		if (doc.containsKey("callback")) {
//			p.setCallback((Boolean)doc.getFieldValue("callback"));
//		}
//		
//		Collection<Object> coll = doc.getFieldValues("organisationTypes");
//		Collection<Object> otColl = new ArrayList<Object>();					
//		if (coll != null) {
//			for (Object o:coll){
//				String desc = otMap.get(o.toString());
//				otColl.add(desc);
//			}
//			String otList =StringUtils.join(otColl.toArray(), ", ");											
//			p.setOrganisationTypesDisplay(otList);
//		}
//		
//		
//		if (doc.containsKey("availabledates")) {
//			Collection<Object> avDates = doc.getFieldValues("availabledates");
//			for (Object obj:avDates){
//				Date d = (Date)obj;
//				sr.getAvailabledates().add(d);
//			}
//		}
//		
//		if (doc.containsKey("smartsuggest")) {
//			sr.setSmartSuggest((Boolean)doc.getFieldValue("smartsuggest"));
//		}
		
		return sr;
						
	}
	// end of new search
	
//	private void addVendorExclusionFilter(SolrQuery q) {
//		q.addFilterQuery("+vendorapproved:true");
//	}
//	
//	private void addProductExclusionFilter(SolrQuery q) {
//		q.addFilterQuery("+productapproved:true");
//	}
	
//	private void populateResultDistance(SolrDocument doc, StandardProduct p) {
//		
//		Float dist = (Float)doc.getFieldValue("score");
//
//		// dist will be in KM so convert to miles (TODO configure this as an option)
//		if (dist != null && dist != 0) {
//			dist = dist/1.609344f;
//		}
//
//		DecimalFormat twoDForm = new DecimalFormat("#.##");
//		p.setDistance(twoDForm.format(dist));
//	}

	private QueryResponse makeSolrQuery(SolrQuery q) throws SolrServerException, IOException {
		long t1 = System.currentTimeMillis();
		m_log.debug("Performing Solr search: " + URLDecoder.decode(q.toString(), "UTF-8"));
		QueryResponse response = getSolrClient().query(q);
		m_log.debug("Search took " + (System.currentTimeMillis() - t1) + "ms");
		return response;
	}

	public void setNavPageCount(int navPageCount) {
		this.navPageCount = navPageCount;
	}

	public int getNavPageCount() {
		return navPageCount;
	}

	public void setDefaultPageSize(int defaultPageSize) {
		this.defaultPageSize = defaultPageSize;
	}

	public int getDefaultPageSize() {
		return defaultPageSize;
	}

//	public SearchResult getProductSearchResults(List<Long> productIDs, Long pType, String searchPC){
//	
//		SearchResult result = new SearchResult();
//		result.setProducts(new ArrayList<DisplayItem>());
//		
//		if (productIDs != null && productIDs.size() > 0){
//			
//			try {
//				//Create the query
//				SolrQuery q = new SolrQuery();
//				q.setRequestHandler("/assist_std");
//	
//				//TODO include this section if type-specific results are needed				
//				//				if (pType.equals(ProductType.SERVICE.getTypeId())){
//				//					q.addFilterQuery("type:service");
//				//				}
//				//				else if (pType.equals(ProductType.PRODUCT.getTypeId())){
//				//					q.addFilterQuery("type:product");
//				//				}
//		
//			    addVendorExclusionFilter(q);
//			    addProductExclusionFilter(q);
//			    
//			    addPostcodeFilter(searchPC, q, null);
//			    
//				StringBuilder productFilter = new StringBuilder("(");
//				productFilter.append("+id:(");
//			    for (Long pID : productIDs) {
//			    	productFilter.append("p");
//			    	productFilter.append(pID);
//			    	productFilter.append(" ");
//				}
//			    productFilter.append("))");
//				q.addFilterQuery(productFilter.toString());
//			    
//			    
//		    	//Do the query
//				QueryResponse solrResponse = makeSolrQuery(q);
//				SolrDocumentList solrResponseDocs = solrResponse.getResults();
//		
//				//Extract the results  
//		
//				result.setTotalResults((int)solrResponseDocs.getNumFound());
//		
//				int hitLen = solrResponseDocs.size();
//				for (int i=0; i < hitLen; i++) {
//					SolrDocument doc = solrResponseDocs.get(i);
//					StandardProduct p = new StandardProduct();
//					p.setScore(hitLen-i);
//					p.setTitle((String)doc.getFieldValue("title_display"));
//					p.setDescription((String)doc.getFieldValue("description_display"));
//					String sId = (String)doc.getFieldValue("id");
//					String replace = "p";
//					if (StringUtils.contains(sId, "c")) {
//						replace = "c";
//					}
//					String id = StringUtils.replace(sId, replace, "");
////					p.setId(id);
////					p.setPrice(doc.getFieldValue("price")==null ?  null : ((Float)doc.getFieldValue("price")).doubleValue()); //Temp fix
////					
////					p.setPriceFrom(doc.getFieldValue("pricefrom")==null ?  null : ((Float)doc.getFieldValue("pricefrom")).doubleValue()); //Temp fix
////					p.setPriceTo(doc.getFieldValue("priceto")==null ?  null : ((Float)doc.getFieldValue("priceto")).doubleValue()); //Temp fix
//					
//					p.setPriceDescription((String)doc.getFieldValue("pricedescription"));
//					p.setStarRating((Integer)doc.getFieldValue("rating"));
//					p.setVendorBusinessName((String)doc.getFieldValue("vendorname"));
//					p.setReviewCount((Integer)doc.getFieldValue("reviewcount"));
//					p.setQuote((Boolean)doc.getFieldValue("quote"));
//					p.setVendorAccredited((Boolean)doc.getFieldValue("vendoraccredited"));
//					p.setStockLevel((Integer)doc.getFieldValue("stocklevel"));
//					p.setVendorId((Integer)doc.getFieldValue("vendorid"));
//					p.setImagePath((String)doc.getFieldValue("imagepath"));
//					//p.setCategory((String)doc.getFieldValue("maincategory"));
//					p.setEnabled((Boolean)doc.getFieldValue("enabled"));
////					if (doc.containsKey("callback")) {
////						p.setCallback((Boolean)doc.getFieldValue("callback"));
////					}
//					p.setProductManaged((Boolean)doc.getFieldValue("productmanaged"));
//					
////					Collection<Object> accreditationimagepaths = doc.getFieldValues("accreditationimagepaths");
////					
////					if (accreditationimagepaths != null){
////						List<StandardAccreditation> accreditations = p.getAccreditations();
////						for (Object aip : accreditationimagepaths) {
////							if (aip instanceof String) {
////								String accreditationimagepath = (String)aip;	
////								String[] splits = StringUtils.split(accreditationimagepath, "|");
////								if (splits.length == 3) {
////									StandardAccreditation standardAccreditation = new StandardAccreditation();
////									standardAccreditation.setDocumentPath(splits[0]);
////									standardAccreditation.setImagePath(splits[1]);
////									Boolean linkEnabled = Boolean.parseBoolean(splits[2]);
////									standardAccreditation.setLinkEnabled(linkEnabled);
////									accreditations.add(standardAccreditation);
////								}
////							}
////						}
////					}
//					
//					result.getProducts().add(p);
//				}
//	
//			}
//			catch (Exception e) {
//				m_log.error("Product search failed ", e);
//			}  
//		}
//		return result;
//	}
//	
//	public SearchResult getRelatedItems(String searchText, String searchCategory, Integer noOfItems) {
//	
//		SearchResult result = new SearchResult();
//		result.setProducts(new ArrayList<DisplayItem>());
//		
//		try {
//			
//			SolrQuery q = new SolrQuery();
//	
//			q.setQuery(searchText);
//	
//			q.setRequestHandler("/assist_std");
//			addVendorExclusionFilter(q);
//			addProductExclusionFilter(q);
//	
//		    String typeFilter = "type:(service or product)";
//		    q.addFilterQuery(typeFilter);
//		    
//		    if (StringUtils.isNotBlank(searchCategory) && StringUtils.isNumeric(searchCategory)){
//		    	//tag=foo thing comes from Solr 1.4 book p155
//		    	q.addFilterQuery("{!tag=foo}categorytree:" + searchCategory); 
//		    }
//		    
//		    q.setRows(noOfItems);
//		    
//		    //Do the query
//			QueryResponse solrResponse = makeSolrQuery(q);
//			SolrDocumentList solrResponseDocs = solrResponse.getResults();
//	
//			//Populate pagination & other search metadata
//			int totalResults = (int)solrResponseDocs.getNumFound();
//			result.setTotalResults(totalResults);
//	
//			int hitLen = solrResponseDocs.size();
//			for (int i=0; i < hitLen; i++) {
//				SolrDocument doc = solrResponseDocs.get(i);
//				StandardProduct p = new StandardProduct(doc, hitLen-i);
//				result.getProducts().add(p);
//			}
//			
//			return result;
//		    
//		} catch (Exception e) {
//			m_log.error("Selected items search failed ", e);
//		}
//	    
//	    return null;
//		
//	}
//	
//	public SearchResult getSelectedRelatedContent(String contentIDs) {
//	
//		SearchResult result = new SearchResult();
//		result.setProducts(new ArrayList<DisplayItem>());
//		
//		if (contentIDs != null && contentIDs.length() > 0){
//			
//			try {
//				//Create the query
//				SolrQuery q = new SolrQuery();
//				
//				
//				String typeFilter = "type:(CMS*)";
//			    q.addFilterQuery(typeFilter);
//		    
//			    q.setRows(9999);
//				
//				StringBuilder contentFilter = new StringBuilder("(");
//				contentFilter.append("+id:(");
//				
//				StringBuilder sb = new StringBuilder();
//				String[] ids = contentIDs.split(",");
//				
//				for (String id : ids) {
//					if (sb.length() > 0) {
//						sb.append(" or ");
//					}
//					
//					sb.append("CMS_CONTENT"+id);
//				}
//				
//				contentFilter.append(sb.toString());
//				contentFilter.append("))");
//			
//				q.setQuery(contentFilter.toString());
//			    
//		    	//Do the query
//				QueryResponse solrResponse = makeSolrQuery(q);
//				SolrDocumentList solrResponseDocs = solrResponse.getResults();
//		
//				//Populate pagination & other search metadata
//				int totalResults = (int)solrResponseDocs.getNumFound();
//				result.setTotalResults(totalResults);
//		
//				int hitLen = solrResponseDocs.size();
//				for (int i=0; i < hitLen; i++) {
//					SolrDocument doc = solrResponseDocs.get(i);
//					StandardProduct p = new StandardProduct(doc, hitLen-i);
//					result.getProducts().add(p);
//				}
//				
//				return result;
//				
//			} catch (Exception e) {
//				m_log.error("Selected related items search failed ", e);
//			}
//		}
//		
//		return null;
//	}
//
//	public SearchResult getSelectedRelatedItems(String productIDs) {
//		
//		SearchResult result = new SearchResult();
//		result.setProducts(new ArrayList<DisplayItem>());
//		
//		if (productIDs != null && productIDs.length() > 0){
//			
//			try {
//				//Create the query
//				SolrQuery q = new SolrQuery();
//				q.setRequestHandler("/assist_std");
//				
//				addVendorExclusionFilter(q);
//				addProductExclusionFilter(q);
//				
//				StringBuilder productFilter = new StringBuilder("(");
//				productFilter.append("+id:(");
//				productFilter.append(productIDs);
//			    productFilter.append("))");
//				q.addFilterQuery(productFilter.toString());
//			    
//			    
//		    	//Do the query
//				QueryResponse solrResponse = makeSolrQuery(q);
//				SolrDocumentList solrResponseDocs = solrResponse.getResults();
//		
//				//Populate pagination & other search metadata
//				int totalResults = (int)solrResponseDocs.getNumFound();
//				result.setTotalResults(totalResults);
//		
//				int hitLen = solrResponseDocs.size();
//				for (int i=0; i < hitLen; i++) {
//					SolrDocument doc = solrResponseDocs.get(i);
//					StandardProduct p = new StandardProduct(doc, hitLen-i);
//					result.getProducts().add(p);
//				}
//				
//				return result;
//				
//			} catch (Exception e) {
//				m_log.error("Selected related items search failed ", e);
//			}
//		}
//		
//		return null;
//	}
	
//	@Override
//	public SearchResult getVendorSearchResults(String vendorIds) {
//		SearchResult result = new SearchResult();
//		result.setProducts(new ArrayList<DisplayItem>());
//		
//		try {
//			
//			SolrQuery q = new SolrQuery();
//
//			String[] split = vendorIds.split(",");
//			
//			// need to maintain order			
//			int totalResults = 0;
//			for (String id : split) {
//				q.setQuery("id:(" + id +")");
//	
//			    String typeFilter = "type:(vendor)";
//			    q.addFilterQuery(typeFilter);
//			    
//			    q.setRows(1);
//			    
//			    //Do the query
//				QueryResponse solrResponse = makeSolrQuery(q);
//				SolrDocumentList solrResponseDocs = solrResponse.getResults();
//				totalResults += (int)solrResponseDocs.getNumFound();
//			
//				int hitLen = solrResponseDocs.size();
//				for (int i=0; i < hitLen; i++) {
//					SolrDocument doc = solrResponseDocs.get(i);
//					StandardProduct p = new StandardProduct(doc, hitLen-i);
//					result.getProducts().add(p);
//				}
//			}  
//	
//			//Populate pagination & other search metadata
//			result.setTotalResults(totalResults);
//	
//			
//			
//			return result;
//		    
//		} catch (Exception e) {
//			m_log.error("Selected items search failed ", e);
//		}
//	    
//	    return null;
//	}
	
	
//	public SearchResult getRelatedPanelItems(String searchText, String searchTypes) {
//		
//		SearchResult result = new SearchResult();
//		result.setProducts(new ArrayList<DisplayItem>());
//		
//		try {
//			
//			SolrQuery q = new SolrQuery();
//
//			q.setQuery(searchText);
//
//			q.setRequestHandler("/assist_std");
//			addVendorExclusionFilter(q);
//			addProductExclusionFilter(q);
//
//			String[] split = searchTypes.split(",");
//			StringBuffer sb = new StringBuffer();
//			boolean start = true;
//			for (String type : split) {
//				if (!start) sb.append(" or ");
//				sb.append(type);
//				start = false;
//			}
//			
//		    String typeFilter = "type:(" + sb.toString() + ")";
//		    q.addFilterQuery(typeFilter);
//		    
//		    q.setRows(5);
//		    
//		    //Do the query
//			QueryResponse solrResponse = makeSolrQuery(q);
//			SolrDocumentList solrResponseDocs = solrResponse.getResults();
//	
//			//Populate pagination & other search metadata
//			int totalResults = (int)solrResponseDocs.getNumFound();
//			result.setTotalResults(totalResults);
//	
//			int hitLen = solrResponseDocs.size();
//			for (int i=0; i < hitLen; i++) {
//				SolrDocument doc = solrResponseDocs.get(i);
//				StandardProduct p = new StandardProduct(doc, hitLen-i);
//				result.getProducts().add(p);
//			}
//			
//			return result;
//		    
//		} catch (Exception e) {
//			m_log.error("Selected items search failed ", e);
//		}
//	    
//	    return null;
//		
//	}
//	
//	
//	/**
//	 * Currently receives single id value for each meta type
//	 */
//	public SearchResult getRelatedPanelMetaItems(String condition, String product, String vendor, String need) {
//		
//		SearchResult result = new SearchResult();
//		result.setProducts(new ArrayList<DisplayItem>());
//		
//		//Its possible the user may not have set any metadata tags so will need a default search pattern here too.
//		
//			    
//	    return null;
//		
//	}
//	
//	
//	@Override
//	public SearchResult getVendorSearchResults(String vendorIds) {
//		SearchResult result = new SearchResult();
//		result.setProducts(new ArrayList<DisplayItem>());
//		
//		try {
//			
//			SolrQuery q = new SolrQuery();
//
//			String[] split = vendorIds.split(",");
//			
//			
//			// need to maintain order			
//			int totalResults = 0;
//			for (String id : split) {
//				q.setQuery("id:(" + id +")");
//	
//			    String typeFilter = "type:(vendor)";
//			    q.addFilterQuery(typeFilter);
//			    
//			    q.setRows(1);
//			    
//			    //Do the query
//				QueryResponse solrResponse = makeSolrQuery(q);
//				SolrDocumentList solrResponseDocs = solrResponse.getResults();
//				totalResults += (int)solrResponseDocs.getNumFound();
//			
//				int hitLen = solrResponseDocs.size();
//				for (int i=0; i < hitLen; i++) {
//					SolrDocument doc = solrResponseDocs.get(i);
//					StandardProduct p = new StandardProduct(doc, hitLen-i);
//					result.getProducts().add(p);
//				}
//			}  
//	
//			//Populate pagination & other search metadata
//			result.setTotalResults(totalResults);
//	
//			
//			
//			return result;
//		    
//		} catch (Exception e) {
//			m_log.error("Selected items search failed ", e);
//		}
//	    
//	    return null;
//	}
//	
//	/*
//	 * Special call for RHP search. Does a search similar to getSearchResults but handles cats, conditions and needs as ORed NOT as filters
//	 * Does not return any facets
//	 */
//	public SearchResult getSearchResultsRHP(String searchText, SearchType searchType, String category, Integer page, Integer pageSize, 
//			String sortOption, List<Long> needList, String latitude, 
//			String longitude, Float radiusKm, String searchPC, String randSort, String condition) {
//		
//		SearchResult result = new SearchResult();
//		
//		SolrQuery q = new SolrQuery();
//		
//	    String typeFilter = searchType.getSearchFilter();
//	    
//	    q.addFilterQuery(typeFilter);
//	    
//	    //Page counting starts at 1
//	    if (page == null || page < 1){
//	    	page = 1;
//	    }
//	    
//		if (pageSize == null || pageSize < 1){
//			pageSize = defaultPageSize;
//		}
//	    
//	    q.setStart((page-1) * pageSize);
//	    q.setRows(pageSize);
//	    
//	    //Sorting options
//    	result.setRandomSortID(null);
//
//    	String sortField = null;
//	    ORDER sortOrder = null;
//	    boolean sortRequired = true;
//        
//        if (StringUtils.equalsIgnoreCase(sortOption, "price")) {
//        	sortField = "price";
//        	sortOrder = ORDER.asc;
//        } else if (StringUtils.equalsIgnoreCase(sortOption, "rating")) {
//        	sortField = "rating";
//        	sortOrder = ORDER.desc;
//        } else if (StringUtils.equalsIgnoreCase(sortOption, "supplier")) {
//        	sortField = "vendorname";
//        	sortOrder = ORDER.asc;
//        } else if (StringUtils.indexOf(sortOption, "random_") == 0) {
//        	sortField = sortOption;
//        	sortOrder = ORDER.asc;
//        }
//        else if (StringUtils.isBlank(searchText)){
//        	//If no search text the the user is browsing, and no sort has been specified yet. 
//        	
//        	if (searchType.equals(SearchType.PROVIDER)) {
//        		sortField = "vendorname";
//            	sortOrder = ORDER.asc;
//        	}
//        	else {
//	        	//Catalogue / shopping browse - we create a randomly named sort field which will mix up the product list
//	        	//but will do so repeatably so that pagination will still work etc as long as we 
//	        	//continue to sort by the same randomly-named field
//        		
//        		if (randSort != null){
//        			//eLog 11122 - this should only happen if the user has got to the page via the back button and does not have
//        			//a sort parameter in their URL
//        			sortField = randSort;
//        		}
//        		else {
//        			sortField = "random_" + new Random().nextInt(10000);
//        		}
//	        	sortOrder = ORDER.asc;
//	        	sortOption = sortField;
//        		
//	        	//eLog 10700 - remember random order for use in back button link
//	        	result.setRandomSortID(sortField);
//    		}
//        }
//        else {
//        	//Leave as default relevancy
//        	sortRequired = false;
//        }
//
//	    if (sortRequired){
//	    	q.setSortField(sortField, sortOrder);
//	    }
//	    
//	    // set query
//	    StringBuffer sbQuery = new StringBuffer();
//	    if (StringUtils.isNotBlank(searchText)) {
//	    	sbQuery.append("( title:"+searchText + "^2.0");
//	    	sbQuery.append(" OR description:"+searchText + "^1.0");
//	    	sbQuery.append(" OR keywords:"+searchText + "^1.0)");
//	    }
//	    	
//	    
//	    if (StringUtils.isNotBlank(category)){
//	    	String[] categoryValues = category.split(",");
//	    	if (categoryValues.length > 0) {
//	    		if (sbQuery.length() > 0) {
//	    			sbQuery.append(" OR ");
//	    		}
//	    		
//		    	StringBuilder  sbCategoryFilter = new StringBuilder();
//		    	for (String categoryId : categoryValues) {
//		    		if (sbCategoryFilter.length() > 0) {
//		    			sbCategoryFilter.append(" OR ");
//		    		}
//		    		sbCategoryFilter.append(categoryId);	
//		    	}
//		    	
//		    	sbQuery.append("(categorytree: (" + sbCategoryFilter.toString() + "))");
//	    	}
//	    	
//	    	
//	    }
//	   
//	    if (StringUtils.isNotBlank(condition)){
//	    	String[] categoryValues = condition.split(",");
//	    	if (categoryValues.length > 0) {
//	    		if (sbQuery.length() > 0) {
//	    			sbQuery.append(" OR ");
//	    		}
//		    	StringBuilder  sbCategoryFilter = new StringBuilder();
//		    	for (String categoryId : categoryValues) {
//		    		if (sbCategoryFilter.length() > 0) {
//		    			sbCategoryFilter.append(" OR ");
//		    		}
//		    		sbCategoryFilter.append(categoryId);	
//		    	}
//		    	
//		    	sbQuery.append("(condition: (" + sbCategoryFilter.toString() +"))");
//	    	}
//	    }
//	    //
//	    //Need filtering
//	    //
//	    if (needList != null && needList.size() > 0) {
//	    	if (sbQuery.length() > 0) {
//    			sbQuery.append(" OR ");
//    		}
//	    	StringBuilder  sbNeedFilter = new StringBuilder();
//	    	for (Long needId : needList) {
//	    		if (sbNeedFilter.length() > 0) {
//	    			sbNeedFilter.append(" OR ");
//	    		}
//	    		sbNeedFilter.append(needId.toString());	
//	    	}
//	    	
//	    	sbQuery.append("(needtree: (" + sbNeedFilter.toString() + "))");
//	    }
//	    
//	    //Add 
//	    if (sbQuery.length() == 0) q.setQueryType("assist_std");
//	    
//	    q.setQuery(sbQuery.toString());
//	    
//	    
//	    
//	    //
//	    //Vendor filtering 
//	    //
//	   
//		if (SearchType.CMS_CONTENT.equals(searchType)) {
//			
//		}
//		else if (SearchType.WEBSITE.equals(searchType)) {
//			
//		}
//		else if (SearchType.RUNTIME.equals(searchType)) {
//			
//		}
//		else {
//		    addVendorExclusionFilter(q);
//		}
//		
//		//
//		//Spatial search
//		//Solr 3.x  geofilter takes the form
//		//fq={!geofilt}&sfield=location&pt=52.636147,-1.127472&d=0.5
//		//the distance/radius d is in km
//		//See http://wiki.apache.org/solr/SpatialSearch
//		//
//		boolean geoSearch = StringUtils.isNotBlank(latitude) && StringUtils.isNotBlank(longitude) && radiusKm != null; 
//		if (geoSearch){
//			q.addFilterQuery("{!geofilt}");
//			q.setParam("sfield", "location");
//			q.setParam("pt", latitude + "," +  longitude);
//			q.setParam("d", radiusKm.toString());
//
//			//Including the following query will return the distance as the score
//			//TODO don't know how we deal with situation where we want a text search as well 
//			//q.setParam("q" , "{!func}geodist()");
//			q.setParam("fl", "score");
//
//			//Sort by nearest first 
//			q.setParam("sort", "geodist() asc");
//		}
//		
//		//
//		//Postcode filtering 
//		//Filters out vendors (and their products/services) for areas which they don't cover
//		//or if they aren't approved for the authority covering the postcode
//		//
//		if (searchType.equals(SearchType.PRODUCT) || searchType.equals(SearchType.SERVICE) || searchType.equals(SearchType.WEBSITE)) {
//			addPostcodeFilter(searchPC, q, searchType);
//		}
//		
//		
//	    try {
//	    	
//	    	//Do the query
//			QueryResponse solrResponse = makeSolrQuery(q);
//			SolrDocumentList solrResponseDocs = solrResponse.getResults();
//
//			//Populate result list
//			result.setProducts(new ArrayList<DisplayItem>());
//			int hitLen = solrResponseDocs.size();
//					
//			for (int i=0; i < hitLen; i++) {
//				SolrDocument doc = solrResponseDocs.get(i);
//				StandardProduct p = new StandardProduct();
//				p.setScore(hitLen-i);
//				p.setTitle((String)doc.getFieldValue("title_display"));
//				p.setDescription((String)doc.getFieldValue("description_display"));
//				String sId = (String)doc.getFieldValue("id");
//				String replace = "p";
//				if (StringUtils.contains(sId, "c")) {
//					replace = "c";
//				}
//				String id = StringUtils.replace(sId, replace, "");
//				p.setId(id);
//				
//				p.setType((String)doc.getFieldValue("type"));
//				p.setPageUrl((String)doc.getFieldValue("pageUrl"));
//				
//			
//				p.setLatitude((Double)doc.getFieldValue("location_0_coordinate"));
//				p.setLongtitude((Double)doc.getFieldValue("location_1_coordinate"));
//				doc.getFieldValue("location_1_coordinate");
//			
//				if (geoSearch){
//					populateResultDistance(doc, p);
//			    }
//			
//				if (searchType.equals(SearchType.PROVIDER) || searchType.equals(SearchType.MANAGED_PROVIDER)  || searchType.equals(SearchType.REGISTERED_PROVIDER)) {
//				
//					p.setVendorBusinessName((String)doc.getFieldValue("vendorname"));
//					p.setReviewCount((Integer)doc.getFieldValue("reviewcount"));
//					p.setVendorAccredited((Boolean)doc.getFieldValue("vendoraccredited"));
//					p.setVendorId((Integer)doc.getFieldValue("vendorid"));
//					p.setImagePath((String)doc.getFieldValue("imagepath"));
//					p.setEnabled((Boolean)doc.getFieldValue("enabled"));
//					p.setVendorManaged((Boolean)doc.getFieldValue("vendormanaged"));
//					p.setVendorTypeId((Integer)doc.getFieldValue("vendortype"));
//			
//				}
//				
//			
//			
//				
//				result.getProducts().add(p);
//			}
//			
//			//Populate pagination & other search metadata
//			int totalResults = (int)solrResponseDocs.getNumFound();
//			result.setTotalResults(totalResults);
//			result.setSearchText(searchText);
//			result.setSearchType(searchType.getSearchParam());
//			result.setSortOption(sortOption);
//			result.setSearchCategory(category);
//			result.setSearchCondition(condition);
//			
//			
//			
//	    } catch (Exception e) {
//			m_log.error("Search failed ", e);
//		}   
//		
//		return result;
//	}

//
//	private String getRelevenceQuery(String searchText) {
//		
//		if (searchText != null){
//			//Remove extra whitespaces from input searchText
//			searchText = searchText.trim();
//			searchText = searchText.replaceAll("\\s+", " ");
//		}
//
//		return searchText;
		
//
//TW SOM-218. I have removed Monali's code below. Keeping it for reference in case we need to reinstate. 
//It is trying to promote results containing phrases above ones containing all the individual search words, above ones containiong at least some of the search words
//However, in combination with the assist_std dismax request handler, it is having the effect of returning NO results for phrase queries like 'my father has dementia' 		
//		
		
		//return searchText of format - ("Dementia Care")^10 OR (Dementia AND Care)^2.0 OR (Dementia OR Care)
		
//		if (searchText != null){
//			//Remove extra whitespaces from input searchText
//			searchText = searchText.trim();
//			searchText = searchText.replaceAll("\\s+", " ");
//		}
//		//Return as it is if blank or single word
//		if (StringUtils.isBlank(searchText) || !StringUtils.contains(searchText, ' ')){
//			return searchText;
//		}
//		
//		
//		StringBuffer relevanceQuery = new StringBuffer();
//		StringBuffer phraseQuery = new StringBuffer("\"" + searchText + "\"");
//		StringBuffer andQuery = new StringBuffer();
//		StringBuffer orQuery = new StringBuffer();
//		
//		String phraseQueryBoost = "^10";
//		String andQueryBoost = "^2";
//		
//		String[] tokens = searchText.split(" ");
//		boolean isFirst = true;
//		if (tokens != null && tokens.length>0){
//			for (int i=0; i<tokens.length;i++){
//				if (!isFirst){
//					andQuery.append(" AND ");
//					orQuery.append(" OR ");
//				}
//				andQuery.append(tokens[i]);
//				orQuery.append(tokens[i]);
//				isFirst = false;
//			}
//		}
//		
//		relevanceQuery.append("( ").append(phraseQuery.toString()).append(" )").append(phraseQueryBoost);
//		relevanceQuery.append(" OR ");
//		relevanceQuery.append("( ").append(andQuery.toString()).append(" )").append(andQueryBoost);
//		relevanceQuery.append(" OR ");
//		relevanceQuery.append("( ").append(orQuery.toString()).append(" )");
//				
//		return relevanceQuery.toString();
//	}
	
	
	
	public List<Suggestion> getSuggesterSearchResults(String searchText, Integer maxResults) {
		List<Suggestion> suggestionList = new ArrayList<Suggestion>();
		
		SolrQuery q = new SolrQuery();

		//q.setParam(CommonParams.QT, "/suggester");
		q.setRequestHandler("/suggester");
		q.setParam("spellcheck.count", maxResults.toString());
		q.add("q", searchText);
		
		try {
			QueryResponse solrResponse = makeSolrQuery(q);
			SpellCheckResponse spellCheckResponse = solrResponse.getSpellCheckResponse();
			if (spellCheckResponse != null) {
				suggestionList = spellCheckResponse.getSuggestions();
			}
			
		} catch (Exception e) {
			String params = String.format("(%s, %s)", searchText, maxResults);
			m_log.error("SuggesterSearch failed. Params  = " + params, e);
		}
		return suggestionList;
	}
	
	
	public SystemConfig getSystemConfig() {
		return systemConfigDAO.getDefaultSystemConfig();
		
	}
	
	public String getSolrServerUrl() {
		return this.solrConfig.getQueryURL();
	}
}
