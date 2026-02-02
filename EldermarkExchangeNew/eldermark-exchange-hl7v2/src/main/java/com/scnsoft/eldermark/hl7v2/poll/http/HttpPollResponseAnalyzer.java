package com.scnsoft.eldermark.hl7v2.poll.http;

import java.net.http.HttpResponse;

public interface HttpPollResponseAnalyzer {

    boolean isNoFurtherMessagesResponse(HttpResponse<String> response);
}
