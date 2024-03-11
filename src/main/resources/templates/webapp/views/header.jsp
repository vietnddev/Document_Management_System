<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">

<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Flowiee Official | Dashboard</title>
  <th:block th:fragment="stylesheets">
    <!-- Google Font: Source Sans Pro -->
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
    <!-- Font Awesome -->
    <link rel="stylesheet" th:href="@{/plugins/fontawesome-free/css/all.min.css}">
    <!-- Ionicons -->
    <link rel="stylesheet" href="https://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css">
    <!-- Tempusdominus Bootstrap 4 -->
    <link rel="stylesheet" th:href="@{/plugins/tempusdominus-bootstrap-4/css/tempusdominus-bootstrap-4.min.css}">
    <!-- iCheck -->
    <link rel="stylesheet" th:href="@{/plugins/icheck-bootstrap/icheck-bootstrap.min.css}">
    <!-- JQVMap -->
    <link rel="stylesheet" th:href="@{/plugins/jqvmap/jqvmap.min.css}">
    <!-- Theme style -->
    <link rel="stylesheet" th:href="@{/dist/css/adminlte.min.css}">
    <!-- overlayScrollbars -->
    <link rel="stylesheet" th:href="@{/plugins/overlayScrollbars/css/OverlayScrollbars.min.css}">
    <!-- Daterange picker -->
    <link rel="stylesheet" th:href="@{/plugins/daterangepicker/daterangepicker.css}">
    <!-- summernote -->
    <link rel="stylesheet" th:href="@{/plugins/summernote/summernote-bs4.min.css}">
    <!-- DataTables -->
    <link rel="stylesheet" th:href="@{/plugins/datatables-bs4/css/dataTables.bootstrap4.min.css}">
    <link rel="stylesheet" th:href="@{/plugins/datatables-responsive/css/responsive.bootstrap4.min.css}">
    <link rel="stylesheet" th:href="@{/plugins/datatables-buttons/css/buttons.bootstrap4.min.css}">
    <!-- SimpleMDE -->
    <link rel="stylesheet" th:href="@{/plugins/simplemde/simplemde.min.css}">
    <!-- dropzonejs -->
    <link rel="stylesheet" th:href="@{/plugins/dropzone/min/dropzone.min.css}">
    <!-- Ekko Lightbox -->
    <link rel="stylesheet" th:href="@{/plugins/ekko-lightbox/ekko-lightbox.css}">
    <!-- Bootstrap Color Picker -->
    <link rel="stylesheet" th:href="@{/plugins/bootstrap-colorpicker/css/bootstrap-colorpicker.min.css}">
    <!-- fullCalendar -->
    <link rel="stylesheet" th:href="@{/plugins/fullcalendar/main.css}">
    <!-- Select2 -->
    <link rel="stylesheet" th:href="@{/plugins/select2/css/select2.min.css}">
    <link rel="stylesheet" th:href="@{/plugins/select2-bootstrap4-theme/select2-bootstrap4.min.css}">
    <!-- Bootstrap4 Duallistbox -->
    <link rel="stylesheet" th:href="@{/plugins/bootstrap4-duallistbox/bootstrap-duallistbox.min.css}">
  </th:block>
</head>

<body class="hold-transition sidebar-mini layout-fixed">
  <div th:fragment="header">
    <!-- Navbar -->
    <nav class="main-header navbar navbar-expand navbar-white navbar-light">
      <!-- Left navbar links -->
      <ul class="navbar-nav">
        <li class="nav-item">
          <a class="nav-link" data-widget="pushmenu" href="#" role="button"><i class="fas fa-bars"></i></a>
        </li>
        <li class="nav-item d-none d-sm-inline-block border mr-2"
            style="border-radius: 15px; font-weight: bold; background-color: aliceblue">
          <a th:href="@{/}" class="nav-link"><i class="fa-solid fa-chart-pie nav-icon"></i>
            Dashboard
          </a>
        </li>
        <li class="nav-item d-none d-sm-inline-block border mr-2"
            style="border-radius: 15px; font-weight: bold; background-color: aliceblue">
          <a th:href="@{${URL_PRODUCT_GALLERY}}" class="nav-link"><i class="fa-solid fa-image nav-icon"></i>
            Kho hình ảnh
          </a>
        </li>
        <li class="nav-item d-none d-sm-inline-block border mr-2"
            style="border-radius: 15px; font-weight: bold; background-color: aliceblue">
          <a th:href="@{${URL_STORAGE_DOCUMENT}}" class="nav-link"><i class="fa-solid fa-file-pdf nav-icon"></i>
            Kho tài liệu
          </a>
        </li>
        <li class="nav-item d-none d-sm-inline-block border mr-2"
            style="border-radius: 15px; font-weight: bold; background-color: aliceblue">
          <a th:href="@{${URL_CATEGORY}}" class="nav-link"><i class="fa-solid fa-list nav-icon"></i>
            Danh mục hệ thống
          </a>
        </li>
      </ul>

      <!-- Right navbar links -->
      <ul class="navbar-nav ml-auto">
        <!-- Notifications Dropdown Menu -->
        <li class="nav-item">
          <a class="nav-link">
            <span class="badge badge-warning" th:text="${USERNAME_LOGIN}"></span>
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" data-toggle="modal" data-target="#notification">
            <i class="far fa-bell" style="cursor: pointer"></i>
            <span class="badge badge-warning navbar-badge" style="cursor: pointer">15</span>
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" th:href="@{/sys/profile}">
            <i class="fa-regular fa-user"></i>
          </a>
        </li>
        <li class="nav-item" style="width: 36px;">
          <div class="btn-group" style="margin-bottom: -3px">
            <a class="nav-link pl-0 pr-0">
              <i class="fa-solid fa-language"></i>
            </a>
            <button type="button" class="btn btn-sm dropdown-toggle dropdown-toggle-split" data-toggle="dropdown" aria-expanded="false"></button>
            <div class="dropdown-menu">
              <a class="dropdown-item" id="langOptionVi" style="cursor: pointer">Vietnamese</a>
              <a class="dropdown-item" id="langOptionEn" style="cursor: pointer">English</a>
            </div>
          </div>
          <hr class="bg-info mt-0 mb-0">
        </li>
        <li class="nav-item">
          <a class="nav-link" th:href="@{/sys/logout}">
            <i class="fa-solid fa-right-from-bracket"></i>
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" data-widget="fullscreen" href="#" role="button">
            <i class="fas fa-expand-arrows-alt"></i>
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" data-widget="control-sidebar" data-controlsidebar-slide="true" href="#" role="button">
            <i class="fas fa-th-large"></i>
          </a>
        </li>
      </ul>
    </nav>

    <div class="modal fade" id="notification">
      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="card">
            <div class="card-header">
              <div class="row justify-content-center">
                <h3 class="card-title"><strong>Thông báo hệ thống</strong></h3>
              </div>
            </div>
            <div class="card-body" style="padding: 0; max-height: 600px; overflow: overlay">
              <table class="table table-bordered table-striped align-items-center">
                <thead class="align-self-center">
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
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
  <div th:fragment="scripts">
    <!--Lib support format time-->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.29.1/moment.min.js"></script>
    <!-- jQuery -->
    <script th:src="@{/plugins/jquery/jquery.min.js}"></script>
    <!-- jQuery UI 1.11.4 -->
    <script th:src="@{/plugins/jquery-ui/jquery-ui.min.js}"></script>
    <!-- Resolve conflict in jQuery UI tooltip with Bootstrap tooltip -->
    <script>
      $.widget.bridge('uibutton', $.ui.button)
    </script>
    <!-- Bootstrap 4 -->
    <script th:src="@{/plugins/bootstrap/js/bootstrap.bundle.min.js}"></script>
    <!-- ChartJS -->
    <script th:src="@{/plugins/chart.js/Chart.min.js}"></script>
    <!-- Sparkline -->
    <script th:src="@{/plugins/sparklines/sparkline.js}"></script>
    <!-- JQVMap -->
    <script th:src="@{/plugins/jqvmap/jquery.vmap.min.js}"></script>
    <script th:src="@{/plugins/jqvmap/maps/jquery.vmap.usa.js}"></script>
    <!-- jQuery Knob Chart -->
    <script th:src="@{/plugins/jquery-knob/jquery.knob.min.js}"></script>
    <!-- daterangepicker -->
    <script th:src="@{/plugins/moment/moment.min.js}"></script>
    <script th:src="@{/plugins/daterangepicker/daterangepicker.js}"></script>
    <!-- Tempusdominus Bootstrap 4 -->
    <script th:src="@{/plugins/tempusdominus-bootstrap-4/js/tempusdominus-bootstrap-4.min.js}"></script>
    <!-- Summernote -->
    <script th:src="@{/plugins/summernote/summernote-bs4.min.js}"></script>
    <!-- overlayScrollbars -->
    <script th:src="@{/plugins/overlayScrollbars/js/jquery.overlayScrollbars.min.js}"></script>
    <!-- AdminLTE App -->
    <script th:src="@{/dist/js/adminlte.js}"></script>
    <!-- AdminLTE for demo purposes -->
    <script th:src="@{/dist/js/demo.js}"></script>
    <!-- AdminLTE dashboard demo (This is only for demo purposes) -->
    <script th:src="@{/dist/js/pages/dashboard.js}"></script>
    <link rel="stylesheet" href="https://kit.fontawesome.com/68eba0f5f3.css" crossorigin="anonymous">
    <!--Icon-->
    <script src="https://kit.fontawesome.com/68eba0f5f3.js" crossorigin="anonymous"></script>
    <!-- DataTables  & Plugins -->
    <script th:src="@{/plugins/datatables/jquery.dataTables.min.js}"></script>
    <script th:src="@{/plugins/datatables-bs4/js/dataTables.bootstrap4.min.js}"></script>
    <script th:src="@{/plugins/datatables-responsive/js/dataTables.responsive.min.js}"></script>
    <script th:src="@{/plugins/datatables-responsive/js/responsive.bootstrap4.min.js}"></script>
    <script th:src="@{/plugins/datatables-buttons/js/dataTables.buttons.min.js}"></script>
    <script th:src="@{/plugins/datatables-buttons/js/buttons.bootstrap4.min.js}"></script>
    <script th:src="@{/plugins/jszip/jszip.min.js}"></script>
    <script th:src="@{/plugins/pdfmake/pdfmake.min.js}"></script>
    <script th:src="@{/plugins/pdfmake/vfs_fonts.js}"></script>
    <script th:src="@{/plugins/datatables-buttons/js/buttons.html5.min.js}"></script>
    <script th:src="@{/plugins/datatables-buttons/js/buttons.print.min.js}"></script>
    <script th:src="@{/plugins/datatables-buttons/js/buttons.colVis.min.js}"></script>
    <!-- Summernote -->
    <script th:src="@{/plugins/summernote/summernote-bs4.min.js}"></script>
    <!-- dropzonejs -->
    <script th:src="@{/plugins/dropzone/min/dropzone.min.js}"></script>
    <!-- Filterizr-->
    <script th:src="@{/plugins/filterizr/jquery.filterizr.min.js}"></script>
    <!-- Ekko Lightbox -->
    <script th:src="@{/plugins/ekko-lightbox/ekko-lightbox.min.js}"></script>
    <!-- bootstrap color picker -->
    <script th:src="@{/plugins/bootstrap-colorpicker/js/bootstrap-colorpicker.min.js}"></script>
    <!-- fullCalendar 2.2.5 -->
    <script th:src="@{/plugins/moment/moment.min.js}"></script>
    <script th:src="@{/plugins/fullcalendar/main.js}"></script>
    <!-- Select2 -->
    <script th:src="@{/plugins/select2/js/select2.full.min.js}"></script>
    <!-- Bootstrap4 Duallistbox -->
    <script th:src="@{/plugins/bootstrap4-duallistbox/jquery.bootstrap-duallistbox.min.js}"></script>
    <!-- Page specific script -->
    <script>
      $(function () {
        $("#example1").DataTable({
          "responsive": true, "lengthChange": false, "autoWidth": false,
          "buttons": ["copy", "csv", "excel", "pdf", "print", "colvis"]
        }).buttons().container().appendTo('#example1_wrapper .col-md-6:eq(0)');
        $('#example2').DataTable({
          "paging": true,
          "lengthChange": false,
          "searching": false,
          "ordering": true,
          "info": true,
          "autoWidth": false,
          "responsive": true,
        });
      });
    </script>
    <!--Js customize-->
    <script th:src="@{/js/Common.js}"></script>
    <script th:src="@{/js/APIHelper.js}"></script>
    <script th:src="@{/js/Modal.js}"></script>
    <script th:src="@{/js/Utils.js}"></script>
  </div>

</body>

</html>