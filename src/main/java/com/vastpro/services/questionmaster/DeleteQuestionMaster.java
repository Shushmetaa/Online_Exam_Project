package com.vastpro.services.questionmaster;

import java.util.HashMap;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

public class DeleteQuestionMaster {
      public  static Map<String,Object> deleteQuestions(DispatchContext dctx, Map<String, ? extends Object> context ){
    	  String examId =(String) context.get("examId");
    	  String qId = (String) context.get("qId");
    	  
    	  Delegator delegator = dctx.getDelegator();
    	  LocalDispatcher dispatcher = dctx.getDispatcher();
    	  
    	  GenericValue userLogin = (GenericValue) context.get("userLogin");
    	  
    		if(examId == null || examId.isEmpty()) {
				return ServiceUtil.returnError("Exam Id is required");
			}
			
			if(qId == null) {
				return ServiceUtil.returnError("QId is required");
			}
			
    	  try {
    	  GenericValue delete_question=EntityQuery.use(delegator)
    			  .from("QuestionBankMasterB")
    			  .where("examId",examId,"qId",qId)
    			  .queryOne();
    	  
    	  if(delete_question==null) {
    		  return ServiceUtil.returnError("Deleting data is Empty");
    	  }
    	  Map<String,Object> input= new HashMap<>();
          input.put("examId", examId);
          input.put("qId",qId);
          input.put("userLogin", userLogin);
          
          Map<String, Object> result = dispatcher.runSync("deleteQuestionMasterAuto", input);
          
          if (ServiceUtil.isError(result)) {
              return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
          }

          Map<String, Object> response = ServiceUtil.returnSuccess("Question deleted successfully");
          response.put("examId", examId);
          return response;
          
    	  }catch(GenericEntityException | GenericServiceException e) {
    		  return ServiceUtil.returnError("Questions are failed to delete:"+e.getMessage());
    	  }
      }
}
