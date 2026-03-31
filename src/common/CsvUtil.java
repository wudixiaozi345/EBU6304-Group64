package common;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvUtil {

    public static List<String> readCsv(String fileName) {
        List<String> dataList = new ArrayList<>();
        File dataDir = new File(Constants.DATA_PATH);

        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File csvFile = new File(Constants.DATA_PATH + fileName);

        if (!csvFile.exists()) {
            try {
                csvFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return dataList;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    dataList.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public static void writeCsv(String fileName, List<String> dataList, boolean append) {
        File dataDir = new File(Constants.DATA_PATH);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File csvFile = new File(Constants.DATA_PATH + fileName);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile, append))) {
            for (String line : dataList) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        List<String> testData = new ArrayList<>();
        testData.add("staffId,moName,email,status");
        testData.add("BUPT123456,TestUser,test@qm.com,1");

        writeCsv(Constants.MO_ACCOUNT_FILE, testData, false);

        System.out.println("===== 读取结果 =====");
        List<String> result = readCsv(Constants.MO_ACCOUNT_FILE);
        for (String line : result) {
            System.out.println(line);
        }
    }
}