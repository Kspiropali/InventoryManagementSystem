function sendNotification(errorCode, message) {
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

function hide_notification() {
    let notification = document.getElementById("notification_box");
    notification.innerHTML = "";
}