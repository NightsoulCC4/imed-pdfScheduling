package com.main.pdfScheduling.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.*;
import java.util.Base64;

import javax.xml.bind.DatatypeConverter;

public final class PDFToBase64 {

    private static byte[] readPdfFile(String pdfFilePath) throws IOException {
        File file = new File(pdfFilePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] pdfBytes = new byte[(int) file.length()];

        fileInputStream.read(pdfBytes);
        fileInputStream.close();

        return pdfBytes;
    }

    public static String convertPdfToBase64(String pdfFilePath) throws IOException {
        byte[] pdfBytes = readPdfFile(pdfFilePath);

        String base64String = Base64.getEncoder().encodeToString(pdfBytes);

        return base64String;
    }

     public static Boolean convertBase64ToPDF(String param, String image_file_name) throws IOException {
        // byte[] pdfBytes = readPdfFile(pdfFilePath);

        System.out.println(image_file_name);
        try {
            byte[] decodedPDF = DatatypeConverter.parseBase64Binary(param);
            Path destinationFile = Paths.get("C:/src/json/", image_file_name);
            Files.write(destinationFile, decodedPDF);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
