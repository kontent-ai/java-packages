package com.github.kentico.delivery_android_sample.data.source;

import java.util.List;

public interface IMultipleCallback<T> extends ICallback{

    void onItemsLoaded(List<T> items);

}
