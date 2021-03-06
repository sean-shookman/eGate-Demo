package com.demo.api.products;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Sean Shookman
 */
@XmlRootElement(name = "request")
public class ProductRequest {

    private Product product;

    @XmlElement(name = "product")
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
