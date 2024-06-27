<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
    <body>
        <!-- Modal confirm clone document -->
        <div class="modal fade" id="modalCloneDoc" th:fragment="modalCloneDocFragment">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-secondary">
                        <strong class="modal-title">Sao chép tài liệu</strong>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <p for="nameField">Tên mới</p>
                            <input class="form-control" type="text" placeholder="Tên tài liệu" id="docCloneNameField" maxlength="200"/>
                        </div>
                    </div>
                    <div class="modal-footer justify-content-end">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                        <button type="button" class="btn btn-primary" id="btnConfirmCloneDoc">Lưu</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal confirm export data -->
        <div class="modal fade" id="modalExportData" th:fragment="modalExportDataFragment">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-info">
                        <strong class="modal-title">Xuất dữ liệu</strong>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    </div>
                    <div class="modal-body">
                        Bạn muốn xuất dữ liệu kho?
                    </div>
                    <div class="modal-footer justify-content-end">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                        <button type="button" class="btn btn-primary" id="btnConfirmExportData">Lưu</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal share role -->
        <div class="modal fade" id="modalShare" th:fragment="modalShareFragment">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header bg-info">
                        <strong class="modal-title">Phân quyền tài liệu</strong>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <div class="card mb-0">
                            <div class="card-body table-responsive p-0">
                                <table class="table table-hover text-nowrap">
                                    <thead>
                                    <tr>
                                        <th>Account name</th>
                                        <th class="text-center">Read</th>
                                        <th class="text-center">Update</th>
                                        <th class="text-center">Delete</th>
                                        <th class="text-center">Move</th>
                                        <th class="text-center">Share</th>
                                    </tr>
                                    </thead>
                                    <tbody id="tblSysAccountShare"></tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer justify-content-end">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-primary" id="btnSubmitShare">Lưu</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal insert and update -->
        <div class="modal fade" id="modalInsertOrUpdate" th:fragment="modalInsertAndUpdateFragment">
            <div class="modal-dialog modal-lg">
                <form id="formInsertOrUpdate" enctype="multipart/form-data">
                    <div class="modal-content">
                        <div id="headerModalIU">
                            <strong class="modal-title" id="titleInsertOrUpdate"></strong>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            <div class="row">
                                <div class="col-12">
                                    <div class="form-group" id="docTypeBlock">
                                        <label for="docTypeField">Loại tài liệu</label>
                                        <select class="custom-select" id="docTypeField"></select>
                                    </div>
                                    <div class="form-group">
                                        <label for="nameField">Tên</label>
                                        <input class="form-control" type="text" placeholder="Tên loại tài liệu" id="nameField" maxlength="200"/>
                                    </div>
                                    <div class="form-group">
                                        <label for="desField">Mô tả</label>
                                        <textarea class="form-control" rows="5" placeholder="Mô tả" id="desField"></textarea>
                                    </div>
                                    <div class="form-group" id="fileBlock">
                                        <label for="fileField">File</label>
                                        <input class="form-control" type="file" id="fileField"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer justify-content-end">
                            <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-primary" id="btnSubmit">Lưu</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- Modal confirm clone document -->
        <div class="modal fade" id="modalMoveDoc" th:fragment="modalMoveDocFragment">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-success">
                        <strong class="modal-title">Di chuyển tài liệu</strong>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    </div>
                    <div class="modal-body">
                        <ul id="myUL">
                            <th:block th:each="ft : ${folderTree}">
                                <li th:if="${ft.hasSubFolder == 'N'}" th:text="${ft.name}" class="caret-name block-caret" th:docId="${ft.id}"></li>

                                <li th:if="${ft.hasSubFolder == 'Y'}">
                                    <span th:class="'caret docId-' + ${ft.id}" th:docId="${ft.id}"></span> <span class="caret-name block-caret" th:text="${ft.name}" th:docId="${ft.id}"></span>
                                    <ul th:class="'nested docId-' + ${ft.id}"></ul>
                                </li>
                            </th:block>
                        </ul>
                    </div>
                    <div class="modal-footer justify-content-end">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                        <button type="button" class="btn btn-primary" id="btnConfirmMoveDoc">Lưu</button>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>