package com.scnsoft.eldermark.service.report.sdoh;

import com.scnsoft.eldermark.beans.reports.model.sdoh.SDoHReport;
import com.scnsoft.eldermark.beans.reports.model.sdoh.SDoHRow;
import com.scnsoft.eldermark.beans.reports.model.sdoh.SdoHRowType;
import com.scnsoft.eldermark.service.report.workbook.BaseWorkbookGenerator;
import com.scnsoft.eldermark.service.report.workbook.ExcelUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.autosizeWidth;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.writeToCellHighlightMissing;
import static java.util.Arrays.asList;

@Service
public class SDoHWorkbookGeneratorImpl extends BaseWorkbookGenerator implements SDoHWorkbookGenerator {

    private static final DateTimeFormatter SDOH_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static final String ALL_SHEET = "All";

    @Override
    public Workbook generateWorkbook(SDoHReport report, ZoneId zoneId) {
        var wb = new XSSFWorkbook();
        var styles = ExcelUtils.createStyles(wb);

        var groupedRows = report.getRows().stream()
                .collect(Collectors.groupingBy(SDoHRow::getIdentificationReferralFulfillment));

        Stream.of(SdoHRowType.values())
                .forEach(type -> addSDoHTab(wb, styles,
                        groupedRows.getOrDefault(type, Collections.emptyList()), type.name(), zoneId));

        addSDoHTab(wb, styles, report.getRows(), ALL_SHEET, zoneId);
        return wb;
    }

    private void addSDoHTab(XSSFWorkbook wb, Map<String, CellStyle> styles, List<SDoHRow> rows, String sheetName, ZoneId zoneId) {
        var headerValues = asList(
                "submitter_id", "submitter_name", "group_tin", "source_system", "source_system_version",
                "member_last_name", "member_first_name", "member_middle_name", "member_date_of_birth", "member_gender", "member_address", "member_city", "member_state", "member_zip_code", "member_hicn", "member_card_id",
                "rendering_provider_first_name", "rendering_provider_last_name", "rendering_provider_middle_name", "rendering_provider_npi", "rendering_provider_specialty", "rendering_provider_tin", "rendering_provider_address1", "rendering_provider_address2", "rendering_provider_city", "rendering_provider_state", "rendering_provider_zip", "rendering_provider_phonenumber", "rendering_provider_fax",
                "service_date", "identification_referral_fulfillment", "identification_source", "sdoh_description", "referral_fulfillment_activity", "referral_fulfillment_status", "referral_fulfillment_program_id",
                "referral_fulfillment_program_name",
                "referral_fulfillment_person_name", "referral_fulfillment_person_id",
                "icd_or_mbr_attribution_code", "snomed_ct_code", "loinc_code", "language",
                "va_clinic_id", "va_clinic_name", "va_clinic_street_address", "va_clinic_city", "va_clinic_state", "va_clinic_zip_code",
                "contact_caregiver_1_first_name", "contact_caregiver_1_last_name", "contact_caregiver_1_type", "contact_caregiver_1_phone", "contact_caregiver_1_relationship", "contact_caregiver_1_consent",
                "contact_caregiver_2_first_name", "contact_caregiver_2_last_name", "contact_caregiver_2_type", "contact_caregiver_2_phone", "contact_caregiver_2_relationship", "contact_caregiver_2_consent",
                "contact_caregiver_3_first_name", "contact_caregiver_3_last_name", "contact_caregiver_3_type", "contact_caregiver_3_phone", "contact_caregiver_3_relationship", "contact_caregiver_3_consent",
                "contact_caregiver_4_first_name", "contact_caregiver_4_last_name", "contact_caregiver_4_type", "contact_caregiver_4_phone", "contact_caregiver_4_relationship", "contact_caregiver_4_consent",
                "contact_caregiver_5_first_name", "contact_caregiver_5_last_name", "contact_caregiver_5_type", "contact_caregiver_5_phone", "contact_caregiver_5_relationship", "contact_caregiver_5_consent",
                "contact_caregiver_6_first_name", "contact_caregiver_6_last_name", "contact_caregiver_6_type", "contact_caregiver_6_phone", "contact_caregiver_6_relationship", "contact_caregiver_6_consent",
                "contact_caregiver_7_first_name", "contact_caregiver_7_last_name", "contact_caregiver_7_type", "contact_caregiver_7_phone", "contact_caregiver_7_relationship", "contact_caregiver_7_consent",
                "contact_caregiver_8_first_name", "contact_caregiver_8_last_name", "contact_caregiver_8_type", "contact_caregiver_8_phone", "contact_caregiver_8_relationship", "contact_caregiver_8_consent",
                "contact_caregiver_9_first_name", "contact_caregiver_9_last_name", "contact_caregiver_9_type", "contact_caregiver_9_phone", "contact_caregiver_9_relationship", "contact_caregiver_9_consent",
                "contact_caregiver_10_first_name", "contact_caregiver_10_last_name", "contact_caregiver_10_type", "contact_caregiver_10_phone", "contact_caregiver_10_relationship", "contact_caregiver_10_consent",
                "household_1_first_name", "household_1_last_name", "household_1_street_address", "household_1_dob", "household_1_city", "household_1_state", "household_1_zip_code", "household_1_phone",
                "household_2_first_name", "household_2_last_name", "household_2_dob", "household_2_street_address", "household_2_city", "household_2_state", "household_2_zip_code", "household_2_phone",
                "household_3_first_name", "household_3_last_name", "household_3_dob", "household_3_street_address", "household_3_city", "household_3_state", "household_3_zip_code", "household_3_phone",
                "household_4_first_name", "household_4_last_name", "household_4_dob", "household_4_street_address", "household_4_city", "household_4_state", "household_4_zip_code", "household_4_phone",
                "household_5_first_name", "household_5_last_name", "household_5_dob", "household_5_street_address", "household_5_city", "household_5_state", "household_5_zip_code", "household_5_phone",
                "referral_fulfillment_program_address", "referral_fulfillment_program_phone", "ref_ful_program_type", "ref_ful_program_subtype", "ref_ful_program_value",
                "VA_10_5345_on_file", "VA_10_5345_rec_date", "VA_10_5345_sig_date", "VA_10_5345_exp_date",
                "VA_10_0485_on_file", "VA_10_0485_rec_date", "VA_10_0485_sig_date", "VA_10_0485_exp_date",
                "Vet_Mil_Branch", "Vet_Mil_Rank", "State_Medicaid_ID", "Medicaid_State", "NPS_Score", "PROB_DOM_NM",
                "POC_PROB_ID", "POC_PROB_NM", "POC_SGN_SYMP_ID", "POC_SGN_SYMP_NM",
                "Alt_6", "Alt_7", "REF_SERVICE_DATE", "NFF_REASON", "Alt_10"
        );
        //
        Sheet sheet = createSheetWithHeader(wb, styles, sheetName, headerValues, 0);

        var rowCount = 1;
        for (var sdohRow : rows) {
            var row = sheet.createRow(rowCount++);

            var rowDescriptor = sdohRow.getRowDescriptor();

            var colCount = 0;

            colCount++; //"submitter_id",
            writeToCellHighlightMissing(styles, row.createCell(colCount++), sdohRow.getSubmitterName(), rowDescriptor.getSubmitterName());

            // "group_tin",
            colCount += 1;

            writeToCellHighlightMissing(styles, row.createCell(colCount++), sdohRow.getSourceSystem(), rowDescriptor.getSourceSystem());

            //"source_system_version",
            colCount += 1;

            writeToCellHighlightMissing(styles, row.createCell(colCount++), sdohRow.getMemberLastName(), rowDescriptor.getMemberLastName());
            writeToCellHighlightMissing(styles, row.createCell(colCount++), sdohRow.getMemberFirstName(), rowDescriptor.getMemberFirstName());
            writeToCellHighlightMissing(styles, row.createCell(colCount++), sdohRow.getMemberMiddleName(), rowDescriptor.getMemberMiddleName());
            writeToCellHighlightMissing(styles, row.createCell(colCount++), formatToDate(sdohRow.getMemberDateOfBirth(), SDOH_DATE_FORMAT), rowDescriptor.getMemberDateOfBirth());
            writeToCellHighlightMissing(styles, row.createCell(colCount++), sdohRow.getMemberGender(), rowDescriptor.getMemberGender());
            writeToCellHighlightMissing(styles, row.createCell(colCount++), sdohRow.getMemberAddress(), rowDescriptor.getMemberAddress());
            writeToCellHighlightMissing(styles, row.createCell(colCount++), sdohRow.getMemberCity(), rowDescriptor.getMemberCity());
            writeToCellHighlightMissing(styles, row.createCell(colCount++), sdohRow.getMemberState(), rowDescriptor.getMemberState());
            writeToCellHighlightMissing(styles, row.createCell(colCount++), sdohRow.getMemberZipCode(), rowDescriptor.getMemberZipCode());
            writeToCellHighlightMissing(styles, row.createCell(colCount++), sdohRow.getMemberHicn(), rowDescriptor.getMemberHicn());
            writeToCellHighlightMissing(styles, row.createCell(colCount++), sdohRow.getMemberCardId(), rowDescriptor.getMemberCardId());


//            "rendering_provider_first_name", "rendering_provider_last_name", "rendering_provider_middle_name",
//            "rendering_provider_npi", "rendering_provider_specialty", "rendering_provider_tin", "rendering_provider_address1",
//            "rendering_provider_address2", "rendering_provider_city", "rendering_provider_state", "rendering_provider_zip"
//            "rendering_provider_phonenumber", "rendering_provider_fax"
            colCount += 13;

            writeToCellHighlightMissing(styles, row.createCell(colCount++), formatToDate(sdohRow.getServiceDate(), zoneId, SDOH_DATE_FORMAT), rowDescriptor.getServiceDate());
            writeToCellHighlightMissing(styles, row.createCell(colCount++), sdohRow.getIdentificationReferralFulfillment().name(), rowDescriptor.getIdentificationReferralFulfillment());

//            , "identification_source", "sdoh_description", "referral_fulfillment_activity", "referral_fulfillment_status"
//            , "referral_fulfillment_program_id",
            colCount += 5;

            writeToCellHighlightMissing(styles, row.createCell(colCount++), sdohRow.getReferralFulfillmentProgramName(), rowDescriptor.getReferralFulfillmentProgramName());

            // "referral_fulfillment_person_name", "referral_fulfillment_person_id",
            colCount += 2;

            writeToCellHighlightMissing(styles, row.createCell(colCount++), sdohRow.getIcdOrMbrAttributionCode(), rowDescriptor.getIcdOrMbrAttributionCode());

//            , "snomed_ct_code", "loinc_code", "language",
            colCount += 3;

//            "va_clinic_id", "va_clinic_name", "va_clinic_street_address", "va_clinic_city", "va_clinic_state", "va_clinic_zip_code",
            colCount += 6;

//            "contact_caregiver_1_first_name", "contact_caregiver_1_last_name", "contact_caregiver_1_type", "contact_caregiver_1_phone", "contact_caregiver_1_relationship", "contact_caregiver_1_consent",
//            "contact_caregiver_2_first_name", "contact_caregiver_2_last_name", "contact_caregiver_2_type", "contact_caregiver_2_phone", "contact_caregiver_2_relationship", "contact_caregiver_2_consent",
//            "contact_caregiver_3_first_name", "contact_caregiver_3_last_name", "contact_caregiver_3_type", "contact_caregiver_3_phone", "contact_caregiver_3_relationship", "contact_caregiver_3_consent",
//            "contact_caregiver_4_first_name", "contact_caregiver_4_last_name", "contact_caregiver_4_type", "contact_caregiver_4_phone", "contact_caregiver_4_relationship", "contact_caregiver_4_consent",
//            "contact_caregiver_5_first_name", "contact_caregiver_5_last_name", "contact_caregiver_5_type", "contact_caregiver_5_phone", "contact_caregiver_5_relationship", "contact_caregiver_5_consent",
//            "contact_caregiver_6_first_name", "contact_caregiver_6_last_name", "contact_caregiver_6_type", "contact_caregiver_6_phone", "contact_caregiver_6_relationship", "contact_caregiver_6_consent",
//            "contact_caregiver_7_first_name", "contact_caregiver_7_last_name", "contact_caregiver_7_type", "contact_caregiver_7_phone", "contact_caregiver_7_relationship", "contact_caregiver_7_consent",
//            "contact_caregiver_8_first_name", "contact_caregiver_8_last_name", "contact_caregiver_8_type", "contact_caregiver_8_phone", "contact_caregiver_8_relationship", "contact_caregiver_8_consent",
//            "contact_caregiver_9_first_name", "contact_caregiver_9_last_name", "contact_caregiver_9_type", "contact_caregiver_9_phone", "contact_caregiver_9_relationship", "contact_caregiver_9_consent",
//            "contact_caregiver_10_first_name", "contact_caregiver_10_last_name", "contact_caregiver_10_type", "contact_caregiver_10_phone", "contact_caregiver_10_relationship", "contact_caregiver_10_consent",
            colCount += 60;

//            "household_1_first_name", "household_1_last_name", "household_1_street_address", "household_1_dob", "household_1_city", "household_1_state", "household_1_zip_code", "household_1_phone",
//            "household_2_first_name", "household_2_last_name", "household_2_dob", "household_2_street_address", "household_2_city", "household_2_state", "household_2_zip_code", "household_2_phone",
//            "household_3_first_name", "household_3_last_name", "household_3_dob", "household_3_street_address", "household_3_city", "household_3_state", "household_3_zip_code", "household_3_phone",
//            "household_4_first_name", "household_4_last_name", "household_4_dob", "household_4_street_address", "household_4_city", "household_4_state", "household_4_zip_code", "household_4_phone",
//            "household_5_first_name", "household_5_last_name", "household_5_dob", "household_5_street_address", "household_5_city", "household_5_state", "household_5_zip_code", "household_5_phone",
            colCount += 40;

            writeToCellHighlightMissing(styles, row.createCell(colCount++), sdohRow.getReferralFulfillmentProgramAddress(), rowDescriptor.getReferralFulfillmentProgramAddress());
            writeToCellHighlightMissing(styles, row.createCell(colCount++), sdohRow.getReferralFulfillmentProgramPhone(), rowDescriptor.getReferralFulfillmentProgramPhone());
            writeToCellHighlightMissing(styles, row.createCell(colCount++), sdohRow.getRefFulProgramType(), rowDescriptor.getRefFulProgramType());
            writeToCellHighlightMissing(styles, row.createCell(colCount++), sdohRow.getRefFulProgramSubtype(), rowDescriptor.getRefFulProgramSubtype());


//          "ref_ful_program_value",
            ++colCount;

//            "VA_10_5345_on_file", "VA_10_5345_rec_date", "VA_10_5345_sig_date", "VA_10_5345_exp_date",
//            "VA_10_0485_on_file", "VA_10_0485_rec_date", "VA_10_0485_sig_date", "VA_10_0485_exp_date",
//            "Vet_Mil_Branch", "Vet_Mil_Rank",
//            "State_Medicaid_ID", "Medicaid_State", "NPS_Score", "PROB_DOM_NM",
//            "POC_PROB_ID", "POC_PROB_NM", "POC_SGN_SYMP_ID", "POC_SGN_SYMP_NM",
//            "Alt_6", "Alt_7", "REF_SERVICE_DATE", "NFF_REASON", "Alt_10"
            colCount += 4 + 4 + 2 + 4 + 4 + 5;
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }
}
