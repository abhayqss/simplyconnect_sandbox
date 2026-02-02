package com.scnsoft.eldermark.services.populator;

import com.scnsoft.eldermark.entity.xds.message.*;
import com.scnsoft.eldermark.entity.xds.segment.*;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.adt.*;
import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventAdtSegmentsPopulator implements Populator<AdtMessage, EventDto> {

    @Autowired
    private Converter<EVNEventTypeSegment, EVNEventTypeSegmentDto> evnSegmentTransformer;

    @Autowired
    private Converter<PIDPatientIdentificationSegment, PIDPatientIdentificationSegmentDto> pidSegmentTransformer;

    @Autowired
    private Converter<PV1PatientVisitSegment, PV1PatientVisitSegmentDto> pv1SegmentTransformer;

    @Autowired
    private ListAndItemTransformer<AdtDG1DiagnosisSegment, DG1DiagnosisSegmentDto> dg1SegmentTransformer;

    @Autowired
    private ListAndItemTransformer<AdtGT1GuarantorSegment, GT1GuarantorSegmentDto> gt1GuarantorSegmentTransformer;

    @Autowired
    private ListAndItemTransformer<PR1ProceduresSegment, PR1ProcedureSegmentDto> pr1ProcedureSegmentTransformer;

    @Autowired
    private ListAndItemTransformer<IN1InsuranceSegment, IN1InsuranceSegmentDto> in1InsuranceSegmentTransformer;

    @Autowired
    private ListAndItemTransformer<AdtAL1AllergySegment, AL1AllergySegmentDto> al1AllergySegmentTransformer;

    @Autowired
    private ListAndItemTransformer<AdtPD1AdditionalDemographicSegment, PD1AdditionalDemographicSegmentDto> pd1AdditionalDemographicSegmentTransformer;

    @Override
    public void populate(final AdtMessage src, final EventDto target) {
        if (src == null) {
            return;
        }
        if (src instanceof EVNSegmentContainingMessage) {
            final EVNEventTypeSegment evn = ((EVNSegmentContainingMessage) src).getEvn();
            target.setAdtSegmentEVN(evnSegmentTransformer.convert(evn));
        }
        if (src instanceof PIDSegmentContainingMessage) {
            final PIDPatientIdentificationSegment pid = ((PIDSegmentContainingMessage) src).getPid();
            target.setAdtSegmentPID(pidSegmentTransformer.convert(pid));
        }
        if (src instanceof PV1SegmentContainingMessage) {
            final PV1PatientVisitSegment pv1 = ((PV1SegmentContainingMessage) src).getPv1();
            target.setAdtSegmentPV1(pv1SegmentTransformer.convert(pv1));
        }
        if (src instanceof DG1ListSegmentContainingMessage) {
            final List<AdtDG1DiagnosisSegment> dg1List = ((DG1ListSegmentContainingMessage)src).getDg1List();
            if (CollectionUtils.isNotEmpty(dg1List)) {
                List<DG1DiagnosisSegmentDto> dg1DiagnosisSegmentDtos = new ArrayList<>();
                dg1SegmentTransformer.convertList(dg1List, dg1DiagnosisSegmentDtos);
                target.setAdtSegmentsDG1(dg1DiagnosisSegmentDtos);
            }
        }

        if (src instanceof GT1ListSegmentContainingMessage) {
            final List<AdtGT1GuarantorSegment> gt1List = ((GT1ListSegmentContainingMessage)src).getGt1List();
            if (CollectionUtils.isNotEmpty(gt1List)) {
                List<GT1GuarantorSegmentDto> gt1GuarantorSegmentDtos = new ArrayList<>();
                gt1GuarantorSegmentTransformer.convertList(gt1List, gt1GuarantorSegmentDtos);
                target.setAdtSegmentsGT1(gt1GuarantorSegmentDtos);
            }
        }

        if (src instanceof PR1ListSegmentContaingMessage) {
            final List<PR1ProceduresSegment> pr1ProceduresSegmentList = ((PR1ListSegmentContaingMessage)src).getPr1List();
            if (pr1ProceduresSegmentList != null) {
                List<PR1ProcedureSegmentDto> pr1ProcedureSegmentDtos = new ArrayList<>();
                pr1ProcedureSegmentTransformer.convertList(pr1ProceduresSegmentList, pr1ProcedureSegmentDtos);
                target.setAdtSegmentsPR1(pr1ProcedureSegmentDtos);
            }
        }

        if (src instanceof IN1ListSegmentContainingMessage) {
            final List<IN1InsuranceSegment> in1InsuranceSegmentList = ((IN1ListSegmentContainingMessage)src).getIn1List();
            if (in1InsuranceSegmentList != null) {
                List<IN1InsuranceSegmentDto> in1InsuranceSegmentDtos = new ArrayList<>();
                in1InsuranceSegmentTransformer.convertList(in1InsuranceSegmentList, in1InsuranceSegmentDtos);
                target.setAdtSegmentsIN1(in1InsuranceSegmentDtos);
            }
        }

        if (src instanceof AL1ListSegmentContainingMessage) {
            final List<AdtAL1AllergySegment> al1List = ((AL1ListSegmentContainingMessage)src).getAL1List();
            if (CollectionUtils.isNotEmpty(al1List)) {
                List<AL1AllergySegmentDto> al1AllergySegmentDtos = new ArrayList<>();
                al1AllergySegmentTransformer.convertList(al1List, al1AllergySegmentDtos);
                target.setAdtSegmentsAL1(al1AllergySegmentDtos);
            }
        }

        if (src instanceof PD1SegmentContainingMessage) {
            final AdtPD1AdditionalDemographicSegment pd1 = ((PD1SegmentContainingMessage)src).getPd1();
            target.setAdtSegmentPD1(pd1AdditionalDemographicSegmentTransformer.convert(pd1));
        }

    }

}
