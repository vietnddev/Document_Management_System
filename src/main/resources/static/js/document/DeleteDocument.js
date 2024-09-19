function deleteDocument() {
    $(document).on("click", ".btn-delete", function () {
        let document = mvDocuments[$(this).attr("docId")];
        $(this).attr("entityId", document.id);
        $(this).attr("actionType", "delete");
        $(this).attr("entityName", document.name);
        showConfirmModal($(this), "Xóa tài liệu", "Bạn chắc chắn muốn xóa tài liệu: " + document.name);
    });

    $("#btn-multiple-delete").on("click", function () {
        if (mvListOfSelectedDocuments.length === 0) {
            alert("Vui lòng chọn tài liệu cần xóa!");
            return;
        }
        let message = "Bạn muốn xóa các tài liệu sau: ";
        $.each(mvListOfSelectedDocuments, function (index, docId) {
            let docDetail = mvDocuments[docId];
            if (index === mvListOfSelectedDocuments.length - 1) {//at the end of arr
                message += docDetail.name;
            } else {
                message += docDetail.name + ", ";
            }
        })
        $(this).attr("actionType", "multi-delete");
        showConfirmModal($(this), "Xóa tài liệu", message);
    });

    $('#yesButton').on("click", function () {
        let apiURL = mvHostURLCallApi + "/stg/doc/delete/" + parseInt($(this).attr("entityId"));
        if ($(this).attr("actionType") === "multi-delete") {
            apiURL = mvHostURLCallApi + "/stg/doc/multi-delete?ids=" + mvListOfSelectedDocuments;
        }
        callApiDelete(apiURL);
    });
}