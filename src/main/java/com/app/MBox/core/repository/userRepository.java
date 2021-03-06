package com.app.MBox.core.repository;

import com.app.MBox.core.model.artist;
import com.app.MBox.core.model.users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository("userRepository")
public interface userRepository extends CrudRepository<users,Integer> {

    users findByEmail(String email);

    @Query(value="select u from users u,recordLabel r where u=r.user")
    List<users> findAllRecordLabels();

    @Query(value="select u from users u,recordLabel r where u=r.user")
    Page<users> findRecordLabels(Pageable pageable);

    //select u.* from users u join record_label r on u.id=r.user_id where u.is_activated=true, nativeQuery=true
    @Query(value="select u from users u , recordLabel r where u=r.user and u.isActivated=true")
    Page<users>findAllActivatedRecordLabels(Pageable pageable);

    @Query(value="select u from users u , recordLabel r where u=r.user and u.isActivated=true")
    List<users>findAllActivatedRecordLabels();

    //select u.* from users u join record_label rl on u.id=rl.user_id where (name LIKE %?1% or email LIKE %?1%), nativeQuery=true
    @Query(value = "select u from users u ,recordLabel r where u=r.user and (u.name LIKE %?1% or u.email LIKE %?1%)")
    List<users>searchRecordLabels(String searchParam);

    @Query(value = "select u.* from (select artist.user_id from record_label_artists,artist where record_label_id=?1 and artist_id=artist.id)a join users u on a.user_id=u.id" , nativeQuery = true)
    Page<users>findArtists(int recordLabelId, Pageable pageable);

    @Query(value = "select u.* from (select artist.user_id from record_label_artists,artist where record_label_id=?1 and artist_id=artist.id)a \n" +
            "join users u on a.user_id=u.id where (name LIKE %?2% or email LIKE %?2%)",nativeQuery = true)
    List<users>searchArtists(int recordLabelId,String searchParam);

    //select u.* from users u join artist a on u.id=a.user_id where u.is_activated=true, nativeQuery=true
    @Query(value = "select u from users u,artist a where u=a.user and u.isActivated=true")
    Page<users> findAllRecentlyAddedArtists(Pageable pageable);

    @Query(value = "select u from users u,artist a where u=a.user and LOWER(name) LIKE LOWER(?1)")
    List<users> findAllExactMatchArtists(String param);

    @Query(value = "select u from users u,recordLabel r where u=r.user and LOWER(name) LIKE LOWER(?1)")
    List<users> findAllExactMatchRecords(String param);

    @Query(value = "select u from users u,artist a where u=a.user and LOWER(name) LIKE LOWER(concat(?1,'%'))")
    List<users> findAllStartingSearchQuery(String param);

    @Query(value = "select u from users u,recordLabel r where u=r.user and LOWER(name) LIKE LOWER(concat(?1,'%'))")
    List<users> findAllRecordsStartingSearchQuery(String param);

    @Query(value = "select u from users u,artist a where u=a.user and LOWER(name) LIKE LOWER(concat('%',?1,'%'))")
    List<users> findAllArtists(String param);

    @Query(value = "select u from users u,recordLabel r where u=r.user and LOWER(name) LIKE LOWER(concat('%',?1,'%'))")
    List<users> findAllRecordsLabels(String param);
}
