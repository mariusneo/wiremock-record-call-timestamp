package eu.wiremock;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RecordTimestampTransformer extends ResponseDefinitionTransformer {

	public static final String NAME = "record-timestamp-transformer";

	private Map<RequestId, List<Instant>> requestTimestampsMap = new HashMap<>();

	@Override
	public ResponseDefinition transform(Request request, ResponseDefinition responseDefinition,
			FileSource files, Parameters parameters) {

		var recordedTimestamps = requestTimestampsMap
				.computeIfAbsent(new RequestId(request.getUrl(), request.getMethod()),
						key -> new ArrayList<>());
		recordedTimestamps.add(Instant.now());
		return responseDefinition;
	}

	@Override
	public String getName() {
		return NAME;
	}


	public List<Instant> getRecordedTimestamps(String url, RequestMethod requestMethod) {

		return requestTimestampsMap
				.getOrDefault(new RequestId(url, requestMethod), Collections.emptyList());
	}

	private static class RequestId {

		private String url;
		private RequestMethod method;

		public RequestId(String url, RequestMethod method) {
			this.url = url;
			this.method = method;
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			RequestId requestId = (RequestId) o;
			return Objects.equals(url, requestId.url) &&
					Objects.equals(method, requestId.method);
		}

		@Override
		public int hashCode() {
			return Objects.hash(url, method);
		}

		@Override
		public String toString() {
			return "RequestId{" +
					"url='" + url + '\'' +
					", method=" + method +
					'}';
		}
	}
}