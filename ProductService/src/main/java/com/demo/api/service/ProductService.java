package com.demo.api.service;

import com.demo.api.products.Product;
import com.demo.api.products.ProductsResponse;
import com.mongodb.*;
import com.sun.jersey.spi.resource.Singleton;

import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sean Shookman
 */
@Named
@Singleton
@Path("/products")
public class ProductService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getAllProducts() {

        DB db;

        try {

            db = establishConnection();
        } catch (UnknownHostException e) {

            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        final DBCollection productCollection = db.getCollection("products");
        final DBCursor cursor = productCollection.find();
        final List<Product> products = new ArrayList<Product>();

        while (cursor.hasNext()) {
            final DBObject current = cursor.next();
            final Product product = new Product();
            product.setBrand(current.get("brand").toString());
            product.setTitle(current.get("Title").toString());
            product.setPrice(new Double(current.get("Price").toString()));
            products.add(product);
        }

        final ProductsResponse productsResponse = new ProductsResponse();
        productsResponse.setProducts(products);

        return Response.ok(productsResponse).build();
    }

    private DB establishConnection() throws UnknownHostException {

        MongoClient mongoClient;

        mongoClient = new MongoClient("localhost" , 27017 );

        DB db = mongoClient.getDB("eGate");
        db.authenticate("admin", "admin".toCharArray());

        return db;
    }
}
