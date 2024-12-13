package bins;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import util.Headers;

import java.util.ArrayList;
import java.util.List;

import static util.PropertiesHelper.getDeleteCreateKey;

public class BaseBinsTest {

    private static final List<String> createdBinsIds = new ArrayList<>();

    @BeforeClass
    public static void before() {
        RestAssured.baseURI = "https://api.jsonbin.io/v3";
        RestAssured
                .filters(new RequestLoggingFilter(),
                        new ResponseLoggingFilter());
    }

    @AfterClass
    public static void after() {
        RestAssured.basePath = "";
        RestAssured.requestSpecification = null;
        RestAssured.responseSpecification = null;
        cleanUpCreatedBins();
    }

    public static void addToCreatedBinsIds(String binId) {
        createdBinsIds.add(binId);
    }

    private static void cleanUpCreatedBins() {
        for (String binId : createdBinsIds) {
            RestAssured.given()
                    .basePath("/b/" + binId)
                    .header(Headers.ACCESS_KEY.getName(), getDeleteCreateKey())
                    .when()
                    .delete()
                    .then()
                    .statusCode(HttpStatus.SC_OK);
        }
    }
}
