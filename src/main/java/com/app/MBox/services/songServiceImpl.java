package com.app.MBox.services;

import com.app.MBox.common.properties;
import com.app.MBox.core.model.artist;
import com.app.MBox.core.model.song;
import com.app.MBox.core.repository.songRepository;
import com.app.MBox.dto.songDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class songServiceImpl implements songService {

    @Autowired
    private songRepository songRepository;
    @Autowired
    private userService userService;
    @Autowired
    private properties properties;

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
}
