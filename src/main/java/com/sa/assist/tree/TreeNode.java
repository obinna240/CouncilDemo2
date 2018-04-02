package com.sa.assist.tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pcg.db.mongo.dao.CategoryDAO;
import com.pcg.db.mongo.model.Category;

import flexjson.JSON;

public class TreeNode {

	private Long id;
	private String name;
	private String description;
	private String rootName;
	private String rootDescription;
	private Long resultsFound;
	private Integer depth;
	private List<TreeNode> children = new ArrayList<TreeNode>();
	private static Map<String, List<String>> nodeLinks = new HashMap<String, List<String>>();
	boolean selected;
	private String context;
	private Boolean keyProperty;
	private boolean showInSearchResults = true;
	private String searchLinkUrl;
	private boolean facetable = true;
	private Integer displayOrder = null;
	private Integer rootDisplayOrder = null;

	
	private CategoryDAO categoryDAO;

	public TreeNode() {
		
	}
	
	public TreeNode(CategoryDAO categoryDAO) {
		this.categoryDAO = categoryDAO;
	}
	
	
	public void loadDBCategoryTree(Category categoryRoot) {

		List<Category> categories = categoryDAO.findAllChildren(categoryRoot, false);
		
		for (Category category : categories) {
			Category parentCat = categoryDAO.findOne(category.getCategoryParentId());
			if (parentCat != null && parentCat.equals(categoryRoot)){
				TreeNode topLevelNode = makeCategoryNode(category); 
				addChildNodes(category, topLevelNode);
				children.add(topLevelNode);
				
				ArrayList<String> nodeList = new ArrayList<String>();
				nodeList.add(getNodeLink(topLevelNode));
				nodeLinks.put(topLevelNode.getId().toString(), nodeList);
			}
		}
		
	}	
		
	private String getNodeLink(TreeNode node){
		return "<a href=\"" + node.getContext() + "/cat?cat=" + node.getId() + "\">" + node.getName() + "</a>";
	}
		
	private void addChildNodes(Category category, TreeNode topLevelNode) {
		
		List<Category> childCats = categoryDAO.getChildCategories(category);
		
		for (Category childCat : childCats) {
			TreeNode childNode = makeCategoryNode(childCat);
			topLevelNode.getChildren().add(childNode);

			ArrayList<String> nodeList = new ArrayList<String>();
			nodeList.add(getNodeLink(topLevelNode));
			nodeList.add(getNodeLink(childNode));
			nodeLinks.put(childNode.getId().toString(), nodeList);
		}
	}

	TreeNode makeCategoryNode(Category category) {
		TreeNode n = new TreeNode();
		n.setId(category.getId());
		n.setName(category.getName());
		n.setDescription(category.getDescription());
		n.setContext(context);
		n.setKeyProperty(category.isKeyProperty());
		n.setFacetable(category.isFacetable());
		return n;		
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
	@JSON
	public List<TreeNode> getChildren() {
		return children;
	}
	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}
	@JSON(include=false)
	public Long getResultsFound() {
		return resultsFound;
	}
	public void setResultsFound(Long resultsFound) {
		this.resultsFound = resultsFound;
	}
	@JSON(include=false)
	public Integer getDepth() {
		return depth;
	}
	public void setDepth(Integer depth) {
		this.depth = depth;
	}
	@JSON(include=false)
	public boolean getSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	@JSON(include=false)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public static List<String> getNodePath(String categoryID){
		return nodeLinks.get(categoryID);
	}

	
	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	@JSON(include=false)
	public String getRootName() {
		return rootName;
	}
	
	public void setRootName(String rootName) {
		this.rootName = rootName;
	}

	
	@JSON(include=false)
	public String getRootDescription() {
		return rootDescription;
	}

	public void setRootDescription(String rootDescription) {
		this.rootDescription = rootDescription;
	}
	
	public Boolean getKeyProperty() {
		return keyProperty;
	}

	public void setKeyProperty(Boolean keyProperty) {
		this.keyProperty = keyProperty;
	}
	
	public boolean isShowInSearchResults() {
		return showInSearchResults;
	}

	public void setShowInSearchResults(boolean showInSearchResults) {
		this.showInSearchResults = showInSearchResults;
	}

	public String getSearchLinkUrl() {
		return searchLinkUrl;
	}

	public void setSearchLinkUrl(String searchLinkUrl) {
		this.searchLinkUrl = searchLinkUrl;
	}

	public boolean isFacetable() {
		return facetable;
	}

	public void setFacetable(boolean facetable) {
		this.facetable = facetable;
	}

	// comparator
	public static Comparator<TreeNode> getComparator(SortParameter... sortParameters) {
        return new TreeNodeComparator(sortParameters);
    }

	 public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public Integer getRootDisplayOrder() {
		return rootDisplayOrder;
	}

	public void setRootDisplayOrder(Integer rootDisplayOrder) {
		this.rootDisplayOrder = rootDisplayOrder;
	}

	public enum SortParameter {
		 KEYPROPERTY, ROOTNAME_ASCENDING, ROOTNAME_DESCENDING, NAME_ASCENDING, NAME_DESCENDING, RESULTS_COUNT, ID_ASCENDING, ROOT_DISPLAYORDER
	 }

    private static class TreeNodeComparator implements Comparator<TreeNode> {
        private SortParameter[] parameters;

        private TreeNodeComparator(SortParameter[] parameters) {
            this.parameters = parameters;
        }

        public int compare(TreeNode o1, TreeNode o2) {
            int comparison;
            
            Integer d1 = o1.depth;
            Integer d2 = o2.depth;
            if (d1 != null && d2 != null && d1.intValue() == d2.intValue()) {
             
	            for (SortParameter parameter : parameters) {
	                switch (parameter) {
	                case ID_ASCENDING:
	                	if (o1.id != null && o2.id != null) {
	                		comparison = o1.id.compareTo(o2.id);
	                		if (comparison != 0) return comparison;
	                	}
	                    break;
	                	case KEYPROPERTY:
		                	if (o1.keyProperty != null && o2.keyProperty != null) {
		                		comparison = o2.keyProperty.compareTo(o1.keyProperty);
		                		if (comparison != 0) return comparison;
		                	}
		                    break;
	                    
	                    case ROOTNAME_ASCENDING:
	                    	if (o1.rootName != null && o2.rootName != null) {
	                    		comparison = o1.rootName.toUpperCase().compareTo(o2.rootName.toUpperCase());
	                    		if (comparison != 0) return comparison;
	                    	}
	                        break;
	                        
	                    case ROOTNAME_DESCENDING:
	                    	if (o1.rootName != null && o2.rootName != null) {
	                    		comparison = o2.rootName.toUpperCase().compareTo(o1.rootName.toUpperCase());
	                    		if (comparison != 0) return comparison;
	                    	}
	                        break;
	                    case NAME_ASCENDING:
	                    	if (o1.name != null && o2.name != null) {
		                    	comparison = o1.name.toUpperCase().compareTo(o2.name.toUpperCase());
		                        if (comparison != 0) return comparison;
	                    	}
	                        break;
	                        
	                    case NAME_DESCENDING:
	                    	if (o1.name != null && o2.name != null) {
		                        comparison = o2.name.toUpperCase().compareTo(o1.name.toUpperCase());
		                        if (comparison != 0) return comparison;
	                    	}
	                        break;
	                        
	                    case RESULTS_COUNT:
		                	if (o1.resultsFound != null && o2.resultsFound != null) {
		                		comparison = o2.resultsFound.compareTo(o1.resultsFound);
		                		if (comparison != 0) return comparison;
		                	}
		                    break;
		                    
	                    case ROOT_DISPLAYORDER:
	                    	//Order based on a root node displayorder integer which may or may not be present in the db
	                    	//The lower the number, the higher up the list, and any node with a number will be above one without one 
		                	if (o1.rootDisplayOrder != null || o2.rootDisplayOrder != null) {
		                		Integer order1 = o1.rootDisplayOrder == null? Integer.MAX_VALUE : o1.rootDisplayOrder;  
		                		Integer order2 = o2.rootDisplayOrder == null? Integer.MAX_VALUE : o2.rootDisplayOrder;
		                		return order1.compareTo(order2);
		                	}
		                    break;		                    
	                }
	            }
            }
            return 0;
        }
    }
	
	
}
