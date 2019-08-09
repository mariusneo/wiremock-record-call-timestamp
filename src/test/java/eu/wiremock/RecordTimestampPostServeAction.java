package eu.wiremock;

import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.PostServeAction;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RecordTimestampPostServeAction extends PostServeAction {

	private Map<RequestId, List<Instant>> requestTimestampsMap = new HashMap<>();


	@Override
	public void doAction(ServeEvent serveEvent,
			Admin admin,
			Parameters parameters
	) {

		var recordedTimestamps = requestTimestampsMap
				.computeIfAbsent(new RequestId(serveEvent.getRequest().getUrl(), serveEvent.getRequest().getMethod()),
						key -> new ArrayList<>());
		recordedTimestamps.add(Instant.now());

	}

	@Override
	public String getName() {
		return "record-timestamp-post-serve-action";
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
