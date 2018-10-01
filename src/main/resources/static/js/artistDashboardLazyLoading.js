jQuery(document).ready(

	function($) {
        var pixelsScrolled=400;
		$("#bodyScroll").scroll(function () {

            var scrolltop=$('#bodyScroll').scrollTop();
            console.log(scrolltop);
		    if(scrolltop==pixelsScrolled) {
		    pixelsScrolled+=1000;
            var page=document.getElementById("row_no").value;
            var size=document.getElementById("size").value;
			$.get("/pageable-songs", {
			page:page,
            size:size
		}, function(data) {
			console.log(data);
			var html="";
            for(i=0 ; i<data.length ; i++) {
                html=html+'<tr><form role="form" action="delete" method="post" id="form">';
                html=html+'<td style="width: 15%;">' + data[i].songName + '</td>';
                html=html+'<td style="width: 15%;">' + data[i].albumName + '</td>';
                html=html+'<td style="width: 15%;">' + data[i].genre + '</td>';
                html=html+'<input type="hidden" name="email" value=' + data[i].id + '>';
                html=html+'<input type="hidden" name="name" value=' + data[i].songName +'>';
                html=html+'<td style="width: 15%;"><button type="submit" class="btn btn-danger" id="' + data[i].songName + '" name="' + data[i].id + '" onclick="deleteFunction(this.id,this.name)">DELETE<i class="fa fa-trash"></i></button></td>';
                html=html+'</form></tr>';
                }
                $("#bodyScroll").append(html);
                document.getElementById("row_no").value=Number(page)+1;
		}).done(function() {
		}).fail(function(xhr, textStatus, errorThrown) {
		}).complete(function() {


		});


                    }


		});


	});