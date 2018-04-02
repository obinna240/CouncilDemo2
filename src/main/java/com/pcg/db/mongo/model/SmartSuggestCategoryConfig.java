package com.pcg.db.mongo.model;

public class SmartSuggestCategoryConfig implements IAssistConfig {
	private Long id;
	private boolean enabled;
	private String name;
	private String displayText;
	private String filterType;
	private boolean smartSuggest; //Used for excluding filters during search as smartsuggest has to be site-wide.
	private boolean applySmartSuggestFilter;//decides whether the request handler assist_smartsuggest_content will be applied.
	private int limit; //number of items to show in smartsuggest dropdown
	private Integer displayOrder;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplayText() {
		return displayText;
	}
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}
	public String getFilterType() {
		return filterType;
	}
	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}
	
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isSmartSuggest() {
		return smartSuggest;
	}
	public void setSmartSuggest(boolean smartSuggest) {
		this.smartSuggest = smartSuggest;
	}
	public boolean isApplySmartSuggestFilter() {
		return applySmartSuggestFilter;
	}
	public void setApplySmartSuggestFilter(boolean applySmartSuggestFilter) {
		this.applySmartSuggestFilter = applySmartSuggestFilter;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	public SmartSuggestCategoryConfig(Long id, boolean enabled, String name,
			String displayText, String filterType, boolean smartSuggest,
			boolean applySmartSuggestFilter, int limit, Integer displayOrder) {
		super();
		this.id = id;
		this.enabled = enabled;
		this.name = name;
		this.displayText = displayText;
		this.filterType = filterType;
		this.smartSuggest = smartSuggest;
		this.applySmartSuggestFilter = applySmartSuggestFilter;
		this.limit = limit;
		this.displayOrder = displayOrder;
	}
}
