package com.app.MBox.services;

import com.app.MBox.core.model.artist;
import com.app.MBox.core.repository.artistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class artistServiceImpl implements artistService {

    @Autowired
    artistRepository artistRepository;


    @Override
    public List<artist> findAllArtists(int recordLabelId) {
        return artistRepository.findAllArtists(recordLabelId);
    }

    public List<artist> findAllArtists(int recordLabelId, Pageable pageable) {
        return artistRepository.findAllArtists(recordLabelId,pageable);
    }

    public void save(artist artist) {
        artistRepository.save(artist);
    }

    public artist findByUserId(int userId) {
        return artistRepository.findByUserId(userId);
    }
}
