package bins.update;

import bins.BaseBinsTest;
import bins.model.BinRequestBody;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import util.BinsHelper;
import util.Headers;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static util.PropertiesHelper.getMasterKey;
import static util.PropertiesHelper.getUpdateOnlyKey;

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


}
