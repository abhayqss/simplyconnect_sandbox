package com.scnsoft.eldermark.services.marketplace.internal;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author phomal
 * Created on 11/28/2017.
 */
abstract class AbstractDtoListSupplier<T extends KeyValueDto> implements MemoizingDtoListSupplier<T>, Supplier<List<T>> {

    private final Supplier<List<T>> memoized = Suppliers.memoizeWithExpiration(this, 60, TimeUnit.MINUTES);

    @Override
    public List<T> getMemoized() {
        return memoized.get();
    }

}
