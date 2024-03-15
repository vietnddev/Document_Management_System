<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Kho tài liệu</title>
    <th:block th:replace="header :: stylesheets"></th:block>
    <style rel="stylesheet">
        .table td, th {
            vertical-align: middle;
        }
    </style>
</head>

<body class="hold-transition sidebar-mini layout-fixed">
    <div class="wrapper">
        <!-- Navbar (header) -->
        <div th:replace="header :: header"></div>
        <!-- /.navbar (header)-->

        <!-- Folder tree -->
        <div th:replace="fragments :: folderTree"></div>

        <!-- Content Wrapper. Contains page content -->
        <div class="content-wrapper" style="padding-top: 10px; padding-bottom: 1px;">
            <section class="content">
                <div class="container-fluid">
                    <!--Breadcrumb-->
                    <div th:replace="fragments :: breadcrumb"></div>

                    <div class="row">
                        <div class="col-12">
                            <!--Search tool-->
                            <div th:replace="fragments :: searchTool('Y','Y','Y','Y','Y','Y','Y')" id="searchTool"></div>

                            <div class="card">
                                <div class="card-header">
                                    <div class="row justify-content-between">
                                        <div class="col-8" style="display: flex; align-items: center">
                                            <h3 class="card-title"><strong th:text="${documentParentName}"></strong></h3>
                                        </div>
                                        <div class="col-4 text-right">
                                            <button type="button" class="btn btn-primary" id="btnInsertFile">Thêm mới tài liệu</button>
                                            <button type="button" class="btn btn-warning" id="btnInsertFolder">Thêm mới thư mục</button>
                                        </div>
                                    </div>
                                </div>
                                <div class="card-body align-items-center p-0">
                                    <table class="table table-bordered table-striped align-items-center">
                                        <thead class="align-self-center">
                                            <tr class="align-self-center">
                                                <th>STT</th>
                                                <th></th>
                                                <th>Thời gian</th>
                                                <th>Tên</th>
                                                <th>Loại tài liệu</th>
                                                <th>Mô tả</th>
                                                <th>Thao tác</th>
                                            </tr>
                                        </thead>
                                        <tbody id="contentTable"></tbody>
                                    </table>
                                </div>
                                <div class="card-footer">
                                    <div th:replace="fragments :: pagination"></div>
                                </div>

                                <!-- Modal insert and update -->
                                <div class="modal fade" id="modalInsertOrUpdate">
                                    <div class="modal-dialog modal-lg">
                                        <form id="formInsertOrUpdate" enctype="multipart/form-data">
                                            <div class="modal-content">
                                                <div id="headerModalIU">
                                                    <strong class="modal-title" id="titleInsertOrUpdate"></strong>
                                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                        <span aria-hidden="true">&times;</span>
                                                    </button>
                                                </div>
                                                <div class="modal-body">
                                                    <div class="row">
                                                        <div class="col-12">
                                                            <div class="form-group" id="docTypeBlock">
                                                                <label for="docTypeField">Loại tài liệu</label>
                                                                <select class="custom-select" id="docTypeField"></select>
                                                            </div>
                                                            <div class="form-group">
                                                                <label for="nameField">Tên</label>
                                                                <input class="form-control" type="text" placeholder="Tên loại tài liệu" id="nameField" maxlength="200"/>
                                                            </div>
                                                            <div class="form-group">
                                                                <label for="desField">Mô tả</label>
                                                                <textarea class="form-control" rows="5" placeholder="Mô tả" id="desField"></textarea>
                                                            </div>
                                                            <div class="form-group" id="fileBlock">
                                                                <label for="fileField">File</label>
                                                                <input class="form-control" type="file" id="fileField"/>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="modal-footer justify-content-end">
                                                    <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                                                    <button type="submit" class="btn btn-primary" id="btnSubmit">Lưu</button>
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                </div>

                                <!-- Modal share role -->
                                <div class="modal fade" id="modalShare">
                                    <div class="modal-dialog modal-lg">
                                        <div class="modal-content">
                                            <div class="modal-header bg-info">
                                                <strong class="modal-title">Phân quyền tài liệu</strong>
                                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                    <span aria-hidden="true">&times;</span>
                                                </button>
                                            </div>
                                            <div class="modal-body">
                                                <div class="card mb-0">
                                                    <div class="card-body table-responsive p-0">
                                                        <table class="table table-hover text-nowrap">
                                                            <thead>
                                                                <tr>
                                                                    <th>Account name</th>
                                                                    <th class="text-center">Read</th>
                                                                    <th class="text-center">Update</th>
                                                                    <th class="text-center">Delete</th>
                                                                    <th class="text-center">Move</th>
                                                                    <th class="text-center">Share</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody id="tblSysAccountShare"></tbody>
                                                        </table>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="modal-footer justify-content-end">
                                                <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                                                <button type="submit" class="btn btn-primary" id="btnSubmitShare">Lưu</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </div>

        <div th:replace="modal_fragments :: confirm_modal"></div>

        <div th:replace="footer :: footer"></div>

        <aside class="control-sidebar control-sidebar-dark"></aside>

        <div th:replace="header :: scripts"></div>
    </div>

    <script type="text/javascript">
        let mvParentId = [[${parentId}]];
        let mvSearchTool = ["BRAND", "PRODUCT_TYPE", "COLOR", "SIZE", "UNIT", "DISCOUNT", "PRODUCT_STATUS", "DOCUMENT_TYPE"];
        let mvDocuments = [];
        let mvDocType = $("#docTypeField");
        let mvName = $("#nameField");
        let mvDes = $("#desField");

        $(document).ready(function () {
            init();
            createDocument();
            loadFolderTree();
            shareDoc();
            updateDocument();
            deleteDocument();
        });

        function init() {
            loadDocuments(mvPageSizeDefault, 1);
            updateTableContentWhenOnClickPagination(loadDocuments);
            setupSearchTool(mvSearchTool);
        }

        function loadDocuments(pageSize, pageNum) {
            let apiURL = mvHostURLCallApi + "/stg/doc/all";
            let params = {
                pageSize: pageSize,
                pageNum: pageNum,
                parentId: mvParentId
            }
            $.get(apiURL, params, function (response) {
                if (response.status === "OK") {
                    let data = response.data;
                    let pagination = response.pagination;

                    updatePaginationUI(pagination.pageNum, pagination.pageSize, pagination.totalPage, pagination.totalElements);

                    let contentTable = $('#contentTable');
                    contentTable.empty();
                    $.each(data, function (index, d) {
                        mvDocuments[d.id] = d;
                        let iconDoc = "/dist/icon/pdf.png";
                        if (d.isFolder === "Y") {
                            iconDoc = "/dist/icon/folder.png";
                        }
                        contentTable.append(`
                            <tr>
                                <td>${(((pageNum - 1) * pageSize + 1) + index)}</td>
                                <td><img src="${iconDoc}"></td>
                                <td>${d.createdAt}</td>
                                <td style="max-width: 300px">
                                    <a href="/stg/doc/${d.asName}-${d.id}">${d.name}</a>
                                </td>
                                <td>${d.docTypeName}</td>
                                <td>${d.description}</td>
                                <td>
                                    <button class="btn btn-warning btn-sm btn-update" docId="${d.id}"> <i class="fa-solid fa-pencil"></i> </button>
                                    <button class="btn btn-info btn-sm btn-share" docId="${d.id}"> <i class="fa-solid fa-share"></i> </button>
                                    <button class="btn btn-danger btn-sm btn-delete" docId="${d.id}"> <i class="fa-solid fa-trash"></i> </button>
                                </td>
                            </tr>
                        `);
                    });
                }
            }).fail(function () {
                showErrorModal("Could not connect to the server");
            });
        }

        function loadDocTypeCategory(defaultValue, idNotIn) {
            let apiURL = mvHostURLCallApi + "/category/document-type";
            if (idNotIn != null) {
                apiURL = apiURL + "?idNotIn=" + idNotIn;
            }
            $.get(apiURL, function (response) {
                if (defaultValue) {
                    mvDocType.append('<option>Chọn loại tài liệu</option>');
                }
                if (response.status === "OK") {
                    $.each(response.data, function (index, d) {
                        mvDocType.append('<option value=' + d.id + '>' + d.name + '</option>');
                    });
                }
            }).fail(function () {
                showErrorModal("Could not connect to the server");
            });
        }

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
                $("#modalInsertOrUpdate").modal();
            })

            $("#btnInsertFolder").on("click", function () {
                $("#docTypeBlock").hide();
                $("#fileBlock").hide();
                $("#btnSubmit").attr("isFolder", "Y");
                $("#titleInsertOrUpdate").text("Thêm mới tài liệu");
                $("#headerModalIU").attr("class", "modal-header bg-primary");
                $("#btnSubmit").attr("action", "create");
                $("#modalInsertOrUpdate").modal();
            })

            submitInsertOrUpdate();
        }

        function shareDoc() {
            let mvAccountShares = [];
            $(document).on("click", ".btn-share", function () {
                let documentId = parseInt($(this).attr("docId"));
                let apiURL = mvHostURLCallApi + '/stg/doc/share/' + documentId;
                $.get(apiURL, function (response) {
                    if (response.status === "OK") {
                        mvAccountShares = response.data;
                        let tableShare = $("#tblSysAccountShare");
                        tableShare.empty();
                        $.each(mvAccountShares, function (index, d) {
                            tableShare.append(`
                                <tr>
                                    <td>${d.accountName}</td>
                                    <td><input class="form-control form-control-sm" type="checkbox" id="canReadCbx_${d.accountId}" ${d.canRead ? "checked" : ""}></td>
                                    <td><input class="form-control form-control-sm" type="checkbox" id="canUpdateCbx_${d.accountId}" ${d.canUpdate ? "checked" : ""}></td>
                                    <td><input class="form-control form-control-sm" type="checkbox" id="canDeleteCbx_${d.accountId}" ${d.canDelete ? "checked" : ""}></td>
                                    <td><input class="form-control form-control-sm" type="checkbox" id="canMoveCbx_${d.accountId}" ${d.canMove ? "checked" : ""}></td>
                                    <td><input class="form-control form-control-sm" type="checkbox" id="canShareCbx_${d.accountId}" ${d.canShare ? "checked" : ""}></td>
                                </tr>
                            `);
                        })
                    }
                }).fail(function () {
                    showErrorModal("Could not connect to the server");
                });
                $("#btnSubmitShare").attr("documentId", documentId);
                $("#modalShare").modal();
            })

            $("#btnSubmitShare").on("click", function () {
                let documentId = parseInt($(this).attr("documentId"));

                $.each(mvAccountShares, function (index, d) {
                    mvAccountShares[index].canRead = $("[id^='canReadCbx_']").eq(index).prop("checked");
                    mvAccountShares[index].canUpdate = $("[id^='canUpdateCbx_']").eq(index).prop("checked");
                    mvAccountShares[index].canDelete = $("[id^='canDeleteCbx_']").eq(index).prop("checked");
                    mvAccountShares[index].canMove = $("[id^='canMoveCbx_']").eq(index).prop("checked");
                    mvAccountShares[index].canShare = $("[id^='canShareCbx_']").eq(index).prop("checked");
                })

                $.ajax({
                    url: mvHostURLCallApi + "/stg/doc/share/" + documentId,
                    type: "PUT",
                    contentType: "application/json",
                    data: JSON.stringify(mvAccountShares),
                    success: function(response) {
                        if (response.status === "OK") {
                            alert("Update successfully!");
                            window.location.reload();
                        }
                    },
                    error: function (xhr) {
                        alert("Error: " + $.parseJSON(xhr.responseText).message);
                    }
                });
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
                    loadDocTypeCategory(false, document.docTypeId);
                    $("#docTypeBlock").show();
                    $("#btnSubmit").attr("isFolder", "N");
                }
                mvName.val(document.name);
                mvDes.val(document.description);
                $("#fileBlock").hide();
                $("#titleInsertOrUpdate").text("Cập nhật tài liệu");
                $("#headerModalIU").attr("class", "modal-header bg-warning");
                $("#btnSubmit").attr("action", "update");
                $("#modalInsertOrUpdate").modal();
                submitInsertOrUpdate();
            })
        }

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
                        error: function (jqXHR, textStatus, errorThrown) {
                            showErrorModal("Could not connect to the server");
                        }
                    });
                }
                if (action === "update") {
                    let apiURL = mvHostURLCallApi + "/stg/doc/update/" + document.id;
                    let formData = new FormData();
                    let isFolder = document.isFolder;
                    formData.append("parentId", document.parentId);
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
                                alert("Update successfully")
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
    </script>
</body>
</html>