$(document).ready(function () {
	$('form').submit(function(event) {
	$("#nameError").show().html("");
	 $("#emailError").show().html("");
	 counter=0;
		       if ($("#name").val().length<2 || $("#name").val().length>50) {
        $("#nameError").show().html("Name must be between 2 and 50 characters");
    	counter++;
    }

    if(checkEmail($("#email").val())==false) {
        $("#emailError").show().html("Your email is invalid");
        counter++;
    }

    if (counter>0) {
    event.preventDefault();
    return;
    }

	})
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
	})
