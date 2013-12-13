package com.demo.api.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Sean Shookman
 */

@XmlRootElement(name = "response")
public class RESTResponse {

    private String statusCode;

    @XmlElement(name = "statusCode")
    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
