package com.app.MBox.core.repository;

import com.app.MBox.core.model.users;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository("userRepository")
public interface userRepository extends CrudRepository<users,Integer> {

    users findByEmail(String email);

    @Query(value="select u.* from users u join record_label r on u.id=r.user_id where u.is_activated=true" , nativeQuery = true)
    List<users> findAllRecordLabels();

    @Query(value="select u.* from users u join record_label r on u.id=r.user_id where u.is_activated=true" , nativeQuery = true)
    List<users>findRecordLabels(Pageable pageable);

    @Query(value = "select u.* from users u join record_label rl on u.id=rl.user_id where (name LIKE %?1% or email LIKE %?1%) and is_activated=true",nativeQuery = true)
    List<users>searchRecordLabels(String searchParam);

    @Query(value = "select u.* from (select artist.user_id from record_label_artists,artist where record_label_id=?1 and artist_id=artist.id)a join users u on a.user_id=u.id where u.is_activated=true",nativeQuery = true)
    List<users>findArtists(int recordLabelId, Pageable pageable);

    @Query(value = "select u.* from (select artist.user_id from record_label_artists,artist where record_label_id=?1 and artist_id=artist.id)a \n" +
            "join users u on a.user_id=u.id where (name LIKE %?2% or email LIKE %?2%) and u.is_activated=true",nativeQuery = true)
    List<users>searchArtists(int recordLabelId,String searchParam);

}
