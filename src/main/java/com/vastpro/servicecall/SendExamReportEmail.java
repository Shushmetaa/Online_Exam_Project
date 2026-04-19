package com.vastpro.servicecall;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class SendExamReportEmail {
	
	private static LocalDispatcher getDispatcher(HttpServletRequest request) {
        LocalDispatcher dispatcher = 
            (LocalDispatcher) request.getAttribute("dispatcher");
        if (dispatcher == null) {
            dispatcher = (LocalDispatcher) request.getSession()
                .getServletContext().getAttribute("dispatcher");
        }
        return dispatcher;
    }

    public static Map<String, Object> sendReport(HttpServletRequest request, HttpServletResponse response) {
        try {

            // ── Read from SESSION (set by exam-submit service) ──────
            HttpSession session = request.getSession();

            String partyId       = (String) session.getAttribute("partyId");
            String examId        = (String) session.getAttribute("examId");
            String performanceId = (String) session.getAttribute("performanceId");

            // ── Validate session values ─────────────────────────────
            if (partyId == null || partyId.isEmpty()) {
                return ServiceUtil.returnError(
                    "Session expired or partyId not found. Please log in again.");
            }
            if (examId == null || examId.isEmpty()) {
                return ServiceUtil.returnError(
                    "Session expired or examId not found.");
            }
            if (performanceId == null || performanceId.isEmpty()) {
                return ServiceUtil.returnError(
                    "Session expired or performanceId not found.");
            }

            LocalDispatcher dispatcher = getDispatcher(request);
            if (dispatcher == null) {
                return ServiceUtil.returnError("Dispatcher is null.");
            }

            GenericValue userLogin = EntityQuery
                .use(
                    (org.apache.ofbiz.entity.Delegator)
                    request.getSession()
                           .getServletContext()
                           .getAttribute("delegator")
                )
                .from("UserLogin")
                .where("userLoginId", "admin")
                .queryOne();

            // ── Build service context ───────────────────────────────
            Map<String, Object> serviceData = new HashMap<>();
            serviceData.put("partyId",       partyId);
            serviceData.put("examId",         examId);
            serviceData.put("performanceId",  performanceId);
            serviceData.put("userLogin",      userLogin);

            Map<String, Object> result = dispatcher.runSync("sendExamReportCsvEmail", serviceData);

            if (ServiceUtil.isError(result)) {
                return ServiceUtil.returnError(
                    ServiceUtil.getErrorMessage(result));
            }

            // ── Clear session after email sent ──────────────────────
            session.removeAttribute("partyId");
            session.removeAttribute("examId");
            session.removeAttribute("performanceId");

            return ServiceUtil.returnSuccess(
                "Exam report sent to your registered email successfully.");

        } catch (Exception e) {
            return ServiceUtil.returnError(
                "Failed to send exam report: " + e.getMessage());
        }
    }
}
	