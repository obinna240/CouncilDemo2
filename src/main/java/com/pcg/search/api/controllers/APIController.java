package com.pcg.search.api.controllers;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pcg.search.api.beans.APIPingResponse;

@RestController
//@RequestMapping("/api/**")
public class APIController {
	
    private final AtomicLong counter = new AtomicLong();
	
    @RequestMapping("/ping")
    public APIPingResponse ping(@RequestParam(value="name", defaultValue="World") String name) {
        return new APIPingResponse(counter.incrementAndGet(), String.format("Hello, %s!", name));
    }
    
}
