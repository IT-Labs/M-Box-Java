package com.app.MBox.core.repository;

import com.app.MBox.core.model.artist;
import com.app.MBox.core.model.recordLabelArtists;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface recordLabelArtistRepository extends CrudRepository<recordLabelArtists,Integer> {

    @Query(value =  "select rla.artist from recordLabelArtists rla where rla.recordLabel.id = ?1")
    List<artist> findAllArtists(int recordLabelId);


}
