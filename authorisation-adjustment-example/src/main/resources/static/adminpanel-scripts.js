// make the form visible
function showForm(id) {
    let element = document.getElementById(id);
    element.removeAttribute("hidden");

    hideOtherForms(id);
}

// hide forms excepted form with given id
function hideOtherForms(id) {
    let elements = document.getElementsByClassName("paymentOperationForm");

    for (let i = 0; i < elements.length; i++) {
        if(elements[i].id != id) {
            elements[i].setAttribute("hidden", "hidden");
        }
    }
}

