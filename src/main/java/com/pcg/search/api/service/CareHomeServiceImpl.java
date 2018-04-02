package com.pcg.search.api.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcg.search.api.beans.CareHomeObject;



/**
 * 
 * @author oonyimadu
 *
 */
@Service("careHomeService")
@Transactional
public class CareHomeServiceImpl implements CareHomeObjectService
{
	//replace with query
	private static List<CareHomeObject> careHome;
	
	static{
        careHome= populateDummyCareHomes();
    }
	
	
		
		/**
		 * 
		 * @return List<CareHome>
		 */
		private static List<CareHomeObject> populateDummyCareHomes()
		{
			/**
		        List<CareHomeObject> careHomes = new ArrayList<CareHomeObject>();
		        CareHomeObject c1 = new CareHomeObject("1", "31 Whitwell Road", "www.tqtwentyone.org", "Support for people with Autism, complex needs and challenging behaviour.", "374376 Winchester Road", "PO4 0QP", "12345678900","2016-01-01");
		        CareHomeObject c2 = new CareHomeObject("2", "58 Whichers Gate Road", "www.alexandra-rose.co.uk", "We accept HCC rates without a top up.", "374-376 Winchester Road", "PO4 3QP", "12345678900","2016-01-01");
		        CareHomeObject c3 = new CareHomeObject("3", "74 Central Road", "www.alverstokehouse.com", "Specialised care for young adults with learning difficulties and additional complex needs", "374-376 Winchester Road", "PO4 0QP", "12345678900","2016-01-01");
		        CareHomeObject c4 = new CareHomeObject("4", "Advance Housing and Support", "www.milkwoodcare.co.uk", "Pre admission assessment.", "105 Ashley Road", "PO4 2QP", "12345678900","2016-01-01");
		        CareHomeObject c5 = new CareHomeObject("5", "Applelea", "www.liaise.co.uk", "Continuing mental health care.", "374-377 Winchester Road", "PO4 1QP", "12345678910","2016-01-01");
	    			
		      careHomes.add(c1);
		      careHomes.add(c2);
		      careHomes.add(c3);
		      careHomes.add(c4);
		      careHomes.add(c5);
	      
		      return careHomes;
		      */
		      return null;
	    	}


		@Override
		public void saveCareHomeObject(CareHomeObject careHomeObject) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void saveCareHomeObjects(List<CareHomeObject> careHomeObjects) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void updateCareHomeObject(CareHomeObject careHomeObject) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void deleteCareHomeObjectById(String id) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void deleteCareHomeObjectsById(List<String> ids) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public List<CareHomeObject> findAllCareHomeObjects() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void deleteAllCareHomeObjects() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean CareHomeExist(CareHomeObject careHomeObject) {
			// TODO Auto-generated method stub
			return false;
		}


		@Override
		public CareHomeObject findById(String id) {
			// TODO Auto-generated method stub
			return null;
		}


		@Override
		public CareHomeObject findByName(String name) {
			// TODO Auto-generated method stub
			return null;
		}
	 
	}
