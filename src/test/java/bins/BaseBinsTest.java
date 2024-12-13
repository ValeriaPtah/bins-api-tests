package bins;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import util.Headers;

import java.util.ArrayList;
import java.util.List;

import static util.PropertiesHelper.getDeleteCreateKey;
import static util.PropertiesHelper.getMasterKey;

public class BaseBinsTest {

    protected final static String BASE_PATH = "/b/";
    private final static List<String> CREATED_BINS_IDS = new ArrayList<>();

    @BeforeClass
    public static void before() {
        RestAssured.baseURI = "https://api.jsonbin.io/v3";
        RestAssured
                .filters(new RequestLoggingFilter(),
                        new ResponseLoggingFilter());
    }

    @AfterClass
    public static void after() {
        RestAssured.basePath = "";
        RestAssured.requestSpecification = null;
        RestAssured.responseSpecification = null;
        cleanUpCreatedBins();
    }

    public static void testSetup(String path) {
        RestAssured.basePath = path;
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
        RestAssured.responseSpecification = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .build();
    }

    public static void addToCreatedBinsIds(String binId) {
        CREATED_BINS_IDS.add(binId);
    }

    public static void removeFromCreatedBinsIds(String binId) {
        CREATED_BINS_IDS.remove(binId);
    }

    private static void cleanUpCreatedBins() {
        for (String binId : CREATED_BINS_IDS) {
            RestAssured.given()
                    .basePath(BASE_PATH + binId)
                    .header(Headers.ACCESS_KEY.getName(), getDeleteCreateKey())
                    .when()
                    .delete()
                    .then()
                    .statusCode(HttpStatus.SC_OK);
        }
    }

    public void enableVersioning(String binId) {
        Response response = RestAssured.given()
                .basePath(BASE_PATH + binId)
                .header(Headers.VERSIONING.getName(), true)
                .header(Headers.MASTER_KEY.getName(), getMasterKey())
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_OK);
    }
}
