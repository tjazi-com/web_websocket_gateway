package com.tjazi.web.websocketgateway.core.websocketcontroller;

import com.tjazi.web.websocketgateway.core.messages.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * Created by Krzysztof Wasiak on 08/01/2016.
 */

@Controller
public class WebSocketController {

    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);

    @MessageMapping("/messages")
    public void handleChatMessage(Message<Object> message, @Payload ChatMessage chatMessage) {

    }
}
