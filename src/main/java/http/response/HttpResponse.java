package http.response;

import http.HttpCookie;
import http.HttpSessionStore;
import http.HttpStatusCode;
import http.MediaType;
import http.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileIoUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

public class HttpResponse {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);

    private HttpResponseStatusLine httpResponseStatusLine;
    private HttpResponseHeader httpResponseHeader;
    private HttpResponseBody httpResponseBody;

    public static HttpResponse of(HttpRequest httpRequest) {
        HttpResponseStatusLine httpResponseStatusLine = new HttpResponseStatusLine(httpRequest.getHttpVersion());

        String sessionId = httpRequest.hasSession() ? httpRequest.getSessionId() : HttpSessionStore.getSession("").getId();

        HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
        httpResponseHeader.addCookie("SessionId=" + sessionId);

        return new HttpResponse(httpResponseStatusLine, httpResponseHeader);
    }

    public HttpResponse(HttpResponseStatusLine httpResponseStatusLine, HttpResponseHeader httpResponseHeader) {
        this.httpResponseStatusLine = httpResponseStatusLine;
        this.httpResponseHeader = httpResponseHeader;
    }

    public void setCookie(HttpCookie cookie) {
        httpResponseHeader.addField("Set-cookie", cookie.toString());
    }

    public void setStatusCode(HttpStatusCode httpStatusCode) {
        httpResponseStatusLine.setHttpStatusCode(httpStatusCode);
    }

    public void redirect(String uri) {
        String contentType = MediaType.getContentType(uri);
        httpResponseStatusLine.setHttpStatusCode(HttpStatusCode.FOUND);
        httpResponseHeader.addField("Content-Type", contentType + ";charset=utf-8");
        httpResponseHeader.setLocation(uri);
    }

    public void forward(String uri) throws IOException, URISyntaxException {
        String contentType = MediaType.getContentType(uri);

        httpResponseStatusLine.setHttpStatusCode(HttpStatusCode.OK);

        this.httpResponseBody = new HttpResponseBody(FileIoUtils.loadFileFromClasspath(MediaType.getFullPath(uri)));

        logger.debug("file full path: {}", MediaType.getFullPath(uri));
        httpResponseHeader.addField("Content-Type", contentType + ";charset=utf-8");
        httpResponseHeader.addField("Content-Length", String.valueOf(httpResponseBody.getBodyLength()));
    }

    public void setHttpResponseBody(HttpResponseBody httpResponseBody) {
        this.httpResponseBody = httpResponseBody;
    }

    public void writeResponse(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeBytes(httpResponseStatusLine.toString());
        dataOutputStream.writeBytes(httpResponseHeader.toString());

        if (httpResponseBody != null) {
            dataOutputStream.write(this.httpResponseBody.getBody(), 0, this.httpResponseBody.getBodyLength());
        }

        dataOutputStream.flush();
    }
}
