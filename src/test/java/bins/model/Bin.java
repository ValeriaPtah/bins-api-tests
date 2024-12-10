package bins.model;

import com.squareup.moshi.Json;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Bin {

    @Json
    private Record record;

    @Json
    private Metadata metadata;

    @Data
    @Builder
    static class Record {
        @Json
        private String data;
    }

    @Data
    @Builder
    static class Metadata {
        @Json private String id;
        @Json private String createdAt;

        @Json(name = "private")
        private boolean access;
    }

    public boolean isPrivate() {
        return this.getMetadata().isAccess();
    }

    public static Record.RecordBuilder getRecordBuilder() {
        return Record.builder();
    }
}
