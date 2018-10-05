   window.onload = function () {
    var previosValue = "";
    var savedValue="";
}

function editDetails(id) {
    result=id.split(",");
    document.getElementById(result[0]).disabled = false;
        if(result[0]=="date") {
           document.getElementById(result[0]).style.display = 'block';
        }
     document.getElementById(result[1]).style.display = 'none';
    document.getElementById(result[2]).style.display = 'block';
    document.getElementById(result[3]).style.display = 'block';
    previosValue=document.getElementById(result[0]).value;

}

function saveDetails(id) {
document.getElementById("nameError").style.display='none';
result=id.split(",");
    if(document.getElementById("artistName").value.length>50 || document.getElementById("artistName").value.length<2) {
        document.getElementById("nameError").style.display='block';
        return;
    }
    document.getElementById(result[0]).disabled = true;
    if(result[0]=="date") {
           document.getElementById(result[0]).style.display = 'none';
           var parts=document.getElementById(result[0]).value.split("-");
           var date=parts[2]+"-"+parts[1]+"-" + parts[0];
           document.getElementById("dateLabel").innerText=date;
        }
    document.getElementById(result[1]).style.display = 'block';
    document.getElementById(result[2]).style.display = 'none';
    document.getElementById(result[3]).style.display = 'none';
    formSubmit();
}

function cancelEditing(id) {
document.getElementById("nameError").style.display='none';
    result=id.split(",");
    document.getElementById(result[0]).value = previosValue;
    document.getElementById(result[0]).disabled = true;
    if(result[0]=="date") {
           document.getElementById(result[0]).style.display = 'none';
           var parts=previosValue.split("-");
           var date=parts[2]+"-"+parts[1]+"-" + parts[0];
           document.getElementById("dateLabel").innerText=date;
        }
    document.getElementById(result[1]).style.display = 'block';
    document.getElementById(result[2]).style.display = 'none';
    document.getElementById(result[3]).style.display = 'none';
}




function formSubmit() {

        var artistName=document.getElementById("artistName").value;
        var date=document.getElementById("date").value;
        var bio=document.getElementById("bio").value;
        var id=document.getElementById("id").value;
        var artistDto={name:artistName,bio:bio,id:id,dateOfBirth:date};
        $.ajax({
        url: "/artist/account",
        headers: {"X-CSRF-TOKEN": $("input[name='_csrf']").val()},
        type: "post",
        data:artistDto,
        success: function (data) {
        },
        });


}