function deleteFunction(name,id) {
    if(confirm("Are you sure you want do delete " + name)) {

          $.get("/artist/delete-song", {
			id:id
		}, function(data) {
            redirectToMySongs();
		}).done(function() {
		}).fail(function(xhr, textStatus, errorThrown) {
		}).complete(function() {
            redirectToMySongs();
		});

    }
            }