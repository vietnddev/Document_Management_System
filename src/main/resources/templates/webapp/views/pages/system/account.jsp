<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tài khoản hệ thống</title>
    <div th:replace="header :: stylesheets"></div>
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
                        <!--Search tool-->
                        <div th:replace="fragments :: searchTool('Y','Y','Y','Y','Y','Y','Y')" id="searchTool"></div>

                        <div class="card">
                            <div class="card-header">
                                <div class="row justify-content-between">
                                    <div class="col-4" style="display: flex; align-items: center">
                                        <h3 class="card-title"><strong>TÀI KHOẢN HỆ THỐNG</strong></h3>
                                    </div>
                                    <div class="col-4 text-right">
                                        <button type="button" class="btn btn-success" data-toggle="modal" data-target="#insert">Thêm mới</button>
                                    </div>
                                </div>
                                <!-- modal-content (Thêm mới)-->
                            </div>
                            <!-- /.card-header -->
                            <div class="card-body">
                                <div class="row">
                                    <div th:each="list, index : ${listAccount}"
                                         class="col-12 col-sm-6 col-md-4 d-flex align-items-stretch flex-column">
                                        <div class="card bg-light d-flex flex-fill">
                                            <div class="card-header text-muted border-bottom-0">Nhân viên cửa hàng</div>
                                            <div class="card-body pt-0">
                                                <div class="row">
                                                    <div class="col-7">
                                                        <h2 class="lead"><b th:text="${list.fullName}"></b></h2>
                                                        <p class="text-muted text-sm"><b>About: </b> Software Engineer </p>
                                                        <ul class="ml-4 mb-0 fa-ul text-muted">
                                                            <li class="small"><span class="fa-li"><i class="fas fa-lg fa-building"></i></span><span th:text="'Address: ' + ${list.diaChi}"></span></li>
                                                            <li class="small"><span class="fa-li"><i class="fas fa-lg fa-phone"></i></span><span th:text="'Phone: ' + ${list.phoneNumber}"></span></li>
                                                        </ul>
                                                    </div>
                                                    <div class="col-5 text-center">
                                                        <img src="../../dist/img/user1-128x128.jpg" alt="user-avatar" class="img-circle img-fluid">
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="card-footer">
                                                <div class="text-right">
                                                    <button class="btn btn-sm btn-info" data-toggle="modal"
                                                            th:data-target="'#update-' + ${list.id}">
                                                        <i class="fa-solid fa-pencil"></i>
                                                    </button>
                                                    <button class="btn btn-sm btn-primary" data-toggle="modal"
                                                            th:data-target="'#role-' + ${list.id}">
                                                        <i class="fa-solid fa-share"></i>
                                                    </button>
                                                    <a th:href="@{/sys/tai-khoan/{id}(id=${list.id})}" class="btn btn-sm btn-primary">
                                                        <i class="fas fa-user"></i> View Profile
                                                    </a>
                                                </div>
                                            </div>
                                        </div>

                                        <!-- MODAL ROLE -->
                                        <div class="modal fade" th:id="'role-' + ${list.id}">
                                            <div class="modal-dialog modal-xl">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <strong class="modal-title">Phân quyền</strong>
                                                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                            <span aria-hidden="true">&times;</span>
                                                        </button>
                                                    </div>
                                                    <div class="modal-body">
                                                        <div class="card card-primary card-tabs" style="min-height: 550px; margin-bottom: 0px">
                                                            <div class="card-header p-0 pt-1">
                                                                <ul class="nav nav-tabs" id="custom-tabs-one-tab" role="tablist">
                                                                    <li class="nav-item" th:each="role : ${list.listRole}">
                                                                        <a class="nav-link"
                                                                           th:id="'#' + ${role.module.entrySet().iterator().next().key} + '_' + ${list.id}"
                                                                           data-toggle="pill"
                                                                           th:href="'#' + ${role.module.entrySet().iterator().next().key} + '_' + ${list.id}"
                                                                           role="tab"
                                                                           aria-controls="custom-tabs-one-home"
                                                                           aria-selected="true"
                                                                           style="font-weight: bold"
                                                                           Th:text="${role.module.entrySet().iterator().next().value}">
                                                                        </a>
                                                                    </li>
                                                                </ul>
                                                            </div>
                                                            <div class="card-body" style="max-height: 485px; overflow: overlay">
                                                                <div class="tab-content" id="custom-tabs-one-tabContent">
                                                                    <div class="tab-pane fade"
                                                                         role="tabpanel"
                                                                         aria-labelledby="custom-tabs-one-home-tab"
                                                                         th:each="role : ${list.listRole}"
                                                                         th:id="${role.module.entrySet().iterator().next().key} + '_' + ${list.id}">
                                                                        <table class="table table-bordered table-striped w-100">
                                                                            <thead>
                                                                                <tr>
                                                                                    <th>STT</th>
                                                                                    <th></th>
                                                                                    <th>Key</th>
                                                                                    <th>Tên quyền</th>
                                                                                </tr>
                                                                            </thead>
                                                                            <tbody>
                                                                                <tr th:each="action : ${role.action}">
                                                                                    <td>
                                                                                        <input type="hidden" name="module" th:value="${role.module.entrySet().iterator().next().key}">
                                                                                        <input type="hidden" name="keyAction" th:value="${action.keyAction}">
                                                                                        <input type="hidden" name="valueAction" th:value="${action.valueAction}">
                                                                                    </td>
                                                                                    <td class="text-center">
                                                                                        <div class="form-group clearfix" style="margin: 0 auto">
                                                                                            <div class="icheck-primary">
                                                                                                <input type="checkbox" name="isChecked" th:checked="${action.isChecked}" th:id="'checked_' + ${action.keyAction}">
                                                                                                <label th:for="'checked_' + ${action.keyAction}"></label>
                                                                                            </div>
                                                                                        </div>
                                                                                    </td>
                                                                                    <td th:text="${action.keyAction}"></td>
                                                                                    <td th:text="${action.valueAction}"></td>
                                                                                </tr>
                                                                            </tbody>
                                                                        </table>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <!-- /.END MODAL ROLE -->

                                        <!-- MODAL XÓA -->
                                        <div class="modal fade" th:id="'delete-' + ${list.id}">
                                            <div class="modal-dialog">
                                                <div class="modal-content">
                                                    <form th:action="@{/sys/tai-khoan/delete/{id}(id=${list.id})}"
                                                          th:object="${account}"
                                                          method="post">
                                                        <div class="modal-header">
                                                            <strong class="modal-title">Xác nhận khóa tài khoản</strong>
                                                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                                <span aria-hidden="true">&times;</span>
                                                            </button>
                                                        </div>
                                                        <div class="modal-body">
                                                            <div class="card-body">
                                                                Tài khoản <strong class="badge text-bg-info" th:text="${list.fullName}" style="font-size: 16px;"></strong>sẽ bị khóa!
                                                            </div>
                                                            <div class="modal-footer justify-content-end" style="margin-bottom: -15px;">
                                                                <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                                                                <button type="submit" class="btn btn-primary">Đồng ý</button>
                                                            </div>
                                                        </div>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>
                                        <!-- /.END MODAL XÓA -->

                                        <!-- /.card-body -->
                                        <!-- Update -->
                                        <div class="modal fade" th:id="'update-' + ${list.id}">
                                            <div class="modal-dialog">
                                                <div class="modal-content">
                                                    <form th:action="@{/sys/tai-khoan/update/{id}(id=${list.id})}"
                                                          th:object="${account}" method="post">
                                                        <div class="modal-header">
                                                            <strong class="modal-title">Cập nhật thông tin tài khoản</strong>
                                                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                                <span aria-hidden="true">&times;</span>
                                                            </button>
                                                        </div>
                                                        <div class="modal-body">
                                                            <div class="row">
                                                                <div class="col-12">
                                                                    <div class="form-group">
                                                                        <label>Tên đăng nhập</label>
                                                                        <input type="text" class="form-control" placeholder="Tên đăng nhập" th:value="${list.username}" disabled/>
                                                                    </div>
                                                                    <div class="form-group">
                                                                        <label>Họ tên</label>
                                                                        <input type="text" class="form-control" placeholder="Họ tên" name="fullName" th:value="${list.fullName}"/>
                                                                    </div>
                                                                    <div class="form-group" th:if="${list.sex}">
                                                                        <label>Giới tính</label>
                                                                        <select class="custom-select" name="sex">
                                                                            <option value="true" selected>Nam</option>
                                                                            <option value="false">Nữ</option>
                                                                        </select>
                                                                    </div>
                                                                    <div class="form-group" th:if="not ${list.sex}">
                                                                        <label>Giới tính</label>
                                                                        <select class="custom-select" name="sex">
                                                                            <option value="true">Nam</option>
                                                                            <option value="false" selected>Nữ
                                                                            </option>
                                                                        </select>
                                                                    </div>
                                                                    <div class="form-group">
                                                                        <label>Số điện thoại</label>
                                                                        <input type="text" class="form-control" placeholder="Số điện thoại" name="phoneNumber" th:value="${list.phoneNumber}"/>
                                                                    </div>
                                                                    <div class="form-group">
                                                                        <label>Email</label>
                                                                        <input type="email" class="form-control" placeholder="Email" name="email" th:value="${list.email}"/>
                                                                    </div>
                                                                    <div class="form-group"
                                                                         th:if="${list.status}">
                                                                        <label>Trạng thái</label>
                                                                        <select class="custom-select" name="status">
                                                                            <option value="true" selected>Kích hoạt</option>
                                                                            <option value="false">Khóa</option>
                                                                        </select>
                                                                    </div>
                                                                    <div class="form-group"
                                                                         th:if="not ${list.status}">
                                                                        <label>Trạng thái</label>
                                                                        <select class="custom-select" name="status">
                                                                            <option value="true">Khóa</option>
                                                                            <option value="false" selected>Hoạt động</option>
                                                                        </select>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div class="modal-footer justify-content-end" style="margin-bottom: -15px;">
                                                                <input type="hidden" name="username" th:value="${list.username}"/>
                                                                <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                                                                <button type="submit" class="btn btn-primary">Lưu</button>
                                                            </div>
                                                        </div>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>
                                        <!-- /.Popup cập nhật, xóa -->
                                    </div>
                                </div>
                            </div>

                            <div class="modal fade" id="insert">
                                <div class="modal-dialog">
                                    <div class="modal-content">
                                        <form th:action="@{/sys/tai-khoan/insert}" th:object="${account}"
                                              method="post">
                                            <div class="modal-header">
                                                <strong class="modal-title">Thêm mới tài khoản</strong>
                                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                    <span aria-hidden="true">&times;</span>
                                                </button>
                                            </div>
                                            <div class="modal-body">
                                                <div class="row">
                                                    <div class="col-12">
                                                        <div class="form-group">
                                                            <label>Tên đăng nhập</label>
                                                            <input type="text" class="form-control" placeholder="Tên đăng nhập" name="username">
                                                        </div>
                                                        <div class="form-group">
                                                            <label>Mật khẩu</label>
                                                            <input type="text" class="form-control" placeholder="Mật khẩu" name="password">
                                                        </div>
                                                        <div class="form-group">
                                                            <label>Họ tên</label>
                                                            <input type="text" class="form-control" placeholder="Họ tên" name="fullName">
                                                        </div>
                                                        <div class="form-group">
                                                            <label>Giới tính</label>
                                                            <select class="custom-select" name="sex">
                                                                <option value="true" selected>Nam</option>
                                                                <option value="false">Nữ</option>
                                                            </select>
                                                        </div>
                                                        <div class="form-group">
                                                            <label>Số điện thoại</label>
                                                            <input type="text" class="form-control" placeholder="Số điện thoại" name="phoneNumber">
                                                        </div>
                                                        <div class="form-group">
                                                            <label>Email</label>
                                                            <input type="email" class="form-control" placeholder="Email" name="email">
                                                        </div>
                                                        <div class="form-group">
                                                            <label>Trạng thái</label>
                                                            <select class="custom-select" name="status">
                                                                <option value="true" selected>Kích hoạt</option>
                                                                <option value="false">Khóa</option>
                                                            </select>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="modal-footer justify-content-end" style="margin-bottom: -15px;">
                                                    <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                                                    <button type="submit" class="btn btn-primary">Lưu</button>
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                    <!-- /.modal-content -->
                                </div>
                                <!-- /.modal-dialog -->
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->

    <div th:replace="footer :: footer"><!-- Nhúng các file JavaScript vào --></div>

    <!-- Control Sidebar -->
    <aside class="control-sidebar control-sidebar-dark"><!-- Control sidebar content goes here --></aside>

    <div th:replace="header :: scripts"><!-- Nhúng các file JavaScript vào --></div>
</div>

</body>

</html>