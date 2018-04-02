package com.sa.assist.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.pcg.search.index.IndexManager;

@RequestMapping("/searchindex/**")
@Controller
public class SearchIndexController {

	@Autowired private IndexManager indexManager;
//	@Autowired private CmsMarkupDAO cmsMarkupDAO;

	private static Log m_log = LogFactory.getLog(SearchIndexController.class);
//
//
	private String section = "Search Index";
//
//	@RequestMapping(value = "/products/all", method = RequestMethod.GET)
//	public String indexAllProducts(Model model, HttpSession session, HttpServletRequest request) {
//		indexManager.indexAllProducts();
//		indexManager.indexAllClassifiedAds();
//
//		String pageTitle = "pt_searchindex";
//		createDisplayBean(model,section,pageTitle);
//
//
//		return "searchindex/index";
//	}
//
//	@RequestMapping(value = "/products/{productId}", method = RequestMethod.GET)
//	public String indexProducts(@PathVariable("productId") Long productId, Model model, HttpSession session, HttpServletRequest request) {
//		indexManager.indexProduct(productId, false);
//
//		String pageTitle = "pt_searchindex";
//		createDisplayBean(model,section,pageTitle);
//
//		return "searchindex/index";
//	}
//
//	@RequestMapping(value = "/classified/{classifiedadId}", method = RequestMethod.GET)
//	public String indexClassifiedAds(@PathVariable("classifiedadId") Long classifiedAdId, Model model, HttpSession session, HttpServletRequest request) {
//		indexManager.indexClassifiedAd(classifiedAdId);
//
//		String pageTitle = "pt_searchindex";
//		createDisplayBean(model,section,pageTitle);
//
//		return "searchindex/index";
//	}
//
//	@RequestMapping(value = "/classified/all", method = RequestMethod.GET)
//	public String indexAllClassifiedAds(@PathVariable("classifiedadId") Long classifiedAdId, Model model, HttpSession session, HttpServletRequest request) {
//		indexManager.indexAllClassifiedAds(true);
//
//		String pageTitle = "pt_searchindex";
//		createDisplayBean(model,section,pageTitle);
//
//		return "searchindex/index";
//	}
//
//	@RequestMapping(value = "/vendors/all", method = RequestMethod.GET)
//	public String indexAllVendors(Model model, HttpSession session, HttpServletRequest request) {
//		indexManager.indexAllVendors(true);
//
//		String pageTitle = "pt_searchindex";
//		createDisplayBean(model,section,pageTitle);
//
//		return "searchindex/index";
//	}
//
//	@RequestMapping(value = "/vendors/{vendorId}", method = RequestMethod.GET)
//	public String indexVendor(@PathVariable("vendorId") Long vendorId, Model model, HttpSession session, HttpServletRequest request) {
//		indexManager.indexVendor(vendorId, true);
//
//		String pageTitle = "pt_searchindex";
//		createDisplayBean(model,section,pageTitle);
//
//		return "searchindex/index";
//	}
//
//	@RequestMapping(value = "/vendors/type/{vendorTypeId}", method = RequestMethod.GET)
//	public String indexVendorType(@PathVariable("vendorTypeId") Long vendorTypeId, Model model, HttpSession session, HttpServletRequest request) {
//
//		VendorType vendorType = VendorType.findById(vendorTypeId);
//		if (vendorType != null) {
//			indexManager.indexVendorsByType(vendorType);
//		}
//
//		String pageTitle = "pt_searchindex";
//		createDisplayBean(model,section,pageTitle);
//
//		return "searchindex/index";
//	}
//
//	@RequestMapping(value = "/vendors/{vendorId}/products", method = RequestMethod.GET)
//	public String indexVendorProducts(@PathVariable("vendorId") Long vendorId, Model model, HttpSession session, HttpServletRequest request) {
//		indexManager.indexProductsForVendor(vendorId, true);
//
//		String pageTitle = "pt_searchindex";
//		createDisplayBean(model, section, pageTitle);
//		return "searchindex/index";
//	}
//
//	public void createDisplayBean(Model map, String section, String pageTitle) {
//		DisplayBean display = new DisplayBean(section, pageTitle);
//		setCsmMarkup(display);
//		map.addAttribute("_display", display);
//	}
//
//	private void setCsmMarkup(DisplayBean display) {
//		display.setMegaMenu(cmsMarkupDAO.getMegaMenu().getMarkup());
//		display.setFooter(cmsMarkupDAO.getFooter().getMarkup());
//	}
//
//	/*@RequestMapping(value = "/smartsuggest/cats", method = RequestMethod.GET)
//    public String indexAllCats(Model model, HttpSession session, HttpServletRequest request) {
//		indexManager.indexSmartSuggestCategories();
//
//		String pageTitle = "pt_searchindex";
// 		createDisplayBean(model,section,pageTitle);
//		return "searchindex/index";
//	}*/
//
//	@RequestMapping(value = "/all", method = RequestMethod.GET)
//	public String indexAll(Model model, HttpSession session, HttpServletRequest request) {
//		indexManager.indexAllVendors();
//		indexManager.indexAllClassifiedAds();
//		indexManager.indexAllCms();
//
//		String pageTitle = "pt_searchindex";
//		createDisplayBean(model,section,pageTitle);
//		m_log.debug("Reindex all Products,Vendors,CMS Content");
//
//		return "searchindex/index";
//	}


	@RequestMapping(value = "/CMS/all", method = RequestMethod.GET)
	public String indexAllCMS(Model model, HttpSession session, HttpServletRequest request) {

		indexManager.indexAllCms();

		String pageTitle = "pt_searchindex";
//		createDisplayBean(model,section,pageTitle);


		return "searchindex/index";
	}
}
