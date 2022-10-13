package io.tomrss.gluon.core.spec;

import java.io.IOException;
import java.util.List;

public interface EntitySpecReader {
    List<EntitySpec> read() throws IOException;
}
