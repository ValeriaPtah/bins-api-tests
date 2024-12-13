package util;

import bins.model.BinRequestBody;
import com.squareup.moshi.Moshi;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.io.File;
import java.util.Objects;

import static util.PropertiesHelper.getDeleteCreateKey;
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
                .data("{'sample': 'Hello World'}")
                .build();
    }

    public static BinRequestBody testBinRequestBody_InvalidJson() {
        return BinRequestBody.builder()
                .data("smth")
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

    //TODO refactor for purpose
    public static String getExistingBinById() {
        Response response = RestAssured.given()
                .basePath("/b")
                .header(Headers.ACCESS_KEY.getName(), getDeleteCreateKey())
                .body(BinsHelper.toJson(testBinRequestBody(), BinRequestBody.class))
                .when()
                .post();

        return response.body().jsonPath().get("metadata.id");
    }

}
