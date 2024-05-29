package com.zerobase.partner.mailgun;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.zerobase.partner.service.mailgun.MailgunApi;
import com.zerobase.partner.service.mailgun.SendingMailForm;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailgunApiTest {

    @InjectMocks
    private MailgunApi mailgunApi;

    @Mock
    private HttpResponse<JsonNode> mockResponse;

    @BeforeEach
    void setUp() {
        // 'mailgunApiKey' 필드에 값을 설정
        ReflectionTestUtils.setField(mailgunApi, "mailgunApiKey", "test-api-key");
    }

    @Test
    void mailgunApiTest() throws UnirestException {
        //given
        SendingMailForm form = SendingMailForm.builder()
                .from("1111")
                .to("2222")
                .subject("3333")
                .text("1234")
                .build();

        JsonNode jsonNode = new JsonNode(new JSONObject().put("message", "Queued. Thank you.").toString());
        when(mockResponse.getBody()).thenReturn(jsonNode);
        when(Unirest.post(anyString())
                .basicAuth(anyString(), anyString())
                .queryString(anyString(), anyString())
                .queryString(anyString(), anyString())
                .queryString(anyString(), anyString())
                .queryString(anyString(), anyString())
                .asJson())
                .thenReturn(mockResponse);

        //when
        String response = mailgunApi.sendVerifyEmail(form);

        //then
        assertEquals("{\"message\":\"Queued. Thank you.\"}", response);
    }

}