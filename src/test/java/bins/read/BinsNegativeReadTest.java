package bins.read;

import bins.BaseBinsTest;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import util.BinsHelper;
import util.Headers;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static util.PropertiesHelper.getBasePath;
import static util.PropertiesHelper.getReadOnlyKey;
import static util.PropertiesHelper.getUpdateOnlyKey;
import static util.Schemas.ERROR_SCHEMA;

/**
 * Documentation: <a href="https://jsonbin.io/api-reference/bins/read">Read Bins API</a>
 */
public class BinsNegativeReadTest extends BaseBinsTest {
    private static final String NOT_EXISTS_BIN_ID = "67598cfcacd3cb34a8b7d725";

    @BeforeClass
    public static void setup() {
        testSetup();
    }

    @Test
    public void canNotReadBin_Private_NoAuth() {
        String existingBinId = BinsHelper.getCreatedBin_ID();

        RestAssured.given()
                .basePath(getBasePath() + existingBinId)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canNotReadBin_Private_WrongAuth() {
        String existingBinId = BinsHelper.getCreatedBin_ID();

        RestAssured.given()
                .basePath(getBasePath() + existingBinId)
                .header(Headers.ACCESS_KEY.getName(), getUpdateOnlyKey())
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canNotRead_InvalidVersion() {
        String existingBinId = BinsHelper.getCreatedBin_ID();
        String version = "some_version";

        RestAssured.given()
                .basePath(getBasePath() + existingBinId + "/" + version)
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canNotRead_InvalidBinID() {
        String invalidBinId = "some_id";
        String version = "some_version";

        RestAssured.given()
                .basePath(getBasePath() + invalidBinId + "/" + version)
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canNotRead_DoesNotExist_Version() {
        String existingBinId = BinsHelper.getBinWithTwoVersions_ID();
        String version = "3";

        RestAssured.given()
                .basePath(getBasePath() + existingBinId + "/" + version)
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canNotRead_DoesNotExist_Bin() {
        String version = "1";

        RestAssured.given()
                .basePath(getBasePath() + NOT_EXISTS_BIN_ID + "/" + version)
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }
}
