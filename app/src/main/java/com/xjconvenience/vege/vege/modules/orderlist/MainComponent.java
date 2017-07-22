package com.xjconvenience.vege.vege.modules.orderlist;

import com.xjconvenience.vege.vege.modules.PreActivity;
import com.xjconvenience.vege.vege.webservices.VegeServices;

import dagger.Component;

/**
 * Created by Ren Haojie on 2017/7/22.
 */

@PreActivity
@Component(modules = MainModule.class, dependencies = VegeServices.class)
public class MainComponent {
}
