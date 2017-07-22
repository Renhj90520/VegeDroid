package com.xjconvenience.vege.vege.webservices;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

/**
 * Created by Ren Haojie on 2017/7/19.
 */

@Singleton
@Component(modules = HttpModule.class)
public interface HttpComponent {
    Retrofit retorfit();
}
