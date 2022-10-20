package io.tomrss.gluon.core.util;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class ResourceUtils {
    public static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();

    public static final String COMMENT_MARKER = "#";

    public static List<String> readMetadataFile(String metadataResourceName) throws IOException {
        return readMetadataFile(CLASS_LOADER, metadataResourceName);
    }

    public static List<String> readMetadataFile(ClassLoader classLoader, String metadataResourceName) throws IOException {
        try (final InputStream is = classLoader.getResourceAsStream(metadataResourceName)) {
            if (is == null) {
                throw new FileNotFoundException("Missing metadata file " + metadataResourceName);
            }
            try (final InputStreamReader isr = new InputStreamReader(is);
                 final BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(line -> line.length() > 0)
                        .filter(line -> !line.startsWith(COMMENT_MARKER))
                        .toList();
            }
        }
    }

    public static void extractResource(String resourceName, Path target) throws IOException {
        extractResource(CLASS_LOADER, resourceName, target);
    }

    public static void extractResource(ClassLoader classLoader, String resourceName, Path target) throws IOException {
        final URL resource = classLoader.getResource(resourceName);
        if (resource == null) {
            throw new FileNotFoundException("Resource " + resourceName + " not found");
        }
        try (final InputStream inputStream = resource.openStream();
             final OutputStream outputStream = new FileOutputStream(target.toFile())) {
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }
}
