package com.zeyo.android;

import com.google.gson.JsonObject;
import com.zeyo.android.model.Closet.ClosetDetailResponse;
import com.zeyo.android.model.Closet.ClosetResponse;
import com.zeyo.android.model.consumer.response.FindIdResponse;
import com.zeyo.android.model.MeasureItem.MeasureItemResponse;
import com.zeyo.android.model.subCategory.SubCategoryResponse;
import com.zeyo.android.model.consumer.response.UserInfo;
import com.zeyo.android.model.subCategory.SubCategoryResponse;

import org.json.JSONObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {
    String zeyo_api_key = "QVntgqTsu6jqt7hQSVpF7ZS8Tw==";
    String heronation_api_key = "66Gc6re4T1Prk5zsnKDsl5RaRVlU7J24VEU=";

    @Headers({"Accept: application/json","Content-Type: application/x-www-form-urlencoded","Authorization: zeyo-api-key " + zeyo_api_key})
    @POST("api/consumers/registry")
    @FormUrlEncoded
    Call<ResponseBody> saveConsumer(@Field("consumerId") String consumerId, @Field("email") String email, @Field("name") String name, @Field("password") String password, @Field("gender") String gender, @Field("termsAdvertisement") String termsAdvertisement, @Field("birthYear") String birthYear, @Field("birthMonth") String birthMonth, @Field("birthDay") String birthDay);


    @Headers({"Accept: application/json", "Content-Type: application/x-www-form-urlencoded", "heronation-api-login-key: " + heronation_api_key})
    @POST("oauth/token")
    @FormUrlEncoded
    Call<JsonObject> saveToken(@Header("Authorization") String credential, @Field("username") String username, @Field("password") String password, @Field("grant_type") String type);

    @POST("commons/temp/upload")
    @Multipart
    Call<String> uploadFile(@Header("Authorization") String token, @Part MultipartBody.Part File);

    @Headers({ "Content-Type: application/x-www-form-urlencoded" })
    @GET("api/consumers/me")
        //@FormUrlEncoded
    Call<UserInfo> saveInfo(@Header("Authorization") String token);

    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST("api/wardrobes")
    Call<JSONObject> addCloth(@Header("Authorization") String token, @Body RequestBody body);

    @Headers({"Authorization: zeyo-api-key " + zeyo_api_key})
    @GET("api/v2_categorys")
        //@FormUrlEncoded
    Call<List<SubCategoryResponse>> getCategory();

    @Headers({"Authorization: zeyo-api-key " + zeyo_api_key})
    @GET("api/measure_items/subcateorys/{id}")
        //@FormUrlEncoded
    Call<List<MeasureItemResponse>> getsubCategory(@Path("id") String id);


    @Headers({"Authorization: zeyo-api-key " + zeyo_api_key})
    @GET("api/consumers/findConsumerId")
    Call<List<FindIdResponse>> saveID(@Query("name") String name, @Query("gender") String gender, @Query("birthYear") String year,
                                      @Query("birthMonth") String month, @Query("birthDay") String day);

    @Headers({"Authorization: zeyo-api-key " + zeyo_api_key})
    @GET("api/consumers/findPassword")
    Call<String> savePW(@Query("email") String email, @Query("consumerId") String consumerId);

    @Headers({"Content-Type: application/json","Accept: application/json","Authorization: zeyo-api-key " + zeyo_api_key})
    @POST("api/board_user_qnas")
    Call<JsonObject> postQuestion(@Body RequestBody jsonObject);

    @GET("api/wardrobes?page=1&size=99&sort=id,desc")
    Call<ClosetResponse> saveClosetList(@Header("Authorization") String token);


    @Headers({"Content-Type: application/x-www-form-urlencoded", "heronation-api-login-key: " + heronation_api_key})
    @POST("api/oauths/naver/token")
    @FormUrlEncoded
    Call<JsonObject> saveTokenNaver(@Field("naverAccessToken") String naver_token);

    @Headers({"Content-Type: application/x-www-form-urlencoded", "heronation-api-login-key: " + heronation_api_key})
    @POST("api/oauths/google/token")
    @FormUrlEncoded
    Call<JsonObject> saveTokenGoogle(@Field("googleAccessToken") String google_token);

    @DELETE("api/wardrobes/{id}")
    Call<String> deleteCloset(@Header("Authorization") String token, @Path("id") String id);

    @GET("api/wardrobes/{id}")
    Call<ClosetDetailResponse> getClothDetail(@Header("Authorization") String token, @Path("id") String id);

    @Headers("heronation-api-login-key: " + heronation_api_key)
    @POST("api/oauths/kakao/token")
    @FormUrlEncoded
    Call<JsonObject> postKakaoLogin(@Field("kakaoAccessToken") String kakaoAccessToken, @Field("heronation-api-uniqId-key") String heronation_api_uniqId_key);

}

