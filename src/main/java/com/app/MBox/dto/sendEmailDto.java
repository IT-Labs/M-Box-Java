package com.app.MBox.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class sendEmailDto {

    private String fromUserFullName;
    private String toEmail;
    private String subject;
    private String body;
}
