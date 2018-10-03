package com.app.MBox.services;

import com.app.MBox.common.customHandler.springChecks;
import com.app.MBox.config.properties;
import com.app.MBox.core.model.artist;
import com.app.MBox.core.model.song;
import com.app.MBox.core.repository.songRepository;
import com.app.MBox.dto.songDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class songServiceImpl implements songService {

    @Autowired
    private songRepository songRepository;
    @Autowired
    private userService userService;
    @Autowired
    private properties properties;
    @Autowired
    private springChecks springChecks;

    @Autowired
    private amazonS3ClientService amazonS3ClientService;
    @Autowired
    configurationService configurationService;

    public static long FILE_SIZE=3*1024*1024;

    public List<song> findByArtistId (int artistId) {
        return songRepository.findByArtistId(artistId);
    }

    public song findById (int id) {
        return songRepository.findById(id);
    }

    @Override
    public List<songDto> getMostRecentSongs(Pageable pageable) {
        List<songDto> songDtos=new LinkedList<>();
        Page<song> songs=songRepository.getMostRecentSongs(pageable);

        songDtos=mapSongToSongDto(songs.getContent());
        return songDtos;
    }

    public String addSong(MultipartFile file, songDto songDto) {
        String result=isValidPicture(file);
        if(result.equals("OK")) {
            song song = new song();
            song.setName(songDto.getSongName());
            song.setAlbumName(songDto.getAlbumName());
            song.setGenre(songDto.getGenre());
            song.setLyrics(songDto.getSongLyrics());
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(songDto.getDateReleased());
                song.setDateOfRelease(date);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            artist artist = springChecks.getLoggedInArtist();
            song.setArtist(artist);
            song.setYoutubeLink(songDto.getYoutubeLink());
            song.setVimeoLink(songDto.getVimeoLink());
            if (!file.isEmpty()) {
                //logic for saving the picture on s3
                String[] extension = file.getContentType().split("/");
                String imageName = String.format("%s.%s",UUID.randomUUID().toString(),extension[1]);
                amazonS3ClientService.uploadFileToS3Bucket(file, false, imageName);
                song.setImage(imageName);
            }

            songRepository.save(song);

        }

        return result;
    }

    public String isValidPicture(MultipartFile file) {

        if(!file.isEmpty()) {
            String name=file.getOriginalFilename();
            String [] extension=name.split("\\.");
            if(!extension[extension.length-1].equals("jpg") && !extension[extension.length-1].equals("jpeg") && !extension[extension.length-1].equals("png")) {
                return "wrongFormat";
            }

            if(file.getSize()>FILE_SIZE) {
                return "sizeExceeded";
            }


        }
        return "OK";
    }

    public List<songDto> findSongs(Pageable pageable) {
        artist artist=springChecks.getLoggedInArtist();
        Page<song> songs=songRepository.findSongs(artist.getId(),pageable);
        List<songDto> songDtos=mapSongToSongDto(songs.getContent());
        return songDtos;
    }

    @Override
    public void deleteSong(int songId) {
        song song=songRepository.findById(songId);
        if(song!=null) {
            amazonS3ClientService.deleteFileFromS3Bucket(song.getImage());
            songRepository.delete(song);
        }
    }

    public List<songDto> searchSongs(String searchParam) {
        artist artist=springChecks.getLoggedInArtist();
        List<song> songs=songRepository.findSongs(artist.getId(),searchParam);
        List<songDto> songDtos=mapSongToSongDto(songs);
        return songDtos;
    }

    public List<songDto> mapSongToSongDto(List<song> songs) {
        List<songDto> songDtos=songs.stream().map(temp-> {
            songDto songDto=new songDto();
            songDto.setAlbumName(temp.getAlbumName());
            songDto.setSongName(temp.getName());
            songDto.setGenre(temp.getGenre());
            songDto.setId(temp.getId());
            songDto.setVimeoLink(temp.getVimeoLink());
            songDto.setYoutubeLink(temp.getYoutubeLink());
            songDto.setArtistName(temp.getArtist().getUser().getName());
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date=simpleDateFormat.format(temp.getDateOfRelease());
            songDto.setDateReleased(date);
            if(temp.getImage()!=null) {
                songDto.setSongImgUrl(amazonS3ClientService.getPictureUrl(temp.getImage()));
            }   else {
                songDto.setSongImgUrl(amazonS3ClientService.getPictureUrl(configurationService.findByKey(properties.getSongDefaultPicture()).getValue()));
            }
            return songDto;
        }).collect(Collectors.toList());
        return songDtos;
    }
}
