let myHeaders = new Headers();
myHeaders.append("Content-Type", "application/json");
myHeaders.append("Access-Control-Allow-Origin", base_url);
myHeaders.append("Access-Control-Allow-Methods", "POST");
myHeaders.append("Access-Control-Allow-Headers", "Content-Type, Authorization");
myHeaders.append("Access-Control-Allow-Credentials", "true");

let requestOptions = {
    method: 'POST',
    headers: myHeaders,
    redirect: 'follow'
};

fetch(base_url+"kiosk/ping", requestOptions)
    .then(response => response.text())
    .then(result => {
        if (result.startsWith("You are logged in with kiosk id:")) {
            window.location.href = base_url+"kiosk/checkout";
        }
    })
    .catch(error => console.log('error', error));

function login() {

    let kioskId = document.getElementById("kiosk_id").value;
    let kioskPassword = "kiosk";
    let auth = "Basic " + btoa(kioskId + ":" + kioskPassword);

    let myHeaders = new Headers();
    myHeaders.append("Authorization", auth);
    myHeaders.append("Content-Type", "application/json");
    myHeaders.append("Access-Control-Allow-Origin", base_url);
    myHeaders.append("Access-Control-Allow-Methods", "POST");
    myHeaders.append("Access-Control-Allow-Headers", "Content-Type, Authorization");
    myHeaders.append("Access-Control-Allow-Credentials", "true");


    let requestOptions = {
        method: 'POST',
        headers: myHeaders,
        redirect: 'follow',
        timeout: 0,
    };

    fetch(base_url+"kiosk/login", requestOptions)
        .then(response => response.text())
        .then(result => {
            if (result === "Kiosk Login Successful") {
                window.location.href = base_url+"kiosk/checkout";
            } else if (result === "Kiosk is not available") {
                sendNotification(401, "Kiosk is not available!")
            } else {
                sendNotification(401, "Wrong kiosk id");
            }
        })
        .catch(error => console.log('error', error));

    event.preventDefault();
}

