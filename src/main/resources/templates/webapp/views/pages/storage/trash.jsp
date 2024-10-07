<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Thùng rác</title>
    <th:block th:replace="header :: stylesheets"></th:block>
    <link rel="stylesheet" th:href="@{/css/Document.css}">
</head>

<body class="hold-transition sidebar-mini layout-fixed">
    <div class="wrapper">
        <div th:replace="header :: header"></div>
        
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
                            <div th:replace="fragments :: searchTool('Y')" id="searchTool"></div>

                            <div class="card">
                                <div class="card-body align-items-center p-0">
                                    <table class="table table-bordered table-striped align-items-center">
                                        <thead class="align-self-center">
                                            <tr class="align-self-center">
                                                <th>STT</th>
                                                <th><input type="checkbox" style="width: 25px; height: 25px" id="tickAllCbx"></th>
                                                <th></th>
                                                <th>Ngày chuyển vào</th>
                                                <th>Tên</th>
                                                <th>Loại tài liệu</th>
                                                <th>
                                                    <div class="row justify-content-between">
                                                        <span class="col" style="display: flex; align-items: center">Thao tác</span>
                                                        <button class="col btn btn-secondary btn-sm mr-1" title="Khôi phục" id="btn-multi-restore"><i class="fa-solid fa-clock-rotate-left"></i></button>
                                                        <button class="col btn btn-danger btn-sm" title="Xóa vĩnh viễn" id="btn-multi-delete"><i class="fa-solid fa-trash"></i> </button>
                                                    </div>
                                                </th>
                                            </tr>
                                        </thead>
                                        <tbody id="contentTable"></tbody>
                                    </table>
                                </div>
                                <div class="card-footer">
                                    <div th:replace="fragments :: pagination"></div>
                                </div>

                                <!-- Modal insert and update -->
                                <!-- <div th:replace="pages/storage/fragments/document-fragments :: modalInsertAndUpdateFragment"></div> -->
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </div>

        <div th:replace="modal_fragments :: confirm_modal"></div>

        <aside class="control-sidebar control-sidebar-dark"></aside>

        <div th:replace="header :: scripts"></div>

        <script type="text/javascript" th:src="@{/js/document/Document.js}"></script>
    </div>

    <script type="text/javascript">
        let mvDocuments = [];
        let mvListOfSelectedDocuments = [];

        $(document).ready(function () {
            init();
            loadDocuments_(mvPageSizeDefault, 1);
            updateTableContentWhenOnClickPagination(loadDocuments_);

            restoreDoc();
            deleteDoc();
            confirmPress();
        });

        function restoreDoc() {
            $(document).on("click", ".btn-restore", function () {
                let docDetail = mvDocuments[$(this).attr("docId")];
                let message = "Bạn muốn khôi phục tài liệu " + docDetail.name;
                $(this).attr("entityId", docDetail.id);
                $(this).attr("actionType", "single-restore");
                $(this).attr("bgHeader", "bg-secondary");
                showConfirmModal($(this), "Khôi phục tài liệu", message);
            })

            $("#btn-multi-restore").on("click", function () {
                if (mvListOfSelectedDocuments.length === 0) {
                    alert("Vui lòng chọn tài liệu cần khôi phục!");
                    return;
                }
                let message = "Bạn muốn khôi phục các tài liệu sau: ";
                $.each(mvListOfSelectedDocuments, function (index, docId) {
                    let docDetail = mvDocuments[docId];
                    if (index === mvListOfSelectedDocuments.length - 1) {//at the end of arr
                        message += docDetail.name;
                    } else {
                        message += docDetail.name + ", ";
                    }
                })
                $(this).attr("actionType", "multi-restore");
                $(this).attr("bgHeader", "bg-secondary");
                showConfirmModal($(this), "Khôi phục tài liệu", message);
            })
        }

        function deleteDoc() {
            $(document).on("click", ".btn-delete", function () {
                let document = mvDocuments[$(this).attr("docId")];
                $(this).attr("entityId", document.id);
                $(this).attr("actionType", "single-delete");
                $(this).attr("entityName", document.name);
                showConfirmModal($(this), "Xóa tài liệu", "Bạn chắc chắn muốn xóa tài liệu: " + document.name);
            });

            $("#btn-multi-delete").on("click", function () {
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
        }

        function confirmPress() {
            $('#yesButton').on("click", function () {
                let actionType = $(this).attr("actionType");
                if (actionType === "multi-restore") {
                    multiRestore();
                }
                if (actionType === "single-restore") {
                    let documentId = $(this).attr("entityId");
                    restore(documentId);
                }
                if (actionType === "single-delete") {
                    let documentId = $(this).attr("entityId");
                    let apiURL = mvHostURLCallApi + "/stg/doc/delete/" + parseInt(documentId) + "?forceDelete=true";
                    callApiDelete(apiURL);
                }
                if (actionType === "multi-delete") {
                    let apiURL = mvHostURLCallApi + "/stg/doc/multi-delete?ids=" + mvListOfSelectedDocuments + "&forceDelete=true";
                    callApiDelete(apiURL);
                }
            });
        }

        function restore(documentId) {
            let apiURL = mvHostURLCallApi + "/stg/doc/trash/restore/" + documentId;
            $.ajax({
                url: apiURL,
                type: 'PUT',
                success: function(response) {
                    if (response.status === "OK") {
                        alert(response.message);
                        window.location.reload();
                    }
                },
                error: function(xhr, status, error) {
                    alert("Error: " + $.parseJSON(xhr.responseText).message);
                }
            })
        }

        function multiRestore() {
            let apiURL = mvHostURLCallApi + "/stg/doc/trash/multi-restore"
            let body = {
                selectedDocuments: mvListOfSelectedDocuments
            }
            $.ajax({
                url: apiURL,
                type: 'PUT',
                contentType: "application/json",
                data: JSON.stringify(body),
                success: function(response) {
                    if (response.status === "OK") {
                        alert(response.message);
                        window.location.reload();
                    }
                },
                error: function(xhr, status, error) {
                    alert("Error: " + $.parseJSON(xhr.responseText).message);
                }
            })
        }

        function loadDocuments_(pageSize, pageNum) {
            let apiURL = mvHostURLCallApi + "/stg/doc/trash";
            let params = {
                pageSize: pageSize,
                pageNum: pageNum
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
                        let iconDoc = d.isFolder === "Y" ? "/dist/icon/folder.png" : "/dist/icon/pdf.png";
                        contentTable.append(`
                            <tr>
                                <td>${(((pageNum - 1) * pageSize + 1) + index)}</td>
                                <td><input type="checkbox" style="width: 25px; height: 25px" name="tickCbx" docId="${d.id}"></td>
                                <td><img src="${iconDoc}"></td>
                                <td>${d.createdAt}</td>
                                <td style="max-width: 300px">${d.name}</td>
                                <td></td>
                                <td>
                                    <button class="btn btn-secondary btn-sm btn-restore" docId="${d.id}" title="Khôi phục"> <i class="fa-solid fa-clock-rotate-left"></i> </button>
                                    <button class="btn btn-danger btn-sm btn-delete" docId="${d.id}" title="Xóa vĩnh viễn"> <i class="fa-solid fa-trash"></i> </button>
                                </td>
                            </tr>
                        `);
                    });
                }
            }).fail(function (xhr) {
                showErrorModal($.parseJSON(xhr.responseText).message);
            });
        }
    </script>
</body>
</html>