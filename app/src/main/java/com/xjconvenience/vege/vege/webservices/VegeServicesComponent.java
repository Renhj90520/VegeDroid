package com.xjconvenience.vege.vege.webservices;

import com.xjconvenience.vege.vege.AppScope;

import dagger.Component;

/**
 * Created by Ren Haojie on 2017/7/19.
 */

@AppScope
@Component(modules = VegeServiceModule.class, dependencies = HttpComponent.class)
public interface VegeServicesComponent {
    VegeServices mVegeServices();
}
