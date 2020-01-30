package com.jwt.authentication.authorization;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
	@Override
	public boolean hasPermission(Authentication authentication, Object accessType, Object permission) {
		if (authentication != null && accessType instanceof String) {
			if ("hasAccess".equalsIgnoreCase(String.valueOf(accessType))) {
				boolean hasAccess = validateAccess(String.valueOf(permission));
				return hasAccess;
			}
			return false;
		}
		return false;
	}
	private boolean validateAccess(String permission) {
		// ideally should be checked with user role, permission in database
		if ("READ".equalsIgnoreCase(permission)) {
			return true;
		}
		return false;
	}
//	@Override
//	public boolean hasPermission(Authentication authentication, Serializable serializable, String targetType,
//			Object permission) {
//		return false;
//	}
	
	//@PreAuthorize("hasRole('ADMIN') and hasPermission('hasAccess','WRITE')")
//}


//@Override
//public boolean hasPermission(
//  Authentication auth, Object targetDomainObject, Object permission) {
//    if ((auth == null) || (targetDomainObject == null) || !(permission instanceof String)){
//        return false;
//    }
//    String targetType = targetDomainObject.getClass().getSimpleName().toUpperCase();
//     
//    return hasPrivilege(auth, targetType, permission.toString().toUpperCase());
//}

@Override
public boolean hasPermission(
  Authentication auth, Serializable targetId, String targetType, Object permission) {
    if ((auth == null) || (targetType == null) || !(permission instanceof String)) {
        return false;
    }
    return hasPrivilege(auth, targetType.toUpperCase(), 
      permission.toString().toUpperCase());
}

private boolean hasPrivilege(Authentication auth, String targetType, String permission) {
    for (GrantedAuthority grantedAuth : auth.getAuthorities()) {
        if (grantedAuth.getAuthority().startsWith(targetType)) {
            if (grantedAuth.getAuthority().contains(permission)) {
                return true;
            }
        }
    }
    return false;
}
}
