package davidokhttputils.qq986945193.com.davidokhttputils.utils;import android.graphics.Bitmap;import android.os.Handler;import android.os.Looper;import android.util.Log;import com.google.gson.Gson;import org.json.JSONException;import org.json.JSONObject;import java.io.IOException;import java.util.Map;import java.util.concurrent.TimeUnit;import davidokhttputils.qq986945193.com.davidokhttputils.constant.Constant;import okhttp3.Call;import okhttp3.Callback;import okhttp3.FormBody;import okhttp3.MediaType;import okhttp3.OkHttpClient;import okhttp3.Request;import okhttp3.RequestBody;import okhttp3.Response;/** * @author ：程序员小冰 * @新浪微博 ：http://weibo.com/mcxiaobing * @GitHub: https://github.com/QQ986945193 * @CSDN博客: http://blog.csdn.net/qq_21376985 * @交流Qq ：986945193 * <p/> * OkHttp使用单利进行封装 */public class OkHttpUtils {    private static OkHttpClient client;    private volatile static OkHttpUtils mOkHttputils;    private final String TAG = OkHttpUtils.class.getSimpleName();//获得类名    private static Handler handler;    //提交json数据    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");    //提交字符串    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown;charset=utf-8");    private static Gson gson;    private OkHttpUtils() {        client = new OkHttpClient().newBuilder().readTimeout(20, TimeUnit.SECONDS)                .connectTimeout(10, TimeUnit.SECONDS)                .writeTimeout(20, TimeUnit.SECONDS).build();        handler = new Handler(Looper.getMainLooper());        gson = new Gson();    }    //采用单例模式获取对象    public static OkHttpUtils getInstance() {        OkHttpUtils instance = null;        if (mOkHttputils == null) {            synchronized (OkHttpUtils.class) {                if (instance == null) {                    instance = new OkHttpUtils();                    mOkHttputils = instance;                }            }        }        return instance;    }    /**     * 同步请求，在android开发中不常用，因为会阻塞UI线程     *     * @param url     * @return     */    public String syncGetByURL(String url) {        //构建一个request请求        Request request = new Request.Builder().url(url).build();        Response response = null;        try {            response = client.newCall(request).execute();//同步请求数据            if (response.isSuccessful()) {                return response.body().string();            }        } catch (Exception e) {            e.printStackTrace();        }        return null;    }    /**     * 请求指定的url返回的结果是json字符串     *     * @param url     * @param callBack     */    public void asyncJsonStringByURL(String url, final OnGetOkhttpStringListener callBack) {        final Request request = new Request.Builder().url(url).build();        client.newCall(request).enqueue(new Callback() {            @Override            public void onFailure(Call call, IOException e) {                e.printStackTrace();            }            @Override            public void onResponse(Call call, Response response) throws IOException {                if (response != null && response.isSuccessful()) {                    onSuccessJsonStringMethod(response.body().string(), callBack);                }            }        });    }    /**     * 请求返回的是jsonOject对象     *     * @param url     * @param callBack     */    public static void asyncJsonObjectByURL(String url, final OnGetJsonObjectListener callBack) {        final Request request = new Request.Builder().url(url).addHeader(Constant.API_KEY, Constant.API_KEY_SECRET).build();        client.newCall(request).enqueue(new Callback() {            @Override            public void onFailure(Call call, IOException e) {                e.printStackTrace();            }            @Override            public void onResponse(Call call, Response response) throws IOException {                if (response != null && response.isSuccessful()) {                    onSuccessJsonObjectMethod(response.body().string(), callBack);                }            }        });    }    /**     * 请求返回的是byte字节数组     *     * @param url     * @param callBack     */    public void asyncGetByteByURL(String url, final OnGetByteArrayListener callBack) {        final Request request = new Request.Builder().url(url).build();        client.newCall(request).enqueue(new Callback() {            @Override            public void onFailure(Call call, IOException e) {                e.printStackTrace();            }            @Override            public void onResponse(Call call, Response response) throws IOException {                if (response != null && response.isSuccessful()) {                    onSuccessByteMethod(response.body().bytes(), callBack);                }            }        });    }    /**     * 请求返回结果是imageView类型 bitmap 类型     *     * @param url     * @param callBack     */    public void asyncDownLoadImageByURL(String url, final OnGetBitmapListenr callBack) {        Request request = new Request.Builder().url(url).build();        client.newCall(request).enqueue(new Callback() {            @Override            public void onFailure(Call call, IOException e) {                e.printStackTrace();            }            @Override            public void onResponse(Call call, Response response) throws IOException {                if (response != null && response.isSuccessful()) {                    byte[] data = response.body().bytes();//                    Bitmap bitmap = new CropSquareTrans().transform(BitmapFactory.decodeByteArray(data, 0, data.length));//                    callBack.onResponse(bitmap);                }            }        });    }    /**     * 模拟表单提交  post提交     *     * @param url     * @param params     * @param callBack     */    public void sendComplexForm(String url, Map<String, String> params, final OnGetJsonObjectListener callBack) {        FormBody.Builder form_builder = new FormBody.Builder();//表单对象，包含以input开始的对象，以html表单为主        if (params != null && !params.isEmpty()) {            for (Map.Entry<String, String> entry : params.entrySet()) {                form_builder.add(entry.getKey(), entry.getValue());            }        }        RequestBody request_body = form_builder.build();        Request request = new Request.Builder().url(url).post(request_body).build();//采用post方式提交        client.newCall(request).enqueue(new Callback() {            @Override            public void onFailure(Call call, IOException e) {                e.printStackTrace();            }            @Override            public void onResponse(Call call, Response response) throws IOException {                if (response != null && response.isSuccessful()) {                    onSuccessJsonObjectMethod(response.body().string(), callBack);                }            }        });    }    /**     * 向服务器提交String请求     *     * @param url     * @param content     * @param callBack     */    public void sendStringByPostMethod(String url, String content, final OnGetJsonObjectListener callBack) {        Request request = new Request.Builder().url(url).post(RequestBody.create(MEDIA_TYPE_MARKDOWN, content)).build();        client.newCall(request).enqueue(new Callback() {            @Override            public void onFailure(Call call, IOException e) {                e.printStackTrace();            }            @Override            public void onResponse(Call call, Response response) throws IOException {                if (response != null && response.isSuccessful()) {                    onSuccessJsonObjectMethod(response.body().string(), callBack);                }            }        });    }    /**     * 请求返回的结果是json字符串     *     * @param jsonValue     * @param callBack     */    private void onSuccessJsonStringMethod(final String jsonValue, final OnGetOkhttpStringListener callBack) {        handler.post(new Runnable() {            @Override            public void run() {                if (callBack != null) {                    try {                        callBack.onResponse(jsonValue);                    } catch (Exception e) {                        e.printStackTrace();                    }                }            }        });    }    /**     * 请求返回的是byte[] 数组     *     * @param data     * @param callBack     */    private void onSuccessByteMethod(final byte[] data, final OnGetByteArrayListener callBack) {        handler.post(new Runnable() {            @Override            public void run() {                if (callBack != null) {                    callBack.onResponse(data);                }            }        });    }    /**     * 返回响应的结果是json对象     *     * @param jsonValue     * @param callBack     */    private static void onSuccessJsonObjectMethod(final String jsonValue, final OnGetJsonObjectListener callBack) {        handler.post(new Runnable() {            @Override            public void run() {                if (callBack != null) {                    try {                        callBack.onResponse(new JSONObject(jsonValue));                    } catch (JSONException e) {                        e.printStackTrace();                    }                }            }        });    }    /**     * 接口 处理OkHttp获取到json字符串     */    interface OnGetOkhttpStringListener {        void onResponse(String result);    }    /**     * 接口 处理OkHttp获取到字节数组     */    public interface OnGetByteArrayListener {        void onResponse(byte[] result);    }    /**     * 接口 处理OkHttp获取到的bitmap     */    public interface OnGetBitmapListenr {        void onResponse(Bitmap bitmap);    }    /**     * 接口 处理OkHttp获取到的JsonObject对象     */    public interface OnGetJsonObjectListener {        void onResponse(JSONObject jsonObject);    }    /**     * 得到gson json对象     *     * @param url     * @param callback     */    public static <T> void get(String url, Class<T> clazz, OkHttpBaseCallback callback) {        Request request = buildGetRequest(url);        request(request, clazz, callback);    }    public static <T> void post(String url, Map<String, String> param, Class<T> clazz, OkHttpBaseCallback callback) {        Request request = buildPostRequest(url, param);        request(request, clazz, callback);    }    public static <T> void request(final Request request, final Class<T> clazz, final OkHttpBaseCallback callback) {        callback.onBeforeRequest(request);        client.newCall(request).enqueue(new Callback() {            @Override            public void onFailure(Call call, IOException e) {                callbackFailure(callback, request, e);            }            @Override            public void onResponse(Call call, Response response) throws IOException {                callback.onResponse(response);                callbackResponse(callback, response);                if (response.isSuccessful()) {                    String resultStr = response.body().string();                    if (callback.mType == String.class) {                        callbackSuccess(callback, response, resultStr);                    } else {                        try {                            Log.e("mm", "7" + resultStr);                            Object obj = gson.fromJson(resultStr, callback.mType);                            callbackSuccess(callback, response, obj);                        } catch (com.google.gson.JsonParseException e) { // Json解析的错误                            Log.e("mm", "666");                            callback.onError(response, response.code(), e);                        }                    }                } else {                    Log.e("mm", "777");                    callbackError(callback, response, null);                }            }        });    }    private static void callbackSuccess(final OkHttpBaseCallback callback, final Response response, final Object obj) {        handler.post(new Runnable() {            @Override            public void run() {                callback.onSuccess(response, obj);            }        });    }    private static void callbackError(final OkHttpBaseCallback callback, final Response response, final Exception e) {        handler.post(new Runnable() {            @Override            public void run() {                callback.onError(response, response.code(), e);            }        });    }    private static void callbackFailure(final OkHttpBaseCallback callback, final Request request, final IOException e) {        handler.post(new Runnable() {            @Override            public void run() {                callback.onFailure(request, e);            }        });    }    private static void callbackResponse(final OkHttpBaseCallback callback, final Response response) {        handler.post(new Runnable() {            @Override            public void run() {                callback.onResponse(response);            }        });    }    private static Request buildPostRequest(String url, Map<String, String> params) {        return buildRequest(url, HttpMethodType.POST, params);    }    private static Request buildGetRequest(String url) {        return buildRequest(url, HttpMethodType.GET, null);    }    private static Request buildRequest(String url, HttpMethodType methodType, Map<String, String> params) {        Request.Builder builder = new Request.Builder()                .url(url).addHeader(Constant.API_KEY, Constant.API_KEY_SECRET);        if (methodType == HttpMethodType.POST) {            RequestBody body = builderFormData(params);            builder.post(body);        } else if (methodType == HttpMethodType.GET) {            builder.get();        }        return builder.build();    }    private static RequestBody builderFormData(Map<String, String> params) {        FormBody.Builder form_builder = new FormBody.Builder();//表单对象，包含以input开始的对象，以html表单为主        if (params != null && !params.isEmpty()) {            for (Map.Entry<String, String> entry : params.entrySet()) {                form_builder.add(entry.getKey(), entry.getValue());            }        }        return form_builder.build();    }    enum HttpMethodType {        GET,        POST,    }    //    private void AddHeader() {//        client.interceptors(new Interceptor() {//            @Override//            public Response intercept(Chain chain) throws IOException {//                Request request = chain.request();//                request = request.newBuilder().addHeader("d", "d").build();//                return chain.proceed(request);//            }//        });//    }}