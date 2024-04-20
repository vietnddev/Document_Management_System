function deleteDocument() {
    $(document).on("click", ".btn-delete", function () {
        let document = mvDocuments[$(this).attr("docId")];
        $(this).attr("entityId", document.id);
        $(this).attr("actionType", "delete");
        $(this).attr("entityName", document.name);
        showConfirmModal($(this), "Xóa tài liệu", "Bạn chắc chắn muốn xóa tài liệu: " + document.name);
    });

    $('#yesButton').on("click", function () {
        let apiURL = mvHostURLCallApi + "/stg/doc/delete/" + parseInt($(this).attr("entityId"));
        callApiDelete(apiURL);
    });
}