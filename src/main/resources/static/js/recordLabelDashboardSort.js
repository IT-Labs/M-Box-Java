 $(document).ready(function () {
            var counterName=0;
            var counterEmail=0;
            $("#artistName").click(function() {
                handle(1);
            });

            $("#artistEmail").click(function() {
                handle(2);
            });


        function handle(param) {
            if(param==1) {
                var name="name";
                if(counterName%2==0) {
                $("#downName").show();
                $("#upName").hide();
                sort(name,counterName);
                counterName++;
              } else {
                $("#downName").hide();
                $("#upName").show();
                sort(name,counterName);
                counterName--;
              }
            }   else if (param==2) {
                var email="email";
                if(counterEmail%2==0) {
                $("#downEmail").show();
                $("#upEmail").hide();
                sort(email,counterEmail);
                 counterEmail++;
              } else {
                $("#downEmail").hide();
                $("#upEmail").show();
                sort(email,counterEmail);
                 counterEmail--;
              }

            }
        }


        function sort(sortParam,direction) {
        var page=document.getElementById("row_no").value;
        page=Number(page)-1;
        var size=document.getElementById("size").value;
        var calculatedSize=page*size+size;
        if(direction==0) {
            sortParam=sortParam+",desc";
        }   else {
            sortParam=sortParam+",asc";
        }
        $.get("/record-label/sort", {
			page:0,
			size:calculatedSize,
			sort:sortParam
		}, function(data) {
			console.log(data);
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
		}).done(function() {
		}).fail(function(xhr, textStatus, errorThrown) {
		}).complete(function() {


		});



        }

     })