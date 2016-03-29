package com.app.studentlearnientapi.Convertors;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created by macbookpro on 22/12/2015.
 */
public class SLJacksonConvertor implements Converter {
    private ObjectMapper mapper = new ObjectMapper();

    @JsonSerialize
    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        //mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        try {
            return mapper.readValue(body.in(), javaType);
        } catch (IOException e) {
            throw new ConversionException(e);
        }
    }

    @Override
    public TypedOutput toBody(Object object) {
        try {
            String charset = "UTF-8";
            String json = mapper.writeValueAsString(object);
            return new JsonTypedOutput(json.getBytes(charset));
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private static class JsonTypedOutput implements TypedOutput {
        private final byte[] jsonBytes;

        JsonTypedOutput(byte[] jsonBytes) { this.jsonBytes = jsonBytes; }

        @Override public String fileName() { return null; }
        @Override public String mimeType() { return "application/json; charset=UTF-8"; }
        @Override public long length() { return jsonBytes.length; }
        @Override public void writeTo(OutputStream out) throws IOException { out.write(jsonBytes); }
    }

}
