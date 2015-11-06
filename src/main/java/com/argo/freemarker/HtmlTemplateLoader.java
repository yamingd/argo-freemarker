package com.argo.freemarker;

import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import freemarker.cache.TemplateLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-12-18
 * Time: 下午11:09
 */
public class HtmlTemplateLoader implements TemplateLoader {

    public static final String ESCAPE_PREFIX = "<#ftl strip_whitespace=true><#escape x as x?html>";
    public static final String ESCAPE_SUFFIX = "</#escape>";

    private final TemplateLoader delegate;

    public HtmlTemplateLoader(TemplateLoader delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {
        return delegate.findTemplateSource(name);
    }

    @Override
    public long getLastModified(Object templateSource) {
        return delegate.getLastModified(templateSource);
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        Reader reader = delegate.getReader(templateSource, encoding);
        try {
            String templateText = CharStreams.toString(reader);
            return new StringReader(ESCAPE_PREFIX + templateText + ESCAPE_SUFFIX);
        } finally {
            Closeables.close(reader, true);
        }
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
        delegate.closeTemplateSource(templateSource);
    }
}
