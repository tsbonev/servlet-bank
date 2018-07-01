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

function checkNumber() {

    var value = document.getElementById("action-amount").value;

    var signedFraction = /^-?[1-9]{1}[0-9]{0,9}[.,]{1}[0-9]{1,5}$/,
        wholeNumber = /^-?[1-9]{1}[0-9]{0,9}$/,
        leadingZeroFraction = /^-?[0]{1}[.,]{1}[0-9]{0,4}[1-9]{1}$/,
        trailingZeroFraction = /^-?[0]{1}[.,]{1}[1-9]{0,4}[1-9]{1}$/,
        unsignedFraction = /^[0]{1}[.,]{1}[1-9]{1}[0-9]{1,4}$/;


    var result = signedFraction.test(value)
        || wholeNumber.test(value)
        || leadingZeroFraction.test(value)
        || trailingZeroFraction.test(value)
        || unsignedFraction.test(value);


    if(!result){
        document.getElementById("validation").innerHTML = "Invalid number!";
        return false;
    }
    else {
        document.getElementById("validation").innerHTML = "";
        return true;
    }
}