package com.app.MBox.services;

import com.app.MBox.core.model.song;
import com.app.MBox.dto.songDto;

import java.util.List;

public interface songService {

    song findByArtistId (int artistId);

    song findById (int id);

    List<songDto> getMostRecentSongs();
}
