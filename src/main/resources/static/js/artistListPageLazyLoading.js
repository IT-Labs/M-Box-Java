jQuery(document).ready(

	function($) {

        $(window).on("scroll", function () {
            var scrollHeight = $(document).height();
            var scrollPosition = $(window).height() + $(window).scrollTop();
            if ((scrollHeight - scrollPosition) / scrollHeight === 0) {
                    var page=document.getElementById("row_no").value;
                    var size=document.getElementById("size").value;
                    var param="dateCreated,desc"
                    $.get("/home/lazy-load-artist", {
                    page:page,
                    size:size,
                    sort:param
                }, function(data) {
                    var html="";
                    for(i=0 ; i<data.length ; i++) {
                        html=html+'<li class="col-sm-2 table-bordered card-container" style="margin-right:3.2%;margin-top:6px">';
                        html=html+'<div class="row card-song-name"><div><div class="text-center text">';
                        html=html+'<a href="/home/artist-details?id='+ data[i].id + '"><h5>' + data[i].name + '</h5></a></div></div></div>';
                        html=html+'<a href="/home/artist-details?id='+ data[i].id + '"><div class="row card-song-img-hold"><img class="card-song-img" src="' + data[i].pictureUrl + '" />';
                        html=html+'</div></a><div class="row"<div><div class="text-center text">';
                        html=html+'<a href="record-label-details?id=' + data[i].recordLabelId +'"><h5>' + data[i].recordLabelName + '</h5></a>';
                        html=html+'</div></div></div></li>';
                        }
                        $("#recordLabelsListId").append(html);
                        document.getElementById("row_no").value=Number(page)+1;
                }).done(function() {
                }).fail(function(xhr, textStatus, errorThrown) {
                }).complete(function() {

                });
                  }
        });
	});