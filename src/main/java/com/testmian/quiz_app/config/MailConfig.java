package com.testmian.quiz_app.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailConfig {

    @Value("${mail.sender.email}")
    private String senderEmail;

    @Value("${mail.sender.password}")
    private String senderPassword;

    @Value("${mail.sender.host}")
    private String host;

    @Value("${mail.sender.port}")
    private int port;

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getSenderPassword() {
        return senderPassword;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}

