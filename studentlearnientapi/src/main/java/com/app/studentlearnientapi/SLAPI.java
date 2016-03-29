package com.app.studentlearnientapi;

import com.app.studentlearnientapi.Constants.SLConstants;
import com.app.studentlearnientapi.Convertors.SLJacksonConvertor;
import retrofit.RestAdapter;

/**
 * Created by macbookpro on 19/12/2015.
 */
public class SLAPI {

    private static SLAPI instance;
    private SLWebInterface webInterface;
    private SLJacksonConvertor slJacksonConvertor;

    private SLAPI(){
        slJacksonConvertor = new SLJacksonConvertor();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setConverter(slJacksonConvertor)
                .setEndpoint(SLConstants.API_BASE_URL)
                .build();

        webInterface = restAdapter.create(SLWebInterface.class);
    }

    public static SLAPI getInstance(){
        if(instance == null){
            instance = new SLAPI();
        }
        return instance;
    }

    public SLWebInterface StudentWebInterface(){
        return webInterface;
    }
}
