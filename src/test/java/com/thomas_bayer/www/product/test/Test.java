package com.thomas_bayer.www.product.test; /**
 * Created by Dzmitry_Sankouski on 31-Mar-17.
 */

import com.thomas_bayer.www.product.bean.Product;
import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.thomas_bayer.www.product.util.Util.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class Test {
    private final String baseUri = "http://www.thomas-bayer.com/sqlrest";
    private final String productPath = "/PRODUCT/";
    private final String BASE_PATH = "C:\\Users\\Dzmitry_Sankouski\\IdeaProjects\\RESTServiceTest\\src\\test\\resources";
    private final File validProductPost = new File(BASE_PATH + "\\ValidProductPost");
    private final File validProductPut = new File(BASE_PATH + "\\ValidProductPut");// todo change full paths to relative
    private final File nonWellFormed = new File(BASE_PATH + "\\NonWellFormedXML");
    private final File NonExistingAttribute = new File(BASE_PATH + "\\NonExistingAttribute");
    private final File negativePrice = new File(BASE_PATH + "\\NegativePriceAttribute");
    private final File nonValidID = new File(BASE_PATH + "\\NonValidID");
    private final File nameAndPriceUpdate = new File(BASE_PATH + "\\nameAndPriceUpdate");
    private final File nameUpdate = new File(BASE_PATH + "\\nameUpdate");
    private final File PriceUpdate= new File(BASE_PATH + "\\PriceUpdate");

    private String freeId;
    private String busyId;

    //HTTP codes:
    private final int OK = 200;
    private final int CREATED = 201;
    private final int BADREQUEST = 400;
    private final int FORBIDDEN = 403;
    private final int NOTFOUND = 404;

    public Queue<String> createdIds = new ConcurrentLinkedQueue<String>();

    @BeforeClass
    public void setUpRestAssured() {
        RestAssured.baseURI = baseUri;
    }

    @AfterClass
    public void deleteCreated(){
        while (!createdIds.isEmpty()){
            delete(productPath + createdIds.poll());
        } // cleanup
    }

    @BeforeMethod
    public void SetUpIds(){
        String productPage = get(productPath).asString();
        freeId = getFirstFreeId(productPage);
        busyId = getFirstBusyId(productPage);
    }

    @AfterMethod
    public void deleteCreatedRecords(){
        createdIds.offer(freeId);
    }

    //----------------creation tests

    @org.testng.annotations.Test
    public void OneCanCreateProductWithPost() {
        Product expected = getProduct(validProductPost);
        String id = expected.getID(); //getting ID from xml to create assert
        given().body(validProductPost)
                .when()
                .post(productPath)
                .then()
                .assertThat()
                .statusCode(CREATED);
        Product actual = getProduct(get(productPath+ id).asString());
        Assert.assertEquals(expected, actual);
    }


    @org.testng.annotations.Test
    public void OneCanCreateProductWithPut() {
        given().body(validProductPut)
                .when()
                .put(productPath+ freeId)
                .then()
                .assertThat()
                .statusCode(CREATED);
        Assert.assertEquals(getProduct(validProductPost), getProduct(get(productPath+ freeId).asString()));
    }


    @org.testng.annotations.Test
    public void linkCreatedWhenProductAdded() {
        given().body(validProductPut)
                .when()
                .put(productPath+ freeId)
                .then()
                .assertThat()
                .statusCode(CREATED);
        Assert.assertTrue(getBusyIDs(get(productPath).asString()).contains(freeId));
    }

    @org.testng.annotations.Test
    public void CreateRecordWithNonWell_formedXMLPost() {
        given().body(nonWellFormed)
                .when()
                .post(productPath)
                .then()
                .assertThat()
                .statusCode(BADREQUEST);
    }

    @org.testng.annotations.Test
    public void CreateProductWithNegativePrice() {
        given().body(negativePrice)
                .when()
                .post(productPath + freeId)
                .then()
                .assertThat()
                .statusCode(FORBIDDEN);
    }

    //----------------update tests

    @org.testng.annotations.Test
    public void OneCanUpdateRecord() {
        Product expected = getProduct(nameAndPriceUpdate);
        expected.setID(busyId);

        given().body(nameUpdate)
                .when()
                .post(productPath + busyId)
                .then()
                .assertThat()
                .statusCode(OK);
        Product actual = getProduct(get(productPath+ busyId).asString());
        Assert.assertEquals(expected, actual);
    }

    @org.testng.annotations.Test
    public void OneCanUpdateNameOfRecord() {
        String expectedName = getXMLPath(nameUpdate).get("NAME");

        given().body(nameUpdate)
                .when()
                .post(productPath + busyId)
                .then()
                .assertThat()
                .statusCode(OK);
        RestAssured.reset();

        when()
                .get(productPath + busyId)
                .then()
                .assertThat()
                .statusCode(OK)
                .and()
                .body("PRODUCT.NAME", equalTo(expectedName));
    }


    @org.testng.annotations.Test
    public void UpdateRecordWithNonExistingAttribute() {
        given().body(NonExistingAttribute)
                .when()
                .post(productPath + busyId)
                .then()
                .assertThat()
                .statusCode(FORBIDDEN);
    }


    @org.testng.annotations.Test
    public void UpdateRecordNonValidID() {
        String wrongId = getProduct(nonValidID).getID();
        given().body(nonValidID)
                .when()
                .post(productPath + wrongId)
                .then()
                .assertThat()
                .statusCode(FORBIDDEN);
    }

    //----------------Delete tests

    @org.testng.annotations.Test
    public void OneCanDeleteExistingProduct() {
        when()
                .delete(productPath + busyId)
                .then()
                .assertThat()
                .statusCode(OK);
        RestAssured.reset();
        when()
                .get(productPath + busyId)
                .then()
                .assertThat()
                .statusCode(NOTFOUND);
    }

    @org.testng.annotations.Test
    public void OneCanDeleteNonExistingProduct() {
        when()
                .delete(productPath + freeId)
                .then()
                .assertThat()
                .statusCode(NOTFOUND);
    }

}
