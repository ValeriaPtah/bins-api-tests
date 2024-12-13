package bins.model;

import com.squareup.moshi.Json;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BinRequestBody {

    @Json
    private String data;
}

