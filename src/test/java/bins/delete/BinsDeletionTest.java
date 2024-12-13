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
import static util.PropertiesHelper.getDeleteCreateKey;
import static util.PropertiesHelper.getMasterKey;
import static util.PropertiesHelper.getReadOnlyKey;
import static util.Schemas.DELETION_SCHEMA;
import static util.Schemas.ERROR_SCHEMA;

public class BinsDeletionTest extends BaseBinsTest {
    private final static String BASE_PATH = "/b/";

    @BeforeClass
    public static void setup() {
        BinsHelper.testSetup("");
    }

    @Test
    public void canDeleteBin_MasterKey() {
        String existingBinId = BinsHelper.getCreatedBinId();

        Response response = RestAssured.given()
                .basePath(BASE_PATH + existingBinId)
                .header(Headers.MASTER_KEY.getName(), getMasterKey())
                .when()
                .delete();
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchema(DELETION_SCHEMA.getSchemaFile()));

        String deletedBinId = response.body().jsonPath().get("metadata.id");
        Assert.assertEquals(BinsHelper.getStatusCode_GetBinById(deletedBinId), HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void canDeleteBin_AccessKey() {
        String existingBinId = BinsHelper.getCreatedBinId();

        Response response = RestAssured.given()
                .basePath(BASE_PATH + existingBinId)
                .header(Headers.ACCESS_KEY.getName(), getDeleteCreateKey())
                .when()
                .delete();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchema(DELETION_SCHEMA.getSchemaFile()));

        String deletedBinId = response.body().jsonPath().get("metadata.id");
        Assert.assertEquals(BinsHelper.getStatusCode_GetBinById(deletedBinId), HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void canNotDeleteBin_NotExist() {
        String NoneExistingBinId = "67598cfcacd3cb34a8b7d725";

        RestAssured.given()
                .basePath(BASE_PATH + NoneExistingBinId)
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
                .basePath(BASE_PATH + invalidBinId)
                .header(Headers.ACCESS_KEY.getName(), getDeleteCreateKey())
                .when()
                .delete()
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canNotDeleteBin_NoAuth() {
        String someBinId = "some_id";

        RestAssured.given()
                .basePath(BASE_PATH + someBinId)
                .when()
                .delete()
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canNotDeleteBin_WrongAuth() {
        String someBinId = "67598cfcacd3cb34a8b7d725";

        RestAssured.given()
                .basePath(BASE_PATH + someBinId)
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .when()
                .delete()
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(matchesJsonSchema(ERROR_SCHEMA.getSchemaFile()));
    }
}
