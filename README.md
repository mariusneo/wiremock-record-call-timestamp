Recording the timestamps of the calls made to a wiremock server
===============================================================


This is a proof of concept on how to record the timestamps
(or do some other custom actions) either when a response
is being served or after a response has been served by
[WireMock](http://wiremock.org/).


Two possibilities are provided in this PoC

```
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
	
```

```
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
```