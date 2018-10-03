package com.app.MBox.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class recordLabelDto implements Comparable {

    private String name;
    private String email;
    private int number;
    private String pictureUrl;
    private String aboutInfo;
    private int id;


    @Override
    public int compareTo(Object o) {
        return  (Integer.compare(this.getNumber(), ((recordLabelDto) o).getNumber()));
    }
}
