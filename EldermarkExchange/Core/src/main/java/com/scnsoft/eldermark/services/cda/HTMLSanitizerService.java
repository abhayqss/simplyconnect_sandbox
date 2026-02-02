package com.scnsoft.eldermark.services.cda;

public interface HTMLSanitizerService {

    /**
     * Process HTML leaving only allowed tags and attributes in CCD Narrative block to
     * avoid XSS attack. List of allowed entries can be found here
     *
     *
     * @param input
     * @return
     */
    String sanitizeCdaNarrativeBlock(String input);

}
