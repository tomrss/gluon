package io.tomrss.gluon.core.template.impl;

import io.tomrss.gluon.core.template.StringTemplateRenderer;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringTemplateRendererImplTest {

    @Test
    void renderStringTemplate_noMatch() {
        // prepare
        final String str = "foobar.txt";
        final StringTemplateRenderer tr = new StringTemplateRendererImpl();
        final Map<String, Object> model = Map.of(
                "foo", "FOO"
        );

        // execute
        final String result = tr.renderStringTemplate(str, model);

        // assert
        assertEquals("foobar.txt", result);
    }
    @Test
    void renderStringTemplate_map() {
        // prepare
        final String str = "{{foo}}bar.txt";
        final StringTemplateRenderer tr = new StringTemplateRendererImpl();
        final Map<String, Object> model = Map.of(
                "foo", "FOO"
        );

        // execute
        final String result = tr.renderStringTemplate(str, model);

        // assert
        assertEquals("FOObar.txt", result);
    }

    @Test
    void renderStringTemplate_map2() {
        // prepare
        final String str = "{{foo}}baz{{bar}}.txt";
        final StringTemplateRenderer tr = new StringTemplateRendererImpl();
        final Map<String, Object> model = Map.of(
                "foo", "FOO",
                "bar", "BAR"
        );

        // execute
        final String result = tr.renderStringTemplate(str, model);

        // assert
        assertEquals("FOObazBAR.txt", result);
    }

    @Test
    void renderStringTemplate_publicFields() {
        // prepare
        final String str = "{{foo}}baz{{bar}}.txt";
        final StringTemplateRenderer tr = new StringTemplateRendererImpl();
        final PublicTestBean model = new PublicTestBean("FOO", "BAR");

        // execute
        final String result = tr.renderStringTemplate(str, model);

        // assert
        assertEquals("FOObazBAR.txt", result);
    }

    @Test
    void renderStringTemplate_getter() {
        // prepare
        final String str = "{{foo}}baz{{bar}}.txt";
        final StringTemplateRenderer tr = new StringTemplateRendererImpl();
        final GetterTestBean model = new GetterTestBean("FOO", "BAR");

        // execute
        final String result = tr.renderStringTemplate(str, model);

        // assert
        assertEquals("FOObazBAR.txt", result);
    }

    @Test
    void renderStringTemplate_record() {
        // prepare
        final String str = "{{foo}}baz{{bar}}.txt";
        final StringTemplateRenderer tr = new StringTemplateRendererImpl();
        final RecordTestBean model = new RecordTestBean("FOO", "BAR");

        // execute
        final String result = tr.renderStringTemplate(str, model);

        // assert
        assertEquals("FOObazBAR.txt", result);
    }

    @Test
    void renderStringTemplate_nested_map() {
        // prepare
        final String str = "{{nest.foo}}baz{{bar}}.txt";
        final StringTemplateRenderer tr = new StringTemplateRendererImpl();
        final Map<String, Object> model = Map.of(
                "nest", Map.of("foo", "FOO"),
                "bar", "BAR"
        );

        // execute
        final String result = tr.renderStringTemplate(str, model);

        // assert
        assertEquals("FOObazBAR.txt", result);
    }

    @Test
    void renderStringTemplate_nested_getter() {
        // prepare
        final String str = "{{nest.foo}}baz{{bar}}.txt";
        final StringTemplateRenderer tr = new StringTemplateRendererImpl();
        final GetterTestBean model = new GetterTestBean(null, "BAR", new GetterTestBean("FOO", null));

        // execute
        final String result = tr.renderStringTemplate(str, model);

        // assert
        assertEquals("FOObazBAR.txt", result);
    }

    @Test
    void renderStringTemplate_nested_public() {
        // prepare
        final String str = "{{nest.foo}}baz{{bar}}.txt";
        final StringTemplateRenderer tr = new StringTemplateRendererImpl();
        final PublicTestBean model = new PublicTestBean(null, "BAR", new PublicTestBean("FOO", null));

        // execute
        final String result = tr.renderStringTemplate(str, model);

        // assert
        assertEquals("FOObazBAR.txt", result);
    }

    @Test
    void renderStringTemplate_nested_record() {
        // prepare
        final String str = "{{nest.foo}}baz{{bar}}.txt";
        final StringTemplateRenderer tr = new StringTemplateRendererImpl();
        final RecordTestBean model = new RecordTestBean(null, "BAR", new RecordTestBean("FOO", null));

        // execute
        final String result = tr.renderStringTemplate(str, model);

        // assert
        assertEquals("FOObazBAR.txt", result);
    }

    @Test
    void renderStringTemplate_nested_multipleMixed() {
        // prepare
        final String str = "{{nest.foo.bar.baz}}baz{{bar}}.txt";
        final StringTemplateRenderer tr = new StringTemplateRendererImpl();
        final MixedTestBean model = new MixedTestBean(Map.of("foo", Map.of("bar", Map.of("baz", "FOO"))), "BAR");

        // execute
        final String result = tr.renderStringTemplate(str, model);

        // assert
        assertEquals("FOObazBAR.txt", result);
    }

    static class PublicTestBean {
        public final String foo;
        public final String bar;
        public final PublicTestBean nest;

        PublicTestBean(String foo, String bar) {
            this(foo, bar, null);
        }

        PublicTestBean(String foo, String bar, PublicTestBean nest) {
            this.foo = foo;
            this.bar = bar;
            this.nest = nest;
        }
    }

    static class GetterTestBean {
        private final String foo;
        private final String bar;
        private final GetterTestBean nest;

        GetterTestBean(String foo, String bar) {
            this(foo, bar, null);
        }

        public GetterTestBean(String foo, String bar, GetterTestBean nest) {
            this.foo = foo;
            this.bar = bar;
            this.nest = nest;
        }

        public String getFoo() {
            return foo;
        }

        public String getBar() {
            return bar;
        }

        public GetterTestBean getNest() {
            return nest;
        }
    }

    record RecordTestBean(String foo, String bar, RecordTestBean nest) {
        RecordTestBean(String foo, String bar) {
            this(foo, bar, null);
        }
    }

    record MixedTestBean(Map<String, Object> nest, String bar) {
    }
}