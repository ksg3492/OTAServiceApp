package com.twobeone.ota.network;

import com.twobeone.ota.data.model.FileDomain;
import com.twobeone.ota.data.model.LogDomain;
import com.twobeone.ota.data.model.TokenDomain;
import com.twobeone.ota.data.model.VersionDomain;

import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface RetrofitService {

    OkHttpClient client = new OkHttpClient().newBuilder()
            .followRedirects(false)
            .followSslRedirects(false)
            .build();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.1.20:8088/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    RetrofitService getInstance = retrofit.create(RetrofitService.class);

    @GET("checkVersion")
    Call<VersionDomain> getVersionInfo(@Query("deviceSerial") String deviceSerial);

    @GET("generateAuthToken")
    Call<TokenDomain> getAuthToken(@Query("deviceSerial") String deviceSerial);

    @POST("getFileInfo")
    Call<FileDomain> getFileInfo(@QueryMap Map<String, String> params);

//    @POST("getFileInfo")
//    Call<FileDomain> getFileInfo(@QueryMap Map<String, String> params);
    @GET("updateState")
    Call<LogDomain> updateLog(@QueryMap Map<String, String> params);

    @Streaming
    @POST("apkDownload")
    Call<ResponseBody> getFileDownload(@Header("Accept-Ranges") String accept, @Header("Range") String range, @QueryMap Map<String, String> params);
}
