let myHeaders = new Headers();
let products_basket = [];

myHeaders.append("Content-Type", "application/json");
myHeaders.append("Access-Control-Allow-Origin", base_url);
myHeaders.append("Access-Control-Allow-Methods", "GET, POST");
//"Csrf-Token": getCookie("XSRF-TOKEN"),
myHeaders.append("Access-Control-Allow-Headers", "Content-Type, Authorization");
myHeaders.append("Access-Control-Allow-Credentials", "true");

let requestOptions = {
    method: 'POST',
    headers: myHeaders,
    redirect: 'follow'
};

// prevent user from leaving the page
window.onbeforeunload = function () {
    return "Your progress will be lost. Are you sure?";
}


fetch(base_url + "user/getItems", requestOptions)
    .then(response => response.text())
    .then(result => {
        let items = JSON.parse(result);

        items.forEach(item => {
            document.getElementById("products_list").innerHTML += '<div class="col-md-4">\n' +
                '                <section class="panel">\n' +
                '                    <div class="pro-img-box">\n' +
                '                        <img class="custom-big-image" id="' + item.name + '_image" src="data:image/jpeg;base64, ' + item.image + ' " alt="">\n' +
                '                        <a href="#" class="adtocart">\n' +
                '                            <img class="custom-image" src="/images/add_basket.png" onclick="addToBasket(\'' + item.name + '\')" alt="add to basket">\n' +
                '                        </a>\n' +
                '                    </div>\n' +
                '                    <div class="panel-body text-center">\n' +
                '                        <h4>\n' +
                '                            <a class="pro-title">\n' +
                '                                ' + item.name + '\n' +
                '                            </a>\n' +
                '                        </h4>\n' +
                '                        <p class="price" id="' + item.name + '_price">' + item.price + '£</p>\n' +
                '                    </div>\n' +
                '                </section>\n' +
                '            </div>';
        });
    });

function addToBasket(item_name) {
    let name = item_name;
    let price = document.getElementById(name + "_price").innerHTML;
    let image = document.getElementById(name + "_image").src;

    // check if product is in basket already
    let is_in_basket = false;
    products_basket.forEach(product => {
        if (product.name === name) {
            is_in_basket = true;
        }
    });


    if (is_in_basket) {
        console.log("Changing quantity and price of: " + name);
        // TODO: increment quantity of product in the basket
        document.getElementById(name + "_basket_quantity").innerHTML++;
        document.getElementById(name + "_basket_price").innerHTML = (document.getElementById(name + "_basket_quantity").innerHTML * price.slice(0, price.length - 1)).toFixed(2) + "£";
    } else {
        console.log("Adding to basket: " + name);
        // add product to basket
        products_basket.push({name: name, price: price});
        document.getElementById("shopping_basket").innerHTML = document.getElementById("shopping_basket").innerHTML + '<div class="item" id="' + name + '_item">\n' +
            '\n' +
            '            <div class="image">\n' +
            '                <img src="' + image + '" style="height: 11rem;width: 14rem" alt="">\n' +
            '            </div>\n' +
            '\n' +
            '            <div class="description">\n' +
            '                <span>  </span>\n' +
            '                <span>' + name + '</span>\n' +
            '                <span>  </span>\n' +
            '            </div>\n' +
            '\n' +
            '            <div class="quantity">\n' +
            '                <button class="plus-btn" onclick="incrementQuantityNTotal(\'' + name + '\')" type="button" name="button">\n' +
            '                    <img src="/images/plus.svg" alt="">\n' +
            '                </button>\n' +
            '                <a type="text" id="' + name + '_basket_quantity"> 1 </a>\n' +
            '                <button class="minus-btn" onclick="decrementQuantityNTotal(\'' + name + '\')" type="button" name="button">\n' +
            '                    <img src="/images/minus.svg" alt="">\n' +
            '                </button>\n' +
            '            </div>\n' +
            '\n' +
            '            <div class="total-price" id="' + name + '_basket_price">' + price + '</div>\n' +
            '        </div>\n' +
            '\n' +
            '\n' +
            '    </div>';
    }
}

function incrementQuantityNTotal(name) {
    let price = document.getElementById(name + "_price").innerHTML;
    document.getElementById(name + "_basket_quantity").innerHTML++;
    document.getElementById(name + "_basket_price").innerHTML = (document.getElementById(name + "_basket_quantity").innerHTML * price.slice(0, price.length - 1)).toFixed(2) + "£";
}

function decrementQuantityNTotal(name) {
    if (document.getElementById(name + "_basket_quantity").innerHTML <= "1") {
        document.getElementById(name + "_item").remove();
        products_basket = products_basket.filter(product => product.name !== name);
        return;
    }
    let price = document.getElementById(name + "_price").innerHTML;
    document.getElementById(name + "_basket_quantity").innerHTML--;
    document.getElementById(name + "_basket_price").innerHTML = (document.getElementById(name + "_basket_quantity").innerHTML * price.slice(0, price.length - 1)).toFixed(2) + "£";
}

function checkout() {
    if (products_basket.length === 0) {
        return;
    }
    let myHeaders = new Headers();
    myHeaders.append("Access-Control-Allow-Headers", "Content-Type, Authorization");
    myHeaders.append("Access-Control-Max-Age", "3600");
    //"Csrf-Token": getCookie("XSRF-TOKEN"),
    myHeaders.append("Access-Control-Allow-Credentials", "true");
    myHeaders.append("Content-Type", "application/json");
    myHeaders.append("Access-Control-Allow-Origin", base_url);
    myHeaders.append("Access-Control-Allow-Methods", "POST");


    let products = [];

    for (let product of products_basket) {
        products.push({
            "name": product.name,
            "quantity": document.getElementById(product.name + "_basket_quantity").innerHTML,
            "price": document.getElementById(product.name + "_basket_price").innerHTML.slice(0, -1),
            "unitId": null
        });
    }

    let requestOptions = {
        method: 'POST',
        headers: myHeaders,
        body: JSON.stringify({
            "products": products,
        }),
        redirect: 'follow'
    };

    fetch(base_url + "analytics/user/checkout", requestOptions)
        .then(response => response.text())
        .then(result => {
            if (result === "Success") {
                // clear basket
                products_basket = [];
                document.getElementById("shopping_basket").innerHTML = "<div class=\"title\">\n" +
                    "            Shopping Cart\n" +
                    "            <button class=\"title\" onclick=\"logout()\"\n" +
                    "                    style=\"margin-left: 8rem;background: #ec2121;color: black;width: 26%;position: absolute;margin-top: -2rem;height: 6%;\">\n" +
                    "                Logout\n" +
                    "            </button>\n" +
                    "            <button class=\"title\" onclick=\"checkout()\"\n" +
                    "                    style=\"margin-left: 30rem;background: aquamarine;width: 26%;position: absolute;margin-top: -2rem;height: 6%;\">\n" +
                    "                Checkout\n" +
                    "            </button>\n" +
                    "        </div>";
            } else {
                alert("Something went wrong, please try again later");
            }
        });
}

function logout() {

    // delete all cookies and session storage
    document.cookie.split(";").forEach(function (c) {
        document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/");
    });
    let requestOptions = {
        method: 'GET',
        redirect: 'follow'
    };

    fetch(base_url + "user/logout", requestOptions);
    sessionStorage.clear();
    // refresh page for changes to take effect
    document.location.reload();
}

/////////////////////////////////////////LIVE CHAT///////////////////////////////////////////////////////////
let default_image = "iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAYAAABw4pVUAAAABmJLR0QA/wD/AP+gvaeTAAAEEElEQVR4nO2dTahUZRjHf/d6y1tZftyCbrQK7PoZgtKiDwKNSKIgFy3VthKB4EpcuHChmHi1lSAELQsMCWlhuMllUYmbkuAWkZoiiNUkeB0X7wzK4Nxz3jPnff/PmXl+8Ocu7jnzfPzPmTnnnXfeA47jOI7jOE79jKkTiGQc2ARs7vx9EXgeeKLz/3+BP4FfgB+Ac8D3wN3smQ4508AB4A+gHanfO/tOZ896CFkOHANaxBvRq1bntZZlrWCI2AZcZXAjenWl89pOSRYRjuS6jejVbCeWswCTwCnSm9HVqU5M5yEsAr4knxldnQYmMtTXOHK8TfXT0Qz1NYpt6MxoE+5T3k9eZUNYCvyF1pA24epreeJaG8Fx9GZ0dSxxreZ5FvgPvRFdtYDnklZcwLgyOPAR8Jg4hweZBHapk1AxThhnUp8VvZpDf6BKeBl98/tpU8K6F0R5JGwRxi5isyqw0pCNwthFjOQZMiOMXcQqVWClIdLLywJkX2YpDVkijF3Ek6rAyu/U28LYZZD0ZiSvty3jhhjDDTGGG2IMN8QYbogx3BBjuCHGcEOM4YYYww0xhtKQ28LYRbRUgZWG3BTGLkKWm9KQq8LYRfytCqw05KIwdhEXVIGVhvwkjF3Ez+oEFGxAP92nn9YmrNssY9icKPdbyqKLUL5ltYHPhPH7cVKdgJJpwv2I+qzoqgU8k7TiAtR36peBE+IcHmQWuKZOQs0UcAP92XEZ4fQfa3yA1oy7wHvJq2wYJ9EZ8kmG+hrHo8AZ8pvxFb6AQF8eB74lnxmnCQeCswAT5PnN+gkMLhhgeb2sduLXN1m7+j7E6cENMYYbYgw3xBhuiDHcEGO4IcZwQ4xh1ZCtGWK8miFG41kM7Af+J/3QyQ1ge56ymscjwIfAJfKP9p4FXklfYjPork81R34jenUOeAe7b+NJWQbswcZai726BOxmRJYknyEMrd9C3/gitYDPgZeSdELMa8DXhO+u1Y2uovPAu7V3RcCbhGLUDa1L3yFc3GwQVgJfoG9gKp0F1tTWrYRMAoexNRsxlW4Dhwj3TiaZAX5E36jcugisq6F/tbIT+Ad9c1S6BewYuIs1MEYY6lA3xIoOIp488elDkhp1zQ7U0QHYVzLBUdTeAfpaibeB+RoSH1bNA29V7m4kU4SfDauLtq5KzyOpMsn4CPBGhf1GjSWEucrfxOwUe0XwAvArPlu8LHcIoxZzZXeIHfv/GDcjhgnCM1JKE3OGTBCWw1gRE8DhOuFJQvNlNo45Q17HzajC00RMqIgxpJHDzkYo/ayUGEPWV0jECZQefIwxRPZMjSFgddkNYwzxz4/qTJXdMMaQpyok4gSWlt0w5rK3XSER5z6lej2Sk8Icx3Ecx3Ecx3Gc+9wD6vspBPkgA3MAAAAASUVORK5CYII=";
'use strict';

//for actual livechat
let stompClient;
let currentSubscription;


// username and current topic(full path room name)
let username = document.cookie.split(",")[0].split("=")[1];
let topic;

//When page is ready
$(document).ready(function () {

    //setting up the actual livechat websocket
    stompClient = Stomp.over(new SockJS('/ws'));
    stompClient.connect({}, socketConnected);

});

function socketConnected() {
    document.getElementById("main_chat_box").style.display = "block";
    if(username > "admin") {
        enterRoom("admin" + username);
        loadMessageHistory("admin" + username);
    } else {
        enterRoom(username);
        loadMessageHistory(username);
    }
}

//////////////////////////////////////// MESSAGE SOCKET FUNCTIONS ///////////////////////////////////////////////////////////////////////
function enterRoom(newRoomId) {
    // since roomId is in user1user2 format, we just need to remove our current username from the string to get destination username
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
        "url": base_url + "download/chat/" + room_id_name + "/messages",
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
                                                        <img src="data:image/jpeg;base64, ` + default_image + `">
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
                                                        <img src="data:image/jpeg;base64, ` + default_image + `">
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
                                                        <img src="data:image/jpeg;base64, ` + default_image + `">
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
                                                        <img src="data:image/jpeg;base64, ` + default_image + `">
                                                        <div class="chat-name">` + message.sender + `</div>
                                                    </div>
                                                </li>`


        }
        document.getElementById("scroller").scrollBy(0, 100000);
    }
}


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////