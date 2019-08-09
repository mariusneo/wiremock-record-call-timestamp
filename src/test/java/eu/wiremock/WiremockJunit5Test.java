package eu.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.RestAssured.given;

public class WiremockJunit5Test {

	WireMockServer wireMockServer;

	RecordTimestampTransformer recordTimestampTransformer;

	RecordTimestampPostServeAction recordTimestampPostServeAction;

	@BeforeEach
	public void setup() {
		recordTimestampPostServeAction = new RecordTimestampPostServeAction();
		recordTimestampTransformer = new RecordTimestampTransformer();
		var configuration = new WireMockConfiguration()
				.extensions(recordTimestampTransformer, recordTimestampPostServeAction)
				.port(8090);
		wireMockServer = new WireMockServer(configuration);
		wireMockServer.start();
		setupStub();
	}

	@AfterEach
	public void teardown() {
		wireMockServer.stop();
	}

	public void setupStub() {
		wireMockServer.stubFor(get(urlEqualTo("/an/endpoint"))
				.withPostServeAction(recordTimestampPostServeAction.getName(), Parameters.empty())
				.willReturn(aResponse().withHeader("Content-Type", "text/plain")
						.withTransformers(recordTimestampTransformer.getName())
						.withStatus(200)
						.withBodyFile("json/glossary.json")));
	}

	@Test
	public void testStatusCodePositive() {
		given().
				when().
				get("http://localhost:8090/an/endpoint").
				then().
				assertThat().statusCode(200);
	}

	@Test
	public void testStatusCodeNegative() {
		given().
				when().
				get("http://localhost:8090/another/endpoint").
				then().
				assertThat().statusCode(404);
	}

	@Test
	public void testResponseContents() {
		Response response = given().when().get("http://localhost:8090/an/endpoint");
		String title = response.jsonPath().get("glossary.title");
		System.out.println(title);
		Assert.assertEquals("example glossary", title);
	}


	@Test
	public void testRecordTimestampsViaTrnsformer() throws Exception {
		given().when().get("http://localhost:8090/an/endpoint");
		Thread.sleep(1000);
		given().when().get("http://localhost:8090/an/endpoint");
		Thread.sleep(2000);
		given().when().get("http://localhost:8090/an/endpoint");

		var url = "/an/endpoint";
		System.out.println("Recorded timestamps for the URL: " + url);
		var recordedTimestamps = recordTimestampTransformer
				.getRecordedTimestamps(url, RequestMethod.GET);
		recordedTimestamps.forEach(System.out::println);
	}

	@Test
	public void testRecordTimestampsViaPostServeAction() throws Exception {
		given().when().get("http://localhost:8090/an/endpoint");
		Thread.sleep(1000);
		given().when().get("http://localhost:8090/an/endpoint");
		Thread.sleep(2000);
		given().when().get("http://localhost:8090/an/endpoint");

		var url = "/an/endpoint";
		System.out.println("Recorded timestamps for the URL: " + url);
		var recordedTimestamps = recordTimestampPostServeAction
				.getRecordedTimestamps(url, RequestMethod.GET);
		recordedTimestamps.forEach(System.out::println);
	}
}