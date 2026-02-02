package com.scnsoft.eldermark.converter.dto2entity.prospect;

import com.scnsoft.eldermark.converter.base.ItemConverter;
import com.scnsoft.eldermark.dto.AvatarUpdateData;
import com.scnsoft.eldermark.dto.ProspectSaveData;
import com.scnsoft.eldermark.dto.prospect.ProspectDto;
import com.scnsoft.eldermark.entity.prospect.Prospect;
import com.scnsoft.eldermark.service.ProspectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class ProspectDataConverter implements Converter<ProspectDto, ProspectSaveData> {

    @Autowired
    private ItemConverter<ProspectDto, Prospect> prospectEntityConverter;

    @Autowired
    private ProspectService prospectService;

    @Override
    public ProspectSaveData convert(ProspectDto source) {
        var target = new ProspectSaveData();

        var prospect = source.getId() != null
                ? prospectService.findById(source.getId())
                : new Prospect();

        target.setEntity(prospect);

        target.setProspectAvatar(new AvatarUpdateData(prospect, source.getAvatar(), source.getShouldRemoveAvatar()));

        var originalSecondOccupant = prospect.getSecondOccupant();

        prospectEntityConverter.convert(source, prospect);

        if (prospect.getSecondOccupant() == null) {
            if (originalSecondOccupant != null && originalSecondOccupant.getAvatar() != null) {
                target.setSecondOccupantAvatar(new AvatarUpdateData(originalSecondOccupant, true));
            }
        } else {
            target.setSecondOccupantAvatar(new AvatarUpdateData(
                    prospect.getSecondOccupant(),
                    source.getSecondOccupant().getAvatar(),
                    source.getSecondOccupant().getShouldRemoveAvatar()
            ));
        }

        return target;
    }
}
