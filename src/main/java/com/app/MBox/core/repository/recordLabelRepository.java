package com.app.MBox.core.repository;

import com.app.MBox.core.model.recordLabel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface recordLabelRepository extends CrudRepository<recordLabel,Integer> {

    recordLabel findByUserId(int userId);

    @Query(value = "select * from record_label" ,nativeQuery=true)
    List<recordLabel> findAllRecordLabels(Pageable pageable);

}
