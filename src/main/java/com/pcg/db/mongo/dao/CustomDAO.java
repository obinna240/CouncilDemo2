package com.pcg.db.mongo.dao;

import java.io.Serializable;
import java.util.List;

public interface CustomDAO<T, ID extends Serializable> {
	public List<T> findAll();
	public List<T> findAllOrderBy(String order);
	public List<T> findEntries(int firstResult, int maxResults);
	public List<T> findEntries(int firstResult, int maxResults, String sortColumn);
	public List<T> findEntries(int firstResult, int maxResults, String sortColumn, boolean desc);
	public long count();
}
