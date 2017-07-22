package com.xjconvenience.vege.vege.modules.orderlist;

import com.xjconvenience.vege.vege.models.PatchDoc;
import com.xjconvenience.vege.vege.webservices.VegeServices;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Ren Haojie on 2017/7/22.
 */

public class OrderInteractor implements IOrderInteractor {
    private VegeServices mVegeServices;

    @Inject
    public OrderInteractor(VegeServices vegeServices) {
        mVegeServices = vegeServices;
    }

    @Override
    public void loadOrders(String index, String perPage, String keyword, String begin, String end, String noshowRemove, OnLoadFinishListenter listener) {

    }

    @Override
    public void updateOrder(int id, List<PatchDoc> order) {

    }
}
