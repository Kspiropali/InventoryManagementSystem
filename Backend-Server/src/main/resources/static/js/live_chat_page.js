'use strict';

let stompClient;
let currentSubscription;
let userToAvatarMap = new Map();
let username = document.cookie.split(",")[0];
let topic = document.cookie.split(",")[1];
let socket = new SockJS('/ws');

//for registration notification
let registerSocket = new SockJS('/ws');
let registerStompClient;
let currentRegistrationSubscription;


function uploadImage() {

    let file = document.querySelector('input[type=file]')['files'][0];

    let reader = new FileReader();
    // console.log("next");

    reader.onload = function () {
        if (reader.result.length > 199999) {
            alert("File is too big!");
            return;
        }

        if (reader.result.search()) {

        }

        let base64String = reader.result.replace("data:", "")
            .replace(/^.+,/, "");

        let chatMessage = {
            sender: username,
            content: base64String,
            type: 'PICTURE'
        };
        stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(chatMessage));
    }

    try {
        reader.readAsDataURL(file);
    } catch (e) {
        console.log(e);
    }


}


// Enter new room and leave the other one
function enterRoom(newRoomId) {
    let toUser;
    let roomId = newRoomId;
    //setting roomId cookie
    document.cookie = username + "," + roomId;
    // console.log(roomId)
    //if roomId is not public, it will be in form of user1user2
    //with user1 or user2 being our username, so we remove the username
    if (roomId !== "public") {
        toUser = roomId.replace(username, "");
    } else {
        toUser = roomId;
    }

    document.getElementById('selected_user_name').innerHTML = toUser;
    topic = `/app/chat/${newRoomId}`;

    if (currentSubscription) {
        currentSubscription.unsubscribe();
    }
    currentSubscription = stompClient.subscribe(`/channel/${roomId}`, onMessageReceived);


    stompClient.send(`${topic}/addUser`,
        {},
        JSON.stringify({sender: username, type: 'JOIN', destination: roomId})
    );
}

// Connect to WebSocket Server on first time initialising
function onConnected() {
    enterRoom(topic);
}

// Send message to the server, chatrooms
function sendMessage(event) {
    // check if event.key is literal string
    if (event.key === '"') {
        event.preventDefault();
        return;
    }
    let messageInput = document.getElementById("chat_box").value;

    // check if enter key was pressed
    if (event.keyCode !== 13) {
        return;
    }


    if (messageInput === "") {
        event.preventDefault();
        return;
    }

    //let messageContent = messageInput.value.trim();
    let chatMessage = {
        sender: username,
        content: messageInput,
        type: 'CHAT'
    };
    stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(chatMessage));
    document.getElementById("chat_box").value = "";

    event.preventDefault();
}

////array of array of username,avatar -> [ ["username","avatar" ], ["username","avatar"], ... ]
//console.log(response);
////array of username,avatar -> ["username", "avatar"]
//console.log(response[0]);
////string of username -> username
//console.log(response[0][0]);
// Receive message from the server either JOIN(when a user enters the room) or CHAT(when a user sends message) or LEAVE(when user leaves the room)
function onMessageReceived(payload) {
    let message = JSON.parse(payload.body);

    if (message.type === 'CHAT') {
        if (message.sender === username) {
            document.getElementById("chat_message_box").innerHTML += ` <li class="chat-left">
                                                    <div class="chat-avatar">
                                                        <img src="data:image/jpeg;base64, ` + userToAvatarMap.get(message.sender) + `" alt="" alt="">
                                                        <div class="chat-name">` + message.sender + `</div>
                                                    </div>
                                                    <div class="chat-text">` + message.content + `
                                                    </div>
                                                    <div class="chat-hour">` + new Date().toLocaleTimeString().slice(0, -3) + `<span
                                                            class="fa fa-check-circle"></span></div>
                                                </li>`


        } else {
            document.getElementById("chat_message_box").innerHTML += `<li class="chat-right">
                                                    <div class="chat-hour">` + new Date().toLocaleTimeString().slice(0, -3) + `<span
                                                            class="fa fa-check-circle"></span></div>
                                                    <div class="chat-text">` + message.content + `
                                                    </div>
                                                    <div class="chat-avatar">
                                                        <img src="data:image/jpeg;base64, ` + userToAvatarMap.get(message.sender) + `" alt="">
                                                        <div class="chat-name">` + message.sender + `</div>
                                                    </div>
                                                </li>`


        }

        getActiveUsers();
        document.getElementById("scroller").scrollBy(0, 100000);
    } else if (message.type === 'JOIN' && message.sender !== username) {
        document.getElementById(message.sender).innerHTML = `
                                                                      <div class="user">
                                                                         <img src="data:image/jpeg;base64, ` + userToAvatarMap.get(message.sender) + `" alt="">
                                                                             <span class="status online"></span>
                                                                      </div>
                                                                        <p class="name-time"><span class="name">` + message.sender + `</span></p>
                                                                      `
        getActiveUsers();

    } else if (message.type === 'PICTURE') {
        if (message.sender === username) {
            document.getElementById("chat_message_box").innerHTML += ` <li class="chat-left">
                                                    <div class="chat-avatar">
                                                        <img src="data:image/jpeg;base64, ` + userToAvatarMap.get(message.sender) + `" alt="">
                                                        <div class="chat-name">` + message.sender + `</div>
                                                    </div>
                                                    <img src="data:image/jpeg;base64,` + message.content + `" alt="">
                                                    <div class="chat-hour">` + new Date().toLocaleTimeString().slice(0, -3) + `<span
                                                            class="fa fa-check-circle"></span></div>
                                                </li>`
            document.getElementById("scroller").scrollBy(0, 100000);

        } else {
            document.getElementById("chat_message_box").innerHTML += `<li class="chat-right">
                                                    <div class="chat-hour">` + new Date().toLocaleTimeString().slice(0, -3) + `<span
                                                            class="fa fa-check-circle"></span></div>
                                                    <img src="data:image/jpeg;base64,` + message.content + `" alt="">
                                                    <div class="chat-avatar">
                                                        <img src="data:image/jpeg;base64, ` + userToAvatarMap.get(message.sender) + `" alt="">
                                                        <div class="chat-name">` + message.sender + `</div>
                                                    </div>
                                                </li>`

        }
        getActiveUsers();
        document.getElementById("scroller").scrollBy(0, 100000);
    } else if (message.type === 'RECORDING') {
        //convert base64 to audio ogg blob
        //get an url of the blob and set it to the audio tag
        //play the audio
        let binary = convertURIToBinary(message.content);
        let blob = new Blob([binary], {
            type: 'audio/ogg'
        });
        let blobUrl = URL.createObjectURL(blob);


        if (message.sender === username) {
            document.getElementById("chat_message_box").innerHTML += ` <li class="chat-left">
                                                    <div class="chat-avatar">
                                                        <img src="data:image/jpeg;base64, ` + userToAvatarMap.get(message.sender) + `" alt="">
                                                        <div class="chat-name">` + message.sender + `</div>
                                                    </div>
                                                    <audio controls type="audio/ogg" src="` + blobUrl + `"></audio>
                                                    <div class="chat-hour">` + new Date().toLocaleTimeString().slice(0, -3) + `<span
                                                            class="fa fa-check-circle"></span></div>
                                                </li>`
            document.getElementById("scroller").scrollBy(0, 1000000);

        } else {
            document.getElementById("chat_message_box").innerHTML += `<li class="chat-right">
                                                    <div class="chat-hour">` + new Date().toLocaleTimeString().slice(0, -3) + `<span
                                                            class="fa fa-check-circle"></span></div>
                                                    <audio controls type="audio/ogg" src="` + blobUrl + `"></audio>
                                                    <div class="chat-avatar">
                                                        <img src="data:image/jpeg;base64, ` + userToAvatarMap.get(message.sender) + `" alt="">
                                                        <div class="chat-name">` + message.sender + `</div>
                                                    </div>
                                                </li>`

        }
        document.getElementById("scroller").scrollBy(0, 1000000);
        getActiveUsers();
    } else if (message.type === 'LEAVE' && message.sender !== username) {
        if (document.getElementById(message.sender) !== null) {
            /*document.getElementById(message.sender).remove();*/
            document.getElementById(message.sender).innerHTML = `
                                                                      <div class="user">
                                                                         <img src="data:image/jpeg;base64, ` + userToAvatarMap.get(message.sender) + `" alt="">
                                                                             <span class="status offline"></span>
                                                                      </div>
                                                                        <p class="name-time"><span class="name">` + message.sender + `</span></p>
                                                                      `

        }
        getActiveUsers();
    }
}

// Load message history from the server and enters a room
function changeChatRoom(roomId) {
    enterRoom(roomId);
    loadMessageHistory(roomId);
    document.getElementById("scroller").scrollBy(0, 1000000);
}

//helper function to load message history from the server
function loadMessageHistory(room_id_name) {

    let settings = {
        "url": "http://79.67.179.40:8080/download/chat/" + room_id_name + "/messages",
        "method": "GET"
    };

    $.ajax(settings).done(function (response) {

        document.getElementById("chat_message_box").innerHTML = "";
        let chatHistory = response;

        for (let i = 0; i < chatHistory.length; i++) {
            if (chatHistory[i].type === 'CHAT') {
                // console.log(chatHistory[i].sender + " TYPE:" + chatHistory[i].type);
                if (chatHistory[i].sender === username) {

                    // use <br> to cut the text into two lines
                    document.getElementById("chat_message_box").innerHTML += ` <li class="chat-left">
                                                    <div class="chat-avatar">
                                                        <img src="data:image/jpeg;base64, ` + userToAvatarMap.get(chatHistory[i].sender) + `" alt="">
                                                        <div class="chat-name">` + chatHistory[i].sender + `</div>
                                                    </div>
                                                    <div class="chat-text">` + chatHistory[i].content + `
                                                    </div>
                                                    <div class="chat-hour">` + chatHistory[i].time.slice(11, 16) + `<span
                                                            class="fa fa-check-circle"></span></div>
                                                </li>`

                } else {
                    document.getElementById("chat_message_box").innerHTML += `<li class="chat-right">
                                                    <div class="chat-hour">` + chatHistory[i].time.slice(11, 16) + `<span
                                                            class="fa fa-check-circle"></span></div>
                                                    <div class="chat-text">` + chatHistory[i].content + `
                                                    </div>
                                                    <div class="chat-avatar">
                                                        <img src="data:image/jpeg;base64, ` + userToAvatarMap.get(chatHistory[i].sender) + `" alt="">
                                                        <div class="chat-name">` + chatHistory[i].sender + `</div>
                                                    </div>
                                                </li>`


                }
            } else if (chatHistory[i].type === 'PICTURE') {
                // console.log(chatHistory[i].sender + " TYPE:" + chatHistory[i].type);
                if (chatHistory[i].sender === username) {

                    // use <br> to cut the text into two lines
                    document.getElementById("chat_message_box").innerHTML += ` <li class="chat-left">
                                                    <div class="chat-avatar">
                                                        <img src="data:image/jpeg;base64, ` + userToAvatarMap.get(chatHistory[i].sender) + `" alt="">
                                                        <div class="chat-name">` + chatHistory[i].sender + `</div>
                                                    </div>
                                                    <img src="data:image/jpeg;base64,` + chatHistory[i].content + `" alt="">
                                                    <div class="chat-hour">` + chatHistory[i].time.slice(11, 16) + `<span
                                                            class="fa fa-check-circle"></span></div>
                                                </li>`

                } else {
                    document.getElementById("chat_message_box").innerHTML += `<li class="chat-right">
                                                    <div class="chat-hour">` + chatHistory[i].time.slice(11, 16) + `<span
                                                            class="fa fa-check-circle"></span></div>
                                                    <img src="data:image/jpeg;base64,` + chatHistory[i].content + `" alt="">
                                                    <div class="chat-avatar">
                                                        <img src="data:image/jpeg;base64, ` + userToAvatarMap.get(chatHistory[i].sender) + `" alt="">
                                                        <div class="chat-name">` + chatHistory[i].sender + `</div>
                                                    </div>
                                                </li>`


                }
            } else if (chatHistory[i].type === 'RECORDING') {
                //convert base64 to audio ogg blob
                //get an url of the blob and set it to the audio tag
                //play the audio
                let binary = convertURIToBinary(chatHistory[i].content);
                let blob = new Blob([binary], {
                    type: 'audio/ogg'
                });
                let blobUrl = URL.createObjectURL(blob);


                if (chatHistory[i].sender === username) {
                    document.getElementById("chat_message_box").innerHTML += ` <li class="chat-left">
                                                    <div class="chat-avatar">
                                                        <img src="data:image/jpeg;base64, ` + userToAvatarMap.get(chatHistory[i].sender) + `" alt="">
                                                        <div class="chat-name">` + chatHistory[i].sender + `</div>
                                                    </div>
                                                    <audio controls type="audio/ogg" src="` + blobUrl + `"></audio>
                                                    <div class="chat-hour">` + new Date().toLocaleTimeString().slice(0, -3) + `<span
                                                            class="fa fa-check-circle"></span></div>
                                                </li>`
                    document.getElementById("scroller").scrollBy(0, 100000);

                } else {
                    document.getElementById("chat_message_box").innerHTML += `<li class="chat-right">
                                                    <div class="chat-hour">` + new Date().toLocaleTimeString().slice(0, -3) + `<span
                                                            class="fa fa-check-circle"></span></div>
                                                    <audio controls type="audio/ogg" src="` + blobUrl + `"></audio>
                                                    <div class="chat-avatar">
                                                        <img src="data:image/jpeg;base64, ` + userToAvatarMap.get(chatHistory[i].sender) + `" alt="">
                                                        <div class="chat-name">` + chatHistory[i].sender + `</div>
                                                    </div>
                                                </li>`

                }
            }
        }

        document.getElementById("scroller").scrollBy(0, 100000);
    });
}


//When page is ready
$(document).ready(function () {


    //setting up the actual livechat websocket
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected);

    //setting up the register websocket
    registerStompClient = Stomp.over(registerSocket);
    registerStompClient.connect({}, onRegisterSocketConnected);

    //get all the users avatars
    getAllAvatars();
    initializeSockets();
    //getting all registered users
    getAllRegisteredUsers();
    //setting up the active users list
    getActiveUsers();
    //loading the message history of the current topic(room)
    loadMessageHistory(topic);
    //scrolling to the bottom of the chat box
    document.getElementById("scroller").scrollBy(0, 100000);


    //setting up the chat room list
    document.getElementById('selected_user_name').innerHTML = topic;
    //setting up username greeter
    document.getElementById('username_greeter').value = username;

    //setting up recording buttons
    document.getElementById("stop").disabled = true;
    document.getElementById("start").disabled = true;


});

function initializeSockets() {

}

function getActiveUsers() {
    let settings = {
        "url": "http://79.67.179.40:8080/download/chat/users",
        "method": "GET",
    };

    $.ajax(settings).done(function (response) {
        //console.log("USERS:" + response);
        if (response.length > 0) {

            for (let i = 0; i < response.length; i++) {
                if (response[i] !== username) {
                    if (document.getElementById(response[i]) === null) {
                        continue;

                    }
                    document.getElementById(response[i]).innerHTML = `
                                                                      <div class="user">
                                                                         <img src="data:image/jpeg;base64, ` + userToAvatarMap.get(response[i]) + `" alt="">
                                                                             <span class="status online"></span>
                                                                      </div>
                                                                        <p class="name-time"><span class="name">` + response[i] + `</span></p>
                                                                      `
                }
            }
        } else {
            // console.log("No online users found yet!");
        }
    });
}

// Logouts the user from the chatroom and clears the session/cookies
function logout() {
    stompClient.send(`${topic}/leave`,
        {},
        JSON.stringify({sender: username, type: 'LEAVE'})
    );
    let settings = {
        "url": "http://79.67.179.40:8080/logout",
        "method": "POST",
        "timeout": 0,
    };

    $.ajax(settings).done(function () {
        window.location.href = "http://79.67.179.40:8080/";
    });


}

function getAllRegisteredUsers() {
    let settings = {
        "url": "http://79.67.179.40:8080/download/chat/registeredUsers",
        "method": "GET",
    };

    $.ajax(settings).done(function (response) {
        for (let i = 0; i < response.length; i++) {
            if (response[i] !== username) {
                let socketByAlphabeticalOrder;
                if (username.localeCompare(response[i]) === -1) {
                    socketByAlphabeticalOrder = username + response[i];
                } else {
                    socketByAlphabeticalOrder = response[i] + username;
                }
                //console.log(socketByAlphabeticalOrder);
                //console.log(userToAvatarMap.get("bob"));
                document.getElementById("active_users").innerHTML += `<li class="person" onclick="changeChatRoom(\`` + socketByAlphabeticalOrder + `\`)" data-chat="person4" id="` + response[i] + `">
                                                                            <div class="user">
                                                                                <img src="data:image/jpeg;base64, ` + userToAvatarMap.get(response[i]) + `" alt="">
                                                                                    <span class="status offline"></span>
                                                                            </div>
                                                                                <p class="name-time"><span class="name">` + response[i] + `</span>
                                                                                </p>
                                                                        </li>`
            }

        }
    });
}

// Setups the register callback socket connection
function onRegisterSocketConnected() {

    if (currentRegistrationSubscription) {
        currentRegistrationSubscription.unsubscribe();
    }
    try {
        currentRegistrationSubscription = registerStompClient.subscribe(`/channel/registerCallbackSocket`, registerMessageReceived);
    } catch (e) {
        registerStompClient.connect({}, onRegisterSocketConnected);
    }

}

//wrapper function to parse the registered user's name and add it to the list
function registerMessageReceived(payload) {
    let message = JSON.parse(payload.body);
    if (message.type === 'REGISTER') {
        let socketByAlphabeticalOrder;
        if (username.localeCompare(message.content) === -1) {
            socketByAlphabeticalOrder = username + message.sender;
        } else {
            socketByAlphabeticalOrder = message.sender + username;
        }
        // console.log(socketByAlphabeticalOrder);
        console.log(message.content);
        document.getElementById("active_users").innerHTML += `<li class="person"  onclick="changeChatRoom(\`` + socketByAlphabeticalOrder + `\`)" data-chat="person4" id="` + message.sender + `">
                                                                            <div class="user">
                                                                                <img src="data:image/jpeg;base64, ` + default_image + `" alt="">
                                                                                    <span class="status offline"></span>
                                                                            </div>
                                                                                <p class="name-time"><span class="name">` + message.sender + `</span>
                                                                                </p>
                                                                        </li>`
    } else if (message.type === 'REMOVE' && message.sender !== "deleted") {
        //remove users from the list

        document.getElementById(message.sender).remove();

    } else if (message.type === 'SPECIAL') {
        sendNotification_1("A user has changed their avatar! Reload to see the changes!");
    }
    //console.log(message);
}


//Account settings button pressed
function settingsPage() {
    //Notify the server that the user is leaving the chatroom
    let chatMessage = {
        sender: username,
        content: null,
        type: 'LEAVE'
    };
    stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(chatMessage));

    window.location.href = "http://79.67.179.40:8080/settings";
}


///////AUDIO FUNCTIONS////////
let recorder;
let chunks;

function getPermission() {
    navigator.mediaDevices.getUserMedia({audio: true}).then(_stream => {
        document.getElementById("start").disabled = false;
        document.getElementById("micPermission").disabled = true;
        recorder = new MediaRecorder(_stream);
        recorder.ondataavailable = e => {
            chunks.push(e.data);
            if (recorder.state === 'inactive') makeLink();
        };
    });
}

function record() {
    document.getElementById("stop").disabled = false;
    document.getElementById("start").disabled = true;
    chunks = [];

    try {
        recorder.start();
    } catch (e) {
        console.log("Give permission first!");
    }

}

function stopRecord() {
    document.getElementById("stop").disabled = true;
    document.getElementById("start").disabled = false;
    try {
        recorder.stop();
    } catch (e) {
        console.log("Recording stopped or has not started yet!");
    }
}

function makeLink() {
    let blob = new Blob(chunks, {type: 'audio/ogg'});
    let reader = new FileReader();

    /////////////////////FUTURE ME, DONT CHANGE THIS!!!!

    reader.onloadend = function () {
        const base64data = reader.result;
        //console.log(base64data);

        //send the audio to socket...
        //console.log("Sending audio to socket...");
        let chatMessage = {
            sender: username,
            content: base64data,
            type: 'RECORDING'
        };
        stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(chatMessage));
    }
    reader.readAsDataURL(blob);
    ////////////////////////


    //converting from arrayBuffer to blob in order to display to browser(server(socket) -> client)
    //let new_blob = new Blob([new Uint8Array(arrayBuffer)]);


    //Convert blob to url to display it in a player with controls
    //let url = URL.createObjectURL(blob);
    //document.getElementById("scroller").innerHTML = `<audio controls="" src="` + url + `"></audio>`
}

//from base64 to ogg audio blob file
function convertURIToBinary(dataURI) {
    let BASE64_MARKER = ';base64,';
    let base64Index = dataURI.indexOf(BASE64_MARKER) + BASE64_MARKER.length;
    let base64 = dataURI.substring(base64Index);
    let raw = window.atob(base64);
    let rawLength = raw.length;
    let arr = new Uint8Array(new ArrayBuffer(rawLength));

    for (let i = 0; i < rawLength; i++) {
        arr[i] = raw.charCodeAt(i);
    }
    return arr;
}


///get all avatar images
function getAllAvatars() {
    let settings = {
        "url": "http://79.67.179.40:8080/user/download/avatars",
        "method": "POST",
        async: false,
    };

    $.ajax(settings).done(function (response) {
        ////array of array of username,avatar -> [ ["username","avatar" ], ["username","avatar"], ... ]
        //console.log(response);
        ////array of username,avatar -> ["username", "avatar"]
        //console.log(response[0]);
        ////string of username -> username
        //console.log(response[0][0]);
        for (let i = 0; i < response.length; i++) {
            userToAvatarMap.set(response[i][0], response[i][1]);
        }
    });
}


//send notifications
function sendNotification_1(message) {
    document.getElementById("notification_box").innerHTML = ` <div style="background: #0d0d18;" class="alert fade alert-simple alert-success alert-dismissible text-left font__family-montserrat font__size-16 font__weight-light brk-library-rendered rendered show">
                                            
                                            <strong class="font__weight-semibold">` + message + `</strong>
                                        </div>`
}

//instead of sending get for an already predefined image, just set it here as its base64 encoding
let default_image = "iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAYAAABw4pVUAAAABmJLR0QA/wD/AP+gvaeTAAAEEElEQVR4nO2dTahUZRjHf/d6y1tZftyCbrQK7PoZgtKiDwKNSKIgFy3VthKB4EpcuHChmHi1lSAELQsMCWlhuMllUYmbkuAWkZoiiNUkeB0X7wzK4Nxz3jPnff/PmXl+8Ocu7jnzfPzPmTnnnXfeA47jOI7jOE79jKkTiGQc2ARs7vx9EXgeeKLz/3+BP4FfgB+Ac8D3wN3smQ4508AB4A+gHanfO/tOZ896CFkOHANaxBvRq1bntZZlrWCI2AZcZXAjenWl89pOSRYRjuS6jejVbCeWswCTwCnSm9HVqU5M5yEsAr4knxldnQYmMtTXOHK8TfXT0Qz1NYpt6MxoE+5T3k9eZUNYCvyF1pA24epreeJaG8Fx9GZ0dSxxreZ5FvgPvRFdtYDnklZcwLgyOPAR8Jg4hweZBHapk1AxThhnUp8VvZpDf6BKeBl98/tpU8K6F0R5JGwRxi5isyqw0pCNwthFjOQZMiOMXcQqVWClIdLLywJkX2YpDVkijF3Ek6rAyu/U28LYZZD0ZiSvty3jhhjDDTGGG2IMN8QYbogx3BBjuCHGcEOM4YYYww0xhtKQ28LYRbRUgZWG3BTGLkKWm9KQq8LYRfytCqw05KIwdhEXVIGVhvwkjF3Ez+oEFGxAP92nn9YmrNssY9icKPdbyqKLUL5ltYHPhPH7cVKdgJJpwv2I+qzoqgU8k7TiAtR36peBE+IcHmQWuKZOQs0UcAP92XEZ4fQfa3yA1oy7wHvJq2wYJ9EZ8kmG+hrHo8AZ8pvxFb6AQF8eB74lnxmnCQeCswAT5PnN+gkMLhhgeb2sduLXN1m7+j7E6cENMYYbYgw3xBhuiDHcEGO4IcZwQ4xh1ZCtGWK8miFG41kM7Af+J/3QyQ1ge56ymscjwIfAJfKP9p4FXklfYjPork81R34jenUOeAe7b+NJWQbswcZai726BOxmRJYknyEMrd9C3/gitYDPgZeSdELMa8DXhO+u1Y2uovPAu7V3RcCbhGLUDa1L3yFc3GwQVgJfoG9gKp0F1tTWrYRMAoexNRsxlW4Dhwj3TiaZAX5E36jcugisq6F/tbIT+Ad9c1S6BewYuIs1MEYY6lA3xIoOIp488elDkhp1zQ7U0QHYVzLBUdTeAfpaibeB+RoSH1bNA29V7m4kU4SfDauLtq5KzyOpMsn4CPBGhf1GjSWEucrfxOwUe0XwAvArPlu8LHcIoxZzZXeIHfv/GDcjhgnCM1JKE3OGTBCWw1gRE8DhOuFJQvNlNo45Q17HzajC00RMqIgxpJHDzkYo/ayUGEPWV0jECZQefIwxRPZMjSFgddkNYwzxz4/qTJXdMMaQpyok4gSWlt0w5rK3XSER5z6lej2Sk8Icx3Ecx3Ecx3Gc+9wD6vspBPkgA3MAAAAASUVORK5CYII=";