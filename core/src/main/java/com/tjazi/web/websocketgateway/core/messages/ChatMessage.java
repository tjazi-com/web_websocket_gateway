package com.tjazi.web.websocketgateway.core.messages;

/**
 * Created by Krzysztof Wasiak on 08/01/2016.
 */

/**
 * Message payload exchanged between WebSocket backend and the web-browser
 */
public class ChatMessage {

    /**
     * This is content of the message.
     */
    public String content;

    /**
     * ID of the sender.
     */
    public String senderId;

    /**
     * This is token, which can be checked. If Authorization token is invalid or inactive,
     * the processing will stop with error.
     */
    public String senderAuthorizationToken;

    /**
     * ID of the receiver. This could be ID of chatroom or individual receiver.
     */
    public String receiverId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderAuthorizationToken() {
        return senderAuthorizationToken;
    }

    public void setSenderAuthorizationToken(String senderAuthorizationToken) {
        this.senderAuthorizationToken = senderAuthorizationToken;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}
