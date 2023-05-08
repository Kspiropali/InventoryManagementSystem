let myHeaders = new Headers();
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

// Load the Visualization API and the corechart package.
google.charts.load('current', {
    callback: drawCharts,
    packages: ['bar', 'corechart', 'table', 'geochart']
});



function setupTopNumbers() {
    // bring all actively registered users
    fetch(base_url + "admin/users/total", requestOptions)
        .then(response => response.text())
        .then(result => {
            document.getElementById("total_users").innerHTML = result;

            // bring all users registered in the last 24 hours
            fetch(base_url + "admin/users/last24", requestOptions)
                .then(response => response.text())
                .then(result => {
                    document.getElementById("last24_users").innerHTML = result;
                    // calculate difference between total and last 24 hours, percentage
                    let total = document.getElementById("total_users").innerHTML;
                    let last24 = document.getElementById("last24_users").innerHTML;
                    if (last24 === "0") {
                        document.getElementById("last24_percentage").innerHTML = "0% increase in the last 24 hours";
                    } else {
                        let difference = total - last24;
                        let increase_percentage = ((total / difference) * 100).toFixed(2);

                        document.getElementById("last24_percentage").innerHTML = increase_percentage + "% increase in the last 24 hours";
                    }
                })
                .catch(error => console.log('error', error));
        })
        .catch(error => console.log('error', error));

    // bring all users registered in the last 24 hours
    fetch(base_url + "admin/items/total", requestOptions)
        .then(response => response.text())
        .then(result => {
            document.getElementById("total_items").innerHTML = result;
        })
        .catch(error => console.log('error', error));

    // bring all users registered in the last 24 hours
    fetch(base_url + "admin/items/networth", requestOptions)
        .then(response => response.text())
        .then(result => {
            document.getElementById("total_networth").innerHTML = result;
        })
        .catch(error => console.log('error', error));
}

function drawProductSalesCharts() {
    fetch(base_url + "admin/items/top5quantity", requestOptions)
        .then(response => response.text())
        .then(result => {
            // result is a string with the format: "product_name quantity\nproduct_name quantity\n..."
            let rows = result.split("\n");

            // sort rows by quantity
            rows.sort(function(a, b) {
                let a_quantity = parseInt(a.split(" ")[1]);
                let b_quantity = parseInt(b.split(" ")[1]);
                return b_quantity - a_quantity;
            });
            console.log(rows);
            let data = new google.visualization.DataTable();
            data.addColumn('string', 'Product');
            data.addColumn('number', 'Quantity');
            for (let i = 0; i < rows.length; i++) {
                let row = rows[i].split(" ");
                data.addRow([row[0], parseInt(row[1])]);
            }
            let options = {
                'width': 600,
                'height': 415,
            }

            let chart = new google.visualization.PieChart(document.getElementById('product_sales1'));
            let chart2 = new google.visualization.BarChart(document.getElementById('product_sales2'));
            chart.draw(data, options);
            chart2.draw(data, options);
        })
        .catch(error => console.log('error', error));
}

function drawGeoChart() {
    fetch(base_url + "admin/users/regional", requestOptions)
        .then(response => response.text())
        .then(result => {
            console.log(result);
            // result is of format: {Canada=0, RU=0, UnitedKingdom=1, France=0, Germany=1, UnitedStates=1, Albania=0}
            let rows = result.split(",");
            let data = new google.visualization.DataTable();
            data.addColumn('string', 'Country');
            data.addColumn('number', 'Users');
            for (let i = 0; i < rows.length; i++) {
                let row = rows[i].split("=");
                if (row[0] === "UnitedStates") {
                    row[0] = "United States";
                } else if (row[0] === "UnitedKingdom") {
                    row[0] = "United Kingdom";
                }
                data.addRow([row[0], parseInt(row[1])]);
            }

            let options = {
                'width': 1200,
                'height': 415,
            }

            let chart = new google.visualization.GeoChart(document.getElementById('users_geochart'));
            chart.draw(data, options);
        })
        .catch(error => console.log('error', error));
}

function drawRecentTransactionsTable() {
    fetch(base_url + "admin/analytics/transactions", requestOptions)
        .then(response => response.text())
        .then(result => {
            console.log(result);
            // result is of format: '1 Milk User 5.92\n2 Salad User 47.68 '
            // create table of format: 'Transaction ID Product Name Initiator Quantity Total Price'
            let rows = result.split("\n");
            let data = new google.visualization.DataTable();
            data.addColumn('string', 'Transaction ID');
            data.addColumn('string', 'Product Name');
            data.addColumn('string', 'Initiator');
            data.addColumn('number', 'Quantity');
            data.addColumn('number', 'Total Price');
            for (let i = 0; i < rows.length - 1; i++) {
                let row = rows[i].split(" ");
                data.addRow(["00000" + row[0], row[1], row[2], parseInt(row[4]), parseInt(row[3])]);
            }
            let options = {
                'width': 1200,
                'height': 415,
            }

            let table = new google.visualization.Table(document.getElementById('recent_transactions_table'));
            table.draw(data, options);
        })
        .catch(error => console.log('error', error));
}

function drawHealthStatistics() {
    let requestOptions = {
        method: 'GET',
        headers: myHeaders,
        redirect: 'follow'
    };

    fetch(base_url + "admin/actuator/health", requestOptions)
        .then(response => response.text())
        .then(result => {
            let free_mem_in_gigas = JSON.parse(result).components.diskSpace.details.free / 1000000000;
            let used_mem = (JSON.parse(result).components.diskSpace.details.total - JSON.parse(result).components.diskSpace.details.free) / 1000000000;
            // convert to percentage
            free_mem_in_gigas = (free_mem_in_gigas / (free_mem_in_gigas + used_mem)) * 100;
            used_mem = (used_mem / (free_mem_in_gigas + used_mem)) * 100;
            // actuator health endpoint returns a json object, display it in table
            let data = new google.visualization.arrayToDataTable([
                ['Component', 'Status'],
                ['Free Disk Space', free_mem_in_gigas],
                ['Used Disk Space', used_mem],
            ]);

            let options = {
                'width': 600,
                'height': 350,
                pieHole: 0.35,
            }

            let chart = new google.visualization.PieChart(document.getElementById('health_metrics_table_disk'));
            chart.draw(data, options);
        })
        .catch(error => console.log('error', error));

    //getting ready to fetch ram data
    requestOptions = {
        method: 'POST',
        headers: myHeaders,
        redirect: 'follow'
    };

    fetch(base_url + "admin/system/ram", requestOptions)
        .then(response => response.text())
        .then(result => {
            // returns a response of string type: "Heap Memory Used: 143.9681MB
            // Max: 8012.0MB
            // Non Heap Memory Used: 115.08923MB"
            let rows = result.split("\n");

            let heap_mem = rows[0].split(": ")[1];
            heap_mem = heap_mem.substring(0, heap_mem.length - 2)
            let max_heap_mem = rows[1].split(": ")[1];
            max_heap_mem = max_heap_mem.substring(0, max_heap_mem.length - 2);


            let data = new google.visualization.arrayToDataTable([
                ['Component', 'Status'],
                ['Heap Memory Used', parseFloat(heap_mem)],
                ['Non Heap Memory Used', parseFloat(max_heap_mem)],
            ]);

            let options = {
                'width': 600,
                'height': 350,
                pieHole: 0.35,
            }

            let chart = new google.visualization.PieChart(document.getElementById('health_metrics_table_ram'));
            chart.draw(data, options);
        })
        .catch(error => console.log('error', error));
}

function fetchSystemLogs(){
    let requestOptions = {
        method: 'POST',
        headers: myHeaders,
        redirect: 'follow'
    };

    fetch(base_url + "admin/system/logs", requestOptions)
        .then(response => response.text())
        .then(result => {
            let pre = document.getElementById("system_logs");
            pre.innerHTML = result;
            pre.scrollTop = pre.scrollHeight;
        })
        .catch(error => console.log('error', error));
}

function logout() {
    // delete all cookies and sesison storage
    document.cookie.split(";").forEach(function (c) {
        document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/");
    });
    let requestOptions = {
        method: 'GET',
        redirect: 'follow'
    };

    fetch(base_url+"admin/logout", requestOptions);
    sessionStorage.clear();
    // refresh page for changes to take effect
    document.location.href = base_url+"admin";
}

function kioskLogout(){
    let kioskId = document.getElementById("kiosk_id").value;

    let requestOptions = {
        method: 'POST',
        headers: myHeaders,
        redirect: 'follow'
    };

    fetch(base_url+"admin/kiosk/logout/"+kioskId, requestOptions).then(response => response.text())
        .then(result => {
            if(result === "Kiosk logged out"){
                sendNotification(200, "Kiosk logged out");
            }else if (result === "Kiosk is already logged out"){
                sendNotification(500, "Kiosk is logged out already");
            } else if(result === "Kiosk does not exist"){
                sendNotification(401, "Kiosk does not exist")
            }
        })
        .catch(error => sendNotification(501, "Server is down"));


    document.getElementById("kiosk_id").value = "";
}

function drawCharts() {
    drawProductSalesCharts();
    drawGeoChart();
    drawRecentTransactionsTable();
    drawHealthStatistics();
    fetchSystemLogs();
}

setupTopNumbers();
drawCharts();