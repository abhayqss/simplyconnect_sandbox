package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.UserAvatarDao;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.entity.phr.UserAvatar;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.web.security.PhrSecurityUtils;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author phomal
 * Created on 5/29/2017.
 */
@Service
@Transactional
public class AvatarService extends BasePhrService {

    @Autowired
    UserAvatarDao userAvatarDao;

    String getPhotoUrl(final Long userId) {
        if (userId == null) {
            return null;
        }
        UserAvatar avatar = userAvatarDao.getByUserId(userId);
        if (avatar == null) {
            return null;
        }
        return "/phr/" + userId + "/profile/avatar";
    }

    public Boolean deleteAvatar(Long userId) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);

        return (userAvatarDao.deleteByUserId(userId) == 1);
    }

    public void downloadAvatar(Long userId, HttpServletResponse response) {
        final UserAvatar avatar = userAvatarDao.getByUserId(userId);
        if (avatar == null) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            throw new PhrException(PhrExceptionType.AVATAR_NOT_FOUND);
        } else {
            response.setContentType(avatar.getContentType());
            try {
                FileCopyUtils.copy(new ByteArrayInputStream(avatar.getFile()), response.getOutputStream());
            } catch (IOException e) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                throw new PhrException(PhrExceptionType.DOWNLOAD_ERROR);
            }
        }
    }

    public String getAvatarAsBase64(Long userId) {
        final UserAvatar avatar = userAvatarDao.getByUserId(userId);
        if (avatar == null) {
            throw new PhrException(PhrExceptionType.AVATAR_NOT_FOUND);
        } else {
            final String photoBase64 = Base64.encodeBase64String(avatar.getFile());
            return photoBase64;
        }
    }

    public UserAvatar setAvatar(Long userId, MultipartFile photo) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);

        try {
            UserAvatar avatar = userAvatarDao.getByUserId(userId);
            if (avatar != null) {
                avatar.setFile(photo.getBytes());
                avatar.setContentType(photo.getContentType());
                return userAvatarDao.save(avatar);
            } else {
                return createAvatar(userId, photo.getBytes(), photo.getContentType());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new PhrException(PhrExceptionType.UPLOAD_ERROR);
        }
    }

    public UserAvatar setAvatar(Long userId, String photoBase64) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        if (!Base64.isBase64(photoBase64)) {
            throw new PhrException(PhrExceptionType.UPLOAD_ERROR);
        }

        byte[] imageBytes = Base64.decodeBase64(photoBase64);

        UserAvatar avatar = userAvatarDao.getByUserId(userId);
        if (avatar != null) {
            avatar.setFile(imageBytes);
            avatar.setContentType(null);
            return userAvatarDao.save(avatar);
        } else {
            return createAvatar(userId, imageBytes, null);
        }
    }

    private UserAvatar createAvatar(Long userId, byte[] imageBytes, String contentType) {
        final User user = userDao.getOne(userId);

        UserAvatar avatar = new UserAvatar();
        avatar.setUser(user);
        avatar.setUserId(userId);
        avatar.setContentType(contentType);
        avatar.setFile(imageBytes);
        return userAvatarDao.save(avatar);
    }

}
