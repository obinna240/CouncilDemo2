package com.pcg.search.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class SearchInfo implements java.io.Serializable {
	
	private String searchText;
	private String a2zText;
	private String searchType;
	
	private String pc;
	private String radius;

	private int pageNum;
	private int pageSize;
	
	private String sortOption;
	private String randomSortId;
	
	private String derivedView;
	
	private boolean applyFacet = false;
	
	private List<String> filterTypes = new ArrayList<String>();
	private List<Long> categories = new ArrayList<Long>();
//private List<Long> conditions = new ArrayList<Long>();
//	private List<Long> needs = new ArrayList<Long>();
	
	private boolean random;
	private boolean apiCall;
	private boolean recommendLinks;
	private boolean logSearch = true;
	
	// main search, not from facets etc
	private boolean toolbarSearch;
	
	// smartsuggest drop down box search
	private boolean smartSuggest;
	private boolean applySmartSuggestFilter;
	private int rows;
	
	// commissionedOnly
	private boolean commissionedOnly;
	
	// kitemarkOnly
	private boolean kitemarkOnly;
	
	// managed / registered managed providers
	private boolean managedVendorsOnly;
	private boolean registeredVendorsOnly;
	
	// context e.g Adults, children
	private List<Long> contexts = new ArrayList<Long>();
	
	//Bed availability from and to dates for reservation calendar
	private boolean bedSearch;
	
	//categorySearchOperator
	boolean categoryOrSearchOperator = false;
	
	// dates
	private Date fromDate;
	private Date toDate;

	// events search
	private String selectedDate;
	private String dateTo;
	private String dateFrom;
	private String searchPeriod;
	private boolean serarchByPeriod;
	
	// default search radius
	private String defaultRadius;
	
	public boolean isSerarchByPeriod() {
		return serarchByPeriod;
	}

	public void setSerarchByPeriod(boolean serarchByPeriod) {
		this.serarchByPeriod = serarchByPeriod;
	}

	public SearchInfo() {
		this.pageNum = 1;
		this.pageSize = 10;
		this.random = false;
		this.apiCall = false;
	}
	
	public synchronized String getSearchText() {
		return searchText;
	}

	public synchronized void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public synchronized String getA2zText() {
		return a2zText;
	}

	public synchronized void setA2zText(String a2zText) {
		this.a2zText = a2zText;
	}

	public synchronized String getSearchType() {
		return searchType;
	}

	public synchronized void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public synchronized String getPc() {
		return pc;
	}

	public synchronized void setPc(String pc) {
		this.pc = pc;
	}

	public synchronized String getRadius() {
		return radius;
	}

	public synchronized void setRadius(String radius) {
		this.radius = radius;
	}

	public synchronized int getPageNum() {
		return pageNum;
	}

	public synchronized void setPage(int pageNum) {
		this.pageNum = pageNum;
	}

	public synchronized Integer getPageSize() {
		return pageSize;
	}

	public synchronized void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public synchronized String getSortOption() {
		return sortOption;
	}

	public synchronized void setSortOption(String sortOption) {
		this.sortOption = sortOption;
	}

	public synchronized String getRandomSortId() {
		return randomSortId;
	}

	public synchronized void setRandomSortId(String randomSortId) {
		this.randomSortId = randomSortId;
	}

	public synchronized String getDerivedView() {
		return derivedView;
	}

	public synchronized void setDerivedView(String derivedView) {
		this.derivedView = derivedView;
	}

	public synchronized boolean isApplyFacet() {
		return applyFacet;
	}

	public synchronized void setApplyFacet(boolean applyFacet) {
		this.applyFacet = applyFacet;
	}

	public synchronized List<String> getFilterTypes() {
		return filterTypes;
	}

	public synchronized  void setFilterTypes(List<String> filterTypes) {
		this.filterTypes = filterTypes;
	}

	public synchronized List<Long> getCategories() {
		return categories;
	}

	public synchronized void setCategories(List<Long> categories) {
		this.categories = categories;
	}

//	public synchronized List<Long> getConditions() {
//		return conditions;
//	}
//
//	public synchronized void setConditions(List<Long> conditions) {
//		this.conditions = conditions;
//	}
//
//	public synchronized List<Long> getNeeds() {
//		return needs;
//	}
//
//	public synchronized void setNeeds(List<Long> needs) {
//		this.needs = needs;
//	}

	public synchronized boolean isRandom() {
		return random;
	}

	public synchronized void setRandom(boolean random) {
		this.random = random;
	}

	public synchronized boolean isApiCall() {
		return apiCall;
	}

	public synchronized void setApiCall(boolean apiCall) {
		this.apiCall = apiCall;
	}

	public synchronized boolean isRecommendLinks() {
		return recommendLinks;
	}

	public synchronized void setRecommendLinks(boolean recommendLinks) {
		this.recommendLinks = recommendLinks;
	}
	
	public synchronized boolean isToolbarSearch() {
		return toolbarSearch;
	}

	public synchronized void setToolbarSearch(boolean toolbarSearch) {
		this.toolbarSearch = toolbarSearch;
	}

	public synchronized boolean isSmartSuggest() {
		return smartSuggest;
	}

	public synchronized void setSmartSuggest(boolean smartSuggest) {
		this.smartSuggest = smartSuggest;
	}

	public synchronized boolean isManagedVendorsOnly() {
		return managedVendorsOnly;
	}

	public synchronized void setManagedVendorsOnly(boolean managedVendorsOnly) {
		this.managedVendorsOnly = managedVendorsOnly;
	}

	public synchronized boolean isRegisteredVendorsOnly() {
		return registeredVendorsOnly;
	}

	public synchronized void setRegisteredVendorsOnly(boolean registeredVendorsOnly) {
		this.registeredVendorsOnly = registeredVendorsOnly;
	}

	public synchronized List<Long> getContexts() {
		return contexts;
	}

	public synchronized void setContexts(List<Long> contexts) {
		this.contexts = contexts;
	}

	public synchronized int getRows() {
		return rows;
	}

	public synchronized void setRows(int rows) {
		this.rows = rows;
	}

	public synchronized boolean isApplySmartSuggestFilter() {
		return applySmartSuggestFilter;
	}

	public synchronized void setApplySmartSuggestFilter(boolean applySmartSuggestFilter) {
		this.applySmartSuggestFilter = applySmartSuggestFilter;
	}

	public synchronized boolean isCommissionedOnly() {
		return commissionedOnly;
	}

	public synchronized void setCommissionedOnly(boolean commissionedOnly) {
		this.commissionedOnly = commissionedOnly;
	}

	public boolean isKitemarkOnly() {
		return kitemarkOnly;
	}

	public void setKitemarkOnly(boolean kitemarkOnly) {
		this.kitemarkOnly = kitemarkOnly;
	}

	public synchronized boolean isBedSearch() {
		return bedSearch;
	}

	public synchronized void setBedSearch(boolean bedSearch) {
		this.bedSearch = bedSearch;
	}

	public synchronized boolean isCategoryOrSearchOperator() {
		return categoryOrSearchOperator;
	}

	public synchronized void setCategoryOrSearchOperator(boolean categoryOrSearchOperator) {
		this.categoryOrSearchOperator = categoryOrSearchOperator;
	}
	
	public synchronized Date getFromDate() {
		return fromDate;
	}

	public synchronized void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public synchronized Date getToDate() {
		return toDate;
	}

	public synchronized void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	
	public String getSelectedDate() {
		return selectedDate;
	}

	public void setSelectedDate(String selectedDate) {
		this.selectedDate = selectedDate;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getSearchPeriod() {
		return searchPeriod;
	}

	public void setSearchPeriod(String searchPeriod) {
		this.searchPeriod = searchPeriod;
	}
	
	public String getDefaultRadius() {
		return defaultRadius;
	}

	public void setDefaultRadius(String defaultRadius) {
		this.defaultRadius = defaultRadius;
	}

	// JSON STUFF
	public synchronized String toJson() {
        return new JSONSerializer().exclude("*.class").deepSerialize(this);
    }

	public static synchronized SearchInfo fromJsonToAttribute(String json) {
        return new JSONDeserializer<SearchInfo>().use(null, SearchInfo.class).deserialize(json);
    }

	public boolean isLogSearch() {
		return logSearch;
	}

	public void setLogSearch(boolean logSearch) {
		this.logSearch = logSearch;
	}


}
