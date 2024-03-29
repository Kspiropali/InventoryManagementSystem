let default_image = "iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAYAAABw4pVUAAAABmJLR0QA/wD/AP+gvaeTAAAEEElEQVR4nO2dTahUZRjHf/d6y1tZftyCbrQK7PoZgtKiDwKNSKIgFy3VthKB4EpcuHChmHi1lSAELQsMCWlhuMllUYmbkuAWkZoiiNUkeB0X7wzK4Nxz3jPnff/PmXl+8Ocu7jnzfPzPmTnnnXfeA47jOI7jOE79jKkTiGQc2ARs7vx9EXgeeKLz/3+BP4FfgB+Ac8D3wN3smQ4508AB4A+gHanfO/tOZ896CFkOHANaxBvRq1bntZZlrWCI2AZcZXAjenWl89pOSRYRjuS6jejVbCeWswCTwCnSm9HVqU5M5yEsAr4knxldnQYmMtTXOHK8TfXT0Qz1NYpt6MxoE+5T3k9eZUNYCvyF1pA24epreeJaG8Fx9GZ0dSxxreZ5FvgPvRFdtYDnklZcwLgyOPAR8Jg4hweZBHapk1AxThhnUp8VvZpDf6BKeBl98/tpU8K6F0R5JGwRxi5isyqw0pCNwthFjOQZMiOMXcQqVWClIdLLywJkX2YpDVkijF3Ek6rAyu/U28LYZZD0ZiSvty3jhhjDDTGGG2IMN8QYbogx3BBjuCHGcEOM4YYYww0xhtKQ28LYRbRUgZWG3BTGLkKWm9KQq8LYRfytCqw05KIwdhEXVIGVhvwkjF3Ez+oEFGxAP92nn9YmrNssY9icKPdbyqKLUL5ltYHPhPH7cVKdgJJpwv2I+qzoqgU8k7TiAtR36peBE+IcHmQWuKZOQs0UcAP92XEZ4fQfa3yA1oy7wHvJq2wYJ9EZ8kmG+hrHo8AZ8pvxFb6AQF8eB74lnxmnCQeCswAT5PnN+gkMLhhgeb2sduLXN1m7+j7E6cENMYYbYgw3xBhuiDHcEGO4IcZwQ4xh1ZCtGWK8miFG41kM7Af+J/3QyQ1ge56ymscjwIfAJfKP9p4FXklfYjPork81R34jenUOeAe7b+NJWQbswcZai726BOxmRJYknyEMrd9C3/gitYDPgZeSdELMa8DXhO+u1Y2uovPAu7V3RcCbhGLUDa1L3yFc3GwQVgJfoG9gKp0F1tTWrYRMAoexNRsxlW4Dhwj3TiaZAX5E36jcugisq6F/tbIT+Ad9c1S6BewYuIs1MEYY6lA3xIoOIp488elDkhp1zQ7U0QHYVzLBUdTeAfpaibeB+RoSH1bNA29V7m4kU4SfDauLtq5KzyOpMsn4CPBGhf1GjSWEucrfxOwUe0XwAvArPlu8LHcIoxZzZXeIHfv/GDcjhgnCM1JKE3OGTBCWw1gRE8DhOuFJQvNlNo45Q17HzajC00RMqIgxpJHDzkYo/ayUGEPWV0jECZQefIwxRPZMjSFgddkNYwzxz4/qTJXdMMaQpyok4gSWlt0w5rK3XSER5z6lej2Sk8Icx3Ecx3Ecx3Gc+9wD6vspBPkgA3MAAAAASUVORK5CYII=";
'use strict';

//for actual livechat
let stompClient;
let currentSubscription;

//for registration notification
let registerStompClient;
let currentRegistrationSubscription;


// username and current topic(full path room name)
let username = document.cookie.split(",")[0].split("=")[1];
document.getElementById("dashboard_name").innerHTML = "Welcome to the Dashboard, " + username;
let topic;

//When page is ready
$(document).ready(function () {

    //setting up the actual livechat websocket
    stompClient = Stomp.over(new SockJS('/ws'));
    stompClient.connect({}, socketConnected);
    // when connected, call a function

    //setting up the register websocket
    registerStompClient = Stomp.over(new SockJS('/ws'));
    registerStompClient.connect({}, onRegisterSocketConnected);

    //getting all registered users
    getAllRegisteredUsers();
});

function socketConnected(){
    document.getElementById("main_chat_box").style.display = "contents";
}

//////////////////////////////////////// MESSAGE SOCKET FUNCTIONS ///////////////////////////////////////////////////////////////////////
function enterRoom(newRoomId) {
    document.getElementById("chat_box").style.display = "block";
    // since roomId is in user1user2 format, we just need to remove our current username from the string to get destination username
    document.getElementById('selected_user_name').innerHTML = newRoomId.replace(username, "");
    topic = `/app/chat/${newRoomId}`;

    if (currentSubscription) {
        currentSubscription.unsubscribe();
    }
    currentSubscription = stompClient.subscribe(`/channel/${newRoomId}`, onMessageReceived);


    stompClient.send(`${topic}/addUser`,
        {},
        JSON.stringify({sender: username, type: 'JOIN', destination: newRoomId})
    );
}

//helper function to load message history from the server
function loadMessageHistory(room_id_name) {

    let settings = {
        "url": base_url+"download/chat/" + room_id_name + "/messages",
        "method": "GET"
    };

    $.ajax(settings).done(function (response) {

        document.getElementById("chat_message_box").innerHTML = "";
        let chatHistory = response;

        for (let i = 0; i < chatHistory.length; i++) {
                // console.log(chatHistory[i].sender + " TYPE:" + chatHistory[i].type);
                if (chatHistory[i].sender === username) {

                    // use <br> to cut the text into two lines
                    document.getElementById("chat_message_box").innerHTML += ` <li class="chat-left">
                                                    <div class="chat-avatar">
                                                        <img src="data:image/jpeg;base64, `+default_image+`">
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
                                                        <img src="data:image/jpeg;base64, `+default_image+`">
                                                        <div class="chat-name">` + chatHistory[i].sender + `</div>
                                                    </div>
                                                </li>`


                }
        }

        document.getElementById("scroller").scrollBy(0, 100000);
    });
}


function changeChatRoom(roomId) {
    enterRoom(roomId);
    loadMessageHistory(roomId);
    document.getElementById("scroller").scrollBy(0, 1000000);
}

document.getElementById("chat_box").addEventListener('keydown', function (e) {
    if (e.keyCode === 13) {
        if (document.getElementById("chat_box").value !== "") {
            let messageInput = document.getElementById("chat_box").value;
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
    }
}, false);


function onMessageReceived(payload) {
    let message = JSON.parse(payload.body);

    if (message.type === 'CHAT') {
        if (message.sender === username) {
            document.getElementById("chat_message_box").innerHTML += ` <li class="chat-left">
                                                    <div class="chat-avatar">
                                                        <img src="data:image/jpeg;base64, `+default_image+`">
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
                                                        <img src="data:image/jpeg;base64, `+default_image+`">
                                                        <div class="chat-name">` + message.sender + `</div>
                                                    </div>
                                                </li>`


        }
        document.getElementById("scroller").scrollBy(0, 100000);
    } else if (message.type === 'JOIN' && message.sender !== username) {
        document.getElementById(message.sender).innerHTML = `
                                                                      <div class="user">
                                                                         <img src="data:image/jpeg;base64, `+default_image+`">
                                                                             <span class="status online"></span>
                                                                      </div>
                                                                        <p class="name-time"><span class="name">` + message.sender + `</span></p>
                                                                      `

    } else if (message.type === 'LEAVE' && message.sender !== username) {
        if (document.getElementById(message.sender) !== null) {
            /*document.getElementById(message.sender).remove();*/
            document.getElementById(message.sender).innerHTML = `
                                                                      <div class="user">
                                                                         <img src="data:image/jpeg;base64, `+default_image+`">
                                                                             <span class="status offline"></span>
                                                                      </div>
                                                                        <p class="name-time"><span class="name">` + message.sender + `</span></p>
                                                                      `

        }
    }
}


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


//////////////////////////////////////////////////////////////////////////// REGISTER SOCKET FUNCTIONS/////////////////////////////////////////////////
// Setups the register callback socket connection
function onRegisterSocketConnected() {

    if (currentRegistrationSubscription) {
        currentRegistrationSubscription.unsubscribe();
    }
    try{
        currentRegistrationSubscription = registerStompClient.subscribe(`/channel/registerCallbackSocket`, registerMessageReceived);
    }
    catch (e) {
        registerStompClient.connect({}, onRegisterSocketConnected);
    }
}

//wrapper function to parse the registered user's name and add it to the list
function registerMessageReceived(payload) {
    let message = JSON.parse(payload.body);
    if (message.type === 'REGISTER') {
        var socketByAlphabeticalOrder;
        if (username.localeCompare(message.content) === -1) {
            socketByAlphabeticalOrder = username + message.sender;
        } else {
            socketByAlphabeticalOrder = message.sender + username;
        }
        // console.log(socketByAlphabeticalOrder);
        console.log(message.content);
        document.getElementById("active_users").innerHTML += `<li class="person"  onclick="changeChatRoom(\`` + socketByAlphabeticalOrder + `\`)" data-chat="person4" id="` + message.sender + `">
                                                                            <div class="user">
                                                                                <img src="data:image/jpeg;base64, `+default_image+`">
                                                                                    <span class="status offline"></span>
                                                                            </div>
                                                                                <p class="name-time"><span class="name">` + message.sender + `</span>
                                                                                </p>
                                                                        </li>`
    } else if (message.type === 'REMOVE') {
        //remove users from the list
        alert(message.sender);
        document.getElementById(message.sender).remove();
    }
}



function getAllRegisteredUsers() {
    let settings = {
        "url": base_url+"download/chat/registeredUsers",
        "method": "GET",
    };

    $.ajax(settings).done(function (response) {
        for (let i = 0; i < response.length; i++) {
            if (response[i] !== username) {
                var sortByAlphabeticalOrder;
                if (username.localeCompare(response[i]) === -1) {
                    sortByAlphabeticalOrder = username + response[i];
                } else {
                    sortByAlphabeticalOrder = response[i] + username;
                }
                //console.log(socketByAlphabeticalOrder);
                document.getElementById("active_users").innerHTML += `<li class="person" onclick="changeChatRoom(\`` + sortByAlphabeticalOrder + `\`)" data-chat="person4" id="` + response[i] + `">
                                                                            <div class="user">
                                                                                <img src="data:image/jpeg;base64, `+default_image+`">
                                                                                    <span class="status offline"></span>
                                                                            </div>
                                                                                <p class="name-time"><span class="name">` + response[i] + `</span>
                                                                                </p>
                                                                        </li>`
            }

        }
    });
}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////