package com.app.MBox.core.repository;

import com.app.MBox.core.model.artist;
import com.app.MBox.core.model.users;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface artistRepository extends CrudRepository<artist,Integer> {

    @Query(value = "select ar.* from record_label_artists rla,artist ar where rla.record_label_id=?1 and rla.artist_id=ar.id" ,nativeQuery = true)
    List<artist> findAllArtists(int recordLabelId);

    @Query(value = "select a.* from users u join (select ar.* from record_label_artists rla,artist ar where rla.record_label_id=?1 and rla.artist_id=ar.id)a on u.id=a.user_id" ,nativeQuery = true)
    List<artist> findAllArtists(int recordLabelId, Pageable pageable);

    artist findByUserId(int userId);
    @Query(value = "select a.* from users u join artist a on u.id=a.user_id where u.is_activated=true order by u.date_created limit 5",nativeQuery = true)
    List<artist> findRecentlyAddedArtist();


}
