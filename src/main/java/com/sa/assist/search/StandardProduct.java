package com.sa.assist.search;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrDocument;

//@Component
public class StandardProduct implements DisplayItem {
//	@Autowired
//	CategoryDAO categoryDAO;

	
	private String id;
	private String title;
	private String description;
	private Double price;
	
	private Double priceFrom;
	private Double priceTo;
	
	private String priceDescription;

	private Double deliveryCost;

	private Integer starRating;
	private Integer reviewCount = 0;
	private String vendorBusinessName;
	private String vendorParentName;
	private Integer vendorId;
	private Integer vendorParentId;
	private boolean quote;
	private int stockLevel;
	private boolean vendorAccredited;
	private String imagePath;
	//private String category;
	private Integer score;
	private boolean enabled;
//	private boolean callback;
	private String distance;
	
	private boolean catAccredited;
	private boolean catHealthDelegated;
	private boolean catContracted;
	
	

	private boolean vendorManaged;
	private Integer vendorTypeId;
	
	// for results that show maps
	private Double latitude;
	private Double longtitude;
	private String mapImagePath;
	
	private String type;
	
	private String pageUrl;
	
	private String vendorEmail;
	private String vendorTelephone;
	private String vendorWebsite;
	
	private boolean productManaged;
	private boolean suspended;
	private boolean contracted;
	private boolean kitemark;
	
	
	// for vendor approval only, this is not set within the solr search bean
	// only within the vendor/vendorproducts controller.
	private boolean applyForApproval;
	
	private String organisationTypesDisplay;
	
	private int productCount;
	private int serviceCount;
	
			
//	private List<StandardAccreditation> accreditations = new ArrayList<StandardAccreditation>();
	
//	private Map<String, String> bedAvailabilityMap = new LinkedHashMap<String, String>(); //for carehome vendor
	private List<Date> availabledates = new ArrayList<Date>();
	
	private boolean smartSuggest;
	
	private boolean recommendedLink = false;
	private String recommendedLinkText = "";
	
	
	@Override
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public String getDescription() {
		return this.description;
	}
	
	public String getShortDescription() {
		if (this.description != null && this.description.length() > 260) {
			int i = this.description.indexOf(" ", 250);
			if (i>0) {
				return this.description.substring(0,i) + "...";
			}
		} 
		return this.description;
		
	}

	@Override
	public String getStarRatingMarkup(Integer rating) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPrice(BigDecimal price, String priceUnit, String pricePosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getSellingMethods() {
		List<String> temp = new ArrayList<String>();
		temp.add("Buy");
		return temp;
	}

	public void setVendorId(int vendorId) {
		this.vendorId = vendorId;
	}

	@Override
	public Integer getVendorId() {
		return this.vendorId;
	}

	@Override
	public String getVendorBusinessName() {
		return this.vendorBusinessName;
	}

	@Override
	public HashMap<String, String> getAttributes() {
		return null;
	}

//	public void setCategory(String category) {
//		this.category = category;
//	}
//
//	public String getCategory() {
//		return category;
//	}



	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setStarRating(Integer starRating) {
		this.starRating = starRating;
	}

	public Integer getStarRating() {
		return starRating;
	}

	public void setVendorBusinessName(String vendorBusinessName) {
		this.vendorBusinessName = vendorBusinessName;
	}

	public void setQuote(boolean quote) {
		this.quote = quote;
	}

	public boolean getQuote() {
		return quote;
	}

	public void setReviewCount(Integer reviewCount) {
		this.reviewCount = reviewCount;
	}

	public Integer getReviewCount() {
		return reviewCount;
	}

	public int getStockLevel() {
		return this.stockLevel;
	}

	public void setStockLevel(int stockLevel) {
		this.stockLevel = stockLevel;
	}

	public void setVendorAccredited(boolean vendorAccredited) {
		this.vendorAccredited = vendorAccredited;
	}

	public boolean getVendorAccredited() {
		return vendorAccredited;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getScore() {
		return score;
	}

	public boolean getEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}

	public void setPriceDescription(String priceDescription) {
		this.priceDescription = priceDescription;
	}

	public String getPriceDescription() {
		return priceDescription;
	}
//
//	public boolean getCallback() {
//		return callback;
//	}
//
//	public void setCallback(boolean callback) {
//		this.callback = callback;
//	}
//	
	
	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

//	public List<StandardAccreditation> getAccreditations() {
//		return accreditations;
//	}
//
//	public void setAccreditations(List<StandardAccreditation> accreditations) {
//		this.accreditations = accreditations;
//	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getImagePath() {
		return imagePath;
	}

	public boolean isCatAccredited() {
		return catAccredited;
	}

	public void setCatAccredited(boolean catAccredited) {
		this.catAccredited = catAccredited;
	}

	public boolean isCatHealthDelegated() {
		return catHealthDelegated;
	}

	public void setCatHealthDelegated(boolean catHealthDelegated) {
		this.catHealthDelegated = catHealthDelegated;
	}

	public boolean isCatContracted() {
		return catContracted;
	}

	public void setCatContracted(boolean catContracted) {
		this.catContracted = catContracted;
	}

	public boolean isVendorManaged() {
		return vendorManaged;
	}

	public void setVendorManaged(boolean vendorManaged) {
		this.vendorManaged = vendorManaged;
	}
	
	public Integer getVendorTypeId() {
		return vendorTypeId;
	}

	public void setVendorTypeId(Integer vendorTypeId) {
		this.vendorTypeId = vendorTypeId;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(Double longtitude) {
		this.longtitude = longtitude;
	}

	public String getMapImagePath() {
		return mapImagePath;
	}

	public void setMapImagePath(String mapImagePath) {
		this.mapImagePath = mapImagePath;
	}
	
//	public String getDisplayCategory() {
//		try {
//			CategoryDAO categoryDAO = (CategoryDAO)SASpringContext.getBeanFactory().getBean("CategoryDAO");
//			Category c  = categoryDAO.findOne(Long.parseLong(this.category));
//			return c.getName();
//		} catch(Exception e) {
//			return "";
//		}
//	}
	
	public Double getDeliveryCost() {
		return deliveryCost;
	}

	public void setDeliveryCost(Double deliveryCost) {
		this.deliveryCost = deliveryCost;
	}

	public String getDeliveryCostUI() {
		NumberFormat Gb = NumberFormat.getCurrencyInstance(Locale.UK);	
		if (deliveryCost != null && deliveryCost > 0d) {
			return Gb.format(deliveryCost);
		}
		
		return null;
	}
	
	public StandardProduct() {
	}
	
	public StandardProduct(SolrDocument doc, Integer score) {
		
		this.setScore(score);
		this.setTitle((String)doc.getFieldValue("title_display"));
		this.setDescription((String)doc.getFieldValue("description_display"));
		String sId = (String)doc.getFieldValue("id");
		String replace = "p";
		if (StringUtils.contains(sId, "c")) {
			replace = "c";
		}
		String id = StringUtils.replace(sId, replace, "");
		this.setId(id);
		this.setType((String)doc.getFieldValue("type"));
		
		Collection<Object> accreditationimagepaths = doc.getFieldValues("accreditationimagepaths");
//		
//		if (accreditationimagepaths != null){
//			List<StandardAccreditation> accreditations = this.getAccreditations();
//			for (Object aip : accreditationimagepaths) {
//				if (aip instanceof String) {
//					String accreditationimagepath = (String)aip;	
//					String[] splits = StringUtils.split(accreditationimagepath, "|");
//					if (splits.length == 3) {
//						StandardAccreditation standardAccreditation = new StandardAccreditation();
//						standardAccreditation.setDocumentPath(splits[0]);
//						standardAccreditation.setImagePath(splits[1]);
//						Boolean linkEnabled = Boolean.parseBoolean(splits[2]);
//						standardAccreditation.setLinkEnabled(linkEnabled);
//						accreditations.add(standardAccreditation);
//					}
//				}
//			}
//		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public String getVendorEmail() {
		return vendorEmail;
	}

	public void setVendorEmail(String vendorEmail) {
		this.vendorEmail = vendorEmail;
	}

	public String getVendorTelephone() {
		return vendorTelephone;
	}

	public void setVendorTelephone(String vendorTelephone) {
		this.vendorTelephone = vendorTelephone;
	}

	public String getVendorWebsite() {
		
		String website = vendorWebsite;
		
		if (!StringUtils.isBlank(website) && (!StringUtils.startsWith(website, "http://") && !StringUtils.startsWith(website, "https://"))) {
			website = "http://" + website;
		}
		
		if (StringUtils.endsWith(website, "/")) {
			website = StringUtils.removeEnd(website, "/");
		}
		
		return website;
	}

	public void setVendorWebsite(String vendorWebsite) {
		this.vendorWebsite = vendorWebsite;
	}

	public boolean isProductManaged() {
		return productManaged;
	}

	public void setProductManaged(boolean productManaged) {
		this.productManaged = productManaged;
	}

	public boolean isSuspended() {
		return suspended;
	}

	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

	public boolean isContracted() {
		return contracted;
	}

	public void setContracted(boolean contracted) {
		this.contracted = contracted;
	}

	public boolean isKitemark() {
		return kitemark;
	}

	public void setKitemark(boolean kitemark) {
		this.kitemark = kitemark;
	}

	public boolean getApplyForApproval() {
		return applyForApproval;
	}

	public void setApplyForApproval(boolean applyForApproval) {
		this.applyForApproval = applyForApproval;
	}
	public String getOrganisationTypesDisplay() {
		return organisationTypesDisplay;
	}

	public void setOrganisationTypesDisplay(String organisationTypesDisplay) {
		this.organisationTypesDisplay = organisationTypesDisplay;
	}

	public int getProductCount() {
		return productCount;
	}

	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}

	public int getServiceCount() {
		return serviceCount;
	}

	public void setServiceCount(int serviceCount) {
		this.serviceCount = serviceCount;
	}

	public String getVendorParentName() {
		return vendorParentName;
	}

	public void setVendorParentName(String vendorParentName) {
		this.vendorParentName = vendorParentName;
	}

	public Integer getVendorParentId() {
		return vendorParentId;
	}

	public void setVendorParentId(Integer vendorParentId) {
		this.vendorParentId = vendorParentId;
	}

	
//	public Boolean isCallback() {
//		return callback;
//	}

//	public Map<String, String> getBedAvailabilityMap() {
//		return bedAvailabilityMap;
//	}
//
//	public void setBedAvailabilityMap(Map<String, String> bedAvailabilityMap) {
//		this.bedAvailabilityMap = bedAvailabilityMap;
//	}
//
	public List<Date> getAvailabledates() {
		return availabledates;
	}

	public void setAvailabledates(List<Date> availabledates) {
		this.availabledates = availabledates;
	}

	public boolean isSmartSuggest() {
		return smartSuggest;
	}

	public void setSmartSuggest(boolean smartSuggest) {
		this.smartSuggest = smartSuggest;
	}

	public boolean isRecommendedLink() {
		return recommendedLink;
	}

	public void setRecommendedLink(boolean recommendedLink) {
		this.recommendedLink = recommendedLink;
	}

	public String getRecommendedLinkText() {
		return recommendedLinkText;
	}

	public void setRecommendedLinkText(String recommendedLinkText) {
		this.recommendedLinkText = recommendedLinkText;
	}

	
	

}
