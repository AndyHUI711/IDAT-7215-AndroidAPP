package com.example.imagerecognitiondemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.imagerecognitiondemo.adapter.ResultAdapter;
import com.example.imagerecognitiondemo.model.GetResult;
import com.example.imagerecognitiondemo.model.GetToken;
import com.example.imagerecognitiondemo.network.ApiService;
import com.example.imagerecognitiondemo.network.NetCallBack;
import com.example.imagerecognitiondemo.network.ServiceGenerator;
import com.example.imagerecognitiondemo.util.Base64Util;
import com.example.imagerecognitiondemo.util.Constant;
import com.example.imagerecognitiondemo.util.FileUtil;
import com.example.imagerecognitiondemo.util.SPUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ApiService service;
    private String accessToken;
    /**
     * show image
     */
    private ImageView ivPicture;
    /**
     *process bar
     */
    private ProgressBar pbLoading;
    /**
     * bottom popup
     */
    private BottomSheetDialog bottomSheetDialog;
    /**
     * popup
     */
    private View bottomView;
    private RxPermissions rxPermissions;
    private File outputImage;
    /**
     * album open code
     */
    private static final int OPEN_ALBUM_CODE = 100;
    /**
     * cam open code
     */
    private static final int TAKE_PHOTO_CODE = 101;

    /**
     * global attributes
     */
    public static String URL ="https://356ac17611.goho.co/detect/";
    public static String model_type = "general";
    public static String function = "POST";
    public static int threshold = 5;
    public static Boolean show_per = Boolean.TRUE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        service = ServiceGenerator.createService(ApiService.class);
        getAccessToken();

        ivPicture = findViewById(R.id.iv_picture);
        pbLoading = findViewById(R.id.pb_loading);
        rxPermissions = new RxPermissions(this);
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomView = getLayoutInflater().inflate(R.layout.dialog_bottom, null);


    }

    /**
     * pic to Bitmap
     * @param path path of picture
     * @return
     */
    public static Bitmap openImage(String path){
        Bitmap bitmap = null;
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    /**
     * draw result
     * @param imageBitmap
     * //@param keywordRects
     * @param valueRects
     * @param msg
     */
    private void drawRectangles(Bitmap imageBitmap,
                                int[] valueRects, String[] msg) {
        float left, top, right, bottom;
        Bitmap mutableBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
//Canvas canvas = new Canvas(imageBitmap);
        Paint paint = new Paint();
        TextPaint textPaint = new TextPaint();
        //隐藏加载
        pbLoading.setVisibility(View.GONE);

        for (int i = 0; i < msg.length; i++) {

            left = (float)valueRects[i * 4];
            top = (float)valueRects[i * 4 + 1];
            right = (float)valueRects[i * 4 + 2];
            bottom = (float)valueRects[i * 4 + 3];
            Log.i("i:", String.valueOf(i));

            if(left!=0 && top !=0 && right!=0 && bottom!=0){

            Random random = new Random();
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);

            paint.setColor(Color.rgb(r,g,b));
            paint.setStyle(Paint.Style.STROKE);//no infill
            paint.setStrokeWidth(10); //line width

            canvas.drawRect(left, top, right, bottom, paint);
            Log.i("VOLLEY","onResponse");

            textPaint.setColor(Color.rgb(252,165,14));
            textPaint.setTextSize(50);

            int width = (int) textPaint.measureText(msg[i]);

            canvas.drawText(msg[i],left,top,textPaint);

//            StaticLayout staticLayout = new StaticLayout(msg[i], textPaint, (int) width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);
//            canvas.translate(left,top);
//            staticLayout.draw(canvas);
//            canvas.translate(-left,-top);
        }}


        ivPicture.setImageBitmap(mutableBitmap);//img: xml's ImagView
    }



    /**
     * image main recognition
     *
     * @param token       token
     * @param imageBase64 picture Base64
     * @param imgUrl      picture Url
     */
    private void ImageRecognition(String token, String imageBase64, String imgUrl) {
        service.getRecognitionResult(token, imageBase64, imgUrl).enqueue(new NetCallBack<GetResult>() {
            @Override
            public void onSuccess(Call<GetResult> call, Response<GetResult> response) {
                List<GetResult.ResultBean> result = response.body() != null ? response.body().getResult() : null;
                if (result != null && result.size() > 0) {
                    //recognition results
                    showRecognitionResult(result);
                } else {
                    pbLoading.setVisibility(View.GONE);
                    showMsg("no recognition results");
                }
            }

            @Override
            public void onFailed(String errorStr) {
                pbLoading.setVisibility(View.GONE);
                Log.e(TAG, "recognition results failed，reason：" + errorStr);
            }
        });
    }

    /**
     * recognition -camera
     *
     * @param view
     */
    @SuppressLint("CheckResult")
    public void IdentifyTakePhotoImage(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rxPermissions.request(
                            Manifest.permission.CAMERA)
                    .subscribe(grant -> {
                        if (grant) {
                            //ACCESS PERMISSIONS
                            turnOnCamera();
                        } else {
                            showMsg("NO ACCESS PERMISSIONS");
                        }
                    });
        } else {
            turnOnCamera();
        }
    }

    /**
     * open cam
     */
    private void turnOnCamera() {
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("HH_mm_ss");
        String filename = timeStampFormat.format(new Date());
        //创建File对象
        outputImage = new File(getExternalCacheDir(), "takePhoto" + filename + ".jpg");
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(this,
                    "com.example.imagerecognitiondemo.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        //打开相机
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO_CODE);
    }


    /**
     * recognition - pic album
     *
     */
    @SuppressLint("CheckResult")
    public void IdentifyAlbumPictures(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rxPermissions.request(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(grant -> {
                        if (grant) {
                            //ACCESS PERMISSIONS
                            openAlbum();
                        } else {
                            showMsg("NO ACCESS PERMISSIONS");
                        }
                    });
        } else {
            openAlbum();
        }
    }

    /**
     * open pictures album
     */
    private void openAlbum() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, OPEN_ALBUM_CODE);
    }

    public void Settings(View view){
        Log.i("setting", "jump setting page");
        Intent intent = new Intent(this,SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            pbLoading.setVisibility(View.VISIBLE);
            if (requestCode == OPEN_ALBUM_CODE) {
                //OPEN ALBUM AND RETURN
                String[] filePathColumns = {MediaStore.Images.Media.DATA};
                final Uri imageUri = Objects.requireNonNull(data).getData();
                Cursor cursor = getContentResolver().query(imageUri, filePathColumns, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumns[0]);
                //PICTURE PATH
                String imagePath = cursor.getString(columnIndex);
                cursor.close();
                //RECOGNITION
                localImageRecognition(imagePath);
            } else if(requestCode == TAKE_PHOTO_CODE) {
                String imagePath = outputImage.getAbsolutePath();
                localImageRecognition(imagePath);
            }
        } else {
            showMsg("NOTHING");
        }
    }

    /**
     * local pic recognition
     */
    public void localImageRecognition(String imagePath) {
        try {
//            if (accessToken == null) {
//                showMsg("GET AccessToken INTO null");
//                return;
//            }
            //PICTURE PATH & SHOW
            Glide.with(this).load(imagePath).into(ivPicture);
            //READ IMAGE
            Bitmap bitmap = openImage(imagePath);
            //bitmap = Bitmap.createScaledBitmap(bitmap, ivPicture.getMeasuredWidth(), ivPicture.getMeasuredHeight(), true);

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, options);

            //byte[] imgData = bitmapToByteArray(bitmap);
            byte[] imgData = FileUtil.readFileByBytes(imagePath);

            //ENCODE Base64
            String imageBase64 = Base64Util.encode(imgData);

            //RECOGNITION API

            if (function.equalsIgnoreCase("api")){
                showMsg("Using API");
                ImageRecognition(accessToken, imageBase64, null);}
            else{
                showMsg("Using POST ->  "+URL+"--"+model_type);
                ImageRecognition_json(imageBase64,bitmap);}

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * result list
     *
     */
    private void showRecognitionResult(List<GetResult.ResultBean> result) {

        bottomSheetDialog.setContentView(bottomView);
        bottomSheetDialog.getWindow();
        RecyclerView rvResult = bottomView.findViewById(R.id.rv_result);
        ResultAdapter adapter = new ResultAdapter(R.layout.item_result_rv, result);
        rvResult.setLayoutManager(new LinearLayoutManager(this));
        rvResult.setAdapter(adapter);
        //LOADING
        pbLoading.setVisibility(View.GONE);
        //POPUP
        bottomSheetDialog.show();

    }

    /**
     * Toast message
     */
    private void showMsg(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    /**
     * get API token
     */
    private void requestApiGetToken() {
        String grantType = "client_credentials";
        String apiKey = "ChPW1tTtOYxAbzxKV6LsOfzZ";
        String apiSecret = "UMwVqNX5uVoX2zlylqZl0mMjuttYwaHF";
        service.getToken(grantType, apiKey, apiSecret)
                .enqueue(new NetCallBack<GetToken>() {
                    @Override
                    public void onSuccess(Call<GetToken> call, Response<GetToken> response) {
                        if (response.body() != null) {
                            //Token
                            accessToken = response.body().getAccess_token();
                            Log.d(TAG,accessToken);
                        }
                    }

                    @Override
                    public void onFailed(String errorStr) {
                        Log.e(TAG, "Token FAILED，FAILED REASON：" + errorStr);
                        accessToken = null;
                    }
                });
    }

    public void onSuccess(Call<GetToken> call, Response<GetToken> response) {
        if (response.body() != null) {
            accessToken = response.body().getAccess_token();
            long expiresIn = response.body().getExpires_in();
            long currentTimeMillis = System.currentTimeMillis() / 1000;
            SPUtils.putString(Constant.TOKEN, accessToken, MainActivity.this);
            SPUtils.putLong(Constant.GET_TOKEN_TIME, currentTimeMillis, MainActivity.this);
            SPUtils.putLong(Constant.TOKEN_VALID_PERIOD, expiresIn, MainActivity.this);
        }
    }

    /**
     * Token check
     *
     * @return
     */
    private boolean isTokenExpired() {

        long getTokenTime = SPUtils.getLong(Constant.GET_TOKEN_TIME, 0, this);
        long effectiveTime = SPUtils.getLong(Constant.TOKEN_VALID_PERIOD, 0, this);
        long currentTime = System.currentTimeMillis() / 1000;

        return (currentTime - getTokenTime) >= effectiveTime;
    }

    /**
     * get Token access
     */
    private String getAccessToken() {
        String token = SPUtils.getString(Constant.TOKEN, null, this);
        if (token == null) {
            //GET API
            requestApiGetToken();
        } else {
            if (isTokenExpired()) {
                requestApiGetToken();
            } else {
                accessToken = token;
            }
        }
        return accessToken;
    }

    /**
     * Post require: URL https://356ac17611.goho.co/detect/
     */
    private void ImageRecognition_json(String imageBase64, Bitmap imageBitmap) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "https://356ac17611.goho.co/detect/";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model_type",model_type);
            jsonBody.put("img_b64",imageBase64);
            final String requestBody = jsonBody.toString();

            JsonObjectRequest JSONObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, URL,
                    new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        //Call<GetResult> call, Response<GetResult> response
                        public void onResponse(JSONObject response) {

                            Log.i("VOLLEY","onResponse");
                            Log.d("VOLLEY", response.toString());
                            Log.i("Class", response.getClass().toString());
                            try {
                                JSONArray cls_names = response.getJSONArray("cls_names");
                                Log.d("cls_names", cls_names.toString());
                                Log.d("cls_names", cls_names.getString(0));

                                JSONArray conf = response.getJSONArray("conf");
                                Log.d("conf", conf.toString());
                                Log.d("conf", String.valueOf(conf.getDouble(0)));

                                JSONArray boxes = response.getJSONArray("boxes");
                                Log.d("boxes", boxes.toString());
                                JSONArray box = boxes.getJSONArray(0);
                                Log.d("boxes",box.toString());

                                //ArrayList<String> result = new ArrayList<String>();
                                String[] result = new String[cls_names.length()];
                                int[] valueRects = new int[cls_names.length()*4];

                                for (int i=0;i<cls_names.length();i++){
                                    Log.i("show_per",show_per.toString());
                                    Log.i("threshold", String.valueOf(threshold));
                                    if (conf.getDouble(i)>= (float)threshold/(float)10) {
                                    if (show_per == Boolean.FALSE){
                                        String msg = cls_names.getString(i);
                                        result[i] = msg;
                                    }
                                    else{
                                        String msg = cls_names.getString(i)+
                                            "\n" +String.format("%.2f", (conf.getDouble(i)));
                                        result[i] = msg;
                                    }

                                    JSONArray boxs = boxes.getJSONArray(i);

                                    valueRects[4*i]= boxs.getInt(0);
                                    valueRects[4*i+1]= boxs.getInt(1);
                                    valueRects[4*i+2]= boxs.getInt(2);
                                    valueRects[4*i+3]= boxs.getInt(3);
                                    //result.add(String.valueOf(conf.getDouble(i)));
                                }}
                                if (cls_names.length()!=0){
                                    drawRectangles(imageBitmap, valueRects, result);}
                                else{
                                    showMsg("Nothing detected, try other picture");
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("VOLLEY","onErrorResponse");
                    Log.e("VOLLEY", error.toString());
                    pbLoading.setVisibility(View.GONE);
                    showMsg(error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected com.android.volley.Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    try {
                        String jsonString =
                                new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        Log.d("VOLLEY", jsonString);
                        return com.android.volley.Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
                    } catch (UnsupportedEncodingException e) {
                        return com.android.volley.Response.error(new ParseError(e));
                    } catch (JSONException je) {
                        return com.android.volley.Response.error(new ParseError(je));
                    }
                }
            };
            JSONObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(JSONObjectRequest);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }


}