# Bins API e2e Tests

E2E API tests with RestAssured for [Bins API](https://jsonbin.io/api-reference/bins/get-started) free acount features.

### Built With

* [TestNG](https://testng.org/doc/)
* [Gradle](https://gradle.org/)
* [Project Lombok](https://projectlombok.org/)
* [REST-assured](https://rest-assured.io/)

### Prerequisites

* Make sure you have annotation processing enabled:
   * IntelliJ: ```Settings/Preference > search for "Annotation Processor" > Enable annotation processing```
   * Eclipse: ```project Properties > Java Compiler > Annotation Processing > Enable annotation processing```
* Have Lombok plugin installed in your IDE ([IntelliJ](https://projectlombok.org/setup/intellij), [Eclipse](https://projectlombok.org/setup/eclipse))

### Running the tests

1. Clone the repo
   ```sh
   git clone https://github.com/ValeriePtah/bins-api-tests.git
   ```
2. To see full console output run tests from the Gradle toolbar
   ```
   Gradle > Tasks > verification > test
   ```

### License

Distributed under the MIT License.
