<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Flowiee | System category</title>
    <div th:replace="header :: stylesheets"></div>
    <style rel="stylesheet">
        .table td, th {
            vertical-align: middle;
        }
    </style>
</head>

<body class="hold-transition sidebar-mini layout-fixed">
    <div class="wrapper">
        <div th:replace="header :: header"></div>

        <div th:replace="sidebar :: sidebar"></div>

        <div class="content-wrapper" style="padding-top: 10px; padding-bottom: 1px;">
            <section class="content">
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-md-3 col-sm-6 col-12 mb-3" style="height: 200px; border-radius:  10px;" th:each="c, index : ${listCategory}">
                            <a th:href="@{${c.endpoint}}">
                                <div class="info-box bg-success h-100" style="border-radius: 20px; opacity: 95%">
                                    <span class="info-box-icon"><i th:class="${c.icon}"></i></span>
                                    <div class="info-box-content">
                                        <span class="info-box-text font-weight-bold text-uppercase" style="font-size: 20px" th:text="${c.name}"></span>
                                        <span class="info-box-number" th:text="${c.totalSubRecords}"></span>
                                        <div class="progress">
                                            <div class="progress-bar" style="width: 70%"></div>
                                        </div>
                                        <span class="progress-description text-wrap" th:text="${c.note}"></span>
                                    </div>
                                </div>
                            </a>
                        </div>
                    </div>
                </div>
            </section>
        </div>

        <div th:replace="footer :: footer"></div>

        <aside class="control-sidebar control-sidebar-dark"></aside>

        <div th:replace="header :: scripts"></div>
    </div>
</body>
</html>