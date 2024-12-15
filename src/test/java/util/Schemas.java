package util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum Schemas {
    BIN_SCHEMA("created-bin-response-schema.json"),
    DELETION_SCHEMA("deleted-bin-response-schema.json"),
    ERROR_SCHEMA("error-response-schema.json");

    private final String fileName;

    private static File getJsonSchema(String schemaName) {
        ClassLoader classLoader = Schemas.class.getClassLoader();
        return new File((Objects.requireNonNull(classLoader.getResource("schemas/" + schemaName)).getFile()));
    }

    public final File getSchemaFile() {
        return getJsonSchema(fileName);
    }
}
