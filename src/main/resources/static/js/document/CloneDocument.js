function cloneDocument() {
    $(document).on("click", ".btn-copy", function () {
        let docId = $(this).attr("docId");
        let docName = mvDocuments[docId].name;
        $("#btnConfirmCloneDoc").attr("docId", docId);
        $("#docCloneNameField").val(docName);
        $("#modalCloneDoc").modal();
    })

    $("#btnConfirmCloneDoc").on("click", function () {
        let docId = $(this).attr("docId");
        let newName = $("#docCloneNameField").val().trim();
        if (newName === "") {
            alert("Vui lòng nhập tên tài liệu!")
            return;
        }
        let apiURL = mvHostURLCallApi + "/stg/doc/copy/" + docId;
        $.post(apiURL, {nameCopy : newName}, function (response) {
            if (response.status === "OK") {
                alert("Sao chép thành công!")
                window.location.reload();
            }
        }).fail(function (xhr, textStatus, errorThrown) {
            alert(textStatus + ': ' + JSON.stringify(xhr.responseJSON.message));
        });
    })
}