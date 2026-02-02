package com.scnsoft.eldermark.web.controller.carecoordination;

import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.password.DatabasePasswordSettings;
import com.scnsoft.eldermark.entity.password.PasswordSettingsType;
import com.scnsoft.eldermark.services.carecoordination.FileService;
import com.scnsoft.eldermark.services.carecoordination.OrganizationService;
import com.scnsoft.eldermark.services.marketplace.MarketplaceService;
import com.scnsoft.eldermark.services.password.DatabasePasswordSettingsService;
import com.scnsoft.eldermark.services.password.EmployeePasswordSecurityService;
import com.scnsoft.eldermark.services.password.PasswordHistoryService;
import com.scnsoft.eldermark.shared.carecoordination.*;
import com.scnsoft.eldermark.shared.carecoordination.community.CommunityCreateDto;
import com.scnsoft.eldermark.shared.carecoordination.community.CommunityViewDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@Controller
//@RequestMapping(value = "/care-coordination/organizations")
//@PreAuthorize(SecurityExpressions.IS_CC_SUPERADMIN)
public class CareCoordinationOrganizationController {
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private MarketplaceService marketplaceService;

    @Autowired
    FileService fileService;

    @Autowired
    private DatabasePasswordSettingsService databasePasswordSettingsService;

    @Autowired
    private EmployeePasswordSecurityService employeePasswordSecurityService;

    @Autowired
    private PasswordHistoryService passwordHistoryService;


    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public @ResponseBody
    OrganizationDto getOrganization(@PathVariable Long id) {
        return organizationService.getOrganization(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    OrganizationDto updateOrganization(@PathVariable Long id, @Valid @RequestBody OrganizationDto database) {
        final OrganizationDto dto = organizationService.update(id, database);
        dto.setMarketplace(marketplaceService.updateForOrganization(id, database.getMarketplace()));
        return dto;
    }


    @RequestMapping(value = "/{id}/logo", method = RequestMethod.POST)
    public @ResponseBody
    String updateOrganizationLogo(@PathVariable Long id, @RequestParam("logo") MultipartFile logo) {
        String fileName = fileService.uploadOrganizationLogo(id, logo);
        return "{\"result\":\"Ok\", \"name\":\""+fileName+"\"}";
    }


    @RequestMapping(value = "/{id}/logo", method = RequestMethod.DELETE)
    public @ResponseBody
    String deleteOrganizationLogo(@PathVariable Long id) {
        fileService.deleteOrganizationLogo(id);
        return "{\"result\":\"Ok\"}";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteOrganization(@PathVariable Long id) {
        marketplaceService.deleteForOrganization(id);
        organizationService.deleteOrganization(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody public OrganizationDto createOrganization(@Valid @RequestBody OrganizationDto database) {
        final OrganizationDto dto = organizationService.create(database, false);
        dto.setMarketplace(marketplaceService.updateForOrganization(dto.getId(), database.getMarketplace()));
        return dto;
    }

    @RequestMapping(method=RequestMethod.GET, value="/isUnique")
    public @ResponseBody Boolean checkUniqueness(
          @ModelAttribute OrganizationDto data
    ) {
        return organizationService.checkIfUnique(data);
    }


    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Page<OrganizationListItemDto> getDatabasePage(@ModelAttribute("organizationFilter") OrganizationFilterDto organizationFilterDto, Pageable pageRequest) {
        return organizationService.list(organizationFilterDto, pageRequest);
    }


    @PreAuthorize(SecurityExpressions.IS_CC_USER)
    @RequestMapping(method=RequestMethod.GET, value="/selectList")
    public @ResponseBody List<SelectBoxItemDto> getOrganizationList() {
        Long currentId = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId();
        List<Pair<Long, String>>  orgsSource = organizationService.listBrief();
        List<SelectBoxItemDto> orgs = new ArrayList<SelectBoxItemDto>();
        for (Pair<Long, String> dto : orgsSource) {
            orgs.add(new SelectBoxItemDto(dto.getFirst(), dto.getSecond(), dto.getFirst().equals(currentId)));
        }
        return orgs;
    }

    @RequestMapping(value = "/details/{id}",method = RequestMethod.GET)
    public String getOrganizationDetails(@PathVariable Long id, Model model) {
        model.addAttribute("organization", organizationService.getOrganization(id));
        model.addAttribute("affiliatedOrganizations", organizationService.getAffiliatedOrganizationsInfo(id));
        model.addAttribute("primaryOrganizations", organizationService.getPrimaryOrganizationsInfo(id));
        return "care.coordination.organization.details";
    }

    @RequestMapping(value="/password-settings/{id}",method = RequestMethod.GET)
    public String getPasswordSettings(@PathVariable("id") Long orgId, Model model) {
        PasswordSettingsDto passwordSettingsDto = new PasswordSettingsDto();
        passwordSettingsDto.setOrganizationId(orgId);
        List<DatabasePasswordSettings> databasePasswordSettings = databasePasswordSettingsService.getOrganizationPasswordSettings(orgId);
        if (CollectionUtils.isEmpty(databasePasswordSettings)) {
            databasePasswordSettings = databasePasswordSettingsService.createDefaultDatabasePasswordSettings(orgId);
        }
        passwordSettingsDto.setDatabasePasswordSettingsList(databasePasswordSettings);
        model.addAttribute("passwordSettingsDto", passwordSettingsDto);
        return "care.coordination.organizations.password.settings";
    }

    @RequestMapping(value="/password-settings/{id}",method = RequestMethod.POST)
    @ResponseBody
    public String updatePasswordSettings(@PathVariable("id") Long orgId, @ModelAttribute PasswordSettingsDto passwordSettingsDto, Model model) {
        List<DatabasePasswordSettings> databasePasswordSettings = databasePasswordSettingsService.getOrganizationPasswordSettings(orgId);
        List<DatabasePasswordSettings> newDatabasePasswordSettings =  passwordSettingsDto.getDatabasePasswordSettingsList();
        newDatabasePasswordSettings = fixDisabledProperties(newDatabasePasswordSettings);
        for (DatabasePasswordSettings pwdSetting : databasePasswordSettings) {
            //process password age switch on\off
            if (pwdSetting.getPasswordSettings().getPasswordSettingsType() == PasswordSettingsType.PASSWORD_MAXIMUM_AGE_IN_DAYS) {
                Boolean previousMaxAgeEnabled = pwdSetting.getEnabled() && pwdSetting.getValue() > 0;
                for (DatabasePasswordSettings newPwdSetting : newDatabasePasswordSettings) {
                    if (newPwdSetting.getPasswordSettings().getPasswordSettingsType() == PasswordSettingsType.PASSWORD_MAXIMUM_AGE_IN_DAYS) {
                        Boolean newMaxAgeEnabled = newPwdSetting.getEnabled() && newPwdSetting.getValue() > 0;
                        if (newMaxAgeEnabled && !previousMaxAgeEnabled) {
                            employeePasswordSecurityService.setPasswordChangedTime(orgId, new Date());
                        } else if (!newMaxAgeEnabled && previousMaxAgeEnabled) {
                            employeePasswordSecurityService.resetPasswordChangedTime(orgId);
                        }
                    }
                }
            }
            //process password history switch on\off
            if (pwdSetting.getPasswordSettings().getPasswordSettingsType() == PasswordSettingsType.COMPLEXITY_PASSWORD_HISTORY_COUNT) {
                Boolean previousPwdHistoryEnabled = pwdSetting.getEnabled() && pwdSetting.getValue() > 0;
                for (DatabasePasswordSettings newPwdSetting : newDatabasePasswordSettings) {
                    if (newPwdSetting.getPasswordSettings().getPasswordSettingsType() == PasswordSettingsType.COMPLEXITY_PASSWORD_HISTORY_COUNT) {
                        Boolean newPwdHistoryEnabled = newPwdSetting.getEnabled() && newPwdSetting.getValue() > 0;
                        if (newPwdHistoryEnabled && !previousPwdHistoryEnabled) {
                            passwordHistoryService.enablePasswordHistory(orgId);
                        } else if (!newPwdHistoryEnabled && previousPwdHistoryEnabled) {
                            passwordHistoryService.clearPasswordHistory(orgId);
                        }
                    }
                }
            }
        }
        databasePasswordSettingsService.updateDatabasePasswordSettings(newDatabasePasswordSettings);
        return "{\"result\":\"Ok\"}";
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody public OrganizationDto updateOrganizationData(@PathVariable Long id, @Valid @RequestBody OrganizationDto organizationDto) {
        return organizationDto;
    }

    private List<DatabasePasswordSettings> fixDisabledProperties(List<DatabasePasswordSettings> databasePasswordSettings) {
        for (DatabasePasswordSettings pwdSetting : databasePasswordSettings) {
            if (pwdSetting.getEnabled() == null) {
                pwdSetting.setEnabled(Boolean.TRUE);
            }
        }
        return databasePasswordSettings;
    }


}
