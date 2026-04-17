package com.vastpro.services.usermaster;

import java.util.Map;

import org.apache.ofbiz.base.crypto.HashCrypt;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

public class LoginUser {

	public static Map<String, Object> loginUser(DispatchContext dctx, Map<String, ? extends Object> context) {

		String email = (String) context.get("email");
		String password = (String) context.get("password");

		// Validation
		if (UtilValidate.isEmpty(email))
			return ServiceUtil.returnError("Email is required");

		if (!UtilValidate.isEmail(email))
		    return ServiceUtil.returnError("Invalid email format. Eg: john@gmail.com");

		if (UtilValidate.isEmpty(password))
		    return ServiceUtil.returnError("Password is required");

		if (!password.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}"))
			return ServiceUtil.returnError("Invalid password. Eg: John@123");

		Delegator delegator = dctx.getDelegator();

		try {
			// Step 1: Fetch UserLogin by email
			GenericValue userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", email)
					.queryOne();

			if (userLogin == null)
				return ServiceUtil.returnError("User not found");

			// Step 2: Check account enabled
			String enabled = userLogin.getString("enabled");
			if ("N".equals(enabled))
				return ServiceUtil.returnError("Account is disabled");

			// Step 3: Compare password
			String storedHashedPassword = userLogin.getString("currentPassword");
			boolean isMatch = HashCrypt.comparePassword(storedHashedPassword, "SHA", password);

			if (!isMatch)
				return ServiceUtil.returnError("Invalid password");

			// Step 4: Fetch Person details using partyId
			String partyId = userLogin.getString("partyId");

			GenericValue roleType = EntityQuery.use(delegator).from("PartyRole").where("partyId", partyId).queryFirst();

			// Step 5: Return success with user info
			Map<String, Object> result = ServiceUtil.returnSuccess("Login successful");
			result.put("partyId", partyId);
			result.put("email", email);
			result.put("role", roleType.getString("roleTypeId"));
			return result;

		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("Error during login: " + e.getMessage());
		}
	}
}
