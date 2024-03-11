<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Flowiee Official | Thông tin cá nhân</title>
    <th:block th:replace="header :: stylesheets"></th:block>
    <style>
        .row {
            margin-left: 0px;
            margin-right: 0px;
        }

        img {
            border-radius: 5px;
        }

        .product-image-thumb {
            border: none;
            box-shadow: none;
            max-width: 6.5rem;
            margin-right: 0rem;
        }
    </style>
</head>

<body class="hold-transition sidebar-mini">
<!-- Site wrapper -->
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
            <div class="container-fluid row">
                <div class="col-md-3">
                    <!-- Profile FileEntity -->
                    <div class="card card-primary card-outline">
                        <div class="card-body box-profile">
                            <div class="text-center">
                                <img class="profile-user-img img-fluid img-circle" src="../../dist/img/user4-128x128.jpg" alt="User profile picture">
                            </div>
                            <h3 class="profile-username text-center" th:text="${profile.fullName}"></h3>
                            <p class="text-muted text-center">Software Engineer</p>
                        </div>
                        <!-- /.card-body -->
                    </div>
                    <!-- /.card -->

                </div>
                <!-- /.col -->
                <div class="col-md-9">
                    <div class="card">
                        <div class="card-header p-2">
                            <ul class="nav nav-pills">
                                <li class="nav-item"><a class="nav-link active" href="#THONG_TIN_CHUNG" data-toggle="tab">Thông tin</a></li>
                                <li class="nav-item"><a class="nav-link" href="#DOI_MAT_KHAU" data-toggle="tab">Đổi mật khẩu</a></li>
                                <li class="nav-item"><a class="nav-link" href="#LICH_SU_BAN_HANG" data-toggle="tab">Lịch sử bán hàng</a></li>
                            </ul>
                        </div><!-- /.card-header -->
                        <div class="card-body">
                            <div class="tab-content">
                                <div class="active tab-pane" id="THONG_TIN_CHUNG">
                                    <form class="form-horizontal" th:action="@{/profile/update}"
                                          th:object="${account}" method="post">
                                        <div class="form-group row">
                                            <label class="col-sm-2 col-form-label">Họ tên</label>
                                            <div class="col-sm-10">
                                                <input type="text" class="form-control" placeholder="Họ tên" name="fullName" th:value="${profile.fullName}"/>
                                            </div>
                                        </div>
                                        <div class="form-group row">
                                            <label class="col-sm-2 col-form-label">Email</label>
                                            <div class="col-sm-10">
                                                <input type="email" class="form-control" placeholder="Email" name="email" th:value="${profile.email}"/>
                                            </div>
                                        </div>
                                        <div class="form-group row">
                                            <label class="col-sm-2 col-form-label">Số điện thoại</label>
                                            <div class="col-sm-10">
                                                <input type="text" class="form-control" placeholder="Số điện thoại" name="phoneNumber" th:value="${profile.phoneNumber}"/>
                                            </div>
                                        </div>
                                        <div class="form-group row">
                                            <label class="col-sm-2 col-form-label">Giới tính</label>
                                            <div class="col-sm-10">
                                                <select class="custom-select" name="sex" th:if="${profile.sex == true}">
                                                    <option value="true" selected>Nam</option>
                                                    <option value="false">Nữ</option>
                                                </select>
                                                <select class="custom-select" name="sex" th:if="${profile.sex == false}">
                                                    <option value="true">Nam</option>
                                                    <option value="false" selected>Nữ</option>
                                                </select>
                                            </div>
                                        </div>
                                        <div class="form-group row">
                                            <div class="offset-sm-2 col-sm-10">
                                                <button type="submit" class="btn btn-info">Lưu</button>
                                            </div>
                                        </div>
                                    </form>
                                </div>

                                <!-- tab đổi mật khẩu time label -->
                                <div class="tab-pane" id="DOI_MAT_KHAU">
                                    <form id="changePassword" class="form-horizontal"
                                          th:action="@{/profile/change-password}"
                                          th:object="${account}" method="post">
                                        <div class="form-group row">
                                            <label class="col-sm-4 col-form-label">Mật khẩu cũ</label>
                                            <div class="col-sm-8">
                                                <input type="password" class="form-control" name="password_old"/>
                                            </div>
                                        </div>
                                        <div class="form-group row">
                                            <label class="col-sm-4 col-form-label">Mật khẩu mới</label>
                                            <div class="col-sm-8">
                                                <input type="password" class="form-control" name="password_new"/>
                                            </div>
                                        </div>
                                        <div class="form-group row">
                                            <label class="col-sm-4 col-form-label">Nhập lại mật khẩu mới</label>
                                            <div class="col-sm-8">
                                                <input type="password" class="form-control" name="password_renew"/>
                                            </div>
                                        </div>
                                        <div class="form-group" style="margin: auto">
                                            <button id="submitButton" type="submit" class="btn btn-info">Lưu
                                            </button>
                                        </div>
                                        <div class="row mt-3">
                                            <label>[[${message}]]</label>
                                        </div>
                                    </form>
                                </div>

                                <!-- /.tab-pane -->
                                <div class="tab-pane" id="LICH_SU_BAN_HANG">
                                    <div class="row w-100" style="max-height: 565px; overflow: scroll">
                                        <table class="table table-bordered table-striped align-items-center">
                                            <thead class="align-self-center">
                                                <tr class="align-self-center">
                                                    <th>STT</th>
                                                    <th>Mã đơn hàng</th>
                                                    <th>Thời gian đặt hàng</th>
                                                    <th>Địa chỉ giao hàng</th>
                                                    <th>Khách hàng</th>
                                                    <th>Kênh bán hàng</th>
                                                    <th>Trạng thái</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                            <tr th:each="list, index : ${listDonHangDaBan}">
                                                <td th:text="${index.index + 1}"></td>
                                                <td>
                                                    <a th:href="@{/don-hang/{id}(id=${list.id})}"
                                                       th:text="${list.code}"></a>
                                                </td>
                                                <td th:text="${list.orderTime}"></td>
                                                <td th:text="${list.receiverAddress}"></td>
                                                <td th:text="${list.receiverName}"></td>
                                                <td th:text="${list.kenhBanHang.name}"></td>
                                                <td th:text="${list.trangThaiDonHang.name}"></td>
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
        </section>
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
<!-- ./wrapper -->

<script>
    $(document).ready(function () {
        $('.product-image-thumb').on('click', function () {
            var $image_element = $(this).find('img')
            $('.product-image').prop('src', $image_element.attr('src'))
            $('.product-image-thumb.active').removeClass('active')
            $(this).addClass('active')
        })
    })
</script>

<script> // Upload file
// DropzoneJS Demo Code Start
Dropzone.autoDiscover = true

// Get the template HTML and remove it from the doumenthe template HTML and remove it from the doument
var previewNode = document.querySelector("#template")
previewNode.id = ""
var previewTemplate = previewNode.parentNode.innerHTML
previewNode.parentNode.removeChild(previewNode)

var myDropzone = new Dropzone(document.body, { // Make the whole body a dropzone
    url: "/upload/sales/products/variants/[[${productVariantID}]]", // Gọi tới function trong spring để xử lý file
    thumbnailWidth: 80,
    thumbnailHeight: 80,
    parallelUploads: 20,
    previewTemplate: previewTemplate,
    autoQueue: false, // Make sure the files aren't queued until manually added
    previewsContainer: "#previews", // Define the container to display the previews
    clickable: ".fileinput-button", // Define the element that should be used as click trigger to select files.
})

myDropzone.on("addedfile", function (file) {
    // Hookup the start button
    file.previewElement.querySelector(".start").onclick = function () {
        myDropzone.enqueueFile(file)
    }
})

// Update the total progress bar
myDropzone.on("totaluploadprogress", function (progress) {
    document.querySelector("#total-progress .progress-bar").style.width = progress + "%"
})

myDropzone.on("sending", function (file) {
    // Show the total progress bar when upload starts
    document.querySelector("#total-progress").style.opacity = "1"
    // And disable the start button
    file.previewElement.querySelector(".start").setAttribute("disabled", "disabled")
})

// Hide the total progress bar when nothing's uploading anymore
myDropzone.on("queuecomplete", function (progress) {
    document.querySelector("#total-progress").style.opacity = "0"
})

// Setup the buttons for all transfers
// The "add files" button doesn't need to be setup because the config
// `clickable` has already been specified.
document.querySelector("#actions .start").onclick = function () {
    myDropzone.enqueueFiles(myDropzone.getFilesWithStatus(Dropzone.ADDED))
}
document.querySelector("#actions .cancel").onclick = function () {
    myDropzone.removeAllFiles(true)
}
// DropzoneJS Demo Code End
</script>

<script th:inline="javascript">
    function onSubmit() {
        // submit form
        document.getElementById("changePassword").submit();

        // hiển thị thông báo
        var notification = document.createElement('div');
        notification.innerHTML = 'Hoàn thành!';

        // cài đặt CSS cho thông báo
        notification.style.position = 'fixed';
        notification.style.bottom = '20px';
        notification.style.right = '20px';
        notification.style.padding = '10px';
        notification.style.backgroundColor = '#4CAF50';
        notification.style.color = 'white';
        notification.style.borderRadius = '5px';

        // thêm thông báo vào trang web và tự động ẩn nó sau 3 giây
        document.body.appendChild(notification);
        setTimeout(function () {
            notification.style.display = "none"
        }, 60000);
    }

    // gán hàm xử lý sự kiện cho nút submit
    document.getElementById("submitButton").addEventListener("click", onSubmit);
</script>
</body>

</html>
