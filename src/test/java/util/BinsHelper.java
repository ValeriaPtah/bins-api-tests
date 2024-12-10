package util;

import bins.model.Bin;
import com.squareup.moshi.Moshi;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;

import java.io.File;
import java.util.Objects;

import static bins.model.Bin.getRecordBuilder;
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

    public static Bin testBinsEntry() {
        return Bin.builder()
                .record(getRecordBuilder().data("{'sample': 'Hello World'}").build())
                .build();
    }

    public static Bin testBinsEntry_InvalidJson() {
        return Bin.builder()
                .record(getRecordBuilder().data("smth").build())
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

    public static Bin getBinById(String binId) {
        return RestAssured.given()
                .basePath("/b/" + binId)
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .when()
                .get()
                .body()
                .as(Bin.class);
    }

}
