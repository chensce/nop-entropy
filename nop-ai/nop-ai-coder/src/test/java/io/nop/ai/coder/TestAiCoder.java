package io.nop.ai.coder;

import io.nop.ai.coder.orm.AiOrmConfig;
import io.nop.ai.coder.orm.AiOrmModelNormalizer;
import io.nop.ai.coder.orm.OrmModelToJava;
import io.nop.ai.core.api.messages.AiChatExchange;
import io.nop.ai.core.model.PromptModel;
import io.nop.api.core.annotations.autotest.EnableSnapshot;
import io.nop.autotest.junit.JunitAutoTestCase;
import io.nop.core.lang.eval.IEvalScope;
import io.nop.core.lang.xml.XNode;
import io.nop.core.reflect.bean.BeanTool;
import io.nop.core.resource.component.ResourceComponentManager;
import io.nop.markdown.simple.MarkdownDocument;
import io.nop.markdown.simple.MarkdownSection;
import io.nop.markdown.utils.MarkdownTool;
import io.nop.orm.model.IEntityModel;
import io.nop.orm.model.OrmModel;
import io.nop.xlang.xdsl.DslModelParser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@NopTestConfig(disableSnapshot = true)
@Disabled
public class TestAiCoder extends JunitAutoTestCase {

    PromptModel loadPrompt(String promptPath) {
        PromptModel promptModel = (PromptModel) ResourceComponentManager.instance().loadComponentModel(promptPath);
        return promptModel;
    }

    @EnableSnapshot
    @Test
    public void testExpandRequirements() {
        PromptModel promptModel = loadPrompt("/nop/ai/prompts/coder/refactor-requirements.prompt.yaml");
        Map<String, Object> vars = new HashMap<>();
        vars.put("inputRequirements", inputText("input-requirements.md"));
        vars.put("needExpand", true);

        IEvalScope scope = promptModel.prepareInputs(vars);
        String prompt = promptModel.generatePrompt(scope);
        outputText("prompt-expand-requirements.md", prompt);

        AiChatExchange response = new AiChatExchange();
        String content = inputText("response-expand-requirements.md");
        response.setContent(content);
        promptModel.processChatResponse(response, scope);

        MarkdownDocument md = (MarkdownDocument) response.getResultValue();
        System.out.println(md.toText(true));
        assertTrue(response.isValid());
    }

    @EnableSnapshot
    @Test
    public void testRefineRequirements() {
        PromptModel promptModel = loadPrompt("/nop/ai/prompts/coder/refine-requirements.prompt.yaml");
        Map<String, Object> vars = new HashMap<>();
        vars.put("requirements", inputText("requirements.md"));

        IEvalScope scope = promptModel.prepareInputs(vars);

        String prompt = promptModel.generatePrompt(scope);
        outputText("prompt-refine-requirements.md", prompt);

        AiChatExchange response = new AiChatExchange();
        String content = inputText("response-refine-requirements.md");
        response.setContent(content);
        promptModel.processChatResponse(response, scope);

        assertTrue(response.isValid());
        System.out.println(response.getContent());
    }

    @EnableSnapshot
    @Test
    public void testOrmDesign() {
        PromptModel promptModel = loadPrompt("/nop/ai/prompts/coder/orm-design.prompt.yaml");
        Map<String, Object> vars = new HashMap<>();
        vars.put("requirements", inputText("response-refine-requirements.md"));

        IEvalScope scope = promptModel.prepareInputs(vars);
        String prompt = promptModel.generatePrompt(scope);
        outputText("prompt-orm-design.md", prompt);

        AiChatExchange response = new AiChatExchange();
        String content = inputText("response-orm-design.md");
        response.setContent(content);
        promptModel.processChatResponse(response, scope);

        XNode node = (XNode) response.getOutput("RESULT");
        node.dump();
    }

    @EnableSnapshot
    @Test
    public void testRefineOrmDesign() {
        PromptModel promptModel = loadPrompt("/nop/ai/prompts/coder/refine-orm-design.prompt.yaml");
        Map<String, Object> vars = new HashMap<>();
        vars.put("ormModelText", inputText("output-orm-design.xml"));
        vars.put("requirements", inputText("response-refine-requirements.md"));

        IEvalScope scope = promptModel.prepareInputs(vars);
        String prompt = promptModel.generatePrompt(scope);
        outputText("prompt-refine-orm-design.md", prompt);

        AiChatExchange response = new AiChatExchange();
        String content = inputText("response-refine-orm-design.md");
        response.setContent(content);
        promptModel.processChatResponse(response, scope);

        assertTrue(response.isValid());
        assertNull(response.getOutput("RESULT"));
        assertEquals(true, response.getOutput("noChange"));
    }

    @EnableSnapshot
    @Test
    public void testMenuDesign() {
        PromptModel promptModel = loadPrompt("/nop/ai/prompts/coder/menu-design.prompt.yaml");
        Map<String, Object> vars = new HashMap<>();
        vars.put("ormModelText", inputText("output-orm-design.xml"));
        vars.put("requirements", inputText("response-refine-requirements.md"));

        IEvalScope scope = promptModel.prepareInputs(vars);
        String prompt = promptModel.generatePrompt(scope);
        //outputText("prompt-menu-design.md", prompt);

        AiChatExchange response = new AiChatExchange();
        String content = inputText("response-menu-design.md");
        response.setContent(content);
        promptModel.processChatResponse(response, scope);

        assertTrue(response.isValid());
        XNode node = (XNode) response.getOutput("RESULT");
        node.dump();
    }

    @EnableSnapshot
    @Test
    public void testApiDesign() {
        PromptModel promptModel = loadPrompt("/nop/ai/prompts/coder/api-design.prompt.yaml");
        Map<String, Object> vars = new HashMap<>();
        vars.put("requirements", inputText("response-refine-requirements.md"));

        IEvalScope scope = promptModel.prepareInputs(vars);
        String prompt = promptModel.generatePrompt(scope);
        outputText("prompt-api-design.md", prompt);
    }

    @EnableSnapshot
    @Test
    public void testExtractEntityRequirements() {
        PromptModel promptModel = loadPrompt("/nop/ai/prompts/coder/extract-entity-requirements.prompt.yaml");
        Map<String, Object> vars = new HashMap<>();
        vars.put("requirements", inputText("response-refine-requirements.md"));
        vars.put("entityName", "stock_operation(库存操作)");

        IEvalScope scope = promptModel.prepareInputs(vars);
        String prompt = promptModel.generatePrompt(scope);
        outputText("prompt-extract-entity-requirements.md", prompt);
    }

    @EnableSnapshot
    @Test
    public void testFormDesign() {
        PromptModel promptModel = loadPrompt("/nop/ai/prompts/coder/form-design.prompt.yaml");
        Map<String, Object> vars = new HashMap<>();
        vars.put("requirements", inputText("response-extract-entity-requirements.md"));

        XNode node = inputXml("output-orm-design.xml");
        AiOrmConfig config = new AiOrmConfig();
        config.setBasePackageName("app");
        AiOrmModelNormalizer normalizer = new AiOrmModelNormalizer();
        normalizer.fixNameForOrmNode(node);
        normalizer.normalizeOrm(node, config);

        OrmModel ormModel = (OrmModel) new DslModelParser().parseFromNode(node);
        IEntityModel entityModel = ormModel.requireEntityModel("StockOperation");
        vars.put("entityModel", entityModel);

        String javaCode = new OrmModelToJava().appendOrmModel(ormModel).toString();
        outputText("entity_model.java", javaCode);

        XNode menuNode = inputXml("output-menu-design.xml");
        menuNode.setAttr("x:schema", "/nop/ai/schema/coder/auth.xdef");
        Object site = new DslModelParser().parseFromNode(menuNode);
        List<Object> roleList = (List<Object>) BeanTool.getProperty(site, "roles");
        vars.put("roleList", roleList);

        IEvalScope scope = promptModel.prepareInputs(vars);
        String prompt = promptModel.generatePrompt(scope);
        outputText("prompt-form-design.md", prompt);
    }

    @EnableSnapshot
    @Test
    public void testGridDesign() {
        PromptModel promptModel = loadPrompt("/nop/ai/prompts/coder/grid-design.prompt.yaml");
        Map<String, Object> vars = new HashMap<>();
        vars.put("requirements", inputText("response-extract-entity-requirements.md"));

        XNode node = inputXml("output-orm-design.xml");
        AiOrmConfig config = new AiOrmConfig();
        config.setBasePackageName("app");
        AiOrmModelNormalizer normalizer = new AiOrmModelNormalizer();
        normalizer.fixNameForOrmNode(node);
        normalizer.normalizeOrm(node, config);

        OrmModel ormModel = (OrmModel) new DslModelParser().parseFromNode(node);
        IEntityModel entityModel = ormModel.requireEntityModel("StockOperation");
        vars.put("entityModel", entityModel);

        XNode menuNode = inputXml("output-menu-design.xml");
        menuNode.setAttr("x:schema", "/nop/ai/schema/coder/auth.xdef");
        Object site = new DslModelParser().parseFromNode(menuNode);
        List<Object> roleList = (List<Object>) BeanTool.getProperty(site, "roles");
        vars.put("roleList", roleList);

        IEvalScope scope = promptModel.prepareInputs(vars);
        String prompt = promptModel.generatePrompt(scope);
        outputText("prompt-grid-design.md", prompt);
    }

    @EnableSnapshot
    @Test
    public void testExpandModuleRequirements() {
        PromptModel promptModel = loadPrompt("/nop/ai/prompts/coder/expand-module-requirements.prompt.yaml");
        MarkdownDocument doc = MarkdownTool.instance().parseFromText(null, inputText("response-requirements.md"));
        MarkdownSection section = doc.findSectionByTitle("2.2 核心功能模块");
        MarkdownSection child = section.getChild(0);

        MarkdownDocument filteredDoc = doc.selectSection(sec -> {
            boolean b = "1".equals(sec.getSectionNo()) || sec == child;
            return b;
        }, true);

        outputText("filtered-requirements.md", filteredDoc.toText());

        MarkdownSection child2 = section.getChild(1);

        filteredDoc = doc.selectSection(sec -> {
            return ("1".equals(sec.getSectionNo()) || sec == child2);
        }, true);

        outputText("filtered-requirements2.md", filteredDoc.toText());
    }
}
