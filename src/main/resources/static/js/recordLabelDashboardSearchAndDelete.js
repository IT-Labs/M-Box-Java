 function deleteFunction(name,email) {
     if(confirm("Are you sure you want do delete " + name)) {

           $.get("/record-label/delete", {
 			email:email
 		}, function(data) {
             redirectToDashboard();
 		}).done(function() {
 		}).fail(function(xhr, textStatus, errorThrown) {
 		}).complete(function() {
             redirectToDashboard();
 		});

     }
             }



 function mySearch() {
            var searchParam=document.getElementById("search").value
            $.get("/record-label/search", {
			searchParam:searchParam
		}, function(data) {
		      if(data.length>0) {
			var html="";
            for(i=0 ; i<data.length ; i++) {
                html=html+'<tr><form role="form" action="delete" method="post" id="form1">';
                html=html+'<td style="width: 30%;">' + '<img src="' + data[i].pictureUrl + '" style="width:8%;"</img></td>';
                html=html+'<td style="width: 27%;">' + data[i].name + '</td>';
                html=html+'<td style="width: 36%;">' + data[i].email + '</td>';
                html=html+'<input type="hidden" name="email" value=' + data[i].email + '>';
                html=html+'<input type="hidden" name="name" value=' + data[i].name +'>';
                html=html+'<td style="width: 15%;"><input type="submit" value="DELETE" id="' + data[i].name + '" name="' + data[i].email + '" onclick="deleteFunction(this.id,this.name)"></td>';
                html=html+'</form></tr>';
                }
                $("#bodyScroll").empty().append(html);
                }   else {
                     redirectToDashboard();
                }
		}).done(function() {
		}).fail(function(xhr, textStatus, errorThrown) {
            redirectToDashboard();
		}).complete(function() {


		});



    }