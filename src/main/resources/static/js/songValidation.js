function isInvalid (url,type) {
   if(url.match(/(http:|https:|)?\/?\/?(player.|www.)?(vimeo\.com|youtu(be\.com|\.be|be\.googleapis\.com))\/(video\/|embed\/|watch\?v=|v\/)?([A-Za-z0-9._%-]*)(\&\S+)?/)){
    if (RegExp.$3.indexOf('youtu') > -1 && type=="1") {
        return true;
    } else if (RegExp.$3.indexOf('vimeo') > -1 && type=="0") {
        return true;
    }   else {
        return false;
    }

    }
    return true;

}


        $(document).ready(function () {

	$("#form").submit(function(event) {
	   $("#nameError").show().html("");
	   $("#albumNameError").show().html("");
	   $("#youtubeLinkError").show().html("");
	   $("#vimeoLinkError").show().html("");
	   $("#songLyricsError").show().html("");
	    var name=$("#name").val();
	    var albumName=$("#albumName").val();
	    var youtubeLink=$("#youtubeLinkUrl").val();
	    var vimeoLink=$("#vimeoLinkUrl").val();
	    var lyrics=$("#songLyrics").val();
	    var counter=0;

        if (!name.replace(/\s/g, '').length) {
        $("#nameError").show().html("Name must be between 1 and 50 characters");
            counter++;
        }
        if ($("#name").val().length<2 || $("#name").val().length>50) {
        $("#nameError").show().html("Name must be between 1 and 50 characters");
    	counter++;
        }

          if (!albumName.replace(/\s/g, '').length) {
                 $("#albumNameError").show().html("Album name must be between 1 and 50 characters");
                     counter++;
                 }


        if(albumName.length<2 || albumName.length>50) {
        $("#albumNameError").show().html("Album name must be between 1 and 50 characters");
    	counter++;
        }

        if(lyrics.length>10000) {
         $("#songLyricsError").show().html("Lyrics can not contain more than 10000 characters");
    	counter++;
        }

        if(isInvalid(youtubeLink,0) || youtubeLink.length>99) {
        counter++;
        $("#youtubeLinkError").show().html("It must be a youtube link");
        }
        if(isInvalid(vimeoLink,1)|| vimeoLink.length>99) {
        counter++;
        $("#vimeoLinkError").show().html("It must be a vimeo link");
        }

        if(counter>0) {
        event.preventDefault();
        return;
    }

	})




	})
