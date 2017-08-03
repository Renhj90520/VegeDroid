package com.xjconvenience.vege.vege.modules.share;

/**
 * Created by Ren Haojie on 2017/8/3.
 */

public interface ShareContract {

    public interface IShareView {
        void createPagers(String src);

        void showMessage(String message);
    }

    public interface ISharePresenter {
        void loadCoupon();
    }
}
