package com.vastpro.services.excelupload;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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

        try {
            String examId   = (String) context.get("examId");
            InputStream file = (InputStream) context.get("file");

            int count = 0;

            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) continue;
                
                String qid             = row.getCell(0)  != null ? row.getCell(0).toString()  : "";
                String topicId         = row.getCell(1)  != null ? row.getCell(1).toString()  : "";
                String questions       = row.getCell(2)  != null ? row.getCell(2).toString()  : "";
                String optiona         = row.getCell(3)  != null ? row.getCell(3).toString()  : "";
                String optionb         = row.getCell(4)  != null ? row.getCell(4).toString()  : "";
                String optionc         = row.getCell(5)  != null ? row.getCell(5).toString()  : "";
                String optiond         = row.getCell(6)  != null ? row.getCell(6).toString()  : "";
                String optione         = row.getCell(7)  != null ? row.getCell(7).toString()  : "";
                String answer          = row.getCell(8)  != null ? row.getCell(8).toString()  : "";
                String numAnswer       = row.getCell(9)  != null ? row.getCell(9).toString()  : "";
                String questionType    = row.getCell(10) != null ? row.getCell(10).toString() : "";
                String difficultyLevel = row.getCell(11) != null ? row.getCell(11).toString() : "";
                String answerValue     = row.getCell(12) != null ? row.getCell(12).toString() : "";
                String negativeMarks   = row.getCell(13) != null ? row.getCell(13).toString() : "";

                if (questions == null || questions.isEmpty()) continue;

                Map<String, Object> data = new HashMap<>();
                data.put("examId",          examId);
                data.put("qId",             qid);
                data.put("topicId",         topicId);
                data.put("questionDetail",  questions);
                data.put("optiona",         optiona);
                data.put("optionb",         optionb);
                data.put("optionc",         optionc);
                data.put("optiond",         optiond);
                data.put("optione",         optione);
                data.put("answer",          answer);
                data.put("numAnswers",      numAnswer);    
                data.put("questiontype",    questionType); 
                data.put("difficultyLevel", difficultyLevel);
                data.put("answerValue",     answerValue);
                data.put("negativeMarkValue", negativeMarks);

                Map<String, Object> result = dispatcher.runSync("excelUpload", data);

                if (!ServiceUtil.isError(result)) {
                    count++;
                }
            }

            workbook.close();

            return ServiceUtil.returnSuccess("Inserted " + count + " questions");

        } catch (Exception e) {
            return ServiceUtil.returnError("Excel upload failed: " + e.getMessage());
        }
    }
}