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

        <script type="text/javascript" th:src="@{/js/document/LoadDocument.js}"></script>
        <script type="text/javascript" th:src="@{/js/document/CreateUpdateDocument.js}"></script>
        <script type="text/javascript" th:src="@{/js/document/DeleteDocument.js}"></script>
        <script type="text/javascript" th:src="@{/js/document/ShareDocument.js}"></script>
    </div>

    <script type="text/javascript">
        let mvParentId = [[${parentId}]];
        let mvSearchTool = ["BRAND", "PRODUCT_TYPE", "COLOR", "SIZE", "UNIT", "DISCOUNT", "PRODUCT_STATUS", "DOCUMENT_TYPE"];
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

        $(document).ready(function () {
            loadDocuments(mvPageSizeDefault, 1);
            setupSearchTool(mvSearchTool);
            loadFolderTree();
            beforeSubmitShareRights();
            shareDoc();
            createDocument();
            updateDocument();
            submitInsertOrUpdate();
            deleteDocument();
            search();
            //updateTableContentWhenOnClickPagination(loadDocuments, mvPageSize, mvPageNum, mvTotalPage, mvTotalElements);
            updateTableContentWhenOnClickPagination();
        });

        function updateTableContentWhenOnClickPagination() {
            $('#selectPageSize').on('click', function() {
                if (mvPageSize === parseInt($(this).val())) {
                    return;
                }
                mvPageSize = $(this).val();
                loadDocuments($(this).val(), 1);
            });

            $('#firstPage').on('click', function() {
                if (mvPageNum === 1) {
                    return;
                }
                loadDocuments(mvPageSize, 1);
            });

            $('#previousPage').on('click', function() {
                if (mvPageNum === 1) {
                    return;
                }
                loadDocuments(mvPageSize, mvPageNum - 1);
            });

            $('#nextPage').on('click', function() {
                if (mvPageNum === mvTotalPage) {
                    return;
                }
                if (mvTotalElements <= mvPageSize) {
                    return;
                }
                loadDocuments(mvPageSize, mvPageNum + 1);
            });

            $('#lastPage').on('click', function() {
                if (mvPageNum === mvTotalPage) {
                    return;
                }
                if (mvTotalElements <= mvPageSize) {
                    return;
                }
                loadDocuments(mvPageSize, mvTotalPage);
            });
        }

        function search() {
            $("#btnSearch").on("click", function () {
                loadDocuments(mvPageSize, mvPageNum);
            })
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
    </script>
</body>
</html>