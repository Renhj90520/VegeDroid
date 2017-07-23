package com.xjconvenience.vege.vege.modules.orderlist;

import com.xjconvenience.vege.vege.modules.PreActivity;
import com.xjconvenience.vege.vege.webservices.VegeServices;
import com.xjconvenience.vege.vege.webservices.VegeServicesComponent;

import dagger.Component;

/**
 * Created by Ren Haojie on 2017/7/22.
 */

@PreActivity
@Component(modules = MainModule.class, dependencies = VegeServicesComponent.class)
public interface MainComponent {
    void inject(MainActivity mainActivity);
}
