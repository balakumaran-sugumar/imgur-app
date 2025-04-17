package com.syf.imgurapp.transmitter;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
@Component
public class DefaultUrlConnectionProvider implements UrlConnectionProvider {
    @Override
    public InputStream openStream(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        return connection.getInputStream();
    }
}
