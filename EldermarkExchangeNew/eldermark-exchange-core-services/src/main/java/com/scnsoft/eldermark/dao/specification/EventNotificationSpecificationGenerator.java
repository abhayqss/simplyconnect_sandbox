package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.entity.EventNotification;
import com.scnsoft.eldermark.entity.EventNotification_;
import com.scnsoft.eldermark.entity.event.Event_;
import com.scnsoft.eldermark.entity.lab.LabResearchOrder_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class EventNotificationSpecificationGenerator {

    public Specification<EventNotification> sentDatetimeIsNotNull() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get(EventNotification_.sentDatetime));
    }

    public Specification<EventNotification> byLabResearchOrderId(Long labResearchOrderId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.join(EventNotification_.event).join(Event_.labResearchOrder).get(LabResearchOrder_.id), labResearchOrderId);
    }
}
