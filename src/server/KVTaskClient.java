package server;

import exception.ClientException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class KVTaskClient {

    private final String API_TOKEN;

    private final HttpClient client;

    private final String basicUrl;

    public KVTaskClient(String urlToServer) {
        basicUrl = "http://" + urlToServer + ":" + KVServer.PORT;

        this.client = HttpClient.newHttpClient();
        URI url = URI.create(basicUrl + "/register");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new ClientException("Во время регистрации произошла ошибка.");
        }

        API_TOKEN = Objects.nonNull(response) ? response.body() : null;
    }

    public void put(String key, String json) {
        URI url = URI.create(basicUrl + "/save/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new ClientException("Во время сохранения произошла ошибка. Скорректируйте запрос.");
        }
    }

    public String load(String key) {
        URI url = URI.create(basicUrl + "/load/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException | InterruptedException e) {
            throw new ClientException("Во время загрузки произошла ошибка. Скорректируйте запрос.");
        }
    }

}
