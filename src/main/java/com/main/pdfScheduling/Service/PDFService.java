package com.main.pdfScheduling.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.logging.log4j.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.lang.reflect.Type;

import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import com.main.pdfScheduling.PdfSchedulingApplication;
import com.main.pdfScheduling.Repository.PDFRepository;
import com.main.pdfScheduling.Utilities.FormatString;
import com.main.pdfScheduling.Utilities.PDFToBase64;

@Service
public class PDFService {

    private final static Logger logger = LogManager.getLogger(PdfSchedulingApplication.class);

    private static Gson gson = new Gson();

    @Value("${data_path}")
    private String dataFolder;

    @Value("${backup_path}")
    private String backupFolder;

    @Value("${json_path}")
    private String jsonFolder;

    @Value("${user}")
    private String user;

    @Value("${location}")
    private String location;

    @Value("${server_endpoint}")
    private String endpoint;

    @Autowired
    PDFRepository pdfRepository;

    public void convertPdfsInFolder() throws IOException {
        List<Map<String, String>> convertedJsonList = new ArrayList<>();
        File folder = new File(dataFolder);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".pdf"));
            if (files != null)
                for (File pdfFile : files) {
                    try {
                        Map<String, String> json = convertPdfToJson(pdfFile);
                        convertedJsonList.add(json);
                        connectToServer(json);
                        moveFileToOutputFolder(pdfFile, "Success");
                        logger.info("Convert success!");
                    } catch (IOException e) {
                        moveFileToOutputFolder(pdfFile, "Failed");
                        logger.info("Convert failed!");
                        e.printStackTrace();
                    }
                }
        }
    }

    private Map<String, String> convertPdfToJson(File pdfFile) throws IOException {

        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("patient_id", getPatientID(dataFolder + pdfFile.getName()));
        jsonMap.put("image_file_name", "PFT_" + pdfFile.getName());
        jsonMap.put("scan_id", user);
        jsonMap.put("scan_spid", location);
        jsonMap.put("scan_date", FormatString.getScanDate(pdfFile.getName()));
        jsonMap.put("scan_time", FormatString.getScanTime(pdfFile.getName()));
        jsonMap.put("folder_name", "PFT");
        jsonMap.put("base64", PDFToBase64.convertPdfToBase64(dataFolder +
                pdfFile.getName()).toString());

        return jsonMap;
    }

    private void moveFileToOutputFolder(File pdfFile, String status) throws IOException {
        Path sourcePath = pdfFile.toPath();
        Path targetPath = null;

        if (status.equals("Success"))
            targetPath = Paths.get(backupFolder).resolve(pdfFile.getName());
        else if (status.equals("Failed"))
            targetPath = Paths.get(jsonFolder).resolve(pdfFile.getName());

        Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private String getPatientID(String pdfFilePath) {
        String text = "", patient_id = "";
        try {
            File file = new File(pdfFilePath);
            PDDocument document = PDDocument.load(file);
            PDFTextStripper pdfTextStripper = new PDFTextStripper();

            text = pdfTextStripper.getText(document);

            patient_id = pdfRepository.searchPatientIdByHNCode(FormatString.extractHN(text));

            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return patient_id;
    }

    private void connectToServer(Map<String, String> json) {
        try {
            // Define the URL to which you want to send the request.
            URL url = new URL(endpoint + "/test");

            // Create a HttpURLConnection object.
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the HTTP request method to POST.
            connection.setRequestMethod("POST");

            // Enable input/output streams.
            connection.setDoOutput(true);

            // Set the content type to form data.
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Define your form data as a key-value map.
            Map<String, String> formData = new HashMap<>();
            formData.put("patient_id", json.get("patient_id"));
            formData.put("image_file_name", json.get("image_file_name"));
            formData.put("scan_id", json.get("scan_id"));
            formData.put("scan_spid", json.get("scan_spid"));
            formData.put("scan_date", json.get("scan_date"));
            formData.put("scan_time", json.get("scan_time"));
            formData.put("folder_name", json.get("folder_name"));
            formData.put("base64", (String) json.get("base64"));

            System.out.println(json.get("base64").length());
            // Add more key-value pairs as you needed.

            // Convert the form data to String.
            String formDataString = FormatString.getFormDataString(formData);

            // Get the output stream to write the form data.
            try (DataOutputStream outStream = new DataOutputStream(connection.getOutputStream())) {
                outStream.writeBytes(formDataString);
                outStream.flush();
            }

            // Get the response code.
            int responseCode = connection.getResponseCode();

            System.out.println(responseCode);

            // Read the response from the server site.
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                // Handle the response as you needed.
                Type type = new TypeToken<Map<String, String>>() {
                }.getType();
                Map<String, String> test = gson.fromJson(response.toString(), type);
                if(json.get("base64").length() == test.get("base64").length())
                    System.out.println("-------------EQUAL!!-------------");
                else{
                    System.out.println("------------NO----------");
                }
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
