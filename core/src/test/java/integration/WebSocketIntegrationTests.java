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
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

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

    private String getTargetUrl() {
        return "ws://" + serverAddress + ":" + serverTcpPortNumber + "/messages";
    }
}
