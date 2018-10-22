package me.salarmgh.ppusher;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Ppusher {
    public String getPusherURL() {
        return pusherURL;
    }

    public String getMetric() {
        return metric;
    }

    public String getProgram() {
        return program;
    }

    public long getValue() {
        return value;
    }

    private String pusherURL;
    private String metric;
    private String program;
    private long value;

    private static final MediaType MEDIA_TYPE_X_FORM
            = MediaType.parse("application/x-www-form-urlencoded");
    private final OkHttpClient client = new OkHttpClient();

    public Ppusher(String pusherURL, String metric,
                   String program, int value) {
        this.pusherURL = pusherURL;
        this.metric = metric;
        this.program = program;
        this.value = value;
    }

    public void sendMetrics() throws IOException {
        String postBody = String.format("%s{name=\"%s\"} %d\n", this.metric, this.program, this.value);
        Request request = new Request.Builder()
                .url(String.format("%s/metrics/job/%s_%s/", this.pusherURL, this.metric, this.program))
                .post(RequestBody.create(MEDIA_TYPE_X_FORM, postBody))
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
    }

    public List<Long> getMetrics() throws IOException {
        ObjectMapper om = new ObjectMapper();
        List<Long> values = new ArrayList<Long>();
        Request request = new Request.Builder()
                .url(String.format("%s/api/v1/query?query=%s/", this.pusherURL, this.metric))
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        JsonNode json = om.readTree(response.body().string());
        for (JsonNode product : json) {
            ObjectReader reader = om.readerFor(new TypeReference<List<Long>>() {
            });
            values = reader.readValue(product.get("value"));
            }
        return values;
    }
}