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
    if(document.getElementById("listenerName").value.length>50 || document.getElementById("listenerName").value.length<2) {
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

        var listenerName=document.getElementById("listenerName").value;
        var id=document.getElementById("id").value;
        var listenerDto={listenerName:listenerName,id:id};
        $.ajax({
        url: "/listener/account",
        headers: {"X-CSRF-TOKEN": $("input[name='_csrf']").val()},
        type: "post",
        data:listenerDto,
        success: function (data) {
        console.log("success");
        },
        });


}