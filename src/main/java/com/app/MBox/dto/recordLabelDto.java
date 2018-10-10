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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof recordLabelDto)) {
            return false;
        }

        recordLabelDto record=(recordLabelDto) obj;
        return this.id==record.getId();
    }

}
