<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Flowiee | Quản lý sản phẩm - Báo cáo thống kê</title>
    <div th:replace="header :: stylesheets">
        <!--Nhúng các file css, icon,...-->
    </div>
    <!--Chart JS-->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.9.4/Chart.js"></script>
</head>
<body class="hold-transition sidebar-mini layout-fixed">
<div class="wrapper">
    <div th:replace="header :: header"></div>

    <div th:replace="sidebar :: sidebar"></div>

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper" style="padding-top: 15px; padding-bottom: 1px;">
        <!-- Main content -->
        <section class="content">
            <div class="container-fluid">
                <div class="row">
                    <!--THỐNG KÊ THEO KÊNH BÁN HÀNG-->
                    <div class="col-sm-12">
                        <div class="card card-info">
                            <div class="card-header">
                                <h3 class="card-title">Doanh thu theo kênh bán hàng</h3>
                                <div class="card-tools">
                                    <button type="button" class="btn btn-tool" data-card-widget="collapse">
                                        <i class="fas fa-minus"></i>
                                    </button>
                                    <button type="button" class="btn btn-tool" data-card-widget="remove">
                                        <i class="fas fa-times"></i>
                                    </button>
                                </div>
                            </div>
                            <div class="card-body">
                                <canvas id="myChart" style="width: 100%; height: 350px"></canvas>
                                <script th:inline="javascript">
                                    var xValues = [[${reportOfDocType_listTen}]];
                                    var yValues = [[${reportOfDocType_listSoLuong}]];
                                    var barColors = [];

                                    new Chart("myChart", {
                                        type: "pie",
                                        data: {
                                            labels: xValues,
                                            datasets: [{
                                                backgroundColor: barColors,
                                                data: yValues
                                            }]
                                        },
                                        options: {
                                            title: {
                                                display: true,
                                                text: "Số liệu đến tháng ? năm ?"
                                            }
                                        }
                                    });
                                </script>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
    <!-- /.content -->
</div>
<!-- /.content-wrapper -->

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