package integration;

import com.tjazi.web.websocketgateway.core.messages.ChatMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.SimpleMessageConverter;
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
import java.util.UUID;

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
        String subscriptionChannel = "/topic";
        String sampleMessage = "Sample message " + UUID.randomUUID().toString();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(sampleMessage);

        TestStompSessionHandler sessionHandler1 = new TestStompSessionHandler();
        this.createAndConnectWebSocketClient(sessionHandler1);

        TestStompSessionHandler sessionHandler2 = new TestStompSessionHandler();
        this.createAndConnectWebSocketClient(sessionHandler2);

        sessionHandler1.subscribe(subscriptionChannel);
        sessionHandler2.subscribe(subscriptionChannel);

        sessionHandler1.send(targetReceiver, chatMessage);

        Thread.sleep(2000);

        List<ChatMessage> receivedMessagesChannel1 = sessionHandler1.getListOfReceivedMessages();
        List<ChatMessage> receivedMessagesChannel2 = sessionHandler2.getListOfReceivedMessages();

        // validation - message should come to both channels
        assertEquals(1, receivedMessagesChannel1.size());
        assertEquals(sampleMessage, receivedMessagesChannel1.get(0).getContent());

        assertEquals(1, receivedMessagesChannel2.size());
        assertEquals(sampleMessage, receivedMessagesChannel2.get(0).getContent());
    }

    private WebSocketClient createAndConnectWebSocketClient(StompSessionHandlerAdapter sessionHandler)
            throws InterruptedException {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient webSocketStompClient = new WebSocketStompClient(webSocketClient);
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

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
