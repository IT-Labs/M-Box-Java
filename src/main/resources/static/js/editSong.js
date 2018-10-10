window.onload = function () {
    var previosValue = "";
    var savedValue="";
    var today = new Date();
            var dd = today.getDate();
            var mm = today.getMonth()+1; //January is 0!
            var yyyy = today.getFullYear();
             if(dd<10){
                    dd='0'+dd
                }
                if(mm<10){
                    mm='0'+mm
                }

            var today = yyyy+'-'+mm+'-'+dd;
            document.getElementById("date").setAttribute("max", today);
}

function editDetails(id) {
    result=id.split(",");
    document.getElementById(result[0]).disabled = false;
    if(result[0]=="date") {
               document.getElementById(result[0]).style.display = 'block';
            }
     document.getElementById(result[1]).style.display = 'none';
    document.getElementById(result[2]).style.display = 'block';
    document.getElementById(result[3]).style.display = 'block';
    previosValue=document.getElementById(result[0]).value;

}

function saveDetails(id) {
document.getElementById("nameError").style.display='none';
document.getElementById("albumError").style.display='none';
result=id.split(",");

    if(result[0]=="date") {
               document.getElementById(result[0]).style.display = 'none';
               var parts=document.getElementById(result[0]).value.split("-");
               var date=parts[2]+"-"+parts[1]+"-" + parts[0];
               document.getElementById("dateLabel").innerText=date;
            }

      if(document.getElementById("songName").value.length>50 || document.getElementById("songName").value.length<2) {
        document.getElementById("nameError").style.display='block';
                return;
      }

      if(document.getElementById("albumName").value.length>50 || document.getElementById("albumName").value.length<2) {
              document.getElementById("albumError").style.display='block';
                      return;
            }

    document.getElementById(result[0]).disabled = true;
    document.getElementById(result[1]).style.display = 'block';
    document.getElementById(result[2]).style.display = 'none';
    document.getElementById(result[3]).style.display = 'none';
    formSubmit();
}

function cancelEditing(id) {
    document.getElementById("nameError").style.display='none';
    document.getElementById("albumError").style.display='none';
    result=id.split(",");
    document.getElementById(result[0]).value = previosValue;
    document.getElementById(result[0]).disabled = true;
    if(result[0]=="date") {
               document.getElementById(result[0]).style.display = 'none';
               var parts=previosValue.split("-");
               var date=parts[2]+"-"+parts[1]+"-" + parts[0];
               document.getElementById("dateLabel").innerText=date;
            }
    document.getElementById(result[1]).style.display = 'block';
    document.getElementById(result[2]).style.display = 'none';
    document.getElementById(result[3]).style.display = 'none';
}

function editLink(link, url, edit, save, cancel) {
    savedValue = document.getElementById(url).value;

    $('#' + url).show();
    $('#' + save).show();
    $('#' + cancel).show();
    $('#' + edit).hide();
}

function saveLink(link, url, edit, save, cancel) {
     document.getElementById("youtubeError").style.display='none';
     document.getElementById("vimeoError").style.display='none';
    if (isInvalid(document.getElementById("youtubeLinkUrl").value,0) ||document.getElementById("youtubeLinkUrl").value.length>99 ) {
        document.getElementById("youtubeError").style.display='block';
        return;
    }

    if (isInvalid(document.getElementById("vimeoLinkUrl").value,1) ||document.getElementById("vimeoLinkUrl").value.length>99 ) {
          document.getElementById("vimeoError").style.display='block';
          return;
        }

    var saveUrl = document.getElementById(url).value;
    $('#' + link).attr('href', saveUrl);

    $('#' + url).hide();
    $('#' + save).hide();
    $('#' + cancel).hide();
    $('#' + edit).show();
    formSubmit();
}

function cancelEditingLink(link, url, edit, save, cancel) {
    document.getElementById("youtubeError").style.display='none';
    document.getElementById("vimeoError").style.display='none';
    document.getElementById(url).value = savedValue;

    $('#' + url).hide();
    $('#' + save).hide();
    $('#' + cancel).hide();
    $('#' + edit).show();
}

function editGenre1(edit, save, cancel, genre) {
    savedValue = document.getElementById("textGenre").innerText;
    document.getElementById(edit).style.display = 'none';
    document.getElementById(save).style.display = 'block';
    document.getElementById(cancel).style.display = 'block';
    document.getElementById(genre).style.display = 'block';
        }

function saveGenre1(edit, save, cancel, genre) {
    document.getElementById(save).style.display = 'none';
    document.getElementById(cancel).style.display = 'none';
    document.getElementById(edit).style.display = 'block';
    document.getElementById(genre).style.display = 'none';
    document.getElementById("textGenre").innerText=document.getElementById(genre).value;
    formSubmit();
}

function cancelGenre1(edit, save, cancel, genre) {
    document.getElementById(save).style.display = 'none';
    document.getElementById(cancel).style.display = 'none';
    document.getElementById(edit).style.display = 'block';
    document.getElementById(genre).style.display = 'none';
    document.getElementById("textGenre").innerText=savedValue;
}

function formSubmit() {

        var songName=document.getElementById("songName").value;
        var albumName=document.getElementById("albumName").value;
        var songLyrics=document.getElementById("songLyrics").value;
        var youtubeLink=document.getElementById("youtubeLinkUrl").value;
        var vimeoLink=document.getElementById("vimeoLinkUrl").value;
        var genre=document.getElementById("genre").value;
        var id=document.getElementById("id").value;
        var dateReleased=document.getElementById("date").value;
        var songDto={songName:songName,albumName:albumName,songLyrics:songLyrics,youtubeLink:youtubeLink,vimeoLink:vimeoLink,genre:genre,id:id,dateReleased:dateReleased};
        $.ajax({
         url: "/artist/edit-song",
         headers: {"X-CSRF-TOKEN": $("input[name='_csrf']").val()},
         type: "post",
         data:songDto,
         success: function (data) {
         },
     });
     }

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