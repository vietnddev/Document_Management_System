function init() {
    createListener();
}

function createListener() {
    tickAllCheckbox();
    tickEachCheckbox();
}

function tickAllCheckbox() {
    $(document).on('click', '#tickAllCbx', function() {
        let isChecked = $(this).is(':checked');
        $(`[name^='tickCbx']`).each(function() {
            $(this).prop("checked", isChecked);
            let docId = $(this).attr("docId");
            if (isChecked) {
                mvListOfSelectedDocuments.push(docId);
            } else {
                mvListOfSelectedDocuments = mvListOfSelectedDocuments.filter(function (item) {
                    return item !== docId;
                })
            }
        });
    });
}

function tickEachCheckbox() {
    $(document).on('click', `[name^='tickCbx']`, function() {
        let docId = $(this).attr("docId");
        let isChecked = $(this).is(':checked');
        if (isChecked) {
            mvListOfSelectedDocuments.push(docId);
        } else {
            mvListOfSelectedDocuments = mvListOfSelectedDocuments.filter(function (item) {
                return item !== docId;
            })
        }
    });
}