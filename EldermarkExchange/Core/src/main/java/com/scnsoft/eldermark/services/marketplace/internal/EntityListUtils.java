package com.scnsoft.eldermark.services.marketplace.internal;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.scnsoft.eldermark.entity.marketplace.DisplayableNamedEntity;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author phomal
 * Created on 12/7/2017.
 */
public final class EntityListUtils {

    private EntityListUtils() {}

    public static <T extends DisplayableNamedEntity> void moveItemToStart(List<T> entities, final String displayName) {
        checkNotNull(displayName);
        final Optional<T> maybeItem = Iterables.tryFind(entities, new Predicate<T>() {
            @Override
            public boolean apply(T input) {
                return displayName.equalsIgnoreCase(input.getDisplayName());
            }
        });
        if (maybeItem.isPresent()) {
            entities.remove(maybeItem.get());
            entities.add(0, maybeItem.get());
        }
    }

    public static <T extends DisplayableNamedEntity> void moveItemToEnd(List<T> entities, final String displayName) {
        checkNotNull(displayName);
        final Optional<T> maybeItem = Iterables.tryFind(entities, new Predicate<T>() {
            @Override
            public boolean apply(T input) {
                return displayName.equalsIgnoreCase(input.getDisplayName());
            }
        });
        if (maybeItem.isPresent()) {
            entities.remove(maybeItem.get());
            entities.add(maybeItem.get());
        }
    }
    
    public static <T extends KeyValueDto> void moveKeyValueDtoinListofListsToEnd(List<List<T>> entities, final String label) {
        checkNotNull(label);
        
        for (List<T> entity : entities) {
            moveKeyValueDtoItemToEnd(entity,label);
        }
    }
    
    public static <T extends KeyValueDto> void moveKeyValueDtoItemToEnd(List<T> entities, final String label) {
        checkNotNull(label);
        final Optional<T> maybeItem = Iterables.tryFind(entities, new Predicate<T>() {
            @Override
            public boolean apply(T input) {
                return label.equalsIgnoreCase(input.getLabel());
            }
        });
        if (maybeItem.isPresent()) {
            entities.remove(maybeItem.get());
            entities.add(maybeItem.get());
        }
    }
    
    public static <T extends KeyValueDto> boolean checkByLabelIfListContains(List<T> sourceList, final T sourceEntity) {
    	for (T entity : sourceList) {
    		if(entity.getLabel().equals(sourceEntity.getLabel())) {
    			return true;
    		}
    	}
    	return false;
    }

}