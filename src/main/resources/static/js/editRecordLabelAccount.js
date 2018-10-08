    window.onload = function () {
    var previosValue = "";
    var savedValue="";
}

function editDetails(id) {
    result=id.split(",");
    document.getElementById(result[0]).disabled = false;
     document.getElementById(result[1]).style.display = 'none';
    document.getElementById(result[2]).style.display = 'block';
    document.getElementById(result[3]).style.display = 'block';
    previosValue=document.getElementById(result[0]).value;

}

function saveDetails(id) {
    document.getElementById("nameError").style.display='none';
    result=id.split(",");
    if(document.getElementById("recordLabelName").value.length>50 || document.getElementById("recordLabelName").value.length<2) {
            document.getElementById("nameError").style.display='block';
            return;
        }
    document.getElementById(result[0]).disabled = true;
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
    document.getElementById(result[1]).style.display = 'block';
    document.getElementById(result[2]).style.display = 'none';
    document.getElementById(result[3]).style.display = 'none';
}




function formSubmit() {

        var recordLabelName=document.getElementById("recordLabelName").value;
        var aboutInfo=document.getElementById("recordLabelAboutInfo").value;
        var id=document.getElementById("id").value;
        var recordLabelDto={name:recordLabelName,aboutInfo:aboutInfo,id:id};
        $.ajax({
        url: "/record-label/account",
        headers: {"X-CSRF-TOKEN": $("input[name='_csrf']").val()},
        type: "post",
        data:recordLabelDto,
        success: function (data) {
        },
        });


}