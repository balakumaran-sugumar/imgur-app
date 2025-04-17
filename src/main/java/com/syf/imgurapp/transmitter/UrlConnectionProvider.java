package com.syf.imgurapp.transmitter;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public interface UrlConnectionProvider {
    InputStream openStream(String url) throws IOException;
}

