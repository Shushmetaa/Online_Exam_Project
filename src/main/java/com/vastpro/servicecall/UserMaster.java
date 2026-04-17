package com.vastpro.servicecall;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

	            String partyId = (String) request.getSession().getAttribute("partyId");
	            if (partyId == null)
	                return ServiceUtil.returnError("User not logged in.");

	            GenericValue userLogin = EntityQuery.use(delegator)
	                    .from("UserLogin")
	                    .where("userLoginId", "admin")
	                    .queryOne();

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
//	            if (partyId == null)
//	                return ServiceUtil.returnError("User not logged in.");

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
	    
	    public static Map<String, Object> verifyExamPassword(
	            HttpServletRequest request, HttpServletResponse response) {
	        try {
	            LocalDispatcher dispatcher = getDispatcher(request);
	            Delegator delegator        = getDelegator(request);

	            String partyId  = (String) request.getSession().getAttribute("partyId");
	            String examId   = request.getParameter("examId");
	            String password = request.getParameter("password");

	            if (partyId == null)
	                return ServiceUtil.returnError("User not logged in.");
	            if (examId == null || password == null)
	                return ServiceUtil.returnError("examId and password are required.");

	            GenericValue adminLogin = EntityQuery.use(delegator)
	                    .from("UserLogin").where("userLoginId", "admin").queryOne();

	            Map<String, Object> data = new HashMap<>();
	            data.put("partyId",   partyId);
	            data.put("examId",    examId);
	            data.put("password",  password);
	            data.put("userLogin", adminLogin);

	            Map<String, Object> result = dispatcher.runSync("verifyExamPassword", data);
	            if (ServiceUtil.isError(result))
	                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));

	            return ServiceUtil.returnSuccess("Password verified.");

	        } catch (Exception e) {
	            return ServiceUtil.returnError("Error: " + e.getMessage());
	        }
	    }
	    public static Map<String, Object> getUserInfo(
	            HttpServletRequest request, HttpServletResponse response) {
	        try {
	            Delegator delegator = getDelegator(request);
	            String partyId = (String) request.getSession().getAttribute("partyId");
	            if (partyId == null)
	                return ServiceUtil.returnError("User not logged in.");

	            GenericValue person = EntityQuery.use(delegator)
	                    .from("Person")
	                    .where("partyId", partyId)
	                    .queryOne();

	            if (person == null)
	                return ServiceUtil.returnError("User not found.");

	            String firstName = person.getString("firstName");

	            Map<String, Object> result = ServiceUtil.returnSuccess("User info fetched.");
	            result.put("firstName", firstName);
	            result.put("partyId",   partyId);
	            return result;

	        } catch (Exception e) {
	            return ServiceUtil.returnError("Error: " + e.getMessage());
	        }
	    }

		public static Object getCertificate(String partyId, String examId, HttpServletRequest request,
				HttpServletResponse response) {
			// TODO Auto-generated method stub
			return null;
		}
}
