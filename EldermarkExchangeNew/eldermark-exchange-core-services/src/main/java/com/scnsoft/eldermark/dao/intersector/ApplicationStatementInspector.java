package com.scnsoft.eldermark.dao.intersector;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.resource.jdbc.spi.StatementInspector;

import java.util.List;

public class ApplicationStatementInspector implements StatementInspector, MetadataBuilderContributor {

    private final List<StatementInspector> statementInspectors;
    private final List<MetadataBuilderContributor> metadataBuilderContributors;


    public ApplicationStatementInspector() {
        var rowNumberStatementInspector = new RowNumberStatementInspector();
        var viewableEventStatementInspector = new ViewableEventStatementInspector();
        var viewableEventMultipleEmployeesStatementInspector = new ViewableEventMultipleEmployeesStatementInspector();

        this.metadataBuilderContributors =
                List.of(
                        rowNumberStatementInspector,
                        viewableEventStatementInspector,
                        viewableEventMultipleEmployeesStatementInspector
                );

        this.statementInspectors = List.of(
                rowNumberStatementInspector,
                viewableEventStatementInspector,
                viewableEventMultipleEmployeesStatementInspector
        );
    }

    @Override
    public void contribute(MetadataBuilder metadataBuilder) {
        metadataBuilderContributors.forEach(contributor -> contributor.contribute(metadataBuilder));
    }

    @Override
    public String inspect(String sql) {
        for (var inspector : statementInspectors) {
            sql = inspector.inspect(sql);
        }
        return sql;
    }
}
