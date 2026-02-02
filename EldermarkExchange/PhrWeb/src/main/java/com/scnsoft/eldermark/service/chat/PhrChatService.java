package com.scnsoft.eldermark.service.chat;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.scnsoft.eldermark.dao.DatabasesDao;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.phr.chat.PhrChatCompanyDao;
import com.scnsoft.eldermark.dao.phr.chat.PhrChatTimezoneDao;
import com.scnsoft.eldermark.dao.phr.chat.PhrChatUserDao;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.entity.phr.chat.PhrChatCompany;
import com.scnsoft.eldermark.entity.phr.chat.PhrChatUser;

@Service
public class PhrChatService {

    @Value("${phr.chat.server.host.address}")
    private String hostChatUrl;

    @Value("${phr.chat.server.login.url}")
    private String serverLoginURL;

    @Value("${phr.chat.server.handset.register}")
    private String handsetRegisterationURL;

    @Value("${phr.chat.server.company.register}")
    private String companyRegisterationURL;

    @Value("${phr.chat.server.whoami.thread}")
    private String whoamiThreadEndPoint;

    @Value("${phr.chat.server.create.thread}")
    private String createThreadEndPoint;

    @Autowired
    private PhrChatUserDao phrChatUserDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private PhrChatCompanyDao phrChatCompanyDao;

    @Autowired
    private DatabasesDao databasesDao;

    @Autowired
    private PhrChatTimezoneDao phrChatTimezoneDao;

    private static Long TIME_ZONE = 1L;

    private static String COMPANY_PASS = "$2a$14$3DTAybV7O9u.iB7S6B3AquwTCu4BVfdx7pJAPA4VIjyqrcLYUbtG.";

    private static final Logger logger = LoggerFactory.getLogger(PhrChatService.class);

    public String getLoginAuthTokenFromChatServer(Long noifyUserId, String uuid, String pushNotificationToken,
            String deviceType) {
        User user = userDao.findOne(noifyUserId);
        Employee employee = employeeDao.get(user.getEmployeeId());
        Database company = databasesDao.getDatabaseById(employee.getDatabaseId());
        PhrChatCompany phrChatCompany = phrChatCompanyDao.findByNotifyCompanyId(company.getId());
        if (phrChatCompany == null) {
            registerNewCompany(user);
            phrChatCompany = phrChatCompanyDao.findByNotifyCompanyId(user.getDatabase().getId());
        }

        PhrChatUser phrChatUser = phrChatUserDao.findByNotifyUserId(noifyUserId);
        if (phrChatUser == null) {
            userCreatePhrChat(noifyUserId);
        }
        registerNewHandset(uuid, pushNotificationToken, phrChatUser, deviceType);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("company_id", phrChatUser.getPhrChatCompany().getId().toString());
        map.add("notifyUserId", noifyUserId.toString());
        map.add("uuid", uuid);
        map.add("type", deviceType);
        map.add("Whoami", "1");
        map.add("deviceToken", pushNotificationToken);

        return httpPostRequest(serverLoginURL, map, null);
    }

    private boolean registerNewHandset(String uuid, String pushNotificationToken, PhrChatUser phrChatUser,
            String deviceType) {

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("company_id", phrChatUser.getPhrChatCompany().getId().toString());
        map.add("pn_token", pushNotificationToken);
        map.add("uuid", uuid);
        map.add("type", deviceType);
        map.add("device_name", phrChatUser.getId() + phrChatUser.getName());

        httpPostRequest(handsetRegisterationURL, map, null);
        return true;
    }

    private boolean registerNewCompany(User user) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("notifyCompanyId", user.getDatabase().getId().toString());
        map.add("name", user.getDatabase().getName());
        map.add("password", "123456");
        map.add("namespace", user.getDatabase().getAlternativeId());
        map.add("enabled", "true");

        httpPostRequest(companyRegisterationURL, map, null);
        return true;
    }

    public String getMessageThread(Long noifyUserId, String mobileAuthToken) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        if (noifyUserId != null) {
            PhrChatUser phrChatUser = phrChatUserDao.findByNotifyUserId(noifyUserId);
            if (phrChatUser != null) {
                map.add("user_id", phrChatUser.getId().toString());
            }
            return httpPostRequest(whoamiThreadEndPoint, map, mobileAuthToken);
        }

        return null;
    }

    public String setMessageThread(Long noifyUserId, List<Long> participantsUserIds, String mobileAuthToken) {
        List<Long> chatUserList = phrChatUserDao.findIdByNotifyUserIdIn(participantsUserIds);
        String participantChatIds = StringUtils.join(chatUserList, ",");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("participants", participantChatIds.toString());

        PhrChatUser phrChatUser = phrChatUserDao.findByNotifyUserId(noifyUserId);
        if (phrChatUser != null) {
            map.add("user_id", phrChatUser.getId().toString());
            return httpPostRequest(whoamiThreadEndPoint, map, mobileAuthToken);
        }
        return null;
    }

    public boolean userCreatePhrChat(Long userId) {
        User user = userDao.findOne(userId);
        PhrChatUser phrChatUser = new PhrChatUser();
        phrChatUser.setNotifyUserId(user.getId());
        phrChatUser.setName(user.getFullName() != null ? user.getFullName() : user.getFirstName());
        phrChatUser.setRole("user");
        phrChatUser.setCreatedAt(DateTime.now(DateTimeZone.UTC).toDate());
        phrChatUser.setUpdatedAt(DateTime.now(DateTimeZone.UTC).toDate());
        Employee employee = employeeDao.get(user.getEmployeeId());
        Database company = databasesDao.getDatabaseById(employee.getDatabaseId());
        if (phrChatCompanyDao.findByNotifyCompanyId(company.getId()) == null) {
            createChatCompany(company);
        }
        phrChatUser.setPhrChatCompany(phrChatCompanyDao.findByNotifyCompanyId(company.getId()));
        phrChatUser.setLogged(0L);
        phrChatUser.setPhrChatTimezone(phrChatTimezoneDao.findOne(TIME_ZONE));
        phrChatUserDao.save(phrChatUser);
        return true;
    }

    private void createChatCompany(Database company) {
        PhrChatCompany phrChatCompany = new PhrChatCompany();
        phrChatCompany.setNotifyCompanyId(company.getId());
        phrChatCompany.setPassword(COMPANY_PASS);
        phrChatCompany.setEnabled(true);
        phrChatCompany.setName(company.getName());
        phrChatCompany.setNamespace(company.getAlternativeId().replaceAll(" _", ""));
        phrChatCompany.setCreatedAt(DateTime.now(DateTimeZone.UTC).toDate());
        phrChatCompany.setUpdatedAt(DateTime.now(DateTimeZone.UTC).toDate());
        phrChatCompanyDao.save(phrChatCompany);
    }

    private String httpPostRequest(String endUrl, MultiValueMap<String, String> map, String mobileAuthToken) {
        try {
            logger.info("phr chat request paramter {0} end url {1}", map, endUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("authorization", mobileAuthToken);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
            String url = hostChatUrl + endUrl;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            logger.debug("phr chat response {}", response.getBody());
            if (response.getStatusCode().is2xxSuccessful())
                return response.getBody();
            else
                logger.info("httpPost Response is not success with endpoint URL ", endUrl, "mobileAuthToken as ",
                        mobileAuthToken);
            return null;
        } catch (Exception e) {
            logger.info("Chat API calling exception {0}", e);
            return null;
        }
    }
    
}
