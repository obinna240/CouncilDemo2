package com.sa.assist.search;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public interface DisplayItem {
	
	public String getId();
	public String getTitle();
	public String getDescription();
//	public String getImageUrl();
//	public String getVendorImageUrl();
	public Integer getReviewCount();
	/**
	 * 
	 * @param rating
	 * @return Entire HTML mark-up to show average rating and have each
	 * 			star link to add rating.
	 */
	public String getStarRatingMarkup(Integer rating);
	/**
	 * 
	 * @param price
	 * @param priceUnit
	 * @param pricePosition
	 * @return Price string based on parameters
	 */
	public String getPrice(BigDecimal price, String priceUnit, String pricePosition);
	public List<String> getSellingMethods();
	public Integer getVendorId();
	public String getVendorBusinessName();
	public HashMap<String,String> getAttributes();
	//public String getCategory();
	//public String getDisplayCategory();
	public String getVendorParentName();
	
	public boolean getQuote();
	public boolean getVendorAccredited();
	public int getStockLevel();
	
	public String getDistance();
	
	public String getPriceDescription();
	
	
	public String getType();
	
	public boolean getApplyForApproval();
	public void setApplyForApproval(boolean applyForApproval);
	public String getPageUrl();
//	public Map<String, String> getBedAvailabilityMap();
	public List<Date> getAvailabledates();
	
	public boolean isSmartSuggest();
	
	public boolean isRecommendedLink();
	public String getRecommendedLinkText();
}
