package com.pcg.db.mongo.dao;


public interface SequenceDAO {
	Long getNextId(Class objClass);
	void resetId(Class objClass);
}
