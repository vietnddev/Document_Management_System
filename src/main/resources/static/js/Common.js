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
        console.log($(this).val())
        if (pageSize === $(this).val()) {
            return;
        }
        pageSize = $(this).val();
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
        console.log("Next page: pageSize ", pageSize, " pageNum, ", pageNum, " ,lvTotalPage ", totalPage, " ,lvTotalElements ", totalElements)
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
    let brandFilter = $('#brandFilter');
    let productTypeFilter = $('#productTypeFilter');
    let colorFilter = $('#colorFilter');
    let sizeFilter = $('#sizeFilter');
    let unitFilter = $('#unitFilter');
    let discountFilter = $('#discountFilter');
    let productStatusFilter = $('#productStatusFilter');

    $("#btnOpenSearchAdvance").on("click", function () {
        brandFilter.empty();
        brandFilter.append("<option>Chọn nhãn hiệu</option>");
        productTypeFilter.empty();
        productTypeFilter.append("<option>Chọn loại sản phẩm</option>");
        colorFilter.empty();
        colorFilter.append("<option>Chọn màu sắc</option>");
        sizeFilter.empty();
        sizeFilter.append("<option>Chọn kích cỡ</option>");
        unitFilter.empty();
        unitFilter.append("<option>Chọn đơn vị tính</option>");
        discountFilter.empty();
        discountFilter.append("<option>Chọn khuyến mãi</option>");
        productStatusFilter.empty();
        productStatusFilter.append("<option>Chọn trạng thái sản phẩm</option>");

        $.each(keySearch, function (index, key) {
            if (key === "BRAND") {
                $.get(mvHostURLCallApi + '/category/brand', function (response) {
                    if (response.status === "OK") {
                        $.each(response.data, function (index, d) {
                            brandFilter.append('<option value=' + d.id + '>' + d.name + '</option>');
                            console.log(d)
                        });
                    }
                }).fail(function () {
                    showErrorModal("Could not connect to the server");
                });
            }
            if (key === "PRODUCT_TYPE") {
                $.get(mvHostURLCallApi + '/category/product-type', function (response) {
                    if (response.status === "OK") {
                        $.each(response.data, function (index, d) {
                            productTypeFilter.append('<option value=' + d.id + '>' + d.name + '</option>');
                            console.log(d)
                        });
                    }
                }).fail(function () {
                    showErrorModal("Could not connect to the server");
                });
            }
            if (key === "COLOR") {
                $.get(mvHostURLCallApi + '/category/color', function (response) {
                    if (response.status === "OK") {
                        $.each(response.data, function (index, d) {
                            colorFilter.append('<option value=' + d.id + '>' + d.name + '</option>');
                            console.log(d)
                        });
                    }
                }).fail(function () {
                    showErrorModal("Could not connect to the server");
                });
            }
            if (key === "SIZE") {
                $.get(mvHostURLCallApi + '/category/size', function (response) {
                    if (response.status === "OK") {
                        $.each(response.data, function (index, d) {
                            sizeFilter.append('<option value=' + d.id + '>' + d.name + '</option>');
                            console.log(d)
                        });
                    }
                }).fail(function () {
                    showErrorModal("Could not connect to the server");
                });
            }
            if (key === "UNIT") {
                $.get(mvHostURLCallApi + '/category/unit', function (response) {
                    if (response.status === "OK") {
                        $.each(response.data, function (index, d) {
                            unitFilter.append('<option value=' + d.id + '>' + d.name + '</option>');
                            console.log(d)
                        });
                    }
                }).fail(function () {
                    showErrorModal("Could not connect to the server");
                });
            }
            if (key === "DISCOUNT") {
                discountFilter.append(`<option value="Y">Đang áp dụng</option><option value="N">Không áp dụng</option>`);
            }
            if (key === "PRODUCT_STATUS") {
                productStatusFilter.append(`<option value="ACTIVE">Đang kinh doanh</option><option value="INACTIVE">Không kinh doanh</option>`);
            }
        })
    })
}

function loadFolderTree() {
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