package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.security.projection.entity.AvatarSecurityAwareEntity;
import com.scnsoft.eldermark.dao.AvatarDao;
import com.scnsoft.eldermark.dto.AvatarUpdateData;
import com.scnsoft.eldermark.entity.Avatar;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.service.storage.AvatarFileStorage;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeType;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class AvatarServiceImpl implements AvatarService {

    @Autowired
    private AvatarDao avatarDao;

    @Autowired
    private AvatarFileStorage avatarFileStorage;

    @Override
    public Pair<byte[], MediaType> downloadById(Long id) {
        var avatar = avatarDao.findById(id).orElseThrow();
        return avatarFileStorage.loadAsBytesWithMediaType(avatar.getAvatarName());
    }

    @Override
    public void update(AvatarUpdateData data) {
        var entity = data.getEntityWithAvatar();
        if (data.getBytes() != null) {
            Avatar avatar;
            if (entity.getAvatar() != null) {
                avatar = entity.getAvatar();
                avatarFileStorage.delete(avatar.getAvatarName());
            } else {
                avatar = new Avatar();
            }

            var newName = avatarFileStorage.save(
                    data.getBytes(),
                    StringUtils.isEmpty(data.getName())
                            ? generateAvatarName(data.getMimeType())
                            : data.getName()
            );
            avatar.setAvatarName(newName);
            avatarDao.save(avatar);

            entity.setAvatar(avatar);
            entity.setAvatarId(avatar.getId());
        } else if (data.getShouldRemoveAvatar()) {
            var avatar = data.getEntityWithAvatar().getAvatar();
            if (avatar != null) {
                avatarFileStorage.delete(avatar.getAvatarName());
                avatarDao.delete(avatar);

                entity.setAvatarId(null);
                entity.setAvatar(null);
            }
        }
    }

    private String generateAvatarName(String mimeType) {
        var extension = MimeType.valueOf(mimeType).getSubtype();
        return UUID.randomUUID() + "." + extension;
    }

    @Override
    public void deleteById(Long id) {
        var avatar = avatarDao.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessExceptionType.NOT_FOUND));
        avatarFileStorage.delete(avatar.getAvatarName());
        avatarDao.deleteById(id);
        avatarDao.flush();
    }

    @Override
    @Transactional(readOnly = true)
    public AvatarSecurityAwareEntity findSecurityAware(Long id) {
        return avatarDao.findById(id, AvatarSecurityAwareEntity.class).orElseThrow();
    }
}
