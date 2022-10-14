package io.tomrss.gluon.core.template.impl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import io.tomrss.gluon.core.template.GluonTemplateException;
import io.tomrss.gluon.core.template.TemplateRenderer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

public class FreemarkerTemplateRenderer implements TemplateRenderer {

    private final Configuration freemarker;

    public FreemarkerTemplateRenderer() {
        this.freemarker = new Configuration(Configuration.VERSION_2_3_31);
        this.freemarker.setDefaultEncoding("UTF-8");
        this.freemarker.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        this.freemarker.setLogTemplateExceptions(false);
        this.freemarker.setWrapUncheckedExceptions(true);
        this.freemarker.setFallbackOnNullLoopVariable(false);
    }

    @Override
    public void setTemplateBaseDirectory(Path templateBaseDirectory) {
        try {
            this.freemarker.setDirectoryForTemplateLoading(templateBaseDirectory.toFile());
        } catch (IOException e) {
            throw new GluonTemplateException("Unable to instance freemarker template renderer", e);
        }
    }

    @Override
    public void render(String templateName, Object model, Path outputFile) throws IOException {
        final Template template = freemarker.getTemplate(templateName);
        try (final Writer writer = new FileWriter(outputFile.toFile())) {
            template.process(model, writer);
            // TODO logger
            System.out.println("Template " + templateName + " rendered to file " + outputFile);
        } catch (TemplateException e) {
            throw new GluonTemplateException(e);
        }
    }
}
