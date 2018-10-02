 $(document).ready(function () {
                  $('form').submit(function(event) {
                  event.preventDefault();
	         var values = $(this).serialize();
	         console.log(values);
	         rez=values.split("=");
	         idRez=rez[rez.length-2].split("&");
                    if(!confirm("Are you sure you want to delete " + rez[rez.length-1] )) {
                     return;
                    }
                    var id=idRez[0];
                     $.ajax({
                                url: '/artist/song/'+id,
                                headers: {"X-CSRF-TOKEN": $("input[name='_csrf']").val()},
                                type: 'DELETE',
                                success: function(result) {
                                    redirectToMySongs();
                                }
                            });

	        });

	            $('form').bind("keypress", function (e) {
            if (e.keyCode == 13) {
                e.preventDefault();
                document.getElementById("buttonSearch").click();
                return;
            }
                });

                })