 $(document).ready(function () {

                  $('form').submit(function(event) {
	         var values = $(this).serialize();
	         rez=values.split("=")
                    if(!confirm("Are you sure you want to delete " + rez[rez.length-1] )) {
                            event.preventDefault();
                             return;
                    }

	        });

	            $('form').bind("keypress", function (e) {
            if (e.keyCode == 13) {
                e.preventDefault();
                document.getElementById("buttonSearch").click();
                return;
            }
                });

                })