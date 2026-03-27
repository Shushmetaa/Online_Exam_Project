package com.vastpro.javaservice;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class QuestionMaster {
	
	public static Map<String, Object> createQuestion(HttpServletRequest request, HttpServletResponse response){
		
		String examId = request.getParameter("examId");
		String qId = request.getParameter("qId");
		String topicId = request.getParameter("topicId");
		String questionDetail = request.getParameter("questionDetail");
		
		return null;
		
	}

}
