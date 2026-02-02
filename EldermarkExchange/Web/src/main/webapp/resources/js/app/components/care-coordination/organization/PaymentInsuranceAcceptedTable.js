function PaymentInsuranceAcceptedTable () {
    Table.apply(this, arguments)
}

PaymentInsuranceAcceptedTable.prototype = Object.create(Table.prototype);
PaymentInsuranceAcceptedTable.prototype.constructor = PaymentInsuranceAcceptedTable;

PaymentInsuranceAcceptedTable.prototype.getDefaultProps = function () {
    var me = this;

    return {
        className: 'payment-insurance-accepted-table',
        hasFooter: false,
        columns: [
            {
                title: 'Network / Payment method',
                name: 'networkName'
            },
            {
                title: 'Plan',
                name: 'plans',
                getExtraProps: function () {
                    return {
                        networkId: me.props.networkId
                    }
                },
                component: PaymentInsuranceAcceptedTable.Cell
            }
        ]
    }
}