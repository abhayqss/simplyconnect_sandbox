/**
 * Created by stsiushkevich on 11.09.18.
 */

function ServicePlanList(props) {
    Grid.apply(this, arguments);
}

ServicePlanList.prototype = Object.create(Grid.prototype);
ServicePlanList.prototype.constructor = ServicePlanList;

ServicePlanList.prototype.getDefaultProps = function () {
    var props = Grid.prototype.getDefaultProps.apply(this);

    var me = this;

    return $.extend({}, props, {
        id: 'servicePlanList',
        columns: [
            {
                name: 'status',
                title: 'Status',
                format: 'string'
            },
            {
                name: 'dateCreated',
                title: 'Date Started',
                format: 'date'
            },
            {
                name: 'dateCompleted',
                title: 'Date Completed',
                format: 'date'
            },
            {
                name: 'scoring',
                title: 'Scoring',
                format: 'string'
            },
            {
                name: 'author',
                title: 'Author',
                format: 'string'
            },
            {
                name: 'action',
                title: 'Action',
                format: 'fake',
                render: function () {
                    return ''
                }
            }
        ],
        callbacks: {
            rowCallback: function (row, data, index) {
                var items = [{
                    type: 'DOWNLOAD',
                    cssClass: 'toolbar__download-item',
                    onClick: function () {
                        me.props.onDownload(data);
                    }
                }];

                data.editable && items.push({
                    type: 'EDIT',
                    cssClass: 'toolbar__edit-item',
                    onClick: function () {
                        me.props.onEdit(data);
                    }
                });

                var toolbar = new ToolBar({
                    container: me.getApi().cell(index, 5).node(),
                    items: items
                });

                toolbar.mount();

                $(row)
                    .attr('style', 'cursor:pointer')
                    .on('click', function (e) {
                        if (e.target.tagName.toLowerCase() === 'td') {
                            me.props.onSelect(data);
                        }
                    });
            },
            errorCallback: function (error) {
                alert(error.responseText);
            },
            footerCallback: function (tfoot, data, start, end, display) {
                $(tfoot).hide();
            }
        },
        noDataText: 'No service plans.',
        onSelect: function (data) {},
        onDownload: function (data) {},
        onEdit: function (data) {},
        isLoadingDeferred: true
    });
};

ServicePlanList.prototype.getDataSource = function () {
    var context = ExchangeApp.info.context;

    return {
        ajax: {url: context + '/care-coordination/patients/patient/' + this.props.patientId + '/service-plans'}
    };
};