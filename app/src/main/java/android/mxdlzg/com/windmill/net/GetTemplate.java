package android.mxdlzg.com.windmill.net;

/**
 * Created by 廷江 on 2017/5/1.
 */

public class GetTemplate {

    public static interface SuccessCallback{
        void onSuccess(String result);
    }
    public static interface FailCallback{
        void onFail();
    }
}
