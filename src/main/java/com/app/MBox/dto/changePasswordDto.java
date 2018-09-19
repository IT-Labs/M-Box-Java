package com.app.MBox.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
public class changePasswordDto {



    @Length(min = 8 , max = 64)
    private String password;

    @Length(min = 8 , max = 64)
    private String newPassword;

    @Length(min = 8 , max = 64)
    private String confirmPassword;


}
