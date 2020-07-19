package com.example.project2.ui.Dial;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.project2.R;

public class Dial extends Fragment implements  View.OnClickListener{
    private Button[] buttons;
    private ImageButton callButton;
    private ImageButton smsButton;
    private ImageButton backspaceButton;
    private TextView text;
    private String count ="";
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final int PERMISSIONS_CALL_PHONE = 2;
    private static final int PERMISSIONS_REQUEST_ALL = 3;
    private static String[] requiredPermissions = new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dial, container, false);
        requestRequiredPermissions();

        // text and call's indices are 11 and 12, respectively
        int[] buttonIDs = new int[]{ R.id.b0, R.id.b1, R.id.b2, R.id.b3, R.id.b4, R.id.b5, R.id.b6, R.id.b7, R.id.b8, R.id.b9 };

        buttons = new Button[buttonIDs.length];
        for (int i = 0; i < buttonIDs.length; i++) {
            buttons[i] = view.findViewById(buttonIDs[i]);
            buttons[i].setOnClickListener(this);
        }

        callButton = view.findViewById(R.id.bcall);
        callButton.setOnClickListener(this);
        smsButton = view.findViewById(R.id.btext);
        smsButton.setOnClickListener(this);
        backspaceButton = view.findViewById(R.id.backspace);
        backspaceButton.setOnClickListener(this);

        text = (TextView)view.findViewById(R.id.text);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backspace) {
            if (count.equals(""))
                return;
            count = count.substring(0, count.length() - 1);
        } else if (v.getId() == R.id.btext) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
            else {
                Intent text = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + count));
                startActivity(text);
            }
        } else if (v.getId() == R.id.bcall) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission.CALL_PHONE }, PERMISSIONS_CALL_PHONE);
            else {
                Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+count));
                startActivity(call);
            }
        } else {
            count += Integer.toString(v.getId() - R.id.b0);
        }
        text.setText(count);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            /* 요청한 권한을 사용자가 "허용"했다면 인텐트를 띄워라
                내가 요청한 게 하나밖에 없기 때문에. 원래 같으면 for문을 돈다.*/
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:010-1111-2222"));
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                }
            }
            else {
                Toast.makeText(getActivity(), "권한 요청을 거부했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void requestRequiredPermissions() {
        boolean allGranted = true;
        for (String permission : Dial.requiredPermissions) {
            boolean granted = ActivityCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED;
            allGranted = allGranted && granted;
        }
        if (!allGranted)
            requestPermissions(requiredPermissions, PERMISSIONS_REQUEST_ALL);
    }
}
