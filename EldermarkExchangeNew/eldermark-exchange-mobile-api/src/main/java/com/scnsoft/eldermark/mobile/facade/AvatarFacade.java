package com.scnsoft.eldermark.mobile.facade;

import javax.servlet.http.HttpServletResponse;

public interface AvatarFacade {

    void downloadById(Long id, HttpServletResponse response);
}
