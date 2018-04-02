package com.sa.assist.search;

/**
 * Enumeration of all supported search types
 * Also serves to map each type to its own search filter
 */
public enum SearchType {

//	CAREHOME("carehome", "(type:vendor AND vendortype:" + VendorType.CAREHOME.getTypeId() + ")"),
//	PA("pa", "(type:vendor AND vendortype:" + VendorType.PA.getTypeId() + ")"),
//	CHILDCARE("childcare", "(type:vendor AND vendortype:" + VendorType.CHILDCARE.getTypeId() + ")"),
//	CLASSIFIED("classified", "type:classified"),
	CMS_FAQ("faq", "type:CMS_FAQ"),
	CMS_CONTENT("content", "type:CMS_CONTENT"),
//	PRODUCT("product", "type:product"),
//	SERVICE("service", "type:service"),
//	VOLUNTEER("volunteer", "(type:vendor AND vendortype:" + VendorType.VOLUNTEER.getTypeId() + ")"),
//	EVENT("event","type:event"),
//	PROVIDER("provider", "(type:vendor AND vendortype:" + VendorType.PROVIDER.getTypeId() + ")"),
//	ADULT_PROVIDER("adults","(type:vendor AND vendortype:" + VendorType.PROVIDER.getTypeId() + " AND contextgroup:1)"),
//	CHILD_PROVIDER("children", "(type:vendor AND vendortype:" + VendorType.PROVIDER.getTypeId() + " AND contextgroup:2)"),
//	MANAGED_PROVIDER("managedprovider", "(type:vendor AND vendorapproved:true AND vendormanaged:true)"),
//	REGISTERED_PROVIDER("registeredprovider", "(type:vendor AND vendorapproved:true AND vendormanaged:false)"),
//	ADULT_MANAGED_PROVIDER("managedprovider", "(type:vendor AND vendorapproved:true AND vendormanaged:true AND contextgroup:1)"),
//	CHILD_REGISTERED_PROVIDER("registeredprovider", "(type:vendor AND vendorapproved:true AND vendormanaged:falseAND contextgroup:2)"),

	WEBSITE("website", "( (type:vendor AND vendorapproved:true) OR (type:service AND productapproved:true) OR (type:product AND productapproved:true) OR type:CMS_*)"),

//	ALL("all", "(type:vendor OR type:service OR type:product)"),
//	ADULT_ALL("alladults","( (type:vendor AND (contextgroup:1 OR contextgroup:3))  OR type:service OR type:product)"),
//	CHILD_ALL("allchildren", "( (type:vendor AND (vendortype:" + VendorType.PROVIDER.getTypeId() + " OR vendortype:" + VendorType.CHILDCARE.getTypeId() + ") AND  (contextgroup:2 OR contextgroup:3))  OR type:service OR type:product)"),


	ITEMS("items", "(type:product OR type:service)"),

	RUNTIME("runtime", "");


	private String searchParam;
	private String searchFilter;

	SearchType(String searchParam, String searchFilter) {
		this.setSearchParam(searchParam);
		this.setSearchFilter(searchFilter);
	}

	public String getSearchParam() {
		return searchParam;
	}

	public void setSearchParam(String searchParam) {
		this.searchParam = searchParam;
	}

	public String getSearchFilter() {
		return searchFilter;
	}

	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
	}

	public static SearchType findBySearchParam(String searchParam){

		if (searchParam == null){
			return null;
		}

		searchParam = searchParam.toLowerCase();

		SearchType[] vals = SearchType.values();
		for (SearchType searchType : vals) {
			//eLog 11122 (ChooseMySupport) - do a startsWith search rather than an exact match
			if (searchParam.equalsIgnoreCase(searchType.getSearchParam())){
				return searchType;
			}
		}

		return null;
	}
}
