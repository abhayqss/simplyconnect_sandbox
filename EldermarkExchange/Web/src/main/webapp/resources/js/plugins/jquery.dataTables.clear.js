$.fn.dataTableExt.oApi.fnClearTable = function (oSettings){
    /*remove records*/
    $(oSettings.nTBody).find('tr').remove();

    /*remove table data*/
    $(oSettings.nTable).dataTable().api().clear();

    /*remove paging buttons*/
    var $pager = $(oSettings.nTableWrapper).find('.dataTables_paginate');
    $pager.find('span .paginate_button').remove();

    /*add zero record*/
    var colTotal = oSettings.aoColumns.length;
    var zeroRecord = '<tr class="odd"><td colspan="' + colTotal + '" class="dataTables_empty">No results found.</td></tr>';
    $(oSettings.nTBody).append(zeroRecord);
};