$(document).ready(function () {
	$('passwordForm').submit(function(event) {
	$("#errorPassword").show().html("");
	 $("#errorConfirmPassword").show().html("");
		       if (checkPwd($("#password").val())==0) {
        $("#errorPassword").show().html("Your password must contain least 8 characters and be alphanumeric");
    	event.preventDefault();
    	return;
    }

    if($("#password").val() != $("#signup-password-confirm").val()){
    	$("#errorConfirmPassword").show().html("passwords does not match");
    	event.preventDefault();
    	return;
    }

	})
    function checkPwd(str) {
    if (str.length < 8) {
        return 0;
    } else if (str.length > 64) {
        return 0;
    } else if (str.search(/[a-zA-Z]/) == -1) {
       return 0;
    } else if (str.search(/\d/) == -1 && str.search(/[^a-zA-Z0-9\!\@\#\$\%\^\&\*\(\)\_\+]/) == -1) {
        return 0;
    }
     return 1;
}
	})