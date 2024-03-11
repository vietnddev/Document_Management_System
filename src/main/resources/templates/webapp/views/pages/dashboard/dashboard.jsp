<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Flowiee Offical | Dashboard</title>
    <div th:replace="header :: stylesheets"></div>
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
                    <div class="col">
                        <div class="col-12 input-group w-100 p-0 mb-2">
                            <input class="form-control col-10" id="txtFilter"/>
                            <button class="btn btn-info form-control col-2" id="btnSearch"><i class="fa-solid fa-magnifying-glass"></i></button>
                        </div>
                        <!-- small box -->
                        <div class="small-box bg-success">
                            <div class="inner">
                                <h3>[[${totalProducts}]]</h3>
                                <p>Total products available</p>
                            </div>
                            <div class="icon">
                                <i class="ion ion-stats-bars"></i>
                            </div>
                            <a href="#" class="small-box-footer">Detail <i class="fas fa-arrow-circle-right"></i></a>
                        </div>
                    </div>

                    <div class="col-lg-3 col-6">
                        <div class="col-12 input-group w-100 p-0 mb-2">
                            <input class="form-control col-10" id="txtFilter"/>
                            <button class="btn btn-info form-control col-2" id="btnSearch"><i class="fa-solid fa-magnifying-glass"></i></button>
                        </div>
                        <!-- small box -->
                        <div class="small-box bg-info">
                            <div class="inner">
                                <h3>[[${soLuongDonHangHomNay}]]</h3>
                                <p>Orders today</p>
                            </div>
                            <div class="icon">
                                <i class="ion ion-bag"></i>
                            </div>
                            <a href="#" class="small-box-footer"
                               data-toggle="modal" data-target="#modalDanhSachDonHangToDay">Detail <i class="fas fa-arrow-circle-right"></i>
                            </a>
                            <div class="modal fade" id="modalDanhSachDonHangToDay">
                                <div class="modal-dialog modal-lg">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <strong class="modal-title">Danh sách đơn hàng đã bán được hôm nay</strong>
                                            <button type="button" class="close" data-dismiss="modal"
                                                    aria-label="Close">
                                                <span aria-hidden="true">&times;</span>
                                            </button>
                                        </div>
                                        <div class="modal-body">
                                            <table class="table table-bordered table-striped align-items-center">
                                                <thead class="align-self-center">
                                                    <tr class="align-self-center">
                                                        <th>STT</th>
                                                        <th>Mã đơn hàng</th>
                                                        <th>Khách hàng</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                <tr th:each="list, index : ${danhSachDonHangHomNay}">
                                                    <td th:text="${index.index + 1}"></td>
                                                    <td></td>
                                                    <td></td>
                                                </tr>
                                                </tbody>
                                                <tfoot>
                                                    <tr class="align-self-center">
                                                        <th>STT</th>
                                                        <th>Mã đơn hàng</th>
                                                        <th>Khách hàng</th>
                                                    </tr>
                                                </tfoot>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- ./col -->
                    <div class="col-lg-3 col-6">
                        <div class="col-12 input-group w-100 p-0 mb-2">
                            <input class="form-control col-10" id="txtFilter"/>
                            <button class="btn btn-info form-control col-2" id="btnSearch"><i class="fa-solid fa-magnifying-glass"></i></button>
                        </div>
                        <!-- small box -->
                        <div class="small-box bg-success">
                            <div class="inner">
                                <h3>[[${doanhThuHomNay}]]</h3>
                                <p>Revenue today</p>
                            </div>
                            <div class="icon">
                                <i class="ion ion-stats-bars"></i>
                            </div>
                            <a href="#" class="small-box-footer">Detail <i class="fas fa-arrow-circle-right"></i></a>
                        </div>
                    </div>
                    <!-- ./col -->
                    <div class="col-lg-3 col-6">
                        <div class="col-12 input-group w-100 p-0 mb-2">
                            <input class="form-control col-10" id="txtFilter"/>
                            <button class="btn btn-info form-control col-2" id="btnSearch"><i class="fa-solid fa-magnifying-glass"></i></button>
                        </div>
                        <!-- small box -->
                        <div class="small-box bg-primary">
                            <div class="inner">
                                <h3>[[${doanhThuThangNay}]]</h3>
                                <p>Revenue this month</p>
                            </div>
                            <div class="icon">
                                <i class="ion ion-pie-graph"></i>
                            </div>
                            <a href="#" class="small-box-footer">Detail <i class="fas fa-arrow-circle-right"></i></a>
                        </div>
                    </div>
                    <!-- ./col -->
                    <div class="col-lg-3 col-6">
                        <div class="col-12 input-group w-100 p-0 mb-2">
                            <input class="form-control col-10" id="txtFilter"/>
                            <button class="btn btn-info form-control col-2" id="btnSearch"><i class="fa-solid fa-magnifying-glass"></i></button>
                        </div>
                        <!-- small box -->
                        <div class="small-box bg-warning">
                            <div class="inner">
                                <h3>[[${khachHangMoiTrongThang}]]</h3>
                                <p>Customers new</p>
                            </div>
                            <div class="icon">
                                <i class="ion ion-person-add"></i>
                            </div>
                            <a href="#" class="small-box-footer">Detail <i class="fas fa-arrow-circle-right"></i></a>
                        </div>
                    </div>
                    <!-- ./col -->
                </div>
                <div class="row">
                    <!--THỐNG KÊ THEO KÊNH BÁN HÀNG-->
                    <div class="col-sm-6">
                        <div class="col-12 input-group w-100 p-0 mb-2">
                            <input class="form-control col-11" id="txtFilter"/>
                            <button class="btn btn-info form-control col-1" id="btnSearch"><i class="fa-solid fa-magnifying-glass"></i></button>
                        </div>
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
                                    var xValues = [[${doanhThuOfKBH_listTen}]];
                                    var yValues = [[${doanhThuOfKBH_listDoanhThu}]];
                                    var barColors = [[${doanhThuOfKBH_listMauSac}]];

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
                    <div class="col-sm-6">
                        <div class="col-12 input-group w-100 p-0 mb-2">
                            <input class="form-control col-11" id="txtFilter"/>
                            <button class="btn btn-info form-control col-1" id="btnSearch"><i class="fa-solid fa-magnifying-glass"></i></button>
                        </div>
                        <div class="card card-info">
                            <div class="card-header">
                                <h3 class="card-title">Doanh thu của các tháng trong năm</h3>
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
                                <canvas id="myLineChart" style="width: 100%; height: 350px"></canvas>
                                <script th:inline="javascript">
                                    var nameOfMonth = ["Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"];
                                    var dataOfMonth = [[${doanhThuOfMonth_listDoanhThu}]];
                                    var ctx = document.getElementById("myLineChart");
                                    var myLineChart = new Chart(ctx, {
                                        type: 'line',
                                        data: {
                                            labels: nameOfMonth,
                                            datasets: [{
                                                label: "Thu Nhập(VNĐ)",
                                                lineTension: 0.3,
                                                backgroundColor: "rgba(2,117,216,0.2)",
                                                borderColor: "rgba(2,117,216,1)",
                                                pointRadius: 5,
                                                pointBackgroundColor: "rgba(2,117,216,1)",
                                                pointBorderColor: "rgba(255,255,255,0.8)",
                                                pointHoverRadius: 5,
                                                pointHoverBackgroundColor: "rgba(2,117,216,1)",
                                                pointHitRadius: 50,
                                                pointBorderWidth: 1,
                                                data: dataOfMonth,
                                            }],
                                        },
                                    });</script>
                            </div>
                        </div>
                    </div>
                    <div class="col-sm-12">
                        <div class="col-12 input-group w-100 p-0 mb-2">
                            <input class="form-control col-11" id="txtFilter"/>
                            <button class="btn btn-info form-control col-1" id="btnSearch"><i class="fa-solid fa-magnifying-glass"></i></button>
                        </div>
                        <div class="card card-info">
                            <div class="card-header">
                                <h3 class="card-title">Doanh thu theo các ngày trong tháng</h3>
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
                                <canvas id="lineChart_DoanhThuTungNgay" style="width: 100%; height: 350px"></canvas>
                                <script th:inline="javascript">
                                    var nameOfMonth = [[${doanhThuOfDay_listNgay}]];
                                    var dataOfMonth = [[${doanhThuOfDay_listDoanhThu}]];
                                    var ctx = document.getElementById("lineChart_DoanhThuTungNgay");
                                    var myLineChart = new Chart(ctx, {
                                        type: 'line',
                                        data: {
                                            labels: nameOfMonth,
                                            datasets: [{
                                                label: "Thu Nhập(VNĐ)",
                                                lineTension: 0.3,
                                                backgroundColor: "rgba(2,117,216,0.2)",
                                                borderColor: "rgba(2,117,216,1)",
                                                pointRadius: 5,
                                                pointBackgroundColor: "rgba(2,117,216,1)",
                                                pointBorderColor: "rgba(255,255,255,0.8)",
                                                pointHoverRadius: 5,
                                                pointHoverBackgroundColor: "rgba(2,117,216,1)",
                                                pointHitRadius: 50,
                                                pointBorderWidth: 1,
                                                data: dataOfMonth,
                                            }],
                                        },
                                    });</script>
                            </div>
                        </div>
                    </div>
                    <div class="col-sm-12">
                        <div class="col-12 input-group w-100 p-0 mb-2">
                            <input class="form-control col-11" id="txtFilter"/>
                            <button class="btn btn-info form-control col-1" id="btnSearch"><i class="fa-solid fa-magnifying-glass"></i></button>
                        </div>
                        <div class="card card-info">
                            <div class="card-header">
                                <h3 class="card-title">Top 10 sản phẩm bán chạy</h3>
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
                                <canvas id="lineChartTopProduct" style="width: 100%; height: 350px"></canvas>
                                <script th:inline="javascript">
                                    var xValues = [[${topSanPham_listTenSanPham}]];
                                    var yValues = [[${topSanPham_listSoLuong}]];
                                    var barColors = ["red", "green", "blue", "orange", "brown", "red", "green", "blue", "orange", "brown"];

                                    new Chart("lineChartTopProduct", {
                                        type: "bar",
                                        data: {
                                            labels: xValues,
                                            datasets: [{
                                                backgroundColor: barColors,
                                                data: yValues
                                            }]
                                        },
                                        options: {
                                            legend: {display: false},
                                            title: {
                                                display: true,
                                                text: "Top 10 sản phẩm bán chạy nhất trong tháng"
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