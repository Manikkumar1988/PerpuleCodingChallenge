package com.perpule.remote;

import com.perpule.data.RootObject;
import retrofit2.Call;
import retrofit2.http.GET;

public interface PerpuleApi {

    @GET("/bins/mxcsl") Call<RootObject> getFeed();
}