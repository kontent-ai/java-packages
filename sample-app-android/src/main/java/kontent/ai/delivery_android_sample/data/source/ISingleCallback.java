package kontent.ai.delivery_android_sample.data.source;

public interface ISingleCallback <T> extends ICallback{

    void onItemLoaded(T item);

}
