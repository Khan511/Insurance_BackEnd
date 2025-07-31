package com.example.insurance.domain.emailService;

public class EmailUtils {
    public static String getEmailMessage(String name, String host, String key) {
        return "Hello " + name +
                "\n\nYour new account has been created. Please click on the link below to verify your account.\n\n" +
                getVerifycationUrl(host, key) + "\n\nThank you,\n\nThe Insurance Team";
    }

    private static String getVerifycationUrl(String host, String key) {
        return host + "/verify/account?key=" + key;
    }

}
