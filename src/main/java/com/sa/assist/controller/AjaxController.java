package com.sa.assist.controller;

import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pcg.db.mongo.dao.CategoryDAO;
import com.pcg.db.mongo.dao.SAAddressDAO;
import com.pcg.db.mongo.dao.SystemConfigDAO;
import com.pcg.db.mongo.model.Category;
import com.pcg.db.mongo.model.SAAddress;
import com.pcg.db.mongo.model.SystemConfig;
import com.pcg.db.mongo.model.SAAddress.Coverage;
import com.sa.assist.utils.CacheHandler;

import flexjson.JSONSerializer;

@RequestMapping("/ajax/**")
@Controller
public class AjaxController {

	private static Log m_log = LogFactory.getLog(AjaxController.class);
	
	
	@Autowired SAAddressDAO addressMasterUKDAO;
	@Autowired private CategoryDAO categoryDAO;
//	@Autowired private UserDAO userDAO;
//	@Autowired private MongoUIDaoService mongoUIDaoService;
	@Autowired private SystemConfigDAO systemConfigDAO;

//	@RequestMapping(method=RequestMethod.POST, value = "/ajax/addresscontactlookup")
//	public @ResponseBody String addressContactLookup(@RequestBody String postcode) {
//    	return addressLookup(postcode);
//	}
//	
//
//	@RequestMapping(method=RequestMethod.POST, value = "/ajax/addresscontactlookup2Uk")
//	public @ResponseBody String addressContactLookup2Uk(@RequestBody String postcode) {
//    	return addressUKLookup(postcode);
//	}
//	
//	@RequestMapping(method=RequestMethod.POST, value = "/ajax/addresscontactlookup2")
//	public @ResponseBody String addressContactLookup2(@RequestBody String postcode) {
//    	return addressLookup(postcode);
//	}
//	
//	@RequestMapping(method=RequestMethod.POST, value = "/ajax/addresscontactlookupUk")
//	public @ResponseBody String addressContactLookupUk(@RequestBody String postcode) {
//    	return addressUKLookup(postcode);
//	}
//	
//    
//    @RequestMapping(method=RequestMethod.POST, value = "/ajax/addressinvoicelookup")
//	public @ResponseBody String addressInvoiceLookup(@RequestBody String postcode) {
//    	return addressLookup(postcode);
//	}
//    
//    @RequestMapping(method=RequestMethod.POST, value = "/ajax/addressinvoicelookupUk")
//	public @ResponseBody String addressInvoiceLookupUk(@RequestBody String postcode) {
//    	return addressUKLookup(postcode);
//    }
//    
//    @RequestMapping(method=RequestMethod.POST, value = "/ajax/addressdeliverylookup")
//	public @ResponseBody String addressDeliveryLookup(@RequestBody String postcode) {
//    	return addressLookup(postcode);
//	}
//    
//    
//    @RequestMapping(method=RequestMethod.POST, value = "/ajax/addressbusinesslookup")
//	public @ResponseBody String addressBusinessLookup(@RequestBody String postcode) {
//    	return addressUKLookup(postcode);
//	}
//    
//    @RequestMapping(method=RequestMethod.POST, value = "/ajax/addressbusinesslookup2")
//	public @ResponseBody String addressBusinessLookup2(@RequestBody String postcode) {
//    	return addressUKLookup(postcode);
//	}
//    
//    @RequestMapping(method=RequestMethod.POST, value = "/ajax/addressvendorlookup")
//	public @ResponseBody String addressVendorLookup(@RequestBody String postcode) {
//    	return addressUKLookup(postcode);
//	}
	
//	@RequestMapping(method=RequestMethod.POST, value = "/ajax/addressLookup")
//    public @ResponseBody String addressLookup(@RequestBody String postcode) {
//	 	if (StringUtils.isNotBlank(uk)) {
//	 		return lookupAddress(postcode, Coverage.UK);
//	 	}
//    	return lookupAddress(postcode, Coverage.LOCALAUTHORITIES);
//    }
	
	
    @RequestMapping(method=RequestMethod.POST, value = "/ajax/flushCaches")
    public @ResponseBody String flushCaches(@RequestBody String json) {
		Map<String,String> jsonPacket = new HashMap<String,String>();
    	try {
    		String[] split = json.split("=");
	    	String context = split[1];
			CacheHandler.flushAllCaches();
			SystemConfig config = systemConfigDAO.getDefaultSystemConfig();
    		jsonPacket.put("message", "All the caches flushed for context: " + context);
    	} catch (Exception e) {
    		m_log.error(e);
    		jsonPacket.put("error", "Something bad happened!");
    	}
		return new JSONSerializer().serialize(jsonPacket);
    }
	
    @RequestMapping(method=RequestMethod.POST, value = "/ajax/addressLookup")
    public @ResponseBody String addressLookup(@RequestBody String str) {
    	try {
    		String s = URLDecoder.decode(str, "UTF-8");
    	
	    	String[] split = s.split(";");
	    	String pc = split[0];
	    	Coverage coverage = Coverage.LOCALAUTHORITIES;
	    	if (split.length == 2) {
	    		if ("uk=true".equalsIgnoreCase(split[1])) {
	    			coverage = Coverage.UK;
	    		}
	    	}
	    	
	    	return lookupAddress(pc, coverage);
    	} catch (Exception e) {
    		m_log.error(e);
    		Map<String,String> jsonPacket = new HashMap<String,String>();
    		jsonPacket.put("error", "Something bad happened!");
    		return new JSONSerializer().serialize(jsonPacket);
    	}
    }
    
	@RequestMapping(method=RequestMethod.POST, value = "/ajax/addressUKLookup")
	public @ResponseBody String addressUKLookup(@RequestBody String postcode) {
    	return lookupAddress(postcode, Coverage.UK);
    }
    
    private String lookupAddress(String postcode, Coverage coverage) {
      	Map<String,String> jsonPacket = new HashMap<String,String>();
        
      	// TODO_MONGO
      	// should pass true json string on method call!
      	postcode = StringUtils.replace(postcode, "=", "");
      	
    	if (StringUtils.isBlank(postcode)) {
    		jsonPacket.put("message", "Address not found!");
    	}
    	else {
    		try {
    			String pc = URLDecoder.decode(postcode, "UTF-8");

    			
    			List<SAAddress> listAddresses = addressMasterUKDAO.findByPostcode(pc, coverage);
	    		
	    		if (listAddresses.size() > 0) {
	    			if (listAddresses.size() == 1 ) {
	    				SAAddress addressmaster = listAddresses.get(0);
	    				return addressmaster.toJson();
	    				
	    			} else {
	    				return SAAddress.toJsonArray(listAddresses);
	    			}
	    		}
	    		else {
	    			
	    			//is it a valid postcode
	    			if (coverage == Coverage.LOCALAUTHORITIES) {
	    				listAddresses = addressMasterUKDAO.findByPostcode(pc, Coverage.UK);
	    				if (listAddresses.size() > 0) {
	    					String notSupportedText = "Address not supported";
	    					jsonPacket.put("message", notSupportedText);
	    				} else {
	    					jsonPacket.put("message", "Address not found!");
	    				}
	    				
	    			} else {
	    				jsonPacket.put("message", "Address not found!");
	    			}
	    		}
	    	
	    	} catch (Exception e) {
	    		m_log.error(e);
	    		jsonPacket.put("error", "Something bad happened!");
	    	}
    	}
    	
		return new JSONSerializer().serialize(jsonPacket);
	}
    
    @RequestMapping(method=RequestMethod.POST, value = "/ajax/categorylookup")
	public @ResponseBody String categoryLookup(@RequestBody String mainCatStr) {
     	Map<String,String> jsonPacket = new HashMap<String,String>();
     	if (StringUtils.isBlank(mainCatStr)) {
    		jsonPacket.put("message", "No categories found!");
    	}
    	else {
			try {
			     
		      	// TODO_MONGO
		      	// should pass true json string on method call!
				mainCatStr = StringUtils.replace(mainCatStr, "=", "");
		  
				long mainCatId = Long.parseLong(mainCatStr);
				Category topCat = categoryDAO.findOne(mainCatId);
				List<Category> caregoryList = getServiceCategoryMap(topCat);
	    		
	    		if (caregoryList.size() > 0) {
	    			
	    			return Category.toJsonArray(caregoryList);
	    			
	    		}
	    		else {
	    			jsonPacket.put("message", "No categories found!");
	    		}
	    	
	    	} catch (Exception e) {
	    		m_log.error(e);
	    		jsonPacket.put("error", "Something bad happened!");
	    	}
    	}
    	
		return new JSONSerializer().serialize(jsonPacket);
	}
    
    private List<Category> getServiceCategoryMap(Category topCat){
		
		List<Category> childCats = categoryDAO.getChildCategories(topCat);
		Collections.sort(childCats);
			
		return childCats;
	}	

    /**
     * Store what a user currently has typed in the postcode box  
     */
//	@RequestMapping(method=RequestMethod.POST, value = "/ajax/userpostcode")
//	public @ResponseBody String userPostCode(@RequestBody String postcode, HttpServletRequest request) {
//		
//		postcode = StringUtils.trim(postcode);
//		postcode = StringUtils.removeEnd(postcode, "=");
//		postcode = StringUtils.remove(postcode, "+");
//		request.getSession().setAttribute("typedInPC", postcode);
//		
//		SaUser saUser = getSaUser();
//    	
//    	if (saUser != null) {
//    		saUser.setModifiedSearchPC(true);
//    	}
//		
//		return "";
//    }
//	@RequestMapping(method=RequestMethod.POST, value = "/ajax/userpostcode") 
//	public @ResponseBody String userPostCode(HttpServletRequest request, @RequestBody String postcode) {
//		postcode = StringUtils.trim(postcode);
//		postcode = StringUtils.removeEnd(postcode, "=");
//		postcode = StringUtils.remove(postcode, "+");
//		request.getSession().setAttribute("typedInPC", postcode);
//		
//		SaUser saUser = getSaUser();
//    	
//    	if (saUser != null) {
//    		saUser.setModifiedSearchPC(true);
//    	}
//		
//		return "";
//    }
//	
//	 @RequestMapping(method=RequestMethod.POST, value = "/ajax/relatedCategoryLookup")
//	 public @ResponseBody String relatedCategoryLookup(@RequestBody String id) {
//    	try {
//    		id = StringUtils.trim(id);
//    		id = StringUtils.removeEnd(id, "=");
//    	
//    		Category category = categoryDAO.findOne(Long.parseLong(id));
//    		List<Category> relatedcategories = categoryDAO.getChildCategories(category);
//    		if (relatedcategories!= null && relatedcategories.size() > 0) {
//    			
//    			 List<CategoryUI> relatedcategoryUIs = new ArrayList<CategoryUI>();
//			     for (Category cat : relatedcategories) {
//			    	 CategoryUI categoryUI = new CategoryUI(cat, mongoUIDaoService);
//			    	 relatedcategoryUIs.add(categoryUI);
//			     }
//    			
//			     String js =  CategoryUI.toJsonArrayCategoryUIs(relatedcategoryUIs);
//			     return js;
//    		}
//    		else {
//    	    	return "";
//    		}
//    	} catch (Exception e) {
//    		m_log.error(e);
//    		Map<String,String> jsonPacket = new HashMap<String,String>();
//    		jsonPacket.put("error", "Something bad happened!");
//    		return new JSONSerializer().serialize(jsonPacket);
//    	}
//    }
	 
//	// check username/email exists 
//	@RequestMapping(method=RequestMethod.POST, value = "/ajax/checkUserName")
//    public @ResponseBody String checkUserName(@RequestBody String str) {
//		Map<String,String> jsonPacket = new HashMap<String,String>();
//		
//    	try {
//    		String[] arr = URLDecoder.decode(str, "UTF-8").split("=");
//    		
//    		boolean result = userDAO.checkUserName(arr[1]);
//    		
//    		if (result == true) {
//    			jsonPacket.put("message", "A user with this name/email has been found");
//    		} else {
//    			jsonPacket.put("message", "No user withn this name found! Please try their usename or email addresss.");
//    		}
//	    	
//    	} catch (Exception e) {
//    		m_log.error(e);
//     		jsonPacket.put("error", "Something bad happened!");
//    		
//    	}
//    	
//    	return new JSONSerializer().serialize(jsonPacket);
//    } 
 
//	 // search facets
//	    
//    @RequestMapping(method=RequestMethod.POST, value = "/ajax/updateSearchInfoType")
//   	public @ResponseBody String  updateSearchInfoType( HttpServletRequest request, @RequestBody String data) {
//    	Map<String,String> jsonPacket = new HashMap<String,String>();
//    	SearchInfo searchInfo = (SearchInfo)request.getSession().getAttribute("searchInfo");
//    	if (searchInfo == null) {
//	    	searchInfo = new SearchInfo();
//			request.getSession().setAttribute("searchInfo", searchInfo);
//    	}
//   	
//		String[] params = StringUtils.split(data, '&');
//		String type = "";
//		boolean checked = false;
//		for (String param : params) {
//			String[] d = StringUtils.split(param, '=');
//			String key = d[0];
//			String val = d[1];
//			
//			if (key.equalsIgnoreCase("type")) {
//				type = val;
//			}
//			else if (key.equalsIgnoreCase("state")) {
//				if (val.equalsIgnoreCase("true")) {
//					checked = true;
//				}
//			}		
//		}
//		
//		List<String> types = searchInfo.getFilterTypes();
//		
//		if (types != null) {
//			// to solve unmodifiable exception
//			List<String> newList = new ArrayList<String>(types);
//			if (checked)  {
//				if (!newList.contains(type)) {
//					newList.add(type);
//				}
//			} else {
//				if (newList.contains(type)) {
//					newList.remove(type);
//				}
//			}
//			searchInfo.setFilterTypes(newList);
//		}
//		String json = searchInfo.toJson();
//		
//		byte[]   bytesEncoded = Base64.encodeBase64(json.getBytes());
//		String encoded = new String(bytesEncoded);
//		jsonPacket.put("encoded", encoded);
//
//       	return new JSONSerializer().serialize(jsonPacket);
//   	}
//    
//    @RequestMapping(method=RequestMethod.POST, value = "/ajax/updateSearchInfoCategory")
//   	public @ResponseBody String  updateSearchInfoCategory( HttpServletRequest request, @RequestBody String data) {
//    	Map<String,String> jsonPacket = new HashMap<String,String>();
//    	SearchInfo searchInfo = (SearchInfo)request.getSession().getAttribute("searchInfo");
//    	if (searchInfo == null) {
//	    	searchInfo = new SearchInfo();
//			request.getSession().setAttribute("searchInfo", searchInfo);
//    	}
//   	
//		String[] params = StringUtils.split(data, '&');
//		String idStr = "";
//		boolean checked = false;
//		for (String param : params) {
//			String[] d = StringUtils.split(param, '=');
//			String key = d[0];
//			String val = d[1];
//			
//			if (key.equalsIgnoreCase("id")) {
//				idStr = val;
//			}
//			else if (key.equalsIgnoreCase("state")) {
//				if (val.equalsIgnoreCase("true")) {
//					checked = true;
//				}
//			}		
//		}
//		
//		List<Long> categories = searchInfo.getCategories();
//		if (categories != null) {
//			Long id = Long.parseLong(idStr);
//			if (checked)  {
//				if (!categories.contains(id)) {
//					categories.add(id);
//				}
//			} else {
//				if (categories.contains(id)) {
//					categories.remove(id);
//				}
//			}
//		}
//		
//		String json = searchInfo.toJson();
//		
//		byte[]   bytesEncoded = Base64.encodeBase64(json.getBytes());
//		String encoded = new String(bytesEncoded);
//		jsonPacket.put("encoded", encoded);
//
//       	return new JSONSerializer().serialize(jsonPacket);
//   	}

}
