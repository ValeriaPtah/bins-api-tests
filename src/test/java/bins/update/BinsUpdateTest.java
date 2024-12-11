package bins.update;

import bins.BaseBinsTest;
import util.BinsHelper;
import bins.model.Bin;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class BinsUpdateTest extends BaseBinsTest {

    @BeforeClass
    public static void setup() {
        RestAssured.basePath = "/b";
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
        RestAssured.responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_CREATED)
                .expectContentType(ContentType.JSON)
                .build();
    }

    @Test
    public void canUpdateBin_Public() {
        final File createdBinSchema = BinsHelper.getJsonSchema("created-bin-schema.json");
        final Bin testBin = BinsHelper.testBinsEntry();

        RestAssured.given()
                .body(BinsHelper.toJson(testBin, Bin.class))
                .when()
                .post()
                .then()
                .body(matchesJsonSchema(createdBinSchema));
    }

    @Test
    public void canUpdateBin_Private() {

    }

    @Test
    public void canNotUpdateBin_Private_NoAuth() {

    }
}
