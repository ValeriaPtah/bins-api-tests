package bins.update;

import bins.BaseBinsTest;
import bins.model.BinRequestBody;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import util.BinsHelper;
import util.Headers;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static util.PropertiesHelper.getDeleteCreateKey;
import static util.PropertiesHelper.getMasterKey;
import static util.PropertiesHelper.getUpdateOnlyKey;
import static util.Schemas.ERROR_SCHEMA;

/**
 * Documentation: <a href="https://jsonbin.io/api-reference/bins/update">Update Bins API</a>
 */
public class BinsUpdateTest extends BaseBinsTest {
    private final static BinRequestBody VALID_TO_UPDATE_BIN_REQUEST_BODY = BinsHelper.testBinRequestBody_updated();
    private final static String BASE_PATH = "/b/";

    @BeforeClass
    public static void setup() {
        BinsHelper.testSetup("");
    }

    @Test
    public void canUpdateBin_MasterKey() {
        String existingBinId = BinsHelper.getCreatedBinId();

        Response response = RestAssured.given()
                .basePath(BASE_PATH + existingBinId)
                .header(Headers.MASTER_KEY.getName(), getMasterKey())
                .body(BinsHelper.toJson(VALID_TO_UPDATE_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .put();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("record", hasEntry("data", VALID_TO_UPDATE_BIN_REQUEST_BODY.getData()));

        Assert.assertEquals(existingBinId, response.body().jsonPath().get("metadata.parentId"));
    }

    @Test
    public void canUpdateBin_AccessKey() {
        String existingBinId = BinsHelper.getCreatedBinId();

        Response response = RestAssured.given()
                .basePath(BASE_PATH + existingBinId)
                .header(Headers.ACCESS_KEY.getName(), getUpdateOnlyKey())
                .body(BinsHelper.toJson(VALID_TO_UPDATE_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .put();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("record", hasEntry("data", VALID_TO_UPDATE_BIN_REQUEST_BODY.getData()));

        Assert.assertEquals(existingBinId, response.body().jsonPath().get("metadata.parentId"));
    }

    @Test
    public void canUpdateBin_Public_NoAuth_CreatesVersion() {
        String existingBinId = BinsHelper.getCreatedBinId(false);

        Response response = RestAssured.given()
                .basePath(BASE_PATH + existingBinId)
                .body(BinsHelper.toJson(VALID_TO_UPDATE_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .put();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("record", hasEntry("data", VALID_TO_UPDATE_BIN_REQUEST_BODY.getData()));

        Assert.assertTrue((int) response.body().jsonPath().get("metadata.version") > 0);
    }

    @Test
    public void canDisableVersioning_WithMasterKey_ForPublic() {
        String existingBinId = BinsHelper.getCreatedBinId(false);

        Response response = RestAssured.given()
                .basePath(BASE_PATH + existingBinId)
                .header(Headers.VERSIONING.getName(), false)
                .header(Headers.MASTER_KEY.getName(), getMasterKey())
                .body(BinsHelper.toJson(VALID_TO_UPDATE_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .put();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("record", hasEntry("data", VALID_TO_UPDATE_BIN_REQUEST_BODY.getData()))
                .body("metadata.version", is(nullValue()));

        Assert.assertEquals(existingBinId, response.body().jsonPath().get("metadata.parentId"));
    }

    /**
     * As per documentation, to disable versioning on Public bins one needs to pass Master Key so this test reveals a bug
     * <a href="https://jsonbin.io/api-reference/bins/update#request-headers">X-Bin-Versioning</a>
     */
    @Test
    public void canNotDisableVersioning_WithAccessKey_ForPublic() {
        String existingBinId = BinsHelper.getCreatedBinId(false);

        Response response = RestAssured.given()
                .basePath(BASE_PATH + existingBinId)
                .header(Headers.VERSIONING.getName(), false)
                .header(Headers.ACCESS_KEY.getName(), getUpdateOnlyKey())
                .body(BinsHelper.toJson(VALID_TO_UPDATE_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .put();
        response
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canNotUpdate_BinPermission() {
    }

    @Test
    public void canNotUpdate_BinName() {
    }

    @Test
    public void canNotUpdate_WithEmptyBin() {
        String existingBinId = BinsHelper.getCreatedBinId();

        Response response = RestAssured.given()
                .basePath(BASE_PATH + existingBinId)
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
        String existingBinId = BinsHelper.getCreatedBinId(true);

        RestAssured.given()
                .basePath(BASE_PATH + existingBinId)
                .body(BinsHelper.toJson(VALID_TO_UPDATE_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .put()
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @Test(dataProvider = "isPrivate")
    public void canNotUpdateBin_WrongAuth(boolean isPrivate) {
        String existingBinId = BinsHelper.getCreatedBinId(isPrivate);

        RestAssured.given()
                .basePath(BASE_PATH + existingBinId)
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
