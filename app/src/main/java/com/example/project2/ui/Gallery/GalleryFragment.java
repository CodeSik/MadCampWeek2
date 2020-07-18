package com.example.project2.ui.Gallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.example.project2.R;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class GalleryFragment extends Fragment {

    private static final int RESULT_OK = -1;
    public ImageView ivImage;
    public GalleryFragment() {
    }

    private final int CAMERA_CODE = 1111;
    private final int GALLERY_CODE=1112;

    private String currentPhotoPath; //실제 사진 파일 경로
    String mImageCaptureName; //이미지 이름

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
        ivImage.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
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

    private void sendPicture(Uri imgUri) {
        String imagePath = getRealPathFromURI(imgUri); // path 경로
        ExifInterface exif = null;
        try { exif = new ExifInterface(imagePath); } catch (IOException e) { e.printStackTrace(); }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);
        Bitmap bitmap = getResizePicture(imagePath);
        //경로를 통해 비트맵으로 전환
        ivImage.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
        }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        checkPermissions();

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        ivImage = root.findViewById(R.id.imageView);

        Button galleryButton = root.findViewById(R.id.gallery_button);
        galleryButton.setOnClickListener(v -> selectGallery());

        Button cameraButton = root.findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(v -> selectPhoto());


        return root;
    }

    private final int MULTIPLE_PERMISSION_REQUEST = 0;


    private void checkPermissions() {
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

