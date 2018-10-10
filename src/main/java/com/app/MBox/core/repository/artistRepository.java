package com.app.MBox.core.repository;

import com.app.MBox.core.model.artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface artistRepository extends CrudRepository<artist,Integer> {
    //select ar.* from record_label_artists rla,artist ar where rla.record_label_id=?1 and rla.artist_id=ar.id
    @Query(value = "select a from recordLabelArtists rla,artist a where rla.recordLabel.id=?1 and rla.artist=a")
    List<artist> findAllArtists(int recordLabelId);
    //select a.* from users u join (select ar.* from record_label_artists rla,artist ar where rla.record_label_id=?1 and rla.artist_id=ar.id)a on u.id=a.user_id
    @Query(value = "select a from artist a,recordLabelArtists rla where rla.recordLabel.id=?1 and rla.artist=a")
    Page<artist> findAllArtists(int recordLabelId, Pageable pageable);

    artist findByUserId(int userId);
    //select a.* from users u join artist a on u.id=a.user_id where u.is_activated=true order by u.date_created desc limit 5
    @Query(value = "select a from users u , artist a where u=a.user and u.isActivated=true")
    Page<artist> findRecentlyAddedArtist(Pageable pageable);

    artist findById(int id);


}
