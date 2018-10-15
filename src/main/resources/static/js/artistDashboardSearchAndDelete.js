function deleteFunction(name, id) {
	if (confirm("Are you sure you want do delete " + name)) {

		$.ajax({
			url: '/artist/song/' + id,
			headers: {
				"X-CSRF-TOKEN": $("input[name='_csrf']").val()
			},
			type: 'DELETE',
			success: function (result) {
				redirectToMySongs();
			}
		});

	}
}

function mySearch() {
	var searchParam = document.getElementById("search").value
	$.get("/artist/search", {
		searchParam: searchParam
	}, function (data) {
		if (data.length > 0) {
			var html = "";
			for (i = 0; i < data.length; i++) {
				html = html + '<tr><form role="form" action="delete" method="post" id="form">';
				html = html + '<td style="width: 30%;"><a href="/home/song?id=' + data[i].id + '">' + data[i].songName + '</a></td>';
				html = html + '<td style="width: 27%;">' + data[i].albumName + '</td>';
				html = html + '<td style="width: 36%;">' + data[i].genre + '</td>';
				html = html + '<input type="hidden" name="email" value=' + data[i].id + '>';
				html = html + '<input type="hidden" name="name" value=' + data[i].songName + '>';
				html = html + '<td style="width: 15%;"><button type="submit" class="btn btn-danger" id="' + data[i].songName + '" name="' + data[i].id + '" onclick="deleteFunction(this.id,this.name)">DELETE<i class="fa fa-trash"></i></button></td>';
				html = html + '</form></tr>';
			}
			$("#bodyScroll").empty().append(html);
		} else {
			redirectToMySongs();
		}
	}).done(function () {}).fail(function (xhr, textStatus, errorThrown) {
		redirectToMySongs();
	}).complete(function () {


	});


}