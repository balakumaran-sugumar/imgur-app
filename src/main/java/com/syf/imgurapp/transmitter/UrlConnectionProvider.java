package com.syf.imgurapp.transmitter;

import java.io.IOException;
import java.io.InputStream;

public interface UrlConnectionProvider {
    InputStream openStream(String url) throws IOException;
}

