package com.pcg.db.mongo.dao;

import org.springframework.data.repository.CrudRepository;

import com.pcg.db.mongo.model.ScheduleLog;

public interface ScheduleLogDAO extends CrudRepository<ScheduleLog, Long>, ScheduleLogDAOCustom {
	
}