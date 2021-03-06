package com.app.MBox.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class songDto {

    private String songName;

    private String songImgUrl;

    private String artistName;

    private String albumName;

    private String genre;

    private String dateReleased;

    private String youtubeLink;

    private String vimeoLink;

    private String songLyrics;

    private int id;

    private int artistId;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof songDto)) {
            return false;
        }

        songDto songDto=(songDto)obj;
        return this.id==songDto.getId();
    }
}
