package com.vastpro.servicecall;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class SignupMaster {

    private static LocalDispatcher getDispatcher(HttpServletRequest request) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        if (dispatcher == null) {
            dispatcher = (LocalDispatcher) request.getSession()
                    .getServletContext().getAttribute("dispatcher");
        }
        return dispatcher;
    }

    private static Delegator getDelegator(HttpServletRequest request) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        if (delegator == null) {
            delegator = (Delegator) request.getSession()
                    .getServletContext().getAttribute("delegator");
        }
        return delegator;
    }

    public static Map<String, Object> signupUser(HttpServletRequest request,
                                                  HttpServletResponse response) {

        // Get delegator and dispatcher using your existing helper methods
        Delegator delegator   = getDelegator(request);
        LocalDispatcher dispatcher = getDispatcher(request);

        String userName  = request.getParameter("userName");
        String password  = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String firstName = request.getParameter("firstName");
        String lastName  = request.getParameter("lastName");
        String role = request.getParameter("roleTypeId");
        
        if (UtilValidate.isEmpty(firstName)) {
            return ServiceUtil.returnError("First name is required");
        }
        if (!firstName.matches("[a-zA-Z\\s\\-']{3,50}")) {
            return ServiceUtil.returnError("Invalid first name. Eg: John");
        }

        // Last Name
        if (UtilValidate.isEmpty(lastName)) {
            return ServiceUtil.returnError("Last name is required");
        }
        if (!lastName.matches("[a-zA-Z\\s\\-']{1,50}")) {
            return ServiceUtil.returnError("Invalid last name. Eg: Doe");
        }

        // Username
        if (UtilValidate.isEmpty(userName)) {
            return ServiceUtil.returnError("Username is required");
        }

        // Password
        if (UtilValidate.isEmpty(password)) {
            return ServiceUtil.returnError("Password is required");
        }
        if (!password.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}")) {
            return ServiceUtil.returnError("Invalid password. Eg: John@123");
        }

        // Confirm Password
        if (UtilValidate.isEmpty(confirmPassword)) {
            return ServiceUtil.returnError("Confirm password is required");
        }
        if (!password.equals(confirmPassword)) {
            return ServiceUtil.returnError("Password and confirm password do not match");
        }

        // Role
        if (UtilValidate.isEmpty(role))
            return ServiceUtil.returnError("Role is required");
        if (!role.equals("SPHINX_ADMIN") && !role.equals("SPHINX_USER"))
            return ServiceUtil.returnError("Invalid role!");


        try {
        	
        	GenericValue existingUser = EntityQuery.use(delegator)
                    .from("UserLogin")
                    .where("userLoginId", userName)
                    .queryOne();

            if (existingUser != null) {
                return ServiceUtil.returnError("Username already exists!");
            }
            
            // Get system userLogin to authorize all service calls
            GenericValue systemUserLogin = delegator.findOne("UserLogin",
                    UtilMisc.toMap("userLoginId", "system"), false);

            // ── Step 1: Create Person + Party ──────────────────────
            Map<String, Object> personMap = new HashMap<>();
            personMap.put("firstName", firstName);
            personMap.put("lastName", lastName);
            personMap.put("userLogin", systemUserLogin);

            Map<String, Object> personResult = dispatcher.runSync(
                    "createPerson", personMap);
            if (ServiceUtil.isError(personResult)) {
                return ServiceUtil.returnError(
                        ServiceUtil.getErrorMessage(personResult));
            }

            String partyId = (String) personResult.get("partyId");

            // ── Step 2: Assign Party Role ───────────────────────────
            String partyRoleId = "SPHINX_ADMIN".equalsIgnoreCase(role) 
                    ? "sphinx_admin" : "sphinx_user";

            Map<String, Object> partyRoleMap = new HashMap<>();
            partyRoleMap.put("partyId", partyId);
            partyRoleMap.put("roleTypeId", partyRoleId);
            partyRoleMap.put("userLogin", systemUserLogin);

            Map<String, Object> partyRoleResult = dispatcher.runSync(
                    "createPartyRole", partyRoleMap);
            if (ServiceUtil.isError(partyRoleResult)) {
                return ServiceUtil.returnError(
                        ServiceUtil.getErrorMessage(partyRoleResult));
            }

            // ── Step 3: Create UserLogin ────────────────────────────
            Map<String, Object> userLoginMap = new HashMap<>();
            userLoginMap.put("userLoginId", userName);
            userLoginMap.put("currentPassword", password);
            userLoginMap.put("currentPasswordVerify", password);
            userLoginMap.put("requirePasswordChange", "N");
            userLoginMap.put("enabled", "Y");
            userLoginMap.put("partyId", partyId);
            userLoginMap.put("userLogin", systemUserLogin);

            Map<String, Object> userLoginResult = dispatcher.runSync(
                    "createUserLogin", userLoginMap);
            if (ServiceUtil.isError(userLoginResult)) {
                return ServiceUtil.returnError(
                        ServiceUtil.getErrorMessage(userLoginResult));
            }

            // ── Step 4: Assign Security Group ──────────────────────
            String securityGroupId = "SPHINX_ADMIN".equalsIgnoreCase(role)
                    ? "SPHINX_ADMIN" : "SPHINX_USER";

            Map<String, Object> secGroupMap = new HashMap<>();
            secGroupMap.put("userLoginId", userName);
            secGroupMap.put("groupId", securityGroupId);
            secGroupMap.put("fromDate", UtilDateTime.nowTimestamp());
            secGroupMap.put("userLogin", systemUserLogin);

            Map<String, Object> secResult = dispatcher.runSync(
                    "addUserLoginToSecurityGroup", secGroupMap);
            if (ServiceUtil.isError(secResult)) {
                return ServiceUtil.returnError(
                        ServiceUtil.getErrorMessage(secResult));
            }

            // ── Step 5: Return success ──────────────────────────────
            Map<String, Object> successResult = ServiceUtil.returnSuccess(
                    "User registered successfully!");
            successResult.put("partyId", partyId);
            successResult.put("userLoginId", userName);
            successResult.put("role", role);
            return successResult;

        } catch (Exception e) {
            return ServiceUtil.returnError(e.getMessage());
        }
    }
}