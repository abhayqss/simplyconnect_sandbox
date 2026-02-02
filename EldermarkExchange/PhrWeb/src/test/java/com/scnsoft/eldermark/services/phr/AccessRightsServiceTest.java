package com.scnsoft.eldermark.services.phr;

import com.scnsoft.eldermark.dao.phr.AccessRightDao;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.ResidentCareTeamMember;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 6/19/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class AccessRightsServiceTest {


    @Mock
    private AccessRightDao accessRightDao;

    @InjectMocks
    private AccessRightsService accessRightsService;

    private Map<AccessRight.Code, AccessRight> accessRightMap = new HashMap<>();

    @Before
    public void postConstruct() {
        // Mockito expectations
        for (AccessRight.Code code : AccessRight.Code.values()) {
            AccessRight accessRight = new AccessRight();
            accessRight.setCode(code);
            accessRight.setDisplayName(code.toString());
            accessRight.setId((long) code.ordinal());
            when(accessRightDao.findByCode(code)).thenReturn(accessRight);

            accessRightMap.put(code, accessRight);
        }

        // call postConstruct to init cache
        accessRightsService.postConstruct();
    }

    @Test
    public void testGetAccessRights() throws Exception {
        // Expected objects
        final Set<AccessRight> accessRights = new HashSet<>();
        accessRights.add(accessRightMap.get(AccessRight.Code.MY_PHR));
        accessRights.add(accessRightMap.get(AccessRight.Code.MEDICATIONS_LIST));
        final ResidentCareTeamMember ctm = new ResidentCareTeamMember();
        ctm.setAccessRights(accessRights);

        final Map<AccessRight.Code, Boolean> expectedMap = new HashMap<>();
        expectedMap.put(AccessRight.Code.MY_PHR, Boolean.TRUE);
        expectedMap.put(AccessRight.Code.MEDICATIONS_LIST, Boolean.TRUE);
        expectedMap.put(AccessRight.Code.EVENT_NOTIFICATIONS, Boolean.FALSE);
        expectedMap.put(AccessRight.Code.MY_CT_VISIBILITY, Boolean.FALSE);

        // Execute the method being tested
        Map<AccessRight.Code, Boolean> result = AccessRightsService.getAccessRights(ctm);

        // Validate
        assertEquals(expectedMap, result);
    }

    @Test
    public void testGetAccessRight() throws Exception {
        // Expected objects
        final AccessRight accessRight = accessRightMap.get(AccessRight.Code.EVENT_NOTIFICATIONS);

        // Execute the method being tested
        AccessRight result = accessRightsService.getAccessRight(AccessRight.Code.EVENT_NOTIFICATIONS);

        // Validate
        assertEquals(accessRight, result);
    }

    @Test
    public void testGetDefaultAccessRights() throws Exception {
        // Execute the method being tested
        accessRightsService.getDefaultAccessRights();
    }

    @Test
    public void testUpdateAccessRights() throws Exception {
        // Expected objects
        final Set<AccessRight> accessRights = new HashSet<>();
        accessRights.add(accessRightMap.get(AccessRight.Code.MY_PHR));
        accessRights.add(accessRightMap.get(AccessRight.Code.MEDICATIONS_LIST));
        final ResidentCareTeamMember ctm = new ResidentCareTeamMember();
        ctm.setAccessRights(accessRights);

        final AccessRight expectedAccessRights[] = {
                accessRightMap.get(AccessRight.Code.MY_PHR),
                accessRightMap.get(AccessRight.Code.EVENT_NOTIFICATIONS)};

        // Execute the method being tested
        accessRightsService.updateAccessRights(ctm, new HashMap<AccessRight.Code, Boolean>() {{
            put(AccessRight.Code.MY_PHR, Boolean.TRUE);
            put(AccessRight.Code.MEDICATIONS_LIST, Boolean.FALSE);
            put(AccessRight.Code.EVENT_NOTIFICATIONS, Boolean.TRUE);
            put(AccessRight.Code.MY_CT_VISIBILITY, Boolean.FALSE);
        }});

        // Validation
        assertThat(ctm.getAccessRights(), containsInAnyOrder(expectedAccessRights));
    }

    @Test
    public void testCheckHasAccessRight() throws Exception {
        // Expected objects
        final Set<AccessRight> accessRights = new HashSet<>();
        accessRights.add(accessRightMap.get(AccessRight.Code.MY_PHR));
        accessRights.add(accessRightMap.get(AccessRight.Code.MEDICATIONS_LIST));
        final ResidentCareTeamMember ctm = new ResidentCareTeamMember();
        ctm.setAccessRights(accessRights);

        // Execute the method being tested
        boolean result = accessRightsService.checkHasAccessRight(ctm, AccessRight.Code.MY_PHR);
        boolean result2 = accessRightsService.checkHasAccessRight(ctm, AccessRight.Code.EVENT_NOTIFICATIONS);

        // Validation
        assertEquals(true, result);
        assertEquals(false, result2);
    }

    @Test
    public void testCheckHasAccessRightNull() throws Exception {
        // Execute the method being tested
        boolean result = accessRightsService.checkHasAccessRight(null, AccessRight.Code.MY_PHR);

        // Validation
        assertEquals(false, result);
    }

}
