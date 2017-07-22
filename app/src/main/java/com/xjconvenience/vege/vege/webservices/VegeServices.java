package com.xjconvenience.vege.vege.webservices;

import com.xjconvenience.vege.vege.models.ItemsResult;
import com.xjconvenience.vege.vege.models.LoginWrapper;
import com.xjconvenience.vege.vege.models.Order;
import com.xjconvenience.vege.vege.models.Result;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Ren Haojie on 2017/7/19.
 */
public interface VegeServices {
    @POST("/authorization/login")
    public Observable<Result<String>> login(@Body LoginWrapper wrapper);

    @GET("/api/orders")
    public Observable<ItemsResult<Order>> loadOrders(@Query("index") String index,
                                                     @Query("perPage") String perPage,
                                                     @Query("keyword") String keyword,
                                                     @Query("begin") String begin,
                                                     @Query("end") String end,
                                                     @Query("noshowRemove") String noshowRemove);
}
