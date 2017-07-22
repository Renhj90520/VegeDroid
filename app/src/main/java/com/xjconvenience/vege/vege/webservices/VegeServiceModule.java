package com.xjconvenience.vege.vege.webservices;

import com.xjconvenience.vege.vege.AppScope;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by Ren Haojie on 2017/7/19.
 */
@Module
public class VegeServiceModule {
    @AppScope
    @Provides
    VegeServices provideVegeServices(Retrofit retrofit) {
        return retrofit.create(VegeServices.class);
    }
}
