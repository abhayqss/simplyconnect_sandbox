package com.scnsoft.eldermark.service.transformer.populator;

import com.scnsoft.eldermark.entity.Participant;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonAddress;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.web.entity.ParticipantDto;
import com.scnsoft.eldermark.web.entity.ParticipantListItemDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ParticipantPopulator implements Populator<Participant, ParticipantDto> {


    @Override
    public void populate(final Participant src, final ParticipantDto target) {
        target.setParticipantType(null);//TODO ggavrysh
        if (src.getTimeLow() != null) {
            target.setDateTime(src.getTimeLow().getTime());
        }
        target.setAddress(null);//TODO ggavrysh
        target.setTelecom(null);//TODO ggavrysh
//        final Person person = src.getPerson();
//        if (person != null) {
//            final List<PersonTelecom> telecoms = person.getTelecoms();
//            if (CollectionUtils.isNotEmpty(telecoms)) {
//                final PersonTelecom personTelecom = telecoms.get(0);
//                //TODO after they appear in DB
//                target.setTelecom(null);
//            }
//
//            final List<PersonAddress> addresses = person.getAddresses();
//            if (CollectionUtils.isNotEmpty(addresses)) {
//
//            }
//        }

    }
}
