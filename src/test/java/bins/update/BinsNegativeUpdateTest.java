package bins.update;

import bins.BaseBinsTest;
import bins.model.BinRequestBody;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import util.BinsHelper;
import util.Headers;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static util.PropertiesHelper.getBasePath;
import static util.PropertiesHelper.getDeleteCreateKey;
import static util.PropertiesHelper.getMasterKey;
import static util.PropertiesHelper.getUpdateOnlyKey;
import static util.Schemas.ERROR_SCHEMA;

/**
 * Documentation: <a href="https://jsonbin.io/api-reference/bins/update">Update Bins API</a>
 */
public class BinsNegativeUpdateTest extends BaseBinsTest {
    private static final BinRequestBody VALID_TO_UPDATE_BIN_REQUEST_BODY = BinsHelper.testBinRequestBody();

    @BeforeClass
    public static void setup() {
        testSetup();
    }

    /**
     * As per documentation, to disable versioning on Public bins one needs to pass Master Key so this test reveals
     * a bug or a fault in documentation
     * <a href="https://jsonbin.io/api-reference/bins/update#request-headers">X-Bin-Versioning</a>
     */
    @Test(enabled = false)
    public void canNotDisableVersioning_WithAccessKey_ForPublic() {
        String existingBinId = BinsHelper.getCreatedBin_ID(false);

        Response response = RestAssured.given()
                .basePath(getBasePath() + existingBinId)
                .header(Headers.VERSIONING.getName(), false)
                .header(Headers.ACCESS_KEY.getName(), getUpdateOnlyKey())
                .body(BinsHelper.toJson(VALID_TO_UPDATE_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .put();
        response
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));

        enableVersioning(existingBinId);
    }

    @Test
    public void canNotUpdate_WithEmptyBin() {
        String existingBinId = BinsHelper.getCreatedBin_ID();

        Response response = RestAssured.given()
                .basePath(getBasePath() + existingBinId)
                .header(Headers.MASTER_KEY.getName(), getMasterKey())
                .when()
                .put();
        response
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canNotUpdateBin_Private_NoAuth() {
        String existingBinId = BinsHelper.getCreatedBin_ID(true);

        RestAssured.given()
                .basePath(getBasePath() + existingBinId)
                .body(BinsHelper.toJson(VALID_TO_UPDATE_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .put()
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canNotUpdateBin_InvalidId() {
        String invalidBinId = "some_id";

        RestAssured.given()
                .basePath(getBasePath() + invalidBinId)
                .header(Headers.ACCESS_KEY.getName(), getUpdateOnlyKey())
                .body(BinsHelper.toJson(VALID_TO_UPDATE_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .put()
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canNotUpdateBin_DoesNotExistId() {
        String doesNotExistBinId = "67598cfcacd3cb34a8b7d725";

        RestAssured.given()
                .basePath(getBasePath() + doesNotExistBinId)
                .header(Headers.ACCESS_KEY.getName(), getUpdateOnlyKey())
                .body(BinsHelper.toJson(VALID_TO_UPDATE_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .put()
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @Test(dataProvider = "isPrivate")
    public void canNotUpdateBin_WrongAuth(boolean isPrivate) {
        String existingBinId = BinsHelper.getCreatedBin_ID(isPrivate);

        RestAssured.given()
                .basePath(getBasePath() + existingBinId)
                .header(Headers.ACCESS_KEY.getName(), getDeleteCreateKey())
                .body(BinsHelper.toJson(VALID_TO_UPDATE_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .put()
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @DataProvider(name = "isPrivate")
    private Object[] isPrivate() {
        return new Object[]{true, false};
    }
}
