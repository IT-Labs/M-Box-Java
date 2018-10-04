package com.app.MBox.services;

import com.app.MBox.core.model.song;
import com.app.MBox.dto.songDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface songService {

    List<song> findByArtistId (int artistId);

    song findById (int id);

    List<songDto> getMostRecentSongs(Pageable pageable);

    String addSong(MultipartFile file,songDto songDto);

    String isValidPicture(MultipartFile file);

    List<songDto> findSongs(Pageable pageable);

    void deleteSong(int songId);

    List<songDto> searchSongs(String searchParam);

    List<songDto> mapSongToSongDto(List<song> songs);

    void saveSong(songDto songDto);
}
