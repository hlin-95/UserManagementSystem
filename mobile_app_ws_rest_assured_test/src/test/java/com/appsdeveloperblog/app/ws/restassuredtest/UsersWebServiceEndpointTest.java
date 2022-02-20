package com.appsdeveloperblog.app.ws.restassuredtest;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UsersWebServiceEndpointTest {
	private final String CONTEXT_PATH = "/mobile_app_ws";
	private final String EMAIL_ADDRESS = "lynnhuang95@gmail.com";
	private static String authorizationHeader;
	private static String userId;
	private static List<Map<String, String>> addresses;
	private static String addressId;
	
	@BeforeEach
	void setUp() throws Exception {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8080;
	}

	@Test
	@Order(1)
	void testUserLogin() {
		Map<String, String> loginDetails = new HashMap<>();
		loginDetails.put("email", EMAIL_ADDRESS);
		loginDetails.put("password", "12345");
		
		Response response = given()
				.contentType("application/json")
				.accept("application/json")
				.body(loginDetails)
				.when()
				.post(CONTEXT_PATH + "/users/login")
				.then()
				.statusCode(200)
				.extract()
				.response();
		
		authorizationHeader = response.header("Authorization");
		userId = response.header("UserId");
		
		assertNotNull(authorizationHeader);
		assertNotNull(userId);	
	}
	
	@Test
	@Order(2)
	void testGetUserDetail() {
		Response response = given()
				.pathParam("id", userId)
				.header("Authorization", authorizationHeader)
				.contentType("application/json")
				.accept("application/json")
				.when()
//				.get(CONTEXT_PATH + "/users/" + userId)
				.get(CONTEXT_PATH + "/users/{id}")
				.then()
				.statusCode(200)
				.extract()
				.response();
		
		String userPublicId = response.jsonPath().getString("userId");
		String userEmail = response.jsonPath().getString("email");
		String firstName = response.jsonPath().getString("firstName");
		
		addresses = response.jsonPath().getList("addresses");
		addressId = addresses.get(0).get("addressId");

		assertNotNull(userPublicId);
		assertNotNull(userEmail);
		assertNotNull(firstName);
		assertNotNull(addressId);
		assertEquals(EMAIL_ADDRESS, userEmail);
		
		assertTrue(addresses.size() == 2);
		assertTrue(addressId.length() == 30);
	}
	
	@Test
	@Order(3)
	void testUpdateUserDetail() {
		Map<String, Object> userDetailsToBeUpdated = new HashMap<>();
		userDetailsToBeUpdated.put("firstName", "Lynn");
		userDetailsToBeUpdated.put("lastName", "H");
		userDetailsToBeUpdated.put("password", "999");
		
		Response response = given()
				.pathParam("id", userId)
				.header("Authorization", authorizationHeader)
				.contentType("application/json")
				.accept("application/json")
				.body(userDetailsToBeUpdated)
				.when()
				.put(CONTEXT_PATH + "/users/{id}")
				.then()
				.statusCode(200)
				.extract()
				.response();
		
		String userPublicId = response.jsonPath().getString("userId");
		String userEmail = response.jsonPath().getString("email");
		String firstName = response.jsonPath().getString("firstName");
		String lastName = response.jsonPath().getString("lastName");
		
		List<Map<String, String>> storedAddresses = response.jsonPath().getList("addresses");
		String storedAddressId = storedAddresses.get(0).get("addressId");
		
		assertNotNull(userPublicId);
		assertNotNull(userEmail);
		assertNotNull(firstName);
		assertNotNull(lastName);
		assertNotNull(storedAddressId);
		assertEquals(firstName, userDetailsToBeUpdated.get("firstName"));
		assertEquals(lastName, userDetailsToBeUpdated.get("lastName"));
		assertEquals(EMAIL_ADDRESS, userEmail);
		
		assertTrue(storedAddresses.size() == addresses.size());
		assertEquals(addressId, storedAddressId);
		
	}
	
	@Test
	@Order(4)
	void testDeleteUser() {
		Response response = given()
				.pathParam("id", userId)
				.header("Authorization", authorizationHeader)
				.contentType("application/json")
				.accept("application/json")
				.when()
				.delete(CONTEXT_PATH + "/users/{id}")
				.then()
				.statusCode(200)
				.extract()
				.response();
		String operationResult = response.jsonPath().getString("operationResult");
		assertNotNull(operationResult);
		assertEquals(operationResult, "SUCCESS");
	}
	
	
}
