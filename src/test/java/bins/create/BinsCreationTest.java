package bins.create;

import bins.BaseBinsTest;
import bins.model.BinRequestBody;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import util.BinsHelper;
import util.Headers;

import java.io.File;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static util.PropertiesHelper.getDeleteCreateKey;
import static util.PropertiesHelper.getMasterKey;
import static util.Schemas.CREATION_SCHEMA;

public class BinsCreationTest extends BaseBinsTest {
    private final static BinRequestBody VALID_BIN_REQUEST_BODY = BinsHelper.testBinRequestBody();
    private final static int SUCCESS_STATUS = HttpStatus.SC_OK;
    private final static File CREATED_BIN_SCHEMA = CREATION_SCHEMA.getSchemaFile();

    @BeforeClass
    public static void setup() {
        BinsHelper.testSetup("/b");
    }

    @Test
    public void canCreateBin_MasterKey() {
        RestAssured.given()
                .header(Headers.MASTER_KEY.getName(), getMasterKey())
                .body(BinsHelper.toJson(VALID_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .post()
                .then()
                .statusCode(SUCCESS_STATUS)
                .body(matchesJsonSchema(CREATED_BIN_SCHEMA));
    }

    @Test
    public void canCreateBin_AccessKey() {
        RestAssured.given()
                .header(Headers.ACCESS_KEY.getName(), getDeleteCreateKey())
                .body(BinsHelper.toJson(VALID_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .post()
                .then()
                .statusCode(SUCCESS_STATUS)
                .body(matchesJsonSchema(CREATED_BIN_SCHEMA));
    }

    @Test
    public void canCreateBin_Private() {
        ValidatableResponse response = RestAssured.given()
                .header(Headers.ACCESS_KEY.getName(), getDeleteCreateKey())
                .header(Headers.PRIVATE_BIN.getName(), true)
                .body(BinsHelper.toJson(VALID_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .post()
                .then()
                .statusCode(SUCCESS_STATUS)
                .body(matchesJsonSchema(CREATED_BIN_SCHEMA));

        Assert.assertTrue(response.extract().body().jsonPath().get("metadata.private"));
    }

    @Test
    public void canCreateBin_Public() {
        ValidatableResponse response = RestAssured.given()
                .header(Headers.ACCESS_KEY.getName(), getDeleteCreateKey())
                .header(Headers.PRIVATE_BIN.getName(), false)
                .body(BinsHelper.toJson(VALID_BIN_REQUEST_BODY, BinRequestBody.class))
                .when()
                .post()
                .then()
                .statusCode(SUCCESS_STATUS)
                .body(matchesJsonSchema(CREATED_BIN_SCHEMA));

        Assert.assertFalse(response.extract().body().jsonPath().get("metadata.private"));
    }
}
