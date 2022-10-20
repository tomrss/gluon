package io.tomrss.gluon.core.spec;

import java.io.IOException;
import java.util.List;

public interface EntitySpecLoader {
    List<EntitySpec> load() throws IOException;
}
