package com.example.project2.ui.Gallery;

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
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.project2.R;
import com.example.project2.ui.phonebook.PhoneBookFragment;
import com.example.project2.ui.phonebook.ProfileData;
import com.facebook.Profile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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


public class GalleryFragment extends Fragment {

    private static final int RESULT_OK = -1;
    public ImageView ivImage;
    private final int CAMERA_CODE = 1111;
    private final int GALLERY_CODE=1112;
    private String currentPhotoPath; //실제 사진 파일 경로

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, camera, gallery;

    String mImageCaptureName; //이미지 이름
    Bitmap mBitmap;
    GalleryAdapter adapter;

    private ArrayList<GalleryData> serverFeeds ;
    private ProfileData profileInfo;
    private TextInputLayout contents_box;
    private TextView testView;
    private TextView content;
    private TextInputEditText contents_input;
    private String feedContents;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        //String id = String.valueOf(Profile.getCurrentProfile().getId());
        String body = "";

        adapter= new GalleryAdapter(new ArrayList<>(), getContext());
        ivImage = root.findViewById(R.id.picked_Image);
        serverFeeds = new ArrayList<>();
        profileInfo = new ProfileData();


        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);

        fab = root.findViewById(R.id.fab);
        camera =  root.findViewById(R.id.camera);
        gallery =  root.findViewById(R.id.gallery);
        content = root.findViewById(R.id.content);
        contents_box = root.findViewById(R.id.contents_box);
        testView = root.findViewById(R.id.testContents);
        contents_input = root.findViewById(R.id.contents_input);

        checkPermissions();
        initRetrofitClient();
        initializeFeeds();
       // new JsonTaskGetProfile().execute("http://192.249.19.244:1180/users/"+id);
        //new JsonTaskGetPhone().execute("http://192.249.19.244:1180/gallery/"+id);

        SwipeRefreshLayout mSwipeRefreshLayout = root.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
        //    new JsonTaskGetPhone().execute("http://192.249.19.244:1180/gallery/"+id);

            adapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        });



        adapter.notifyDataSetChanged();
        initializeFeeds();

        FloatingActionButton uploadButton = root.findViewById(R.id.upload_Button);
        RecyclerView recycler = root.findViewById(R.id.gallery_recycler_view);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getContext())) ;

        fab.setOnClickListener(v -> {
            anim();
        });
        camera.setOnClickListener(v -> {
            anim();
            selectPhoto();
            recycler.setVisibility(View.INVISIBLE);
            ivImage.setVisibility(View.VISIBLE);
            uploadButton.setVisibility(View.VISIBLE);
            fab.setVisibility(View.INVISIBLE);
            contents_box.setVisibility(View.VISIBLE);
            testView.setVisibility(View.VISIBLE);

            contents_input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    feedContents = s.toString();
                            //feedContents 가 유저가 입력하는 피드 내용. 이후에 uploadButton 을 누르면 서버에 전송하기.
                }
                @Override
                public void afterTextChanged(Editable s) {
                    testView.setText(s.toString());
                }
            });


            uploadButton.setOnClickListener(u -> {
                if (mBitmap != null) {
                    multipartImageUpload();
                    //여기에서 feedContents 도 서버로 전송하기
                }
                else {
                    Toast.makeText(getContext(), "Bitmap is null. Try again", Toast.LENGTH_SHORT).show();
                }
                recycler.setVisibility(View.VISIBLE);
                ivImage.setVisibility(View.INVISIBLE);
                uploadButton.setVisibility(View.INVISIBLE);
                ivImage.setImageResource(0);
                fab.setVisibility(View.VISIBLE);
                contents_box.setVisibility(View.INVISIBLE);
                testView.setVisibility(View.INVISIBLE);

            });
        });

        gallery.setOnClickListener(v -> {
            anim();
            selectGallery();
            recycler.setVisibility(View.INVISIBLE);
            ivImage.setVisibility(View.VISIBLE);
            uploadButton.setVisibility(View.VISIBLE);
            fab.setVisibility(View.INVISIBLE);
            contents_box.setVisibility(View.VISIBLE);
            testView.setVisibility(View.VISIBLE);

            contents_input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    feedContents = s.toString();
                    //feedContents 가 유저가 입력하는 피드 내용. 이후에 uploadButton 을 누르면 서버에 전송하기.
                }
                @Override
                public void afterTextChanged(Editable s) {
                    testView.setText(s.toString());
                }
            });

            uploadButton.setOnClickListener(u -> {
                if (mBitmap != null) {
                    multipartImageUpload();
                    //상태메세지 올리기
                }
                else {
                    Toast.makeText(getContext(), "Bitmap is null. Try again", Toast.LENGTH_SHORT).show();
                }
                recycler.setVisibility(View.VISIBLE);
                ivImage.setVisibility(View.INVISIBLE);
                uploadButton.setVisibility(View.INVISIBLE);
                ivImage.setImageResource(0);
                fab.setVisibility(View.VISIBLE);
                contents_box.setVisibility(View.INVISIBLE);
                testView.setVisibility(View.INVISIBLE);
            });

        });


        return root;
    }

    private void selectPhoto() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(Objects.requireNonNull(getContext()).getPackageManager()) != null) {
                File photoFile = null;
                try { photoFile = createImageFile(); }
                catch (IOException ignored) { }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(
                            getContext(),
                            "com.example.project2",
                            photoFile
                    );
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, CAMERA_CODE);
                    }
                }
            }
        }

    private File createImageFile() throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/path/");
        if (!dir.exists()) {
            dir.mkdirs(); }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mImageCaptureName = timeStamp + ".png";
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/path/" + mImageCaptureName);
        currentPhotoPath = storageDir.getAbsolutePath();
        return storageDir;
    }

    private void getPictureForPhoto() {
        Bitmap bitmap = getResizePicture(currentPhotoPath);
        ExifInterface exif = null;
        try { exif = new ExifInterface(currentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation;
        int exifDegree;
        if (exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientationToDegrees(exifOrientation);
        } else {
            exifDegree = 0;
        }
        Bitmap finalBitmap = rotate(bitmap, exifDegree);
        //ivImage.setImageBitmap(finalBitmap);//이미지 뷰에 비트맵 넣기
        mBitmap = finalBitmap;
        ivImage.setImageBitmap(finalBitmap);
    }


    private void selectGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
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

    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0; String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(contentUri, proj, null, null, null);
        assert cursor != null;
        if(cursor.moveToFirst()){ column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        } return cursor.getString(column_index);
    }


    private void multipartImageUpload() {
        try {
            File filesDir = getContext().getFilesDir();
            File file = new File(filesDir, "image" + ".png"); //file name = image.png Todo change filename by feeds

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
                        Toast.makeText(getContext(), "upload success", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getContext(), response.code() + " ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getContext(), "Request failed", Toast.LENGTH_SHORT).show();
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

    ApiService apiService;

    private void initRetrofitClient() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        apiService = new Retrofit.Builder().baseUrl("http://192.249.19.244:1180/").client(client).build().create(ApiService.class);
    }

    public void anim() {

        if (isFabOpen) {
            camera.startAnimation(fab_close);
            gallery.startAnimation(fab_close);
            camera.setClickable(false);
            gallery.setClickable(false);
            isFabOpen = false;
        } else {
            camera.startAnimation(fab_open);
            gallery.startAnimation(fab_open);
            camera.setClickable(true);
            gallery.setClickable(true);
            isFabOpen = true;
        }
    }




    final int MULTIPLE_PERMISSION_REQUEST = 0;

    //a
    private void checkPermissions(){
        /* Set permission */
        ArrayList<String> rejectedPermission = new ArrayList<String>();
        String[] requiredPermission = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        for (String permission: requiredPermission) {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),permission)
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


    private void initializeFeeds() {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            adapter.updateItems(serverFeeds);
            adapter.updateProfile(profileInfo);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MULTIPLE_PERMISSION_REQUEST) {
            if (grantResults.length == 0) {
                initializeFeeds();
                return;
            }
            for (int i = 0; i < permissions.length; ++i) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    checkPermissions();
                }
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
     if(resultCode == RESULT_OK) {
        switch (requestCode) {
            case GALLERY_CODE:
                sendPicture(data.getData()); //갤러리에서 가져오기
                break;
            case CAMERA_CODE:
                getPictureForPhoto(); //카메라에서 가져오기
                break;
            default: break;
        }
    }
}


    public class JsonTaskGetPhone extends AsyncTask<String, String, String> {
        ProgressDialog dialog;
        protected void onPreExecute() {

            super.onPreExecute();


            dialog = new ProgressDialog(getContext());

            //dialog.setCancelable(false);

            dialog.show();

        }

        @Override
        protected String doInBackground(String... urls) {

            try {

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    URL url = new URL(urls[0]);//이러면 되냐?
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
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }
                    //다 가져오면 String 형변환을 수행한다. 이유는 protected String doInBackground(String… urls) 니까
                    return buffer.toString();
                    //아래는 예외처리 부분이다.

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
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
            if (serverFeeds != null)
                serverFeeds.clear();
            if (adapter.getListViewItemList() != null)
                adapter.getListViewItemList().clear();

            try {
                JSONArray jarray = new JSONArray(result);
                //  ArrayList<JsonData> datalist = new ArrayList<>();
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
                    String id = jObject.getString("id");
                    String photoid = jObject.getString("photoid");
                    String image = jObject.getString("image");
                    String contents = jObject.getString("contents");
                    int like = jObject.getInt("like");
                    GalleryData data = new GalleryData(id, photoid, image, contents, like);
                    serverFeeds.add(data);
                }


            }catch (JSONException e) {

                e.printStackTrace();

            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.updateItems(serverFeeds);
                    //adapter.notifyDataSetChanged();
                }
            });
            Log.d("printget",result);
        }

    }

    public class JsonTaskGetProfile extends AsyncTask<String, String, String> {
        ProgressDialog dialog;
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getContext());
            dialog.show();
        }

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
                    profileInfo = new ProfileData(id, name, number,follow, state, photo);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.updateProfile(profileInfo);
                    //adapter.notifyDataSetChanged();
                }
            });
            Log.d("printget",result);
        }
    }

}