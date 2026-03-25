package com.vastpro.services.exammaster;

import java.util.Map;

import org.apache.ofbiz.service.DispatchContext;

public class UpdateExam {
	
	public static Map<String, Object> updateExam(DispatchContext dctx, Map<String, ? extends Object> context){
		
		String examId = (String) context.get("examId");
		String examName = (String) context.get("examName");
		String description = (String) context.get("description");
		String noOfQuestions = (String) context.get("noOfQuestions");
		String duration = (String) context.get("duration");
		String passPercentage = (String) context.get("passPercentage");
		
		
		
		
		return null;
		
	}

}
