package com.jwt.authentication.authorization;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

//@Component
public class SecurityService {
	
	public boolean hasAccess(String data){
	
		return true;
	}

	//@PreAuthorize("@SecurityService.hasAccess("suan")")
}
