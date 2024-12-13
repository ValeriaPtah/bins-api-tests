package util;

import bins.BaseBinsTest;
import bins.model.BinRequestBody;
import com.squareup.moshi.Moshi;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.util.Objects;

import static util.PropertiesHelper.getDeleteCreateKey;
import static util.PropertiesHelper.getMasterKey;
import static util.PropertiesHelper.getReadOnlyKey;

public class BinsHelper {

    private static final Moshi MOSHI = new Moshi.Builder().build();

    public static void testSetup(String path) {
        RestAssured.basePath = path;
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
        RestAssured.responseSpecification = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .build();
    }

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

    public static File getJsonSchema(String schemaName) {
        ClassLoader classLoader = BinsHelper.class.getClassLoader();
        return new File((Objects.requireNonNull(classLoader.getResource("schemas/" + schemaName)).getFile()));
    }

    public static int getStatusCode_GetBinById(String binId) {
        return RestAssured.given()
                .basePath("/b/" + binId)
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .when()
                .get()
                .statusCode();
    }

    public static String getCreatedBin_ID() {
        return getCreatedBin_ID(true);
    }

    public static String getCreatedBin_ID(boolean isPrivate) {
        return getCreatedBin(isPrivate).get("metadata.id");
    }

    public static String getBinWithTwoVersions_ID() {
        return getBinWithTwoVersions_Public().get("metadata.parentId");
    }

    private static JsonPath getCreatedBin(boolean isPrivate) {
        Response response = RestAssured.given()
                .basePath("/b")
                .header(Headers.ACCESS_KEY.getName(), getDeleteCreateKey())
                .header(Headers.PRIVATE_BIN.getName(), isPrivate)
                .body(BinsHelper.toJson(testBinRequestBody(), BinRequestBody.class))
                .when()
                .post();

        BaseBinsTest.addToCreatedBinsIds(response.body().jsonPath().get("metadata.id"));

        return response.body().jsonPath();
    }

    private static JsonPath getBinWithTwoVersions_Public() {
        String existingBinId = getCreatedBin_ID(false);
        Response response = null;

        for (int i = 0; i < 2; i++) {
            response = RestAssured.given()
                    .basePath("/b/" + existingBinId)
                    .header(Headers.MASTER_KEY.getName(), getMasterKey())
                    .header(Headers.VERSIONING.getName(), true)
                    .body(BinsHelper.toJson(testBinRequestBody(), BinRequestBody.class))
                    .when()
                    .put();
        }

        return response.body().jsonPath();
    }

}
