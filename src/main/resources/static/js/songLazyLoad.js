jQuery(document).ready(

	function($) {

        $(window).on("scroll", function () {
            var scrollHeight = $(document).height();
            var scrollPosition = $(window).height() + $(window).scrollTop();
            if ((scrollHeight - scrollPosition) / scrollHeight === 0) {
                    var page=document.getElementById("row_no").value;
                    var size=document.getElementById("size").value;
                    var param="dateCreated,desc";
                    var artistId=document.getElementById("artistId").value;
                    $.get("/home/song-lazy-load", {
                    page:page,
                    size:size,
                    sort:param,
                    artistId:artistId
                }, function(data) {
                    var html="";
                    for(i=0 ; i<data.length ; i++) {
                        html=html+'<li class="col-sm-3 card-container-song table-bordered" style="margin-right:3.2%; margin-bottom: 2%;height:240px"><div><div class="row card-song-name"><div class="text-center text">'
                        html=html+'<a href="/home/song?id=' + data[i].id + '"><h5>' + data[i].songName + '</h5></a>';
                        html=html+'</div></div><div class="row card-song-img-hold">';
                        html=html+'<img class="card-song-img" src="' + data[i].songImgUrl + '" /></div><div class="row out-link-social"><div class="col-sm-6">';
                        html=html+'<a target=”_blank” href="' + data[i].youtubeLink + '">Youtube</a></div><div class="col-sm-6">' ;
                        html=html+'<a target=”_blank” href="' + data[i].vimeoLink + '">Vimeo</a></div></div></div></li>';

                        }
                        $("#songListId").append(html);
                        document.getElementById("row_no").value=Number(page)+1;
                }).done(function() {
                }).fail(function(xhr, textStatus, errorThrown) {
                }).complete(function() {

                });
                  }
        });
	});