<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Flowiee Official | Đăng nhập hệ thống</title>
    <div th:replace="header :: stylesheets">
        <!--Nhúng các file css, icon,...-->
    </div>
</head>

<body class="hold-transition login-page"
      th:style="'background-image: url(\'' + @{/dist/img/bg_login_3.png} + '\'); background-size: cover; background-position: center; background-repeat: no-repeat;'">
    <div class="login-box"
        style="opacity: 90%">
        <div class="login-logo">
            <h2 class="text-uppercase font-weight-bold text-light">Flowiee System</h2>
        </div>
        <!-- /.login-logo -->
        <div class="card">
            <div class="card-body login-card-body" style="border-radius: 20px">
                <form th:action="@{/j_spring_security_check}" method="post">
                    <div class="input-group mb-3">
                        <input type="text" class="form-control" name="username" id="username" placeholder="Username" required/>
                        <div class="input-group-append">
                            <div class="input-group-text">
                                <span class="fas fa-user"></span>
                            </div>
                        </div>
                    </div>
                    <div class="input-group mb-3">
                        <input type="password" class="form-control" name="password" id="password" placeholder="Password" required/>
                        <div class="input-group-append">
                            <div class="input-group-text">
                                <span class="fas fa-lock"></span>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-3"></div>
                        <!-- /.col -->
                        <div class="col-6">
                            <button type="submit" class="btn btn-sm btn-primary btn-block">Login</button>
                        </div>
                        <!-- /.col -->
                        <div class="col-3"></div>
                    </div>
                </form>
            </div>
            <!-- /.login-card-body -->
        </div>
    </div>
    <!-- /.login-box -->

    <div th:replace="header :: scripts">
        <!-- Nhúng các file JavaScript vào -->
    </div>
</body>

</html>