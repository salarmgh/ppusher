package me.salarmgh.ppusher;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectReader;
import okhttp3.*;

import java.io.IOException;
//import java.util.List;

public class Ppusher {
    private static final MediaType MEDIA_TYPE_X_FORM
            = MediaType.parse("application/x-www-form-urlencoded");
    private final OkHttpClient client = new OkHttpClient();

    private String pusherURL;
    private String metric;
    private String program;

    public String getPusherURL() { return pusherURL; }

    public String getMetric() { return metric; }

    public String getProgram() { return program; }

    public Ppusher(String pusherURL, String metric,
                   String program) {
        this.pusherURL = pusherURL;
        this.metric = metric;
        this.program = program;
    }

    public void sendMetrics(String value) throws IOException {
        String postBody = String.format("%s{name=\"%s\"} %s\n", this.metric, this.program, value);
        Request request = new Request.Builder()
                .url(String.format("%s/metrics/job/%s_%s", this.pusherURL, this.metric, this.program))
                .post(RequestBody.create(MEDIA_TYPE_X_FORM, postBody))
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
    }

//    public long getMetrics() throws IOException {
//        long value = 0;
//        ObjectMapper om = new ObjectMapper();
//        Request request = new Request.Builder()
//                .url(String.format("%s/api/v1/query?query=%s", this.pusherURL, this.metric))
//                .build();
//        Response response = client.newCall(request).execute();
//        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//
//        JsonNode json = om.readTree(response.body().string());
//        for (JsonNode product : json.get("data").get("result")) {
//            if (this.program.equals(product.get("metric").get("name").asText())) {
//                ObjectReader reader = om.readerFor(new TypeReference<List<Long>>() {});
//                List<Long> values = reader.readValue(product.get("value"));
//                value = values.get(1);
//                break;
//            }
//        }
//        return value;
//    }
}