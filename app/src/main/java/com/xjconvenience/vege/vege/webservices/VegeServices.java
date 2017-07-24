package com.xjconvenience.vege.vege.webservices;

import com.xjconvenience.vege.vege.models.ItemsResult;
import com.xjconvenience.vege.vege.models.LoginWrapper;
import com.xjconvenience.vege.vege.models.Order;
import com.xjconvenience.vege.vege.models.PatchDoc;
import com.xjconvenience.vege.vege.models.Result;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Ren Haojie on 2017/7/19.
 */
public interface VegeServices {
    @POST("/authorization/login")
    public Observable<Result<String>> login(@Body LoginWrapper wrapper);

    @GET("/api/orders")
    public Observable<Result<ItemsResult<Order>>> loadOrders(@Query("index") String index,
                                                             @Query("perPage") String perPage,
                                                             @Query("keyword") String keyword,
                                                             @Query("state") String state,
                                                             @Query("begin") String begin,
                                                             @Query("end") String end,
                                                             @Query("noshowRemove") String noshowRemove);

    @PATCH("api/orders/{id}")
    public Observable<Result<Boolean>> updateOrder(@Path("id") int id, @Body List<PatchDoc> order);
}
