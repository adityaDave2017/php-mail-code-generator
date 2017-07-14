package com.phpmail;

import com.google.gson.Gson;
import javafx.collections.ObservableList;

import java.io.*;


class Util {


    private static FormatTemplate loadTemplate(File location) throws IOException {
        return new Gson().fromJson(new FileReader(location), FormatTemplate.class);
    }


    static boolean generatePhp(File templateFile, File saveFile, EmailData data, ObservableList<Field> fields) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(saveFile))) {
            bufferedWriter.write("<?php\n\n");

            // Declare PHP variables
            bufferedWriter.write("$mail_to = \"" + data.getEmailTo() + "\";");
            bufferedWriter.write("\n");
            bufferedWriter.write("$email_subject = \"" + data.getEmailSubject() + "\";");
            bufferedWriter.write("\n");

            bufferedWriter.write("\n\n");

            for (Field field : fields) {
                bufferedWriter.write(field.toPhpVariable() + "\n");
            }

            bufferedWriter.write("\n\n");
            bufferedWriter.write("$company_url = \"" + data.getCompanyHomePage() + "\";");
            bufferedWriter.write("\n");
            bufferedWriter.write("$company_logo_url = \"" + data.getCompanyLogoUrl() + "\";");
            bufferedWriter.write("\n");
            bufferedWriter.write("$greetings_from = \"" + data.getGreetingsText() + "\";");
            bufferedWriter.write("\n");
            bufferedWriter.write("$from = \"" + data.getFromText() + "\";");
            bufferedWriter.write("\n");
            bufferedWriter.write("$success_url = \"" + data.getSuccessUrl() + "\";");
            bufferedWriter.write("\n");
            bufferedWriter.write("$failure_url = \"" + data.getFailureUrl() + "\";");
            bufferedWriter.write("\n\n");

            FormatTemplate template = loadTemplate(templateFile);
            String head = "$email_body = \n" + template.getHead();
            head = head.replace("$$COMPANY_LOGO_URL$$", data.getCompanyLogoUrl());
            bufferedWriter.write(head);

            for (Field field : fields) {
                String body = template.getBody();
                body = body.replace("$$DISPLAY_NAME$$", field.getDisplayName());
                body = body.replace("$$FIELD_VAR_NAME$$", "$" + field.getFormFieldName());
                bufferedWriter.write(body);
            }

            bufferedWriter.write(template.getTail());

            String headers = template.getHeaders();
            headers = headers.replace("$$FROM$$", data.getFromText());
            headers = headers.replace("$$EMAIL_TO$$", data.getEmailTo());
            bufferedWriter.write(headers);

            bufferedWriter.write("\n\n");
            bufferedWriter.write(template.getMailCode());

            bufferedWriter.flush();
            return true;
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        return false;
    }

}
