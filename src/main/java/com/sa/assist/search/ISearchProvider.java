package com.sa.assist.search;

import java.util.Date;
import java.util.List;


public interface ISearchProvider {

	

	SearchResult getSearchResults(String searchText, Integer pageNum, Integer pSize, String sortOption, Long vendorId, boolean vendorSearch, boolean atoz, 
			List<FacetSearchType> facetSearchTypeList, List<Long> categoryFacetList, List<Long> needList, 
			boolean commissionedOnly, boolean kitemarkOnly, boolean managedVendorsOnly, boolean registeredVendorsOnly, List<Long> contextList, 
			String searchPC, String latitude, String longitude, Float radius, String randSort, boolean isSmartSuggest,
			boolean isApplySmartSuggestFilter, Date fromDate, Date toDate, boolean isBedSearch,  boolean categoryORSearch);

	SearchResult getProductSearchResults(List<Long> alsoPurchasedProducts, Long pType, String searchPC);
	SearchResult getSelectedRelatedItems(String productIDs);
	SearchResult getSelectedRelatedContent(String contentIDs);
	SearchResult getRelatedItems(String searchText,  String searchCategory, Integer noOfItems);
	String getSolrServerUrl();
	//SearchResult getRelatedPanelItems(String searchText,  String searchType);
	//SearchResult getRelatedPanelMetaItems(String condition, String product, String vendor, String need);
	SearchResult getVendorSearchResults(String vendorIds);
	//SearchResult getSearchResultsRHP(String searchText, SearchType searchType, String category, Integer page, Integer pageSize, String sortOption, List<Long> needList, String latitude, String longitude, Float radiusKm, String searchPC, String randSort, String condition);
}