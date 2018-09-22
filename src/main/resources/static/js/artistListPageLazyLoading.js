jQuery(document).ready(

	function($) {

        $(window).on("scroll", function () {
            var scrollHeight = $(document).height();
            var scrollPosition = $(window).height() + $(window).scrollTop();
            if ((scrollHeight - scrollPosition) / scrollHeight === 0) {
                    var page=document.getElementById("row_no").value;
                    var size=document.getElementById("size").value;
                    var param="date_created,desc"
                    $.get("/home/artist-lazyLoad", {
                    page:page,
                    size:size,
                    sort:param
                }, function(data) {
                    console.log(data);
                    var html="";
                    for(i=0 ; i<data.length ; i++) {
                        html=html+'<li class="col-sm-2 table-bordered" style="margin-right:3.2%;margin-top:6px">';
                        html=html+'<div><div class="row"><div><div class="text-center text">';
                        html=html+'<h5>' + data[i].name + '</h5></div></div></div>';
                        html=html+'<div class="row"><div><img class="img-circle img-responsive artistAlign" style="width: 80%;height: 80%;margin-left: auto;margin-right: auto;" src="' + data[i].pictureUrl + '" />';
                        html=html+'</div></div><div class="row"<div><div class="text-center text">';
                        html=html+'<h5>' + data[i].recordLabelName + '</h5>';
                        html=html+'</div></div></div></div></li>';
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