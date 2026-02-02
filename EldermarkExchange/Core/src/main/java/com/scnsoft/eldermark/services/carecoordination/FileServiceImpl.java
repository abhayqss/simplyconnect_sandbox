package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.dao.carecoordination.CareCoordinationCommunityDao;
import com.scnsoft.eldermark.dao.carecoordination.CareCoordinationOrganizationDao;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.shared.carecoordination.OrganizationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by averazub on 5/20/2016.
 */
@Service
public class FileServiceImpl implements FileService{
    @Autowired
    OrganizationService organizationService;

    @Autowired
    CareCoordinationOrganizationDao organizationDao;

    @Autowired
    CareCoordinationCommunityDao communityDao;

    @Autowired
    CommunityCrudService communityCrudService;

    @Value("${image.upload.basedir}")
    String pictureUploadBasedir;

    @Value("${image.logo.height}")
    Integer logoPictureHeight;

    @Override
    public String uploadOrganizationLogo(Long organizationId, MultipartFile logo) {
        deleteOrganizationLogo(organizationId);
        String name = "logo_"+organizationId+"_"+logo.getOriginalFilename().replace(' ', '_').replace('%', '_');
        try {
            uploadAndResizeLogo(name, logo.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("There were some error while uploading logo for organization id="+organizationId);
        }
        organizationDao.updateMainLogoPath(organizationId, name);
        return name;
    }

    @Override
    public String uploadCommunityLogo(Long communityId, MultipartFile logo) {
        deleteCommunityLogo(communityId);
        String name = "logo_comm_"+communityId+"_"+logo.getOriginalFilename().replace(' ', '_').replace('%', '_');
        try {
            uploadAndResizeLogo(name, logo.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("There were some error while uploading logo for community id="+communityId);
        }
        communityDao.updateMainLogoPath(communityId, name);
        return name;
    }

    protected void uploadAndResizeLogo(String fileName, InputStream inputStream) throws IOException {
        File f = new File(pictureUploadBasedir+File.separator+fileName);
        if (!new File(pictureUploadBasedir).exists()) new File(pictureUploadBasedir).mkdirs();
        f.createNewFile();
        BufferedImage image = ImageIO.read(inputStream);
        if (logoPictureHeight<image.getHeight()) {
            double imageRatio = (double) image.getWidth() / (double) image.getHeight();
            int newWidth = new Double(logoPictureHeight * imageRatio).intValue();
            image = resizeImage(image, image.getType(), newWidth, logoPictureHeight);
        }
        String ext = fileName.substring(fileName.lastIndexOf('.')+1);
        ImageIO.write(image, ext, f);

    }

    @Override
    public void deleteOrganizationLogo(Long organizationId) {
        OrganizationDto org = organizationService.getOrganization(organizationId);
        String mainLogoPath = org.getMainLogoPath();
        if ((mainLogoPath!=null) && (!"".equals(mainLogoPath))) {
            File toRemove = new File(pictureUploadBasedir+File.separator+mainLogoPath);
            if (!toRemove.exists()) throw new RuntimeException("Cannot find logo image in filesystem for path specified for organization"+org.getName());
            toRemove.delete();
            organizationDao.updateMainLogoPath(organizationId, null);
        }
    }

    @Override
    public void deleteCommunityLogo(Long communityId) {
        Organization community = communityDao.findOne(communityId);
        String mainLogoPath = community.getMainLogoPath();
        if ((mainLogoPath!=null) && (!"".equals(mainLogoPath))) {
            File toRemove = new File(pictureUploadBasedir+File.separator+mainLogoPath);
            if (!toRemove.exists()) throw new RuntimeException("Cannot find logo image in filesystem for path specified for community"+community.getName());
            toRemove.delete();
            communityDao.updateMainLogoPath(communityId, null);
        }
    }


    private static BufferedImage resizeImage(BufferedImage originalImage, int type, int width, int height){
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }


}
