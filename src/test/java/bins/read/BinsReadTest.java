package bins.read;

import bins.BaseBinsTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import util.BinsHelper;
import util.Headers;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static util.PropertiesHelper.getMasterKey;
import static util.PropertiesHelper.getReadOnlyKey;
import static util.Schemas.BIN_SCHEMA;

public class BinsReadTest extends BaseBinsTest {
    private final static String BASE_PATH = "/b/";

    @BeforeClass
    public static void setup() {
        BinsHelper.testSetup("");
    }

    @Test
    public void canReadBin_Private_MasterKey() {
        String existingBinId = BinsHelper.getCreatedBin_ID();

        Response response = RestAssured.given()
                .basePath(BASE_PATH + existingBinId)
                .header(Headers.MASTER_KEY.getName(), getMasterKey())
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchema(BIN_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canReadBin_Private_AccessKey() {
        String existingBinId = BinsHelper.getCreatedBin_ID();

        Response response = RestAssured.given()
                .basePath(BASE_PATH + existingBinId)
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchema(BIN_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canReadBin_Public_NoAuth() {
        String existingBinId = BinsHelper.getCreatedBin_ID(false);

        Response response = RestAssured.given()
                .basePath(BASE_PATH + existingBinId)
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchema(BIN_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canReadSpecificVersion() {
        String existingBinId = BinsHelper.getBinWithTwoVersions_ID();
        String versionNumber = "2";

        Response response = RestAssured.given()
                .basePath(BASE_PATH + existingBinId + "/" + versionNumber)
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchema(BIN_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canRead_NoMetadata_Query() {
        String existingBinId = BinsHelper.getCreatedBin_ID();

        Response response = RestAssured.given()
                .basePath(BASE_PATH + existingBinId)
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .queryParam("meta", false)
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("metadata", is(nullValue()));
    }

    @Test
    public void canRead_NoMetadata_Header() {
        String existingBinId = BinsHelper.getCreatedBin_ID();

        Response response = RestAssured.given()
                .basePath(BASE_PATH + existingBinId)
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .header(Headers.METADATA.getName(), false)
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("metadata", is(nullValue()));
    }

    @Test
    public void canReadLatestVersion() {
    }

    @Test(enabled = false)
    public void canReadAllBins() {
        Response response = RestAssured.given()
                .basePath(BASE_PATH + "675c7448e41b4d34e464e58a")
                .header(Headers.MASTER_KEY.getName(), getMasterKey())
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_OK);
    }
}
