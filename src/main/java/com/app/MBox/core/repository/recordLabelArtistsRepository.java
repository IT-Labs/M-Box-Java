package com.app.MBox.core.repository;

import com.app.MBox.core.model.artist;
import com.app.MBox.core.model.recordLabelArtists;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface recordLabelArtistsRepository extends CrudRepository <recordLabelArtists,Integer> {


    @Query(value="select count(*) from record_label_artists r where r.record_label_id=?1" , nativeQuery = true)
     int findNumberOfArtistsInRecordLabel(int recordLabelId);

     recordLabelArtists findByRecordLabelId(int recordLabelId);

     recordLabelArtists findByArtistId(int artistId);
}
