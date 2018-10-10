

 $(document).ready(function () {

        $('#searchForm').submit(function(event) {
           var searchParam=$('#searchParam').val();
         if (!searchParam.replace(/\s/g, '').length) {
              event.preventDefault();
        }

        if (searchParam.length>50 || searchParam.length<1 ) {
              event.preventDefault();
        }
        });


         $('input[type=radio]').on('change', function() {
                var counter=0;
                var what=this.value;
                $("#resultsList li").filter(function() {
                if(!$(this).hasClass(what)) {
                    $(this).hide();
                }   else {
                $(this).show();
                counter++;
                }

                if(what=="mostRelevant") {
                counter++;
                        $(this).show();
                }
                console.log(counter);
            });

            if(counter==0 && !$("#resultsH3").is(":visible")) {
                  $("#noResults").show();
                }   else {
                  $("#noResults").hide();
                }

        });

});