package bins;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class BaseBinsTest {

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
    }
}
