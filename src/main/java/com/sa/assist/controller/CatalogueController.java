package com.sa.assist.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pcg.db.mongo.dao.AddressDAO;
import com.pcg.db.mongo.dao.CategoryDAO;
import com.pcg.db.mongo.dao.CategoryTypeDAO;
import com.pcg.db.mongo.dao.SAAddressDAO;
import com.pcg.db.mongo.dao.SequenceDAO;
import com.pcg.db.mongo.dao.SystemConfigDAO;
import com.pcg.db.mongo.model.Category;
import com.pcg.db.mongo.model.DirectoryInfo;
import com.pcg.db.mongo.model.SystemConfig;
import com.pcg.db.mongo.model.SAAddress.Coverage;
import com.sa.assist.search.FacetSearchType;
import com.sa.assist.search.SearchInfo;
import com.sa.assist.search.SearchResult;
import com.sa.assist.search.SearchServices;
import com.sa.assist.service.MongoUIDaoService;
import com.sa.assist.viewbean.CategoryUI;

@RequestMapping("/cat/**")
@Controller
public class CatalogueController {

	@Autowired private SearchServices searchService;

//	@Autowired private UserToAddressDAO userToAddressDAO;
//	@Autowired private VendorToAddressDAO vendorToAddressDAO;
//	@Autowired private SearchTrackerDAO searchTrackerDAO;

//	@Autowired private OrderHeaderDAO orderHeaderDAO;
	@Autowired private CategoryDAO categoryDAO;
//	@Autowired private VendorDAO vendorDAO;
//	@Autowired private ReviewSummaryDAO reviewSummaryDAO;
//	@Autowired private ProductDAO productDAO;
//	@Autowired private UserDAO userDAO;
//	@Autowired private VendorToAccreditationDAO vendorToAccreditationDAO;
//	@Autowired private VendorAttributesDAO vendorAttributesDAO;
//	@Autowired private ReportItemDAO reportItemDAO;
	@Autowired private SequenceDAO sequenceDAO;
	@Autowired private AddressDAO addressDAO;
	@Autowired private CategoryTypeDAO categoryTypeDAO;
	@Autowired private MongoUIDaoService mongoUIDaoService;
	@Autowired private SystemConfigDAO systemConfigDAO;
	@Autowired protected SAAddressDAO addressMasterUKDAO;
	
	private String[] allAlpha = {"0-9","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

	//private String section = "Marketplace";
	private String section = "Search Results";



//	/**
//	 * Sets a base page title for all pages within this controller
//	 */
//	
//	@ModelAttribute("_pT")
//	public String getPageTitle(HttpServletRequest request) {
//		String name = systemConfigDAO.getDefaultSystemConfig().getSiteName();
//		if (StringUtils.isEmpty(name)) return "Marketplace";
//		else return name;
//	}
//
//	@ModelAttribute("catalogue")
//	public boolean catCheck() {
//		return true;
//	}
//
//	@ModelAttribute("searchLink")
//	public String getSearchLink(HttpServletRequest request) {
//		String reqUrl = request.getRequestURL().toString(); 
//		String queryString = request.getQueryString(); 
//		if (queryString != null) {
//			reqUrl += "?" + queryString; 
//		}; 
//		return reqUrl;
//	}
//
//	@ModelAttribute("qs")
//	public String getQueryString(HttpServletRequest request) {
//		return request.getQueryString();
//	}

	private static Log m_log = LogFactory.getLog(CatalogueController.class);

	// New search code

	protected String search(ModelMap modelMap, HttpServletRequest request) {

		modelMap.put("newSearch", "true");
		    	
		String searchText = null;
		String searchPC = null;
		String filterType = null;
		String browseCategory = null;

		SearchInfo searchInfo = (SearchInfo)request.getSession().getAttribute("searchInfo");
		if (searchInfo == null) {
			searchInfo = new SearchInfo();
//			SearchHelper.addContextToSearchInfo(searchInfo, mongoUIDaoService);
			request.getSession().setAttribute("searchInfo", searchInfo);
		}

		Integer page = null;
		if (searchInfo != null) {
			searchText = searchInfo.getSearchText();
			searchPC = searchInfo.getPc();
			// not sure if we need separate parameter atoz reuse q?
			//a2zText = searchInfo.getA2zText();
			//searchType = searchInfo.getSearchType();
			List<String> types = searchInfo.getFilterTypes();
			if (types != null && types.size() == 1) {
				filterType = types.get(0);
			}

			page = searchInfo.getPage();
		}

//		//Display the appropriate category landing page
//		if(null != filterType ){
//			m_log.debug("FilterType selected:"+ filterType);
//			browseCategory = getBrowseCategory(filterType);
//		}


		// any cat filters selected
		boolean browseByCat = false;
		List<Long> selectedCategories = searchInfo.getCategories();
//		List<Long> selectedNeeds = searchInfo.getNeeds();
		boolean commissionedOnly = searchInfo.isCommissionedOnly();
		boolean kitemarkOnly = searchInfo.isKitemarkOnly();

//		if (selectedCategories.size() == 0 && selectedNeeds.size() == 0 
//				//    			&& StringUtils.isBlank(searchPC) 
//				&& commissionedOnly == false && kitemarkOnly == false 
//				&& (page == null || page == 0)) {
//			browseByCat = true;
//		}


		if (StringUtils.isBlank(searchText) && !StringUtils.isEmpty(browseCategory) 
				&& browseByCat == true) {

			Category root = categoryDAO.findOne(Long.parseLong(browseCategory));

			List<CategoryUI> browseCatList  = new ArrayList<CategoryUI>();

			List<Category> catList  = categoryDAO.getChildCategories(root);

			for (Category cat : catList) {
				CategoryUI categoryUI = new CategoryUI(cat, mongoUIDaoService);
				String json = searchInfo.toJson();
				SearchInfo searchInfoTn  = SearchInfo.fromJsonToAttribute(json);

				List<Long> ids = searchInfoTn.getCategories();
				ids.add(cat.getId());

				byte[]   bytesEncoded = Base64.encodeBase64(searchInfoTn.toJson().getBytes());
				String encoded = new String(bytesEncoded);
				categoryUI.setSearchLinkUrl(encoded);
				browseCatList.add(categoryUI);
			}

			modelMap.put("browseCatList", browseCatList);

//			SaUser saUser = null;

//			// get displayResultsPage config
//			// set to default
//			// maybe this should be set up depending on search type or for each result?
//			DirectoryInfo directoryInfo = DirectoryInfoHelper.getDirectoryInfo(mongoUIDaoService, null, false);
//			DisplayConfigUI displayConfigUI = setDisplayResultsPageConfig(modelMap);

			//Do the search and populate the modelMap with all the search-related display info 
			searchService.doSearch(request, modelMap);

			SearchResult result = (SearchResult)modelMap.get("searchResult");
//
//			boolean displayMap = getDefaultDisplayMap(directoryInfo);
//			if (displayMap) {
//				List<DisplayItem> products  = result.getProducts();
//				configureMapResults(modelMap, filterType, searchPC, products, displayConfigUI);
//			}
			modelMap.put("showNavDetail", "true");
			
//			String pageTitle = "pt_index_" + systemConfigDAO.getDefaultSystemConfig().getContextRoot().toLowerCase();	 
//			createDisplayBean(modelMap, section, pageTitle, Arrays.asList(filterType), request);
			

			return "cat/browse/index";	
		}
		else {
			boolean validPostcode = true;
			if (!StringUtils.isBlank(searchPC)) {

				String pcShort = searchPC.replaceAll(" ", "");

				// do we need to check if postcode is valid?
//				if (getDefaultSearchPostcodeValidate() == true) {
					validPostcode = addressMasterUKDAO.isPostcodeInLA(pcShort, Coverage.LOCALAUTHORITIES);
					if (!validPostcode) {
						modelMap.put("invalidPostcodeText", "IMVALID PC");
					}
//				}
			}

			if (validPostcode == true) {
				isDefaultSpatialSearch(searchInfo);
				
				DirectoryInfo directoryInfo = null;

				// not sure if used if we display all results
				//directoryInfo = DirectoryInfoHelper.getDirectoryInfo(mongoUIDaoService, "service");
				//if (directoryInfo != null && directoryInfo.isEnabled() == true) {
				//	modelMap.put("serviceEnabled", true);
				//}
				//directoryInfo = DirectoryInfoHelper.getDirectoryInfo(mongoUIDaoService, "product");
				//if (directoryInfo != null && directoryInfo.isEnabled() == true) {
				//	modelMap.put("productEnabled", true);
				//}

				// get displayResultsPage config
				// set to default
				// maybe this should be set up depending on search type or for each result?
//				directoryInfo = DirectoryInfoHelper.getDirectoryInfo(mongoUIDaoService, null, false);
//				DisplayConfigUI displayConfigUI = setDisplayResultsPageConfig(modelMap);

				//Do the search and populate the modelMap with all the search-related display info 
				searchService.doSearch(request, modelMap);

				SearchResult result = (SearchResult)modelMap.get("searchResult");

//				boolean displayMap = getDefaultDisplayMap(directoryInfo);
//				if (displayMap) {
//					List<DisplayItem> products  = result.getProducts();
//					configureMapResults(modelMap, filterType, searchPC, products, displayConfigUI);
//				}
//
//				if (getDisplayAToZ() == true) {
//					Map<String, Long> atozFacets = result.getAtozFacets();
//					configureAtoZ(modelMap, atozFacets);
//				}

				modelMap.put("showNavDetail", "true");

			}

			String pageTitle = "pt_catalogue_results";
			
//			createDisplayBean(modelMap, section, pageTitle, null, request);
			
			return "cat/results/list";
		}

	}
	
	private void isDefaultSpatialSearch(SearchInfo searchInfo) {
		if (searchInfo != null && StringUtils.isBlank(searchInfo.getRadius()) && StringUtils.isNotBlank(searchInfo.getPc())) {
			SystemConfig sc =  systemConfigDAO.getDefaultSystemConfig();
		
			int maxRadius = 0; 
			boolean applySpacialSearch = false;
//			String searchRadius = sc.getDefaultSearchRadius();
			String searchRadius = "10";
			if (StringUtils.isNotBlank(searchRadius)) {
				maxRadius = Integer.parseInt(searchRadius);
			}
			List<String> filterTypes = searchInfo.getFilterTypes();
			for (String directoryType : filterTypes) {
				FacetSearchType facetSearchType = FacetSearchType.fromName(directoryType);
				if (facetSearchType != null) {
					String type = facetSearchType.getSubType();
					if (StringUtils.isBlank(type)) {
						type = facetSearchType.getType();
					}
//					DirectoryInfo directoryInfo = DirectoryInfoHelper.getDirectoryInfo(sc, type, true);
//				
//					if (directoryInfo != null) {
//						if (directoryInfo.isSpatialSearch() == true) {
//							applySpacialSearch = true;
//							if (StringUtils.isNotBlank(directoryInfo.getDefaultRadius())) {
//								int radius = Integer.parseInt(directoryInfo.getDefaultRadius());
//								maxRadius = Math.max(maxRadius, radius);
//							}
//						} else {
//							applySpacialSearch = false;
//							break;
//						}
//					}
				}
			}
			if (applySpacialSearch == true && maxRadius > 0) {
				searchInfo.setDefaultRadius(Integer.toString(maxRadius));
			}
		}
	}
//
////	private void configureMapResults(ModelMap modelMap, String filterType, String postCode, List<DisplayItem> products, DisplayConfigUI displayConfigUI) {
////
////		LatLng latLng = null;
////		if (StringUtils.isNotBlank(postCode)) {
////			String pcShort = postCode.replaceAll(" ", "");
////			latLng = addressMasterUKDAO.findLatLngByPostcode(pcShort, Coverage.UK);
////		}
////
////		if (latLng != null) {
////			Double lat = latLng.getLatitude();
////			Double lng = latLng.getLongitude();
////			modelMap.put("lat", lat.toString());
////			modelMap.put("lng", lng.toString());
////			modelMap.put("zoom", getDefaultZoom(null)); 
////			modelMap.put("radius", getDefaultRadius(null)); 
////		} else {
////			modelMap.put("lat", getDefaultLatitude(filterType)); 
////			modelMap.put("lng", getDefaultLongitude(filterType)); 
////			modelMap.put("zoom", getDefaultZoom(filterType)); 
////			modelMap.put("radius", getDefaultRadius(filterType)); 
////		}
////
////		if (products != null) {
////			List<MapPin> mapPins = new ArrayList<MapPin>();
////			int index = 1;
////
////			for (DisplayItem di : products) {
////				if (di instanceof StandardProduct) {
////					StandardProduct sp = (StandardProduct)di;
////					Double lat = sp.getLatitude();
////					Double lng = sp.getLongtitude();
////					if (lat != null && lng != null) {
////						MapPin mapPin = new MapPin();
////						mapPin.setLatitude(lat.toString());
////						mapPin.setLongtitude(lng.toString());
////
////						String context = getSystemConfig().getId();
////						mapPin.setContext(context);
////
////						StringBuffer icon;
////						icon = new StringBuffer("/images/maps/pinburgandy.png"); 
////						icon.insert(icon.indexOf(".png"), index);
////						mapPin.setIcon(icon.toString());
////
////						mapPin.setTitle(sp.getTitle());
////						sp.setMapImagePath("/"+icon.toString());
////
////						mapPins.add(mapPin);
////
////						String type = sp.getType();
////						StringBuffer desc  = new StringBuffer();
////
////						desc.append("<ul class=\"links\">");
////						if ("vendor".equalsIgnoreCase(type)) {
////							desc.append("<li><a href=\"/"+ context + "/cat/vendor/" + sp.getId() +"\">View details</a></li>");
////						} else {
////							desc.append("<li><a href=\"/" + context + "/cat/product/" + sp.getId() +"\">View details</a></li>");
////						}
////
////						if (displayConfigUI!= null && displayConfigUI.isDisplayWebsite()) {
////							if (!StringUtils.isBlank(sp.getVendorWebsite())) {
////								desc.append("<li><a href=\"" + sp.getVendorWebsite() + "\">Visit website</a></li>");
////							}
////						}
////
////						if (displayConfigUI!= null && displayConfigUI.isDisplayEmail()) {
////							if (!StringUtils.isBlank(sp.getVendorEmail())) {
////								desc.append("<li><a href=\"mailto:" + sp.getVendorEmail() + "\">Email</a></li>");
////							}
////						}
////
////						if (displayConfigUI != null && displayConfigUI.isDisplayTelNo()) {
////							if (!StringUtils.isBlank(sp.getVendorTelephone())) {
////								desc.append("<li>Telephone: " + sp.getVendorTelephone() + "</li>");
////							}
////						}
////
////						desc.append("<ul>");   				
////
////						mapPin.setDescription(desc.toString());
////
////						index++;
////					}
////				}
////			}
////
////			if (mapPins.size() > 0) {
////				modelMap.put("showmap", true);
////			}
////
//////			if  (getDisplayDefaultMapHide() == true) {
//////				modelMap.put("hideMapResults", true);
//////			}
////
////			String jsonPins = MapPin.toJsonArray(mapPins);
////			modelMap.put("jsonPins", jsonPins);
////		}
////
////		modelMap.put("useMaps", true);
////	}
//
//	private void configureAtoZ(ModelMap modelMap, Map<String, Long> atozFacets) {
//		//Fill in A to Z facet details
//		if (!atozFacets.isEmpty()) {
//			ArrayList<AZInfo> alphaList = new ArrayList<AZInfo>();
//			for(int i=0; i < allAlpha.length; i++){
//				AZInfo curList = new AZInfo();
//				Long count = atozFacets.get(allAlpha[i]);
//				curList.setLetter(allAlpha[i]);
//
//				if (count == null){
//					curList.setCount("0");
//				}
//				else {
//					curList.setCount(count.toString());
//				}
//
//				alphaList.add(i, curList);
//			}
//			modelMap.put("alphaNumConfig", alphaList);
//		}
//
//	}
//	// end of new search code
//
//
//
//	@RequestMapping(value = "/cat/report/item/{id}")
//	public String reportItem(ModelMap modelMap, @PathVariable("id") Long id, HttpServletRequest request, ReportItem reportItem) {
//
//		Product p = productDAO.findOne(id);
//		modelMap.addAttribute("product", p);
//
//		Vendor v = vendorDAO.findOne(p.getVendorId());
//		modelMap.addAttribute("vendor", v);
//
//		reportItem.setItemId(p.getId());
//
//		String pageTitle = "pt_catalogue_report";
////		createDisplayBean(modelMap, section, pageTitle, null, request);
//
//		return "cat/report/item";
//	}
//
//	@RequestMapping(value = "/cat/report/vendor/{id}")
//	public String reportProvider(ModelMap modelMap, @PathVariable("id") Long id, HttpServletRequest request, ReportItem reportItem) {
//
//		Vendor v = vendorDAO.findOne(id);
//		modelMap.addAttribute("vendor", v);
//
//		reportItem.setVendorId(id);
//
//		String pageTitle = "pt_catalogue_report";
////		createDisplayBean(modelMap, section, pageTitle, null, request);
//
//		return "cat/report/vendor";
//	}
//
//
//	private String getDerivedView(String view) {
//
//		if (view != null) {
//			if (view.equalsIgnoreCase("list") || view.equalsIgnoreCase("tile")) return view;
//			else return "list";
//		} else {
//			return "list";
//		}
//	}
//
//
//
//	/*
//	 * Old search
//	 */
//	/*
//    @RequestMapping
//    public String index(ModelMap modelMap, HttpServletRequest request, SearchFacetForm searchFacetForm) {
//
//    	String searchText = request.getParameter("q");
//    	String searchCategory = request.getParameter("cat");
//    	String searchType = request.getParameter("type");
//    	String searchPC = request.getParameter("pc");
//    	String toolbarsearch = request.getParameter("toolbarsearch");
//    	String a2zText = request.getParameter("atoz");
//    	String derivedView = getDerivedView(request.getParameter("view"));
//
//    	SaUser saUser = getSaUser();
//
//    	if (saUser != null){
//
//    		if (saUser.isModifiedSearchPC() == false) {
//    			searchPC = saUser.getSearchPC();
//    		}
//
//    		modelMap.put("searchPC", searchPC);
//
//    	} else {
//    		modelMap.put("searchPC", searchPC);
//    	}
//    	modelMap.put("sPC", searchPC);
//
//    	//eLog 23219 - this is for cases where the user has NOT submitted a search, but has typed a postcode and then clicked on a link
//    	//and for some reason expects a postcode filter to apply to the link 
//    	//TW disabled for now as it seems plain wrong
//    	//String typedInPC = (String)request.getSession().getAttribute("typedInPC");
//    	//if (StringUtils.isBlank(searchPC) && !StringUtils.isBlank(typedInPC)) {
//		//	searchPC = typedInPC;
//    	//}
//
//		//eLog 11122 Ensure that the type param doesn't have any random characters appended to it
//		if (StringUtils.isNotEmpty(searchType)){
//			searchType = searchType.trim();
//			if (searchType.startsWith("product")){
//				searchType = "product";
//			} else if (searchType.startsWith("service")){
//				searchType = "service";
//			} else if (searchType.startsWith("vendor")){
//				searchType = "vendor";
//			} else if (searchType.startsWith("classified")){
//				searchType = "classified";
//			} else if (searchType.startsWith("provider")){
//				searchType = "provider";
//			} else if (searchType.startsWith("carehome")){
//				searchType = "carehome";
//			} else if (searchType.startsWith("pa")){
//				searchType = "pa";
//			} else if (searchType.startsWith("childcare")){
//				searchType = "childcare";
//			} else if (searchType.startsWith("all")){
//				searchType = "all";
//			}
//		}
//		else {
//			//searchType = "service";
//			SystemConfig sc =  getSystemConfig();
//	    	String dst = sc.getDefaultSearchType();
//
//	    	if (!StringUtils.isBlank(dst)) {
//	    		searchType = dst;
//	    	} else {
//	    		searchType = "service";
//	    	}
//		}
//		//eLog 11122 
//
//    	modelMap.put("siteLoc", searchType);
//    	modelMap.put("searchType", searchType);
//
//    	UiUtils.generateBackButtonURL(modelMap, request);
//
//
//    	DirectoryInfo directoryInfo = DirectoryInfoHelper.getDirectoryInfo(mongoUIDaoService, "service");
// 		if (directoryInfo != null && directoryInfo.isEnabled() == true) {
// 			modelMap.put("serviceEnabled", true);
// 		}
//
// 		directoryInfo = DirectoryInfoHelper.getDirectoryInfo(mongoUIDaoService, "product");
// 		if (directoryInfo != null && directoryInfo.isEnabled() == true) {
// 			modelMap.put("productEnabled", true);
// 		}
//
// 		// get displayResultsPage config
// 		// set to default
// 		// maybe this should be set up depending on search type or for each result?
//     	directoryInfo = DirectoryInfoHelper.getDirectoryInfo(mongoUIDaoService, null);
//     	setDisplayResultsPageConfig(modelMap);
//
//     	boolean displayMap = getDefaultDisplayMap(directoryInfo);
// 		modelMap.put("useMaps", displayMap);
//
//    	//Display the appropriate category landing page
//    	String browseCategory = getBrowseCategory(searchType);
//
//	    if (StringUtils.isBlank(toolbarsearch) && 
//				StringUtils.isBlank(searchText) && 
//				StringUtils.isBlank(a2zText) && 
//				StringUtils.isBlank(searchCategory) &&
//				searchFacetForm.getApplyFacet()==false && 
//				!StringUtils.isEmpty(browseCategory)) {
//
//	    	Category root = categoryDAO.findOne(Long.parseLong(browseCategory));
//
//	    	List<Category> catList  = categoryDAO.getChildCategories(root);
//
//	    	modelMap.put("catList", catList);
//
//			String pageTitle = "pt_index_" + this.getSystemConfig().getContextRoot().toLowerCase();	 
//    		createDisplayBean(modelMap, section, pageTitle, Arrays.asList(searchType), request);
//
//
//	    	return "cat/browse/index";
//
//    	}
//    	else {
//    		if (!StringUtils.isBlank(searchPC)) {
//
//    			String pcShort = searchPC.replaceAll(" ", "");
//
//    			// do we need to check if postcode is valid?
//    			if (getDefaultSearchPostcodeValidate() == true) {
//    				boolean validPostcode = addressMasterUKDAO.isPostcodeInLA(pcShort, Coverage.LOCALAUTHORITIES);
//    				if (!validPostcode) {
//    					modelMap.put("invalidPostcodeText",getSearchPostcodeNotSupportedText());
//    					modelMap.put("searchSection","cat");
//    		        	modelMap.put("derivedView", derivedView);
//    		        	modelMap.put("showNavDetail", "true");
//
//    		        	String pageTitle = "pt_index_" + this.getSystemConfig().getContextRoot().toLowerCase();	
//    		    		createDisplayBean(modelMap, section, pageTitle, Arrays.asList(searchType), "listing_noresults");
//
//    		        	return "cat/results/" + derivedView;
//
//    				}
//    			}
//
//
//    	     	LatLng latLng = addressMasterUKDAO.findLatLngByPostcode(pcShort, Coverage.UK);
//
//
//    	        if (latLng != null) {
//    	        	Double lat = latLng.getLatitude();
//    	          	Double lng = latLng.getLongitude();
//    	        	modelMap.put("lat", lat.toString());
//    	        	modelMap.put("lng", lng.toString());
//    	        	modelMap.put("zoom", getDefaultZoom(null)); 
//    	        	modelMap.put("radius", getDefaultRadius(null)); 
//    	     	}
//        	} else {
//        		// no post code defined
//        		modelMap.put("lat", getDefaultLatitude(searchType)); 
//	        	modelMap.put("lng", getDefaultLongitude(searchType)); 
//	        	modelMap.put("zoom", getDefaultZoom(searchType)); 
//	        	modelMap.put("radius", getDefaultRadius(searchType)); 
//         	}
//
//    		//Do the search and populate the modelMap with all the search-related display info 
//    		searchService.doSearch(request, modelMap, null, false, saUser, searchFacetForm);
//
//    		SearchResult result = (SearchResult)modelMap.get("searchResult");
//    		List<DisplayItem> products  = result.getProducts();
//
//
//    		List<MapPin> mapPins = new ArrayList<MapPin>();
//
//    		if (displayMap == true && products != null){
//
//	    		int index = 1;
//
//	    		for (DisplayItem di : products) {
//	    			if (di instanceof StandardProduct) {
//	    				StandardProduct sp = (StandardProduct)di;
//	    				Double lat = sp.getLatitude();
//	    				Double lng = sp.getLongtitude();
//	    				if (lat != null && lng != null) {
//		    				MapPin mapPin = new MapPin();
//		    				mapPin.setLatitude(lat.toString());
//		    				mapPin.setLongtitude(lng.toString());
//
//		    				StringBuffer icon;
//		        			icon = new StringBuffer("images/maps/pinburgandy.png"); 
//		        			icon.insert(icon.indexOf(".png"), index);
//		        			mapPin.setIcon(icon.toString());
//
//		        			mapPin.setTitle(sp.getTitle());
//		        			sp.setMapImagePath("/"+icon.toString());
//
//		    				mapPins.add(mapPin);
//
//		    				StringBuffer desc  = new StringBuffer();
//		    				String context = getSystemConfig().getContextRoot();
//
//		    				desc.append("<ul class=\"links\">");
//		    				desc.append("<li><a href=\"cat/vendor/" + sp.getId() +"\">View details</a></li>");
//
//		    				if (!StringUtils.isBlank(sp.getVendorWebsite())) {
//		    					desc.append("<li><a href=\"" + sp.getVendorWebsite() + "\">Visit website</a></li>");
//		    				}
//
//		    				if (!StringUtils.isBlank(sp.getVendorEmail())) {
//		    					desc.append("<li><a href=\"mailto:" + sp.getVendorEmail() + "\">Email</a></li>");
//		    				}
//
//		    				if (!StringUtils.isBlank(sp.getVendorTelephone())) {
//		    					desc.append("<li>Telephone: " + sp.getVendorTelephone() + "</li>");
//		    				}
//
//		    				desc.append("<ul>");   				
//
//		    				mapPin.setDescription(desc.toString());
//
//		    				index++;
//	    				}
//	    			}
//	    		}
//
//
//    		} 
//
//
//    		if (mapPins.size() > 0) {
//    			modelMap.put("showmap", true);
//    		} else{
//    			//TO_DO
//    			// this is done to stop javascript from blowing up
//    			// need better way
//    			MapPin mapPin = new MapPin();
//
//    			mapPin.setLatitude(getDefaultLatitude(null)); 
//				mapPin.setLongtitude(getDefaultLongitude(null)); 
//
//    			mapPins.add(mapPin);
//    			String jsonPins = MapPin.toJsonArray(mapPins);
//    			modelMap.put("jsonPins", jsonPins);
//    			modelMap.put("showmap", false);
//    		}
//
//
//    		String jsonPins = MapPin.toJsonArray(mapPins);
//			modelMap.put("jsonPins", jsonPins);
//
//
//
//    		if (getDisplayAToZ() == true) {
//
//	    		//Fill in A to Z facet details
//	    		SearchResult res = (SearchResult)modelMap.get("searchResult");
//	    		Map<String, Long> atozFacets = res.getAtozFacets();
//
//	    		ArrayList<AZInfo> alphaList = new ArrayList<AZInfo>();
//				for(int i=0; i < allAlpha.length; i++){
//					AZInfo curList = new AZInfo();
//					Long count = atozFacets.get(allAlpha[i]);
//					curList.setLetter(allAlpha[i]);
//
//					if (count == null){
//						curList.setCount("0");
//					}
//					else {
//						curList.setCount(count.toString());
//					}
//
//					alphaList.add(i, curList);
//				}
//				modelMap.put("alphaNumConfig", alphaList);
//				if (a2zText != null) {
//					modelMap.put("sAtoZ", a2zText);
//				}
//    		}
//
//
//    		//Not sure how to persist this yet!
//    		modelMap.put("searchSection","cat");
//        	modelMap.put("derivedView", derivedView);
//        	modelMap.put("showNavDetail", "true");
//
//        	String pageTitle = "pt_index_" + this.getSystemConfig().getContextRoot().toLowerCase();	
//    		createDisplayBean(modelMap, section, pageTitle, Arrays.asList(searchType), "listing_noresults");
//
//        	return "cat/results/" + derivedView;
//
//
//    	}
//
//    }
//	 */

}
