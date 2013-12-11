package com.demo.api.message;

import com.demo.api.common.RESTResponse;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author Sean Shookman
 */
public class MessageResponse extends RESTResponse {

    @XmlElement(name = "message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
