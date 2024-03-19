package ru.ptrff.tracktag.api;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.ptrff.tracktag.utils.GsonStringConverterFactory;

public class MapsClient {
    private static final String BASE_URL = "https://maps.rtuitlab.dev/";

    private static Retrofit retrofit;
    private static OkHttpClient client;
    private static boolean runningWithAuth = false;

    public static Retrofit getClient(String authToken) {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(authToken))
                    .build();
        }
        if (retrofit == null || !runningWithAuth) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(new GsonStringConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .client(client)
                    .build();
            runningWithAuth = true;
        }
        return retrofit;
    }

    public static Retrofit getClient() {
        if (retrofit == null || runningWithAuth) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(new GsonStringConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
            runningWithAuth = false;
        }
        return retrofit;
    }
}
