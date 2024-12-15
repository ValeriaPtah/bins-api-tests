package bins.create;

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

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static util.PropertiesHelper.getBasePath;
import static util.PropertiesHelper.getDeleteCreateKey;
import static util.PropertiesHelper.getMasterKey;
import static util.PropertiesHelper.getPath_BinAccess;
import static util.PropertiesHelper.getPath_BinID;
import static util.PropertiesHelper.getPath_BinName;
import static util.Schemas.BIN_SCHEMA;

/**
 * Documentation: <a href="https://jsonbin.io/api-reference/bins/create">Create Bins API</a>
 */
public class BinsCreationTest extends BaseBinsTest {
    private static final BinRequestBody VALID_BIN_REQUEST_BODY = BinsHelper.testBinRequestBody();

    @BeforeClass
    public static void setup() {
        testSetup(getBasePath());
    }

    @Test
    public void canCreateBin_MasterKey() {
        Response response = RestAssured.given()
                .header(Headers.MASTER_KEY.getName(), getMasterKey())
                .body(BinsHelper.toJson(VALID_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .post();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchema(BIN_SCHEMA.getSchemaFile()));

        addToCreatedBinsIds(response.body().jsonPath().get(getPath_BinID()));
    }

    @Test
    public void canCreateBin_AccessKey() {
        Response response = RestAssured.given()
                .header(Headers.ACCESS_KEY.getName(), getDeleteCreateKey())
                .body(BinsHelper.toJson(VALID_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .post();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchema(BIN_SCHEMA.getSchemaFile()));

        addToCreatedBinsIds(response.body().jsonPath().get(getPath_BinID()));
    }

    @Test
    public void canCreateBin_Private() {
        Response response = RestAssured.given()
                .header(Headers.ACCESS_KEY.getName(), getDeleteCreateKey())
                .header(Headers.PRIVATE_BIN.getName(), true)
                .body(BinsHelper.toJson(VALID_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .post();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchema(BIN_SCHEMA.getSchemaFile()));

        Assert.assertTrue(response.body().jsonPath().get(getPath_BinAccess()));

        addToCreatedBinsIds(response.body().jsonPath().get(getPath_BinID()));
    }

    @Test
    public void canCreateBin_Public() {
        Response response = RestAssured.given()
                .header(Headers.ACCESS_KEY.getName(), getDeleteCreateKey())
                .header(Headers.PRIVATE_BIN.getName(), false)
                .body(BinsHelper.toJson(VALID_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .post();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchema(BIN_SCHEMA.getSchemaFile()));

        Assert.assertFalse(response.body().jsonPath().get(getPath_BinAccess()));

        addToCreatedBinsIds(response.body().jsonPath().get(getPath_BinID()));
    }

    @Test
    public void canCreateBin_SetName() {
        String binName = "Bin Name";
        Response response = RestAssured.given()
                .header(Headers.MASTER_KEY.getName(), getMasterKey())
                .header(Headers.BIN_NAME.getName(), binName)
                .body(BinsHelper.toJson(VALID_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .post();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchema(BIN_SCHEMA.getSchemaFile()));

        Assert.assertEquals(binName, response.body().jsonPath().get(getPath_BinName()));

        addToCreatedBinsIds(response.body().jsonPath().get(getPath_BinID()));
    }
}
