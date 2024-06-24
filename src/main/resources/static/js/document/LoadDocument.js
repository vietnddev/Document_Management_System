function loadDocuments(pageSize, pageNum) {
    let contentTable = $('#contentTable');
    contentTable.empty();
    let apiURL = mvHostURLCallApi + "/stg/doc/all";
    let params = {
        pageSize: pageSize,
        pageNum: pageNum,
        parentId: mvParentId,
        txtSearch: $("#txtFilter").val()
    }
    $.get(apiURL, params, function (response) {
        if (response.status === "OK") {
            let data = response.data;
            mvPagination = response.pagination;
            mvPageNum = parseInt(mvPagination.pageNum);
            mvPageSize = parseInt(mvPagination.pageSize);
            mvTotalPage = parseInt(mvPagination.totalPage);
            mvTotalElements = parseInt(mvPagination.totalElements);

            updatePaginationUI(mvPagination.pageNum, mvPagination.pageSize, mvPagination.totalPage, mvPagination.totalElements);

            $.each(data, function (index, d) {
                mvDocuments[d.id] = d;
                let iconDoc = d.isFolder === "Y" ? "/dist/icon/folder.png" : "/dist/icon/pdf.png";
                let btnUpdate = d.thisAccCanUpdate ? `<button class="btn btn-warning btn-sm btn-update" docId="${d.id}"> <i class="fa-solid fa-pencil"></i> </button>` : ``;
                let btnShare = d.thisAccCanShare ? `<button class="btn btn-info btn-sm btn-share" docId="${d.id}"> <i class="fa-solid fa-share"></i> </button>` : ``;
                let btnDelete = d.thisAccCanDelete ? `<button class="btn btn-danger btn-sm btn-delete" docId="${d.id}"> <i class="fa-solid fa-trash"></i> </button>` : ``;
                contentTable.append(`
                    <tr>
                        <td>${(((pageNum - 1) * pageSize + 1) + index)}</td>
                        <td><img src="${iconDoc}"></td>
                        <td>${d.createdAt}</td>
                        <td style="max-width: 300px"><a href="/stg/doc/${d.asName}-${d.id}">${d.name}</a></td>
                        <td>${d.docTypeName}</td>
                        <td>${d.description}</td>
                        <td>
                            <button class="btn btn-secondary btn-sm btn-copy" docId="${d.id}"> <i class="fa-solid fa-copy"></i> </button>
                            ${btnUpdate}
                            ${btnShare}
                            ${btnDelete}
                        </td>
                    </tr>
                `);
            });
        }
    }).fail(function () {
        showErrorModal("Could not connect to the server");
    });
}