package com.scnsoft.eldermark.consana.sync.server.services.consumers;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface CommitWrapper {

    <T> T executeWithCommit(Supplier<T> resultSupplier, Consumer<Exception> exceptionHandler);
}
