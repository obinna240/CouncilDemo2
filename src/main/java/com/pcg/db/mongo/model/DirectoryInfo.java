package com.pcg.db.mongo.model;


public class DirectoryInfo  implements IAssistConfig{
	private String type;
	private String name;
	private boolean enabled;
	private String search;
	private String latitude;
	private String longitude;
	private String zoom;
	private String defaultRadius;
	private String browseCategory;
	private String searchType;	
	private boolean registrationEnabled;
	private boolean emailToFriend;
	
	private String directoryCategories;
	private boolean contractedSupported = false;
	private boolean approvedStatusSupported = false;
	private boolean callBackEnabled = false;
	private String applicableAccreditationLevels;
	private String mandatoryAccreditationLevels;
	
	private String bedCategories;
	private String supportedContexts;
	
	private boolean removeFromIndex = false;
	
	private boolean agencySupported = true;
	
	private boolean kitemarkSupported = false;
	
	private boolean spatialSearch = false;
	
	// not supported yet - TO_DO!
	//private boolean productsSupported = false;
	//private boolean servicesSupported = false;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public String getSearch() {
		return search;
	}
	
	public void setSearch(String search) {
		this.search = search;
	}
	
	public String getLatitude() {
		return latitude;
	}
	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public String getLongitude() {
		return longitude;
	}
	
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public String getZoom() {
		return zoom;
	}
	
	public void setZoom(String zoom) {
		this.zoom = zoom;
	}
	
	public String getDefaultRadius() {
		return defaultRadius;
	}
	
	public void setDefaultRadius(String defaultRadius) {
		this.defaultRadius = defaultRadius;
	}
	
	public String getBrowseCategory() {
		return browseCategory;
	}
	
	public void setBrowseCategory(String browseCategory) {
		this.browseCategory = browseCategory;
	}
	
	public String getSearchType() {
		return searchType;
	}
	
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
	
	public boolean isRegistrationEnabled() {
		return registrationEnabled;
	}
	
	public void setRegistrationEnabled(boolean registrationEnabled) {
		this.registrationEnabled = registrationEnabled;
	}
	
	
	public boolean isEmailToFriend() {
		return emailToFriend;
	}
	
	public void setEmailToFriend(boolean emailToFriend) {
		this.emailToFriend = emailToFriend;
	}
	
	public String getDirectoryCategories() {
		return directoryCategories;
	}
	
	public void setDirectoryCategories(String directoryCategories) {
		this.directoryCategories = directoryCategories;
	}
	
	public boolean isContractedSupported() {
		return contractedSupported;
	}
	
	public void setContractedSupported(boolean contractedSupported) {
		this.contractedSupported = contractedSupported;
	}
	
	public boolean isApprovedStatusSupported() {
		return approvedStatusSupported;
	}
	
	public void setApprovedStatusSupported(boolean approvedStatusSupported) {
		this.approvedStatusSupported = approvedStatusSupported;
	}
	
	public boolean isCallBackEnabled() {
		return callBackEnabled;
	}
	
	public void setCallBackEnabled(boolean callBackEnabled) {
		this.callBackEnabled = callBackEnabled;
	}

	public String getApplicableAccreditationLevels() {
		return applicableAccreditationLevels;
	}

	public void setApplicableAccreditationLevels(
			String applicableAccreditationLevels) {
		this.applicableAccreditationLevels = applicableAccreditationLevels;
	}

	public String getMandatoryAccreditationLevels() {
		return mandatoryAccreditationLevels;
	}

	public void setMandatoryAccreditationLevels(String mandatoryAccreditationLevels) {
		this.mandatoryAccreditationLevels = mandatoryAccreditationLevels;
	}

	public String getBedCategories() {
		return bedCategories;
	}

	public void setBedCategories(String bedCategories) {
		this.bedCategories = bedCategories;
	}

	public String getSupportedContexts() {
		return supportedContexts;
	}

	public void setSupportedContexts(String supportedContexts) {
		this.supportedContexts = supportedContexts;
	}

	public boolean isRemoveFromIndex() {
		return removeFromIndex;
	}

	public void setRemoveFromIndex(boolean removeFromIndex) {
		this.removeFromIndex = removeFromIndex;
	}

	public boolean isAgencySupported() {
		return agencySupported;
	}

	public void setAgencySupported(boolean agencySupported) {
		this.agencySupported = agencySupported;
	}

	public boolean isKitemarkSupported() {
		return kitemarkSupported;
	}

	public void setKitemarkSupported(boolean kitemarkSupported) {
		this.kitemarkSupported = kitemarkSupported;
	}

	public boolean isSpatialSearch() {
		return spatialSearch;
	}

	public void setSpatialSearch(boolean spatialSearch) {
		this.spatialSearch = spatialSearch;
	}


//	public boolean isProductsSupported() {
//		return productsSupported;
//	}
//
//	public void setProductsSupported(boolean productsSupported) {
//		this.productsSupported = productsSupported;
//	}
//
//	public boolean isServicesSupported() {
//		return servicesSupported;
//	}
//
//	public void setServicesSupported(boolean servicesSupported) {
//		this.servicesSupported = servicesSupported;
//	}
	
	
}
