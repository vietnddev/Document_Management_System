<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Group account</title>
    <th:block th:replace="header :: stylesheets"></th:block>
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
                    <div class="row">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-header">
                                    <div class="row justify-content-between">
                                        <div class="col-4" style="display: flex; align-items: center">
                                            <h3 class="card-title"><strong>GROUP ACCOUNT</strong></h3>
                                        </div>
                                        <div class="col-6 text-right">
                                            <button type="button" class="btn btn-success btn-insert"><i class="fa-solid fa-circle-plus mr-2"></i>Thêm mới</button>
                                        </div>
                                    </div>
                                </div>
                                <div class="card-body p-0">
                                    <table class="table table-bordered table-striped">
                                        <thead>
                                            <tr>
                                                <th>STT</th>
                                                <th>Group name</th>
                                                <th>Note</th>
                                                <th></th>
                                            </tr>
                                        </thead>
                                        <tbody id="contentTable"></tbody>
                                        <tfoot>
                                            <tr>
                                                <th>STT</th>
                                                <th>Group name</th>
                                                <th>Note</th>
                                                <th></th>
                                            </tr>
                                        </tfoot>
                                    </table>
                                </div>
                                <div class="card-footer">
                                    <div th:replace="fragments :: pagination"></div>
                                </div>

                                <!--Modal insert/update-->
                                <div class="modal fade" id="modal_insert_update">
                                    <div class="modal-dialog modal-lg">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <strong class="modal-title" id="modal_insert_update_title"></strong>
                                                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                            </div>
                                            <div class="modal-body">
                                                <div class="row">
                                                    <div class="col-12">
                                                        <div class="form-group">
                                                            <label for="groupCodeField">Group code</label>
                                                            <input type="text" class="form-control" id="groupCodeField"/>
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="groupNameField">Group name</label>
                                                            <input type="text" class="form-control" id="groupNameField" required/>
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="noteField">Note</label>
                                                            <textarea class="form-control" rows="5" id="noteField"></textarea>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="modal-footer justify-content-end">
                                                <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                                                <button type="button" class="btn btn-primary" id="btn-insert-update-submit">Lưu</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <!--Modal insert/update-->

                                <!--Modal rights-->
                                <div class="modal fade" id="modal_rights">
                                    <div class="modal-dialog modal-lg">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <strong class="modal-title" id="modal_rights_title"></strong>
                                                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                            </div>
                                            <div class="modal-body">
                                                <div class="row">
                                                    <div class="card w-100">
                                                        <div class="card-body p-0" style="max-height: 467px; overflow: overlay">
                                                            <table class="table table-bordered">
                                                                <tbody id="tblRights"></tbody>
                                                            </table>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="modal-footer justify-content-end">
                                                <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                                                <button type="button" class="btn btn-primary" id="btn-rights-submit">Lưu</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <!--Modal rights-->
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </div>

        <div th:replace="modal_fragments :: confirm_modal"></div>

        <div th:replace="footer :: footer"></div>

        <aside class="control-sidebar control-sidebar-dark"></aside>

        <div th:replace="header :: scripts"></div>
    </div>
    <script type="text/javascript">
        let mvGroups = {};
        let mvRights = [];
        let mvId = 0;
        let mvCode = $("#groupCodeField");
        let mvName = $("#groupNameField");
        let mvNote = $("#noteField");

        $(document).ready(function() {
            loadGroupAccounts(mvPageSizeDefault, 1);
            updateTableContentWhenOnClickPagination(loadGroupAccounts);

            preCreateGroupAcc();
            preUpdateGroupAcc();
            submitInsertOrUpdate();
            deleteGroupAcc();
            loadRightsOfGroup();
            grantRights();
        });

        function preCreateGroupAcc() {
            $(document).on("click", ".btn-insert", function () {
                $("#modal_insert_update_title").text("Add new account group");
                mvCode.val("");
                mvName.val("");
                mvNote.val("");
                $("#btn-insert-update-submit").attr("actionType", "insert");
                $("#modal_insert_update").modal();
            });
        }

        function preUpdateGroupAcc() {
            $(document).on("click", ".btn-update", function () {
                let group = mvGroups[$(this).attr("id")];
                $("#modal_insert_update_title").text("Update group account");
                mvId = group.id;
                mvCode.val(group.groupCode);
                mvName.val(group.groupName);
                mvNote.val(group.note);
                $("#btn-insert-update-submit").attr("actionType", "update");
                $("#modal_insert_update").modal();
            });
        }

        function submitInsertOrUpdate() {
            $("#btn-insert-update-submit").on("click", function () {
                let actionType = $(this).attr("actionType");
                let group = {id : mvId, groupCode : mvCode.val(), groupName : mvName.val(), note : mvNote.val()};
                if (actionType === "insert") {
                    $.ajax({
                        url: mvHostURLCallApi + "/sys/group-account/create",
                        type: "POST",
                        contentType: "application/json",
                        data: JSON.stringify(group),
                        success: function(response, a, b) {
                            if (response.status === "OK") {
                                alert("Create successfully!");
                                window.location.reload();
                            }
                        },
                        error: function (xhr) {
                            alert("Error: " + $.parseJSON(xhr.responseText).message);
                        }
                    });
                }
                if (actionType === "update") {
                    $.ajax({
                        url: mvHostURLCallApi + "/sys/group-account/update/" + mvId,
                        type: "PUT",
                        contentType: "application/json",
                        data: JSON.stringify(group),
                        success: function(response) {
                            if (response.status === "OK") {
                                alert("Update successfully!");
                                window.location.reload();
                            }
                        },
                        error: function (xhr) {
                            alert("Error: " + $.parseJSON(xhr.responseText).message);
                        }
                    });
                }
            });
        }

        function deleteGroupAcc() {
            $(document).on("click", ".btn-delete", function () {
                let group = mvGroups[$(this).attr("id")];
                mvId = group.id;
                $(this).attr("actionType", "delete");
                $(this).attr("entityName", group.groupName);
                showConfirmModal($(this), "Delete group account", "Are you sure to delete: " + group.groupName);
            });

            $('#yesButton').on("click", function () {
                let apiURL = mvHostURLCallApi + "/sys/group-account/delete/" + mvId;
                callApiDelete(apiURL);
            });
        }

        function loadRightsOfGroup() {
            $(document).on("click", ".btn-rights", function () {
                let group = mvGroups[$(this).attr("id")];
                $("#btn-rights-submit").attr("groupId", group.id);
                $("#modal_rights_title").text("System's rights of " + group.groupName);
                let apiURL = mvHostURLCallApi + '/sys/group-account/' + group.id + '/rights';
                $.get(apiURL, function (response) {
                    if (response.status === "OK") {
                        let data = response.data;
                        let contentTable = $('#tblRights');
                        contentTable.empty();
                        contentTable.append(`
                            <tr class="font-weight-bold">
                                <td>STT</td>
                                <td>Module</td>
                                <td>Function</td>
                                <td>Allowed</td>
                            </tr>
                        `);
                        $.each(data, function (index, d) {
                            //mvRights[d.groupId] = d;
                            mvRights.push(d);
                            let isAllowed = d.isAuthor ? "checked" : "";
                            contentTable.append(`
                                <tr>
                                    <td>${index + 1}</td>
                                    <td>${d.module.moduleLabel}</td>
                                    <td>${d.action.actionLabel}</td>
                                    <td>
                                        <input type="checkbox" style="width: 25px; height: 25px" id="isAuthorCbx_${d.action.actionKey}" ${isAllowed}>
                                    </td>
                                </tr>
                            `);
                        });
                    }
                }).fail(function () {
                    showErrorModal("Could not connect to the server");
                });

                $("#modal_rights").modal();
            });
        }

        function grantRights() {
            $(document).on("click", "#btn-rights-submit", function () {
                let groupId = mvRights[$(this).attr("groupId")].groupId;
                $.each(mvRights, function (index, value) {
                    value.isAuthor = $("[id^='isAuthorCbx_" + value.action.actionKey + "']").prop("checked");
                })
                $.ajax({
                    url: mvHostURLCallApi + "/sys/group-account/grant-rights/" + groupId,
                    type: "PUT",
                    contentType: "application/json",
                    data: JSON.stringify(mvRights),
                    success: function(response) {
                        if (response.status === "OK") {
                            alert("Update successfully!");
                            window.location.reload();
                        }
                    },
                    error: function (xhr) {
                        alert("Error: " + $.parseJSON(xhr.responseText).message);
                    }
                });
            });
        }

        function loadGroupAccounts(pageSize, pageNum) {
            let apiURL = mvHostURLCallApi + '/sys/group-account/all';
            let params = {pageSize: pageSize, pageNum: pageNum}
            $.get(apiURL, params, function (response) {
                if (response.status === "OK") {
                    let data = response.data;
                    let pagination = response.pagination;

                    updatePaginationUI(pagination.pageNum, pagination.pageSize, pagination.totalPage, pagination.totalElements);

                    let contentTable = $('#contentTable');
                    contentTable.empty();
                    $.each(data, function (index, d) {
                        mvGroups[d.id] = d;
                        contentTable.append(`
                            <tr>
                                <td>${(((pageNum - 1) * pageSize + 1) + index)}</td>
                                <td>${d.groupName}</td>
                                <td>${d.note}</td>
                                <td>
                                    <button class="btn btn-info    btn-sm btn-update mr-1"  id="${d.id}"><i class="fa-solid fa-pencil"></i></button>
                                    <button class="btn btn-primary btn-sm btn-rights mr-1"  id="${d.id}"><i class="fa-solid fa-share-nodes"></i></button>
                                    <button class="btn btn-danger  btn-sm btn-delete"       id="${d.id}"><i class="fa-solid fa-trash"></i></button>
                                </td>
                            </tr>
                        `);
                    });
                }
            }).fail(function () {
                showErrorModal("Could not connect to the server");
            });
        }
    </script>
</body>
</html>