package com.pcg.db.mongo.model;



public class CategoryToAttribute implements java.io.Serializable {

	private Long id;
	private Long attributeId;
	private Long categoryId;

	public CategoryToAttribute() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(Long attributeId) {
		this.attributeId = attributeId;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	
	//TODO-MONGO
	// not sure if used??
	/*
	public String toString() {
        StringBuilder sb = new StringBuilder();
        //if (getCategory() != null) {
        //	sb.append(getCategory().getName());
        //}
        if (getAttribute() != null) {
        	sb.append(getAttribute().getName());
        }
        return sb.toString();
     }
	*/
	
//	 @Override
//	 public boolean equals(Object aThat) {
//		//check for self-comparison
//		if ( this == aThat ) return true;
//		
//		if (aThat instanceof Categorytoattribute) {
//		
//			Categorytoattribute that = (Categorytoattribute)aThat;
//			
//			return true;
//		}
//		
//		return false;
//	 	
//	 }
	
}
