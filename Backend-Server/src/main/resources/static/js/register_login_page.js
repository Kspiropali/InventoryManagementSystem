function login() {
    let username = document.getElementById("username_login").value;
    let password = document.getElementById("password_login").value;

    if (username === "" || password === "") {
        return;
    }

    let user = username + ":" + password;
    //console.log(user);
    let encodedUser = btoa(user);
    //console.log(encodedUser);

    let settings = {
        "url": "https://localhost:8080/user/login",
        "method": "POST",
        "timeout": 0,
        "headers": {
            "Authorization": "Basic Ym9iQGJvYi5jb206dGVzdGluZ2JvYjEyMyE="
        },
    };

    $.ajax(settings).always(function (response) {
        console.log(response);
        console.log(response.status);
        if (response === "Logged in!") {
            console.log("Logged in!");
            //document.cookie = username + ",public";
            //window.location.href = "http://79.67.179.40:8080/chat";
        } else if (response.status === 401 && response.responseText === "HTTP Status 401 - User is disabled\n") {
            sendNotification(401, "You need to activate your account first!").then(r => {});
        } else if (response.status === 401 && response.responseText === "HTTP Status 401 - Bad credentials\n") {
            sendNotification(401, "Wrong username or password!").then(r => {});
        } else if (response.status === 403) {
            sendNotification(403, "Cors Error! Aborting request").then(r => {});
        } else if (response.status === 500) {
            sendNotification(500, "Internal 500 error!").then(r => {})
        } else if (response.status === 404) {
            sendNotification(404, "Server is offline/dead!").then(r => {});
        }
    });
    //document.getElementById("login_button").event.preventDefault();
    event.preventDefault();
}

function register() {
    event.preventDefault();
    let username = document.getElementById("username_register").value;
    let email = document.getElementById("email_register").value;
    let password = document.getElementById("password_register").value;
    let passwordConfirm = document.getElementById("confirm-password").value;

    let usernameRegex = /^[a-zA-Z]+$/;
    if (username === ""){
        sendNotification(403, "Username can't be empty!");
        return;
    }
    if(email === ""){
        sendNotification(403, "Email can't be empty!");
        return;
    }
    if(password === ""){
        sendNotification(403, "Password can't be empty!");
        return;
    }
    // email regex
    if (!email.match(/^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/)) {
        sendNotification(403, "Email is not valid!");
        return;
    }

    if(! username.match(usernameRegex) && username.length >= 3 && username.length <= 20) {
        sendNotification(401, "Username can only contain letters, minimum 3 characters and maximum 20 characters!");
        event.preventDefault();
        return;
    }

    if (password !== passwordConfirm) {
        sendNotification(401, "Passwords do not match!");
        return;
    }

    let user = {
        "username": username, "password": password, "email": email, "passwordConfirm": passwordConfirm
    }

    let settings = {
        "url": "http://79.67.179.40:8080/user/register", "method": "POST", "timeout": 0, "headers": {
            "Content-Type": "application/json"
        }, "data": JSON.stringify(user),
    };

    $.ajax(settings).always(function (response) {
        console.log(response);
        if (response === "Success") {
            sendNotification(200, "Successfully registered! Please check your inbox");
            document.getElementById("confirm-password").value = "";
            document.getElementById("password_register").value = "";
            document.getElementById("username_register").value = "";
            document.getElementById("email_register").value = "";
        } else if (response.status === 401) {
            sendNotification(401, "You somehow need to be logged in to register?");
        } else if (response.status === 403) {
            sendNotification(403, "Cors Error! Aborting request");
        } else if (response.status === 500) {
            sendNotification(500, "Your username or email is already taken!");
        } else if (response.status === 404) {
            sendNotification(404, "Server could not be resolved!");
        } else{
            sendNotification(404, "Server is dead!");
        }

    });

    //event.preventDefault();
}


async function sendNotification(errorCode, message) {
    let notification = document.getElementById("notification_box");

    if (errorCode === 200) {
        //Green notification
        notification.innerHTML = ` <div class="alert fade alert-simple alert-success alert-dismissible text-left font__family-montserrat font__size-16 font__weight-light brk-library-rendered rendered show">
                                            <button type="button" class="close font__size-18" data-dismiss="alert">
                                               <span aria-hidden="true"><a> <i class="fa fa-times greencross"></i></a></span>
                                                <span class="sr-only">Close</span>
                                            </button>
                                            <i class="start-icon far fa-check-circle faa-tada animated"></i>
                                            <strong class="font__weight-semibold">` + message + `</strong>
                                        </div>`
    } else if (errorCode === 401 || errorCode === 403) {
        //Yellow notification
        notification.innerHTML = `<div class="alert fade alert-simple alert-warning alert-dismissible text-left font__family-montserrat font__size-16 font__weight-light brk-library-rendered rendered show" role="alert" data-brk-library="component__alert">
          <button type="button" class="close font__size-18" data-dismiss="alert">
                <span aria-hidden="true">
                <i class="fa fa-times warning"></i>
                </span>
                <span class="sr-only">Close</span>
                </button>
          <i class="start-icon fa fa-exclamation-triangle faa-flash animated"></i>
          <strong class="font__weight-semibold">` + message + `</strong> 
        </div>`
    } else if (errorCode === 500 || errorCode === 404) {
        //Red notification
        notification.innerHTML = `<div class="alert fade alert-simple alert-danger alert-dismissible text-left font__family-montserrat font__size-16 font__weight-light brk-library-rendered rendered show" role="alert" data-brk-library="component__alert">
          <button type="button" class="close font__size-18" data-dismiss="alert">
                <span aria-hidden="true">
                <i class="fa fa-times danger "></i>
                </span>
                <span class="sr-only">Close</span>
                </button>
          <i class="start-icon far fa-times-circle faa-pulse animated"></i>
          <strong class="font__weight-semibold">` + message + `</strong>
        </div>`
    }
}

/*
$(document).ready(function () {

    let settings = {
        "url": "http://79.67.179.40:8080/user/login", "method": "POST",
    };

    $.ajax(settings).always(function (response) {
        console.log(response);
        console.log(response.status);
        if (response === "Logged in!") {
            window.location.href = "http://79.67.179.40:8080/chat";
        }
    });

});
*/