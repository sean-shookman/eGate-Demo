package com.demo.api.message;

import com.demo.api.common.RESTResponse;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Sean Shookman
 */
@XmlRootElement(name = "response")
public class MessageResponse extends RESTResponse {

    private String message;

    @XmlElement(name = "message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
