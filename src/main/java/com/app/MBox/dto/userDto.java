package com.app.MBox.dto;

import com.app.MBox.common.validation.passwordMatches;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@passwordMatches
public class userDto {

    @NotNull
    @NotEmpty
    @Length(min = 1 , max = 50)
    private String name;

    @NotNull
    @NotEmpty
    @Length(min = 8 , max = 64)
    private String password;
    @NotNull
    @NotEmpty
    @Length(min = 8 , max = 64)
    private String matchingPassword;

    @NotNull
    @NotEmpty
    @Email
    @Length(max = 320)
    private String email;

}

