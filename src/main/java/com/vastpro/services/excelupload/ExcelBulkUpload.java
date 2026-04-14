package com.vastpro.services.excelupload;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

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

    private static final Logger logger =
            Logger.getLogger(ExcelBulkUpload.class.getName());

    public static Map<String, Object> excelUpload(
            DispatchContext dctx, Map<String, ? extends Object> context) {

        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator        = dctx.getDelegator();
        GenericValue userLogin     = (GenericValue) context.get("userLogin");

        try {
            String examId   = (String) context.get("examId");
            InputStream file = (InputStream) context.get("file");

            if (examId == null || examId.isEmpty())
                return ServiceUtil.returnError("Exam ID is required");
            if (file == null)
                return ServiceUtil.returnError("File is required");

            // Find current max qId for this exam — max+1 logic
            List<GenericValue> existing = EntityQuery.use(delegator)
                    .from("QuestionBankMasterB")
                    .where("examId", examId)
                    .queryList();

            long maxQId = 0;
            for (GenericValue q : existing) {
                try {
                    long id = Long.parseLong(q.getString("qId"));
                    if (id > maxQId) maxQId = id;
                } catch (NumberFormatException e) {
                    // Safety net — skip non-numeric qIds
                    logger.warning("Skipping non-numeric qId: ["
                        + q.getString("qId") + "] - Manually inserted record, "
                        + "excluding from max qId calculation.");
                }
            }

            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet       = workbook.getSheetAt(0);
            int count         = 0;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Column 0 — topic_name (look up topicId)
                String topicName = row.getCell(0) != null
                        ? row.getCell(0).toString().trim() : "";

                if (topicName.isEmpty()) continue;

                // Look up topicId from TopicMaster by topic_name
                GenericValue topicRecord = EntityQuery.use(delegator)
                        .from("TopicMaster")
                        .where("topicName", topicName)
                        .queryFirst();

                if (topicRecord == null) {
                    logger.warning("Row " + i + " skipped — topic not found: "
                            + topicName);
                    continue;
                }
                String topicId = topicRecord.getString("topicId");

                // Read remaining columns
                String questions       = row.getCell(1)  != null ? row.getCell(1).toString()  : "";
                String optiona         = row.getCell(2)  != null ? row.getCell(2).toString()  : "";
                String optionb         = row.getCell(3)  != null ? row.getCell(3).toString()  : "";
                String optionc         = row.getCell(4)  != null ? row.getCell(4).toString()  : "";
                String optiond         = row.getCell(5)  != null ? row.getCell(5).toString()  : "";
                String optione         = row.getCell(6)  != null ? row.getCell(6).toString()  : "";
                String answer          = row.getCell(7)  != null ? row.getCell(7).toString()  : "";
                String numAnswerStr    = row.getCell(8)  != null ? row.getCell(8).toString()  : "1";
                String questionType    = row.getCell(9)  != null ? row.getCell(9).toString()  : "";
                String difficultyLevel = row.getCell(10) != null ? row.getCell(10).toString() : "";
                String answerValueStr  = row.getCell(11) != null ? row.getCell(11).toString() : "1";
                String negativeMarksStr= row.getCell(12) != null ? row.getCell(12).toString() : "0";

                if (questions.isEmpty()) continue;

                Long       numAnswer     = (long) Double.parseDouble(numAnswerStr.isEmpty()      ? "1" : numAnswerStr);
                BigDecimal answerValue   = new BigDecimal(answerValueStr.isEmpty()   ? "1" : answerValueStr);
                BigDecimal negativeMarks = new BigDecimal(negativeMarksStr.isEmpty() ? "0" : negativeMarksStr);

                // Auto generate qId — max+1
                String qId = String.valueOf(++maxQId); 

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
                    logger.warning("Row " + i + " failed: "
                            + ServiceUtil.getErrorMessage(result));
                }
            }

            workbook.close();
            return ServiceUtil.returnSuccess("Inserted " + count + " questions successfully!");

        } catch (Exception e) {
            return ServiceUtil.returnError("Excel upload failed: " + e.getMessage());
        }
    }
}