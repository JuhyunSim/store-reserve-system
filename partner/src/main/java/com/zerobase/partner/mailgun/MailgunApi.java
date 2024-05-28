package com.zerobase.partner.mailgun;



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

    public String sendVerifyEmail() throws UnirestException {
        HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + DOMAIN + "/messages")
			.basicAuth("api", mailgunApiKey)
                .queryString("from", "Excited User <verify@store.com>")
                .queryString("to", "floweronwall31@gmail.com")
                .queryString("subject", "hello")
                .queryString("text", "testing")
                .asJson();
        return request.getBody().toString();
    }
}
