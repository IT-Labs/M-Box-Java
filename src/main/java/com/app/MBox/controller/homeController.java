package com.app.MBox.controller;



import com.app.MBox.common.customHandler.springChecks;
import com.app.MBox.common.enumeration.rolesEnum;
import com.app.MBox.config.properties;
import com.app.MBox.core.model.artist;
import com.app.MBox.core.model.song;
import com.app.MBox.dto.aboutMessageDto;
import com.app.MBox.dto.artistDto;
import com.app.MBox.dto.recordLabelDto;
import com.app.MBox.dto.songDto;
import com.app.MBox.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Controller
@RequestMapping(value = "/home")
public class homeController {
    @Autowired
    private songService songService;
    @Autowired
    private artistService artistService;
    @Autowired
    private recordLabelService recordLabelService;
    @Autowired
    private emailService emailService;
    @Autowired
    private properties properties;
    @Autowired
    private springChecks springChecks;
    @Autowired
    private userService userService;

    public static int INITIAL_PAGE=0;
    public static int INITIAL_SIZE=25;
    public static int INITIAL_HOMEPAGE_SIZE=5;
    public static String INITIAL_SORT_PARAMETAR="dateCreated";
    public static Sort.Direction INITIAL_SORT_DIRECTION=Sort.Direction.DESC;

    @GetMapping("/homepage")
    public ModelAndView home(ModelAndView modelAndView, Model model) {
        List<songDto> songs=songService.getMostRecentSongs(PageRequest.of(INITIAL_PAGE,INITIAL_HOMEPAGE_SIZE,INITIAL_SORT_DIRECTION,INITIAL_SORT_PARAMETAR));
        List<artistDto> artists=artistService.findRecentlyAddedArtist(PageRequest.of(INITIAL_PAGE,INITIAL_HOMEPAGE_SIZE,INITIAL_SORT_DIRECTION,INITIAL_SORT_PARAMETAR));
        model.addAttribute("artists",artists);
        model.addAttribute("songs",songs);
        modelAndView.setViewName("home");
        return modelAndView;

    }

    @RequestMapping(value = "/artist")
    public ModelAndView showArtists(ModelAndView modelAndView,Model model) {
        List<artistDto> artists=artistService.findAllArtists(PageRequest.of(INITIAL_PAGE,INITIAL_SIZE,INITIAL_SORT_DIRECTION,INITIAL_SORT_PARAMETAR));
        model.addAttribute("artists",artists);
        modelAndView.setViewName("artistsListPage");
        return modelAndView;
    }

    @RequestMapping(value = "/record-label",method = RequestMethod.GET)
    public ModelAndView showRecordLabels(ModelAndView modelAndView,Model model) {
        List<recordLabelDto> recordLabels=recordLabelService.getRecordLabels(PageRequest.of(INITIAL_PAGE,INITIAL_SIZE,INITIAL_SORT_DIRECTION,INITIAL_SORT_PARAMETAR));
        model.addAttribute("recordLabels",recordLabels);
        modelAndView.setViewName("recordLabelsListPage");
        return modelAndView;
    }

    @RequestMapping(value = "/lazy-load-artist",method = RequestMethod.GET)
    @ResponseBody
    public List<artistDto> processArtistLazyLoad(Pageable pageable) {
        List<artistDto> artists=artistService.findAllArtists(pageable);
        return artists;
    }

    @RequestMapping(value = "/lazy-load-record-label",method = RequestMethod.GET)
    @ResponseBody
    public List<recordLabelDto> processRecordLabelLazyLoad(Pageable pageable) {
        List<recordLabelDto> recordLabels=recordLabelService.getRecordLabels(pageable);
        return recordLabels;
    }

    @RequestMapping(value = "/about",method = RequestMethod.GET)
    public ModelAndView showAbout(ModelAndView modelAndView,Model model) {
        List<recordLabelDto> recordLabels=recordLabelService.getAllRecordLabels();
        aboutMessageDto aboutMessageDto=new aboutMessageDto();
        model.addAttribute("recordLabels",recordLabels);
        model.addAttribute("aboutMessageDto",aboutMessageDto);
        modelAndView.setViewName("about");
        return modelAndView;
    }

    @RequestMapping(value = "/about",method = RequestMethod.POST)
    public ModelAndView proccesAbout(ModelAndView modelAndView,@ModelAttribute("aboutMessageDto") @Valid aboutMessageDto aboutMessageDto) {
        emailService.sendAboutEmail(aboutMessageDto);
        modelAndView.addObject("successfullMessage",properties.getSuccessfullMessage());
        modelAndView.setViewName("about");
        return modelAndView;
    }

    @RequestMapping(value = "/song",method = RequestMethod.GET)
    public ModelAndView showSongDetails(ModelAndView modelAndView, Model model, @RequestParam("id") int id, @RequestParam("search")Optional<String> search) {

        if(search.isPresent()){
            modelAndView.addObject("search",search.get());
        }
        songDto song=songService.findAndMapSong(id);
        model.addAttribute("song",song);
        String role=springChecks.getLoggedInUserRole();
        if(rolesEnum.ARTIST.toString().equals(role) && springChecks.getLoggedInArtist().getId()==song.getArtistId()) {
         modelAndView.setViewName("artistEditSong");
         songDto songDto=new songDto();
         model.addAttribute("songDto",songDto);
        }else {
            modelAndView.setViewName("songDetails");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/artist-details",method = RequestMethod.GET)
    public ModelAndView showArtistDetails(ModelAndView modelAndView,Model model,@RequestParam("id") int id, @RequestParam("search")Optional<String> search) {
        if(search.isPresent()){
            modelAndView.addObject("search",search.get());
        }
        artistDto artist=artistService.findAndMapArtistById(id);
        model.addAttribute("artist",artist);
        List<song> songs=songService.findByArtistId(id,PageRequest.of(INITIAL_PAGE,INITIAL_SIZE-INITIAL_HOMEPAGE_SIZE,INITIAL_SORT_DIRECTION,INITIAL_SORT_PARAMETAR));
        List<songDto> songDtos=songService.mapSongToSongDto(songs);
        model.addAttribute("songs",songDtos);
        modelAndView.setViewName("artistDetails");
        return modelAndView;
    }

    @RequestMapping(value = "/song-lazy-load",method = RequestMethod.GET)
    @ResponseBody
    public List<songDto> processPageableSongs(Pageable pageable,@RequestParam int artistId) {
        List<song> songs=songService.findByArtistId(artistId,pageable);
        List<songDto>songDtos=songService.mapSongToSongDto(songs);
        return songDtos;
    }

    @RequestMapping(value = "/record-label-details",method = RequestMethod.GET)
    public ModelAndView showRecordLabelDetails(ModelAndView modelAndView,Model model,@RequestParam("id") int id, @RequestParam("search")Optional<String> search) {
        if(search.isPresent()){
            modelAndView.addObject("search",search.get());
        }
        recordLabelDto recordLabel=recordLabelService.findRecordLabel(id);
        model.addAttribute("recordLabel",recordLabel);
        List<artist> artists=artistService.findAllArtists(recordLabel.getId());
        List<artistDto> artistDtos=artistService.mapArtistToArtistDto(artists);
        model.addAttribute("artists",artistDtos);
        modelAndView.setViewName("recordLabelDetails");
        return modelAndView;
    }

    @RequestMapping(value = "search",method = RequestMethod.GET)
    public ModelAndView showSearchDetails(ModelAndView modelAndView,Model model,@RequestParam("searchParam") String param) {
        List<Object> result=results(param);
        model.addAttribute("result",result);
        modelAndView.setViewName("searchResults");
        return modelAndView;
    }



    private List<Object> results(String param) {

        List<songDto> songsMatchName=songService.searchAllExactMatchSongs(param);
        List<artistDto> artistsMatch=userService.searchAllExactMatchArtists(param);
        List<recordLabelDto> recordLabelsMatch=userService.searchAllExactMatchRecords(param);
        List<songDto> songsMatchLyrics=songService.searchAllExactMatchSongsLyrics(param);

        List<songDto> songsStartSearchQuery=songService.searchAllStartingSearchQuery(param);
        List<artistDto> artistsMatchStartSearchQuery=userService.searchAllStartingSearchQuery(param);
        List<recordLabelDto> recordLabelsMatchStartSearchQuery=userService.searchAllRecordsStartingSearchQuery(param);
        List<songDto> songsStartSearchQueryLyrics=songService.searchAllStartingSearchQueryLyrics(param);

        List<songDto> songs=songService.searchAllSongs(param);
        List<artistDto> artists=userService.searchAllArtists(param);
        List<recordLabelDto> recordLabels=userService.searchAllRecordsLabels(param);
        List<songDto> songsLyrics=songService.searchAllSongsLyrics(param);

        songsStartSearchQuery.removeAll(songsMatchName);
        songs.removeAll(songsMatchName);
        songs.removeAll(songsStartSearchQuery);

        artistsMatchStartSearchQuery.removeAll(artistsMatch);
        artists.removeAll(artistsMatch);
        artists.removeAll(artistsMatchStartSearchQuery);

        recordLabelsMatchStartSearchQuery.removeAll(recordLabelsMatch);
        recordLabels.removeAll(recordLabelsMatch);
        recordLabels.removeAll(recordLabelsMatchStartSearchQuery);

        songsStartSearchQueryLyrics.removeAll(songsMatchLyrics);
        songsLyrics.removeAll(songsMatchLyrics);
        songsLyrics.removeAll(songsStartSearchQueryLyrics);
        songsMatchLyrics.removeAll(songsMatchName);
        songsLyrics.removeAll(songsMatchName);
        songsStartSearchQueryLyrics.removeAll(songsMatchName);

        songsMatchLyrics.removeAll(songsStartSearchQuery);
        songsLyrics.removeAll(songsStartSearchQuery);
        songsStartSearchQueryLyrics.removeAll(songsStartSearchQuery);

        songsMatchLyrics.removeAll(songs);
        songsLyrics.removeAll(songs);
        songsStartSearchQueryLyrics.removeAll(songs);

        List<Object> result=new LinkedList<>();
        Stream.of(songsMatchName,artistsMatch,recordLabelsMatch,songsMatchLyrics,songsStartSearchQuery,
                artistsMatchStartSearchQuery,recordLabelsMatchStartSearchQuery,songsStartSearchQueryLyrics,
                songs,artists,recordLabels,songsLyrics).forEach(result::addAll);
        return result;
    }
}
