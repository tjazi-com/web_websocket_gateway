package com.tjazi.web.websocketgateway.core.websocketcontroller;

import com.tjazi.web.websocketgateway.core.messages.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * Created by Krzysztof Wasiak on 08/01/2016.
 */

@Controller
public class WebSocketController {

    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/messages")
    public void handleChatMessage(Message<Object> message, @Payload ChatMessage chatMessage) {

        log.debug("[WebSocketController] Received message: " + chatMessage.getContent());

        log.debug("[WebSocketController] Sending message to all subscribers...");
        messagingTemplate.convertAndSend("/topic", chatMessage);
    }
}
