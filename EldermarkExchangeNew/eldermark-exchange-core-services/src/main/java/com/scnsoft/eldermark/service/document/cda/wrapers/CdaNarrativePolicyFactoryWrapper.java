package com.scnsoft.eldermark.service.document.cda.wrapers;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Component;

/**
 * Inits PolicyFactory which defines allowed content of cda narrative block.
 * This factory just allows tags and attributes to be present in input without
 * checking their mutual relation (i. e. some attributes should be present in
 * some specific tags)
 *
 * <p>
 * Allowed tags were taken from the page below.
 * https://data.developer.nhs.uk/specifications/NHS-CDA-eDischarge/AssociatedSpecifications/NarrativeBlockTagsGuidance/Documents/narrative.htm
 */
@Component
public class CdaNarrativePolicyFactoryWrapper extends AbstractPolicyFactoryWrapper implements PolicyFactoryWrapper {

    @Override
    protected PolicyFactory initPolicyFactory() {
        return new HtmlPolicyBuilder()
                // First off, allow cda specific entries
                .allowElements("text", "content", "linkHTML", "sub", "sup", "br", "footnote", "footnoteRef",
                        "renderMultiMedia", "paragraph", "caption", "list", "item", "table", "col", "colgroup", "thead",
                        "tfoot", "tbody", "tr", "th", "td")
                .allowAttributes("referencedObject", "language", "rules", "title", "align", "axis", "listType",
                        "colspan", "charoff", "cellpadding", "scope", "rowspan", "rel", "ID", "href", "IDREF", "abbr",
                        "summary", "border", "headers", "rev", "mediaType", "valign", "styleCode", "name", "width",
                        "cellspacing", "char", "revised", "frame", "span")
                .globally().requireRelsOnLinks("nofollow", "noopener").toFactory()
                // Secondly, allow most common html entries
                .and(Sanitizers.BLOCKS).and(Sanitizers.FORMATTING).and(Sanitizers.IMAGES).and(Sanitizers.LINKS)
                .and(Sanitizers.STYLES).and(Sanitizers.TABLES);
    }
}
