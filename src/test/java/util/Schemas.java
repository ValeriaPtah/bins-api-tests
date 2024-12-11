package util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;

@Getter
@AllArgsConstructor
public enum Schemas {
    CREATION_SCHEMA("created-bin-response-schema.json"),
    DELETION_SCHEMA("deleted-bin-response-schema.json"),
    ERROR_SCHEMA("error-response-schema.json");

    private final String fileName;

    public final File getSchemaFile() {
        return BinsHelper.getJsonSchema(fileName);
    }
}
