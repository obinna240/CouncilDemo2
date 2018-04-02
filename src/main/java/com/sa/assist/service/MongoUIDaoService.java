package com.sa.assist.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.pcg.db.mongo.dao.AuthorityDAO;
import com.pcg.db.mongo.dao.CategoryDAO;
import com.pcg.db.mongo.dao.CategoryTypeDAO;
import com.pcg.db.mongo.dao.SequenceDAO;
import com.pcg.db.mongo.dao.SystemConfigDAO;



public class MongoUIDaoService {
	
//	@Autowired public VendorDAO vendorDAO;
//	@Autowired public ProductDAO productDAO;
//	@Autowired public ProductQuoteDAO productQuoteDAO;
//	@Autowired public OpenQuoteProductDAO openQuoteProductDAO;

	@Autowired public CategoryDAO categoryDAO;
//	@Autowired public VatCodeDAO vatCodeDAO;
//	@Autowired public DeliveryCostDAO deliveryCostDAO;
	@Autowired public AuthorityDAO authorityDAO;
//	@Autowired public UserDAO userDAO;
	
//	@Autowired public HelperAgeDAO helperAgeDAO;
//	@Autowired public HelperSexDAO helperSexDAO;
//	@Autowired public HelperEthnicityDAO helperEthnicityDAO;
//	@Autowired public HelperNeedDAO helperNeedDAO;
	
//	@Autowired public AreaDAO areaDAO;
	
	
//	@Autowired public VendorAttributesDAO vendorAttributesDAO;
	@Autowired public SequenceDAO sequenceDAO;
	
//	@Autowired public OrganisationTypeDAO organisationTypeDAO;
	
	@Autowired public SystemConfigDAO systemConfigDAO;
	
//	@Autowired public ContextDAO contextDAO;

//	@Autowired public VendorToAddressDAO vendorToAddressDAO;
	
//	@Autowired public CmsFormDAO cmsFormDAO; 
//	@Autowired public CmsItemDAO cmsItemDAO;
//	@Autowired public CmsFormLevelDAO cmsFormLevelDAO;
	
//	@Autowired public CmsUserFormDAO cmsUserFormDAO;
	@Autowired public CategoryTypeDAO categoryTypeDAO;
//	@Autowired public ManagedBedDAO managedBedDAO;
}
