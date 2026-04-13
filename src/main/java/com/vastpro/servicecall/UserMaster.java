package com.vastpro.servicecall;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class UserMaster {
	 private static LocalDispatcher getDispatcher(HttpServletRequest request) {
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        if (dispatcher == null)
	            dispatcher = (LocalDispatcher) request.getSession()
	                            .getServletContext().getAttribute("dispatcher");
	        return dispatcher;
	    }

	    private static Delegator getDelegator(HttpServletRequest request) {
	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        if (delegator == null)
	            delegator = (Delegator) request.getSession()
	                            .getServletContext().getAttribute("delegator");
	        return delegator;
	    }
	    public static Map<String, Object> getAssignedExams(
	            HttpServletRequest request, HttpServletResponse response) {
	        try {
	            LocalDispatcher dispatcher = getDispatcher(request);
	            Delegator delegator        = getDelegator(request);

	            // Get partyId from session (set during login)
	            String partyId = (String) request.getSession().getAttribute("partyId");
	            if (partyId == null)
	                return ServiceUtil.returnError("User not logged in.");

	            GenericValue userLogin = EntityQuery.use(delegator)
	                    .from("UserLogin").where("userLoginId", "admin").queryOne();

	            Map<String, Object> data = new HashMap<>();
	            data.put("partyId",   partyId);
	            data.put("userLogin", userLogin);

	            Map<String, Object> result = dispatcher.runSync("getAssignedExams", data);
	            if (ServiceUtil.isError(result))
	                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));

	            Map<String, Object> resp = ServiceUtil.returnSuccess();
	            resp.put("examList", result.get("examList"));
	            return resp;

	        } catch (Exception e) {
	            return ServiceUtil.returnError("Error: " + e.getMessage());
	        }
	    }
	    public static Map<String, Object> getUserStats(
	            HttpServletRequest request, HttpServletResponse response) {
	        try {
	            LocalDispatcher dispatcher = getDispatcher(request);
	            Delegator delegator        = getDelegator(request);

	            String partyId = (String) request.getSession().getAttribute("partyId");
	            if (partyId == null)
	                return ServiceUtil.returnError("User not logged in.");

	            GenericValue userLogin = EntityQuery.use(delegator)
	                    .from("UserLogin").where("userLoginId", "admin").queryOne();

	            Map<String, Object> data = new HashMap<>();
	            data.put("partyId",   partyId);
	            data.put("userLogin", userLogin);

	            Map<String, Object> result = dispatcher.runSync("getUserExamStats", data);
	            if (ServiceUtil.isError(result))
	                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));

	            Map<String, Object> resp = ServiceUtil.returnSuccess();
	            resp.put("completed",  result.get("completed"));
	            resp.put("bestScore",  result.get("bestScore"));
	            return resp;

	        } catch (Exception e) {
	            return ServiceUtil.returnError("Error: " + e.getMessage());
	        }
	    }
	    public static Map<String, Object> login(
	            HttpServletRequest request, HttpServletResponse response) {
	        try {
	            LocalDispatcher dispatcher = getDispatcher(request);
	            Delegator delegator        = getDelegator(request);

	            String email    = request.getParameter("email");
	            String password = request.getParameter("password");

	            if (email == null || password == null)
	                return ServiceUtil.returnError("Email and password are required.");

	            // Find UserLogin by email (stored in Party/ContactMech or directly)
	            GenericValue userLogin = EntityQuery.use(delegator)
	                    .from("UserLogin")
	                    .where("userLoginId", email)
	                    .queryOne();

	            if (userLogin == null)
	                return ServiceUtil.returnError("Invalid credentials.");

	            // Verify password via OFBiz login service
	            GenericValue adminLogin = EntityQuery.use(delegator)
	                    .from("UserLogin").where("userLoginId", "admin").queryOne();

	            Map<String, Object> data = new HashMap<>();
	            data.put("login.username", email);
	            data.put("login.password", password);
	            data.put("userLogin",      adminLogin);

	            Map<String, Object> result = dispatcher.runSync("userLogin", data);
	            if (ServiceUtil.isError(result))
	                return ServiceUtil.returnError("Invalid credentials.");

	            // Store partyId in session on successful login
	            String partyId = userLogin.getString("partyId");
	            request.getSession().setAttribute("partyId",      partyId);
	            request.getSession().setAttribute("userLoginId",  email);

	            Map<String, Object> resp = ServiceUtil.returnSuccess();
	            resp.put("partyId",     partyId);
	            resp.put("role",        userLogin.getString("partyId"));
	            resp.put("userName",    email);
	            return resp;

	        } catch (Exception e) {
	            return ServiceUtil.returnError("Error: " + e.getMessage());
	        }
	    }
}
