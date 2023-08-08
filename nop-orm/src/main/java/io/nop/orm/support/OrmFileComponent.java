package io.nop.orm.support;

import io.nop.api.core.convert.ConvertHelper;
import io.nop.api.core.ioc.IBeanProvider;
import io.nop.commons.util.StringHelper;
import io.nop.orm.IOrmEntity;
import io.nop.orm.IOrmEntityFileStore;
import io.nop.orm.OrmConstants;

public class OrmFileComponent extends AbstractOrmComponent {
    public static final String PROP_NAME_filePath = "filePath";

    public String getFilePath() {
        return ConvertHelper.toString(internalGetPropValue(PROP_NAME_filePath));
    }

    public void setFilePath(String value) {
        internalSetPropValue(PROP_NAME_filePath, value);
    }

    @Override
    public void flushToEntity() {
        IOrmEntity entity = orm_owner();
        int propId = getColPropId(PROP_NAME_filePath);
        if (entity.orm_state().isUnsaved() || entity.orm_propDirty(propId)) {
            IBeanProvider beanProvider = entity.orm_enhancer().getBeanProvider();
            IOrmEntityFileStore fileStore = (IOrmEntityFileStore) beanProvider.getBean(OrmConstants.BEAN_ORM_ENTITY_FILE_STORE);
            String oldValue = (String) entity.orm_propOldValue(propId);

            String fileId = fileStore.decodeFileId(getFilePath());
            String propName = entity.orm_propName(propId);

            String bizObjName = getBizObjName();

            if (!StringHelper.isEmpty(oldValue)) {
                fileStore.detachFile(fileId, bizObjName, entity.orm_idString(), propName);
            } else {
                fileStore.attachFile(fileId, bizObjName, entity.orm_idString(), propName);
            }
        }
    }

    public String getBizObjName() {
        IOrmEntity entity = orm_owner();
        return StringHelper.lastPart(entity.orm_entityName(), '.');
    }
}