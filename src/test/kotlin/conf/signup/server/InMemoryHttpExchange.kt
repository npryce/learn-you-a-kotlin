package conf.signup.server

import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpContext
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpPrincipal
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.URI

class InMemoryHttpExchange(
    private val requestMethod: String,
    private val requestUri: URI,
    private val requestHeaders: Headers,
    private val requestBody: ByteArrayInputStream
) : HttpExchange() {
    constructor(
        requestMethod: String,
        requestUri: String,
        requestHeaders: Headers = noHeaders(),
        requestBody: ByteArrayInputStream = noBody()
    ) : this(requestMethod, URI.create(requestUri), requestHeaders, requestBody)
    
    private var responseCode = 0
    private val responseHeaders = Headers()
    private var expectedResponseBodySize: Long = 0
    private val responseBody: ByteArrayOutputStream = object : ByteArrayOutputStream() {
        override fun close() {
            super.close()
            checkResponseBodySize()
        }
    }
    
    private fun checkResponseBodySize() {
        check(!(expectedResponseBodySize > 0 && responseBody.size().toLong() != expectedResponseBodySize)) {
            "incorrect response body size sent: " +
                "expected " + expectedResponseBodySize + ", was " + responseBody.size()
        }
    }
    
    override fun getRequestHeaders(): Headers = requestHeaders
    override fun getResponseHeaders(): Headers = responseHeaders
    override fun getRequestURI(): URI = requestUri
    override fun getProtocol(): String = requestUri.scheme
    override fun getRequestMethod(): String = requestMethod
    
    override fun getResponseCode(): Int = responseCode
    
    override fun getRequestBody(): ByteArrayInputStream = requestBody
    
    override fun getHttpContext(): HttpContext {
        throw UnsupportedOperationException()
    }
    
    override fun close() {
        requestBody.close()
        responseBody.close()
    }
    
    override fun getResponseBody(): ByteArrayOutputStream {
        check(responseCode != 0) { "response headers have not been sent" }
        return responseBody
    }
    
    override fun sendResponseHeaders(rCode: Int, responseLength: Long) {
        responseCode = rCode
        expectedResponseBodySize = responseLength
    }
    
    override fun getRemoteAddress(): InetSocketAddress {
        throw UnsupportedOperationException()
    }
    
    override fun getLocalAddress(): InetSocketAddress {
        throw UnsupportedOperationException()
    }
    
    override fun getAttribute(name: String): Any {
        throw UnsupportedOperationException()
    }
    
    override fun setAttribute(name: String, value: Any) {
        throw UnsupportedOperationException()
    }
    
    override fun setStreams(i: InputStream, o: OutputStream) {
        throw UnsupportedOperationException()
    }
    
    override fun getPrincipal(): HttpPrincipal {
        throw UnsupportedOperationException()
    }
}

private fun noHeaders(): Headers =
    Headers()

private fun noBody(): ByteArrayInputStream =
    ByteArrayInputStream(ByteArray(0))

