package integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.print.DocFlavor;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Krzysztof Wasiak on 13/01/2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest
@SpringApplicationConfiguration(classes = {TestConfiguration.class})
public class WebSocketIntegrationTests {

    @Value("${server.port}")
    private int serverTcpPortNumber;

    @Value("${server.address}")
    private String serverAddress;

    private static final Logger log = LoggerFactory.getLogger(WebSocketIntegrationTests.class);

    /**
        [Test] Test connectivity to the target server
     */
    @Test
    public void setConnection_Test() throws InterruptedException {

        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient webSocketStompClient = new WebSocketStompClient(webSocketClient);
        webSocketStompClient.setMessageConverter(new StringMessageConverter());

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        webSocketStompClient.setTaskScheduler(taskScheduler);

        TestStompSessionHandler sessionHandler = new TestStompSessionHandler();
        webSocketStompClient.connect(this.getTargetUrl(), sessionHandler);

        Thread.sleep(2000);

        Throwable webSocketException = sessionHandler.getException();

        if (webSocketException !=null) {

            log.error(webSocketException.toString());
            assertTrue(false);
        }

        assertNotNull(sessionHandler.getStompSession());
    }

    @Test
    public void per2perMessage_Test() throws InterruptedException {

        String targetReceiver = "/app/messages";
        String subscriptionChannel = "/app/topic";

        TestStompSessionHandler sessionHandler1 = new TestStompSessionHandler();
        WebSocketClient webSocketClient1 = this.createAndConnectWebSocketClient(sessionHandler1);

        TestStompSessionHandler sessionHandler2 = new TestStompSessionHandler();
        WebSocketClient webSocketClient2 = this.createAndConnectWebSocketClient(sessionHandler2);

        sessionHandler1.subscribe(subscriptionChannel);
        sessionHandler2.subscribe(subscriptionChannel);

        sessionHandler1.send(targetReceiver, "Sample message");

        Thread.sleep(2000);

        List<String> receivedMessagesChannel1 = sessionHandler1.getListOfReceivedMessages();
        List<String> receivedMessagesChannel2 = sessionHandler1.getListOfReceivedMessages();

        // validation
        assertEquals(1, receivedMessagesChannel1.size());
        assertEquals(1, receivedMessagesChannel2.size());
    }

    private WebSocketClient createAndConnectWebSocketClient(StompSessionHandlerAdapter sessionHandler)
            throws InterruptedException {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient webSocketStompClient = new WebSocketStompClient(webSocketClient);
        webSocketStompClient.setMessageConverter(new StringMessageConverter());

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        webSocketStompClient.setTaskScheduler(taskScheduler);

        webSocketStompClient.connect(this.getTargetUrl(), sessionHandler);

        Thread.sleep(2000);

        return webSocketClient;
    }

    private String getTargetUrl() {
        return "ws://" + serverAddress + ":" + serverTcpPortNumber + "/messages";
    }
}
