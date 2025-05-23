package io.nop.ai.coder.orm;

import io.nop.commons.util.StringHelper;
import io.nop.core.lang.xml.XNode;
import io.nop.core.lang.xml.parse.XNodeParser;
import io.nop.orm.model.IEntityModel;
import io.nop.orm.model.IEntityRelationModel;
import io.nop.orm.model.OrmModel;
import io.nop.xlang.xdsl.DslModelParser;

import java.util.LinkedHashSet;
import java.util.Set;

public class AiOrmModel {
    private XNode node;
    private AiOrmConfig config;
    private XNode normalizedNode;
    private OrmModel ormModel;

    public static AiOrmModel buildFromAiResult(XNode node, AiOrmConfig config) {
        if (config == null)
            config = AiOrmConfig.fromOrmNode(node);

        AiOrmModel model = new AiOrmModel();
        model.config = config;
        node.removeNoneAttributes(); // 删除所有值为none的属性，比如stdDomain="none"
        model.node = new AiOrmModelNormalizer().fixNameForOrmNode(node);
        return model;
    }

    public static AiOrmModel buildFromOrmNode(XNode node) {
        AiOrmConfig config = AiOrmConfig.fromOrmNode(node);
        AiOrmModel model = new AiOrmModel();
        model.config = config;
        model.node = new AiOrmModelSimplifier().simplify(node.cloneInstance());
        model.normalizedNode = node;
        return model;
    }

    public static AiOrmModel buildFromOrmFile(String path) {
        XNode node = XNodeParser.instance().parseFromVirtualPath(path);
        return buildFromOrmNode(node);
    }

    public XNode getOrmNodeForAi() {
        return node;
    }

    public XNode getOrmNode() {
        if (normalizedNode == null) {
            this.normalize();
        }
        return normalizedNode;
    }

    public AiOrmModel normalize() {
        AiOrmConfig config = this.config;
        if (config == null) {
            config = new AiOrmConfig();
            config.setBasePackageName("app");
        }
        normalizedNode = new AiOrmModelNormalizer().normalizeOrm(node, config);
        return this;
    }

    public AiOrmModel parse() {
        XNode normalizedNode = getOrmNode();
        ormModel = (OrmModel) new DslModelParser().parseFromNode(normalizedNode);
        return this;
    }

    public OrmModel getOrmModel() {
        if (ormModel == null)
            parse();
        return ormModel;
    }

    public String getOrmModelXml() {
        return getOrmModelXml(null);
    }

    public String getOrmModelXml(Set<String> selectedEntityNames) {
        XNode node = getOrmNode();
        if (selectedEntityNames == null)
            return node.xml();

        node = node.cloneInstance();
        XNode entities = node.childByTag("entities");
        if (entities != null) {
            entities.getChildren().removeIf(child -> {
                String name = child.attrText("name");
                String shortName = StringHelper.simpleClassName(name);
                return !selectedEntityNames.contains(shortName);
            });
        }
        return node.xml();
    }

    public String getOrmModelJava() {
        return getOrmModelJava(null);
    }

    public String getOrmModelJava(Set<String> selectedEntityNames) {
        return new OrmModelToJava(selectedEntityNames).appendOrmModel(getOrmModel()).toString();
    }

    public Set<String> getAllRelatedEntityNames(Set<String> names) {
        Set<String> ret = new LinkedHashSet<>();
        for (String name : names) {
            addRelatedEntityNames(name, ret);
        }
        return ret;
    }

    private void addRelatedEntityNames(String name, Set<String> ret) {
        if (!ret.add(StringHelper.simpleClassName(name)))
            return;

        IEntityModel entityModel = getOrmModel().getEntityModel(name);
        if (entityModel != null) {
            for (IEntityRelationModel relModel : entityModel.getRelations()) {
                addRelatedEntityNames(relModel.getRefEntityName(), ret);
            }
        }
    }

    public String getEntityListInfo() {
        return getEntityListInfo(false);
    }

    public String getEntityListInfo(boolean includeComment) {
        return getEntityListInfo(includeComment, null);
    }

    public String getEntityListInfo(boolean includeComment, Set<String> selectedNames) {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (IEntityModel entityModel : getOrmModel().getEntityModels()) {
            if (selectedNames != null && !selectedNames.contains(entityModel.getShortName()))
                continue;
            index++;
            sb.append(index).append('.').append(' ');
            sb.append(entityModel.getShortName());
            sb.append('[').append(entityModel.getDisplayName()).append("]");

            if (includeComment) {
                if (!StringHelper.isEmpty(entityModel.getComment())) {
                    sb.append(": ").append(entityModel.getComment());
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public IEntityModel getEntityModel(String entityName) {
        return getOrmModel().requireEntityModel(entityName);
    }
}