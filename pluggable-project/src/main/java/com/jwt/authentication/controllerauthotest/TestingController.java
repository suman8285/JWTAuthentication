package com.jwt.authentication.controllerauthotest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("landing/")
public class TestingController {
	
	private static final Logger logger = LoggerFactory.getLogger(TestingController.class);
	
	
	//@PreAuthorize("@SecurityService.hasAccess(1)")
	//@PreAuthorize("hasRole('ADMIN') and hasPermission('hasAccess','WRITE')")
	@PreAuthorize("hasPermission('hasAccess','READ')")
	 @RequestMapping(value="load-user-application-details/{userID}", method=RequestMethod.GET)
	    public List<String> getApplicationsByUserID(@PathVariable String userID){
		 logger.info("inside the getapplicationByUserId");
		 return null;
	    }

}
