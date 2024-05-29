package com.zerobase.partner.service.mailgun;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendingMailForm {
    private String from;
    private String to;
    private String subject;
    private String text;
}
