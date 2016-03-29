package com.app.studentlearnientapi.Convertors;

import com.app.studentlearnientapi.Constants.SLConstants;
import com.app.studentlearnientapi.SLAPI;
import com.app.studentlearnientapi.SLWebInterface;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.SimpleXMLConverter;

/**
 * Created by macbookpro on 26/12/2015.
 */
public class XMLConvertor {

    RestAdapter adapter;
    private static XMLConvertor instance;
    private SLWebInterface webInterface;

    private XMLConvertor() {
        this.webInterface = webInterface;
        adapter = new RestAdapter.Builder()
                .setClient(new OkClient(new OkHttpClient())).setEndpoint(SLConstants.API_BASE_URL)
                        .setConverter(new SimpleXMLConverter())
                        .build();
        webInterface = adapter.create(SLWebInterface.class);
    }

    public static XMLConvertor getInstance(){
        if(instance == null){
            instance = new XMLConvertor();
        }
        return instance;
    }

    public SLWebInterface StudentWebInterface(){
        return webInterface;
    }
}
