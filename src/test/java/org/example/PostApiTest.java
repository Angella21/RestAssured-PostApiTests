package org.example;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class PostApiTest {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    @BeforeAll
    static void setup() {
        // Set base URI once for all tests
        RestAssured.baseURI = BASE_URL;

        // log requests/responses only when validation fails
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    // GET single post
    @Test
    void getPostById(){
       // RestAssured.baseURI =BASE_URL;
        given()
                .when()
                .get("/posts/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("userId", equalTo(1))
                .log().body();


    }

    //GET all posts
    @Test
    void getAllPosts(){
       // RestAssured.baseURI = BASE_URL;

        given()
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .log().body()  //print the JSON response in the console
                .body("size()", equalTo(100))
                .body("[0].userId", equalTo(1))
                //.log().ifValidationFails(); //  print response only if something goes wrong
    }

    // POST create new post
    @Test
    void createPostAndVerify() {
       // RestAssured.baseURI = BASE_URL;

        String requestBody = """
            {
                "title": "My new post",
                "body": "This is the content of my post",
                "userId": 1
            }""";

        // Create post and extract ID
        Integer postId =
                given()
                        .contentType(ContentType.JSON)
                        .body(requestBody)
                        .when()
                        .post("/posts")
                        .then()
                        .statusCode(201)
                        .body("title", equalTo("My new post"))
                        .body("body", equalTo("This is the content of my post"))
                        .body("userId", equalTo(1))
                        .extract()
                        .path("id");

        System.out.println("Created post ID: " + postId);

        // Verify the created post using GET
        given()
                .pathParam("id", postId)
                .when()
                .get("/posts/{id}")
                .then()
                .statusCode(200)
                .body("title", equalTo("My new post"))
                .body("body", equalTo("This is the content of my post"))
                .body("userId", equalTo(1));
    }


    // PUT full update
    @Test
    void updatePost(){
       // RestAssured.baseURI = BASE_URL;

        String updatedBody = """
                {
                "id": 1,
                "title": "Updated title",
                "body": "Updated content",
                "userId": 1
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(updatedBody)
                .when()
                .put("/posts/1")
                .then()
                .statusCode(200)
                .log().body()
                .body("id", equalTo(1))
                .body("title", equalTo("Updated title"))
                .body("userId", equalTo(1));

    }

    // PATCH partial update
    @Test
    void partialUpdatePost(){
        //RestAssured.baseURI = BASE_URL;

        String patchBody = """
               { 
                "title" : "Partially updated title"
               
                }""";


        given()
                .contentType(ContentType.JSON)
                .body(patchBody)
                .when()
                .patch("/posts/1")
                .then()
                .statusCode(200)
                .log().body()
                .body("id", equalTo(1))
                .body("title", equalTo("Partially updated title"));
                //.body("body", equalTo("Updated content"))
               // .body("userId", equalTo(1));

    }

    // DELETE post
    @Test
    void deletePost(){
       // RestAssured.baseURI = BASE_URL;

        given()
                .when()
                .delete("/posts/1")
                .then()
                .statusCode(200)
                .log().body();
    }

    //negative test case : GET non-existing post
    @Test
    void getNonExistingPost(){
       // RestAssured.baseURI = BASE_URL;

        given()
                .when()
                .get("/posts/9999")
                .then()
                .statusCode(404)
                .log().body();
    }
}
