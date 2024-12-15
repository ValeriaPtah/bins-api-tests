package bins.read;

import bins.BaseBinsTest;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import util.BinsHelper;
import util.Headers;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static util.PropertiesHelper.getBasePath;
import static util.PropertiesHelper.getMasterKey;
import static util.PropertiesHelper.getPath_BinID;
import static util.PropertiesHelper.getPath_Etag;
import static util.PropertiesHelper.getReadOnlyKey;
import static util.Schemas.BIN_SCHEMA;

/**
 * Documentation: <a href="https://jsonbin.io/api-reference/bins/read">Read Bins API</a>
 */
public class BinsReadTest extends BaseBinsTest {

    @BeforeClass
    public static void setup() {
        testSetup();
    }

    @Test
    public void canReadBin_Private_MasterKey() {
        String existingBinId = BinsHelper.getCreatedBin_ID();

        Response response = RestAssured.given()
                .basePath(getBasePath() + existingBinId)
                .header(Headers.MASTER_KEY.getName(), getMasterKey())
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchema(BIN_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canReadBin_Private_AccessKey() {
        String existingBinId = BinsHelper.getCreatedBin_ID();

        Response response = RestAssured.given()
                .basePath(getBasePath() + existingBinId)
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchema(BIN_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canReadBin_Public_NoAuth() {
        String existingBinId = BinsHelper.getCreatedBin_ID(false);

        Response response = RestAssured.given()
                .basePath(getBasePath() + existingBinId)
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchema(BIN_SCHEMA.getSchemaFile()));
    }

    @Test
    public void canReadSpecificVersion() {
        String existingBinId = BinsHelper.getBinWithTwoVersions_ID();
        String version = "1";

        Response response = RestAssured.given()
                .basePath(getBasePath() + existingBinId + "/" + version)
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchema(BIN_SCHEMA.getSchemaFile()))
                .body(getPath_Etag(), equalTo(BinsHelper.getEtagByVersion(Integer.parseInt(version))));
    }

    @Test
    public void canRead_NoMetadata_Query() {
        String existingBinId = BinsHelper.getCreatedBin_ID();

        Response response = RestAssured.given()
                .basePath(getBasePath() + existingBinId)
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .queryParam("meta", false)
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("metadata", is(nullValue()));
    }

    @Test
    public void canRead_NoMetadata_Header() {
        String existingBinId = BinsHelper.getCreatedBin_ID();

        Response response = RestAssured.given()
                .basePath(getBasePath() + existingBinId)
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .header(Headers.METADATA.getName(), false)
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("metadata", is(nullValue()));
    }

    @Test
    public void canReadLatestVersion() {
        String existingBinId = BinsHelper.getBinWithTwoVersions_ID();

        Response response = RestAssured.given()
                .basePath(getBasePath() + existingBinId + "/latest")
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchema(BIN_SCHEMA.getSchemaFile()))
                .body(getPath_Etag(), equalTo(BinsHelper.getEtagLatestVersion()));
    }

    @Test
    public void canReadLatestVersion_NoVersion() {
        JsonPath existingBin = BinsHelper.getCreatedBin(false);
        String existingBinId = existingBin.get(getPath_BinID());

        Response response = RestAssured.given()
                .basePath(getBasePath() + existingBinId + "/latest")
                .header(Headers.ACCESS_KEY.getName(), getReadOnlyKey())
                .when()
                .get();
        response
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchema(BIN_SCHEMA.getSchemaFile()))
                .body(getPath_Etag(), equalTo(existingBin.get(getPath_Etag())));
    }
}
