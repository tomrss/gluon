package io.tomrss.gluon.core.template.impl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import io.tomrss.gluon.core.template.GluonTemplateException;
import io.tomrss.gluon.core.template.TemplateRenderer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

public class FreemarkerTemplateRenderer implements TemplateRenderer {

    private final Configuration freemarker;

    public FreemarkerTemplateRenderer(String templateFolder) {
        this(new File(templateFolder));
    }

    public FreemarkerTemplateRenderer(Path templateFolder) {
        this(templateFolder.toFile());
    }

    public FreemarkerTemplateRenderer(File templateFolder) {
        this.freemarker = new Configuration(Configuration.VERSION_2_3_31);
        try {
            this.freemarker.setDirectoryForTemplateLoading(templateFolder);
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO
        }
        this.freemarker.setDefaultEncoding("UTF-8");
        this.freemarker.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        this.freemarker.setLogTemplateExceptions(false);
        this.freemarker.setWrapUncheckedExceptions(true);
        this.freemarker.setFallbackOnNullLoopVariable(false);
    }

    @Override
    public void templateToFile(String templateName, Object model, Path outputFile) throws IOException {
        final Template template = freemarker.getTemplate(templateName);
        try (final Writer writer = new FileWriter(outputFile.toFile())) {
            template.process(model, writer);
            System.out.println("Template " + templateName + " rendered to file " + outputFile);
        } catch (TemplateException e) {
            throw new GluonTemplateException(e);
        }
    }
}
