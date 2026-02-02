package com.scnsoft.eldermark.beans.pagination;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PrependedElementsPageable implements Pageable {

    private final Pageable delegate;
    private final int prependedCount;

    public PrependedElementsPageable(Pageable delegate, int prependedCount) {
        this.delegate = delegate;
        this.prependedCount = prependedCount;
    }

    @Override
    public boolean isPaged() {
        return delegate.isPaged();
    }

    @Override
    public boolean isUnpaged() {
        return delegate.isUnpaged();
    }

    @Override
    public int getPageNumber() {
        return delegate.getPageNumber();
    }

    @Override
    public int getPageSize() {
        if (delegate.getOffset() - prependedCount < 0) {
            return delegate.getPageSize() - prependedCount;
        }
        return delegate.getPageSize();
    }

    @Override
    public long getOffset() {
        return Math.max(delegate.getOffset() - prependedCount, 0);
    }

    @Override
    public Sort getSort() {
        return delegate.getSort();
    }

    @Override
    public Sort getSortOr(Sort sort) {
        return delegate.getSortOr(sort);
    }

    @Override
    public Pageable next() {
        return new PrependedElementsPageable(delegate.next(), prependedCount);
    }

    @Override
    public Pageable previousOrFirst() {
        return new PrependedElementsPageable(delegate.previousOrFirst(), prependedCount);
    }

    @Override
    public Pageable first() {
        return new PrependedElementsPageable(delegate.first(), prependedCount);
    }

    @Override
    public boolean hasPrevious() {
        return delegate.hasPrevious();
    }

    public Pageable getDelegate() {
        return delegate;
    }

    public int getPrependedCount() {
        return prependedCount;
    }
}
