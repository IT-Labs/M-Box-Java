package com.app.MBox.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class artistDto {

    private String pictureUrl;
    private String name;
    private String email;
    private String recordLabelName;
    private boolean deleted;

}
