// check if kiosk is logged in by sending a login request
let myHeaders = new Headers();
myHeaders.append("Content-Type", "application/json");
myHeaders.append("Access-Control-Allow-Origin", base_url);
myHeaders.append("Access-Control-Allow-Methods", "POST");
myHeaders.append("Access-Control-Allow-Headers", "Content-Type, Authorization");
//"Csrf-Token": getCookie("XSRF-TOKEN"),
myHeaders.append("Access-Control-Allow-Credentials", "true");


let requestOptions = {
    method: 'POST',
    headers: myHeaders,
    redirect: 'follow',
    timeout: 0,
};

fetch(base_url + "kiosk/ping", requestOptions)
    .then(response => response.text())
    .then(result => {
        if (!result.startsWith("You are logged in with kiosk id:")) {
            //clear all cookies
            document.cookie.split(";").forEach(function (c) {
                document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/");
            });

            window.location.href = base_url + "kiosk";
        }
    })
    .catch(error => console.log('error', error));

///////////////////////////////////////////////////////////////////////
// Event listener to always focus on the barcode input
inputBoxInterval = setInterval(function () {
    document.getElementById("barcodeText_input").focus();
}, 50);
// check last input time of a character
let previousInputTime = 0;
// count of characters in the barcode input
let count = 0;
// all product names in an array
let product_names = [];

window.onload = function () {
    document.getElementById("barcodeText_input").focus();
};
window.onbeforeunload = function () {
    return "Your progress will be lost. Are you sure?";
}

// document is ready
$(document).ready(function () {
    total_price = document.getElementById("all_products_total_price").innerHTML;
});

// infinite loop to focus on the barcode input


// Listen for keypress events on the barcode input
function barcodeInputKeypress() {
    // Calculate the time difference between this input and the previous input
    count++;
    const currentTime = new Date().getTime();
    const timeDifference = previousInputTime === 0 ? 0 : currentTime - previousInputTime;
    previousInputTime = currentTime;

    if (timeDifference > 5) {
        // This is a new input, so reset the code
        document.getElementById("barcodeText_input").value = "";
        previousInputTime = 0;
        count = 0;
    }

    if (document.getElementById("barcodeText_input").value.length === 13) {
        // Barcode is complete, so submit the form
        console.log("Barcode complete, submitting form");

        let barcode = document.getElementById("barcodeText_input").value.slice(0, -1);

        let myHeaders = new Headers();
        myHeaders.append("Access-Control-Allow-Credentials", "true");
        myHeaders.append("Content-Type", "application/json");
        myHeaders.append("Access-Control-Allow-Origin", base_url);
        //"Csrf-Token": getCookie("XSRF-TOKEN"),
        myHeaders.append("Access-Control-Allow-Methods", "POST");
        myHeaders.append("Access-Control-Allow-Headers", "Content-Type, Authorization");
        myHeaders.append("Access-Control-Max-Age", "3600");


        let requestOptions = {
            method: 'POST',
            headers: myHeaders,
            redirect: 'follow'
        };

        fetch(base_url + "kiosk/checkItemByBarcode?barcode=" + barcode, requestOptions)
            .then(response => response.text())
            .then(result => {
                if (result.startsWith("Item not found")) {

                } else if (result === "Kiosk is not logged in") {
                    // logout and delete cookies
                    document.cookie.split(";").forEach(function (c) {
                        document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/");
                    });


                    //remove onbeforeunload event
                    window.onbeforeunload = null;

                    // logout
                    let myHeaders = new Headers();
                    myHeaders.append("Access-Control-Allow-Credentials", "true");
                    myHeaders.append("Content-Type", "application/json");
                    myHeaders.append("Access-Control-Allow-Origin", base_url);
                    //"Csrf-Token": getCookie("XSRF-TOKEN"),
                    myHeaders.append("Access-Control-Allow-Methods", "POST");
                    myHeaders.append("Access-Control-Allow-Headers", "Content-Type, Authorization");
                    myHeaders.append("Access-Control-Max-Age", "3600");


                    let requestOptions = {
                        method: 'POST',
                        headers: myHeaders,
                        redirect: 'follow'
                    };

                    fetch(base_url + "kiosk/logout", requestOptions)
                        .then(response => response.text())
                        .then(result => {
                            // refresh page
                            window.location.href = base_url + "kiosk";
                        });


                }

                let details = result.split(",");
                // 0 -> name, 1 -> price, 2 -> expires, 3 -> type, 4-> base64 image

                if (product_names.includes(details[0])) {
                    document.getElementById("product_quantity_" + details[0]).innerHTML = parseInt(document.getElementById("product_quantity_" + details[0]).innerHTML) + 1;
                    document.getElementById("product_total_price_" + details[0]).innerHTML = (parseInt(document.getElementById("product_quantity_" + details[0]).innerHTML) * details[1]).toFixed(2);
                    document.getElementById("all_products_total_price").innerHTML = (parseFloat(details[1]) + parseFloat(document.getElementById("all_products_total_price").innerHTML)).toFixed(2);
                    document.getElementById("all_products_discount").innerHTML = (parseFloat(document.getElementById("all_products_total_price").innerHTML) * 0.1).toFixed(2);
                } else {
                    document.getElementById("products_table").innerHTML += "                    <tr id='" + details[0] + "' '>\n" +
                        "                        <td class=\"p-4\">\n" +
                        "                            <div class=\"media align-items-center\">\n" +
                        "                                <img src=\"data:image/png;base64, " + details[4] + "\"\n" +
                        "                                     class=\"d-block ui-w-40 ui-bordered mr-4\" alt=\"\">\n" +
                        "                                <div class=\"media-body\">\n" +
                        "                                    <a href=\"#\" class=\"d-block text-dark\">" + details[0] + "</a>\n" +
                        "                                    <small>\n" +
                        "                                        <span class=\"text-muted\">Type: </span> " + details[3] + " &nbsp;\n" +
                        "                                        <span class=\"text-muted\">Discounted: </span> 10%\n" +
                        "                                    </small>\n" +
                        "                                </div>\n" +
                        "                            </div>\n" +
                        "                        </td>\n" +
                        "                        <td class=\"text-right font-weight-semibold align-middle p-4\" id='product_price_" + details[0] + "'> " + details[1] + " </td>\n" +
                        "                        <td class=\"align-middle text-right p-4\" id='product_quantity_" + details[0] + "'> 1 </td>\n" +
                        "                        <td class=\"text-right font-weight-semibold align-middle p-4\" id='product_total_price_" + details[0] + "' > " + details[1] + " </td>\n" +
                        "                        <td class=\"text-center align-middle px-0\"><button \n" +
                        "                                                                     class=\"shop-tooltip close float-none text-danger\"\n" +
                        "                                                                     title=\"\" data-original-title=\"Remove\" onclick='deleteProduct( \"" + details[0] + "\" )'  >×</button></td>\n" +
                        "                    </tr>"
                    product_names.push(details[0]);
                    document.getElementById("all_products_total_price").innerHTML = (parseFloat(document.getElementById("all_products_total_price").innerHTML) + parseFloat(details[1])).toFixed(2);
                    document.getElementById("all_products_discount").innerHTML = (parseFloat(document.getElementById("all_products_total_price").innerHTML) * 0.1).toFixed(2);
                }
            })
            .catch(error => console.log('error', error));
    }


}

function deleteProduct(product_name) {

    document.getElementById("all_products_total_price").innerHTML = (parseFloat(document.getElementById("all_products_total_price").innerHTML) - parseFloat(document.getElementById("product_total_price_" + product_name).innerHTML)).toFixed(2);
    document.getElementById("all_products_discount").innerHTML = (parseFloat(document.getElementById("all_products_total_price").innerHTML) * 0.1).toFixed(2);
    document.getElementById(product_name).remove()
    // remove element from product_names
    product_names = product_names.filter(function (value) {
        return value !== product_name;
    })
}

function clearAllProductsFromCart() {
    document.getElementById("products_table").innerHTML = "";
    document.getElementById("all_products_total_price").innerHTML = "0";
    product_names = [];
}

function checkout_basket() {
    if (product_names.length === 0) {
        return;
    }
    document.getElementById("overlay").style.display = "block";
    // remove the interval focus
    clearInterval(inputBoxInterval);
}

function checkout() {
    let myHeaders = new Headers();
    myHeaders.append("Access-Control-Allow-Credentials", "true");
    myHeaders.append("Content-Type", "application/json");
    myHeaders.append("Access-Control-Allow-Origin", base_url);
    //"Csrf-Token": getCookie("XSRF-TOKEN"),
    myHeaders.append("Access-Control-Allow-Methods", "POST");
    myHeaders.append("Access-Control-Allow-Headers", "Content-Type, Authorization");
    myHeaders.append("Access-Control-Max-Age", "3600");

    //add prices to the products
    let products = [];
    //enhaned for loop
    for (let product_name of product_names) {
        products.push({
            "name": product_name,
            "quantity": parseInt(document.getElementById("product_quantity_" + product_name).innerHTML),
            "price": parseFloat(document.getElementById("product_price_" + product_name).innerHTML),
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


    fetch(base_url + "analytics/kiosk/checkout", requestOptions)
        .then(response => response.text())
        .then(result => {
            if (result === "Success") {
                document.getElementById("overlay").style.display = "none";
                // re add the interval focus
                inputBoxInterval = setInterval(function () {
                    document.getElementById("barcodeText_input").focus();
                }, 50);
                clearAllProductsFromCart();
                // clear total_discount
                document.getElementById("all_products_discount").innerHTML = "0";
                sendNotification("success", "Payment successful", "Your payment was successful. Thank you for shopping with us.");
            }
        });
}

function cancelPayment() {
    // readd the interval focus
    inputBoxInterval = setInterval(function () {
        document.getElementById("barcodeText_input").focus();
    }, 50);
    document.getElementById("overlay").style.display = "none";
}

function changeActivePayment(paymentId) {
    let paymentMethods = document.getElementsByClassName("paymentMethod");
    for (let i = 0; i < paymentMethods.length; i++) {
        paymentMethods[i].classList.remove("active");
    }

    document.getElementById(paymentId).classList.add("active");
}