$(document).ready(function () {
	$('form').submit(function(event) {
	$("#firstNameError").show().html("");
	 $("#emailError").show().html("");
	 $("#globalError").show().html("");
	 $("#passwordError").show().html("");
		register(event);
	});

});
function register(event){
    counter=0;
    if ($("#name").val().length<2 || $("#name").val().length>50) {
        $("#firstNameError").show().html("Name must be between 1 and 50 characters");
    	counter++;
    }


     if (checkEmail($("#email").val())==false) {
        $("#emailError").show().html("Email address not in valid format");
    	counter++;
    }

    if (checkPwd($("#password").val())==0) {
        $("#passwordError").show().html("Your password must contain least 8 characters and be alphanumeric");
    	counter++;
    }

    if(counter>0) {
        event.preventDefault();
        return;
    }

    $(".alert").html("").hide();
    $(".error-list").html("");
    if($("#password").val() != $("#matchPassword").val()){
    	$("#globalError").show().html("password does not match");
    	event.preventDefault();
    	return;
    }

    function checkPwd(str) {
    if (str.length < 8) {
        return 0;
    } else if (str.length > 50) {
        return 0;
    } else if (str.search(/[a-zA-Z]/) == -1) {
       return 0;
    } else if (str.search(/\d/) == -1 && str.search(/[^a-zA-Z0-9\!\@\#\$\%\^\&\*\(\)\_\+]/) == -1) {
        return 0;
    }
     return 1;
}

function checkEmail(email)
{
    //regular expression for email
    var pattern = new RegExp(/^(("[\w-\s]+")|([\w-]+(?:\.[\w-]+)*)|("[\w-\s]+")([\w-]+(?:\.[\w-]+)*))(@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$)|(@\[?((25[0-5]\.|2[0-4][0-9]\.|1[0-9]{2}\.|[0-9]{1,2}\.))((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\.){2}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\]?$)/i);
    if(pattern.test(email)){
        if(email.length<321) {
        return true;
        }   else {
            return false;
        }
    } else {
        return false;
    }
}


}