function checkPasswordRepeat() {

    var password = document.getElementById("password").value;
    var repeat = document.getElementById("password-repeat").value;
    var errorLabel = document.getElementById("pass-error");
    var submit = document.getElementById("submit");

    if(password != repeat){
        errorLabel.innerHTML = "Passwords must match!";
        submit.disabled = true;
    }
    else {
        errorLabel.innerHTML = "";
        submit.disabled = false;
    }
    
}