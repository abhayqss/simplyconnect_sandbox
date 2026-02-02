package com.scnsoft.eldermark.framework.dao.source;

import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import com.scnsoft.eldermark.framework.dao.source.filters.DeletedKeysReadFilter;
import com.scnsoft.eldermark.framework.model.source.DeletedKeysData;

import java.util.List;

public interface DeletedKeysSourceDao {

    List<DeletedKeysData> read(Sql4DOperations sql4DOperations, DeletedKeysReadFilter filter);

}
