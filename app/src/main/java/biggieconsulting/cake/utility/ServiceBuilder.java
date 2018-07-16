package biggieconsulting.cake.utility;

import com.google.gson.GsonBuilder;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceBuilder {


    private static OauthToken oauthToken;

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(Constant.API_BASE_URL) // Server Url설정
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create()); // Json to Object

    // oauth token
    public static <S> S basicService(Class<S> serviceClass) {
        return builder.client(
                new OkHttpClient.Builder().addInterceptor(chain -> chain.proceed(requestBuild(chain.request(),
                        OauthToken.basic()).build()))
                        .build())
                .build().create(serviceClass);
    }

    // oauth access token
    public static <S> S createService(Class<S> serviceClass) {
        return builder.client(
                // why x set : o add ??????
                new OkHttpClient.Builder().addInterceptor(chain -> {
                    Request original = chain.request();
                    Response response = chain.proceed(requestBuild(original, oauthToken.bearer()).build());
                    if (response.code() == 401) {
                        Request newRequest = requestBuild(
                                new Request.Builder()
                                        .url(String.format("%s/oauth/token?grant_type=refresh_token&refresh_token=%s", Constant.API_BASE_URL, oauthToken.getRefreshToken()))
                                        .method("POST", RequestBody.create(MediaType.parse("application/json"), new byte[0]))
                                        .build(), oauthToken.bearer()).build(); // create simple requestBuilder

                        Response newResponse = chain.proceed(newRequest);

                        if (newResponse.code() == 200) {
                            assert newResponse.body() != null;
                            oauthToken = new GsonBuilder().create().fromJson(newResponse.body().string(), OauthToken.class);
                            response = chain.proceed(requestBuild(original, oauthToken.bearer()).build());
                        }

                        // else {}
                    }

                    return response;
                }).build()
        ).build()
                .create(serviceClass);
    }

    // Header / header insert in retrofit request builder
    private static Request.Builder requestBuild(Request request, String auth) {
        return request.newBuilder()
                .header("Accept", "application/json")
                .header("Authorization", auth)
                .method(request.method(), request.body());
    }
}