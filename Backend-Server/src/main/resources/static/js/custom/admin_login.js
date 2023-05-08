function admin_login(){

    let email = document.getElementById("email_input").value;
    let password = document.getElementById("password_input").value;
    let credentials = "Basic " + btoa(email + ":" + password);

    let myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");
    myHeaders.append("Authorization", credentials);
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

    fetch(base_url+"admin/login", requestOptions)
        .then(response => response.text())
        .then(result => {
            if(result === "Logged in!"){
                document.cookie = "username="+email;
                window.location.href = base_url+"admin/dashboard";
            }else{
                sendNotification(401, "Wrong credentials!")
            }
        })
        .catch(error => console.log('error', error));

    event.preventDefault();
}