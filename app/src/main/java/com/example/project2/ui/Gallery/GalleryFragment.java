package com.example.project2.ui.Gallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project2.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    String mImageCaptureName; //이미지 이름
    Bitmap mBitmap;

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
        int resize = 100;
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
            File file = new File(filesDir, "image" + ".png");

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


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        ArrayList<String> list = new ArrayList<>();
        for (int i=0; i<100; i++) {
            list.add(String.format("TEXT %d", i));
        }

        RecyclerView recyclerView = root.findViewById(R.id.gallery_recycler_view) ;
        GalleryAdapter adapter = new GalleryAdapter(list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())) ;


        ivImage = root.findViewById(R.id.picked_Image);
        checkPermissions();
        initRetrofitClient();
        Button uploadButton = root.findViewById(R.id.upload_Button);
        Button cameraButton = root.findViewById(R.id.camera_button);
        Button galleryButton = root.findViewById(R.id.gallery_button);
        LinearLayout linearLayout = root.findViewById(R.id.linearLayout);
        RecyclerView recycler = root.findViewById(R.id.gallery_recycler_view);

        galleryButton.setOnClickListener(v -> {
            selectGallery();
            recycler.setVisibility(View.INVISIBLE);
            linearLayout.setVisibility(View.INVISIBLE);
            ivImage.setVisibility(View.VISIBLE);
            uploadButton.setVisibility(View.VISIBLE);

            uploadButton.setOnClickListener(u -> {
                if (mBitmap != null) {
                    multipartImageUpload();
                }
                else {
                    Toast.makeText(getContext(), "Bitmap is null. Try again", Toast.LENGTH_SHORT).show();
                }
                recycler.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                ivImage.setVisibility(View.INVISIBLE);
                uploadButton.setVisibility(View.INVISIBLE);
                ivImage.setImageResource(0);
            });
        });

        cameraButton.setOnClickListener(v -> {
            selectPhoto();
            recycler.setVisibility(View.INVISIBLE);
            linearLayout.setVisibility(View.INVISIBLE);
            ivImage.setVisibility(View.VISIBLE);
            uploadButton.setVisibility(View.VISIBLE);
            uploadButton.setOnClickListener(u -> {
                if (mBitmap != null){
                    multipartImageUpload();
                }
                else {
                    Toast.makeText(getContext(), "Bitmap is null. Try again", Toast.LENGTH_SHORT).show();
                }
                recycler.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                ivImage.setVisibility(View.INVISIBLE);
                uploadButton.setVisibility(View.INVISIBLE);
                ivImage.setImageResource(0);
            });
        });


        return root;
    }

    final int MULTIPLE_PERMISSION_REQUEST = 0;


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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MULTIPLE_PERMISSION_REQUEST) {
            if (grantResults.length == 0) {
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

}