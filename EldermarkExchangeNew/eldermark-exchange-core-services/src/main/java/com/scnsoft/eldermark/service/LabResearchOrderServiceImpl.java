package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.LabResearchOrderFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.LabOrderSecurityAwareEntity;
import com.scnsoft.eldermark.dao.*;
import com.scnsoft.eldermark.dao.specification.LabResearchOrderSpecificationGenerator;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.EventAuthor;
import com.scnsoft.eldermark.entity.lab.*;
import com.scnsoft.eldermark.entity.lab.review.LabResearchOrderBulkReviewListItem;
import com.scnsoft.eldermark.entity.lab.review.LabResearchOrderDocument;
import com.scnsoft.eldermark.entity.lab.review.LabResearchOrderWithClient;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.service.hl7.ApolloORMGenerator;
import com.scnsoft.eldermark.service.hl7.ApolloORMSender;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.EventNotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class LabResearchOrderServiceImpl implements LabResearchOrderService {

    @Autowired
    private LabResearchOrderDao labResearchOrderDao;

    @Autowired
    private LabResearchOrderSpecificationGenerator labResearchOrderSpecificationGenerator;

    @Autowired
    private LabIcd10GroupDao labIcd10GroupDao;

    @Autowired
    private SpecimenTypeDao specimenTypeDao;

    @Autowired
    private ApolloORMGenerator apolloORMGenerator;

    @Autowired
    private ApolloORMSender apolloORMSender;

    @Autowired
    private LabResearchOrderORMDao labOrderORMDao;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventTypeService eventTypeService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private LabResearchOrderORUDao orderORUDao;

    @Autowired
    private LabResearchOrderObservationResultDao labResearchOrderObservationResultDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Override
    @Transactional
    public LabResearchOrder create(LabResearchOrder entity) {
        entity.setStatus(LabResearchOrderStatus.SENT_TO_LAB);
        entity.setCreatedDate(Instant.now());
        var saved = labResearchOrderDao.save(entity);

        saved.setRequisitionNumber("SC-" + saved.getId().toString());
        saved = labResearchOrderDao.save(saved);

        sendORM(saved);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public LabResearchOrder findById(Long id) {
        return labResearchOrderDao.findById(id).orElseThrow();
    }

    @Override
    @Transactional
    public void review(Long id) {
        var order = labResearchOrderDao.findById(id).orElseThrow();
        order.setStatus(LabResearchOrderStatus.REVIEWED);
        LabResearchOrder labOrder = labResearchOrderDao.save(order);
        eventService.save(createEvent(labOrder));
    }


    @Override
    @Transactional(readOnly = true)
    public Page<LabResearchOrderListItem> findLabOrders(LabResearchOrderFilter filter, PermissionFilter permissionFilter, Pageable pageable) {
        var hasAccess = labResearchOrderSpecificationGenerator.hasAccess(permissionFilter);
        var byFilter = labResearchOrderSpecificationGenerator.byFilter(filter);
        return labResearchOrderDao.findLabOrders(byFilter.and(hasAccess), pageable);
    }

    private void sendORM(LabResearchOrder saved) {
        var orm = apolloORMGenerator.generate(saved);
        labOrderORMDao.save(orm);

        if (apolloORMSender.send(orm)) {
            labOrderORMDao.save(orm);
        } else {
            throw new InternalServerException(InternalServerExceptionType.LABS_APOLLO_UNAVAILABLE);
            //or create queue of failed attempts and send in future on scheduled basis
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<LabIcd10Group> findIcdGroupCodes() {
        return labIcd10GroupDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecimenType> findSpecimens() {
        return specimenTypeDao.findAll(Sort.by(SpecimenType_.ORDER));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRequisitionNumberUniqueInOrganization(String requisitionNumber, Long organizationId) {
        return !labResearchOrderDao.existsRequisitionNumberInOrganization(requisitionNumber, organizationId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public LabResearchOrderORU createOruInNewTransaction(LabResearchOrderORU orderOru) {
        return orderORUDao.saveAndFlush(orderOru);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateOrderOruFailInNewTransaction(Long id, String errorMessage) {
        //updating by query in order to avoid transaction deadlocks
        orderORUDao.updateOrderOruForFail(id, errorMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LabResearchOrderObservationResult> findLabResults(Long labResearchOrderId, Pageable pageable) {
        return labResearchOrderObservationResultDao.findAllByLabOrderId(labResearchOrderId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long count(LabResearchOrderFilter filter, PermissionFilter permissionFilter) {
        var hasAccess = labResearchOrderSpecificationGenerator.hasAccess(permissionFilter);
        var byFilter = labResearchOrderSpecificationGenerator.byFilter(filter);
        return labResearchOrderDao.count(byFilter.and(hasAccess));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LabResearchOrderBulkReviewListItem> findReviewOrders(LabResearchOrderFilter filter, PermissionFilter permissionFilter, Sort sort) {
        var hasAccess = labResearchOrderSpecificationGenerator.hasAccess(permissionFilter);
        var byFilter = labResearchOrderSpecificationGenerator.byFilter(filter);
        var docsWithOrders = labResearchOrderDao.findLabOrdersWithDocuments(byFilter.and(hasAccess), sort);

        return docsWithOrders.stream()
                .map(item -> new Pair<LabResearchOrderWithClient, LabResearchOrderDocument>(new LabResearchOrderWithClient(item.getId(), item.getClientId(), item.getClientFirstName(), item.getClientLastName(), item.getOrderDate()),
                        item.getDocumentId() != null ? new LabResearchOrderDocument(item.getDocumentId(), item.getDocumentTitle(), item.getDocumentOriginalFileName(), item.getMimeType()) : null))
                .collect(Collectors.groupingBy(Pair::getFirst, Collectors.mapping(Pair::getSecond, Collectors.toList())))
                .entrySet().stream()
                .map(labEntry -> new LabResearchOrderBulkReviewListItem(labEntry.getKey().getId(), labEntry.getKey().getClientId(), labEntry.getKey().getClientFirstName(),
                        labEntry.getKey().getClientLastName(), labEntry.getKey().getOrderDate(), labEntry.getValue().stream().filter(Objects::nonNull).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LabOrderSecurityAwareEntity findSecurityAware(Long id) {
        return labResearchOrderDao.findById(id, LabOrderSecurityAwareEntity.class).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findReviewers(LabResearchOrder labResearchOrder) {
        var labReviewers = labResearchOrderSpecificationGenerator.labOrderReviewers(labResearchOrder);
        return employeeDao.findAll(labReviewers);
    }

    private Event createEvent(LabResearchOrder order) {
        var event = new Event();

        var author = new EventAuthor();
        var employee = loggedUserService.getCurrentEmployee();
        author.setFirstName(employee.getFirstName());
        author.setLastName(employee.getLastName());
        author.setOrganization(order.getClient().getOrganization().getName());
        author.setRole(employee.getCareTeamRole().getName());
        event.setEventAuthor(author);

        event.setClient(order.getClient());
        event.setLabResearchOrder(order);
        event.setEventType(eventTypeService.findByCode(EventNotificationUtils.LAB_REVIEWED));
        event.setEventDateTime(Instant.now());
        event.setManual(false);
        return event;
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findById(Long aLong, Class<P> projection) {
        return labResearchOrderDao.findById(aLong, projection).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllById(Collection<Long> longs, Class<P> projection) {
        return labResearchOrderDao.findByIdIn(longs, projection);
    }

}
