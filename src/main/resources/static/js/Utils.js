let mvFormatCurrency = (currencyInput) => {
    return currencyInput.replace(/,/g, '');
}

let formatCurrency = (number) =>  {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(number);
}

//Change language
$("#langOptionVi").click(function () {
    mvLang = "vi";
    window.location.replace("?lang=vi");
})

$("#langOptionEn").click(function () {
    mvLang = "en";
    window.location.replace("?lang=en");
})