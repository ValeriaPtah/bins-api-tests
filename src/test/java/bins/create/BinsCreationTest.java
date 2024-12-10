package bins.create;

import bins.BaseBinsTest;
import bins.model.Bin;
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

public class BinsCreationTest extends BaseBinsTest {
    private final static File CREATION_SCHEMA = BinsHelper.getJsonSchema("created-bin-response-schema.json");
    private final static Bin VALID_TEST_BIN = BinsHelper.testBinsEntry();

    @BeforeClass
    public static void setup() {
        BinsHelper.testSetup("/b");
    }

    @Test
    public void canCreateBin_MasterKey() {
        RestAssured.given()
                .header(Headers.MASTER_KEY.getName(), getMasterKey())
                .body(BinsHelper.toJson(VALID_TEST_BIN, Bin.class))
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchema(CREATION_SCHEMA));
    }

    @Test
    public void canCreateBin_AccessKey() {
        RestAssured.given()
                .header(Headers.ACCESS_KEY.getName(), getDeleteCreateKey())
                .body(BinsHelper.toJson(VALID_TEST_BIN, Bin.class))
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchema(CREATION_SCHEMA));
    }

    @Test
    public void canCreateBin_Private() {
        ValidatableResponse response = RestAssured.given()
                .header(Headers.ACCESS_KEY.getName(), getDeleteCreateKey())
                .header(Headers.PRIVATE_BIN.getName(), true)
                .body(BinsHelper.toJson(VALID_TEST_BIN, Bin.class))
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.SC_OK);

        Assert.assertTrue(response.extract().body().jsonPath().get("metadata.private"));
    }

    @Test
    public void canCreateBin_Public() {
        ValidatableResponse response = RestAssured.given()
                .header(Headers.ACCESS_KEY.getName(), getDeleteCreateKey())
                .header(Headers.PRIVATE_BIN.getName(), false)
                .body(BinsHelper.toJson(VALID_TEST_BIN, Bin.class))
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.SC_OK);

        Assert.assertFalse(response.extract().body().jsonPath().get("metadata.private"));

    }

    @Test
    public void canNotCreateBin_MoreThan128Chars() {

    }

    @Test
    public void canNotCreateBin_NoAuth() {

    }

    @Test
    public void canNotCreateBin_InvalidJson() {
        final Bin testInvalidJsonBin = BinsHelper.testBinsEntry_InvalidJson();

        RestAssured.given()
                .header(Headers.MASTER_KEY.getName(), getMasterKey())
                .body(BinsHelper.toJson(testInvalidJsonBin, Bin.class))
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }
}
