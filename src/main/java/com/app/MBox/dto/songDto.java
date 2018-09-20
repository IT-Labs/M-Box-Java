package com.app.MBox.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private Date dateReleased;

    private String youtubeLink;

    private String vimeoLink;

    private String songLyrics;

}
