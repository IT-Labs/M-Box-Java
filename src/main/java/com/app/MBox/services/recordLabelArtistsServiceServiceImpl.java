package com.app.MBox.services;

import com.app.MBox.core.model.recordLabelArtists;
import com.app.MBox.core.repository.recordLabelArtistsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("recordLabelArtistsImpl")
public class recordLabelArtistsServiceServiceImpl implements recordLabelArtistsService {

    @Autowired
    private recordLabelArtistsRepository recordLabelArtistsRepository;

    public int findNumberOfArtistsInRecordLabel(int recordLabelId) {
        return recordLabelArtistsRepository.findNumberOfArtistsInRecordLabel(recordLabelId);
    }

    public recordLabelArtists save(recordLabelArtists recordLabelArtists) {
        return recordLabelArtistsRepository.save(recordLabelArtists);
    }
}
