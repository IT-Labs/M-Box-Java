package com.app.MBox.core.repository;

import com.app.MBox.core.model.song;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface songRepository extends CrudRepository<song,Integer> {

    List<song> findByArtistId (int artistId);

    song findById (int id);

    @Query(value = "select * from song order by date_of_release DESC limit 5",nativeQuery = true)
    List<song> getMostRecentSongs();

    @Query(value = "select * from song where artist_id=?1",nativeQuery = true)
    List<song> findSongs(int artistId, Pageable pageable);
}
