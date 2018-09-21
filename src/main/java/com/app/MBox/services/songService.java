package com.app.MBox.services;

import com.app.MBox.core.model.artist;
import com.app.MBox.core.model.song;
import com.app.MBox.dto.songDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface songService {

    song findByArtistId (int artistId);

    song findById (int id);

    List<songDto> getMostRecentSongs();

    song addSong(MultipartFile file,songDto songDto);

    String isValidPicture(MultipartFile file);
}
