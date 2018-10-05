package com.app.MBox.services;

import com.app.MBox.common.customException.emailAlreadyExistsException;
import com.app.MBox.core.model.*;
import com.app.MBox.dto.artistDto;
import com.app.MBox.dto.csvParseResultDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public interface artistService {

    List<artist> findAllArtists(int recordLabelId);

    List<artist> findAllArtists(int recordLabelId, Pageable pageable);

    void save(artist artist);

    artist findByUserId(int userId);

    users inviteArtist(String name, String email, HttpServletRequest request) throws emailAlreadyExistsException ;

    users createUser(String name,String email) ;

    csvParseResultDto addArtistsByCsvFile(MultipartFile file, HttpServletRequest request) throws Exception;

    void deleteArtist(String email);

    List<artistDto> findRecentlyAddedArtist(Pageable pageable);

    List<artistDto> findAllArtists(Pageable pageable);

    List<artistDto> mapArtistToArtistDto(List<artist> artists);

    artist findById(int id);

    String addPicture(MultipartFile file,int id);

    void saveArtist(artistDto artist);
}
