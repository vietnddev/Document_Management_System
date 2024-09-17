function createDocument() {
    $("#btnInsertFile").on("click", function () {
        mvDocType.empty();
        loadDocTypeCategory(true, null);
        $("#docTypeBlock").show();
        $("#fileBlock").show();
        $("#btnSubmit").attr("isFolder", "N");
        $("#titleInsertOrUpdate").text("Thêm mới tài liệu");
        $("#headerModalIU").attr("class", "modal-header bg-primary");
        $("#btnSubmit").attr("action", "create");
        $("#nameField").val("");
        $("#desField").val("");
        $("#modalInsertOrUpdate").modal();
    })

    $("#btnInsertFolder").on("click", function () {
        $("#docTypeBlock").hide();
        $("#fileBlock").hide();
        $("#btnSubmit").attr("isFolder", "Y");
        $("#titleInsertOrUpdate").text("Thêm mới tài liệu");
        $("#headerModalIU").attr("class", "modal-header bg-primary");
        $("#btnSubmit").attr("action", "create");
        $("#nameField").val("");
        $("#desField").val("");
        $("#modalInsertOrUpdate").modal();
    })

    $("#btnImportDoc").on("click", function () {
        $("#modalImport").modal();
    })
}

function updateDocument() {
    $(document).on("click", ".btn-update", function (e) {
        e.preventDefault();
        let document = mvDocuments[$(this).attr("docId")];
        if (document.isFolder === "Y") {
            $("#docTypeBlock").hide();
            $("#btnSubmit").attr("isFolder", "Y");
        }
        if (document.isFolder === "N") {
            mvDocType.empty();
            mvDocType.append(`<option value="${document.docTypeId}">${document.docTypeName}</option>`);
            //loadDocTypeCategory(false, document.docTypeId);
            $("#docTypeBlock").show();
            $("#btnSubmit").attr("isFolder", "N");
        }
        mvName.val(document.name);
        mvDes.val(document.description);
        $("#fileBlock").hide();
        $("#titleInsertOrUpdate").text("Cập nhật tài liệu");
        $("#headerModalIU").attr("class", "modal-header bg-warning");
        $("#btnSubmit").attr("action", "update");
        $("#btnSubmit").attr("docId", document.id);
        $("#modalInsertOrUpdate").modal();
    })
}

function submitInsertOrUpdate() {
    $("#formInsertOrUpdate").submit(function (e) {
        e.preventDefault();
        let action = $("#btnSubmit").attr("action");
        if (action === "create") {
            let apiURL = mvHostURLCallApi + "/stg/doc/create";
            let formData = new FormData();
            let isFolder = $("#btnSubmit").attr("isFolder");
            formData.append("parentId", mvParentId);
            formData.append("isFolder", isFolder);
            formData.append("name", mvName.val());
            formData.append("description", mvDes.val());
            if (isFolder === "N") {
                if ($("#fileField").val() === "") {
                    alert("File attach is required!")
                    return;
                }
                if (mvDocType.val() === "-1") {
                    alert("Vui lòng chọn loại tài liệu!")
                    return;
                }
                formData.append("docTypeId", mvDocType.val());
                formData.append("fileUpload", $("#fileField")[0].files[0]); //input có type là file
            }
            $.ajax({
                url: apiURL,
                type: "POST",
                data: formData,
                processData: false,
                contentType : false,
                success: function (response, textStatus, jqXHR) {
                    if (response.status === "OK") {
                        alert("Create successfully")
                        window.location.reload();
                    }
                },
                error: function (xhr, status, error) {
                    alert(status + ': ' + JSON.stringify(xhr.responseJSON.message));
                }
            });
        }
        if (action === "update") {
            let lvDocDetail = mvDocuments[$("#btnSubmit").attr("docId")];
            let apiURL = mvHostURLCallApi + "/stg/doc/update/" + lvDocDetail.id;
            let formData = new FormData();
            let isFolder = lvDocDetail.isFolder;
            formData.append("parentId", lvDocDetail.parentId);
            formData.append("isFolder", isFolder);
            formData.append("name", mvName.val());
            formData.append("description", mvDes.val());
            $.ajax({
                url: apiURL,
                type: "PUT",
                data: formData,
                processData: false,
                contentType : false,
                success: function (response, textStatus, jqXHR) {
                    if (response.status === "OK") {
                        alert("Update successfully");
                        window.location.reload();
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    showErrorModal("Could not connect to the server");
                }
            });
        }
    })
}

function importDoc() {
    $("#formImport").submit(function (e) {
        e.preventDefault();
        if ($("#fileImportField").val() === "") {
            alert("File attach is required!")
            return;
        }
        let apiURL = mvHostURLCallApi + "/stg/doc/import/" + mvParentId;
        let formData = new FormData();
        formData.append("applyRightsParent", false);
        formData.append("fileUpload", $("#fileImportField")[0].files[0]); //input có type là file
        $.ajax({
            url: apiURL,
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function (response, textStatus, jqXHR) {
                if (response.status === "OK") {
                    alert("Import successfully")
                    window.location.reload();
                }
            },
            error: function (xhr, status, error) {
                alert(status + ': ' + JSON.stringify(xhr.responseJSON.message));
            }
        });

    })
}