package com.app.MBox.core.repository;

import com.app.MBox.core.model.song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface songRepository extends CrudRepository<song,Integer> {

    List<song> findByArtistId (int artistId);

    song findById (int id);

    @Query(value = "select s from song s")
    Page<song> getMostRecentSongs(Pageable pageable);

    @Query(value = "select s from song s where s.artist.id=?1")
    Page<song> findSongs(int artistId, Pageable pageable);

    @Query(value = "select s from song s where s.artist.id=?1 and (s.albumName LIKE %?2% or s.genre LIKE %?2% or s.name LIKE %?2%)")
    List<song> findSongs(int artistId, String searchParam);
}
