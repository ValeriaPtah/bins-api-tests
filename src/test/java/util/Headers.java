package util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Headers {
    MASTER_KEY("X-Master-Key"),
    ACCESS_KEY("X-Access-Key"),
    PRIVATE_BIN("X-Bin-Private"),
    BIN_NAME("X-Bin-Name"),
    VERSIONING("X-Bin-Versioning");

    private final String name;
}
