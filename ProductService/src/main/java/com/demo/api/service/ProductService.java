package com.demo.api.service;

import com.demo.api.common.MarshallUtil;
import com.demo.api.products.Product;
import com.demo.api.products.ProductResponse;
import com.demo.api.products.ProductsResponse;
import com.demo.api.service.db.DBKeys;
import com.demo.api.service.request.Parameters;
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
    public Response getAllProducts(@QueryParam(Parameters.SORT_FIELD) @DefaultValue(Parameters.SORT_FIELD_DEFAULT) final String sortField,
                                   @QueryParam(Parameters.SORT_ORDER) @DefaultValue(Parameters.SORT_ORDER_DEFAULT) final String sortOrder,
                                   @QueryParam(Parameters.BRAND) @DefaultValue(Parameters.BRAND_DEFAULT) final String brandSearch,
                                   @QueryParam(Parameters.TITLE) @DefaultValue(Parameters.TITLE_DEFAULT) final String titleSearch,
                                   @QueryParam(Parameters.PRICE) @DefaultValue(Parameters.PRICE_DEFAULT) final String priceSearch) {

        DB db;

        try {
            db = establishConnection();
        } catch (UnknownHostException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        final DBCollection productCollection = db.getCollection(DBKeys.COLLECTION_PRODUCTS);

        final BasicDBObject sortObject = new BasicDBObject();
        sortObject.put(convertToSortField(sortField), convertToSortID(sortOrder));

        BasicDBObject searchObject = buildSearchObject(brandSearch, titleSearch, priceSearch);

        final DBCursor cursor = productCollection.find(searchObject).sort(sortObject);
        final List<Product> products = new ArrayList<Product>();

        while (cursor.hasNext()) {
            final DBObject current = cursor.next();
            final Product product = new Product();
            product.setId(convertToString(current.get(DBKeys.ID)));
            product.setBrand(convertToString(current.get(DBKeys.BRAND)));
            product.setTitle(convertToString(current.get(DBKeys.TITLE)));
            product.setPrice(convertToDouble(current.get(DBKeys.PRICE)));
            products.add(product);
        }

        final ProductsResponse productsResponse = new ProductsResponse();
        productsResponse.setProducts(products);

        return Response.ok(productsResponse).build();
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getProductByID(@PathParam(Parameters.ID) final String id) {

        DB db;

        try {
            db = establishConnection();
        } catch (UnknownHostException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        final DBCollection productCollection = db.getCollection(DBKeys.COLLECTION_PRODUCTS);

        BasicDBObject query = new BasicDBObject();
        BasicDBObject allFields = new BasicDBObject();
        query.put(DBKeys.ID, new ObjectId(id));

        final DBCursor cursor = productCollection.find(query, allFields);
        Product product = null;

        if (cursor.hasNext()) {
            final DBObject current = cursor.next();
            product = new Product();
            product.setId(convertToString(current.get(DBKeys.ID)));
            product.setBrand(convertToString(current.get(DBKeys.BRAND)));
            product.setTitle(convertToString(current.get(DBKeys.TITLE)));
            product.setPrice(convertToDouble(current.get(DBKeys.PRICE)));
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

        Product request;
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

        final DBCollection productCollection = db.getCollection(DBKeys.COLLECTION_PRODUCTS);
        BasicDBObject document = new BasicDBObject();
        document.put(DBKeys.BRAND, request.getBrand());
        document.put(DBKeys.TITLE, request.getTitle());
        document.put(DBKeys.PRICE, request.getPrice());
        WriteResult result = productCollection.insert(document);

        if (result.getError() != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result.getError()).build();
        }

        return Response.ok().build();
    }

    @PUT
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateProduct(@Context final HttpContext requestContext, @PathParam("id") final String id) {

        final String contentType = requestContext.getRequest().getHeaderValue(HttpHeaders.CONTENT_TYPE);
        final String originalRequest = requestContext.getRequest().getEntity(String.class);

        Product request;
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

        final DBCollection productCollection = db.getCollection(DBKeys.COLLECTION_PRODUCTS);
        BasicDBObject document = new BasicDBObject();
        document.put(DBKeys.BRAND, request.getBrand());
        document.put(DBKeys.TITLE, request.getTitle());
        document.put(DBKeys.PRICE, request.getPrice());

        BasicDBObject query = new BasicDBObject();
        query.put(DBKeys.ID, new ObjectId(id));

        WriteResult result = productCollection.update(query, document);

        if (result.getError() != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result.getError()).build();
        }

        return Response.ok().build();
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

        final DBCollection productCollection = db.getCollection(DBKeys.COLLECTION_PRODUCTS);

        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put(DBKeys.ID, new ObjectId(id));

        WriteResult result = productCollection.remove(searchQuery);

        if (result.getError() != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result.getError()).build();
        }

        return Response.ok().build();
    }

    private DB establishConnection() throws UnknownHostException {

        MongoClient mongoClient;

        mongoClient = new MongoClient(DBKeys.SERVER_HOST , DBKeys.SERVER_PORT );

        DB db = mongoClient.getDB(DBKeys.DB_EGATE);
        db.authenticate(DBKeys.DB_EGATE_USER, DBKeys.DB_EGATE_PASS.toCharArray());

        return db;
    }

    private BasicDBObject buildSearchObject(String brandSearch, String titleSearch, String priceSearch) {

        BasicDBObject searchObject = new BasicDBObject();

        if (!brandSearch.isEmpty()) {
            searchObject.put(DBKeys.BRAND, brandSearch);
        }
        if (!titleSearch.isEmpty()) {
            searchObject.put(DBKeys.TITLE, titleSearch);
        }
        if (!priceSearch.isEmpty()) {
            searchObject.put(DBKeys.PRICE, new Double(priceSearch));
        }

        return searchObject;
    }

    private String convertToString(Object object) {

        if (object != null) {
            return object.toString();
        }

        return null;
    }

    private Double convertToDouble(Object object) {

        if (object != null) {
            return new Double(object.toString());
        }

        return null;
    }

    private int convertToSortID(String sortOrder) {

        if (sortOrder.equalsIgnoreCase("ascending")) {
            return 1;
        }
        else if (sortOrder.equalsIgnoreCase("descending")) {
            return -1;
        }
        else {
            return 0;
        }
    }

    private String convertToSortField(String sortField) {

        sortField = sortField.toLowerCase();
        if(sortField.equalsIgnoreCase("id")) {
            sortField = "_" + sortField;
        }

        return sortField;
    }
}
