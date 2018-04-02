package com.pcg.search.api.service;

import java.util.List;
import com.pcg.search.api.beans.CareHomeObject;

/**
 * 
 * @author oonyimadu
 *
 */
public interface CareHomeObjectService 
{
	 CareHomeObject findById(String id);
     
	 CareHomeObject findByName(String name);
	     
	 void saveCareHomeObject(CareHomeObject careHomeObject);
	 
	 void saveCareHomeObjects(List<CareHomeObject> careHomeObjects);
	     
	 void updateCareHomeObject(CareHomeObject careHomeObject);
	     
	 void deleteCareHomeObjectById(String id);
	 
	 void deleteCareHomeObjectsById(List<String> ids);
	 
	 List<CareHomeObject> findAllCareHomeObjects(); 
	     
	 void deleteAllCareHomeObjects();
	     
	 public boolean CareHomeExist(CareHomeObject careHomeObject);
}
