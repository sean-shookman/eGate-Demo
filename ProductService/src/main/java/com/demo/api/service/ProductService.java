package com.demo.api.service;

import com.demo.api.common.MarshallUtil;
import com.demo.api.products.Product;
import com.demo.api.products.ProductResponse;
import com.demo.api.products.ProductsResponse;
import com.mongodb.*;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.resource.Singleton;
import org.bson.types.ObjectId;

import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
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
            product.setId(convertToString(current.get("_id")));
            product.setBrand(convertToString(current.get("brand")));
            product.setTitle(convertToString(current.get("Title")));
            product.setPrice(new Double(convertToString(current.get("Price"))));
            products.add(product);
        }

        final ProductsResponse productsResponse = new ProductsResponse();
        productsResponse.setProducts(products);

        return Response.ok(productsResponse).build();
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getProductByID(@PathParam("id") final String id) {

        DB db;

        try {
            db = establishConnection();
        } catch (UnknownHostException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        final DBCollection productCollection = db.getCollection("products");

        BasicDBObject query = new BasicDBObject();
        BasicDBObject allFields = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        final DBCursor cursor = productCollection.find(query, allFields);
        Product product = null;

        if (cursor.hasNext()) {
            final DBObject current = cursor.next();
            product = new Product();
            product.setId(convertToString(current.get("_id")));
            product.setBrand(convertToString(current.get("brand")));
            product.setTitle(convertToString(current.get("Title")));
            product.setPrice(new Double(convertToString(current.get("Price"))));
        }

        final ProductResponse productResponse = new ProductResponse();
        productResponse.setProduct(product);

        return Response.ok(productResponse).build();
    }

    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response createProduct(@Context final HttpContext requestContext) {

        final String contentType = requestContext.getRequest().getHeaderValue(HttpHeaders.CONTENT_TYPE);
        final String originalRequest = requestContext.getRequest().getEntity(String.class);

        Product request = null;
        DB db;

        try {
            request = MarshallUtil.unmarshall(Product.class, originalRequest, contentType);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            db = establishConnection();
        } catch (UnknownHostException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        final DBCollection productCollection = db.getCollection("products");
        BasicDBObject document = new BasicDBObject();
        document.put("brand", request.getBrand());
        document.put("Title", request.getTitle());
        document.put("Price", request.getPrice());
        WriteResult result = productCollection.insert(document);

        if (result.getError() != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result.getError()).build();
        }

        final ProductResponse productResponse = new ProductResponse();
        productResponse.setProduct(request);

        return Response.ok(request).build();
    }

    @PUT
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateProduct(@Context final HttpContext requestContext, @PathParam("id") final String id) {

        final String contentType = requestContext.getRequest().getHeaderValue(HttpHeaders.CONTENT_TYPE);
        final String originalRequest = requestContext.getRequest().getEntity(String.class);

        Product request = null;
        DB db;

        try {
            request = MarshallUtil.unmarshall(Product.class, originalRequest, contentType);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            db = establishConnection();
        } catch (UnknownHostException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        final DBCollection productCollection = db.getCollection("products");
        BasicDBObject document = new BasicDBObject();
        document.put("brand", request.getBrand());
        document.put("Title", request.getTitle());
        document.put("Price", request.getPrice());

        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        WriteResult result = productCollection.update(query, document);

        if (result.getError() != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result.getError()).build();
        }

        final ProductResponse productResponse = new ProductResponse();
        productResponse.setProduct(request);

        return Response.ok(request).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteProductByID(@PathParam("id") final String id) {

        DB db;

        try {
            db = establishConnection();
        } catch (UnknownHostException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        final DBCollection productCollection = db.getCollection("products");

        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("_id", new ObjectId(id));

        WriteResult result = productCollection.remove(searchQuery);

        if (result.getError() != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result.getError()).build();
        }

        return Response.ok(id).build();
    }

    private DB establishConnection() throws UnknownHostException {

        MongoClient mongoClient;

        mongoClient = new MongoClient("localhost" , 27017 );

        DB db = mongoClient.getDB("eGate");
        db.authenticate("admin", "admin".toCharArray());

        return db;
    }

    private String convertToString(Object object) {

        if (object != null) {
            return object.toString();
        }

        return null;
    }
}
