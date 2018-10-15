$(document).ready(function () {
	$('form').submit(function (event) {
		event.preventDefault();
		var values = $(this).serialize();
		rez = values.split("=");
		emailRez = rez[rez.length - 2].split("&");
		if (!confirm("Are you sure you want to delete " + rez[rez.length - 1])) {
			return;
		}
		var email = emailRez[0];
		$.ajax({
			url: '/admin/record-label/' + email,
			headers: {
				"X-CSRF-TOKEN": $("input[name='_csrf']").val()
			},
			type: 'DELETE',
			success: function (result) {
				redirectToDashboard();
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