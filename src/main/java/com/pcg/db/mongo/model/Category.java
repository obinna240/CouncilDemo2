package com.pcg.db.mongo.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class Category implements java.io.Serializable, Comparable<Category> {

	private Long id;
	private Long categoryRootId;
	private Long categoryParentId;
	private String description;
	private String name;
	private String imagePath;
	private boolean enabled = true;
	private boolean facetable = true;
	private Long categoryDisplayTypeId;
	private int minSelectable = 0;
	private int maxSelectable = 0;
	private boolean keyProperty = true;
    private List <Long> relatedTerms = new ArrayList<Long>();
    private boolean showInDetailsPage = false;
    private Integer displayOrder = null;
	private String info;
	
	 //private List <Long> aliases = new ArrayList<Long>();
	   
	// only used if contexts are used
	private List <Long> contextIds = new ArrayList<Long>();
	   
	public Category() {
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCategoryRootId() {
		return categoryRootId;
	}

	public void setCategoryRootId(Long categoryRootId) {
		this.categoryRootId = categoryRootId;
	}

	public Long getCategoryParentId() {
		return categoryParentId;
	}

	public void setCategoryParentId(Long categoryParentId) {
		this.categoryParentId = categoryParentId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
//	public String getImageURL(){
//		return SASpringContext.getImageManager().getCategoryDefaultImageURL(this.getIdAsString());
//	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isFacetable() {
		return facetable;
	}

	public void setFacetable(boolean facetable) {
		this.facetable = facetable;
	}
	
	public Long getCategoryDisplayTypeId() {
		return categoryDisplayTypeId;
	}

	public void setCategoryDisplayTypeId(Long categoryDisplayTypeId) {
		this.categoryDisplayTypeId = categoryDisplayTypeId;
	}

	public int getMinSelectable() {
		return minSelectable;
	}

	public void setMinSelectable(int minSelectable) {
		this.minSelectable = minSelectable;
	}

	public int getMaxSelectable() {
		return maxSelectable;
	}

	public void setMaxSelectable(int maxSelectable) {
		this.maxSelectable = maxSelectable;
	}

	public boolean isKeyProperty() {
		return keyProperty;
	}

	public void setKeyProperty(boolean keyProperty) {
		this.keyProperty = keyProperty;
	}

	public List<Long> getRelatedTerms() {
		return relatedTerms;
	}

	public void setRelatedTerms(List<Long> relatedTerms) {
		this.relatedTerms = relatedTerms;
	}
	
	public boolean isShowInDetailsPage() {
		return showInDetailsPage;
	}

	public void setShowInDetailsPage(boolean showInDetailsPage) {
		this.showInDetailsPage = showInDetailsPage;
	}
	
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public List<Long> getContextIds() {
		return contextIds;
	}

	public void setContextIds(List<Long> contextIds) {
		this.contextIds = contextIds;
	}

//	public List<Long> getAliases() {
//		return aliases;
//	}
//
//	public void setAliases(List<Long> aliases) {
//		this.aliases = aliases;
//	}

	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public static Category fromJsonToCategory(String json) {
        return new JSONDeserializer<Category>().use(null, Category.class).deserialize(json);
    }

	public static String toJsonArray(Collection<Category> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static Collection<Category> fromJsonArrayToCategorys(String json) {
        return new JSONDeserializer<List<Category>>().use(null, ArrayList.class).use("values", Category.class).deserialize(json);
    }
	
	public String toString() {
		return getIdAsString();
    }
	
	@Override
	public boolean equals(Object aThat) {
		//check for self-comparison
		if ( this == aThat ) return true;
		
		if (aThat instanceof Category) {
		
			Category that = (Category)aThat;
			if (that.getId() == null) {
				return false;
			}
			if (this.getId().longValue() == that.getId().longValue()) {
				return true;
			}
		}
		
		return false;
	 	
	 }
	 
	 public String getIdAsString() {
	 	if (this.getId() == null) {
	 		return "";
	 	}
	 
		return new Long(this.id).toString();
	}

	@Override
	public int compareTo(Category o) {
		
		Category catComp = (Category)o;
		
		if (this.getName() != null && catComp.getName() != null){
			return this.getName().toLowerCase().compareTo(catComp.getName().toLowerCase());
		}
		else {
			return 0;
		}
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

}
