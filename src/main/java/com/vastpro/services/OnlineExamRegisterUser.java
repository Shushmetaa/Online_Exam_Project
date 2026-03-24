package com.vastpro.services;

import java.util.Map;

import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

public class OnlineExamRegisterUser {
	
public static Map<String, Object> registerUser(DispatchContext dctx, Map<String, ? extends Object> context){
		
	String firstName = (String) context.get("firstName");
	String lastName = (String) context.get("lastName");
	String email = (String) context.get("email");
	String phoneNumber = (String) context.get("phoneNumber");
	String password = (String) context.get("password");	
	//validation
	if(firstName == null || firstName.isEmpty()) {
		return ServiceUtil.returnError("First name is required");
	}
	
	if(lastName == null || lastName.isEmpty()) {
		return ServiceUtil.returnError("Last name is required");
	}
	
	if(email == null || email.isEmpty()) {
	    return ServiceUtil.returnError("Email is required");
	}
	if(!email.contains("@")) {
	    return ServiceUtil.returnError("Invalid email format");
	}
	
	if(phoneNumber == null || phoneNumber.isEmpty()) {
		return ServiceUtil.returnError("Phone number is required");
	}
	
	if(password == null || password.isEmpty()) {
		return ServiceUtil.returnError("Password is required");
	}
	
	Delegator delegator = dctx.getDelegator();
	
	try {
		
		//checking email exists
		GenericValue existingUser = EntityQuery.use(delegator)
				                               .from("UserLogin")
				                               .where("userLoginId", email)
				                               .queryOne();
		
		if(existingUser != null) {
			return ServiceUtil.returnError("User already exists");
		}
		
		//create party record
		String partyId = delegator.getNextSeqId("Party");
		
		GenericValue party = delegator.makeValue("Party");
		party.set("partyId", partyId);
		party.set("partyTypeId", "PERSON");
		party.set("statusId", "PARTY_ENABLED");
		delegator.create(party);
		
		//create person record
		GenericValue person = delegator.makeValue("Person");
		person.set("partyId", partyId);
		person.set("firstName", firstName);
		person.set("lastName", lastName);
		delegator.create(person);
		
		//create userLogin record
		GenericValue userLogin = delegator.makeValue("UserLogin");
		userLogin.set("userLoginId", email);
		userLogin.set("partyId", partyId);
		userLogin.set("currentPassword", password);
		userLogin.set("enabled", "Y");
		userLogin.set("hasLoggedOut", "N");
		delegator.create(userLogin);
		
		//create contact for phone
		String contactMechId = delegator.getNextSeqId("ContactMech");
		
		GenericValue contactMech = delegator.makeValue("ContactMech");
		contactMech.set("contactMechId", contactMechId);
		contactMech.set("contactMechTypeId", "TELECOM_NUMBER");
		contactMech.set("infoString", phoneNumber);
		delegator.create(contactMech);
		
		//link party table with contactMech
		GenericValue partyContactMech = delegator.makeValue("PartyContactMech");
		partyContactMech.set("partyId", partyId);
		partyContactMech.set("contactMechId", contactMechId);
		partyContactMech.set("fromDate", UtilDateTime.nowTimestamp());
		delegator.create(partyContactMech);
		
		//assign role
		GenericValue partyRole = delegator.makeValue("PartyRole");
		partyRole.set("partyId", partyId);
		partyRole.set("roleTypeId", "EXAM_USER");
		delegator.create(partyRole);
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("partyId", partyId);
		return result;
				
	}catch(GenericEntityException e) {
		return ServiceUtil.returnError("Error creating user: " + e.getMessage());
	}
	
}

}

