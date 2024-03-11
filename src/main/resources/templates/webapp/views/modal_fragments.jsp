<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
    <body>
        <div th:fragment="dialog_modal" class="modal fade" id="dialogModal"><!-- bootstrap modal -->
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <strong class="modal-title" id="modalTitle">Thông báo</strong>
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                    </div>

                    <div class="modal-body">
                        <span id="modalBody"></span>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-sm btn-primary" data-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>

        <div th:fragment="confirm_modal" class="modal fade" id="confirmModal"><!-- bootstrap modal -->
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <strong class="modal-title" id="confirmTitle">Xác nhận xóa</strong>
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                    </div>

                    <div class="modal-body">
                        <span id="confirmText"></span>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-sm btn-primary" href="" id="yesButton">Yes</button>
                        <button type="button" class="btn btn-sm btn-default" data-dismiss="modal">No</button>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>