package com.sa.assist.controller.api.cms;

//@RequestMapping("/assistapi/cms/index/**")
//@Controller
public class IndexController {
	
//	@Autowired private IndexManager indexManager;
//	
//	private static Log m_log = LogFactory.getLog(IndexController.class);
//	
//	private static Map<String, JSONSerializer> serializers;
//	static {
//		serializers = new HashMap<String, JSONSerializer>();
//		serializers.put("DEFAULT", new JSONSerializer().exclude("*.class"));
//	}
//
//	@RequestMapping(method=RequestMethod.POST, value = "/add")
//	public @ResponseBody String addCmsDocumentToIndexPost(@RequestBody String json) {
//		
//		try {
//			CmsDocument doc = new JSONDeserializer<CmsDocument>().use(null, CmsDocument.class).deserialize(json);
//			
//			indexManager.indexCmsDocument(doc);
//			
//			AjaxResult ar = new AjaxResult(AjaxStatus.OK, "Document added to solr index");
//			return serializers.get("DEFAULT").serialize(ar);
//			
//		} catch (Exception e) {
//			m_log.error(e.getMessage());
//			AjaxResult ar = new AjaxResult(AjaxStatus.ERROR, "Error adding document to solr index");
//			return serializers.get("DEFAULT").serialize(ar);
//		}
// 
//	}
//	
//	@RequestMapping(value = "/remove")
//	public @ResponseBody String RemoveCmsDocumentToIndexPost(@RequestBody String json) {
//		
//		try {
//			CmsDocument doc = new JSONDeserializer<CmsDocument>().use(null, CmsDocument.class).deserialize(json);
//			
//			indexManager.removeCmsContent(doc);
//			
//			AjaxResult ar = new AjaxResult(AjaxStatus.OK, "Document removed from solr index");
//			return serializers.get("DEFAULT").serialize(ar);
//			
//		} catch (Exception e) {
//			m_log.error(e.getMessage(), e);
//			AjaxResult ar = new AjaxResult(AjaxStatus.ERROR, "Error removing document from solr index");
//			return serializers.get("DEFAULT").serialize(ar);
//		}
// 
//	}
}
