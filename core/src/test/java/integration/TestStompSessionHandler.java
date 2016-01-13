package integration;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;

/**
 * Created by Krzysztof Wasiak on 13/01/2016.
 */
public class TestStompSessionHandler extends StompSessionHandlerAdapter {

    private StompSession stompSession;
    private StompHeaders connectedHeaders;

    public StompSession getStompSession() {
        return stompSession;
    }

    public StompHeaders getConnectedHeaders() {
        return connectedHeaders;
    }

    public StompHeaders getLatestHeaders() {
        return latestHeaders;
    }

    public Object getPayLoad() {
        return payLoad;
    }

    public Throwable getException() {
        return exception;
    }

    private StompHeaders latestHeaders;
    private Object payLoad;
    private Throwable exception;

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.stompSession = session;
        this.connectedHeaders = connectedHeaders;

        super.afterConnected(session, connectedHeaders);
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        this.payLoad = payload;
        super.handleFrame(headers, payload);
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        this.latestHeaders = headers;
        this.exception = exception;
        super.handleException(session, command, headers, payload, exception);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        this.exception = exception;
        super.handleTransportError(session, exception);
    }
}
