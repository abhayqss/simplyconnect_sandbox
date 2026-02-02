package com.scnsoft.eldermark.beans.reports.model.sdoh;

public final class SDohRowDescriptorFactory {

    private SDohRowDescriptorFactory() {
    }

    public static SDohRowDescriptor create(SdoHRowType type) {
        switch (type) {
            case IDN:
                return createForIDN();
            case REF:
                return createForREF();
            case FUL:
                return createForFUL();
            case NFF:
                return createForNFF();
        }
        throw new IllegalArgumentException("Unknown SDoH row type");
    }

    public static SDohRowDescriptor createForIDN() {
        return createBaseDescriptor();
    }

    public static SDohRowDescriptor createForREF() {
        return createBaseWithRequiredFullfilment();
    }

    public static SDohRowDescriptor createForFUL() {
        return createBaseWithRequiredFullfilment();
    }

    public static SDohRowDescriptor createForNFF() {
        return createBaseWithRequiredFullfilment();
    }

    private static SDohRowDescriptor createBaseDescriptor() {
        var result = new SDohRowDescriptor();

        result.setSubmitterName(new SdohFieldDescriptor(true, 50));
        result.setSourceSystem(new SdohFieldDescriptor(true, 10));
        result.setMemberLastName(new SdohFieldDescriptor(true, 30));
        result.setMemberFirstName(new SdohFieldDescriptor(true, 30));
        result.setMemberMiddleName(new SdohFieldDescriptor(false, 30));
        result.setMemberDateOfBirth(new SdohFieldDescriptor(true, 8));
        result.setMemberGender(new SdohFieldDescriptor(true, 10));
        result.setMemberAddress(new SdohFieldDescriptor(true, 50));
        result.setMemberCity(new SdohFieldDescriptor(true, 50));
        result.setMemberState(new SdohFieldDescriptor(true, 2));
        result.setMemberZipCode(new SdohFieldDescriptor(true, 10));
        result.setMemberHicn(new SdohFieldDescriptor(false, 16));
        result.setMemberCardId(new SdohFieldDescriptor(true, 50));
        result.setServiceDate(new SdohFieldDescriptor(true, 8));
        result.setIdentificationReferralFulfillment(new SdohFieldDescriptor(true, 3));
        result.setIcdOrMbrAttributionCode(new SdohFieldDescriptor(true, 8));
        result.setReferralFulfillmentProgramName(new SdohFieldDescriptor(false, 50));
        result.setReferralFulfillmentProgramAddress(new SdohFieldDescriptor(false, 50));
        result.setReferralFulfillmentProgramPhone(new SdohFieldDescriptor(false, 10));
        result.setRefFulProgramType(new SdohFieldDescriptor(false, 50));
        result.setRefFulProgramSubtype(new SdohFieldDescriptor(false, 50));

        return result;
    }

    private static void requireReferralFulfillment(SDohRowDescriptor result) {
        result.getReferralFulfillmentProgramName().setRequired(true);
        result.getReferralFulfillmentProgramAddress().setRequired(true);
        result.getReferralFulfillmentProgramPhone().setRequired(true);
        result.getRefFulProgramType().setRequired(true);
        result.getRefFulProgramSubtype().setRequired(true);
    }

    private static SDohRowDescriptor createBaseWithRequiredFullfilment() {
        var result = createBaseDescriptor();
        requireReferralFulfillment(result);
        return result;
    }
}
