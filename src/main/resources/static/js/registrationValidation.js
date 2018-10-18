$(document).ready(function () {
	$("#registrationForm").submit(function(event) {
	$("#firstNameError").show().html("");
	 $("#emailError").show().html("");
	 $("#globalError").show().html("");
	 $("#passwordError").show().html("");
		register(event);
	});

});
function register(event){
    counter=0;
    if ($("#name").val().length<2) {
        $("#firstNameError").show().html("Name must contain more than 1 alphanumerical characters!");
    	counter++;
    }

    if ($("#name").val().length>50) {
            $("#firstNameError").show().html("Name must contain less than 50 alphanumerical characters!");
        	counter++;
        }

      var trimName=$("#name").val()
         if (!trimName.replace(/\s/g, '').length) {
            $("#firstNameError").show().html("Name must contain more than 1 alphanumerical characters!");
                     counter++;
                 }


     if (checkEmail($("#email").val())==false) {
        $("#emailError").show().html("Email address should be in format example@exp.exp!");
    	counter++;
    }

    if (checkPwd($("#password").val())==0) {
        $("#passwordError").show().html("Password must be at least 8 characters long with one number or symbol!");
    	counter++;
    }

    if(checkPwd($("#password").val())==2) {
        $("#passwordError").show().html("Password cannot contain more than 64 characters!");
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
    } else if (str.length > 64) {
        return 2;
    } else if (str.search(/[a-zA-Z]/) == -1) {
       return 0;
    } else if (str.search(/\d/) == -1 && str.search(/[^\w\s]/) == -1) {
        return 0;
    }
     return 1;
}

function checkEmail(email)
{
    //regular expression for email

    var pattern = new RegExp(/^(("[\w-\s]+")|([\w-]+(?:\.[\w-]+)*)|("[\w-\s]+")([\w-]+(?:\.[\w-]+)*))(@((?:[\w-]+\.)*\w[\w-]{0,64})\.([a-z]{2,6}(?:\.[a-z]{2})?)$)|(@\[?((25[0-5]\.|2[0-4][0-9]\.|1[0-9]{2}\.|[0-9]{1,2}\.))((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\.){2}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\]?$)/i);
    if(pattern.test(email)){
        var parts=email.split("@");
            console.log(email + " ----- " + parts)
            if(parts[0].length>64) {
                return false;
            }
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