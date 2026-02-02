package com.scnsoft.eldermark.services.marketplace.internal;

import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;

import java.util.List;

/**
 * @author phomal
 * Created on 11/28/2017.
 */
public interface  MemoizingDtoListSupplier<T extends KeyValueDto> {
    List<T> getMemoized();
}
