<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Bộ nhớ</title>
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
                                            <tr>
                                                <td colspan="4">Các tệp sử dụng bộ nhớ:</td>
                                            </tr>
                                            <tr class="align-self-center">
                                                <th>STT</th>
                                                <th></th>
                                                <th>Tên tệp</th>
                                                <th>Bộ nhớ đã dùng
                                                    <i class="fa-solid fa-sort ml-1" id="sortBySize" style="cursor: pointer"></i>
                                                </th>
                                            </tr>
                                        </thead>
                                        <tbody id="contentTable"></tbody>
                                    </table>
                                </div>
                                <div class="card-footer">
                                    <div th:replace="fragments :: pagination"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </div>

        <div th:replace="modal_fragments :: confirm_modal"></div>

        <aside class="control-sidebar control-sidebar-dark"></aside>

        <div th:replace="header :: scripts"></div>
    </div>

    <script type="text/javascript">
        let mvDocuments = [];
        let mvListOfSelectedDocuments = [];
        let mvSortBy;
        let mvSortMode = "desc";

        $(document).ready(function () {
            init();
            loadDocuments(mvPageSizeDefault, 1);
            updateTableContentWhenOnClickPagination(loadDocuments);
        });

        function init() {
            sort();
        }

        function sort() {
            $("#sortBySize").on("click", function () {
                mvSortBy = "fileSize";
                if (mvSortMode === "desc") {
                    mvSortMode = "asc";
                } else {
                    mvSortMode = "desc";
                }
                loadDocuments(getPageSize(), getPageNum());
            })
        }

        function loadDocuments(pageSize, pageNum) {
            let apiURL = mvHostURLCallApi + "/stg/doc/quota";
            let params = {
                pageSize: pageSize,
                pageNum: pageNum,
                sortBy: mvSortBy,
                sort: mvSortMode
            }
            $.get(apiURL, params, function (response) {
                if (response.status === "OK") {
                    let data = response.data;
                    let pagination = response.pagination;

                    updatePaginationUI(pagination.pageNum, pagination.pageSize, pagination.totalPage, pagination.totalElements);

                    let contentTable = $('#contentTable');
                    contentTable.empty();
                    $.each(data.documents, function (index, d) {
                        mvDocuments[d.id] = d;
                        let iconDoc = "/dist/icon/pdf.png";
                        contentTable.append(`
                            <tr>
                                <td>${(((pageNum - 1) * pageSize + 1) + index)}</td>
                                <td><img src="${iconDoc}"></td>
                                <td style="max-width: 300px">${d.name}</td>
                                <td>${d.memoryUsed}</td>
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