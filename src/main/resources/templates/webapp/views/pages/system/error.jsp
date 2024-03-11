<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Flowiee official | Error</title>
    <div th:replace="header :: stylesheets">
        <!--Nhúng các file css, icon,...-->
    </div>
    <style rel="stylesheet">
        .table td,
        th {
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
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1></h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                        </ol>
                    </div>
                </div>
            </div><!-- /.container-fluid -->
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="error-page">
                <h2 class="headline text-danger" th:text="${error.status}">403</h2>
                <div class="error-content">
                    <h3><i class="fas fa-exclamation-triangle text-danger"></i> Flowiee! Thông báo.</h3>
                    <p th:text="${error.message}"></p>
                </div>
            </div>
            <!-- /.error-page -->

        </section>
        <!-- /.content -->
    </div>

    <div th:replace="footer :: footer">
        <!-- Nhúng các file JavaScript vào -->
    </div>

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