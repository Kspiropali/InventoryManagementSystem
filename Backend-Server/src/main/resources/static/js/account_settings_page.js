//for registration notification
let registerSocket_1 = new SockJS('/ws');
let registerStompClient_1 = null;
let currentRegistrationSubscription_1;


function changeDetails() {
    let username = document.getElementById("name").value;
    let lastname = document.getElementById("lastname").value;
    let password = document.getElementById("new_password").value;
    let passwordConfirm = document.getElementById("new_password_confirm").value;
    let language = document.getElementById("language_picker").value;
    let telephone = document.getElementById("telephone_number").value;
    let optIn = document.getElementById("opt_to_emails_checkbox").checked;

    if (!(document.getElementById("name").value.match(/^[a-zA-Z]+$/) && document.getElementById("name").value.length >= 3 && document.getElementById("name").value.length <= 20)) {
        sendStatus("red", "Name can only contain letters and must be between 3 and 20 characters long");
        return;
    }

    if (!lastname.match(/^[a-zA-Z]+$/) && lastname.length >= 3 && lastname.length <= 20) {
        sendStatus("red", "Lastname can only contain letters and must be between 3 and 20 characters long");
        return;
    }

    if (password.length < 8 || password.includes("\"")) {
        sendStatus("red", "Password must be at least 8 characters long and cannot contain \"");
        return;
    }

    if (password !== passwordConfirm) {
        sendStatus("red", "Passwords do not match");
        return;
    }

    if (!telephone.match(/^[\d\-\s]+$/)) {
        sendStatus("red", "Telephone number can only contain numbers and dashes");
        return;
    }


    let settings = {
        "url": "http://79.67.179.40:8080/user/update",
        "method": "POST",
        "timeout": 0,
        "headers": {
            "Content-Type": "application/json",
        },
        "data": JSON.stringify({
            "username": document.cookie.split(",")[0],
            "name": username,
            "surname": lastname,
            "password": password,
            "language": language,
            "phoneNumber": telephone,
            "optIn": optIn
        }),
    };

    $.ajax(settings).done(function (response) {
        console.log(response);
        if (response.status === 401) {
            document.cookie = "";
        } else if (response === "User updated") {

            sendStatus("green", "User Details updated successfully");
        }
    });


    event.preventDefault();
}

function cancelled() {
    document.getElementById("name").value = "";
    document.getElementById("lastname").value = "";
    document.getElementById("new_password").value = "";
    document.getElementById("new_password_confirm").value = "";
    document.getElementById("language_picker").value = "";
    document.getElementById("telephone_number").value = "";
    document.getElementById("opt_to_emails_checkbox").checked = false;
    sendStatus("yellow", "Details discarded!");
}


async function sendStatus(status, message) {
    let notification = document.getElementById("notification_box");

    if (status === "green") {
        //Green notification
        notification.innerHTML = ` <div style="color: darkgreen;background-color: #130e0e;border-color: #f5c6cb;" class="alert fade alert-simple alert-success alert-dismissible text-left font__family-montserrat font__size-16 font__weight-light brk-library-rendered rendered show">
                                            <button type="button" class="close font__size-18" data-dismiss="alert">
                                               <span aria-hidden="true"><a> <i class="fa fa-times greencross"></i></a></span>
                                                <span class="sr-only">Close</span>
                                            </button>
                                            <i class="start-icon far fa-check-circle faa-tada animated"></i>
                                            <strong class="font__weight-semibold">` + message + `</strong>
                                        </div>`
    } else if (status === "yellow") {
        //Yellow notification
        notification.innerHTML = `<div  style="color: yellow;background-color: #130e0e;border-color: #f5c6cb;"  class="alert fade alert-simple alert-warning alert-dismissible text-left font__family-montserrat font__size-16 font__weight-light brk-library-rendered rendered show" role="alert" data-brk-library="component__alert">
          <button type="button" class="close font__size-18" data-dismiss="alert">
                <span aria-hidden="true">
                <i class="fa fa-times warning"></i>
                </span>
                <span class="sr-only">Close</span>
                </button>
          <i class="start-icon fa fa-exclamation-triangle faa-flash animated"></i>
          <strong class="font__weight-semibold">` + message + `</strong> 
        </div>`
    } else {
        //Red notification
        notification.innerHTML = `<div style="color: #ff0018;background-color: #130e0e;border-color: #f5c6cb;" class="alert fade alert-simple alert-danger alert-dismissible text-left font__family-montserrat font__size-16 font__weight-light brk-library-rendered rendered show" role="alert" data-brk-library="component__alert">
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

function deleteAccount() {
    let settings = {
        "url": "http://79.67.179.40:8080/user/delete",
        "method": "POST",
        "timeout": 0,
        "headers": {
            "Content-Type": "application/json"
        },
        "data": JSON.stringify({
            "username": document.cookie.split(",")[0]
        }),
    };

    $.ajax(settings).done(function (response) {
        console.log(response);
        //session expired
        if (response.status === 401) {
            document.cookie = "";
            window.location.href = "http://79.67.179.40:8080/";
            //user deleted
        } else if (response === "User deleted") {
            let settings = {
                "url": "http://79.67.179.40:8080/logout",
                "method": "GET",
                "timeout": 0,
            };

            $.ajax(settings).done(function (response) {
                console.log(response);
                window.location.href = "http://79.67.179.40:8080/";
            });

        }
    });
}

async function uploadAvatarImage() {
    //getting the avatar image file
    let file = document.querySelector('input[type=file]')['files'][0];

    let reader = new FileReader();

    reader.onload = function () {
        if (reader.result.length > 199999) {
            sendStatus("red", "Image size too large");
            return;
        }

        if (reader.result.search()) {
            sendStatus("red", "Image format not supported");
            return;
        }

        let base64String = reader.result.replace("data:", "").replace(/^.+,/, "");
        //console.log(base64String);


        let settings = {
            "url": "http://79.67.179.40:8080/user/upload/avatar",
            "method": "POST",
            "timeout": 0,
            "headers": {
                "Content-Type": "application/json"
            },
            "data": JSON.stringify({
                "username": document.cookie.split(",")[0],
                "avatar": base64String
            }),
        };

        $.ajax(settings).done(function (response) {
            console.log(response);

            if (response.status === 401) {
                //logout since session is expired
                let settings = {
                    "url": "http://79.67.179.40:8080/logout",
                    "method": "GET",
                    "timeout": 0,
                };

                $.ajax(settings).done(function (response) {
                    console.log(response);
                    window.location.href = "http://79.67.179.40:8080/";
                });


            } else if (response === "Avatar updated") {
                let chatMessage = {
                    sender: null,
                    content: null,
                    type: 'SPECIAL'
                };
                registerStompClient_1.send(`/channel/registerCallbackSocket`, {}, JSON.stringify(chatMessage));
                document.getElementById("avatar_image").src = "data:image/jpeg;base64, " + base64String;
                sendStatus("green", "Avatar uploaded");
            }
        });
    }

    try {
        reader.readAsDataURL(file);
    } catch (e) {
        console.log(e);
    }


}


$(document).ready(function () {
    registerStompClient_1 = Stomp.over(registerSocket_1)
    registerStompClient_1.connect({}, onRegisterSocketConnected);

});

function onRegisterSocketConnected() {
    try {
        currentRegistrationSubscription_1 = registerStompClient_1.subscribe(`/channel/registerCallbackSocket/sendMessage`);
    }catch (e){
        registerStompClient_1.connect({}, onRegisterSocketConnected);
    }

}