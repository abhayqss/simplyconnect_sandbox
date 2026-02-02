package com.scnsoft.eldermark.services.phr;

import com.scnsoft.eldermark.dao.phr.ActivityDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.phr.InvitationActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author phomal
 * Created on 5/18/2017
 */
@Service
@Transactional
public class InvitationActivityService {

    @Autowired
    ActivityDao activityDao;

    public List<InvitationActivity> logInvitationAcceptedActivity(Employee invitee) {
        return logInvitationActivity(invitee, InvitationActivity.Status.ACCEPTED);
    }

    public List<InvitationActivity> logInvitationRejectedActivity(Employee invitee) {
        return logInvitationActivity(invitee, InvitationActivity.Status.REJECTED);
    }

    private List<InvitationActivity> logInvitationActivity(Employee invitee, InvitationActivity.Status status) {
        List<InvitationActivity> newActivities = new ArrayList<InvitationActivity>();
        List<InvitationActivity> invitationActivities = activityDao.findInvitationActivitiesByEmployeeAndStatus(invitee, InvitationActivity.Status.SENT);

        Date now = new Date();
        for (InvitationActivity activity : invitationActivities) {
            InvitationActivity newActivity = new InvitationActivity();
            newActivity.setEmployee(activity.getEmployee());
            newActivity.setPatientId(activity.getPatientId());
            newActivity.setStatus(status);
            newActivity.setDate(now);
            newActivity = activityDao.save(newActivity);
            newActivities.add(newActivity);
        }

        return newActivities;
    }

}
