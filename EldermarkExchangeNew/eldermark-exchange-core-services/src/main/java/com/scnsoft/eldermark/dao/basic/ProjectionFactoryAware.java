package com.scnsoft.eldermark.dao.basic;

import org.springframework.data.projection.ProjectionFactory;

public interface ProjectionFactoryAware {
    void setProjectionFactory(ProjectionFactory projectionFactory);
}
