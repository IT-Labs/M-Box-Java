package com.app.MBox.core.repository;

import com.app.MBox.core.model.recordLabelArtists;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface recordLabelArtistsRepository extends CrudRepository <recordLabelArtists,Integer> {

    //select count(*) from record_label_artists r where r.record_label_id=?1
    @Query(value="select count(r) from recordLabelArtists r where r.recordLabel.id=?1")
     int findNumberOfArtistsInRecordLabel(int recordLabelId);

     recordLabelArtists findByRecordLabelId(int recordLabelId);

     Optional <recordLabelArtists> findByArtistId(int artistId);
}
