function beforeSubmitShareRights() {
    $(document).on("change", "[id^='canUpdateCbx_']", function () {
        let accId = parseInt($(this).attr("id").split("_")[1]);
        if ($(this).is(':checked')) {
            $(`[id^='canReadCbx_${accId}']`).prop("checked", true);
        } else {
            unCheckReadRightWhenRemoveAllRight(accId, $(this).attr("indexAcc"));
        }
    })
    $(document).on("change", "[id^='canDeleteCbx_']", function () {
        let accId = $(this).attr("id").split("_")[1];
        if ($(this).is(':checked')) {
            $(`[id^='canReadCbx_${accId}']`).prop("checked", true);
        } else {
            unCheckReadRightWhenRemoveAllRight(accId, $(this).attr("indexAcc"));
        }
    })
    $(document).on("change", "[id^='canMoveCbx_']", function () {
        let accId = $(this).attr("id").split("_")[1];
        if ($(this).is(':checked')) {
            $(`[id^='canReadCbx_${accId}']`).prop("checked", true);
        } else {
            unCheckReadRightWhenRemoveAllRight(accId, $(this).attr("indexAcc"));
        }
    })
    $(document).on("change", "[id^='canShareCbx_']", function () {
        let accId = $(this).attr("id").split("_")[1];
        if ($(this).is(':checked')) {
            $(`[id^='canReadCbx_${accId}']`).prop("checked", true);
        } else {
            unCheckReadRightWhenRemoveAllRight(accId, $(this).attr("indexAcc"));
        }
    })
}

function unCheckReadRightWhenRemoveAllRight(accId, indexAcc) {
    let defaultAccCanRead = mvAccountShares[indexAcc].canRead;
    let cbxCanUpdate = $(`[id^='canUpdateCbx_${accId}']`);
    let cbxCanDelete = $(`[id^='canDeleteCbx_${accId}']`);
    let cbxCanMove = $(`[id^='canMoveCbx_${accId}']`);
    let cbxCanShare = $(`[id^='canShareCbx_${accId}']`);
    if (!defaultAccCanRead && (!cbxCanUpdate.is(':checked') && !cbxCanDelete.is(':checked') && !cbxCanMove.is(':checked') && !cbxCanShare.is(':checked'))) {
        $(`[id^='canReadCbx_${accId}']`).prop("checked", false);
    }
}

function shareDoc() {
    $(document).on("click", ".btn-share", function () {
        let documentId = parseInt($(this).attr("docId"));
        let apiURL = mvHostURLCallApi + '/stg/doc/share/' + documentId;
        $.get(apiURL, function (response) {
            if (response.status === "OK") {
                mvAccountShares = response.data;
                let tableShare = $("#tblSysAccountShare");
                tableShare.empty();
                $.each(mvAccountShares, function (index, d) {
                    tableShare.append(`
                                <tr>
                                    <td>${d.accountName}</td>
                                    <td><input class="form-control form-control-sm" type="checkbox" indexAcc="${index}" id="canReadCbx_${d.accountId}" ${d.canRead ? "checked" : ""}></td>
                                    <td><input class="form-control form-control-sm" type="checkbox" indexAcc="${index}" id="canUpdateCbx_${d.accountId}" ${d.canUpdate ? "checked" : ""}></td>
                                    <td><input class="form-control form-control-sm" type="checkbox" indexAcc="${index}" id="canDeleteCbx_${d.accountId}" ${d.canDelete ? "checked" : ""}></td>
                                    <td><input class="form-control form-control-sm" type="checkbox" indexAcc="${index}" id="canMoveCbx_${d.accountId}" ${d.canMove ? "checked" : ""}></td>
                                    <td><input class="form-control form-control-sm" type="checkbox" indexAcc="${index}" id="canShareCbx_${d.accountId}" ${d.canShare ? "checked" : ""}></td>
                                </tr>
                            `);
                })
            }
        }).fail(function () {
            showErrorModal("Could not connect to the server");
        });
        $("#btnSubmitShare").attr("documentId", documentId);
        $("#modalShare").modal();
    })

    $("#btnSubmitShare").on("click", function () {
        let documentId = parseInt($(this).attr("documentId"));
        $.each(mvAccountShares, function (index, d) {
            mvAccountShares[index].canRead = $("[id^='canReadCbx_']").eq(index).prop("checked");
            mvAccountShares[index].canUpdate = $("[id^='canUpdateCbx_']").eq(index).prop("checked");
            mvAccountShares[index].canDelete = $("[id^='canDeleteCbx_']").eq(index).prop("checked");
            mvAccountShares[index].canMove = $("[id^='canMoveCbx_']").eq(index).prop("checked");
            mvAccountShares[index].canShare = $("[id^='canShareCbx_']").eq(index).prop("checked");
        })
        $.ajax({
            url: mvHostURLCallApi + "/stg/doc/share/" + documentId,
            type: "PUT",
            contentType: "application/json",
            data: JSON.stringify(mvAccountShares),
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
    })
}