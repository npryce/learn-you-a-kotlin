package learnyouakotlin.part4;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class InMemoryHttpExchange extends HttpExchange {
    private final String requestMethod;
    private final URI requestUri;
    private final Headers requestHeaders;
    private final ByteArrayInputStream requestBody;
    private int responseCode = 0;
    private final Headers responseHeaders = new Headers();
    private long expectedResponseBodySize = 0;
    private final ByteArrayOutputStream responseBody = new ByteArrayOutputStream() {
        @Override
        public void close() throws IOException {
            super.close();
            checkResponseBodySize();
        }
    };

    private void checkResponseBodySize() {
        if (expectedResponseBodySize > 0 && responseBody.size() != expectedResponseBodySize) {
            throw new IllegalStateException("incorrect response body size sent: " +
                "expected " + expectedResponseBodySize + ", was " + responseBody.size());
        }
    }

    public InMemoryHttpExchange(String requestMethod, URI requestUri, Headers requestHeaders, ByteArrayInputStream requestBody) {
        this.requestMethod = requestMethod;
        this.requestUri = requestUri;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public InMemoryHttpExchange(String requestMethod, String requestUri) {
        this(requestMethod, requestUri, noHeaders(), noBody());
    }

    public InMemoryHttpExchange(String requestMethod, String requestUri, Headers requestHeaders, ByteArrayInputStream requestBody) {
        this(requestMethod, URI.create(requestUri), requestHeaders, requestBody);
    }

    public static Headers noHeaders() {
        return new Headers();
    }

    public static ByteArrayInputStream noBody() {
        return new ByteArrayInputStream(new byte[0]);
    }

    public static ByteArrayInputStream utf8Body(String textBody) {
        return new ByteArrayInputStream(textBody.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Headers getRequestHeaders() {
        return requestHeaders;
    }

    @Override
    public Headers getResponseHeaders() {
        return responseHeaders;
    }

    @Override
    public URI getRequestURI() {
        return requestUri;
    }

    @Override
    public String getRequestMethod() {
        return requestMethod;
    }

    @Override
    public HttpContext getHttpContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        try {
            requestBody.close();
            responseBody.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public ByteArrayInputStream getRequestBody() {
        return requestBody;
    }

    @Override
    public ByteArrayOutputStream getResponseBody() {
        if (responseCode == 0) {
            throw new IllegalStateException("response headers have not been sent");
        }
        return responseBody;
    }

    @Override
    public void sendResponseHeaders(int rCode, long responseLength) throws IOException {
        responseCode = rCode;
        expectedResponseBodySize = responseLength;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getResponseCode() {
        return responseCode;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProtocol() {
        return requestUri.getScheme();
    }

    @Override
    public Object getAttribute(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute(String name, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStreams(InputStream i, OutputStream o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpPrincipal getPrincipal() {
        throw new UnsupportedOperationException();
    }
}
