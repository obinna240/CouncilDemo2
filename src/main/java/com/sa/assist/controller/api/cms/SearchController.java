package com.sa.assist.controller.api.cms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pcg.db.mongo.dao.SAAddressDAO;
import com.pcg.db.mongo.model.SAAddress;
import com.pcg.db.mongo.model.SAAddress.Coverage;
import com.sa.assist.controller.CatalogueController;
import com.sa.assist.controller.api.cms.json.AjaxResult;
import com.sa.assist.controller.api.cms.json.AjaxStatus;
import com.sa.assist.controller.api.cms.json.CMSSearchResult;
import com.sa.assist.controller.api.cms.json.packets.LocationPacket;
import com.sa.assist.controller.bean.SmartSuggestCategory;
import com.sa.assist.search.FacetSearchType;
import com.sa.assist.search.SearchInfo;
import com.sa.assist.search.SearchResult;
import com.sa.assist.search.SearchServices;
import com.sa.assist.service.MongoUIDaoService;

import flexjson.JSONSerializer;
import flexjson.transformer.HtmlEncoderTransformer;

@RequestMapping("/assistapi/cms/search/**")
@Controller
public class SearchController extends CatalogueController{
	
	private static Log m_log = LogFactory.getLog(SearchController.class);
	
	@Autowired private SearchServices searchService;
//	@Autowired private VendorDAO vendorDAO;
//	@Autowired private ManagedBedDAO managedBedDAO;
//	@Autowired private UserDAO userDAO;
	@Autowired private SAAddressDAO saAddressDAO;
	@Autowired private MongoUIDaoService mongoUIDaoService;
	
	private	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy"); //jquery date
	private SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd"); //HTML5 date

	private static Map<String, JSONSerializer> serializers;
	static {
		serializers = new HashMap<String, JSONSerializer>();
		serializers.put("DEFAULT", new JSONSerializer().exclude("*.class"));
		//serializers.put("PRODUCTSEARCH", new JSONSerializer().exclude("*.class","products.accreditations","products.starRatingMarkup","products.attributes","atozFacets","categoryFacets","conditionFacets").transform(new HtmlEncoderTransformer(), "products.price"));
		serializers.put("PRODUCTSEARCH", new JSONSerializer().exclude("*.class","searchResult.products.accreditations","searchResult.products.starRatingMarkup","searchResult.products.attributes","searchResult.atozFacets","searchResult.categoryFacets","searchResult.conditionFacets","searchResult.vendorTypeFacets","searchResult.searchTypeFacets").transform(new HtmlEncoderTransformer(), "products.price"));
		serializers.put("PRODUCTSEARCHFACETS", new JSONSerializer().exclude("*.class","searchResult.products.accreditations","searchResult.products.starRatingMarkup","searchResult.products.attributes","searchResult.atozFacets","searchResult.categoryFacets","searchResult.conditionFacets").include("categoryList", "conditionList").transform(new HtmlEncoderTransformer(), "products.price"));
		serializers.put("VENDOR", new JSONSerializer().exclude("*.class","userdetail.userdetailtoaddresses","userdetail.demographicdatas","testimonial.vendor"));
	}
	
	private static final char[] hexChar = { '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private static String unicodeEscape(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if ((c >> 7) > 0) {
				sb.append("\\u");
				sb.append(hexChar[(c >> 12) & 0xF]); // append the hex character
														// for the left-most
														// 4-bits
				sb.append(hexChar[(c >> 8) & 0xF]); // hex for the second group
													// of 4-bits from the left
				sb.append(hexChar[(c >> 4) & 0xF]); // hex for the third group
				sb.append(hexChar[c & 0xF]); // hex for the last group, e.g.,
												// the right most 4-bits
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	// New search code
	@RequestMapping(value = "/ref")
	public synchronized String processLink(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(value = "search", required = true) String encodedJSON,
			RedirectAttributes redirectAttributes) {
		
		SearchInfo searchInfo;
		String json = "";
		try {
			byte[] valueDecoded= Base64.decodeBase64(encodedJSON.getBytes());
			json = new String(valueDecoded);
			searchInfo = SearchInfo.fromJsonToAttribute(json);
		} catch (Exception e) {
			m_log.debug("Failed to deserialize searchInfo: " + encodedJSON);
			StringBuffer sbRequest = request.getRequestURL();
			String requestStr = sbRequest.toString();
			m_log.debug("Referrer: " + requestStr);
			searchInfo = new SearchInfo();	
		}
		request.getSession().setAttribute("searchInfo", null);
		request.getSession().removeAttribute("searchInfo");
	
		request.getSession().setAttribute("searchInfo", searchInfo);
	
		return search(modelMap, request);
	}	
	
	

	@RequestMapping(value = "/doSearch")
	public synchronized String doSearch(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(value = "q", required = false) String searchText,
			@RequestParam(value = "atoz", required = false) String atoz,
			@RequestParam(value = "pc", required = false) String searchPC,
			@RequestParam(value = "ps", required = false) String pageSize,
			@RequestParam(value = "p", required = false) String pageNo,
			@RequestParam(value = "ft", required = false) String filterType,
			@RequestParam(value = "cat", required = false) String categories,
			RedirectAttributes redirectAttributes) {
		
		if (pageNo == null) {
			pageNo = "0";
		}
	
		if (StringUtils.isBlank(searchText)) {
			searchText = "SEARCH";
		}
		
		if (StringUtils.isBlank(searchPC)) {
			searchPC = "SEARCH";
		}
		
		// reset search Info 
		// there is an issue where the searchInfo does not seem to be cleared so do belts and braces approach
		HttpSession session =  request.getSession(false);
		if( session!= null && session.getAttribute("searchInfo") != null){
			m_log.debug("Removed serachInfo from sesstion");
			session.setAttribute("searchInfo", null);
			session.removeAttribute("searchInfo");
		}
		
		
		updateSearchInfo(request, searchText, atoz, searchPC, null, pageSize, pageNo, filterType, categories);	
		
		//if ("CMS_CONTENT".equalsIgnoreCase(filterType)) {
		//	modelMap.put("hideCategoryFacets", true);
		//}
			
		
		return search(modelMap, request);
	}	
	
	@RequestMapping(value = "/doPaginationSearch")
	public synchronized String doPaginationSearch(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(value = "ps", required = false) String pageSize,
			@RequestParam(value = "p", required = false) String pageNo,
			RedirectAttributes redirectAttributes) {
		
		/// preserve PC if set
		String searchPC= "";
		SearchInfo searchInfo = (SearchInfo)request.getSession().getAttribute("searchInfo");
		if (searchInfo != null) {
			searchPC = searchInfo.getPc();
		}
				
		updateSearchInfo(request, null, null, searchPC, null, pageSize, pageNo, null, null);
			
		return search(modelMap, request);
	}
		
	@RequestMapping(value = "/doFacetDistanceSearch")
	public synchronized String facetSearch(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(value = "pc", required = false) String searchPC,
			@RequestParam(value = "radius", required = false) String radius,
			RedirectAttributes redirectAttributes) {
		
		if (StringUtils.isBlank(searchPC)) {
			searchPC = "SEARCH";
		}
		
		if (StringUtils.isBlank(radius)) {
			radius = "0";
		}
		
		updateSearchInfo(request, null, null, searchPC, radius, null, "1", null, null);	
		
		return search(modelMap, request);
	}	
	
	@RequestMapping(value = "/doMenuSearch")
	public synchronized String doMenuSearch(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(value = "ft", required = false) String filterType,
			@RequestParam(value = "cat", required = false) String categories,
			RedirectAttributes redirectAttributes) {
		
		/// preserve PC if set
		String searchPC= "";
		SearchInfo searchInfo = (SearchInfo)request.getSession().getAttribute("searchInfo");
		if (searchInfo != null) {
			searchPC = searchInfo.getPc();
		}
		
		// reset search Info 
		// there is an issue where the searchInfo does not seem to be cleared so do belts and braces approach
		request.getSession().setAttribute("searchInfo", null);
		request.getSession().removeAttribute("searchInfo");
			
		String ft = null;
		FacetSearchType facetSearchType = FacetSearchType.find(filterType);
		if (facetSearchType != null) {
			ft = facetSearchType.getName();
		}
		
		updateSearchInfo(request, null, null, searchPC, null, null, "0", ft, categories);	
		
		//if ("CMS_CONTENT".equalsIgnoreCase(filterType)) {
		//	modelMap.put("hideCategoryFacets", true);
		//}
			
		return search(modelMap, request);
	}	
	
	private void updateSearchInfo(HttpServletRequest request, String searchText, String atoz, String searchPC, String searchRadius, String pageSize, String pageNo, 
			String filterType, String categories) {

		SearchInfo searchInfo = (SearchInfo)request.getSession().getAttribute("searchInfo");
		if (searchInfo == null) {
			searchInfo = new SearchInfo();
//			SearchHelper.addContextToSearchInfo(searchInfo, mongoUIDaoService);
			request.getSession().setAttribute("searchInfo", searchInfo);
		}

		if (StringUtils.equalsIgnoreCase(searchText, "SEARCH")){
			//eLog 9697 The search field is prepopulated with 'SEARCH' if empty
			//So treat this like a blank search
			searchText = "";
			searchInfo.setSearchText(searchText);
		} else if (StringUtils.isNotBlank(searchText)) {
			searchInfo.setSearchText(searchText);
		}

		if (StringUtils.isNotBlank(atoz)) {
			searchInfo.setA2zText(atoz);
		}

		if (StringUtils.equalsIgnoreCase(searchPC, "SEARCH") || StringUtils.isBlank(searchPC)){
			//eLog 9697 The search field is prepopulated with 'SEARCH' if empty
			//So treat this like a blank search
			searchPC = "";
			searchInfo.setPc(searchPC);
		} else 	if (StringUtils.isNotBlank(searchPC)) {
			searchInfo.setPc(searchPC);
		}
		
		if (StringUtils.equalsIgnoreCase(searchRadius, "0")){
			searchInfo.setRadius("");
		} else if (StringUtils.isNotBlank(searchRadius)) {
			searchInfo.setRadius(searchRadius);
		}

		try {
			if (StringUtils.isNotBlank(pageSize)) {
				searchInfo.setPageSize(Integer.parseInt(pageSize));
			}
		} catch (Exception e) {
			// Ignore and use the default
		}
		
		try {
			if (StringUtils.isNotBlank(pageNo)) {
				searchInfo.setPage(Integer.parseInt(pageNo));
			}
		} catch (Exception e) {
			// Ignore and use the default
		}

		if (StringUtils.isNotBlank(filterType)) {
			List<String> items = Arrays.asList(filterType.split("\\s*,\\s*"));
			searchInfo.setFilterTypes(items);
		}
		
		if (StringUtils.isNotBlank(categories)) {
			List<String> cats = Arrays.asList(categories.split(";"));
			List<Long> catIds = new ArrayList<Long>();
			for (String catId : cats) {
				catIds.add(Long.valueOf(catId));
			}
			searchInfo.setCategories(catIds);
		}

		//searchInfo.setSearchType("website");
		searchInfo.setRecommendLinks(true); // recommend links 
				
	}
	
	// end new search code
	
	@RequestMapping(value = "/getLocation")
	public @ResponseBody String getLocationSearch(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(value = "pc", required = true) String searchPC) {
		
		List<SAAddress> addressPostcode = saAddressDAO.findByPostcode(searchPC, Coverage.UK);
		LocationPacket locationPacket = new LocationPacket();
		if (addressPostcode != null && addressPostcode.size() > 0) {
			SAAddress address = addressPostcode.get(0);
			locationPacket.setLatitude(Double.toString(address.getLatitude()));
			locationPacket.setLongitude(Double.toString(address.getLongtitude()));
		}
		
		return serializers.get("DEFAULT").serialize(locationPacket);
	}
	
	@RequestMapping(value = "/getPostcode")
	public @ResponseBody String getPostcodeSearch(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
		
		String postcode = "";
		SearchInfo searchInfo = (SearchInfo)request.getSession().getAttribute("searchInfo");
		if (searchInfo != null) {
			postcode = searchInfo.getPc();
		}
		
		return serializers.get("DEFAULT").serialize(postcode);
	}
	
	@RequestMapping(value = "/getSnac")
	public @ResponseBody String getSnac(HttpServletRequest request, ModelMap modelMap,
			@RequestParam(value = "pc", required = true) String postcode) {
		
		List<String> codeByPostcode = saAddressDAO.findLocalGovtCodeByPostcode(postcode,Coverage.LOCALAUTHORITIES);
		AjaxResult result;
		
		if (codeByPostcode.size() > 0) {
			result = new AjaxResult(AjaxStatus.OK, "SNAC found", codeByPostcode.get(0));
		} else {
			result = new AjaxResult(AjaxStatus.ERROR, "SNAC not found", null);
		}
		
		return serializers.get("DEFAULT").serialize(result);
	}

	// CMS Calls
	@RequestMapping(value = "/getRelatedContent")
	public @ResponseBody String getRelatedContent(HttpServletRequest request, ModelMap modelMap,
			@RequestParam(value = "cats", required = false) String catList,
			@RequestParam(value = "cons", required = false) String conList,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "random", required = false) Boolean random){
		
		Long[] cats = generateArray(catList);
		Long[] cons = generateArray(conList);
		
		// search now only takes cat ids
		Long[] all = (Long[])ArrayUtils.addAll(cats, cons);
		
		searchService.searchForRelatedContent(request, modelMap, null, false, all, FacetSearchType.CMS_CONTENT.getName(), false, false);
		SearchResult sr = (SearchResult) modelMap.get("searchResult");
		
		CMSSearchResult cmsSearchResult = new CMSSearchResult();
		cmsSearchResult.setSearchResult(sr);
	
		String retVal = serializers.get("PRODUCTSEARCH").serialize(cmsSearchResult);
		
		return unicodeEscape(retVal);
	}

	@RequestMapping(value = "/getRelatedItems")
	public @ResponseBody String getRelatedItems(HttpServletRequest request, ModelMap modelMap,
			@RequestParam(value = "cats", required = false) String catList,
			@RequestParam(value = "cons", required = false) String conList,
			@RequestParam(value = "pc", required = false) String pc) {
		
		Long[] cats = generateArray(catList);
		Long[] cons = generateArray(conList);
		// search now only takes cat ids
		Long[] all = (Long[])ArrayUtils.addAll(cats, cons);
		
		String items = FacetSearchType.PRODUCT.getName() + "," + FacetSearchType.SERVICE.getName();
		searchService.searchForRelatedContent(request, modelMap, null, false, all, items, false, false);
		SearchResult sr = (SearchResult) modelMap.get("searchResult");
		
		CMSSearchResult cmsSearchResult = new CMSSearchResult();
		cmsSearchResult.setSearchResult(sr);
	
		String retVal = serializers.get("PRODUCTSEARCH").serialize(cmsSearchResult);
		return unicodeEscape(retVal);   
	}	
	
	@RequestMapping(value = "/getSelectedRelatedItems")
	public @ResponseBody String getSelectedRelatedItems(HttpServletRequest request,
			@RequestParam(value = "idlist", required = true) String productIDs) {
		
		SearchResult sr = searchService.searchForSelectedRelatedItems(productIDs);
		CMSSearchResult cmsSearchResult = new CMSSearchResult();
		cmsSearchResult.setSearchResult(sr);
	
		String retVal = serializers.get("PRODUCTSEARCH").serialize(cmsSearchResult);
		return unicodeEscape(retVal);   
	
	}
	@RequestMapping(value = "/getRelatedRegisteredVendors")
	public @ResponseBody String getRelatedRegisteredVendors(HttpServletRequest request, ModelMap modelMap,
			@RequestParam(value = "cats", required = false) String catList,
			@RequestParam(value = "cons", required = false) String conList,
			@RequestParam(value = "pc", required = false) String pc) {
		
		Long[] cats = generateArray(catList);
		Long[] cons = generateArray(conList);
		// search now only takes cat ids
		Long[] all = (Long[])ArrayUtils.addAll(cats, cons);
			
		//TODO: Currently condition and post-code are unimplemented!!!!
		
		searchService.searchForRelatedContent(request, modelMap, null, false, all, FacetSearchType.PROVIDER.getName(), false, true);
		SearchResult sr = (SearchResult) modelMap.get("searchResult");
		
		CMSSearchResult cmsSearchResult = new CMSSearchResult();
		cmsSearchResult.setSearchResult(sr);
	
		String retVal = serializers.get("PRODUCTSEARCH").serialize(cmsSearchResult);
		return unicodeEscape(retVal);   
	}
	

	@RequestMapping(value = "/getRelatedManagedVendors")
	public @ResponseBody String getRelatedManagedVendors(HttpServletRequest request, ModelMap modelMap,
			@RequestParam(value = "cats", required = false) String catList,
			@RequestParam(value = "cons", required = false) String conList,
			@RequestParam(value = "pc", required = false) String pc) {
		
		Long[] cats = generateArray(catList);
		Long[] cons = generateArray(conList);
				
		//TODO: Currently condition and post-code are unimplemented!!!!
		// search now only takes cat ids
		Long[] all = (Long[])ArrayUtils.addAll(cats, cons);
			
		searchService.searchForRelatedContent(request, modelMap, null, false, all, FacetSearchType.PROVIDER.getName(), true, false);
		SearchResult sr = (SearchResult) modelMap.get("searchResult");
		
		CMSSearchResult cmsSearchResult = new CMSSearchResult();
		cmsSearchResult.setSearchResult(sr);
	
		String retVal = serializers.get("PRODUCTSEARCH").serialize(cmsSearchResult);
		return unicodeEscape(retVal);   
	}
	
	 private Long[] generateArray(String catList) {
			
			if (catList != null && catList != "") {
				String[] catSplit = catList.split(",");
				Long[] cats = new Long[catSplit.length];
		
				for (int i = 0; i < catSplit.length; i++) {
				    try {
				    	cats[i] = Long.parseLong(catSplit[i]);
				    } catch (NumberFormatException nfe) {
				    	// No logger set up!!
				    };
				}
				
				return cats;
			} else {
				return null;
			}
		}
	// end of cms calls

	
//	@RequestMapping(value = "/getSearchResults")
//	public @ResponseBody String doProductSearch(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, 
//			@RequestParam(value = "q", required = false) String searchText,
//			@RequestParam(value = "cat", required = false) String searchCategory,
//			@RequestParam(value = "type", required = false) String searchType,
//			@RequestParam(value = "pc", required = false) String searchPC,
//			@RequestParam(value = "p", required = false) String pageNo,
//			@RequestParam(value = "ps", required = false) String pageSize
//			) {
//		
//		//Get user detail from sso ??
//    	SaUser saUser = null; 
//    	
//		//TODO: This may be too much overhead, might be possible to just do the actual search
//    	// so we dont have to pap about getting stuff out of request!!
//		searchService.doSearch(request, modelMap, saUser);
//		SearchResult sr = (SearchResult) modelMap.get("searchResult");
//		
//		response.setCharacterEncoding(" UTF-8");
//		
//		CMSSearchResult cmsSearchResult = new CMSSearchResult();
//		cmsSearchResult.setSearchResult(sr);
//	
//		String retVal = serializers.get("PRODUCTSEARCH").serialize(cmsSearchResult);
//		return unicodeEscape(retVal);   
//	}
	
//	@RequestMapping(value = "/getFacetSearchResults")
//	public @ResponseBody String doCMSSearch(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, 
//			@RequestParam(value = "q", required = false) String searchText,
//			@RequestParam(value = "cat", required = false) String searchCategory,
//			@RequestParam(value = "type", required = false) String searchType,
//			@RequestParam(value = "pc", required = false) String searchPC,
//			@RequestParam(value = "p", required = false) String pageNo,
//			@RequestParam(value = "ps", required = false) String pageSize,
//			@RequestParam(value = "facetType", required = false) String[] facetTypes,
//			@RequestParam(value = "facetCategory", required = false) String[] facetCategories,
//			@RequestParam(value = "facetCondition", required = false) String[] facetConditions
//	
//			) {
//		
//		//Get user detail from sso ??
//		SaUser saUser = null; 
//    	
//		
//		SearchType st = SearchType.findBySearchParam(searchType);
//		
//		
//		//TODO: This may be too much overhead, might be possible to just do the actual search
//    	// so we dont have to pap about getting stuff out of request!!
//    	SearchFacetForm searchFacetForm  = null;
//    	
//    	if (facetTypes != null || facetCategories != null || facetConditions != null) {
//    		searchFacetForm = new SearchFacetForm();
//    		searchFacetForm.setApplyFacet(true);
//    		searchFacetForm.setApiCall(false);
//    		
//    		if (facetTypes != null) {
//    			searchFacetForm.setTypes(facetTypes);
//    			st = SearchType.RUNTIME;
//    		}
//    		
//    		if (facetCategories != null) {
//    			Integer[] categories = new Integer[facetCategories.length];
//    			int index = 0;
//    			for (String str : facetCategories) {
//    				categories[index] = Integer.valueOf(str);
//    				index++;
//    			}
//    			searchFacetForm.setCategories(categories);
//    		}
//    		
//    		if (facetConditions != null) {
//    			Integer[] conditions = new Integer[facetConditions.length];
//    			int index = 0;
//    			for (String str : facetConditions) {
//    				conditions[index] = Integer.valueOf(str);
//    				index++;
//    			}
//    			searchFacetForm.setConditions(conditions);
//    		}
//    	}
//    	
//    	
//		searchService.doSearch(request, modelMap, saUser, searchFacetForm);
//		SearchResult sr = (SearchResult) modelMap.get("searchResult");
//				
//		CMSSearchResult cmsSearchResult = new CMSSearchResult();
//		cmsSearchResult.setSearchResult(sr);
//	
//		List<TreeNode> categoryList = new ArrayList<TreeNode>();
//		List<TreeNode> conditionList = new ArrayList<TreeNode>();
//		CategoryType catType;
//		TreeNode ct;
//		
//		if (st.equals(SearchType.PRODUCT)){
//			catType = CategoryType.PRODUCT;
//			ct = SASpringContext.getTreeNodeManager().getCategoryRoot(catType);
//			categoryList = SASpringContext.getTreeNodeManager().getFacetedCategoryList(ct, sr.getCategoryFacets());
//		}
//		else if (st.equals(SearchType.CAREHOME)){
//			catType = CategoryType.CARE_HOME;
//			ct = SASpringContext.getTreeNodeManager().getCategoryRoot(catType);
//			categoryList = SASpringContext.getTreeNodeManager().getFacetedCategoryList(ct, sr.getCategoryFacets());
//		}
//		else if (st.equals(SearchType.ITEMS)){
//			catType = CategoryType.PRODUCT;
//			ct = SASpringContext.getTreeNodeManager().getCategoryRoot(catType);
//			List<TreeNode> productCategoryList = SASpringContext.getTreeNodeManager().getFacetedCategoryList(ct, sr.getCategoryFacets());
//		
//			catType = CategoryType.SERVICE;
//			ct = SASpringContext.getTreeNodeManager().getCategoryRoot(catType);
//			List<TreeNode> serviceCategoryList = SASpringContext.getTreeNodeManager().getFacetedCategoryList(ct, sr.getCategoryFacets());
//			
//			categoryList = new ArrayList<TreeNode>();
//			categoryList.addAll(productCategoryList);
//			categoryList.addAll(serviceCategoryList);
//		}
//		else if (st.equals(SearchType.ALL) || st.equals(SearchType.WEBSITE)){
//			catType = CategoryType.PRODUCT;
//			ct = SASpringContext.getTreeNodeManager().getCategoryRoot(catType);
//			List<TreeNode> productCategoryList = SASpringContext.getTreeNodeManager().getFacetedCategoryList(ct, sr.getCategoryFacets());
//		
//			catType = CategoryType.SERVICE;
//			ct = SASpringContext.getTreeNodeManager().getCategoryRoot(catType);
//			List<TreeNode> serviceCategoryList = SASpringContext.getTreeNodeManager().getFacetedCategoryList(ct, sr.getCategoryFacets());
//			
//			
//			catType = CategoryType.CARE_HOME;
//			ct = SASpringContext.getTreeNodeManager().getCategoryRoot(catType);
//			List<TreeNode> carehomeCategoryList = SASpringContext.getTreeNodeManager().getFacetedCategoryList(ct, sr.getCategoryFacets());
//		
//
//			categoryList = new ArrayList<TreeNode>();
//			categoryList.addAll(productCategoryList);
//			categoryList.addAll(serviceCategoryList);
//			categoryList.addAll(carehomeCategoryList);
//		}
//		else if (st.equals(SearchType.RUNTIME)){
//			
//			String searchFilter = st.getSearchFilter();
//			
//			boolean productCatType = false;
//			boolean serviceCatType = false;
//			boolean carehomeCatType = false;
//					
//			if (searchFilter.indexOf("type:product") != -1) {
//				productCatType = true;
//			}
//			
//			if (searchFilter.indexOf("type:service") != -1) {
//				serviceCatType = true;
//			}
//			
//			if (searchFilter.indexOf("type:vendor") != -1) {
//				carehomeCatType = true;
//			}	
//			
//			if (productCatType == true) {
//				catType = CategoryType.PRODUCT;
//				ct = SASpringContext.getTreeNodeManager().getCategoryRoot(catType);
//				List<TreeNode> productCategoryList = SASpringContext.getTreeNodeManager().getFacetedCategoryList(ct, sr.getCategoryFacets());
//				categoryList.addAll(productCategoryList);
//			}
//			
//			if (serviceCatType== true) {
//				catType = CategoryType.SERVICE;
//				ct = SASpringContext.getTreeNodeManager().getCategoryRoot(catType);
//				List<TreeNode> serviceCategoryList = SASpringContext.getTreeNodeManager().getFacetedCategoryList(ct, sr.getCategoryFacets());
//				categoryList.addAll(serviceCategoryList);
//			}
//			
//			if (carehomeCatType == true) {
//				catType = CategoryType.CARE_HOME;
//				ct = SASpringContext.getTreeNodeManager().getCategoryRoot(catType);
//				List<TreeNode> carehomeCategoryList = SASpringContext.getTreeNodeManager().getFacetedCategoryList(ct, sr.getCategoryFacets());
//				categoryList.addAll(carehomeCategoryList);	
//			}
//				
//		}
//		cmsSearchResult.setCategoryList(categoryList);
//		
//		TreeNode tnCondition = SASpringContext.getTreeNodeManager().getCategoryRoot(CategoryType.CONDITION);
//		if (tnCondition != null) {
//			conditionList = SASpringContext.getTreeNodeManager().getFacetedCategoryList(tnCondition, sr.getConditionFacets());
//			//String a = serializers.get("DEFAULT").serialize(conditionList); 
//			
//		}
//		cmsSearchResult.setConditionList(conditionList);
//
//		
//		List<MapPin> mapPins = new ArrayList<MapPin>();
//		int index = 1;
//		
//		for (DisplayItem di : sr.getProducts()) {
//			if (di instanceof StandardProduct) {
//				StandardProduct sp = (StandardProduct)di;
//				Double lat = sp.getLatitude();
//				Double lng = sp.getLongtitude();
//				if (lat != null && lng != null) {
//    				MapPin mapPin = new MapPin();
//    				mapPin.setLatitude(lat.toString());
//    				mapPin.setLongtitude(lng.toString());
//    					
//    				StringBuffer icon;
//        			icon = new StringBuffer("img/maps/pinburgandy.png"); 
//        			icon.insert(icon.indexOf(".png"), index);
//        			mapPin.setIcon(icon.toString());
//    				
//        			mapPin.setTitle(sp.getTitle());
//        			sp.setMapImagePath("/"+icon.toString());
//        			
//    				StringBuffer desc  = new StringBuffer();
//    				String context = getSystemConfig().getContextRoot();
//    				
//    				desc.append("<ul class=\"links\">");
//    				desc.append("<li><a href=\""+ context + "/cat/vendor/" + sp.getId() +"\">View details</a></li>");
//	    			
//    				
//    				desc.append("<ul>");   				
//    				
//    				mapPin.setDescription(desc.toString());
//    			
//    				mapPins.add(mapPin);
//    				
//    				index++;
//				}
//				
//			}
//		}
//	
//		if (mapPins.size() > 0) {
//			String jsonPins = MapPin.toJsonArray(mapPins);
//			cmsSearchResult.setJsonMapPins(jsonPins);
//		}
//		
//		String retVal = serializers.get("PRODUCTSEARCHFACETS").serialize(cmsSearchResult); 
//					
//		return unicodeEscape(retVal);   
//	}
	
	
	@RequestMapping(method=RequestMethod.GET, value = "/getSmartSuggestions")
   	public @ResponseBody String getSuggestionsAjax(HttpServletRequest request, ModelMap map) {
    	String q = request.getParameter("q");
    	
    	List<SmartSuggestCategory> result = searchService.doSmartSuggestSearch(request, map);
       	String ret = new JSONSerializer().exclude("*.class").deepSerialize(result);
   		return ret;
   	}
	

//	
//	@RequestMapping(value = "/getRelatedVendors")
//	public @ResponseBody String getRelatedVendors(HttpServletRequest request, ModelMap modelMap,
//			@RequestParam(value = "cats", required = false) String catList,
//			@RequestParam(value = "cons", required = false) String conList,
//			@RequestParam(value = "pc", required = false) String pc) {
//		
//		Integer[] cats = generateArray(catList);
//		Integer[] cons = generateArray(conList);
//				
//		//TODO: Currently condition and post-code are unimplemented!!!!
//		
//		searchService.searchForRelatedContent(request, modelMap, null, false, null, cats, cons, SearchType.PROVIDER);
//		SearchResult sr = (SearchResult) modelMap.get("searchResult");
//		
//		CMSSearchResult cmsSearchResult = new CMSSearchResult();
//		cmsSearchResult.setSearchResult(sr);
//	
//		String retVal = serializers.get("PRODUCTSEARCH").serialize(cmsSearchResult);
//		return unicodeEscape(retVal);   
//	}
//
//	@RequestMapping(value = "/getRelatedRegisteredVendors")
//	public @ResponseBody String getRelatedRegisteredVendors(HttpServletRequest request, ModelMap modelMap,
//			@RequestParam(value = "cats", required = false) String catList,
//			@RequestParam(value = "cons", required = false) String conList,
//			@RequestParam(value = "pc", required = false) String pc) {
//		
//		Integer[] cats = generateArray(catList);
//		Integer[] cons = generateArray(conList);
//				
//		//TODO: Currently condition and post-code are unimplemented!!!!
//		
//		searchService.searchForRelatedContent(request, modelMap, null, false, null, cats, cons, SearchType.REGISTERED_PROVIDER);
//		SearchResult sr = (SearchResult) modelMap.get("searchResult");
//		
//		CMSSearchResult cmsSearchResult = new CMSSearchResult();
//		cmsSearchResult.setSearchResult(sr);
//	
//		String retVal = serializers.get("PRODUCTSEARCH").serialize(cmsSearchResult);
//		return unicodeEscape(retVal);   
//	}
//	
//
//	@RequestMapping(value = "/getRelatedManagedVendors")
//	public @ResponseBody String getRelatedManagedVendors(HttpServletRequest request, ModelMap modelMap,
//			@RequestParam(value = "cats", required = false) String catList,
//			@RequestParam(value = "cons", required = false) String conList,
//			@RequestParam(value = "pc", required = false) String pc) {
//		
//		Integer[] cats = generateArray(catList);
//		Integer[] cons = generateArray(conList);
//				
//		//TODO: Currently condition and post-code are unimplemented!!!!
//		
//		searchService.searchForRelatedContent(request, modelMap, null, false, null, cats, cons, SearchType.MANAGED_PROVIDER);
//		SearchResult sr = (SearchResult) modelMap.get("searchResult");
//		
//		CMSSearchResult cmsSearchResult = new CMSSearchResult();
//		cmsSearchResult.setSearchResult(sr);
//	
//		String retVal = serializers.get("PRODUCTSEARCH").serialize(cmsSearchResult);
//		return unicodeEscape(retVal);   
//	}
//
//
	@RequestMapping(value = "/getVendors")
	public @ResponseBody String getVendors(HttpServletRequest request, ModelMap modelMap,
			@RequestParam(value = "vendorIds", required = true) String vendorIds) {
		
		SearchResult sr = searchService.searchForVendors(vendorIds);
		
		CMSSearchResult cmsSearchResult = new CMSSearchResult();
		cmsSearchResult.setSearchResult(sr);
	
		String retVal = serializers.get("PRODUCTSEARCH").serialize(cmsSearchResult);
		return unicodeEscape(retVal);   
	}
	    
    @RequestMapping(value = "/getSelectedRelatedContent")
	public @ResponseBody String getSelectedRelatedContent(HttpServletRequest request,
			@RequestParam(value = "idlist", required = true) String contentIDs) {
		
		SearchResult sr = searchService.searchForSelectedRelatedContent(contentIDs);
		CMSSearchResult cmsSearchResult = new CMSSearchResult();
		cmsSearchResult.setSearchResult(sr);
	
		String retVal = serializers.get("PRODUCTSEARCH").serialize(cmsSearchResult);
		return unicodeEscape(retVal);   
	
	}
//
//    
   
	
}
