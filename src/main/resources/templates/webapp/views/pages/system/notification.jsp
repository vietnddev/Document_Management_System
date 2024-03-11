<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Nhật ký hệ thống</title>
    <div th:replace="header :: stylesheets">
        <!--Nhúng các file css, icon,...-->
    </div>
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
                                <h3 class="card-title"><strong>THÔNG BÁO HỆ THỐNG</strong></h3>
                            </div>
                            <!-- /.card-header -->
                            <div class="card-body">
                                <table id="example1" class="table table-bordered table-striped">
                                    <thead>
                                        <tr>
                                            <th>STT</th>
                                            <th>Time</th>
                                            <th>Title</th>
                                            <th>Detail</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr th:each="noti, index : ${listNotification}">
                                            <td th:text="${index.index + 1}"></td>
                                            <td th:text="${noti.createdAt}"></td>
                                            <td th:text="${noti.title}"></td>
                                            <td th:text="${noti.content}"></td>
                                        </tr>
                                    </tbody>
                                    <tfoot>
                                        <tr>
                                            <th>STT</th>
                                            <th>Time</th>
                                            <th>Title</th>
                                            <th>Detail</th>
                                        </tr>
                                    </tfoot>
                                </table>
                            </div>
                            <!-- /.card-body -->
                        </div>
                    </div>
                </div>
            </div>
        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->


    <!-- Control Sidebar -->
    <aside class="control-sidebar control-sidebar-dark">
        <!-- Control sidebar content goes here -->
    </aside>

    <div th:replace="header :: scripts">
        <!-- Nhúng các file JavaScript vào -->
    </div>
</div>

</body>

</html>
