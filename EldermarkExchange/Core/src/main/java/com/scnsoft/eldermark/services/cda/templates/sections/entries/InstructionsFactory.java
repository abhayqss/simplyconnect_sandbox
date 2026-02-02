package com.scnsoft.eldermark.services.cda.templates.sections.entries;

import com.scnsoft.eldermark.entity.Instructions;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.cda.Act;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author phomal
 * Created on 4/13/2018.
 */
@Component
public class InstructionsFactory {

    private final CcdCodeFactory ccdCodeFactory;

    @Autowired
    public InstructionsFactory(CcdCodeFactory ccdCodeFactory) {
        this.ccdCodeFactory = ccdCodeFactory;
    }

    public Instructions parseInstructions(Act act, Resident resident) {
        if (!CcdParseUtils.hasContent(act) || resident == null) {
            return null;
        }

        final Instructions instructions = new Instructions();
        instructions.setDatabase(resident.getDatabase());

        instructions.setCode(ccdCodeFactory.convert(act.getCode()));
        instructions.setText(CcdTransform.EDtoString(act.getText(), instructions.getCode()));

        return instructions;
    }

    public <A extends Act> List<Instructions> parseInstructions(Resident resident, EList<A> instructionss) {
        if (CollectionUtils.isEmpty(instructionss)) {
            return Collections.emptyList();
        }

        // TODO test on real examples
        final List<Instructions> result = new ArrayList<>();
        for (Act ccdInstructions : instructionss) {
            final Instructions instruction = this.parseInstructions(ccdInstructions, resident);
            if (instruction != null) {
                result.add(instruction);
            }
        }

        return result;
    }

}
