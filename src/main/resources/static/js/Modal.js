function showErrorModal(message) {
    showModalDialog("Error", message);
}

function showModalDialog(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").text(message);
    $("#dialogModal").modal();
}

function showConfirmModal(linkObject, title, text) {
    let entity = linkObject.attr("entity");
    let entityId = linkObject.attr("entityId");//link là 1 đối tượng JQuery -->lấy ra giá trị của thuộc tính entityId
    let entityName = linkObject.attr("entityName");
    let actionType = linkObject.attr("actionType");
    let bgHeader = linkObject.attr("bgHeader");

    $("#yesButton").attr("entity", linkObject.attr("entity"));
    $("#yesButton").attr("entityId", linkObject.attr("entityId"));
    $("#yesButton").attr("entityName", linkObject.attr("entityName"));
    $("#yesButton").attr("actionType", linkObject.attr("actionType"));

    //Title
    if (actionType === 'delete') {
        if (title === null) {
            $("#confirmTitle").text("Xác nhận xóa");
        } else {
            $("#confirmTitle").text(title);
        }
        if (text === null) {
            $("#confirmText").text("Bạn chắc chắn muốn xóa: " + entityName + "?");
        } else {
            $("#confirmText").text(text);
        }
    } else {
        $("#confirmTitle").text(title);
        $("#confirmText").text(text);
    }

    //Modal header
    let modalHeader = $("#confirmModalHeader");
    let defaultClass = "modal-header bg-danger";
    if (bgHeader != null) {
        modalHeader.attr("class", "modal-header " + bgHeader);
    } else {
        modalHeader.attr("class", defaultClass);
    }

    $("#confirmModal").modal();//hiển thị modal
}