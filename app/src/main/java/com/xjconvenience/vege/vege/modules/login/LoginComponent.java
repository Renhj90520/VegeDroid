package com.xjconvenience.vege.vege.modules.login;

import com.xjconvenience.vege.vege.modules.PreActivity;
import com.xjconvenience.vege.vege.webservices.VegeServicesComponent;

import dagger.Component;

/**
 * Created by Ren Haojie on 2017/7/19.
 */
@PreActivity
@Component(modules = LoginModule.class, dependencies = VegeServicesComponent.class)
public interface LoginComponent {
    void inject(LoginActivity loginActivity);
}
