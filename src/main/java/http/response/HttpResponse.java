package http.response;

import http.MediaType;

import java.io.IOException;
import java.net.URISyntaxException;

public class HttpResponse {
    private HttpStatusLine httpStatusLine;
    private HttpResponseHeader httpResponseHeader;
    private HttpResponseBody httpResponseBody;

    public HttpResponse() {
    }

    public HttpStatusLine getHttpStatusLine() {
        return httpStatusLine;
    }

    public HttpResponseHeader getHttpResponseHeader() {
        return httpResponseHeader;
    }

    public HttpResponseBody getHttpResponseBody() {
        return httpResponseBody;
    }

    public void sendRedirect(String uri) {
        this.httpStatusLine = new HttpStatusLine("HTTP/1.1 302 FOUND \r\n");
        this.httpResponseHeader = new HttpResponseHeader(
                "Content-Type: text/html;charset=utf-8\r\n"
                        + "Location: /" + uri + "\r\n");
    }

    public void send200Ok(String uri) throws IOException, URISyntaxException {
        String contentType = MediaType.getContentType(uri);

        this.httpStatusLine = new HttpStatusLine("HTTP/1.1 200 OK \r\n");
        this.httpResponseBody = new HttpResponseBody(uri);
        this.httpResponseHeader = new HttpResponseHeader(
                "Content-Type: " + contentType + ";charset=utf-8\r\n"
                        + "Content-Length: " + httpResponseBody.getBodyLength());
    }
}
