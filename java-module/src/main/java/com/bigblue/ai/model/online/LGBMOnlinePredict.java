package com.bigblue.ai.model.online;


import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.*;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: TheBigBlue
 * @Description:
 * @Date: 2019/3/7
 */
public class LGBMOnlinePredict {

    public Evaluator evaluator;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Evaluator evaluator = loadPmml("D:/Work/Project/06-Metis-AI-文件夹智能推荐/metis_round2.pmml");
        long endTime1 = System.currentTimeMillis();
        System.out.println("加载模型耗时：" + (endTime1 - startTime));
        Map<String, Map<String, Double>> map = getEntityMapFromFile("D:/Work/Project/06-Metis-AI-文件夹智能推荐/metis_round2_test.csv", 40);
        long endTime2 = System.currentTimeMillis();
        System.out.println("加载一条数据耗时：" + (endTime2 - endTime1));
        Map<String, Double> valueMap = predict(evaluator, map);

        long endTime3 = System.currentTimeMillis();
        System.out.println("预测模型耗时：" + (endTime3 - endTime2));
        valueMap.forEach((key, value) -> System.out.println(key + " -> " + value));
    }

    /**
     * @Author: TheBigBlue
     * @Description: 加载pmml，返回evaluator
     * @Date: 2019/3/7
     * @Return:
     **/
    public static Evaluator loadPmml(String pmmlPath) {
        PMML pmml = new PMML();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(pmmlPath);
            pmml = org.jpmml.model.PMMLUtil.unmarshal(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭输入流
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Evaluator evaluator = null;
        if (pmml != null) {
            ModelEvaluatorFactory modelEvaluatorFactory = ModelEvaluatorFactory.newInstance();
            evaluator = modelEvaluatorFactory.newModelEvaluator(pmml);
        }
        return evaluator;
    }

    /**
     * @Author: TheBigBlue
     * @Description: 模型预测
     * @Date: 2019/3/7
     * @Return:
     **/
    public static Map<String, Double> predict(Evaluator evaluator, Map<String, Map<String, Double>> map) {
        List<InputField> inputFields = evaluator.getInputFields();
        Map<String, Double> valueMap = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, Double>> entry : map.entrySet()) {
            Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();
            for (InputField inputField : inputFields) {
                FieldName inputFieldName = inputField.getFieldName();
                Double rowValue = entry.getValue().get(inputFieldName.getValue());
                FieldValue inputFieldValue = inputField.prepare(rowValue);
                arguments.put(inputFieldName, inputFieldValue);
            }
            Map<FieldName, ?> results = evaluator.evaluate(arguments);
            FieldName targetFieldName = evaluator.getTargetFields().get(0).getFieldName();
            Computable computable = (Computable) results.get(targetFieldName);
            valueMap.put(entry.getKey(), (double) computable.getResult());
        }
        return valueMap;
    }

    /**
     * @Author: TheBigBlue
     * @Description: 获取指定行数据
     * @Date: 2019/3/7
     * @Return:
     **/
    public static Map<String, Map<String, Double>> getEntityMapFromFile(String filePath, int count) {
        BufferedReader br = null;
        Map<String, Map<String, Double>> map = new LinkedHashMap<>();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            int i = 0;
            while (br.readLine() != null && i <= count) {
                if (i != 0) {
                    Map<String, Double> entityMap = new LinkedHashMap<String, Double>();
                    String[] strValues = br.readLine().split(",");
                    entityMap.put("LABEL", Double.valueOf(strValues[0]));
                    entityMap.put("CDE_COUNT", Double.valueOf(strValues[1]));
                    entityMap.put("CDE_OUT_COUNT", Double.valueOf(strValues[2]));
                    entityMap.put("CDE_OUT_NUM", Double.valueOf(strValues[3]));
                    entityMap.put("CDE_OPRT_COUNT_1", Double.valueOf(strValues[4]));
                    entityMap.put("CDE_OPRT_COUNT_3", Double.valueOf(strValues[5]));
                    entityMap.put("CDE_OPRT_COUNT_4", Double.valueOf(strValues[6]));
                    entityMap.put("CDE_OPRT_COUNT_5", Double.valueOf(strValues[7]));
                    entityMap.put("CDE_OPRT_COUNT_6", Double.valueOf(strValues[8]));
                    entityMap.put("CDE_OPRT_COUNT_7", Double.valueOf(strValues[9]));
                    entityMap.put("CDE_OPRT_COUNT_8", Double.valueOf(strValues[10]));
                    entityMap.put("CDE_OPRT_COUNT_9", Double.valueOf(strValues[11]));
                    entityMap.put("CDE_OPRT_COUNT_10", Double.valueOf(strValues[12]));
                    entityMap.put("CDE_OPRT_COUNT_11", Double.valueOf(strValues[13]));
                    entityMap.put("CDE_OPRT_COUNT_12", Double.valueOf(strValues[14]));
                    entityMap.put("CDE_OPRT_COUNT_13", Double.valueOf(strValues[15]));
                    entityMap.put("CDE_OPRT_COUNT_14", Double.valueOf(strValues[16]));
                    entityMap.put("CDE_OPRT_COUNT_16", Double.valueOf(strValues[17]));
                    entityMap.put("CDE_OPRT_COUNT_17", Double.valueOf(strValues[18]));
                    entityMap.put("CDE_OPRT_COUNT_18", Double.valueOf(strValues[19]));
                    entityMap.put("CDE_OPRT_COUNT_19", Double.valueOf(strValues[20]));
                    entityMap.put("CDE_OPRT_COUNT_20", Double.valueOf(strValues[21]));
                    entityMap.put("CDE_OPRT_COUNT_22", Double.valueOf(strValues[22]));
                    entityMap.put("CDE_OPRT_COUNT_23", Double.valueOf(strValues[23]));
                    entityMap.put("CDE_OPRT_COUNT_24", Double.valueOf(strValues[24]));
                    entityMap.put("CDE_OPRT_COUNT_25", Double.valueOf(strValues[25]));
                    entityMap.put("CDE_OUT_OPRT_COUNT_1", Double.valueOf(strValues[26]));
                    entityMap.put("CDE_OUT_OPRT_COUNT_3", Double.valueOf(strValues[27]));
                    entityMap.put("CDE_OUT_OPRT_COUNT_4", Double.valueOf(strValues[28]));
                    entityMap.put("CDE_OUT_OPRT_COUNT_5", Double.valueOf(strValues[29]));
                    entityMap.put("CDE_OUT_OPRT_COUNT_6", Double.valueOf(strValues[30]));
                    entityMap.put("CDE_OUT_OPRT_COUNT_7", Double.valueOf(strValues[31]));
                    entityMap.put("CDE_OUT_OPRT_COUNT_8", Double.valueOf(strValues[32]));
                    entityMap.put("CDE_OUT_OPRT_COUNT_9", Double.valueOf(strValues[33]));
                    entityMap.put("CDE_OUT_OPRT_COUNT_10", Double.valueOf(strValues[34]));
                    entityMap.put("CDE_OUT_OPRT_COUNT_11", Double.valueOf(strValues[35]));
                    entityMap.put("CDE_OUT_OPRT_COUNT_14", Double.valueOf(strValues[36]));
                    entityMap.put("CDE_OUT_OPRT_COUNT_16", Double.valueOf(strValues[37]));
                    entityMap.put("CDE_OUT_OPRT_COUNT_17", Double.valueOf(strValues[38]));
                    entityMap.put("CDE_OUT_OPRT_COUNT_18", Double.valueOf(strValues[39]));
                    entityMap.put("CDE_OUT_OPRT_COUNT_19", Double.valueOf(strValues[40]));
                    entityMap.put("CDE_OUT_OPRT_COUNT_20", Double.valueOf(strValues[41]));
                    entityMap.put("CDE_OUT_OPRT_COUNT_22", Double.valueOf(strValues[42]));
                    entityMap.put("CDE_OUT_OPRT_COUNT_23", Double.valueOf(strValues[43]));
                    entityMap.put("CDE_OUT_OPRT_COUNT_24", Double.valueOf(strValues[44]));
                    entityMap.put("CDE_OUT_OPRT_COUNT_25", Double.valueOf(strValues[45]));
                    entityMap.put("FILE_COUNT", Double.valueOf(strValues[46]));
                    entityMap.put("OUT_FILE_COUNT", Double.valueOf(strValues[47]));
                    entityMap.put("FILE_OUT_NUM_COUNT", Double.valueOf(strValues[48]));
                    entityMap.put("FILE_OPRT_COUNT_1", Double.valueOf(strValues[49]));
                    entityMap.put("FILE_OPRT_COUNT_3", Double.valueOf(strValues[50]));
                    entityMap.put("FILE_OPRT_COUNT_4", Double.valueOf(strValues[51]));
                    entityMap.put("FILE_OPRT_COUNT_5", Double.valueOf(strValues[52]));
                    entityMap.put("FILE_OPRT_COUNT_6", Double.valueOf(strValues[53]));
                    entityMap.put("FILE_OPRT_COUNT_7", Double.valueOf(strValues[54]));
                    entityMap.put("FILE_OPRT_COUNT_8", Double.valueOf(strValues[55]));
                    entityMap.put("FILE_OPRT_COUNT_9", Double.valueOf(strValues[56]));
                    entityMap.put("FILE_OPRT_COUNT_10", Double.valueOf(strValues[57]));
                    entityMap.put("FILE_OPRT_COUNT_11", Double.valueOf(strValues[58]));
                    entityMap.put("FILE_OPRT_COUNT_12", Double.valueOf(strValues[59]));
                    entityMap.put("FILE_OPRT_COUNT_13", Double.valueOf(strValues[60]));
                    entityMap.put("FILE_OPRT_COUNT_14", Double.valueOf(strValues[61]));
                    entityMap.put("FILE_OPRT_COUNT_16", Double.valueOf(strValues[62]));
                    entityMap.put("FILE_OPRT_COUNT_17", Double.valueOf(strValues[63]));
                    entityMap.put("FILE_OPRT_COUNT_18", Double.valueOf(strValues[64]));
                    entityMap.put("FILE_OPRT_COUNT_19", Double.valueOf(strValues[65]));
                    entityMap.put("FILE_OPRT_COUNT_20", Double.valueOf(strValues[66]));
                    entityMap.put("FILE_OPRT_COUNT_22", Double.valueOf(strValues[67]));
                    entityMap.put("FILE_OPRT_COUNT_23", Double.valueOf(strValues[68]));
                    entityMap.put("FILE_OPRT_COUNT_24", Double.valueOf(strValues[69]));
                    entityMap.put("FILE_OPRT_COUNT_25", Double.valueOf(strValues[70]));
                    entityMap.put("OUT_FILE_OPRT_COUNT_1", Double.valueOf(strValues[71]));
                    entityMap.put("OUT_FILE_OPRT_COUNT_3", Double.valueOf(strValues[72]));
                    entityMap.put("OUT_FILE_OPRT_COUNT_4", Double.valueOf(strValues[73]));
                    entityMap.put("OUT_FILE_OPRT_COUNT_5", Double.valueOf(strValues[74]));
                    entityMap.put("OUT_FILE_OPRT_COUNT_6", Double.valueOf(strValues[75]));
                    entityMap.put("OUT_FILE_OPRT_COUNT_7", Double.valueOf(strValues[76]));
                    entityMap.put("OUT_FILE_OPRT_COUNT_8", Double.valueOf(strValues[77]));
                    entityMap.put("OUT_FILE_OPRT_COUNT_9", Double.valueOf(strValues[78]));
                    entityMap.put("OUT_FILE_OPRT_COUNT_10", Double.valueOf(strValues[79]));
                    entityMap.put("OUT_FILE_OPRT_COUNT_11", Double.valueOf(strValues[80]));
                    entityMap.put("OUT_FILE_OPRT_COUNT_14", Double.valueOf(strValues[81]));
                    entityMap.put("OUT_FILE_OPRT_COUNT_16", Double.valueOf(strValues[82]));
                    entityMap.put("OUT_FILE_OPRT_COUNT_17", Double.valueOf(strValues[83]));
                    entityMap.put("OUT_FILE_OPRT_COUNT_18", Double.valueOf(strValues[84]));
                    entityMap.put("OUT_FILE_OPRT_COUNT_19", Double.valueOf(strValues[85]));
                    entityMap.put("OUT_FILE_OPRT_COUNT_20", Double.valueOf(strValues[86]));
                    entityMap.put("OUT_FILE_OPRT_COUNT_22", Double.valueOf(strValues[87]));
                    entityMap.put("OUT_FILE_OPRT_COUNT_23", Double.valueOf(strValues[88]));
                    entityMap.put("OUT_FILE_OPRT_COUNT_24", Double.valueOf(strValues[89]));
                    entityMap.put("OUT_FILE_OPRT_COUNT_25", Double.valueOf(strValues[90]));
                    entityMap.put("OUT_USER_NUM", Double.valueOf(strValues[91]));
                    entityMap.put("CDE_OUT_COUNT_max", Double.valueOf(strValues[92]));
                    entityMap.put("CDE_OUT_COUNT_min", Double.valueOf(strValues[93]));
                    entityMap.put("CDE_OUT_COUNT_mean", Double.valueOf(strValues[94]));
                    entityMap.put("CDE_OUT_COUNT_std", Double.valueOf(strValues[95]));
                    entityMap.put("OUT_USER_NUM_max", Double.valueOf(strValues[96]));
                    entityMap.put("OUT_USER_NUM_min", Double.valueOf(strValues[97]));
                    entityMap.put("OUT_USER_NUM_mean", Double.valueOf(strValues[98]));
                    entityMap.put("OUT_USER_NUM_std", Double.valueOf(strValues[99]));
                    entityMap.put("PRODUCT_TYPE", Double.valueOf(strValues[100]));
                    entityMap.put("APPL_AMOUNT", Double.valueOf(strValues[101]));
                    map.put("文件夹" + i, entityMap);
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

}
