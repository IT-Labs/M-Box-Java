jQuery(document).ready(

	function($) {
        var pixelsScrolled=600;
		$("#bodyScroll").scroll(function () {

            var scrolltop=$('#bodyScroll').scrollTop();
            console.log(scrolltop);
		    if(scrolltop==pixelsScrolled) {
		    pixelsScrolled+=1220;
            var page=document.getElementById("row_no").value;
            var size=document.getElementById("size").value;
			$.get("/record-label/pageable-artists", {
			page:page,
			size:size
		}, function(data) {
			var html="";
            for(i=0 ; i<data.length ; i++) {
                html=html+'<tr><form role="form" action="delete" method="post" id="form1">';
                html=html+'<td style="width: 30%;">' + '<img src="' + data[i].pictureUrl + '" style="width:8%;"</img></td>';
                html=html+'<td style="width: 27%;">' + data[i].name + '</td>';
                html=html+'<td style="width: 36%;">' + data[i].email + '</td>';
                html=html+'<input type="hidden" name="email" value=' + data[i].email + '>';
                html=html+'<input type="hidden" name="name" value=' + data[i].name +'>';
                html=html+'<td style="width: 15%;"><button type="submit" class="btn btn-danger" id="' + data[i].name + '" name="' + data[i].email + '" onclick="deleteFunction(this.id,this.name)">DELETE<i class="fa fa-trash"></i></button></td>';
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