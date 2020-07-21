package com.example.project2.ui.phonebook;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.project2.JsonTaskPost;
import com.example.project2.JsonTaskPut;
import com.example.project2.R;
import com.example.project2.ui.Gallery.ApiService;
import com.facebook.Profile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GalleryActivity extends AppCompatActivity {
    public ImageView ivImage;
    private final int GALLERY_CODE = 1111;
    private String id;
    ApiService apiService;
    Bitmap mBitmap;
    final int MULTIPLE_PERMISSION_REQUEST = 0;
    private ProfileData profileData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        ivImage = (ImageView) findViewById(R.id.uploadView2);
        id = String.valueOf(Profile.getCurrentProfile().getId());
        checkPermissions();
        selectGallery();
        initRetrofitClient();

        FloatingActionButton fab = findViewById(R.id.uploadButton3);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBitmap != null) {
                    multipartImageUpload();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Bitmap is null. Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ivImage.setImageResource(0); //View Reset

    }


/*
    public class JsonTaskGetProfile extends AsyncTask<String, String, String> {
        ProgressDialog dialog;
        @Override
        protected String doInBackground(String... urls) {

            try {

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();

                    con.connect();

                    InputStream stream = con.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));

                    //실제 데이터를 받는곳
                    StringBuffer buffer = new StringBuffer();
                    //line별 스트링을 받기 위한 temp 변수
                    String line = "";
                    //아래라인은 실제 reader에서 데이터를 가져오는 부분이다. 즉 node.js서버로부터 데이터를 가져온다.
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    //다 가져오면 String 형변환을 수행한다. 이유는 protected String doInBackground(String… urls) 니까
                    return buffer.toString();
                    //아래는 예외처리 부분이다.

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();
            //Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();

            try {
                JSONArray jarray = new JSONArray(result);

                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
                    String id = jObject.getString("id");
                    String name = jObject.getString("name");
                    String number = jObject.getString("number");
                    String follow = jObject.getString("follow");
                    String state = jObject.getString("state");
                    String photo = jObject.getString("photo");
                    profileData = new ProfileData(id, name, number,follow, state, photo);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.d("printget",result);
        }

    }

 */
    private void checkPermissions(){
        /* Set permission */
        ArrayList<String> rejectedPermission = new ArrayList<String>();
        String[] requiredPermission = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        for (String permission: requiredPermission) {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getApplicationContext()),permission)
                    != PackageManager.PERMISSION_GRANTED) {
                rejectedPermission.add(permission);
            }
        }
        String[] rejectedPermission2 = new String[rejectedPermission.size()];

        for (int i=0; i < rejectedPermission.size(); ++i){
            rejectedPermission2[i] = rejectedPermission.get(i);
        }

        if(rejectedPermission.size() > 0) {
            requestPermissions(rejectedPermission2, MULTIPLE_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MULTIPLE_PERMISSION_REQUEST) {
            if (grantResults.length == 0) {
                //initializeFeeds();
                return;
            }
            for (int i = 0; i < permissions.length; ++i) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    checkPermissions();
                }
            }
        }
    }

    private void selectGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == GALLERY_CODE) {
                sendPicture(data.getData()); //갤러리에서 가져오기
            }
        }
    }

    private void sendPicture(Uri imgUri) {
        String imagePath = getRealPathFromURI(imgUri); // path 경로
        ExifInterface exif = null;
        try { exif = new ExifInterface(imagePath); } catch (IOException e) { e.printStackTrace(); }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);

        //경로를 통해 비트맵으로 전환
        Bitmap bitmap = getResizePicture(imagePath);
        Bitmap finalBitmap = rotate(bitmap, exifDegree);

        //이미지 뷰에 비트맵 넣기
        mBitmap = finalBitmap;
        ivImage.setImageBitmap(finalBitmap);
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0; String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getApplicationContext().getContentResolver().query(contentUri, proj, null, null, null);
        assert cursor != null;
        if(cursor.moveToFirst()){ column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        } return cursor.getString(column_index);
    }

    private Bitmap getResizePicture(String imagePath){ //사진 용량을 줄여주는 함수
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        int resize = 1000;
        int width = options.outWidth;
        int height = options.outHeight;
        int sampleSize = 1;
        while (true) {
            if (width / 2 < resize || height / 2 < resize)
                break;

            width /= 2;
            height /= 2;
            sampleSize *= 2;
        }
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(imagePath, options);
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        } return 0;
    }

    private Bitmap rotate(Bitmap src, float degree) {
        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree); // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    /*upload*/
    private void initRetrofitClient() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        apiService = new Retrofit.Builder().baseUrl("http://192.249.19.244:1180/").client(client).build().create(ApiService.class);
    }

    private void multipartImageUpload() {
        try {
            File filesDir = getApplicationContext().getFilesDir();
            File file = new File(filesDir, "image" + id + ".png"); //file name = image.png

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] bitmapdata = bos.toByteArray();


            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("photo", file.getName(), reqFile);
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "photo");
            retrofit2.Call<okhttp3.ResponseBody> req = apiService.postImage(body, name);
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (response.code() == 200) {
                        Toast.makeText(getApplicationContext(), "upload success", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getApplicationContext(), response.code() + " ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Request failed", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mBitmap = null;
    }

}