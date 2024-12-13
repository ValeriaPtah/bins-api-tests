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
import static util.PropertiesHelper.getReadOnlyKey;
import static util.PropertiesHelper.getUpdateOnlyKey;
import static util.Schemas.ERROR_SCHEMA;

public class BinsNegativeReadTest extends BaseBinsTest {

    @BeforeClass
    public static void setup() {
        testSetup("");
    }

    @Test
    public void canNotReadBin_Private_NoAuth() {
        String existingBinId = BinsHelper.getCreatedBin_ID();

        Response response = RestAssured.given()
                .basePath(BASE_PATH + existingBinId)
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canNotReadBin_Private_WrongAuth() {
        String existingBinId = BinsHelper.getCreatedBin_ID();

        Response response = RestAssured.given()
                .basePath(BASE_PATH + existingBinId)
                .header(Headers.ACCESS_KEY.getName(), getUpdateOnlyKey())
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canNotRead_InvalidVersion() {
        String existingBinId = BinsHelper.getCreatedBin_ID();
        String version = "some_version";

        Response response = RestAssured.given()
                .basePath(BASE_PATH + existingBinId + "/" + version)
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canNotRead_InvalidBinID() {
        String invalidBinId = "some_id";
        String version = "some_version";

        Response response = RestAssured.given()
                .basePath(BASE_PATH + invalidBinId + "/" + version)
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canNotRead_DoesNotExist_Version() {
        String existingBinId = BinsHelper.getBinWithTwoVersions_ID();
        String version = "3";

        Response response = RestAssured.given()
                .basePath(BASE_PATH + existingBinId + "/" + version)
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canNotRead_DoesNotExist_Bin() {
        String doesNotExistBinId = "67598cfcacd3cb34a8b7d725";
        String version = "1";

        Response response = RestAssured.given()
                .basePath(BASE_PATH + doesNotExistBinId + "/" + version)
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }
}
