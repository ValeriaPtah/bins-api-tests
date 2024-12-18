package bins.update;

import bins.BaseBinsTest;
import bins.model.BinRequestBody;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import util.BinsHelper;
import util.Headers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static util.PropertiesHelper.getBasePath;
import static util.PropertiesHelper.getMasterKey;
import static util.PropertiesHelper.getPath_BinParentId;
import static util.PropertiesHelper.getPath_BinVersion;
import static util.PropertiesHelper.getPath_Etag;
import static util.PropertiesHelper.getUpdateOnlyKey;

/**
 * Documentation: <a href="https://jsonbin.io/api-reference/bins/update">Update Bins API</a>
 */
public class BinsUpdateTest extends BaseBinsTest {
    private static final BinRequestBody VALID_TO_UPDATE_BIN_REQUEST_BODY = BinsHelper.testBinRequestBody();

    @BeforeClass
    public static void setup() {
        testSetup();
    }

    @Test
    public void canUpdateBin_MasterKey() {
        String existingBinId = BinsHelper.getCreatedBin_ID();

        Response response = RestAssured.given()
                .basePath(getBasePath() + existingBinId)
                .header(Headers.MASTER_KEY.getName(), getMasterKey())
                .body(BinsHelper.toJson(VALID_TO_UPDATE_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .put();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(getPath_Etag(), equalTo(VALID_TO_UPDATE_BIN_REQUEST_BODY.getData().getEtag()))
                .body(getPath_BinParentId(), equalTo(existingBinId));
    }

    @Test
    public void canUpdateBin_AccessKey() {
        String existingBinId = BinsHelper.getCreatedBin_ID();

        Response response = RestAssured.given()
                .basePath(getBasePath() + existingBinId)
                .header(Headers.ACCESS_KEY.getName(), getUpdateOnlyKey())
                .body(BinsHelper.toJson(VALID_TO_UPDATE_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .put();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(getPath_Etag(), equalTo(VALID_TO_UPDATE_BIN_REQUEST_BODY.getData().getEtag()))
                .body(getPath_BinParentId(), equalTo(existingBinId));
    }

    @Test
    public void canUpdateBin_Public_NoAuth_CreatesVersion() {
        String existingBinId = BinsHelper.getCreatedBin_ID(false);

        Response response = RestAssured.given()
                .basePath(getBasePath() + existingBinId)
                .body(BinsHelper.toJson(VALID_TO_UPDATE_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .put();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(getPath_Etag(), equalTo(VALID_TO_UPDATE_BIN_REQUEST_BODY.getData().getEtag()))
                .body(getPath_BinVersion(), greaterThan(0));
    }

    @Test
    public void canDisableVersioning_WithMasterKey_ForPublic() {
        String existingBinId = BinsHelper.getCreatedBin_ID(false);

        Response response = RestAssured.given()
                .basePath(getBasePath() + existingBinId)
                .header(Headers.VERSIONING.getName(), false)
                .header(Headers.MASTER_KEY.getName(), getMasterKey())
                .body(BinsHelper.toJson(VALID_TO_UPDATE_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .put();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(getPath_Etag(), equalTo(VALID_TO_UPDATE_BIN_REQUEST_BODY.getData().getEtag()))
                .body(getPath_BinVersion(), is(nullValue()))
                .body(getPath_BinParentId(), equalTo(existingBinId));

        enableVersioning(existingBinId);
    }


}
