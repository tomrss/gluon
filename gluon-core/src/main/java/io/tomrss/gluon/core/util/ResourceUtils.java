package io.tomrss.gluon.core.util;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Utilities for classpath resource management.
 *
 * @author Tommaso Rossi
 */
public class ResourceUtils {
    public static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();

    /**
     * Read a metadata file in resource specified.
     * <p>
     * Metadata file is a file containing a newline separated list of
     * resource names.
     *
     * @param metadataResourceName name of the metadata file resource
     * @return resource names from metadata file
     * @throws IOException error reading metadata file resource
     */
    public static List<String> readMetadataFile(String metadataResourceName) throws IOException {
        return readMetadataFile(CLASS_LOADER, metadataResourceName);
    }

    /**
     * Read a metadata file in resource specified.
     * <p>
     * Metadata file is a file containing a newline separated list of
     * resource names.
     *
     * @param classLoader          class loader
     * @param metadataResourceName name of the metadata file resource
     * @return resource names from metadata file
     * @throws IOException error reading metadata file resource
     */
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
                        .filter(line -> !line.isEmpty())
                        .filter(line -> !line.startsWith(Constants.COMMENT_MARKER))
                        .toList();
            }
        }
    }

    /**
     * Extract resource from jar into target path.
     *
     * @param resourceName name of the resource
     * @param target       path where to extract resource
     * @throws IOException unable to extract
     */
    public static void extractResource(String resourceName, Path target) throws IOException {
        extractResource(CLASS_LOADER, resourceName, target);
    }

    /**
     * Extract resource from jar into target path.
     *
     * @param classLoader  class loader
     * @param resourceName name of the resource
     * @param target       path where to extract resource
     * @throws IOException unable to extract
     */
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
