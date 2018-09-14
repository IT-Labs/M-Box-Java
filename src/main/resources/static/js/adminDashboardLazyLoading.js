jQuery(document).ready(

	function($) {
        var pixelsScrolled=240;
		$("#bodyScroll").scroll(function () {

            var scrolltop=$('#bodyScroll').scrollTop();
            console.log(scrolltop);
		    if(scrolltop==pixelsScrolled) {
		    pixelsScrolled+=860;
            var page=document.getElementById("row_no").value;

			$.get("/admin/lazyLoad", {
			page:page
		}, function(data) {
			console.log(data);
			var html="";
            for(i=0 ; i<data.length ; i++) {
                html=html+'<tr><form role="form" action="delete" method="post" id="form1">';
                html=html+'<td style="width: 15%;">' + data[i].name + '</td>';
                html=html+'<td style="width: 15%;">' + data[i].email + '</td>';
                html=html+'<td style="width: 15%;">' + data[i].number + '</td>';
                html=html+'<input type="hidden" name="email" value=' + data[i].email + '>';
                html=html+'<input type="hidden" name="name" value=' + data[i].name +'>';
                html=html+'<td style="width: 15%;"><input type="submit" value="DELETE" id="' + data[i].name + '" name="' + data[i].email + '" onclick="myFunction(this.id,this.name)"></td>';
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