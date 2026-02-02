package com.scnsoft.eldermark.util;

import com.scnsoft.eldermark.entity.phr.User;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author phomal
 * Created on 8/17/2017.
 */
public class MockitoAnswers {

    public static Answer<User> returnsPersistedUser() {
        return new Answer<User>() {
                @Override
                public User answer(InvocationOnMock invocation) throws Throwable {
                    final User user = invocation.getArgumentAt(0, User.class);
                    if (user.getPhone() == null || user.getEmail() == null) {
                        // in runtime it may be SqlServerException
                        throw new NullPointerException();
                    }
                    return user;
                }
            };
    }

}
