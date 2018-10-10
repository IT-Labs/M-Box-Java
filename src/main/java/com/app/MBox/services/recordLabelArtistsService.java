package com.app.MBox.services;

import com.app.MBox.core.model.recordLabelArtists;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface recordLabelArtistsService {
     int findNumberOfArtistsInRecordLabel(int recordLabelId);

     recordLabelArtists save(recordLabelArtists recordLabelArtists);

     recordLabelArtists findByRecordLabelId(int recordLabelId) ;

     void delete(recordLabelArtists recordLabelArtists) ;

     Optional<recordLabelArtists> findByArtistId(int id) ;
}
