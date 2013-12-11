package com.demo.api.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Sean Shookman
 */

@XmlRootElement(name = "response")
public class RESTResponse {

    @XmlElement(name = "statusCode")
    private String statusCode;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
