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
    private properties properties;
    @Autowired
    private springChecks springChecks;
    @Autowired
    private amazonS3ClientService amazonS3ClientService;
    @Autowired
    configurationService configurationService;

    public static long FILE_SIZE=3*1024*1024;

    public List<song> findByArtistId (int artistId,Pageable pageable) {
        return songRepository.findByArtistId(artistId,pageable);
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
            String [] youtube=songDto.getYoutubeLink().split("//");
            String [] vimeo=songDto.getVimeoLink().split("//");
            if(youtube.length>1) {
                song.setYoutubeLink(songDto.getYoutubeLink());
            }   else {
                song.setYoutubeLink(String.format("//%s",songDto.getYoutubeLink()));
            }

            if(vimeo.length>1) {
                song.setVimeoLink(songDto.getVimeoLink());
            }   else {
                song.setVimeoLink(String.format("//%s",songDto.getVimeoLink()));
            }

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
        List<songDto> songDtos=songs.stream().map(song-> {
            songDto songDto=new songDto();
            songDto.setAlbumName(song.getAlbumName());
            songDto.setSongName(song.getName());
            songDto.setGenre(song.getGenre());
            songDto.setId(song.getId());
            songDto.setSongLyrics(song.getLyrics());
            songDto.setVimeoLink(song.getVimeoLink());
            songDto.setYoutubeLink(song.getYoutubeLink());
            songDto.setArtistName(song.getArtist().getUser().getName());
            songDto.setArtistId(song.getArtist().getId());
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date=simpleDateFormat.format(song.getDateOfRelease());
            songDto.setDateReleased(date);
            if(song.getImage()!=null) {
                songDto.setSongImgUrl(amazonS3ClientService.getPictureUrl(song.getImage()));
            }   else {
                songDto.setSongImgUrl(amazonS3ClientService.getPictureUrl(configurationService.findByKey(properties.getSongDefaultPicture()).getValue()));
            }
            return songDto;
        }).collect(Collectors.toList());
        return songDtos;
    }

    public void saveSong(songDto songDto)  {

        song song=findById(songDto.getId());
        song.setLyrics(songDto.getSongLyrics());
        song.setName(songDto.getSongName());
        song.setVimeoLink(songDto.getVimeoLink());
        song.setYoutubeLink(songDto.getYoutubeLink());
        song.setAlbumName(songDto.getAlbumName());
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(songDto.getDateReleased());
            song.setDateOfRelease(date);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        song.setGenre(songDto.getGenre());
        songRepository.save(song);
    }

    public String addPicture(MultipartFile file, int songId) {

        String result=isValidPicture(file);
        if(result.equals("OK") && !file.isEmpty()) {
            song song=findById(songId);
            String[] extension = file.getContentType().split("/");
            String imageName = String.format("%s.%s", UUID.randomUUID().toString(),extension[1]);
            amazonS3ClientService.uploadFileToS3Bucket(file, false, imageName);
            song.setImage(imageName);
            songRepository.save(song);
        }
        return result;
    }

    public List<songDto> searchAllExactMatchSongs(String searchParam) {
        List<song> songs=songRepository.findAllExactMatchSongs(searchParam);
        List<songDto> songDtos=mapSongToSongDto(songs);
        return songDtos;
    }

    public List<songDto> searchAllExactMatchSongsLyrics(String searchParam) {
        List<song> songs=songRepository.findAllExactMatchSongsLyrics(searchParam);
        List<songDto> songDtos=mapSongToSongDto(songs);
        return songDtos;

    }

    public List<songDto> searchAllStartingSearchQuery(String searchParam) {
        List<song> songs=songRepository.findAllStartingSearchQuery(searchParam);
        List<songDto> songDtos=mapSongToSongDto(songs);
        return songDtos;

    }

    public List<songDto> searchAllStartingSearchQueryLyrics(String param) {
        List<song> songs=songRepository.findAllStartingSearchQueryLyrics(param);
        List<songDto> songDtos=mapSongToSongDto(songs);
        return songDtos;
    }

    public List<songDto> searchAllSongs(String param) {

        List<song> songs=songRepository.findAllSongs(param);
        List<songDto> songDtos=mapSongToSongDto(songs);
        return songDtos;
    }

    public  List<songDto> searchAllSongsLyrics(String param) {
        List<song> songs=songRepository.findAllSongsLyrics(param);
        List<songDto> songDtos=mapSongToSongDto(songs);
        return songDtos;
    }

    public songDto findAndMapSong(int id) {
        song song=findById(id);
        List<song> songs=new LinkedList<>();
        songs.add(song);
        List<songDto> songDtos=mapSongToSongDto(songs);
        return songDtos.get(0);
    }
}
