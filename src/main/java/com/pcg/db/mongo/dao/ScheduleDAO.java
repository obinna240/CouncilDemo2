package com.pcg.db.mongo.dao;

import org.springframework.data.repository.CrudRepository;

import com.pcg.db.mongo.model.Schedule;

public interface ScheduleDAO extends CrudRepository<Schedule, Long>, ScheduleDAOCustom {
	
}