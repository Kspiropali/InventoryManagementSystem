//let csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1').split(",")[0];

function login() {
    let username = document.getElementById("username_login").value;
    let password = document.getElementById("password_login").value;

    if (username === "" || password === "") {
        return;
    }

    let user = username + ":" + password;
    let encodedUser = btoa(user);

    let settings = {
        "url": base_url+"user/login", "method": "POST", "headers": {
            "Authorization": "Basic " + encodedUser,
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": getCookie("XSRF-TOKEN"),
            "Access-Control-Allow-Origin": base_url,
            //"Csrf-Token": getCookie("XSRF-TOKEN"),
            "Access-Control-Allow-Credentials": "true",
            "Access-Control-Allow-Methods": "GET, POST",
            "Access-Control-Allow-Headers": "Content-Type, Authorization, X-Requested-With, X-XSRF-TOKEN",

        },
    };

    $.ajax(settings).always(function (response) {
        // console.log(response);
        // console.log(response.status);
        if (response === "Login successful") {
            document.cookie = "username="+username;
            window.location.href = base_url+"user/products";
        } else if (response.status === 401 && response.responseText === "HTTP Status 401 - User is disabled\n") {
            sendNotification(401, "You need to activate your account first!");
        } else if (response.status === 401 && response.responseText === "HTTP Status 401 - Bad credentials\n") {
            sendNotification(401, "Wrong username or password!");
        } else if (response.status === 403) {
            sendNotification(403, "Cors Error! Aborting request");
        } else if (response.status === 500) {
            sendNotification(500, "Internal 500 error!")
        } else if (response.status === 404) {
            sendNotification(404, "Server is offline/dead!");
        }
    });

    event.preventDefault();
}

function register() {
    event.preventDefault();
    let username = document.getElementById("username_register").value;
    let email = document.getElementById("email_register").value;
    let password = document.getElementById("password_register").value;
    let passwordConfirm = document.getElementById("confirm-password").value;

    let usernameRegex = /^[a-zA-Z]+$/;
    if (username === "") {
        sendNotification(403, "Username can't be empty!");
        return;
    }
    if (email === "") {
        sendNotification(403, "Email can't be empty!");
        return;
    }
    if (password === "") {
        sendNotification(403, "Password can't be empty!");
        return;
    }
    // email regex
    if (!email.match(/^([\w-.]+@([\w-]+\.)+[\w-]{2,4})?$/)) {
        sendNotification(403, "Email is not valid!");
        return;
    }

    if (!username.match(usernameRegex) && username.length >= 3 && username.length <= 20) {
        sendNotification(401, "Username can only contain letters, minimum 3 characters and maximum 20 characters!");
        event.preventDefault();
        return;
    }

    if (password !== passwordConfirm) {
        sendNotification(401, "Passwords do not match!");
        return;
    }

    let user = {
        "username": username, "password": password, "email": email
    }

    let settings = {
        "url": base_url+"user/register", "method": "POST", "timeout": 0, "headers": {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": getCookie("XSRF-TOKEN"),
            //"Csrf-Token": getCookie("XSRF-TOKEN"),
            "Access-Control-Allow-Origin": base_url,
            "Access-Control-Allow-Credentials": "true",
            "Access-Control-Allow-Methods": "GET, POST",
            "Access-Control-Allow-Headers": "Content-Type, Authorization, X-Requested-With, X-XSRF-TOKEN",

        }, "data": JSON.stringify(user),
    };

    $.ajax(settings).always(function (response) {
        // console.log(response);
        if (response === "Success") {
            sendNotification(200, "Successfully registered! Please check your inbox/spam")
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
        } else {
            sendNotification(404, "Server is dead!");
        }

    });

    event.preventDefault();
}



$(document).ready(function () {

    let settings = {
        "url": base_url+"user/login", "method": "POST",
        "timeout": 0, "headers": {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": getCookie("XSRF-TOKEN"),
            //"Csrf-Token": getCookie("XSRF-TOKEN"),
            "Access-Control-Allow-Origin": base_url,
            "Access-Control-Allow-Credentials": "true",
            "Access-Control-Allow-Methods": "GET, POST",
            "Access-Control-Allow-Headers": "Content-Type, Authorization, X-Requested-With, X-XSRF-TOKEN",

        }
    };

    $.ajax(settings).always(function (response) {
        //console.log(response);
        //console.log(response.status);
        if (response === "Logged in!") {
            window.location.href = base_url+"chat";
        }
    });

});

function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}