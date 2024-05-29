package com.zerobase.partner.service.mailgun;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service(value = "mailgun")
public class MailgunApi {

    private final String DOMAIN = "sandboxf04e04549aa1462f8a83221bd7291760.mailgun.org";

    @Value("${mailgun.apikey}")
    private String mailgunApiKey;

    public String sendVerifyEmail(SendingMailForm sendingMailForm)
            throws UnirestException {
        HttpResponse<JsonNode> request
                = Unirest.post("https://api.mailgun.net/v3/"
                                + DOMAIN
                                + "/messages")
			.basicAuth("api", mailgunApiKey)
                .queryString("from",
                        "Excited User <" + sendingMailForm.getFrom() + ">")
                .queryString("to", sendingMailForm.getTo())
                .queryString("subject", sendingMailForm.getSubject())
                .queryString("text", sendingMailForm.getText())
                .asJson();
        return request.getBody().toString();
    }
}
