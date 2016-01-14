package integration;

import org.springframework.messaging.simp.stomp.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Krzysztof Wasiak on 13/01/2016.
 */
public class TestStompSessionHandler extends StompSessionHandlerAdapter {

    private StompSession stompSession;
    public StompSession getStompSession() {
        return stompSession;
    }

    private StompHeaders connectedHeaders;
    public StompHeaders getConnectedHeaders() {
        return connectedHeaders;
    }

    private ArrayList<String> listOfReceivedMessages;
    public ArrayList<String> getListOfReceivedMessages() { return listOfReceivedMessages; }

    private StompHeaders latestHeaders;
    public StompHeaders getLatestHeaders() {
        return latestHeaders;
    }

    private Object payLoad;
    public Object getPayLoad() {
        return payLoad;
    }

    private Throwable exception;
    public Throwable getException() {
        return exception;
    }

    public TestStompSessionHandler() {
        listOfReceivedMessages = new ArrayList<>();
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.stompSession = session;
        this.connectedHeaders = connectedHeaders;

        stompSession.subscribe("/topic", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders stompHeaders) {
                return byte[].class;
            }

            @Override
            public void handleFrame(StompHeaders stompHeaders, Object receivedObject) {
                listOfReceivedMessages.add(new String((byte[]) receivedObject));
            }
        });

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

    public void subscribe(String targetChannel) {

        if (stompSession  == null) {
            throw new IllegalArgumentException("Stomp Session is not set. Please check the connection.");
        }
    }

    public void send(String targetChannel, Object objectToSend) {

        if (stompSession  == null) {
            throw new IllegalArgumentException("Stomp Session is not set. Please check the connection.");
        }

        stompSession.send(targetChannel, objectToSend);
    }
}
