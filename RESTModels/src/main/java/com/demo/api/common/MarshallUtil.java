package com.demo.api.common;

import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author Priya, Sean Shookman
 */
public class MarshallUtil {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final String CLASS_MISMATCH_ERROR = "The request object is not of the correct class.";

    public static <T> T unmarshall(Class clazz, String input, String contentType) throws Exception {

        if (contentType.contains(MediaType.APPLICATION_XML.toString())) {

            return MarshallUtil.<T>unmarshallXMLNew(clazz, input);
        }
        else if (contentType.contains(MediaType.APPLICATION_JSON.toString())) {

            return MarshallUtil.<T>unmarshallJSON(clazz, input);
        }
        else {

            return null;
        }
    }

    public static <T> T unmarshallXML(Class clazz, String xml) throws Exception {

        final JAXBContext context = JAXBContext.newInstance(clazz);
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final T bo = (T) unmarshaller.unmarshal(new StringReader(xml));

        return bo;
    }

    private static <T> T unmarshallXMLNew(Class clazz, String xml) throws JAXBException {

        T bo;

        final JAXBContext context = JAXBContext.newInstance(clazz);
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        bo = (T) unmarshaller.unmarshal(new StringReader(xml));

        if (!bo.getClass().equals(clazz)) {
            throw new JAXBException(CLASS_MISMATCH_ERROR);
        }

        return bo;
    }

    private static <T> T unmarshallJSON(Class clazz, String json) throws IOException, JAXBException {

        T bo = null;

        bo = (T) JSON_MAPPER.readValue(json, clazz);

        if (!bo.getClass().equals(clazz)) {
            throw new JAXBException(CLASS_MISMATCH_ERROR);
        }

        return bo;
    }
}
