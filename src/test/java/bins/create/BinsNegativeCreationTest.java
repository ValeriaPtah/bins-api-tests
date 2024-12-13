package bins.create;

import bins.model.BinRequestBody;
import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import util.BinsHelper;
import util.Headers;

import java.io.File;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static util.PropertiesHelper.getMasterKey;
import static util.PropertiesHelper.getReadOnlyKey;
import static util.Schemas.ERROR_SCHEMA;

public class BinsNegativeCreationTest {
    private final static BinRequestBody VALID_TEST_BIN_RESPONSE_BODY = BinsHelper.testBinRequestBody();
    private final static File ERROR_MESSAGE_SCHEMA = ERROR_SCHEMA.getSchemaFile();

    @BeforeClass
    public static void setup() {
        BinsHelper.testSetup("/b");
    }

    /**
     * As per documentation, blank name is not allowed (should be between 1-128 characters) so this test reveals a bug
     * <a href="https://jsonbin.io/api-reference/bins/create#request-headers">X-BinResponseBody-Name</a>
     */
    @Test
    public void canNotCreateBin_NameBlank() {
        RestAssured.given()
                .header(Headers.MASTER_KEY.getName(), getMasterKey())
                .header(Headers.BIN_NAME.getName(), "")
                .body(BinsHelper.toJson(VALID_TEST_BIN_RESPONSE_BODY, BinRequestBody.class))
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(matchesJsonSchema(ERROR_MESSAGE_SCHEMA));
    }

    @Test
    public void canNotCreateBin_MoreThan128Chars() {
        RestAssured.given()
                .header(Headers.MASTER_KEY.getName(), getMasterKey())
                .header(Headers.BIN_NAME.getName(), RandomStringUtils.randomAlphabetic(129))
                .body(BinsHelper.toJson(VALID_TEST_BIN_RESPONSE_BODY, BinRequestBody.class))
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(matchesJsonSchema(ERROR_MESSAGE_SCHEMA));
    }

    @Test
    public void canNotCreateBin_NoAuth() {
        RestAssured.given()
                .body(BinsHelper.toJson(VALID_TEST_BIN_RESPONSE_BODY, BinRequestBody.class))
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(matchesJsonSchema(ERROR_MESSAGE_SCHEMA));
    }

    @Test
    public void canNotCreateBin_WrongAuth() {
        RestAssured.given()
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .body(BinsHelper.toJson(VALID_TEST_BIN_RESPONSE_BODY, BinRequestBody.class))
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(matchesJsonSchema(ERROR_MESSAGE_SCHEMA));
    }

    @Test
    public void canNotCreateBin_InvalidJson() {
        final BinRequestBody testInvalidJsonBinResponseBody = BinsHelper.testBinRequestBody_InvalidJson();

        RestAssured.given()
                .header(Headers.MASTER_KEY.getName(), getMasterKey())
                .body(BinsHelper.toJson(testInvalidJsonBinResponseBody, BinRequestBody.class))
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(matchesJsonSchema(ERROR_MESSAGE_SCHEMA));
    }
}
