package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.service.storage.ImageFileStorage;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogoServiceImpl implements LogoService {

    @Autowired
    private ImageFileStorage imageFileStorage;

    @Override
    @Transactional(readOnly = true)
    public byte[] getLogoBytes(Community community) {
        var bytes = loadImage(StringUtils.isNotEmpty(community.getMainLogoPath())
                ? community.getMainLogoPath() : community.getAdditionalLogoPath());
        if (bytes != null) {
            return bytes;
        }
        return loadImage(StringUtils.isNotEmpty(community.getOrganization().getMainLogoPath())
                ? community.getOrganization().getMainLogoPath() : community.getOrganization().getAdditionalLogoPath());
    }

    private byte[] loadImage(String logoPath) {
        if (StringUtils.isNotEmpty(logoPath) && imageFileStorage.exists(logoPath)) {
            return imageFileStorage.loadAsBytes(logoPath);
        } else {
            return null;
        }
    }
}
