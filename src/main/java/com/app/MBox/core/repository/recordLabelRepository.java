package com.app.MBox.core.repository;

import com.app.MBox.core.model.recordLabel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface recordLabelRepository extends CrudRepository<recordLabel,Integer> {

    recordLabel findByUserId(int userId);

    @Query(value = "select r from recordLabel r")
    List<recordLabel> findAllRecordLabels(Pageable pageable);

    Optional<recordLabel> findById(int id);

}
