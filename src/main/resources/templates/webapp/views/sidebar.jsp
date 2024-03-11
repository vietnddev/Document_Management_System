<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Flowiee Official | Dashboard</title>
    </head>

    <body class="hold-transition sidebar-mini layout-fixed">
        <div th:fragment="sidebar">
            <aside class="main-sidebar sidebar-dark-primary elevation-4">
                <div class="sidebar">
                    <div class="form-inline mt-3">
                        <div class="input-group" data-widget="sidebar-search">
                            <input class="form-control form-control-sidebar" type="search" placeholder="Search" aria-label="Search">
                            <div class="input-group-append">
                                <button class="btn btn-sidebar"><i class="fas fa-search fa-fw"></i></button>
                            </div>
                        </div>
                    </div>

                    <nav class="mt-2">
                        <ul class="nav nav-pills nav-sidebar flex-column" data-widget="treeview" role="menu" data-accordion="false">
                            <li class="nav-header">
                                <strong>QUẢN LÝ BÁN HÀNG</strong></li>
                            <li class="nav-item">
                                <a th:href="@{${URL_PRODUCT}}" class="nav-link"><i class="fa-solid fa-shirt nav-icon mr-2"></i><p>Sản phẩm</p></a>
                            </li>
                            <li class="nav-item">
                                <a th:href="@{${URL_PRODUCT_ORDER}}" class="nav-link"><i class="fa-solid fa-cart-shopping nav-icon mr-2"></i><p>Đơn hàng</p></a>
                            </li>
                            <li class="nav-item">
                                <a th:href="@{/don-hang/ban-hang}" class="nav-link"><i class="fa-solid fa-file-invoice-dollar mr-2"></i><p>Bán hàng</p></a>
                            </li>
                            <li class="nav-item">
                                <a th:href="@{/san-pham/voucher}" class="nav-link"><i class="fa-solid fa-gifts mr-2"></i><p>Voucher</p></a>
                            </li>
                            <li class="nav-item">
                                <a th:href="@{${URL_PRODUCT_CUSTOMER}}" class="nav-link"><i class="fa-solid fa-user-tag mr-2"></i><p>Khách hàng</p></a>
                            </li>
                            <li class="nav-item">
                                <a th:href="@{${URL_PRODUCT_SUPPLIER}}" class="nav-link"><i class="fa-solid fa-user-tag mr-2"></i><p>Nhà cung cấp</p></a>
                            </li>

                            <li class="nav-header">
                                <hr class="mt-0 mb-3" style="border-color: darkgrey">
                                <strong>KHO</strong>
                            </li>
                            <li class="nav-item">
                                <a th:href="@{${URL_STORAGE_TICKET_IMPORT}}" class="nav-link"><i class="fa-solid fa-cloud-arrow-up fa-rotate-90 mr-2"></i><p>Nhập hàng</p></a>
                            </li>
                            <li class="nav-item">
                                <a th:href="@{${URL_STORAGE_TICKET_EXPORT}}" class="nav-link"><i class="fa-solid fa-cloud-arrow-up fa-rotate-270 mr-2"></i><p>Xuất hàng</p></a>
                            </li>
                            <li class="nav-item">
                                <a th:href="@{${URL_STORAGE_MATERIAL}}" class="nav-link"><i class="fa-solid fa-scroll nav-icon mr-2"></i><p>Nguyên vật liệu</p></a>
                            </li>

                            <li class="nav-header">
                                <hr class="mt-0 mb-3" style="border-color: darkgrey">
                                <strong>QUẢN TRỊ HỆ THỐNG</strong>
                            </li>
                            <li class="nav-item">
                                <a th:href="@{${URL_SYSTEM_CONFIG}}" class="nav-link"><i class="fa-solid fa-gear nav-icon mr-2"></i><p>Cấu hình</p></a>
                            </li>
                            <li class="nav-item">
                                <a th:href="@{${URL_SYSTEM_ACCOUNT}}" class="nav-link"><i class="fa-solid fa-users nav-icon mr-2"></i><p>Tài khoản hệ thống</p></a>
                            </li>
                            <li class="nav-item">
                                <a th:href="@{${URL_SYSTEM_LOG}}" class="nav-link"><i class="fa-solid fa-clock nav-icon mr-2"></i><p>Nhật ký hệ thống</p></a>
                            </li>
                        </ul>
                    </nav>
                </div>
            </aside>
        </div>
    </body>
</html>