package com.main.pdfScheduling.Utilities;

import java.util.Map;

public class FormatString {
    public static String extractHN(String inputText) {
        // Split the input text by ":" and take the second part.
        String[] parts = inputText.split(":");

        if (parts.length >= 2) {
            return parts[1].trim();
        } else {
            // Return an empty string or handle the case when ":" is not found.
            return "";
        }
    }

    public static String getScanDate(String inputText) {
        // Split the input text by "_" and take the second part.
        String[] parts = inputText.split("_");

        if (parts.length >= 2) {
            return parts[1].trim().substring(0, 4) + "-" + parts[1].trim().substring(4, 6) + "-"
                    + parts[1].trim().substring(6, 8);
        } else {
            // Return an empty string or handle the case when "_" is not found.
            return "";
        }
    }

    public static String getScanTime(String inputText) {
        // Split the input text by "_" and take the second part.
        String[] parts = inputText.split("_");

        if (parts.length >= 2) {
            return parts[1].trim().substring(8, 10) + ":" + parts[1].trim().substring(10, 12) + ":"
                    + parts[1].trim().substring(12, 14);
        } else {
            // Return an empty string or handle the case when "_" is not found.
            return "";
        }
    }

    public static String getFormDataString(Map<String, String> formData) {
        StringBuilder formDataStringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            formDataStringBuilder.append(entry.getKey());
            formDataStringBuilder.append("=");
            formDataStringBuilder.append(entry.getValue());
            formDataStringBuilder.append("&");
        }
        // Remove the trailing '&' character.
        if (formDataStringBuilder.length() > 0) {
            formDataStringBuilder.setLength(formDataStringBuilder.length() - 1);
        }
        return formDataStringBuilder.toString();
    }
}
