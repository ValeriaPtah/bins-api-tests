package bins.model;

import com.squareup.moshi.Json;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BinRequestBody {

    @Json
    private BinData data;

    public static BinData.BinDataBuilder getDataBuilder() {
        return BinData.builder();
    }

    @Data
    @Builder
    public static class BinData {
        @Json
        private String fields;

        @Json
        private String etag;
    }
}
