package com.app.MBox.services;

import com.app.MBox.common.customHandler.springChecks;
import com.app.MBox.common.properties;
import com.app.MBox.core.model.artist;
import com.app.MBox.core.model.song;
import com.app.MBox.core.repository.songRepository;
import com.app.MBox.dto.songDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class songServiceImpl implements songService {

    @Autowired
    private songRepository songRepository;
    @Autowired
    private userService userService;
    @Autowired
    private properties properties;
    @Autowired
    private springChecks springChecks;

    public static long FILE_SIZE=3*1024*1024;

    public song findByArtistId (int artistId) {
        return songRepository.findByArtistId(artistId);
    }

    public song findById (int id) {
        return songRepository.findById(id);
    }

    @Override
    public List<songDto> getMostRecentSongs() {
        List<songDto> songDtos=new LinkedList<>();
        List<song> songs=songRepository.getMostRecentSongs();
        for (song s:songs) {
            songDto songDto=new songDto();
            songDto.setSongName(s.getName());
            songDto.setAlbumName(s.getAlbumName());
            songDto.setYoutubeLink(s.getYoutubeLink());
            songDto.setVimeoLink(s.getVimeoLink());
            songDto.setArtistName(s.getArtist().getUser().getName());
            if(s.getImage()!=null) {
                //logic from S3
            }   else {
                songDto.setSongImgUrl(properties.getSongDefaultImage());
            }
            songDtos.add(songDto);
        }

        return songDtos;
    }

    public song addSong(MultipartFile file, songDto songDto) {
        song song=new song();
        song.setName(songDto.getSongName());
        song.setAlbumName(songDto.getAlbumName());
        song.setGenre(songDto.getGenre());
        song.setLyrics(songDto.getSongLyrics());
        String [] dateParts=songDto.getDateReleased().split("-");
        String parseDate=String.format("%s/%s/%s",dateParts[2],dateParts[1],dateParts[0]);
            try {
                Date date = new SimpleDateFormat("dd/MM/yyyy").parse(parseDate);
                song.setDateOfRelease(date);
            }   catch (Exception e) {
                log.error(e.getMessage());
            }
        artist artist=springChecks.getLoggedInArtist();
        song.setArtist(artist);
        song.setYoutubeLink(songDto.getYoutubeLink());
        song.setVimeoLink(songDto.getVimeoLink());
        if(!file.isEmpty()) {
            //logic for saving the picture on s3
        }

        song=songRepository.save(song);
        return song;
    }

    public String isValidPicture(MultipartFile file) {

        if(!file.isEmpty()) {
            String name=file.getOriginalFilename();
            String [] extension=name.split("\\.");
            if(!extension[extension.length-1].equals("jpg") && !extension[extension.length-1].equals("jpeg") && !extension[extension.length-1].equals("png")) {
                return "wrongFormat";
            }

            if(file.getSize()>FILE_SIZE) {
                return "sizeExceeded";
            }


        }
        return "OK";
    }

}
