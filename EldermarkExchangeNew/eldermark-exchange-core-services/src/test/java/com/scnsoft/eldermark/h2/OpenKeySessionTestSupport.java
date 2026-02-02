package com.scnsoft.eldermark.h2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class OpenKeySessionTestSupport {
    private static final Logger logger = LoggerFactory.getLogger(OpenKeySessionTestSupport.class);

    private static ThreadLocal<Set<Integer>> holder = ThreadLocal.withInitial(HashSet::new);
    private static ThreadLocal<Boolean> allowOpenSameSession = ThreadLocal.withInitial(() -> true);

    public static void reset() {
        holder.get().clear();
    }

    public static void addSession(Integer sessionId) {
        if (sessionId != null) {
            logger.info("added session {}", sessionId);
            if (!holder.get().add(sessionId) && !allowOpenSameSession.get()) {
                throw new IllegalStateException("Key already opened for session");
            }
        }
    }

    public static int size() {
        return holder.get().size();
    }

    public static Set<Integer> get() {
        return new HashSet<>(holder.get());
    }

    public static void setAllowOpenSameSession(boolean value) {
        allowOpenSameSession.set(value);
    }
}
