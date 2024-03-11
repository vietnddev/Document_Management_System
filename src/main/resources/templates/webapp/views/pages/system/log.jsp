<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Nhật ký hệ thống</title>
    <th:block th:replace="header :: stylesheets"></th:block>
</head>

<body class="hold-transition sidebar-mini layout-fixed">
    <div class="wrapper">
        <!-- Navbar (header) -->
        <div th:replace="header :: header"></div>
        <!-- /.navbar (header)-->

        <!-- Main Sidebar Container -->
        <div th:replace="sidebar :: sidebar"></div>

        <!-- Content Wrapper. Contains page content -->
        <div class="content-wrapper" style="padding-top: 10px; padding-bottom: 1px;">
            <!-- Main content -->
            <section class="content">
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-header">
                                    <h3 class="card-title"><strong>NHẬT KÝ HỆ THỐNG</strong></h3>
                                </div>
                                <div class="card-body p-0">
                                    <table class="table table-bordered table-striped">
                                        <thead>
                                            <tr>
                                                <th>STT</th>
                                                <th>Tài khoản</th>
                                                <th>Module</th>
                                                <th>Thao tác</th>
                                                <th>Nội dung</th>
                                                <th>Nội dung cập nhật</th>
                                                <th>Thời gian</th>
                                                <th>IP</th>
                                            </tr>
                                        </thead>
                                        <tbody id="contentTable"></tbody>
                                        <tfoot>
                                            <tr>
                                                <th>STT</th>
                                                <th>Tài khoản</th>
                                                <th>Module</th>
                                                <th>Thao tác</th>
                                                <th>Nội dung</th>
                                                <th>Nội dung cập nhật</th>
                                                <th>Thời gian</th>
                                                <th>IP</th>
                                            </tr>
                                        </tfoot>
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

        <!-- Control Sidebar -->
        <aside class="control-sidebar control-sidebar-dark">
            <!-- Control sidebar content goes here -->
        </aside>

        <div th:replace="header :: scripts"></div>
    </div>
    <script type="text/javascript">
        $(document).ready(function() {
            loadLogs(mvPageSizeDefault, 1);
            updateTableContentWhenOnClickPagination(loadLogs)
        });

        function loadLogs(pageSize, pageNum) {
            let apiURL = mvHostURLCallApi + '/sys/log/all';
            let params = {pageSize: pageSize, pageNum: pageNum}
            $.get(apiURL, params, function (response) {
                if (response.status === "OK") {
                    let data = response.data;
                    let pagination = response.pagination;

                    updatePaginationUI(pagination.pageNum, pagination.pageSize, pagination.totalPage, pagination.totalElements);

                    let contentTable = $('#contentTable');
                    contentTable.empty();
                    $.each(data, function (index, d) {
                        contentTable.append(
                            '<tr>' +
                                '<td>' + (((pageNum - 1) * pageSize + 1) + index) + '</td>' +
                                '<td>' + d.username + '</td>' +
                                '<td>' + d.module + '</td>' +
                                '<td style="max-width: 100px">' + d.action + '</td>' +
                                '<td style="max-width: 250px">' + d.content + '</td>' +
                                '<td style="max-width: 250px">' + d.contentChange + '</td>' +
                                '<td>' + d.createdAt + '</td>' +
                                '<td>' + d.ip + '</td>' +
                            '</tr>'
                        );
                    });
                }
            }).fail(function () {
                showErrorModal("Could not connect to the server");
            });
        }
    </script>
</body>
</html>