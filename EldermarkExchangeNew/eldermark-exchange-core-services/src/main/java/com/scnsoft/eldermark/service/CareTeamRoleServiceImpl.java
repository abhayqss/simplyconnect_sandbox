package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.CareTeamRoleDao;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.EditableRoleMatrix;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.entity.CareTeamRoleCode.*;

@Service
public class CareTeamRoleServiceImpl implements CareTeamRoleService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CareTeamRoleDao careTeamRoleDao;

    private Map<CareTeamRoleCode, CareTeamRole> careTeamRoleMapByCode;

    private List<EditableRoleMatrix> contactEditableRoleMatrixList;

    private List<EditableRoleMatrix> clientCareTeamMemberEditableRoleMatrixList;

    private List<EditableRoleMatrix> communityCareTeamMemberEditableRoleMatrixList;

    private Map<CareTeamRoleCode, List<CareTeamRole>> allowedCtmRoleByEmployeeRoleMap;

    @PostConstruct
    protected void postConstruct() {
        //todo add entries for pharmacy technician
        final List<CareTeamRole> roleList = careTeamRoleDao.findAll(Sort.by(CareTeamRoleDao.ORDER_BY_POSITION));
        careTeamRoleMapByCode = roleList.stream().collect(Collectors.toMap(CareTeamRole::getCode, Function.identity()));

        //todo will be moved to DB and loaded on each request to allow permission modification in runtime.
        contactEditableRoleMatrixList = Arrays.asList(
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_CASE_MANAGER)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_CARE_COORDINATOR)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_PARENT_GUARDIAN)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_PERSON_RECEIVING_SERVICES)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_BEHAVIORAL_HEALTH)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_PHARMACY_TECHNICIAN)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_NURSE)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_TELE_HEALTH_NURSE)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_COMMUNITY_MEMBERS)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_SERVICE_PROVIDER)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_HCA)),

                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_CASE_MANAGER)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_CARE_COORDINATOR)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_PARENT_GUARDIAN)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_PERSON_RECEIVING_SERVICES)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_BEHAVIORAL_HEALTH)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_PHARMACY_TECHNICIAN)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_NURSE)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_TELE_HEALTH_NURSE)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_COMMUNITY_MEMBERS)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_SERVICE_PROVIDER)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_HCA)),

                new EditableRoleMatrix(get(ROLE_PARENT_GUARDIAN), get(ROLE_PARENT_GUARDIAN)),
                new EditableRoleMatrix(get(ROLE_PARENT_GUARDIAN), get(ROLE_PERSON_RECEIVING_SERVICES)),

                new EditableRoleMatrix(get(ROLE_PERSON_RECEIVING_SERVICES), get(ROLE_PARENT_GUARDIAN)),
                new EditableRoleMatrix(get(ROLE_PERSON_RECEIVING_SERVICES), get(ROLE_PERSON_RECEIVING_SERVICES)),

                new EditableRoleMatrix(get(ROLE_PRIMARY_PHYSICIAN), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_PRIMARY_PHYSICIAN), get(ROLE_BEHAVIORAL_HEALTH)),
                new EditableRoleMatrix(get(ROLE_PRIMARY_PHYSICIAN), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_PRIMARY_PHYSICIAN), get(ROLE_PHARMACY_TECHNICIAN)),

                new EditableRoleMatrix(get(ROLE_BEHAVIORAL_HEALTH), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_BEHAVIORAL_HEALTH), get(ROLE_BEHAVIORAL_HEALTH)),

                new EditableRoleMatrix(get(ROLE_COMMUNITY_MEMBERS), get(ROLE_COMMUNITY_MEMBERS)),

                new EditableRoleMatrix(get(ROLE_PHARMACIST), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_PHARMACIST), get(ROLE_PHARMACY_TECHNICIAN)),

                new EditableRoleMatrix(get(ROLE_PHARMACY_TECHNICIAN), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_PHARMACY_TECHNICIAN), get(ROLE_PHARMACY_TECHNICIAN)),

                new EditableRoleMatrix(get(ROLE_NURSE), get(ROLE_NURSE)),
                new EditableRoleMatrix(get(ROLE_NURSE), get(ROLE_TELE_HEALTH_NURSE)),

                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_PARENT_GUARDIAN)),
                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_PERSON_RECEIVING_SERVICES)),
                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_BEHAVIORAL_HEALTH)),
                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_PHARMACY_TECHNICIAN)),
                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_SERVICE_PROVIDER)),

                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_CASE_MANAGER)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_CARE_COORDINATOR)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_PARENT_GUARDIAN)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_PERSON_RECEIVING_SERVICES)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_BEHAVIORAL_HEALTH)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_PHARMACY_TECHNICIAN)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_NURSE)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_TELE_HEALTH_NURSE)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_COMMUNITY_MEMBERS)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_SERVICE_PROVIDER)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_SUPER_ADMINISTRATOR)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_ADMINISTRATOR)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_COMMUNITY_ADMINISTRATOR)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_HCA)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_CONTENT_CREATOR)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_MARKETER)),

                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_CASE_MANAGER)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_CARE_COORDINATOR)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_PARENT_GUARDIAN)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_PERSON_RECEIVING_SERVICES)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_BEHAVIORAL_HEALTH)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_PHARMACY_TECHNICIAN)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_NURSE)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_TELE_HEALTH_NURSE)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_COMMUNITY_MEMBERS)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_SERVICE_PROVIDER)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_ADMINISTRATOR)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_COMMUNITY_ADMINISTRATOR)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_HCA)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_MARKETER)),

                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_CASE_MANAGER)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_CARE_COORDINATOR)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_PARENT_GUARDIAN)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_PERSON_RECEIVING_SERVICES)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_BEHAVIORAL_HEALTH)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_PHARMACY_TECHNICIAN)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_NURSE)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_TELE_HEALTH_NURSE)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_COMMUNITY_MEMBERS)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_SERVICE_PROVIDER)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_COMMUNITY_ADMINISTRATOR)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_HCA)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_MARKETER)));


        clientCareTeamMemberEditableRoleMatrixList = Arrays.asList(
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_CASE_MANAGER)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_CARE_COORDINATOR)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_PARENT_GUARDIAN)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_PERSON_RECEIVING_SERVICES)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_BEHAVIORAL_HEALTH)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_COMMUNITY_MEMBERS)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_SERVICE_PROVIDER)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_PHARMACY_TECHNICIAN)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_NURSE)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_TELE_HEALTH_NURSE)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_HCA)),

                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_CASE_MANAGER)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_CARE_COORDINATOR)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_PARENT_GUARDIAN)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_PERSON_RECEIVING_SERVICES)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_BEHAVIORAL_HEALTH)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_COMMUNITY_MEMBERS)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_SERVICE_PROVIDER)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_PHARMACY_TECHNICIAN)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_NURSE)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_TELE_HEALTH_NURSE)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_HCA)),

                new EditableRoleMatrix(get(ROLE_PARENT_GUARDIAN), get(ROLE_PARENT_GUARDIAN)),
                new EditableRoleMatrix(get(ROLE_PARENT_GUARDIAN), get(ROLE_PERSON_RECEIVING_SERVICES)),

                new EditableRoleMatrix(get(ROLE_PERSON_RECEIVING_SERVICES), get(ROLE_PARENT_GUARDIAN)),
                new EditableRoleMatrix(get(ROLE_PERSON_RECEIVING_SERVICES), get(ROLE_PERSON_RECEIVING_SERVICES)),

                new EditableRoleMatrix(get(ROLE_PRIMARY_PHYSICIAN), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_PRIMARY_PHYSICIAN), get(ROLE_BEHAVIORAL_HEALTH)),

                new EditableRoleMatrix(get(ROLE_BEHAVIORAL_HEALTH), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_BEHAVIORAL_HEALTH), get(ROLE_BEHAVIORAL_HEALTH)),

                new EditableRoleMatrix(get(ROLE_COMMUNITY_MEMBERS), get(ROLE_COMMUNITY_MEMBERS)),

                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_PARENT_GUARDIAN)),
                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_PERSON_RECEIVING_SERVICES)),
                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_BEHAVIORAL_HEALTH)),
                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_SERVICE_PROVIDER)),
                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_PHARMACY_TECHNICIAN)),
                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_NURSE)),
                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_TELE_HEALTH_NURSE)),

                new EditableRoleMatrix(get(ROLE_PHARMACIST), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_PHARMACIST), get(ROLE_PHARMACY_TECHNICIAN)),
                new EditableRoleMatrix(get(ROLE_PHARMACIST), get(ROLE_NURSE)),
                new EditableRoleMatrix(get(ROLE_PHARMACIST), get(ROLE_TELE_HEALTH_NURSE)),

                new EditableRoleMatrix(get(ROLE_PHARMACY_TECHNICIAN), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_PHARMACY_TECHNICIAN), get(ROLE_PHARMACY_TECHNICIAN)),

                new EditableRoleMatrix(get(ROLE_NURSE), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_NURSE), get(ROLE_PHARMACY_TECHNICIAN)),
                new EditableRoleMatrix(get(ROLE_NURSE), get(ROLE_NURSE)),
                new EditableRoleMatrix(get(ROLE_NURSE), get(ROLE_TELE_HEALTH_NURSE)),

                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_CASE_MANAGER)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_CARE_COORDINATOR)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_PARENT_GUARDIAN)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_PERSON_RECEIVING_SERVICES)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_BEHAVIORAL_HEALTH)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_COMMUNITY_MEMBERS)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_SERVICE_PROVIDER)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_PHARMACY_TECHNICIAN)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_NURSE)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_TELE_HEALTH_NURSE)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_HCA)),

                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_CASE_MANAGER)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_CARE_COORDINATOR)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_PARENT_GUARDIAN)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_PERSON_RECEIVING_SERVICES)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_BEHAVIORAL_HEALTH)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_COMMUNITY_MEMBERS)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_SERVICE_PROVIDER)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_PHARMACY_TECHNICIAN)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_NURSE)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_TELE_HEALTH_NURSE)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_HCA)),

                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_CASE_MANAGER)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_CARE_COORDINATOR)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_PARENT_GUARDIAN)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_PERSON_RECEIVING_SERVICES)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_BEHAVIORAL_HEALTH)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_COMMUNITY_MEMBERS)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_SERVICE_PROVIDER)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_PHARMACY_TECHNICIAN)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_NURSE)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_TELE_HEALTH_NURSE)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_HCA))
        );

        communityCareTeamMemberEditableRoleMatrixList = Arrays.asList(
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_CASE_MANAGER)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_CARE_COORDINATOR)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_BEHAVIORAL_HEALTH)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_COMMUNITY_MEMBERS)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_SERVICE_PROVIDER)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_NURSE)),
                new EditableRoleMatrix(get(ROLE_CASE_MANAGER), get(ROLE_TELE_HEALTH_NURSE)),

                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_CASE_MANAGER)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_CARE_COORDINATOR)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_BEHAVIORAL_HEALTH)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_COMMUNITY_MEMBERS)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_SERVICE_PROVIDER)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_CARE_COORDINATOR), get(ROLE_NURSE)),

                new EditableRoleMatrix(get(ROLE_PRIMARY_PHYSICIAN), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_PRIMARY_PHYSICIAN), get(ROLE_BEHAVIORAL_HEALTH)),

                new EditableRoleMatrix(get(ROLE_BEHAVIORAL_HEALTH), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_BEHAVIORAL_HEALTH), get(ROLE_BEHAVIORAL_HEALTH)),

                new EditableRoleMatrix(get(ROLE_COMMUNITY_MEMBERS), get(ROLE_COMMUNITY_MEMBERS)),

                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_BEHAVIORAL_HEALTH)),
                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_SERVICE_PROVIDER)),
                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_PHARMACY_TECHNICIAN)),
                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_NURSE)),
                new EditableRoleMatrix(get(ROLE_SERVICE_PROVIDER), get(ROLE_TELE_HEALTH_NURSE)),

                new EditableRoleMatrix(get(ROLE_PHARMACIST), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_PHARMACIST), get(ROLE_PHARMACY_TECHNICIAN)),
                new EditableRoleMatrix(get(ROLE_PHARMACIST), get(ROLE_NURSE)),
                new EditableRoleMatrix(get(ROLE_PHARMACIST), get(ROLE_TELE_HEALTH_NURSE)),

                new EditableRoleMatrix(get(ROLE_PHARMACY_TECHNICIAN), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_PHARMACY_TECHNICIAN), get(ROLE_PHARMACY_TECHNICIAN)),

                new EditableRoleMatrix(get(ROLE_NURSE), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_NURSE), get(ROLE_PHARMACY_TECHNICIAN)),
                new EditableRoleMatrix(get(ROLE_NURSE), get(ROLE_NURSE)),
                new EditableRoleMatrix(get(ROLE_NURSE), get(ROLE_TELE_HEALTH_NURSE)),

                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_CASE_MANAGER)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_CARE_COORDINATOR)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_BEHAVIORAL_HEALTH)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_COMMUNITY_MEMBERS)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_SERVICE_PROVIDER)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_PHARMACY_TECHNICIAN)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_NURSE)),
                new EditableRoleMatrix(get(ROLE_SUPER_ADMINISTRATOR), get(ROLE_TELE_HEALTH_NURSE)),

                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_CASE_MANAGER)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_CARE_COORDINATOR)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_BEHAVIORAL_HEALTH)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_COMMUNITY_MEMBERS)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_SERVICE_PROVIDER)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_PHARMACY_TECHNICIAN)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_NURSE)),
                new EditableRoleMatrix(get(ROLE_ADMINISTRATOR), get(ROLE_TELE_HEALTH_NURSE)),

                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_CASE_MANAGER)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_CARE_COORDINATOR)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_PRIMARY_PHYSICIAN)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_BEHAVIORAL_HEALTH)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_COMMUNITY_MEMBERS)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_SERVICE_PROVIDER)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_PHARMACIST)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_PHARMACY_TECHNICIAN)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_NURSE)),
                new EditableRoleMatrix(get(ROLE_COMMUNITY_ADMINISTRATOR), get(ROLE_TELE_HEALTH_NURSE))
        );

        allowedCtmRoleByEmployeeRoleMap = careTeamRoleMapByCode.keySet().stream()
            .collect(Collectors.toMap(
                Function.identity(),
                it -> {
                    if (it == ROLE_HCA) {
                        return List.of(get(ROLE_HCA));
                    } else if (it == ROLE_PARENT_GUARDIAN || it == ROLE_PERSON_RECEIVING_SERVICES) {
                        return List.of(get(ROLE_PARENT_GUARDIAN), get(ROLE_PERSON_RECEIVING_SERVICES));
                    }
                    else {
                        return careTeamRoleMapByCode.keySet().stream()
                            .map(this::get)
                            .collect(Collectors.toList());
                    }
                }
            ));
    }

    @Override
    public CareTeamRole get(CareTeamRoleCode code) {
        return careTeamRoleMapByCode.get(code);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEditableContactRole(CareTeamRole loggedUserSystemRole, Long currentRoleId, Long roleToCheckId) {
        var allowedRoles = findContactEditableRoles(loggedUserSystemRole);
        return isEditableRole(allowedRoles, currentRoleId, roleToCheckId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEditableClientCareTeamMemberRole(CareTeamRole loggedUserSystemRole, Long currentRoleId, Long roleToCheckId) {
        var allowedRoles = findClientCareTeamMemberEditableRoles(loggedUserSystemRole);
        return isEditableRole(allowedRoles, currentRoleId, roleToCheckId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEditableCommunityCareTeamMemberRole(CareTeamRole loggedUserSystemRole, Long currentRoleId, Long roleToCheckId) {
        var allowedRoles = findCommunityCareTeamMemberEditableRoles(loggedUserSystemRole);
        return isEditableRole(allowedRoles, currentRoleId, roleToCheckId);
    }

    private boolean isEditableRole(List<CareTeamRole> allowedRoles, Long currentRoleId, Long targetRoleId) {
        if (allowedRoles.isEmpty()) {
            return false;
        }
        var allowedRolesIds = CareCoordinationUtils.toIdsSet(allowedRoles);

        boolean currentRoleIsEditable = currentRoleId == null || allowedRolesIds.contains(currentRoleId);

        if (ANY_TARGET_ROLE.equals(targetRoleId)) {
            return currentRoleIsEditable;
        }
        if (ANOTHER_TARGET_ROLE.equals(targetRoleId)) {
            Objects.requireNonNull(currentRoleId, "ANOTHER_TARGET_ROLE is available for edit mode only, i.e. currentRoleId shouldn't be null");
            return currentRoleIsEditable && allowedRolesIds.stream().anyMatch(allowedRoleId -> !allowedRoleId.equals(currentRoleId));
        }

        return currentRoleIsEditable && allowedRolesIds.contains(targetRoleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CareTeamRole> findContactEditableRoles(CareTeamRole role) {
        return findEditableRoles(role, contactEditableRoleMatrixList);
    }

    @Override
    public List<CareTeamRole> findClientCareTeamMemberEditableRoles(CareTeamRole role) {
        return findEditableRoles(role, clientCareTeamMemberEditableRoleMatrixList);
    }

    @Override
    public List<CareTeamRole> findCommunityCareTeamMemberEditableRoles(CareTeamRole role) {
        return findEditableRoles(role, communityCareTeamMemberEditableRoleMatrixList);
    }

    private List<CareTeamRole> findEditableRoles(CareTeamRole role, List<EditableRoleMatrix> listToCheck) {
        return listToCheck.stream()
                .filter(matrixEntry -> matrixEntry.getLoggedInUserRole().getId().equals(role.getId()))
                .map(EditableRoleMatrix::getEditableRole)
                .sorted(Comparator.comparing(CareTeamRole::getPosition))
                .collect(Collectors.toList());
    }

    @Override
    public List<CareTeamRole> findAllowedCtmRolesForEmployee(Long employeeId) {
        var careTeamRole = employeeService.getEmployeeById(employeeId).getCareTeamRole();
        if (careTeamRole != null) {
            return findAllowedCtmRolesForEmployeeRole(careTeamRole);
        } else {
            return allowedCtmRoleByEmployeeRoleMap.keySet().stream()
                .map(this::get)
                .collect(Collectors.toList());
        }
    }

    @Override
    public List<CareTeamRole> findAllowedCtmRolesForEmployeeRole(CareTeamRole employeeRole) {
        return allowedCtmRoleByEmployeeRoleMap.getOrDefault(employeeRole.getCode(), List.of());
    }
}
