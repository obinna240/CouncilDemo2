package com.pcg.search.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pcg.search.api.service.CareHomeObjectService;


@RestController
@RequestMapping("/careHome/**")
public class CareHomeController 
{
	@Autowired
	CareHomeObjectService careHomeObjectService;
	
	
	
}
