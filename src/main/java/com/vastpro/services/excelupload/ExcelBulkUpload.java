package com.vastpro.services.excelupload;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExcelBulkUpload {

    public Map<String, Object> excelUpload(DispatchContext dctx, Map<String, ? extends Object> context) {

        LocalDispatcher dispatcher = dctx.getDispatcher();
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        try {
            String examId   = (String) context.get("examId");
            InputStream file = (InputStream) context.get("file");

            int count = 0;

            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            
            Delegator delegator = dctx.getDelegator();
            
            //here we are querying to ask how many questions are stored in db
            List<GenericValue> existing = EntityQuery.use(delegator)
                    .from("QuestionBankMasterB")
                    .where("examId", examId)
                    .queryList();
            
            int startQid = (existing != null ? existing.size() : 0) + 1;//then that number it will take and add 1

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            	
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String topicId = row.getCell(0)  != null ? row.getCell(0).toString()  : "";
                String questions = row.getCell(1)  != null ? row.getCell(1).toString()  : "";
                String optiona = row.getCell(2)  != null ? row.getCell(2).toString()  : "";
                String optionb = row.getCell(3)  != null ? row.getCell(3).toString()  : "";
                String optionc = row.getCell(4)  != null ? row.getCell(4).toString()  : "";
                String optiond = row.getCell(5)  != null ? row.getCell(5).toString()  : "";
                String optione = row.getCell(6)  != null ? row.getCell(6).toString()  : "";
                String answer = row.getCell(7)  != null ? row.getCell(7).toString()  : "";
                String numAnswerStr = row.getCell(8)  != null ? row.getCell(8).toString()  : "";
                String questionType = row.getCell(9)  != null ? row.getCell(9).toString()  : "";
                String difficultyLevel = row.getCell(10) != null ? row.getCell(10).toString() : "";
                String answerValueStr = row.getCell(11) != null ? row.getCell(11).toString() : "0";
                String negativeMarksStr = row.getCell(12) != null ? row.getCell(12).toString() : "0";
                
                Long numAnswer = (long) Double.parseDouble(numAnswerStr.isEmpty()     ? "0" : numAnswerStr);
                BigDecimal answerValue     = new BigDecimal(answerValueStr.isEmpty()   ? "0" : answerValueStr);
                BigDecimal negativeMarks   = new BigDecimal(negativeMarksStr.isEmpty() ? "0" : negativeMarksStr);

                if (questions.isEmpty()) continue;

                String qId = examId + "_" + (startQid + count);//this will display like '1000_31'

                Map<String, Object> data = new HashMap<>();
                data.put("examId",            examId);
                data.put("qId",               qId);        
                data.put("topicId",           topicId);    
                data.put("questionDetail",    questions);
                data.put("optiona",           optiona);
                data.put("optionb",           optionb);
                data.put("optionc",           optionc);
                data.put("optiond",           optiond);
                data.put("optione",           optione);
                data.put("answer",            answer);
                data.put("numAnswers",        numAnswer);
                data.put("questiontype",      questionType);
                data.put("difficultyLevel",   difficultyLevel);
                data.put("answerValue",       answerValue);
                data.put("negativeMarkValue", negativeMarks);
                data.put("userLogin",         userLogin);

                Map<String, Object> result = dispatcher.runSync("excelUploadAuto", data);
                if (!ServiceUtil.isError(result)) {
                    count++;
                } else {
                    System.out.println("Row " + i + " failed: " + ServiceUtil.getErrorMessage(result));
                }
            }

            workbook.close();

            return ServiceUtil.returnSuccess("Inserted " + count + " questions");

        } catch (Exception e) {
            return ServiceUtil.returnError("Excel upload failed: " + e.getMessage());
        }
    }
}