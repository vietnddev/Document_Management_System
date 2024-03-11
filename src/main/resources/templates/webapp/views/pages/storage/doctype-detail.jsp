<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cấu hình loại tài liệu</title>
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

        <!-- Main Sidebar Container -->
        <div th:replace="sidebar :: sidebar"></div>

        <!-- Content Wrapper. Contains page content -->
        <div class="content-wrapper" style="padding-top: 10px; padding-bottom: 1px;">
            <section class="content">
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-header">
                                    <div class="row justify-content-between">
                                        <div class="col-4" style="display: flex; align-items: center">
                                            <h3 class="card-title"><strong th:text="${nameDocType}"></strong></h3>
                                        </div>
                                        <div class="col-4 text-right">
                                            <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#insert">Thêm mới</button>
                                        </div>
                                    </div>
                                </div>
                                <!-- /.card-header -->
                                <div class="card-body align-items-center p-0">
                                    <table class="table table-bordered table-striped align-items-center">
                                        <thead class="align-self-center">
                                        <tr class="align-self-center">
                                            <th>Id</th>
                                            <th>Tên field</th>
                                            <th>Kiểu nhập</th>
                                            <th>Min length</th>
                                            <th>Max length</th>
                                            <th>Min number</th>
                                            <th>Max number</th>
                                            <th>Bắt buộc</th>
                                            <th>Sắp xếp</th>
                                            <th>Trạng thái</th>
                                            <th>Thao tác</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                            <tr th:each="list : ${docFields}">
                                                <form th:action="@{/stg/doc/doc-field/update/{id}(id=${list.id})}" method="POST" th:object="${docField}">
                                                    <!--Tên field-->
                                                    <td th:text="${list.id}"></td>
                                                    <td>
                                                        <input type="text" class="form-control" placeholder="Tên field" name="name" required th:value="${list.name}"/>
                                                    </td>
                                                    <!--Kiểu nhập-->
                                                    <td>
                                                        <select class="custom-select" name="type">
                                                            <option th:text="${list.type}" selected></option>
                                                        </select>
                                                    </td>
                                                    <!--Min length-->
                                                    <td>
                                                        <input type="text" class="form-control" name="minLength" required style="max-width: 80px" th:value="${list.minLength}"/>
                                                    </td>
                                                    <!--Max length-->
                                                    <td>
                                                        <input type="text" class="form-control" name="maxLength" required style="max-width: 80px" th:value="${list.maxLength}"/>
                                                    </td>
                                                    <!--Min number-->
                                                    <td>
                                                        <input type="text" class="form-control" name="minNumber" required style="max-width: 80px" th:value="${list.minNumber}"/>
                                                    </td>
                                                    <!--Max number-->
                                                    <td>
                                                        <input type="text" class="form-control" name="maxNumber" required style="max-width: 80px" th:value="${list.maxNumber}"/>
                                                    </td>
                                                    <!--Checkbox bắt buộc nhập-->
                                                    <td class="text-center">
                                                        <div class="form-group clearfix"
                                                             style="margin: 0 auto">
                                                            <div class="icheck-danger" th:if="${list.required}">
                                                                <input type="checkbox" th:id="'checked_' + ${list.id}" name="required" checked>
                                                                <label th:for="'checked_'+${list.id}"></label>
                                                            </div>
                                                            <div class="icheck-danger" th:if="!${list.required}">
                                                                <input type="checkbox" th:id="'unchecked_' + ${list.id}" name="required">
                                                                <label th:for="'unchecked_'+${list.id}"></label>
                                                            </div>
                                                        </div>
                                                    </td>
                                                    <!--Sắp xếp thứ tự hiển thị-->
                                                    <td>
                                                        <input type="text" class="form-control" name="sort" required style="max-width: 42px" th:value="${list.sort}"/>
                                                    </td>
                                                    <!--Trạng thái field-->
                                                    <td>
                                                        <th:block th:if="${list.status}">Sử dụng</th:block>
                                                        <th:block th:if="!${list.status}">Không sử dụng</th:block>
                                                    </td>

                                                    <!--Button thao tác-->
                                                    <td class="text-center">
                                                        <input type="hidden" name="docType" th:value="${list.docType.id}">
                                                        <input type="hidden" name="status" th:value="${list.status}">

                                                        <!--Button SAVE-->
                                                        <button class="btn btn-success btn-sm" type="submit" name="update">
                                                            <i class="fa-solid fa-check"></i>
                                                        </button>
                                                        <!--Button LOCK-->
                                                        <button class="btn btn-warning btn-sm" data-toggle="modal" th:data-target="'#update-' + ${list.id}">
                                                            <i class="fa-solid fa-lock"></i>
                                                        </button>
                                                        <!--Button UNLOCK-->
                                                        <button class="btn btn-info btn-sm" data-toggle="modal" th:data-target="'#update-' + ${list.id}">
                                                            <i class="fa-solid fa-unlock"></i>
                                                        </button>
                                                        <!--Button DELETE-->
                                                        <button class="btn btn-danger btn-sm btn-delete" type="button" th:id="${list.id}" th:name="${list.name}">
                                                            <i class="fa-solid fa-trash"></i>
                                                        </button>
                                                    </td>
                                                </form>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>

                        <!-- modal insert -->
                        <div class="modal fade" id="insert">
                            <div class="modal-dialog modal-lg">
                                <div class="modal-content">
                                    <form th:action="@{/stg/doc/doc-field/create}" th:object="${docField}" method="post">
                                        <div class="modal-header">
                                            <strong class="modal-title">Thêm mới trường dữ liệu</strong>
                                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                <span aria-hidden="true">&times;</span>
                                            </button>
                                        </div>
                                        <div class="modal-body">
                                            <div class="row">
                                                <div class="form-group col-sm-12">
                                                    <label>Tên trường</label>
                                                    <input type="text" class="form-control"
                                                           placeholder="Tên trường" required
                                                           name="name"/>
                                                </div>
                                                <div class="form-group col-sm-12">
                                                    <label>Kiểu nhập</label>
                                                    <select class="custom-select" name="type">
                                                        <option value="text" selected>Text</option>
                                                        <option value="textarea">Text area</option>
                                                        <option value="number">Number</option>
                                                    </select>
                                                </div>
                                                <div class="form-group col-sm-6">
                                                    <label>Min length</label>
                                                    <input type="number" class="form-control" placeholder="Min length" name="minLength" value="0" required>
                                                </div>
                                                <div class="form-group col-sm-6">
                                                    <label>Max length</label>
                                                    <input type="number" class="form-control" placeholder="Max length" name="maxLength" value="255" required>
                                                </div>
                                                <div class="form-group col-sm-6">
                                                    <label>Min number</label>
                                                    <input type="number" class="form-control" placeholder="Min number" name="minNumber" value="0" required>
                                                </div>
                                                <div class="form-group col-sm-6">
                                                    <label>Max number</label>
                                                    <input type="number" class="form-control" placeholder="Max number" name="maxNumber" value="255" required>
                                                </div>
                                                <div class="form-group col-sm-12">
                                                    <label>Có bắt buộc nhập?</label>
                                                    <input type="checkbox" class="form-control" style="display: block; width: 4%" name="required">
                                                </div>
                                                <div class="form-group col-sm-12">
                                                    <label>Sắp xếp</label>
                                                    <input type="number" class="form-control" placeholder="Sắp xếp" name="sort" value="0" required>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="modal-footer justify-content-end" style="margin-bottom: -15px;">
                                            <input type="hidden" name="docType" th:value="${docTypeId}">
                                            <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                                            <button type="submit" class="btn btn-primary">Lưu</button>
                                        </div>
                                    </form>
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
    <script>
        $(document).ready(function () {
            deleteDocField();
        });

        function deleteDocField() {
            $(document).on("click", ".btn-delete", function () {
                let id = $(this).attr("id");
                let name = $(this).attr("name");
                $(this).attr("actionType", "delete");
                $(this).attr("entityId", id);
                $(this).attr("entityName", name);
                showConfirmModal($(this), "Xóa trường dữ liệu", "Bạn chắc chắn muốn xóa trường dữ liệu: " + name);
            });

            $('#yesButton').on("click", function () {
                let apiURL = mvHostURLCallApi + "/stg/doc/doc-field/delete/" + parseInt($(this).attr("entityId"));
                callApiDelete(apiURL);
            });
        }
    </script>
</body>
</html>