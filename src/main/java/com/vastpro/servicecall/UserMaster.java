package com.vastpro.servicecall;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import javax.ws.rs.core.Response;

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

            GenericValue userLogin = EntityQuery.use(delegator)
                    .from("UserLogin").where("userLoginId", "admin").queryOne();

            Map<String, Object> data = new HashMap<>();
            data.put("partyId",   partyId);
            data.put("userLogin", userLogin);

            Map<String, Object> result = dispatcher.runSync("getUserExamStats", data);
            if (ServiceUtil.isError(result))
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));

            Map<String, Object> resp = ServiceUtil.returnSuccess();
            resp.put("completed", result.get("completed"));
            resp.put("bestScore", result.get("bestScore"));
            return resp;

        } catch (Exception e) {
            return ServiceUtil.returnError("Error: " + e.getMessage());
        }
    }

    public static Map<String, Object> verifyExamPassword(String password, String examId,
            HttpServletRequest request, HttpServletResponse response) {
        try {
            LocalDispatcher dispatcher = getDispatcher(request);
            Delegator delegator        = getDelegator(request);

            String partyId = (String) request.getSession().getAttribute("partyId");
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

            return dispatcher.runSync("verifyExamPassword", data);

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
                    .from("Person").where("partyId", partyId).queryOne();

            if (person == null)
                return ServiceUtil.returnError("User not found.");

            Map<String, Object> result = ServiceUtil.returnSuccess("User info fetched.");
            result.put("firstName", person.getString("firstName"));
            result.put("partyId",   partyId);   // ← must be here for CertificatePage.jsx
            return result;

        } catch (Exception e) {
            return ServiceUtil.returnError("Error: " + e.getMessage());
        }
    }

    public static Map<String, Object> getPassedExams(
            String partyId, HttpServletRequest request) {

        try {
            if (partyId == null || partyId.trim().isEmpty()) {
                return ServiceUtil.returnError("partyId is required");
            }

            LocalDispatcher dispatcher = getDispatcher(request);
            Delegator delegator        = getDelegator(request);

            if (dispatcher == null) {
                return ServiceUtil.returnError("Dispatcher not found");
            }

            GenericValue userLogin = EntityQuery.use(delegator)
                    .from("UserLogin").where("userLoginId", "admin").queryOne();
            
            Map<String, Object> result = dispatcher.runSync(
                    "getExamsForParty",
                    UtilMisc.toMap("partyId", partyId.trim(),"userLogin", userLogin)
            );

            if (ServiceUtil.isError(result)) {
	            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
	        } else {
	            return result;
	        }

        } catch (Exception e) {
            return ServiceUtil.returnError("Error: " + e.getMessage());
        }
    }
    /**
     * downloadCertificate
     * Called by: POST /exam/api/certificate/download
     */
    public static Response downloadCertificate(
            String examId, String partyId, HttpServletRequest request) {
        try {
            LocalDispatcher dispatcher = getDispatcher(request);
            Delegator delegator = getDelegator(request);
            
            if (dispatcher == null) {
                return Response.status(500)
                        .entity("{\"error\":\"Dispatcher not found\"}")
                        .type("application/json")
                        .build();
            }

            if (examId == null || examId.trim().isEmpty() ||
                partyId == null || partyId.trim().isEmpty()) {
                return Response.status(400)
                        .entity("{\"error\":\"examId and partyId are required\"}")
                        .type("application/json")
                        .build();
            }
        
            GenericValue userLogin = EntityQuery.use(delegator)
                    .from("UserLogin").where("userLoginId", "admin").queryOne();
            
            Map<String, Object> result = dispatcher.runSync(
                    "generateUserCertificate",
                    UtilMisc.toMap(
                        "examId",  examId.trim(),
                        "partyId", partyId.trim(),
                        "userLogin", userLogin
                    )
            );

            if (ServiceUtil.isError(result)) {
                return Response.status(500)
                        .entity("{\"error\":\"" + ServiceUtil.getErrorMessage(result) + "\"}")
                        .type("application/json")
                        .build();
            }

            byte[] pdfBytes = (byte[]) result.get("certificatePdf");
            String fileName = (String) result.get("fileName");

            if (pdfBytes == null || pdfBytes.length == 0) {
                return Response.status(500)
                        .entity("{\"error\":\"PDF generation returned empty output\"}")
                        .type("application/json")
                        .build();
            }

            if (fileName == null || fileName.isEmpty()) {
                fileName = "certificate_" + partyId + "_" + examId + ".pdf";
            }

            return Response.ok(pdfBytes, "application/pdf")
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .header("Content-Length", pdfBytes.length)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .type("application/json")
                    .build();
        }
    }
}
