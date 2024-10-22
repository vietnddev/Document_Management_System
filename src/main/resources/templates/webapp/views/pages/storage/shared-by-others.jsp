<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tài liệu được chia sẽ</title>
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
                                <div class="card-header">
                                    <div class="row justify-content-between">
                                        <div class="col" style="display: flex; align-items: center">
                                            <h3 class="card-title"><strong th:text="${documentParentName}"></strong></h3>
                                        </div>
                                        <div class="col text-right">
                                            <button type="button" class="btn btn-sm btn-primary" id="btnInsertFile">Thêm mới tài liệu</button>
                                            <button type="button" class="btn btn-sm btn-warning" id="btnInsertFolder">Thêm mới thư mục</button>
                                            <button type="button" class="btn btn-sm btn-success" id="btnImportDoc">Import dữ liệu</button>
                                            <button type="button" class="btn btn-sm btn-info"    data-target="#modalExportData" data-toggle="modal">Xuất dữ liệu</button>
                                        </div>
                                    </div>
                                </div>
                                <div class="card-body align-items-center p-0">
                                    <table class="table table-bordered table-striped align-items-center">
                                        <thead class="align-self-center">
                                            <tr class="align-self-center">
                                                <th>STT</th>
                                                <th><input type="checkbox" style="width: 25px; height: 25px" id="tickAllCbx"></th>
                                                <th></th>
                                                <th>Thời gian</th>
                                                <th>Tên</th>
                                                <th>Loại tài liệu</th>
                                                <th>Mô tả</th>
                                                <th>
                                                    <div class="row justify-content-between">
                                                        <span class="col" style="display: flex; align-items: center">Thao tác</span>
                                                        <button class="col btn btn-danger btn-sm mr-1" title="Xóa nhiều tài liệu" id="btn-multiple-delete"><i class="fa-solid fa-trash"></i></button>
                                                        <button class="col btn btn-success btn-sm" title="Di chuyển nhiều tài liệu" id="btn-multiple-move"> <i class="fa-solid fa-up-down-left-right"></i> </button>
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
                                <div th:replace="pages/storage/fragments/document-fragments :: modalInsertAndUpdateFragment"></div>

                                <!-- Modal import -->
                                <div th:replace="pages/storage/fragments/document-fragments :: modalImportFragment"></div>

                                <!-- Modal share role -->
                                <div th:replace="pages/storage/fragments/document-fragments :: modalShareFragment"></div>

                                <!-- Modal confirm export data -->
                                <div th:replace="pages/storage/fragments/document-fragments :: modalExportDataFragment"></div>

                                <!-- Modal confirm clone document -->
                                <div th:replace="pages/storage/fragments/document-fragments :: modalCloneDocFragment"></div>

                                <!-- Modal confirm clone document -->
                                <div th:replace="pages/storage/fragments/document-fragments :: modalMoveDocFragment"></div>
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
        <script type="text/javascript" th:src="@{/js/document/CreateUpdateDocument.js}"></script>
        <script type="text/javascript" th:src="@{/js/document/DeleteDocument.js}"></script>
        <script type="text/javascript" th:src="@{/js/document/ShareDocument.js}"></script>
        <script type="text/javascript" th:src="@{/js/document/CloneDocument.js}"></script>
        <script type="text/javascript" th:src="@{/js/document/MoveDocument.js}"></script>
    </div>

    <script type="text/javascript">
        let mvParentId = [[${parentId}]];
        let mvSearchTool = ["DOCUMENT_TYPE"];
        let mvDocuments = [];
        let mvDocType = $("#docTypeField");
        let mvName = $("#nameField");
        let mvDes = $("#desField");
        let mvAccountShares = [];
        let mvPageSize = mvPageSizeDefault;
        let mvPageNum = 1;
        let mvTotalPage = 1;
        let mvTotalElements = 1;
        let mvPagination;
        let mvListOfSelectedDocuments = [];

        $(document).ready(function () {
            init();
            loadDocumentss(mvPageSizeDefault, 1);
            setupSearchTool(mvSearchTool);
            loadFolderTreeOnSideBar();
            loadFolderTreeOnMoveModal();
            beforeSubmitShareRights();
            shareDoc();
            createDocument();
            updateDocument();
            importDoc();
            submitInsertOrUpdate();
            deleteDocument();
            cloneDocument();
            moveDocument();
            search();
            updateTableContentWhenOnClickPagination(loadDocumentss);
            exportData();
            downloadDocument();
        });

        function search() {
            $("#btnSearch").on("click", function () {
                loadDocumentss(mvPageSize, mvPageNum);
            })
        }

        function loadDocTypeCategory(defaultValue, idNotIn) {
            let apiURL = mvHostURLCallApi + "/category/document-type";
            if (idNotIn != null) {
                apiURL = apiURL + "?idNotIn=" + idNotIn;
            }
            $.get(apiURL, function (response) {
                if (defaultValue) {
                    mvDocType.append(`<option value='-1'>Chọn loại tài liệu</option>`);
                }
                if (response.status === "OK") {
                    $.each(response.data, function (index, d) {
                        mvDocType.append('<option value=' + d.id + '>' + d.name + '</option>');
                    });
                }
            }).fail(function (xhr) {
                showErrorModal($.parseJSON(xhr.responseText).message);
            });
        }

        function exportData() {
            $("#btnConfirmExportData").on("click", function () {
                let apiURL = mvHostURLCallApi + "/stg/doc/export/excel";
                callApiExportData(apiURL);
                $("#modalExportData").modal('hide');
            })
        }

        function downloadDocument() {
            $(document).on("click", ".btn-download", function () {
                let apiURL = mvHostURLCallApi + "/stg/doc/download/" + $(this).attr("docId");
                callApiExportData(apiURL);
            })
        }

        function loadDocumentss(pageSize, pageNum) {
            let contentTable = $('#contentTable');
            contentTable.empty();
            let apiURL = mvHostURLCallApi + "/stg/doc/shared-by-others";
            let params = {
                pageSize: pageSize,
                pageNum: pageNum,
                parentId: mvParentId,
                txtSearch: $("#txtFilter").val()
            }
            $.get(apiURL, params, function (response) {
                if (response.status === "OK") {
                    let data = response.data;
                    mvPagination = response.pagination;
                    mvPageNum = parseInt(mvPagination.pageNum);
                    mvPageSize = parseInt(mvPagination.pageSize);
                    mvTotalPage = parseInt(mvPagination.totalPage);
                    mvTotalElements = parseInt(mvPagination.totalElements);

                    updatePaginationUI(mvPagination.pageNum, mvPagination.pageSize, mvPagination.totalPage, mvPagination.totalElements);

                    $.each(data, function (index, d) {
                        mvDocuments[d.id] = d;
                        let iconDoc = d.isFolder === "Y" ? "/dist/icon/folder.png" : "/dist/icon/pdf.png";
                        let btnMove = d.thisAccCanMove ? `<button class="btn btn-success btn-sm btn-move" docId="${d.id}" title="Di chuyển"> <i class="fa-solid fa-up-down-left-right"></i> </button>` : ``;
                        let btnUpdate = d.thisAccCanUpdate ? `<button class="btn btn-warning btn-sm btn-update" docId="${d.id}" title="Cập nhật"> <i class="fa-solid fa-pencil"></i> </button>` : ``;
                        let btnShare = d.thisAccCanShare ? `<button class="btn btn-info btn-sm btn-share" docId="${d.id}" title="Chia sẽ"> <i class="fa-solid fa-share"></i> </button>` : ``;
                        let btnDelete = d.thisAccCanDelete ? `<button class="btn btn-danger btn-sm btn-delete" docId="${d.id}" title="Xóa"> <i class="fa-solid fa-trash"></i> </button>` : ``;
                        contentTable.append(`
                    <tr>
                        <td>${(((pageNum - 1) * pageSize + 1) + index)}</td>
                        <td><input type="checkbox" style="width: 25px; height: 25px" name="tickCbx" docId="${d.id}"></td>
                        <td><img src="${iconDoc}"></td>
                        <td>${d.createdAt}</td>
                        <td style="max-width: 300px"><a href="/stg/doc/${d.asName}-${d.id}">${d.name}</a></td>
                        <td>${d.docTypeName}</td>
                        <td>${d.description}</td>
                        <td>
                            <button class="btn btn-secondary btn-sm btn-copy" docId="${d.id}" title="Sao chép"> <i class="fa-solid fa-copy"></i> </button>
                            <button class="btn btn-primary btn-sm btn-download" docId="${d.id}" title="Tải về"> <i class="fa-solid fa-download"></i> </button>
                            ${btnMove}
                            ${btnUpdate}
                            ${btnShare}
                            ${btnDelete}
                        </td>
                    </tr>
                `);
                    });
                }
            }).fail(function () {
                showErrorModal("Could not connect to the server");
            });
        }
    </script>
</body>
</html>