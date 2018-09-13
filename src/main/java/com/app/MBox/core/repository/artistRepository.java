package com.app.MBox.core.repository;

import com.app.MBox.core.model.artist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface artistRepository extends CrudRepository<artist,Integer> {

    @Query(value = "select ar.* from record_label_artists rla,artist ar where rla.record_label_id=?1 and rla.artist_id=ar.id" ,nativeQuery = true)
    List<artist> findAllArtists(int recordLabelId);

    artist findByUserId(int userId);
}
