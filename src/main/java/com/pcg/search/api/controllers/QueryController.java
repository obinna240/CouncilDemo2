package com.pcg.search.api.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pcg.search.api.beans.APIQueryResponse;
import com.pcg.search.query.SearchInfo;
import com.pcg.search.query.SolrSearcher;



//@RestController
//@RequestMapping("/api/query/**")
public class QueryController {
	
	@Autowired
	private SolrSearcher searcher;
	
	
	/**
	 * @param request
	 * @param modelMap
	 * @param searchText
	 * @param atoz
	 * @param searchPC
	 * @param searchRadius
	 * @param pageSize
	 * @param pageNum
	 * @param filterType
	 * @param categories
	 * 
	 * TODO add
	 * instance (e.g. hampshire)
	 * townName
	 * 
	 * @return
	 */
    @RequestMapping("")
    public APIQueryResponse query(HttpServletRequest request, ModelMap modelMap,
    							@RequestParam(value = "q", required = false) String searchText,
    							@RequestParam(value = "atoz", required = false) String atoz,
    							@RequestParam(value = "pc", required = false) String searchPC,
    							@RequestParam(value = "rad", required = false) String searchRadius, /*TODO why are these numerical values passed as strings?*/
    							@RequestParam(value = "ps", required = false, defaultValue = "10") String pageSize, 
    							@RequestParam(value = "p", required = false, defaultValue = "1") String pageNum,     
    							@RequestParam(value = "ft", required = false) String filterType,
    			    			@RequestParam(value = "cat", required = false) String categories
    							) {
	
    	SearchInfo searchInfo = updateSearchInfo(request, searchText, atoz, searchPC, searchRadius, pageSize, pageNum, filterType, categories);	
    	
    	APIQueryResponse queryResponse = searcher.getQueryResults(searchInfo);
    	
    	return queryResponse;
    }
    
    
	private SearchInfo updateSearchInfo(HttpServletRequest request, String searchText, String atoz, String searchPC, String searchRadius, String pageSize, String pageNum, String filterType, String categories) {

		//TODO parameter validation - throw exception if invalid
		
		SearchInfo searchInfo = new SearchInfo();


		//Remove extra whitespaces from input searchText
		if (searchText != null){
			searchText = searchText.trim();
			searchText = searchText.replaceAll("\\s+", " ");
		}

		if (StringUtils.equalsIgnoreCase(searchText, "SEARCH")) {
			// eLog 9697 The search field is prepopulated with 'SEARCH' if empty
			// So treat this like a blank search
			searchText = "";
			searchInfo.setSearchText(searchText);
		} else if (StringUtils.isNotBlank(searchText)) {
			searchInfo.setSearchText(searchText);
		}

		if (StringUtils.isNotBlank(atoz)) {
			searchInfo.setA2zText(atoz);
		}

		if (StringUtils.equalsIgnoreCase(searchPC, "SEARCH") || StringUtils.isBlank(searchPC)) {
			// eLog 9697 The search field is prepopulated with 'SEARCH' if empty
			// So treat this like a blank search
			searchPC = "";
			searchInfo.setPc(searchPC);
		} else if (StringUtils.isNotBlank(searchPC)) {
			searchInfo.setPc(searchPC);
		}

		if (StringUtils.equalsIgnoreCase(searchRadius, "0")) {
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
			if (StringUtils.isNotBlank(pageNum)) {
				searchInfo.setPage(Integer.parseInt(pageNum));
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

		// searchInfo.setSearchType("website");
		searchInfo.setRecommendLinks(true);
		searchInfo.setLogSearch(request.getParameter("samonitor") != null);

		return searchInfo;
	}
	
	
	
}
