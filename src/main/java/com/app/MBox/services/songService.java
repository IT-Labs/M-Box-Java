package com.app.MBox.services;

import com.app.MBox.core.model.song;
import com.app.MBox.dto.songDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface songService {

    List<song> findByArtistId (int artistId,Pageable pageable);

    song findById (int id);

    List<songDto> getMostRecentSongs(Pageable pageable);

    String addSong(MultipartFile file,songDto songDto);

    String isValidPicture(MultipartFile file);

    List<songDto> findSongs(Pageable pageable);

    void deleteSong(int songId);

    List<songDto> searchSongs(String searchParam);

    List<songDto> mapSongToSongDto(List<song> songs);

    void saveSong(songDto songDto);

    String addPicture(MultipartFile file, int id);

    List<songDto> searchAllExactMatchSongs(String param);

    List<songDto> searchAllStartingSearchQuery(String param);

    List<songDto> searchAllExactMatchSongsLyrics(String param);

    List<songDto> searchAllStartingSearchQueryLyrics(String param);

    List<songDto> searchAllSongs(String param);

    List<songDto> searchAllSongsLyrics(String param);

    songDto findAndMapSong(int id);
}
