package com.scnsoft.eldermark.services.bluestone;

import java.util.concurrent.Future;

/**
 * Created by pzhurba on 24-Nov-15.
 */
public interface BlueStoneService {
    Future<Boolean> sendBlueStoneNotification(String content);
}
