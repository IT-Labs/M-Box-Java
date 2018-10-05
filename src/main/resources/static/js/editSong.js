window.onload = function () {
    var previosValue = "";
    var savedValue="";
}

function editDetails(id) {
    result=id.split(",");
    document.getElementById(result[0]).disabled = false;
     document.getElementById(result[1]).style.display = 'none';
    document.getElementById(result[2]).style.display = 'block';
    document.getElementById(result[3]).style.display = 'block';
    previosValue=document.getElementById(result[0]).value;

}

function saveDetails(id) {
result=id.split(",");
    document.getElementById(result[0]).disabled = true;
    document.getElementById(result[1]).style.display = 'block';
    document.getElementById(result[2]).style.display = 'none';
    document.getElementById(result[3]).style.display = 'none';
    formSubmit();
}

function cancelEditing(id) {
    result=id.split(",");
    document.getElementById(result[0]).value = previosValue;
    document.getElementById(result[0]).disabled = true;
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
    var saveUrl = document.getElementById(url).value;
    $('#' + link).attr('href', saveUrl);

    $('#' + url).hide();
    $('#' + save).hide();
    $('#' + cancel).hide();
    $('#' + edit).show();
    formSubmit();
}

function cancelEditingLink(link, url, edit, save, cancel) {
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
        var songDto={songName:songName,albumName:albumName,songLyrics:songLyrics,youtubeLink:youtubeLink,vimeoLink:vimeoLink,genre:genre,id:id};
        $.ajax({
         url: "/artist/editSong",
         headers: {"X-CSRF-TOKEN": $("input[name='_csrf']").val()},
         type: "post",
         data:songDto,
         success: function (data) {
         },
     });
     }