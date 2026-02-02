package com.scnsoft.eldermark.service.twilio;

import com.scnsoft.eldermark.beans.twilio.messages.ServiceMessage;

interface ServiceMessageEncoder {

    String encode(ServiceMessage message);
}
