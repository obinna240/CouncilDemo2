package com.sa.assist.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;


/**
 * Enumeration of all supported facet types
 * Also serves to map each type to its own search filter
 */
public enum FacetSearchType {
	
	PRODUCT(1L, "Products", "product", "", true),
	SERVICE(2L, "Services", "service", "", true),

	CAREHOME(101L, "Care homes", "vendor", "carehome", true),
	PA(102L, "PAs", "vendor", "pa", false),
	CHILDCARE(103L, "Childcare", "vendor", "childcare", false),
	VOLUNTEER(104L, "Volunteers", "vendor", "volunteer", false),
	PROVIDER(105L, "Providers", "vendor", "provider", false),	
	SUPPORTGROUP(106L, "Support groups", "vendor", "supportgroup", false),
	SCHOOL(107L, "Schools", "vendor", "school", false),
	
	CMS_FAQ(201L, "FAQ", "CMS_FAQ", "", false),
	CMS_CONTENT(202L, "Information pages", "CMS_CONTENT", "", false),
	CMS_NEWS(203L, "News", "CMS_NEWS", "", false),
	//CMS_EVENT(204L, "Events", "CMS_EVENT", "", true);
	EVENT(204L, "Events", "event", "", true);
	
	private Long id;	
	private String name;
	private String type;
	private String subType;
	private boolean geoAware;
	
	FacetSearchType(Long id, String name, String type, String subType, boolean geoAware) {
		this.setId(id);
        this.setName(name);
        this.setType(type);
        this.setSubType(subType);
        this.setGeoAware(geoAware);
    }

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
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public boolean isGeoAware() {
		return geoAware;
	}

	public void setGeoAware(boolean geoAware) {
		this.geoAware = geoAware;
	}

	public static FacetSearchType fromName(String name){
		
		FacetSearchType[] values = FacetSearchType.values();
		
		for (FacetSearchType facetType : values) {
			if (StringUtils.equalsIgnoreCase(facetType.getName(), name)){
				return facetType;
			}
		}
		
		
		
		return null;
	}
	
	public static List<FacetSearchType> fromType(String type){
		
		ArrayList<FacetSearchType> facetSearchTypes = new ArrayList<FacetSearchType>();
		
		FacetSearchType[] values = FacetSearchType.values();
		
		for (FacetSearchType facetType : values) {
			if (StringUtils.equalsIgnoreCase(facetType.getType(), type)){
				facetSearchTypes.add(facetType);
			}
		}
		
		return facetSearchTypes;
	}
	
	public static FacetSearchType fromSubType(String subType){
		
		FacetSearchType[] values = FacetSearchType.values();
		
		for (FacetSearchType facetType : values) {
			if (StringUtils.equalsIgnoreCase(facetType.getSubType(), subType)){
				return facetType;
			}
		}
		
		return null;
	}
	
	public static FacetSearchType find(String name){
		
		FacetSearchType facetSearchType = fromName(name);
		if (facetSearchType != null) {
			return facetSearchType;
		}
		
		List<FacetSearchType> facetSearchTypes = fromType(name);
		if (facetSearchTypes != null && facetSearchTypes.size() == 1) {
			return facetSearchTypes.get(0);
		}

		facetSearchType = fromSubType(name);
		if (facetSearchType != null) {
			return facetSearchType;
		}

		return null;
	}
}
