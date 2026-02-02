package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Payer;

/**
 * Created with IntelliJ IDEA.
 * User: knetkachou
 * Date: 10/22/13
 * Time: 1:30 PM
 *
 * @deprecated Transition to Spring Data repositories is recommended. Use {@link com.scnsoft.eldermark.dao.healthdata.PolicyActivityDao PolicyActivityDao} instead.
 */
public interface PayerDao extends ResidentAwareDao<Payer> {
}
