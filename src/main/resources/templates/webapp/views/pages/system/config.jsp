<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cấu hình hệ thống</title>
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
                    <!-- Small boxes (Stat box) -->
                    <div class="row">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-header">
                                    <div class="row justify-content-between">
                                        <div class="col-4" style="display: flex; align-items: center">
                                            <h3 class="card-title"><strong>CẤU HÌNH HỆ THỐNG</strong></h3>
                                        </div>
                                        <div class="col-4 text-right">
                                            <button type="button" class="btn btn-success" data-toggle="modal" data-target="#modalRefreshApp">Refresh app</button>
                                        </div>
                                        <div class="modal fade" id="modalRefreshApp">
                                            <div class="modal-dialog">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <strong class="modal-title">Refresh app</strong>
                                                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                                    </div>
                                                    <div class="modal-body">
                                                        1. Refresh category label <br>
                                                        2. ...
                                                    </div>
                                                    <div class="modal-footer justify-content-end">
                                                        <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                                                        <button type="button" class="btn btn-primary" id="btnRefreshApp">Lưu</button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="card-body p-0">
                                    <table class="table table-bordered table-striped">
                                        <thead>
                                            <tr>
                                                <th>STT</th>
                                                <th>Name</th>
                                                <th>Value</th>
                                                <th>Sort</th>
                                                <th>Thao tác</th>
                                            </tr>
                                        </thead>
                                        <tbody id="contentTable"></tbody>
                                    </table>
                                </div>
                                <div class="modal fade" id="modalUpdate">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <strong class="modal-title">Cập nhật config</strong>
                                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                    <span aria-hidden="true">&times;</span>
                                                </button>
                                            </div>
                                            <div class="modal-body">
                                                <div class="row">
                                                    <div class="col-12">
                                                        <div class="form-group">
                                                            <label for="codeField">Code</label>
                                                            <input type="text" class="form-control" placeholder="Code" id="codeField" disabled>
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="nameField">Name</label>
                                                            <input type="text" class="form-control" placeholder="Name" id="nameField" disabled>
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="valueField">Value</label>
                                                            <input type="text" class="form-control" placeholder="Value" id="valueField">
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="sortField">Sort</label>
                                                            <input type="number" class="form-control" placeholder="0" id="sortField">
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="modal-footer justify-content-end">
                                                <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                                                <button type="button" class="btn btn-primary" id="btnUpdateSubmit">Lưu</button>
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

        <!-- Control Sidebar -->
        <aside class="control-sidebar control-sidebar-dark">
            <!-- Control sidebar content goes here -->
        </aside>

        <div th:replace="header :: scripts"></div>
    </div>

    <script type="text/javascript">
        let mvConfigs = [];
        let mvId;
        let mvCode = $("#codeField");
        let mvName = $("#nameField");
        let mvValue = $("#valueField");
        let mvSort = $("#sortField");

        $(document).ready(function() {
            loadConfigs();
            updateConfig();
            refreshApp();
        });

        function refreshApp() {
            $("#btnRefreshApp").on("click", function () {
                let apiURL = mvHostURLCallApi + '/sys/refresh';
                $.get(apiURL, function (response) {
                    if (response.status === "OK") {
                        alert(response.data);
                        window.location.reload();
                    }
                }).fail(function () {
                    showErrorModal("Could not connect to the server");
                });
            })
        }

        function updateConfig() {
            $(document).on("click", ".btn-update", function () {
                let config = mvConfigs[$(this).attr("configId")];
                mvId = config.id;
                mvCode.val(config.code);
                mvName.val(config.name);
                mvValue.val(config.value);
                mvSort.val(config.sort);
                $("#modalUpdate").modal();
            })

            $("#btnUpdateSubmit").on("click", function () {
                let apiURL = mvHostURLCallApi + "/sys/config/update/" + mvId;
                let body = {
                    id : mvId,
                    code : mvCode.val(),
                    name : mvName.val(),
                    value : mvValue.val(),
                    sort : mvSort.val()
                };
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
            })
        }

        function loadConfigs() {
            let apiURL = mvHostURLCallApi + '/sys/config/all';
            $.get(apiURL, function (response) {
                if (response.status === "OK") {
                    let data = response.data;
                    let contentTable = $('#contentTable');
                    contentTable.empty();
                    $.each(data, function (index, d) {
                        mvConfigs[d.id] = d;
                        contentTable.append(`
                            <tr>
                                <td>${index + 1}</td>
                                <td>${d.name}</td>
                                <td>${d.value}</td>
                                <td>${d.sort}</td>
                                <td>
                                    <button type="button" class="btn btn-sm btn-info btn-update" configId="${d.id}"><i class="fa-solid fa-pencil"></i></button>
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