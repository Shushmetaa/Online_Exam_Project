package com.vastpro.servicecall;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class ExcelUpload {
	
	private static LocalDispatcher getDispatcher(HttpServletRequest request) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        if (dispatcher == null) {
            dispatcher = (LocalDispatcher) request.getSession().getServletContext().getAttribute("dispatcher");
        }
        return dispatcher;
    }

    private static Delegator getDelegator(HttpServletRequest request) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        if (delegator == null) {
            delegator = (Delegator) request.getSession().getServletContext().getAttribute("delegator");
        }
        return delegator;
    }

	public static Map<String, Object> uploadQuestions(String examId, InputStream file, HttpServletRequest request, HttpServletResponse response) {
		
		try {
			
			LocalDispatcher dispatcher=(LocalDispatcher) request.getAttribute("dispatcher");
	
			
			GenericValue userLogin=EntityQuery.use((Delegator) request.getAttribute("delegator"))
						.from("UserLogin")
						.where("userLoginId", "admin")
						.queryOne();
			
			Map<String, Object> excelData = new HashMap<>();
			
			excelData.put("examId", examId);
			excelData.put("file", file);
			excelData.put("userLogin", userLogin);
			
			Map<String, Object> result = dispatcher.runSync("excelUpload", excelData);
			
			if(ServiceUtil.isError(result)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
			}
			else {
				return ServiceUtil.returnSuccess("Excel uploaded successfully");
			}
			
			
		} catch (GenericEntityException | GenericServiceException e) {
			
			return ServiceUtil.returnError("Excel uploaded failed" + e.getMessage());
		}
	}

}
