package com.sa.assist.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.pcg.db.mongo.dao.CategoryDAO;
import com.pcg.db.mongo.dao.RecommendedLinkDAO;
import com.pcg.db.mongo.dao.SAAddressDAO;
import com.pcg.db.mongo.dao.SearchLoggerDAO;
import com.pcg.db.mongo.dao.SystemConfigDAO;
import com.pcg.db.mongo.model.Category;
import com.pcg.db.mongo.model.RecommendedLink;
import com.pcg.db.mongo.model.SAAddress;
import com.pcg.db.mongo.model.SAAddress.Coverage;
import com.pcg.db.mongo.model.SystemConfig;
import com.sa.assist.controller.bean.SmartSuggestCategory;
import com.sa.assist.service.MongoUIDaoService;
import com.sa.assist.tree.TreeNode;

import uk.me.jstott.jcoord.LatLng;

@Component
public class SearchServices {

	@Autowired
	private ISearchProvider searchProvider;
	
	@Autowired
	private SolrSmartSuggester smartSuggester;
	
	@Autowired private CategoryDAO categoryDAO;
//	@Autowired private UserToAddressDAO userToAddressDAO;
	@Autowired private SearchLoggerDAO searchLoggerDAO;
//	@Autowired private UserDAO userDAO;
	@Autowired private SAAddressDAO addressMasterUKDAO;
	@Autowired private SystemConfigDAO systemConfigDAO;
//	@Autowired private ContextDAO contextDAO;
	@Autowired private MongoUIDaoService mongoUIDaoService;
	@Autowired private RecommendedLinkDAO recommendedLinkDAO;
	
	private static Log m_log = LogFactory.getLog(SearchServices.class);
	
	private enum SortOrder {
        ASCENDING,
        DESCENDING
    }
    
    private enum SortType {
        RELEVANCY,
        PRICE,
        SUPPLIER,
        RATING
    }
    
    private SystemConfig getSystemConfig() {
		return systemConfigDAO.getDefaultSystemConfig();
		
	}
    
    private SearchType getDefaultSearchType() {
    	SearchType defaultSearchType = SearchType.WEBSITE;
    	
       	SystemConfig sc =  getSystemConfig();
    	String dst = "";//sc.getDefaultSearchType();
        
    	if (!StringUtils.isBlank(dst)) {
    		SearchType st = SearchType.findBySearchParam(dst);
    		if (st != null) {
    			return st;
    		}
    	}
    	
    	return defaultSearchType;
    }
    
    // new Search
    public void doSearch(HttpServletRequest request, ModelMap modelMap) {
    	SearchInfo searchInfo = (SearchInfo)request.getSession().getAttribute("searchInfo");
    	
    	performSearch(request, modelMap, searchInfo, null, false, true);
	}
    
    public void doSearch(HttpServletRequest request, ModelMap modelMap, SearchInfo searchInfo) {
    	
    	performSearch(request, modelMap, searchInfo, null, false, true);
	}
	
	public void doVendorSearch(HttpServletRequest request, ModelMap modelMap, Long vendorId, boolean vendorSearch) {
		SearchInfo searchInfo = (SearchInfo)request.getSession().getAttribute("searchInfo");
		
		performSearch(request, modelMap, searchInfo, vendorId, vendorSearch, false);
	}
	
	public List<SmartSuggestCategory> doSmartSuggestSearch(HttpServletRequest request, ModelMap modelMap) {
		return smartSuggester.getSmartSuggestions(request, modelMap);
	}
	public void doSmartSuggestSearch(HttpServletRequest request, ModelMap modelMap, SearchInfo searchInfo) {
				
		performSearch(request, modelMap, searchInfo, null, false, false);
	}
	
	public SearchResult performSearch(HttpServletRequest request, ModelMap modelMap, SearchInfo searchInfo, Long vendorId, boolean vendorSearch, boolean logSearch) {
		SearchResult result;
		
		
		if (searchInfo != null) { 
			String searchText = searchInfo.getSearchText();
			
			boolean atoz = false;
			// atoz
			if (StringUtils.isNotBlank(searchInfo.getA2zText())) {
				atoz = true;
				searchText = searchInfo.getA2zText();
			}
				
			// sort
	    	String sortOption = searchInfo.getSortOption();
	    	
	    	// rand sort
	    	String randomSortId = searchInfo.getRandomSortId();
	    	
	    	// pagination
	    	int pageNum = 1;
	    	if (searchInfo.getPage() != null && searchInfo.getPage() > 0) {
	    		pageNum = searchInfo.getPage();
	    	}
			
			int pSize = 10;
			if (searchInfo.getPageSize() != null) {
				pSize = searchInfo.getPageSize();
			}
				
			// Geo
			String rad = searchInfo.getRadius();
			if (StringUtils.isBlank(rad)) {
				rad = searchInfo.getDefaultRadius();
			}
	    	String searchPC = searchInfo.getPc();

	    	List<Long> needList = new ArrayList<Long>();
			//
			//Grab user needs if available
			//
//			User user = null;
//			if (saUser != null){
//				// check to see if we are a carer etc
//				Long managedUserId = saUser.getManagedUserId();
//				if (managedUserId != null) {
//					user = userDAO.findOne(managedUserId);
//				}
//				else {
//					user = userDAO.findOne(saUser.getId());
//				}
//			}
//			
//			if (user != null && saUser.getNeedFilter()) {
//				
//				
//				// needs
//				List<UserToNeed> userToNeeds = user.getUserNeedList();
//				for (UserToNeed need : userToNeeds) {
//					Long needlevelId = need.getNeedLevelId();
//					
//					if (needlevelId > 1L) {
//						needList.add(need.getCategoryId());
//					}
//				}
//				
//				// conditions
//				List<Long> conditions = user.getUserConditionList();
//				for (Long c : conditions) {
//					needList.add(c);
//				}
//				
//				// if need list is empty then return
//				if (needList.size() == 0) {
//					result = new SearchResult();
//					modelMap.put("searchResult", result);
//					return result;
//				}		
//			}
	    	
	    	//
			//Geo search. If radius search value is specified then we need to find out the location of the user's address 
			//
			String latitude = null;
			String longitude = null;
			Float radius = null;
			
			
			if (!StringUtils.isEmpty(rad)) {
				try {
					radius = Float.valueOf(rad);
					radius = 1.609344f * radius;// convert to Km  		
				
					if (!StringUtils.isBlank(searchPC)) {
						List<SAAddress> listAddresses = addressMasterUKDAO.findByPostcode(searchPC, Coverage.UK);
						if (listAddresses != null && listAddresses.size() > 0 ) {
							// just use the first?
							SAAddress addressmaster = listAddresses.get(0);
							latitude = Double.toString(addressmaster.getLatitude());
							longitude =  Double.toString(addressmaster.getLongtitude());
						} else {
							LatLng latLng = addressMasterUKDAO.findLatLngByPostcode(searchPC, Coverage.UK);
							if (latLng != null) {
			    	        	Double lat = latLng.getLatitude();
			    	          	Double lng = latLng.getLongitude();
			    	       
			    				latitude = Double.toString(lat);
								longitude =  Double.toString(lng);
			    	        }
						}
					}
				}
				catch (NumberFormatException e){
					m_log.error("Invalid search radius : " + e.getMessage());
				}
			}
			
			// managed / registered providers
			boolean managedVendorsOnly = searchInfo.isManagedVendorsOnly();
			boolean registeredVendorsOnly = searchInfo.isRegisteredVendorsOnly(); 
			
			// context
			List<Long> contextList = null;
			if (vendorSearch == false) {
				contextList = searchInfo.getContexts();
			} 
			
			// facets
			List<String> typeFacetList = searchInfo.getFilterTypes();
			List<Long> categoryFacetList = searchInfo.getCategories();
			//List<Long> conditionFacetList = searchInfo.getConditions();
			
			// commissionedOnly
			boolean commissionedOnly = searchInfo.isCommissionedOnly();
	
			// kitemarkOnly
			boolean kitemarkOnly = searchInfo.isKitemarkOnly();
				
			// translate Facet list
			List<FacetSearchType> facetSearchTypeList = new ArrayList<FacetSearchType>();
			for (String typeFacet : typeFacetList) {
				FacetSearchType facetSearchType = FacetSearchType.fromName(typeFacet);
				if (facetSearchType == null){
					List<FacetSearchType> facetSearchTypes = FacetSearchType.fromType(typeFacet);
			   		if (facetSearchTypes != null && facetSearchTypes.size() >0) {
			   			facetSearchTypeList.addAll(facetSearchTypes);
			   		}else{			   			
			   			facetSearchTypeList.add(FacetSearchType.fromSubType(typeFacet));
			   		}
				}else{
					facetSearchTypeList.add(facetSearchType);
				}
			}
			
			//
			//Search
			//
			result = searchProvider.getSearchResults(searchText, pageNum, pSize, sortOption, vendorId, vendorSearch, atoz, 
					facetSearchTypeList, categoryFacetList, needList, 
					commissionedOnly,  kitemarkOnly, managedVendorsOnly,registeredVendorsOnly, contextList, 
					searchPC, latitude, longitude, radius, randomSortId, searchInfo.isSmartSuggest(),
					searchInfo.isApplySmartSuggestFilter(), searchInfo.getFromDate(), searchInfo.getToDate(), searchInfo.isBedSearch(), searchInfo.isCategoryOrSearchOperator());
			
			if (result.getRandomSortID() != null){
				searchInfo.setRandomSortId(result.getRandomSortID());
				//request.getSession().setAttribute("randSort", result.getRandomSortID());
			}
			
			// put result in scope
			if (modelMap != null) {
				modelMap.put("searchResult", result);
			}
			
			// log the search
			if (logSearch == true) {
				logSearch(request, searchText, result);
			}
			
			//Get recommended links
			setRecommendedLinks(result, pageNum, searchText);
			
	     
		
			if (modelMap != null) {
				// create facets
				createFacets(result, searchInfo, modelMap);

				
				if (vendorId != null) {
					StringBuffer params = new StringBuffer("?vid=" + vendorId);
					if (vendorSearch == true) {
						modelMap.put("searchPagLink", "/vendorproducts/doPaginationSearch" + params.toString());
					} else {
						List<String> types = searchInfo.getFilterTypes();
			        	String filterType = "services";
						if (types != null && types.size() == 1) {
			        		filterType = types.get(0).toLowerCase();
			        	}
						modelMap.put("searchPagLink", "/cat/vendor/" + filterType + params.toString());
					}
				}
				else {
					modelMap.put("searchPagLink", "/api/cms/search/doPaginationSearch");
				}
				modelMap.put("searchText", searchText);
			}
			
		} else 	{
			result = new SearchResult();
			if (modelMap != null) {
				modelMap.put("searchResult", result);
			}
		}
		
		return result;
	}

	
	public void createFacets(SearchResult result, SearchInfo searchInfo, ModelMap modelMap) {
		SystemConfig sc =  getSystemConfig();
		
		List<String> typeFacetList = searchInfo.getFilterTypes();
		List<Long> categoryFacetList = searchInfo.getCategories();
		//List<Long> conditionFacetList = searchInfo.getConditions();
	
		
		List<TreeNode> searchTypeFacetsList = new ArrayList<TreeNode>();	     
		
		Map<String, Long> searchTypeFacets = result.getSearchTypeFacets();
		for (Map.Entry<String, Long> entry : searchTypeFacets.entrySet()) {
			TreeNode tn = new TreeNode();
			String type = entry.getKey();
			
//			// is this type enabled in the system config or cms content
			boolean enabled = true;
//			DirectoryInfo directoryInfo = sc.getDirectoryInfo(type);
//			
//			if (directoryInfo != null && directoryInfo.isEnabled() == false) {
//				enabled = false;
//			}
			
			//JIRA-HER-189 - Disable events filter if events Module is disabled
			boolean eventsEnabled = false;
//			if(sc.getModules().isEventsEnabled()){
//				eventsEnabled = true;
//				
//			}
			
			if (enabled) {
				if (!type.equalsIgnoreCase(FacetSearchType.PROVIDER.getType())) {
					if(type.equalsIgnoreCase(FacetSearchType.EVENT.getType()) && !eventsEnabled){
						continue;
					}
					List<FacetSearchType> facetSearchTypes = FacetSearchType.fromType(type);
			   		if (facetSearchTypes != null && facetSearchTypes.size() == 1) {
			   			FacetSearchType facetType = facetSearchTypes.get(0);
			   			String name = "";
//			   			if (directoryInfo != null) {
//			   				name = directoryInfo.getName();
//			   			}
			   			if (StringUtils.isBlank(name)) {
			   				name = facetType.getName();
			   			}
			   			tn.setName(name);
						tn.setResultsFound(entry.getValue());
						tn.setId(facetType.getId());
						tn.setDescription(name);
						
						// set selected state
						if (typeFacetList != null) {
							for (String selectedFacet : typeFacetList) {
								if (facetType.getName().equalsIgnoreCase(selectedFacet) || facetType.getType().equalsIgnoreCase(selectedFacet)) {
									tn.setSelected(true);
									break;
								}
							}
						
							if (searchInfo != null) {
								
								String json = searchInfo.toJson();
								SearchInfo searchInfoTn  = SearchInfo.fromJsonToAttribute(json);
								searchInfoTn.setPage(0);
								searchInfoTn.setDefaultRadius(null);
								List<String> types = searchInfoTn.getFilterTypes();
								
								if (tn.getSelected() == true) {
									types.remove(facetType.getName());
								} else {
									types.add(facetType.getName());
								}
								
								byte[]   bytesEncoded = Base64.encodeBase64(searchInfoTn.toJson().getBytes());
								String encoded = new String(bytesEncoded);
								tn.setSearchLinkUrl(encoded);
								
							}
						}
						
						
						searchTypeFacetsList.add(tn);
					}
				}
			}
		}
	
//		Map<Long, Long> vendorTypeFacets = result.getVendorTypeFacets();
//		for (Map.Entry<Long, Long> entry : vendorTypeFacets.entrySet()) {
//			TreeNode tn = new TreeNode();
//			Long id = entry.getKey();
//			VendorType vendorType = VendorType.findById(id);
//			if (vendorType != null) {
//				
//				// is this type enabled in the system config or cms content
//				boolean enabled = true;
//				DirectoryInfo directoryInfo = sc.getDirectoryInfo(vendorType.getDescription());
//				
//				if (directoryInfo != null && directoryInfo.isEnabled() == false) {
//					enabled = false;
//				}
//				if (enabled) {
//					FacetSearchType facetType = FacetSearchType.fromSubType(vendorType.getDescription());
//					if (facetType != null) {
//						String name = "";
//			   			if (directoryInfo != null) {
//			   				name = directoryInfo.getName();
//			   			}
//			   			if (StringUtils.isBlank(name)) {
//			   				name = facetType.getName();
//			   			}
//			   			tn.setName(name);
//						tn.setResultsFound(entry.getValue());
//						tn.setId(facetType.getId());
//						tn.setDescription(name);
//						
//						// set selected state
//						if (typeFacetList != null) {
//							for (String selectedFacet : typeFacetList) {
//								if (facetType.getName().equalsIgnoreCase(selectedFacet) || facetType.getType().equalsIgnoreCase(selectedFacet)) {
//									tn.setSelected(true);
//									break;
//								}
//							}
//							
//							if (searchInfo != null) {
//								
//								String json = searchInfo.toJson();
//								SearchInfo searchInfoTn  = SearchInfo.fromJsonToAttribute(json);
//								searchInfoTn.setPage(0);
//								searchInfoTn.setDefaultRadius(null);
//								List<String> types = searchInfoTn.getFilterTypes();
//								
//								if (tn.getSelected() == true) {
//									types.remove(facetType.getName());
//								} else {
//									types.add(facetType.getName());
//								}
//								
//								byte[]   bytesEncoded = Base64.encodeBase64(searchInfoTn.toJson().getBytes());
//								String encoded = new String(bytesEncoded);
//								tn.setSearchLinkUrl(encoded);
//								
//							}
//						}
//						searchTypeFacetsList.add(tn);
//					}
//				}
//			}
//		}
	
		Comparator<TreeNode> cp = TreeNode.getComparator(TreeNode.SortParameter.KEYPROPERTY, TreeNode.SortParameter.NAME_ASCENDING);
		Collections.sort(searchTypeFacetsList, cp);
		modelMap.put("typeList", searchTypeFacetsList);
	
	 	List<TreeNode> categoryList = null;
		List<TreeNode> conditionList = null;
			
		
//		categoryList = SASpringContext.getTreeNodeManager().getFacetedCategoryList(result.getCategoryFacets());
//		cp = TreeNode.getComparator(TreeNode.SortParameter.ROOT_DISPLAYORDER, TreeNode.SortParameter.ROOTNAME_ASCENDING, TreeNode.SortParameter.KEYPROPERTY,
//				TreeNode.SortParameter.NAME_ASCENDING);
//		Collections.sort(categoryList, cp);
//		// set selected state
//		// may be this be done in the above method?
//		if (categoryFacetList != null) {
//			setSelectedTreeNode(categoryList, categoryFacetList, searchInfo);
//		}
//		
//		modelMap.put("categoryList", categoryList);
		
		// commissioned Only
		boolean commissiondOnly = searchInfo.isCommissionedOnly();
		TreeNode tnCommisionedOnly = new TreeNode();
		tnCommisionedOnly.setName("Commissioned only");
		//tn.setResultsFound(entry.getValue());
		tnCommisionedOnly.setId(1L);
		tnCommisionedOnly.setDescription("Commissioned only");
		// set selected state
		if (commissiondOnly == true) {
			tnCommisionedOnly.setSelected(true);
		}		
		
		
		// kitemark Only
		boolean kitemarkOnly = searchInfo.isKitemarkOnly();
		TreeNode tnKitemarkOnly = new TreeNode();
		tnKitemarkOnly.setName("kitemark only");
		//tn.setResultsFound(entry.getValue());
		tnKitemarkOnly.setId(1L);
		tnKitemarkOnly.setDescription("Kitemark only");
		// set selected state
		if (kitemarkOnly == true) {
			tnKitemarkOnly.setSelected(true);
		}		
		
	
		
		if (searchInfo != null) {
			
			String json = searchInfo.toJson();
			SearchInfo searchInfoCommissionedTn  = SearchInfo.fromJsonToAttribute(json);
			searchInfoCommissionedTn.setPage(0);
			searchInfoCommissionedTn.setDefaultRadius(null);
			
			if (tnCommisionedOnly.getSelected() == true) {
				searchInfoCommissionedTn.setCommissionedOnly(false);
			} else {
				searchInfoCommissionedTn.setCommissionedOnly(true);
			}
			byte[]   bytesEncoded = Base64.encodeBase64(searchInfoCommissionedTn.toJson().getBytes());
			String encoded = new String(bytesEncoded);
			tnCommisionedOnly.setSearchLinkUrl(encoded);
			
			SearchInfo searchInfoKitemarkTn  = SearchInfo.fromJsonToAttribute(json);
			searchInfoKitemarkTn.setPage(0);
			searchInfoKitemarkTn.setDefaultRadius(null);
			
			if (tnKitemarkOnly.getSelected() == true) {
				searchInfoKitemarkTn.setKitemarkOnly(false);
			} else {
				searchInfoKitemarkTn.setKitemarkOnly(true);
			}
			
			bytesEncoded = Base64.encodeBase64(searchInfoKitemarkTn.toJson().getBytes());
			encoded = new String(bytesEncoded);
			tnKitemarkOnly.setSearchLinkUrl(encoded);			
		}
		
		modelMap.put("commisionedOnly", tnCommisionedOnly);
		modelMap.put("kitemarkOnly", tnKitemarkOnly);
		
	}
	
	private void setSelectedTreeNode(List<TreeNode> treeNodes, List<Long> selectedIds, SearchInfo searchInfo) {
		for (TreeNode treeNode: treeNodes) {
			List<TreeNode> children = treeNode.getChildren();
			if(children != null && children.size() > 0) {
				setSelectedTreeNode(children, selectedIds, searchInfo);
			}
			
			Long id = treeNode.getId();
			for (Long selectedId : selectedIds) {
				if (selectedId != null) {
					if (selectedId.equals(id)) {
						treeNode.setSelected(true);
						break;
					}
				}
			}
			
			// set facetable
			Category category = categoryDAO.findOne(id);
			if (category != null) {
				treeNode.setFacetable(category.isFacetable());
			}
			
			if (searchInfo != null) {
				
				String json = searchInfo.toJson();
				SearchInfo searchInfoTn  = SearchInfo.fromJsonToAttribute(json);
				searchInfoTn.setPage(0);

				List<Long> ids =  searchInfoTn.getCategories();
				
				if (treeNode.getSelected() == true) {
					ids.remove(id);
				} else {
					ids.add(id);
				}
				
				byte[]   bytesEncoded = Base64.encodeBase64(searchInfoTn.toJson().getBytes());
				String encoded = new String(bytesEncoded);
				treeNode.setSearchLinkUrl(encoded);
				
			}
		}
	}
	
	//
	//Log the search to the db for reporting purposes
	//We only log the initial search, not when the user is browsing or 
	//refining the search results via the category tree  
	//
	private void logSearch(HttpServletRequest request, String searchText, SearchResult result ) {
			if (StringUtils.isNotBlank(searchText) && StringUtils.isEmpty(request.getParameter("samonitor"))){
			Long _web = 0L;
			Long _user = 0L;
			Long _carer = 0L;
			Long _broker = 0L;
			
//			if (saUser==null)
				_web = 1L;
//			else {
//				if (request.isUserInRole("ROLE_CARER"))
//					_carer = 1L;
//				else if (request.isUserInRole("ROLE_BROKER"))
//					_broker = 1L;
//				else if (request.isUserInRole("ROLE_ADMIN"))
//					_web = 1L;
//				else if (request.isUserInRole("ROLE_VENDOR"))
//					_web = 1L;
//				else _user = 1L;
//				
//			}
			
			searchLoggerDAO.logSearch(searchText, "Website", new Integer(result.getTotalResults()).longValue(), _web, _user, _carer, _broker);
		}
	}
	
    // end of new search
	
	// CMS search
    public void searchForProductList(List<Long> productIDs, Long pType, ModelMap modelMap) {
		String searchPC = null;//(saUser == null? null : saUser.getSearchPC());
		SearchResult result = searchProvider.getProductSearchResults(productIDs, pType, searchPC);
		modelMap.put("productSearchResult", result);
	}
	
    
    public SearchResult searchForRelatedItems(String searchText, String searchCategory, Integer noOfItems) {
		return searchProvider.getRelatedItems(searchText, searchCategory, noOfItems);
	}

    /**
	  * Used by the CMS API to retrieve related content/products and services/vendors
	  * 
	  * @param request
	  * @param modelMap
	  * @param vendorId
	  * @param vendorSearch
	  * @param saUser
	  * @param categories
	  * @param conditions
	  * @param searchType
	 */
    public void searchForRelatedContent(HttpServletRequest request, ModelMap modelMap, 
    		Long vendorId, boolean vendorSearch, Long[] categories,
    		String filterTypes, boolean managedVendorsOnly, boolean registeredVendorsOnly) {
		
 		SearchInfo searchInfo  = new SearchInfo();
 		
 		if (StringUtils.isNotBlank(filterTypes)) {
			List<String> items = Arrays.asList(filterTypes.split("\\s*,\\s*"));
			searchInfo.setFilterTypes(items);
		}
 		if (categories != null){ 
 			searchInfo.setCategories(Arrays.asList(categories));
 		}

	 	
 		searchInfo.setManagedVendorsOnly(managedVendorsOnly);
 		searchInfo.setRegisteredVendorsOnly(registeredVendorsOnly);
 		searchInfo.setCategoryOrSearchOperator(true);
 		
 		performSearch(request, modelMap, searchInfo, vendorId, vendorSearch, false);
		 	    	
		// Remove facet lists from request scope
		modelMap.remove("categoryList");
		modelMap.remove("conditionList");
	}
	  
 	
	 public SearchResult searchForSelectedRelatedItems(String productIDs) {
		return searchProvider.getSelectedRelatedItems(productIDs);
	 }
	 
	 private void setRecommendedLinks(SearchResult result, int pageNum, String searchText) {
		if (searchText != null && pageNum <= 1) {

			List<DisplayItem> recommendedLinkResults  = new ArrayList<DisplayItem>();
			Set<String> recommendedURLs = new HashSet<String>(); 

			searchText = searchText.trim();
			List<RecommendedLink> recommendedLinks = recommendedLinkDAO.findByKeyword(searchText);
			
			if (recommendedLinks != null && recommendedLinks.size() > 0) {
				
				for (RecommendedLink link : recommendedLinks) {
					
					String keyWord = link.getKeyword();
					
					String[] phrases = StringUtils.split(keyWord, ",");
					
					boolean match = false;
					for (String phrase : phrases) {
						if ((phrase.trim().startsWith("\"") && phrase.trim().endsWith("\"")) || 
								(phrase.trim().startsWith("'") && phrase.trim().endsWith("'"))) {
							//Quoted keyword phrase - only use this link if the whole phrase is matched
							//Remove spaces as these aren't considered relevant for matching
							
							String phraseTrim = phrase.trim();
							String queryText = "";
							if (phraseTrim.startsWith("\"")) {
								queryText = StringUtils.removeStart(phraseTrim, "\"");  
								queryText = StringUtils.removeEnd(queryText, "\"");
							} else {
								queryText = StringUtils.removeStart(phraseTrim, "'");  
								queryText = StringUtils.removeEnd(queryText, "'");
							}
							
							queryText = StringUtils.remove(queryText, " ");
							
							if (!queryText.equalsIgnoreCase(StringUtils.remove(searchText, " "))){
								//Not a phrase match - don't recommend this link
								continue;
							} else {
								match = true;
							}
						}	else {
							// 
							Pattern p = Pattern.compile(searchText, Pattern.CASE_INSENSITIVE);
						    Matcher m = p.matcher(phrase);
						    if (m.find() == true) {
						    	match = true;
							}
						}
					}
					
					if (match == false) {
						continue;
					}
					
					String linkUrl = link.getUrl();
					
					if (StringUtils.isNotBlank(linkUrl) && !linkUrl.startsWith("http")) {
						String defaultDomain = "";//systemConfigDAO.getDefaultSystemConfig().getWebsiteDomain();
						linkUrl = defaultDomain + (linkUrl.startsWith("/")? "" : "/") + linkUrl;
					}
					
					recommendedURLs.add(linkUrl);//use this list to prevent recommended links turning up in the main result set as well

					StandardProduct di = new StandardProduct();
					
					di.setPageUrl(link.getUrl());
					di.setTitle(link.getTitle());
					di.setRecommendedLinkText(link.getDescription());
					di.setRecommendedLink(true);
					di.setType("RecLink");
					
					recommendedLinkResults.add(di);
				
					// update total results
					//int tr = result.getTotalResults() + 1;
					//result.setTotalResults(tr);
					
				}

			}
		
			//Add recommended links
			result.setRecommendedLinks(recommendedLinkResults);
			
			//remove standard result if recommended link is found
			List<DisplayItem> newResultList = new ArrayList<DisplayItem>();
			List<DisplayItem> resultList = result.getProducts();
			if (resultList != null){
				for (DisplayItem displayItem : resultList) {
					if (!recommendedURLs.contains(displayItem.getPageUrl())) {
						newResultList.add(displayItem);
					}
				}
			}
			result.setProducts(newResultList);
		}
	}
	 
	 // end of cms search
    
	 /**
    * Get category link for display in the search results    
    * @param vendorMode 
    * @param searchPC 
    * @param searchText 
    * @param searchType.getSearchParam() 
    * @param directoryvendor 
    */
   public String getCategoryNavLinks(HttpServletRequest request, FacetSearchType searchCategory){
   	
	   	String links = null;
	   	StringBuilder linkBuff = new StringBuilder();
	   	
	   	SystemConfig sc =  getSystemConfig();
	   	String context = "";//sc.getContextRoot();
   
		linkBuff.append("<li><a href=\"/\">Home</a><span class=\"divider\">></span></li>");
	
		linkBuff.append("<li><a href=\"/").append(context).append("/api/cms/search/doMenuSearch?ft="+searchCategory.getType()).append("\">"+searchCategory.getName()+"</a><span class=\"divider\">></span></li>");
	
		links = linkBuff.toString();
		return links;
   }
	 
//    public void doSearch(HttpServletRequest request, ModelMap modelMap, SaUser user) {
//		doSearch(request, modelMap, null, false, user);
//	}
//	
//	public void doSearch(HttpServletRequest request, ModelMap modelMap, SaUser user, SearchFacetForm searchFacetForm) {
//		doSearch(request, modelMap, null, false, user, searchFacetForm);
//	}
//	
//	public void doSearch(HttpServletRequest request, ModelMap modelMap, Long vendorId, boolean vendorSearch, SaUser saUser) {
//		doSearch(request, modelMap, vendorId, vendorSearch, saUser, null);
//	}
//	
//	public void doSearch(HttpServletRequest request, ModelMap modelMap, Long vendorId, boolean vendorSearch, SaUser saUser, SearchFacetForm searchFacetForm) {
//		doSearch(request, modelMap, vendorId, vendorSearch, saUser, searchFacetForm, null);
//	}
//
//	public void doSearch(HttpServletRequest request, ModelMap modelMap, Long vendorId, boolean vendorSearch, SaUser saUser, SearchFacetForm searchFacetForm, SearchType searchTypeOverride) {
//		//
//		//Get params
//		//
//		String searchText = request.getParameter("q");
//		String a2zText = request.getParameter("atoz");
//		
//		if (StringUtils.equals(searchText, "SEARCH")){
//			//eLog 9697 The search field is prepopulated with 'SEARCH' if empty
//			//So treat this like a blank search
//			searchText = "";
//		}
//		
//		
//		String type = request.getParameter("type");
//		if (StringUtils.isBlank(type)) {
//			type = (String)modelMap.get("type");
//		}
//
//		String st = type;
//		DirectoryInfo directoryInfo = null;
//		SystemConfig sc = getSystemConfig();
//		List<DirectoryInfo> directoryInfos = sc.getDirectoryInfos();
//		for (DirectoryInfo di : directoryInfos) {
//			if (di.getType().equalsIgnoreCase(type)) {
//				directoryInfo = di;
//				break;
//			}
//		}
//		if (directoryInfo != null) {
//			String dst = directoryInfo.getSearchType();
//			if (!StringUtils.isBlank(dst)) {
//				st = dst;
//			}
//		}
//		
//		SearchType searchType;
//		
//		if (searchFacetForm != null) {
//			String[] types = searchFacetForm.getTypes();
//			if (types != null) {
//				StringBuffer sb  = new StringBuffer();
//				
//				for (String t : types) {
//					if (sb.length() > 0) {
//						sb.append(" OR ");
//					}
//					
//					if (t.equalsIgnoreCase("product")) {
//						sb.append("type:product");			
//					}
//					else if (t.equalsIgnoreCase("service")) {
//						sb.append("type:service");
//					}
//					else if (t.equalsIgnoreCase("productsservices")) {
//						sb.append("type:service OR type:product");
//					}
//					else if (t.equalsIgnoreCase("vendor")) {
//						sb.append("(type:vendor AND vendorapproved:true)");
//					}
//					else if (t.equalsIgnoreCase("CMS_CONTENT")) {
//						sb.append("type:CMS_CONTENT");
//					}
//					else if (t.equalsIgnoreCase("CMS_EVENT")) {
//						sb.append("type:CMS_EVENT");
//					}
//					else if (t.equalsIgnoreCase("CMS_FAQ")) {
//						sb.append("type:CMS_FAQ");
//					}	
//					else if (t.equalsIgnoreCase("CMS_NEWS")) {
//						sb.append("type:CMS_NEWS");
//					}
//					else if (t.equalsIgnoreCase("WEBSITE")) {
//						sb.append("( (type:vendor AND vendorapproved:true) OR type:service OR type:product OR type:CMS_*)");
//					}
//				
//				}
//				searchType = SearchType.RUNTIME;
//				searchType.setSearchFilter(sb.toString());	
//			}
//			else {
//				searchType = SearchType.findBySearchParam(st);
//				
//			}	
//		} else {
//			searchType = SearchType.findBySearchParam(st);
//		}
//		
//		
//		
//		if (searchType == null){
//			searchType = getDefaultSearchType();
//		}
//
//		if (searchTypeOverride!=null) {
//			searchType = searchTypeOverride;
//		}
//		
//			
//    	String searchCategory = request.getParameter("cat");
//    	String condition = request.getParameter("condition"); 
//    	String searchPage = request.getParameter("p");
//    	String pageSize = request.getParameter("ps");
//    	String sortOption = request.getParameter("sort");
//    	String rad = request.getParameter("rad");
//    	String searchPC = request.getParameter("pc");
//    	// search pc may not be in request!
//		// is it in the model map?
//    	if (StringUtils.isBlank(searchPC)) {
//    		searchPC = (String)modelMap.get("searchPC");
//    	}
//    	
//    	String browseMode = request.getParameter("browse");
//    	
//    	boolean commissioned = false;
//    	boolean siteMap = false;
//	
//    	// Get list of categories from facet form
//    	if (searchFacetForm!=null && searchFacetForm.getApplyFacet()) {
//    		
//    		//Apply sitemap flag (default is false)
//    		siteMap = searchFacetForm.isSiteMap();
//    		
//    		// Reset pageSize and searchPage
//    		if (searchFacetForm.isApiCall()) {
//    			if (searchFacetForm.isRandom()) {
//	    			// Used in front end to display random results in related panel
//	    			searchPage = "1";
//	        		pageSize = "30";
//	        		sortOption = "random_" + new Random().nextInt(10000);
//    			} else {
//    				// Used in the back end to show paginated related content in the gridview.
//    				searchPage = searchFacetForm.getPage().toString();
//	        		pageSize = "3000";
//    			}
//    		} else {
//    			if (searchFacetForm.getPage() != null) {
//    				searchPage = searchFacetForm.getPage().toString();
//    			} else {
//    				searchPage = null;
//    			}
//    			if (!searchFacetForm.isRandom()) {
//    				sortOption = null;
//    			}
//	    		
//	    		pageSize = null;
//    		}
//    		
//    		searchCategory = null;
//    		if (searchFacetForm.getCategories()!=null && searchFacetForm.getCategories().length > 0) {
//    			searchCategory = searchFacetForm.getCategories()[0].toString();
//    			for (int i = 1; i < searchFacetForm.getCategories().length; i++) {
//    				searchCategory += "," + searchFacetForm.getCategories()[i]; 
//    			}
//    		}
//    		condition = null;
//    		if (searchFacetForm.getConditions()!=null && searchFacetForm.getConditions().length > 0) {
//    			condition = searchFacetForm.getConditions()[0].toString();
//    			for (int i = 1; i < searchFacetForm.getConditions().length; i++) {
//    				condition += "," + searchFacetForm.getConditions()[i]; 
//    			}
//    		}
//    		
//        	commissioned = searchFacetForm.getCommissioned();
//       		
//    	} else {
//    		// Put search category into form
//    		if (searchCategory!=null) {
//    			String[] strArrCats = searchCategory.split(",");
//    			Integer[] intArrCats = new Integer[strArrCats.length];
//    			int cc = 0;
//    			for (String cat : strArrCats) {
//    				intArrCats[cc] = Integer.parseInt(cat);
//    				cc++;
//    			}
//    			searchFacetForm.setCategories(intArrCats);
//    		}
//    		if (condition!=null) {
//    			String[] strArrCats = condition.split(",");
//    			Integer[] intArrCats = new Integer[strArrCats.length];
//    			int cc = 0;
//    			for (String cat : strArrCats) {
//    				intArrCats[cc] = Integer.parseInt(cat);
//    				cc++;
//    			}
//    			searchFacetForm.setConditions(intArrCats);
//    		}    		
//    	}
//    	    	
//		SearchResult result = new SearchResult();
//		
//		List<Long> needList = new ArrayList<Long>();
//		//
//		//Grab user needs if available
//		//
//		User user = null;
//		if (saUser != null){
//			// check to see if we are a carer etc
//			Long managedUserId = saUser.getManagedUserId();
//			if (managedUserId != null) {
//				user = userDAO.findOne(managedUserId);
//			}
//			else {
//				user = userDAO.findOne(saUser.getId());
//			}
//		}
//		
//		if (user != null && saUser.getNeedFilter()) {
//			
//			if (searchType == SearchType.SERVICE || searchType == SearchType.PRODUCT) {
//				// needs
//				List<UserToNeed> userToNeeds = user.getUserNeedList();
//				for (UserToNeed need : userToNeeds) {
//					Long needlevelId = need.getNeedLevelId();
//					
//					if (needlevelId > 1L) {
//						needList.add(need.getCategoryId());
//					}
//				}
//				
//				// conditions
//				List<Long> conditions = user.getUserConditionList();
//				for (Long c : conditions) {
//					needList.add(c);
//				}
//				
//				// if need list is empty then return
//				if (needList.size() == 0) {
//					modelMap.put("searchResult", result);
//					modelMap.put("searchText", searchText);
//					modelMap.put("searchCategory", searchCategory);
//					modelMap.put("condition", condition);
//					return;
//				}		
//			}
//		}
//		
//		
//		
//		
//		//
//		//Geo search. If radius search value is specified then we need to find out the location of the user's address 
//		//
//		String latitude = null;
//		String longitude = null;
//		Float radius = null;
//		
//		if (StringUtils.isBlank(rad)) {
//			rad = (String)modelMap.get("radius");
//		}
//		
//
//		if (!StringUtils.isEmpty(rad)) {
//			try {
//				radius = Float.valueOf(rad);
//				radius = 1.609344f * radius;// convert to Km  		
//			
//				if (!StringUtils.isBlank(searchPC)) {
//					List<SAAddress> listAddresses = addressMasterUKDAO.findByPostcode(searchPC, Coverage.UK);
//					if (listAddresses != null && listAddresses.size() > 0 ) {
//						// just use the first?
//						SAAddress addressmaster = listAddresses.get(0);
//						latitude = Double.toString(addressmaster.getLatitude());
//						longitude =  Double.toString(addressmaster.getLongtitude());
//					}
//				}
////				else if (user != null) {
////		    		Address address = userToAddressDAO.findDefaultAddress(user.getId(), AddressType.CONTACT);
////			    	if (address != null) {
////				    	if (address.getLatitude() != null){
////				    		latitude = address.getLatitude().toString();
////				    	}
////				    	
////				    	if (address.getLongitude() != null){
////				    		longitude = address.getLongitude().toString();
////				    	}
////			    	}
////				} 
//			}
//			catch (NumberFormatException e){
//				m_log.error("Invalid search radius : " + e.getMessage());
//			}
//		}
//		
//		int pageNum = 1;
//
//		try {
//			pageNum = Integer.parseInt(searchPage);
//		} catch (Exception e) {
//			// Ignore and use 1 as the default
//		}
//
//		int pSize = 10;
//		try {
//			pSize = Integer.parseInt(pageSize);
//		} catch (Exception e) {
//			// Ignore and use 10 as the default
//		}
//		
//		boolean atoz = false;
//		if (a2zText != null) {
//			if (a2zText.equalsIgnoreCase("all")) {
//				searchText = null;
//			} else {
//				atoz = true;
//				searchText = a2zText;
//			}
//		}
//		
//		//
//		//Search
//		//
//		result = searchProvider.getSearchResults(searchText, searchType, searchCategory, pageNum, pSize, sortOption, vendorId, vendorSearch,
//				atoz, needList, latitude, longitude, radius, searchPC, (String)request.getSession().getAttribute("randSort"), condition, commissioned, siteMap);
//
//		
//		//
//		//Log the search to the db for reporting purposes
//		//We only log the initial search, not when the user is browsing or 
//		//refining the search results via the category tree  
//		//
//		if (StringUtils.isEmpty(searchCategory) && StringUtils.isEmpty(request.getParameter("samonitor"))){
//			Long _web = 0L;
//			Long _user = 0L;
//			Long _carer = 0L;
//			Long _broker = 0L;
//			
//			if (saUser==null)
//				_web = 1L;
//			else {
//				if (request.isUserInRole("ROLE_CARER"))
//					_carer = 1L;
//				else if (request.isUserInRole("ROLE_BROKER"))
//					_broker = 1L;
//				else if (request.isUserInRole("ROLE_ADMIN"))
//					_web = 1L;
//				else if (request.isUserInRole("ROLE_VENDOR"))
//					_web = 1L;
//				else _user = 1L;
//				
//			}
//			
//			//do not store blank searches
//			if (StringUtils.isNotBlank(searchText)) {
//				searchLoggerDAO.logSearch(searchText, searchType.toString(), new Integer(result.getTotalResults()).longValue(), _web, _user, _carer, _broker);
//			}
//		}
//		
//        //
//        //Make a cut-down results tree with the result facet counts
//        //&& !"provider".equalsIgnoreCase(searchType)
//		List<TreeNode> categoryList = null;
//		List<TreeNode> conditionList = null;
//		if (!searchType.equals(SearchType.CLASSIFIED) && browseMode == null) {
//
////			CategoryType catType;
////			
////			if (searchType.equals(SearchType.PRODUCT)){
////				catType = CategoryType.PRODUCT;
////			}
////			else if (searchType.equals(SearchType.CAREHOME)){
////				catType = CategoryType.CARE_HOME;
////			}
////			else if (searchType.equals(SearchType.PA)){
////				catType = CategoryType.PA;
////			}
////			else if (searchType.equals(SearchType.ADULT_PROVIDER)){
////				catType = CategoryType.ADULTS;
////			}
////			else if (searchType.equals(SearchType.CHILD_PROVIDER)){
////				catType = CategoryType.CHILDREN;
////			}
////			else if (searchType.equals(SearchType.CHILDCARE)){
////				catType = CategoryType.CHILDCARE;
////			}
////			else if (searchType.equals(SearchType.ALL)){
////				catType = CategoryType.NONE;
////			}
////			else {
////				catType = CategoryType.SERVICE;
////			}
//			
//			Map<Long, Long> categoryFacets = result.getCategoryFacets();
//			
//			categoryList = SASpringContext.getTreeNodeManager().getFacetedCategoryList(result.getCategoryFacets());
//
//			//TreeNode ct = SASpringContext.getTreeNodeManager().getCategoryRoot(catType);
//			//if (ct != null) {
//			//	categoryList = SASpringContext.getTreeNodeManager().getFacetedCategoryList(ct, result.getCategoryFacets());
//			//}
//			
//			
//			TreeNode tnCondition = SASpringContext.getTreeNodeManager().getCategoryRoot(CategoryType.CONDITION);
//			if (tnCondition != null) {
//				conditionList = SASpringContext.getTreeNodeManager().getFacetedCategoryList(tnCondition, result.getConditionFacets());
//			}
//		}
//		
//		// Browse mode
//		// Resolve child categories and breadcrumb path
//		if (browseMode!=null) {
//			
//			// Are we looking at a single category?
//			if (searchCategory!=null && searchCategory.split(",").length == 1) {
//				
//				Category currCat = categoryDAO.findOne(new Long(searchCategory));
//				if (currCat!=null) {
//					
//					List<Category> browseCrumbtrail = new ArrayList<Category>();
//					
//					browseCrumbtrail.add(0, currCat);
//					modelMap.put("browseCat", currCat);
//					
//					List<Category> childCategories = categoryDAO.getChildCategories(currCat);
//					modelMap.put("browseChildCategories", childCategories);
//					
//					while (currCat.getCategoryParentId()!=currCat.getCategoryRootId()) {
//						
//						currCat = categoryDAO.findOne(currCat.getCategoryParentId());
//						browseCrumbtrail.add(0, currCat);
//						
//					}
//					
//					modelMap.put("browseCrumbtrail", browseCrumbtrail);
//					
//				}
//				
//			}
//			
//		}
//		
//		//
//		//Put required details in scope for the view
//		//
//		boolean vendorMode = request.isUserInRole("ROLE_VENDOR");
//		boolean laMode = request.isUserInRole("ROLE_LOCAL_AUTHORITY");
//		Long directoryvendor = null;
//		if (modelMap.get("directoryvendor") != null) {
//			directoryvendor = (Long)modelMap.get("directoryvendor");
//		}
//		
//		if (result.getRandomSortID() != null){
//			request.getSession().setAttribute("randSort", result.getRandomSortID());
//		}
//		
//		boolean atozMode = false;
//		if (a2zText != null) {
//			atozMode = true;
//		}
//		
//		String bcLinks = getCategoryNavLinks(searchCategory, vendorMode, laMode, directoryvendor, atozMode, searchType, searchPC, searchText);
//		
//		
//		modelMap.put("searchResult", result);
//		modelMap.put("categoryList", categoryList);
//		modelMap.put("conditionList", conditionList);
//		modelMap.put("searchPagLink", getSearchPaginationLink(type, result, vendorMode, laMode, directoryvendor, atozMode));
//		modelMap.put("searchCatLink", getSearchCatLink(type, result, vendorMode, laMode, directoryvendor, atozMode));
//		modelMap.put("viewSwitchLink", getViewSwitchLink(type, result, vendorMode, laMode, directoryvendor, atozMode));
//		modelMap.put("typeSwitchLink", getTypeSwitchLink(type, result, vendorMode, laMode, directoryvendor, atozMode));
//		modelMap.put("catNavLinks", getCategoryNavLinks(searchCategory, vendorMode, laMode, directoryvendor, atozMode, searchType, searchPC, searchText));
//		modelMap.put("bcLinks", bcLinks);
//		modelMap.put("searchText", searchText);
//		modelMap.put("searchCategory", searchCategory);
//		modelMap.put("type", request.getParameter("type"));
//		
//		if (rad != null) {
//			modelMap.put("distance", rad);
//			
//		}
//	}	
//    
//
//    
//	
//    /**
//     * Returns Search URL params omitting page  
//     */
//    private String getSearchPaginationLink(String type, SearchResult sr, boolean vendorMode, boolean laMode, Long vendorid, boolean atozMode){
//    	return makeLink(type, sr, vendorMode, laMode, vendorid, atozMode, true, true, true, true, true);
//    }
//    
//    /**
//     * Returns Search URL params omitting category and page
//     * @param directoryvendor 
//     */
//    private String getSearchCatLink(String type, SearchResult sr, boolean vendorMode, boolean laMode, Long vendorid, boolean atozMode){
//    	return makeLink(type, sr, vendorMode, laMode, vendorid, atozMode, true, true, false, true, false);
//    }
//    
//    /**
//     * Returns Search URL params omitting pagesize & page 
//     * @param directoryvendor 
//     */
//    private String getViewSwitchLink(String type, SearchResult sr, boolean vendorMode, boolean laMode, Long vendorid, boolean atozMode){
//    	return makeLink(type, sr, vendorMode, laMode, vendorid, atozMode, false, true, true, true, false);
//    }
//    
//    /**
//     * Returns Search URL params omitting everything except the query string  
//     * @param directoryvendor 
//     */
//    private String getTypeSwitchLink(String type, SearchResult sr, boolean vendorMode, boolean laMode, Long vendorid, boolean atozMode){
//    	return makeLink(type, sr, vendorMode, laMode, vendorid, atozMode, false, false, false, false, false);
//    }
//    
//    private String makeLink(String searchType, SearchResult sr, boolean vendorMode, boolean laMode, Long vendorId, boolean atozMode, boolean pageSize, boolean sort, boolean category, boolean type, boolean pc){
//    	
//    	StringBuilder buff = new StringBuilder();
//    	
//    	if (vendorMode && "provider".equalsIgnoreCase(searchType) == false){
//    		buff.append("/vendorproducts/vendor?");
//    	} else if (laMode){
//    		buff.append("?");
//    	} else if (vendorId != null){
//    		buff.append(vendorId.toString()+"?");
//    	}
//    	else {
//    		buff.append("cat?");
//    	}
//    	
//    	if (atozMode) {
//    		appendParam(buff, "atoz", sr.getSearchText());
//    	}
//    	else 
//   		// eLog 9914 - pagination not working when searching for nothing
//    	// faked: query=SEARCH&toolbarsearch=t
//    	if(StringUtils.isEmpty(sr.getSearchText())){
//   			appendParam(buff, "q", "SEARCH");
//   			appendParam(buff, "toolbarsearch", "t");
//   		}else{
//   			appendParam(buff, "q", sr.getSearchText());
//   		}
//   		
//   		if (type){
//   			appendParam(buff, "type", searchType);
//   		}
//
//   		if (pageSize){
//   			appendParam(buff, "ps", Integer.toString(sr.getPageSize()));
//   		}
//   		
//   		if (sort){
//   			appendParam(buff, "sort", sr.getSortOption());
//   		}
//   		
//   		if (category){
//   			appendParam(buff, "cat", sr.getSearchCategory());
//   			appendParam(buff, "condition", sr.getSearchCondition());
//   		}
//   		
//   		if (pc){
//   			appendParam(buff, "pc", sr.getSearchPostcode());
//   		}
//   		
//   		
//    	return buff.toString();
//    }
//    
//    private void appendParam(StringBuilder buff, String name, String val){
//    	
//    	if (val == null){
//    		return;
//    	}
//
//    	if (buff.indexOf("?") < buff.length() - 1){
//    		buff.append("&");
//    	}
//
//    	buff.append(name);
//		buff.append("=");
//		buff.append(val);
//    }
    
    
  
	 
//    public SearchResult searchForRelatedPanelItems(String searchText, String searchType) {
//		return searchProvider.getRelatedPanelItems(searchText, searchType);
//	}
//    
//    public SearchResult searchForRelatedPanelMetaItems(String condition, String product, String service, String need) {
//		return searchProvider.getRelatedPanelMetaItems(condition, product, service, need);
//	}
//    
    public SearchResult searchForVendors(String vendorIds) {
  		return searchProvider.getVendorSearchResults(vendorIds);
  	}

    
	
//	
	public SearchResult searchForSelectedRelatedContent(String contentIDs) {
		return searchProvider.getSelectedRelatedContent(contentIDs);
	}
	
	
	
}
