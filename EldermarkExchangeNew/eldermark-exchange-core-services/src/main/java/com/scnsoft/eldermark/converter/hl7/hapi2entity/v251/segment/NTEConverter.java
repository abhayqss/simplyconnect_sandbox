package com.scnsoft.eldermark.converter.hl7.hapi2entity.v251.segment;

import ca.uhn.hl7v2.model.v251.segment.NTE;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0105SourceOfComment;
import com.scnsoft.eldermark.entity.xds.segment.NTENotesAndComments;
import org.springframework.stereotype.Service;

@Service
public class NTEConverter extends HL7SegmentConverter<NTE, NTENotesAndComments> {

    @Override
    protected NTENotesAndComments doConvert(NTE source) {
        var nte = new NTENotesAndComments();
        nte.setSetId(dataTypeService.getValue(source.getNte1_SetIDNTE()));
        nte.setSourceOfComment(dataTypeService.createID(source.getNte2_SourceOfComment(), HL7CodeTable0105SourceOfComment.class));
        nte.setComments(dataTypeService.createStringList(source.getNte3_Comment()));
        return nte;
    }
}
