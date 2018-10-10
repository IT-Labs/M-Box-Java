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
    private String bio;
    private int id;
    private int recordLabelId;
    private String dateOfBirth;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof artistDto)) {
            return false;
        }

        artistDto artist=(artistDto)obj;
        return this.id==artist.getId();
    }

}
