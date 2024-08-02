package ca.cmpt213.demo;

import ca.cmpt213.controller.TokimonCardController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc //this sets up a MockMvc instance to perform web requests and assert responses in your tests without needed to start a full HTTP server
public class TokimonCardControllerTest {

	@Autowired
	private MockMvc mvc; //this is the main entry point for server-side Spring MVC test support

	@Autowired //this annotation is used to inject the MockMvc instance into the test class
	private TokimonCardController tokimonCardController;

	@BeforeEach //this method is run before each test method in the class, it is used to reset the json file before each test
	public void resetJsonFile() throws Exception {
		tokimonCardController.init();
	}

	//this test method sends a GET request to the /api/tokimon/all endpoint and expects a 200 OK status code in return
	@Test
	public void testGetAllTokimonCards() throws Exception {
		mvc.perform(get("/api/tokimon/all"))
				.andExpect(status().isOk()) //
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}


	//test to see if the contents of the json have the initial tokimon cards
	@Test
	public void testGetAllTokimonCardsContent() throws Exception {
		mvc.perform(get("/api/tokimon/all"))
				.andExpect(status().isOk()) //
				.andExpect(content().json("[" +
						"{\"tid\":1,\"name\":\"Tokimander\",\"elementType\":\"FIRE\", \"imageName\":\"charmander.png\",\"healthPoints\": 100, \"attackPoints\": 50}," +
						"{\"tid\":2,\"name\":\"Tokiurtle\",\"elementType\":\"WATER\", \"imageName\":\"squirtle.png\",\"healthPoints\": 80, \"attackPoints\": 60}," +
						"{\"tid\":3,\"name\":\"Tokisaur\",\"elementType\":\"GRASS\", \"imageName\":\"bulbasaur.png\",\"healthPoints\": 60, \"attackPoints\": 85}" +
						"]"));
	}

	//this test method sends a GET request to the /api/tokimon/1 endpoint and expects a 200 OK status code in return and the json object with the tid of 1
	@Test
	public void testGetTokimonCard() throws Exception {
		mvc.perform(get("/api/tokimon/1"))
				.andExpect(status().isOk())
				.andExpect(content().json("{\"tid\":1,\"name\":\"Tokimander\",\"elementType\":\"FIRE\", \"imageName\":\"charmander.png\",\"healthPoints\": 100, \"attackPoints\": 50}"));
	}

	//this test method sends a GET request to the /api/tokimon/4 endpoint and expects a 404 Not Found status code in return
	@Test
	public void testGetTokimonCardNotFound() throws Exception {
		mvc.perform(get("/api/tokimon/4"))
				.andExpect(status().isNotFound());
	}

	//this test method sends a POST request to the /api/tokimon/add endpoint and expects a 201 Created status code in return
	@Test
	public void testAddTokimonCard() throws Exception {
		String tokimonCardJson = "{\"name\":\"Tokitto\",\"elementType\":\"NORMAL\", \"imageName\":\"/resources/static/images/ditto.png\",\"healthPoints\": 150, \"attackPoints\": 20}";

		mvc.perform(post("/api/tokimon/add")
				.content(tokimonCardJson).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}

	@Test
	public void testAddTokimonCardInvalidElementType() throws Exception {
		mvc.perform(post("/api/tokimon/add")
				.param("name", "Tokichu")
				.param("elementType", "INVALID") //this is an invalid element type
				.param("imageName", "/resources/static/images/unown.png")
				.param("healthPoints", "100")
				.param("attackPoints", "50"))
				.andExpect(status().isBadRequest())
				//this line checks if the exception thrown is an instance of InvalidElementTypeException
				.andExpect(result -> assertEquals("", result.getResponse().getContentAsString()));
	}

	@Test
	public void testAddTokimonCardInvalidURL() throws Exception {
		mvc.perform(post("/api/tokimon/add")
				.param("name", "Tokichu")
				.param("elementType", "ELECTRIC")
				.param("imageName", "invalidURL") //this is an invalid URL
				.param("healthPoints", "100")
				.param("attackPoints", "50"))
				.andExpect(status().isBadRequest())
				//this line checks if the exception thrown is an instance of InvalidElementTypeException
				.andExpect(result -> assertEquals("", result.getResponse().getContentAsString()));

	}

	@Test
	public void testAddTokimonCardInvalidHP() throws Exception {
		mvc.perform(post("/api/tokimon/add")
				.param("name", "Tokichu")
				.param("elementType", "ELECTRIC")
				.param("imageName", "/resources/static/images/unown.png")
				.param("healthPoints", "0") //this is an invalid HP value
				.param("attackPoints", "50"))
				.andExpect(status().isBadRequest())
				//this line checks if the exception thrown is an instance of InvalidElementTypeException
				.andExpect(result -> assertEquals("", result.getResponse().getContentAsString()));
	}

	@Test
	public void testUploadFileInvalidType() throws Exception {
		Path tempFile2 = Files.createTempFile("testImage", ".txt");
		MockMultipartFile file = new MockMultipartFile(
				"file",
				tempFile2.getFileName().toString(),
				MediaType.TEXT_PLAIN_VALUE,
				"test image content".getBytes()
		);

		try {
			mvc.perform(multipart("/api/tokimon/uploadPhoto")
							.file(file))
					.andExpect(status().isBadRequest()) //this is a 404 Not Found status code
					.andExpect(content().string("Invalid file type. Only PNG and JPEG files are allowed"));
		} finally {
			Files.deleteIfExists(tempFile2);
		}
	}


	//test editing a tokimon card with a valid element type in the request body
	//this also verifies that the status code is 200 OK and the request body is in JSON format
	@Test
	public void testEditTokimonCard() throws Exception {
		String tokimonCardReqBody = "{\"tid\":1,\"name\":\"Tokimander\",\"elementType\":\"FIRE\"}";

		mvc.perform(put("/api/tokimon/edit/1")
						.content(tokimonCardReqBody)
						.contentType(MediaType.APPLICATION_JSON)) //
				.andExpect(status().isOk());
	}

	//test editing a tokimon card with an invalid element type in the request body
	@Test
	public void testEditTokimonCardInvalidElementType() throws Exception {
		String tokimonCardReqBody = "{\"tid\":1,\"name\":\"Tokimander\",\"elementType\":\"INVALID\"}";

		mvc.perform(put("/api/tokimon/edit/1")
						.content(tokimonCardReqBody)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	//test deleting a tokimon card with a valid tid
	@Test
	public void testDeleteTokimonCard() throws Exception {
		mvc.perform(delete("/api/tokimon/1"))
				.andExpect(status().isNoContent());
	}

	//test deleting a tokimon card with an invalid tid
	@Test
	public void testDeleteTokimonCardNotFound() throws Exception {
		mvc.perform(delete("/api/tokimon/4"))
				.andExpect(status().isNotFound());
	}





}
