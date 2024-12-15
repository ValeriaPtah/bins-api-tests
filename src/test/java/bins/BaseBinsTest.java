package bins;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import util.Headers;

import java.util.ArrayList;
import java.util.List;

import static util.PropertiesHelper.getBasePath;
import static util.PropertiesHelper.getBaseURL;
import static util.PropertiesHelper.getDeleteCreateKey;
import static util.PropertiesHelper.getMasterKey;

public class BaseBinsTest {
    private static final List<String> CREATED_BINS_IDS = new ArrayList<>();

    @BeforeClass
    public static void before() {
        RestAssured.baseURI = getBaseURL();
        RestAssured
                .filters(new RequestLoggingFilter(),
                        new ResponseLoggingFilter());
    }

    @AfterClass
    public static void after() {
        RestAssured.basePath = "";
        RestAssured.requestSpecification = null;
        RestAssured.responseSpecification = null;
    }

    @AfterSuite
    public void cleanUp() {
        cleanUpCreatedBins();
    }

    public static void testSetup() {
        testSetup(StringUtils.EMPTY);
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

    public void enableVersioning(String binId) {
        Response response = RestAssured.given()
                .basePath(getBasePath() + binId)
                .header(Headers.VERSIONING.getName(), true)
                .header(Headers.MASTER_KEY.getName(), getMasterKey())
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    public static void addToCreatedBinsIds(String binId) {
        CREATED_BINS_IDS.add(binId);
    }

    public static void removeFromCreatedBinsIds(String binId) {
        CREATED_BINS_IDS.remove(binId);
    }

    private static void cleanUpCreatedBins() {
        for (String binId : CREATED_BINS_IDS) {
            Response response = RestAssured.given()
                    .basePath(getBasePath() + binId)
                    .header(Headers.ACCESS_KEY.getName(), getDeleteCreateKey())
                    .when()
                    .delete();

            response
                    .then()
                    .statusCode(HttpStatus.SC_OK);
        }
    }
}
