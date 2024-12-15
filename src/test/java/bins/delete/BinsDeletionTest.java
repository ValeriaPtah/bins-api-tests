package bins.delete;

import bins.BaseBinsTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import util.BinsHelper;
import util.Headers;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static util.PropertiesHelper.getBasePath;
import static util.PropertiesHelper.getDeleteCreateKey;
import static util.PropertiesHelper.getMasterKey;
import static util.PropertiesHelper.getPath_BinID;
import static util.PropertiesHelper.getReadOnlyKey;
import static util.Schemas.DELETION_SCHEMA;
import static util.Schemas.ERROR_SCHEMA;

/**
 * Documentation: <a href="https://jsonbin.io/api-reference/bins/delete">Delete Bins API</a>
 */
public class BinsDeletionTest extends BaseBinsTest {
    private static final String NOT_EXISTS_BIN_ID = "67598cfcacd3cb34a8b7d725";

    @BeforeClass
    public static void setup() {
        testSetup();
    }

    @Test
    public void canDeleteBin_MasterKey() {
        String existingBinId = BinsHelper.getCreatedBin_ID();

        Response response = RestAssured.given()
                .basePath(getBasePath() + existingBinId)
                .header(Headers.MASTER_KEY.getName(), getMasterKey())
                .when()
                .delete();
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchema(DELETION_SCHEMA.getSchemaFile()));

        String deletedBinId = response.body().jsonPath().get(getPath_BinID());
        int statusCodeForGet = BinsHelper.getStatusCode_GetBinById(deletedBinId);

        Assert.assertEquals(statusCodeForGet, HttpStatus.SC_NOT_FOUND);

        if (statusCodeForGet == HttpStatus.SC_NOT_FOUND) {
            BaseBinsTest.removeFromCreatedBinsIds(deletedBinId);
        }
    }

    @Test
    public void canDeleteBin_AccessKey() {
        String existingBinId = BinsHelper.getCreatedBin_ID();

        Response response = RestAssured.given()
                .basePath(getBasePath() + existingBinId)
                .header(Headers.ACCESS_KEY.getName(), getDeleteCreateKey())
                .when()
                .delete();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchema(DELETION_SCHEMA.getSchemaFile()));

        String deletedBinId = response.body().jsonPath().get(getPath_BinID());
        int statusCodeForGet = BinsHelper.getStatusCode_GetBinById(deletedBinId);

        Assert.assertEquals(statusCodeForGet, HttpStatus.SC_NOT_FOUND);

        if (statusCodeForGet == HttpStatus.SC_NOT_FOUND) {
            BaseBinsTest.removeFromCreatedBinsIds(deletedBinId);
        }
    }

    @Test
    public void canNotDeleteBin_NotExist() {
        RestAssured.given()
                .basePath(getBasePath() + NOT_EXISTS_BIN_ID)
                .header(Headers.ACCESS_KEY.getName(), getDeleteCreateKey())
                .when()
                .delete()
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canNotDeleteBin_InvalidId() {
        String invalidBinId = "some_id";

        RestAssured.given()
                .basePath(getBasePath() + invalidBinId)
                .header(Headers.ACCESS_KEY.getName(), getDeleteCreateKey())
                .when()
                .delete()
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canNotDeleteBin_NoAuth() {
        RestAssured.given()
                .basePath(getBasePath() + NOT_EXISTS_BIN_ID)
                .when()
                .delete()
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canNotDeleteBin_WrongAuth() {
        RestAssured.given()
                .basePath(getBasePath() + NOT_EXISTS_BIN_ID)
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .when()
                .delete()
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }
}
