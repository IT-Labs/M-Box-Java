package com.app.MBox.services;

import com.app.MBox.core.model.recordLabelArtists;
import org.springframework.stereotype.Component;

@Component
public interface recordLabelArtistsService {
     int findNumberOfArtistsInRecordLabel(int recordLabelId);

     recordLabelArtists save(recordLabelArtists recordLabelArtists);

     recordLabelArtists findByRecordLabelId(int recordLabelId) ;

     void delete(recordLabelArtists recordLabelArtists) ;

     recordLabelArtists findByArtistId(int id) ;
}
