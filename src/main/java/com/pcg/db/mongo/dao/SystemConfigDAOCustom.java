package com.pcg.db.mongo.dao;

import com.pcg.db.mongo.model.SystemConfig;

public interface SystemConfigDAOCustom extends CustomDAO<SystemConfig, Long>{
	public SystemConfig getDefaultSystemConfig();
	public SystemConfig getWritableDefaultSystemConfig();
	//Get SystemConfig based on the contextId for MultiContext scenario
	public SystemConfig getSystemConfigForContext(String context);
}
