package com.nordigy.testrestapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

import net.minidev.json.JSONObject;
import io.restassured.response.Response;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// It allows to refresh context(Database) before an each method. So your tests always will be executed on the same snapshot of DB.
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class RestApiTests {

    @LocalServerPort
    private int port;

    @PostConstruct
    public void init() {
        RestAssured.port = port;
    }

    @Test
    public void shouldReturnCorrectUsersListSize() {
        given().log().all()
            .contentType(ContentType.JSON)
            .when().get("/api/users")
            .then().log().ifValidationFails()
            .statusCode(200)
            .body("page.totalElements", is(20));
    }


    @Test
    public void shouldReturnCorrectUser() {
        given().log().all()
            .contentType(ContentType.JSON)
            .when().get("/api/users/{id}", 1)
            .then().log().ifValidationFails()
            .statusCode(200)
            .body("id", is(1));
    }

    @Test
    public void shouldReturnErrorNoUser() {
        given().log().all()
            .when().get("/api/users/{id}", 21)
            .then().log().all()
            .statusCode(404);
    }

    @Test
    public void shouldReturnErrorNoContentToThisUser() {
        given().log().all( )
            .contentType(ContentType.JSON)
            .when().delete("/api/users/{id}", 1)
            .then().log().ifValidationFails();

        given().log().all()
            .contentType(ContentType.JSON)
            .when().get("/api/users/{id}", 1)
            .then().log().all()
            .statusCode(404);
    }

    @Test
    public void shouldReturnErrorUncorrectDayOfBirth() {

        JSONObject requestParams = new JSONObject();
        requestParams.put("firstName", "Fo");
        requestParams.put("lastName", "Hu");
        requestParams.put("dayOfBirth", "2020-12-22");
        requestParams.put("email", "f@tre.ru");

        given().body(requestParams).log().all()
            .contentType(ContentType.JSON)
            .when().put("/api/users/{id}", 2)
            .then().log().ifValidationFails()
            .statusCode(400);

    }

    @Test
    public void shouldReturnErrorFirstNameIsNull() {

        JSONObject requestParams = new JSONObject();
        requestParams.put("firstName", null);
        requestParams.put("lastName", "Gee");
        requestParams.put("dayOfBirth", "1990-12-02");
        requestParams.put("email", "gee@gmail.com");

        given().body(requestParams).log().all()
            .contentType(ContentType.JSON)
            .when().post("/api/users/")
            .then().log().ifValidationFails()
            .statusCode(400);
    }



    // Проверка тест-кейсов
    @Test
    public void shouldReturnErrorFirstNameIsNotValid() {

        JSONObject requestParams = new JSONObject();
        requestParams.put("firstName", "ann!");
        requestParams.put("lastName", "Gee");
        requestParams.put("dayOfBirth", "1990-12-02");
        requestParams.put("email", "gee@gmail.com");

        given().body(requestParams).log().all()
            .contentType(ContentType.JSON)
            .when().post("/api/users/")
            .then().log().ifValidationFails()
            //TODO: Должна быть 400
            .statusCode(201);
    }

    @Test
    public void TestWithArrayInput() {

        int[] arrayName = {1,2,3};
        JSONObject requestParams = new JSONObject();
        requestParams.put("firstName", arrayName);
        requestParams.put("lastName", "Gee");
        requestParams.put("dayOfBirth", "1990-12-02");
        requestParams.put("email", "ex3@gmail.com");

        given().body(requestParams).log().all()
            .contentType(ContentType.JSON)
            .when().post("/api/users/")
            .then().log().ifValidationFails()
            .statusCode(400);
    }

    @Test
    public void TestWithTheSameEmail() {
        String sameEmail = "mail@gmail.com";
        JSONObject user = new JSONObject();
        user.put("firstName", "Bree");
        user.put("lastName", "Gee");
        user.put("dayOfBirth", "1990-12-02");
        user.put("email", sameEmail);

        JSONObject userWithTheSameEmail = new JSONObject();
        userWithTheSameEmail.put("firstName", "Drake");
        userWithTheSameEmail.put("lastName", "Nile");
        userWithTheSameEmail.put("dayOfBirth", "1990-12-02");
        userWithTheSameEmail.put("email", sameEmail);

        given().body(user).contentType(ContentType.JSON).log().all()
            .contentType(ContentType.JSON)
            .when().post("/api/users/")
            .then().log().ifValidationFails();

        given().body(userWithTheSameEmail).contentType(ContentType.JSON).log().all()
            .contentType(ContentType.JSON)    
            .when().post("/api/users/")
            .then().log().all()
            .statusCode(409);

    }

    @Test
    public void shouldReturnErrorUncorrectEmailFormat() {

        JSONObject requestParams = new JSONObject();
        requestParams.put("firstName", "Fo");
        requestParams.put("lastName", "Hu");
        requestParams.put("dayOfBirth", "2010-12-22");
        requestParams.put("email", "fotre.ru");

        given().body(requestParams).log().all()
            .contentType(ContentType.JSON)
            .when().put("/api/users/{id}", 3)
            .then().log().ifValidationFails()
            .statusCode(400);
    }

    @Test
    public void shouldReturnErrorLastNameIsTooShort() {

        JSONObject requestParams = new JSONObject();
        requestParams.put("firstName", "Ben");
        requestParams.put("lastName", "G");
        requestParams.put("dayOfBirth", "1990-12-22");
        requestParams.put("email", "fotre@gmail.com");

        given().body(requestParams).log().all()
            .contentType(ContentType.JSON)
            .when().post("/api/users/")
            .then().log().ifValidationFails()
            .statusCode(400);
    }

    @Test
    public void shouldReturnErrorFirstNameIsTooLong() {

        JSONObject requestParams = new JSONObject();
        requestParams.put("firstName", "Bee");
        requestParams.put("lastName", "Bengameenebrootie");
        requestParams.put("dayOfBirth", "1990-12-02");
        requestParams.put("email", "gee@gmail.com");

        given().body(requestParams).log().all()
            .contentType(ContentType.JSON)
            .when().post("/api/users/")
            .then().log().ifValidationFails()
            .statusCode(400);
    }

    @Test
    public void shouldCreateNewUser() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("firstName", "Ivan");
        objectNode.put("lastName", "Ivanov");
        objectNode.put("dayOfBirth", "2000-01-01");
        objectNode.put("email", "asdas@asdas.tr");



        ObjectNode user = given().log().all()
                                 .body(objectNode)
                                 .contentType(ContentType.JSON)
                                 .when().post("/api/users")
                                 .then().log().ifValidationFails()
                                 .statusCode(201)
                                 .extract().body().as(ObjectNode.class);

        assertThat(user.get("firstName")).isEqualTo(objectNode.get("firstName"));
        assertThat(user.get("lastName")).isEqualTo(objectNode.get("lastName"));
        assertThat(user.get("dayOfBirth")).isEqualTo(objectNode.get("dayOfBirth"));
        assertThat(user.get("email")).isEqualTo(objectNode.get("email"));
        assertThat(user.get("id").asLong()).isGreaterThan(20);
    }



    // TODO: The test methods above are examples of test cases.
    //  Please add new cases below, but don't hesitate to refactor the whole class.
}