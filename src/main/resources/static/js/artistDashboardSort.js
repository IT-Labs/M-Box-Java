$(document).ready(function () {
            var counterName=0;
            var counterAlbumName=0;
            var counterGenre=0;
            $("#songName").click(function() {
                handle(1);
            });

            $("#albumName").click(function() {
                handle(2);
            });

            $("#genre").click(function() {
                handle(3);
            });


        function handle(param) {
        $("#downEmail").hide();
        $("#upEmail").hide();
        $("#downName").hide();
        $("#upName").hide();
        $("#downNumber").hide();
        $("#upNumber").hide();
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
                var albumName="album_name";
                if(counterAlbumName%2==0) {
                $("#downEmail").show();
                $("#upEmail").hide();
                sort(albumName,counterAlbumName);
                 counterAlbumName++;
              } else {
                $("#downEmail").hide();
                $("#upEmail").show();
                sort(albumName,counterAlbumName);
                 counterAlbumName--;
              }

            }   else {
                var genre="genre";
            if(counterGenre%2==0) {
                $("#downNumber").show();
                $("#upNumber").hide();
                sort(genre,counterGenre);
                counterGenre++;
              } else {
                $("#downNumber").hide();
                $("#upNumber").show();
                sort(genre,counterGenre);
                counterGenre--;
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
                $.get("/artist/sort", {
        			page:0,
        			size:calculatedSize,
        			sort:sortParam
        		}, function(data) {
        			console.log(data);
        			var html="";
                    for(i=0 ; i<data.length ; i++) {
                        html=html+'<tr><form role="form" action="delete" method="post" id="form">';
                        html=html+'<td style="width: 30%;">' + data[i].songName + '</td>';
                        html=html+'<td style="width: 27%;">' + data[i].albumName + '</td>';
                        html=html+'<td style="width: 36%;">' + data[i].genre + '</td>';
                        html=html+'<input type="hidden" name="email" value=' + data[i].id + '>';
                        html=html+'<input type="hidden" name="name" value=' + data[i].songName +'>';
                        html=html+'<td style="width: 15%;"><button type="submit" class="btn btn-danger" id="' + data[i].songName + '" name="' + data[i].id + '" onclick="deleteFunction(this.id,this.name)">DELETE<i class="fa fa-trash"></i></button></td>';
                        html=html+'</form></tr>';
                        }
                        $("#bodyScroll").empty().append(html);
        		}).done(function() {
        		}).fail(function(xhr, textStatus, errorThrown) {
        		}).complete(function() {


        		});

        }

     })