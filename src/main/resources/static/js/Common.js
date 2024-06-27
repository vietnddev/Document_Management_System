//Host
const mvHostURLCallApi = 'http://localhost:8086/api/v1';
const mvHostURL = 'http://localhost:8086';

//Language to use
let mvLang = "vi";

//User login info
const mvCurrentAccountId = '';
const mvCurrentAccountUsername = '';

//Config product status
const mvProductStatus = {};
mvProductStatus["A"] = "Đang kinh doanh";
mvProductStatus["I"] = "Ngừng kinh doanh";

//Config ticket import status
const mvTicketImportStatus = {};
mvTicketImportStatus["DRAFT"] = "Nháp";
mvTicketImportStatus["COMPLETED"] = "Hoàn thành";
mvTicketImportStatus["CANCEL"] = "Hủy";

//Config ticket export status
const mvTicketExportStatus = {};
mvTicketExportStatus["DRAFT"] = "Nháp";
mvTicketExportStatus["COMPLETED"] = "Hoàn thành";
mvTicketExportStatus["CANCEL"] = "Hủy";

//Pagination
const mvPageSizeDefault = 10;
function updatePaginationUI(pageNum, pageSize, totalPage, totalElements) {
    $('#paginationInfo').attr("pageNum", pageNum);
    $('#paginationInfo').attr("pageSize", pageSize);
    $('#paginationInfo').attr("totalPage", totalPage);
    $('#paginationInfo').attr("totalElements", totalElements);
    $('#pageNum').text(pageNum);

    let startCount = (pageNum - 1) * pageSize + 1;
    let endCount = startCount + pageSize - 1;
    if (endCount > totalElements) {
        endCount = totalElements;
    }
    $('#paginationInfo').text("Showing " + startCount + " to " + endCount + " of " + totalElements + " entries");

    $('#totalPages').text("Total pages " + totalPage);
}

function updateTableContentWhenOnClickPagination(loadNewDataMethod, pageSize, pageNum, totalPage, totalElements) {
    $('#selectPageSize').on('click', function() {
        if (pageSize === parseInt($(this).val())) {
            return;
        }
        pageSize = parseInt($(this).val());
        loadNewDataMethod($(this).val(), 1);
    });

    $('#firstPage').on('click', function() {
        if (pageNum === 1) {
            return;
        }
        loadNewDataMethod(pageSize, 1);
    });

    $('#previousPage').on('click', function() {
        if (pageNum === 1) {
            return;
        }
        loadNewDataMethod(pageSize, pageNum - 1);
    });

    $('#nextPage').on('click', function() {
        if (pageNum === totalPage) {
            return;
        }
        if (totalElements <= pageSize) {
            return;
        }
        loadNewDataMethod(pageSize, pageNum + 1);
    });

    $('#lastPage').on('click', function() {
        if (pageNum === totalPage) {
            return;
        }
        if (totalElements <= pageSize) {
            return;
        }
        loadNewDataMethod(pageSize, totalPage);
    });
}

//Search tool
function setupSearchTool(keySearch) {
    let documentTypeFilter = $('#documentTypeFilter');

    $("#btnOpenSearchAdvance").on("click", function () {
        documentTypeFilter.empty();
        documentTypeFilter.append("<option>Chọn loại tài liệu</option>");

        $.each(keySearch, function (index, key) {
            if (key === "DOCUMENT_TYPE") {
                $.get(mvHostURLCallApi + '/category/document-type', function (response) {
                    if (response.status === "OK") {
                        $.each(response.data, function (index, d) {
                            documentTypeFilter.append('<option value=' + d.id + '>' + d.name + '</option>');
                            console.log(d)
                        });
                    }
                }).fail(function () {
                    showErrorModal("Could not connect to the server");
                });
            }
        })
    })
}

function loadFolderTreeOnSideBar() {
    // Bắt sự kiện click cho các button có class bắt đầu bằng "nav-link folder-"
    $(document).on('click', 'a[class^="nav-link folder-"]', function() {
        if ($(this).attr("hasSubFolder") === "N") {
            return;
        }
        if ($(this).attr("collapse") === "Y") {
            return;
        }
        let aClass = $(this).attr('class');
        let folderId = aClass.split('-')[2];

        let subFolders = $('#sub-folders-' + folderId);
        subFolders.empty();

        $.get(mvHostURLCallApi + '/stg/doc/folders', {parentId: folderId}, function (response) {
            let subFoldersData = response.data;
            $.each(subFoldersData, function (index, d) {
                let iconDropdownList = ``;
                if (d.hasSubFolder === "Y") {
                    iconDropdownList = `<i class="fas fa-angle-left right"></i>`;
                }
                subFolders.append(`
                    <li class="nav-item">
                        <a href="#" class="nav-link folder-${d.id}" hasSubFolder="${d.hasSubFolder}" collapse="N">
                            <p>${d.name} ${iconDropdownList}</p>
                        </a>
                        <ul class="nav nav-treeview" id="sub-folders-${d.id}" style="margin-left: 15px"></ul>
                    </li>
                `);
            })
        }).fail(function () {
            showErrorModal("Could not connect to the server");
        });
        $(this).attr("collapse", "Y");
    });
}