package com.vastpro.services.exammaster;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class RetireExam {
	
	public static Map<String, Object> retireExam(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		try {
            String examId = (String) context.get("examId");
            String lastModifiedByUserLogin = (String) context.get("lastModifiedByUserLogin");

            if (examId == null || examId.isEmpty()) {
                return ServiceUtil.returnError("Exam ID is required");
            }

            // Set thruDate
            Timestamp now = new Timestamp(System.currentTimeMillis());

            // Prepare input map for auto service
            Map<String, Object> input = new HashMap<>();
            input.put("examId", examId);
            input.put("thruDate", now);
            input.put("lastModifiedByUserLogin", lastModifiedByUserLogin);

            LocalDispatcher dispatcher = dctx.getDispatcher();

            Map<String, Object> result = dispatcher.runSync("retireExamAuto", input);

            if (ServiceUtil.isError(result)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
            }

            Map<String, Object> response = ServiceUtil.returnSuccess("Exam retired successfully");
            response.put("retiredAt", now.toString());
            return response;
			
            } catch (GenericServiceException e) {
		            return ServiceUtil.returnError("Error retiring exam: " + e.getMessage());
		        }
		}
	
	}
