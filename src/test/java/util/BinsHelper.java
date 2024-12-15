package util;

import bins.BaseBinsTest;
import bins.model.BinRequestBody;
import com.squareup.moshi.Moshi;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.Map;

import static util.PropertiesHelper.getBasePath;
import static util.PropertiesHelper.getDeleteCreateKey;
import static util.PropertiesHelper.getMasterKey;
import static util.PropertiesHelper.getPath_BinID;
import static util.PropertiesHelper.getPath_BinParentId;
import static util.PropertiesHelper.getPath_Etag;
import static util.PropertiesHelper.getReadOnlyKey;

public class BinsHelper {
    private static final Moshi MOSHI = new Moshi.Builder().build();
    private static final Map<Integer, String> VERSION_ETAG = new HashMap<>();

    public static BinRequestBody testBinRequestBody() {
        return BinRequestBody.builder()
                .data(BinRequestBody.getDataBuilder()
                        .fields("{'sample': 'Hello World'}")
                        .etag(RandomStringUtils.randomAlphanumeric(15))
                        .build())
                .build();
    }

    public static <T> String toJson(T object, Class<T> type) {
        return MOSHI
                .adapter(type)
                .lenient()
                .toJson(object);
    }

    public static int getStatusCode_GetBinById(String binId) {
        return RestAssured.given()
                .basePath(getBasePath() + binId)
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .when()
                .get()
                .statusCode();
    }

    public static String getCreatedBin_ID() {
        return getCreatedBin_ID(true);
    }

    public static String getCreatedBin_ID(boolean isPrivate) {
        return getCreatedBin(isPrivate).get(getPath_BinID());
    }

    public static String getBinWithTwoVersions_ID() {
        return getBinWithTwoVersions_Public().get(getPath_BinParentId());
    }

    public static String getEtagByVersion(int version) {
        return VERSION_ETAG.get(version);
    }

    public static String getEtagLatestVersion() {
        return VERSION_ETAG.get(VERSION_ETAG.size());
    }

    public static JsonPath getCreatedBin(boolean isPrivate) {
        Response response = RestAssured.given()
                .basePath(getBasePath())
                .header(Headers.ACCESS_KEY.getName(), getDeleteCreateKey())
                .header(Headers.PRIVATE_BIN.getName(), isPrivate)
                .body(BinsHelper.toJson(testBinRequestBody(), BinRequestBody.class))
                .when()
                .post();

        BaseBinsTest.addToCreatedBinsIds(response.body().jsonPath().get(getPath_BinID()));

        return response.body().jsonPath();
    }

    private static JsonPath getBinWithTwoVersions_Public() {
        String existingBinId = getCreatedBin_ID(false);
        Response response = null;

        for (int i = 1; i <= 2; i++) {
            response = RestAssured.given()
                    .basePath(getBasePath() + existingBinId)
                    .header(Headers.MASTER_KEY.getName(), getMasterKey())
                    .header(Headers.VERSIONING.getName(), true)
                    .body(BinsHelper.toJson(testBinRequestBody(), BinRequestBody.class))
                    .when()
                    .put();
            VERSION_ETAG.put(i, response.body().jsonPath().get(getPath_Etag()));
        }

        return response.body().jsonPath();
    }

}
