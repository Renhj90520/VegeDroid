package com.xjconvenience.vege.vege.modules.share;

import com.xjconvenience.vege.vege.modules.PreActivity;
import com.xjconvenience.vege.vege.webservices.VegeServicesComponent;

import dagger.Component;

/**
 * Created by Ren Haojie on 2017/8/3.
 */

@PreActivity()
@Component(modules = ShareModule.class, dependencies = VegeServicesComponent.class)
public interface ShareComponent {
    void Inject(ShareActivity shareActivity);
}
