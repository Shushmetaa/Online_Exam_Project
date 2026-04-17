package com.vastpro.services.usermaster;

import java.util.HashMap;
import java.util.Map;

import org.apache.ofbiz.base.crypto.HashCrypt;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class SignupUser {
	
public static Map<String, Object> signupUser(DispatchContext dctx, Map<String, ? extends Object> context){
		
	String firstName = (String) context.get("firstName");
	String lastName = (String) context.get("lastName");
	String email = (String) context.get("email");
	String password = (String) context.get("password");	
	String confirmPassword = (String) context.get("confirmPassword");	
	String role=(String) context.get("roleTypeId");
	
	// First Name
    if (UtilValidate.isEmpty(firstName))
        return ServiceUtil.returnError("First name is required");

    if (!firstName.matches("[a-zA-Z\\s\\-']{3,50}"))
        return ServiceUtil.returnError("Invalid first name. Eg: John");

    // Last Name
    if (UtilValidate.isEmpty(lastName))
        return ServiceUtil.returnError("Last name is required");

    if (!lastName.matches("[a-zA-Z\\s\\-']{1,50}"))
        return ServiceUtil.returnError("Invalid last name. Eg: Doe");

    // Email
    if (UtilValidate.isEmpty(email))
        return ServiceUtil.returnError("Email is required");

    if (!UtilValidate.isEmail(email))
        return ServiceUtil.returnError("Invalid email format. Eg: john@gmail.com");

    // Password
    if (UtilValidate.isEmpty(password))
        return ServiceUtil.returnError("Password is required");

    // OFBIZ has no password validator custom regex needed
    if (!password.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}"))
        return ServiceUtil.returnError("Invalid password. Eg: John@123");

    // Confirm Password
    if (UtilValidate.isEmpty(confirmPassword))
        return ServiceUtil.returnError("Confirm password is required");

    if (!password.equals(confirmPassword))
        return ServiceUtil.returnError("Password and confirm password do not match");
    
	Delegator delegator = dctx.getDelegator();
	LocalDispatcher dispatcher = dctx.getDispatcher();
	String hashedPassword = HashCrypt.cryptUTF8("SHA", null, password);
	try {
		
		//checking email exists
		GenericValue existingUser = EntityQuery.use(delegator)
				                               .from("UserLogin")
				                               .where("userLoginId", email)
				                               .queryOne();
		
		if(existingUser != null) {
			return ServiceUtil.returnError("User already exists");
		}
		// Step 1: Create Party
	    Map<String, Object> partyContext = new HashMap<>();
	    partyContext.put("partyTypeId", "PERSON");
	    partyContext.put("userLogin", context.get("userLogin"));
	    Map<String, Object> partyResult = dispatcher.runSync("signupUserParty", partyContext);
	    
	    if (ServiceUtil.isError(partyResult))
            return ServiceUtil.returnError("Party creation failed: " + ServiceUtil.getErrorMessage(partyResult));
        String partyId = (String) partyResult.get("partyId");
        if (partyId == null)
            return ServiceUtil.returnError("partyId is null after party creation");

	    // Step 2: Create Person
	    Map<String, Object> personContext = new HashMap<>();
	    personContext.put("partyId", partyId);
	    personContext.put("firstName", firstName);
	    personContext.put("lastName", lastName);
	    personContext.put("userLogin", context.get("userLogin"));
	    dispatcher.runSync("signupUserPerson", personContext);

	    // Step 3: Create UserLogin
	    Map<String, Object> loginContext = new HashMap<>();
	    loginContext.put("userLoginId", email);
	    loginContext.put("currentPassword", hashedPassword);
	    loginContext.put("partyId", partyId);
	    loginContext.put("enabled", "Y");
	    loginContext.put("userLogin", context.get("userLogin"));
	    dispatcher.runSync("signupUserLogin", loginContext);
		
	    //create role
	    Map<String, Object> partyRoleInput = new HashMap<>();
		partyRoleInput.put("partyId", partyId);
		partyRoleInput.put("roleTypeId", role);
		dispatcher.runSync("createPartyRole", partyRoleInput);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		return result;
				
	}catch(GenericEntityException | GenericServiceException e) {
		return ServiceUtil.returnError("Error creating user: " + e.getMessage());
	}
	
}

}

