package com.app.MBox.services;

import com.app.MBox.common.customException.emailAlreadyExistsException;
import com.app.MBox.core.model.recordLabel;
import com.app.MBox.core.model.users;
import com.app.MBox.dto.recordLabelDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Component
public interface recordLabelService {

     recordLabel findByUserId(int userId);

     recordLabel saveRecordLabel(recordLabel recordLabel) ;


     recordLabel createRecordLabel(String name,String email,HttpServletRequest request) throws emailAlreadyExistsException;


     users createUser(String name, String email) ;


     void deleteRecordLabel(String email);

     List<recordLabelDto> getRecordLabels(Pageable pageable);

     List<recordLabelDto> getAllRecordLabels();

     Optional<recordLabel> findById(int id);

     recordLabelDto findRecordLabel(int id);


     void saveRecordLabel(recordLabelDto recordLabelDto);

     String addPicture(MultipartFile file, int id);
}
