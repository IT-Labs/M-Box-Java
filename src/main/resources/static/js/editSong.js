window.onload = function () {
    var previosValue = "";
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
result=id.split(",");
    document.getElementById(result[0]).disabled = true;
    document.getElementById(result[1]).style.display = 'block';
    document.getElementById(result[2]).style.display = 'none';
    document.getElementById(result[3]).style.display = 'none';
}

function cancelEditing(id) {
    result=id.split(",");
    document.getElementById(result[0]).value = previosValue;
    document.getElementById(result[0]).disabled = true;
    document.getElementById(result[1]).style.display = 'block';
    document.getElementById(result[2]).style.display = 'none';
    document.getElementById(result[3]).style.display = 'none';
}