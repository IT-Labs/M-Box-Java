package com.app.MBox.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class aboutMessageDto {

    @Length(min = 2 , max = 50)
    private String name;

    @NotNull
    @Length(max = 255)
    private String email;

    @Length(min = 1 , max = 200)
    private String message;
}
