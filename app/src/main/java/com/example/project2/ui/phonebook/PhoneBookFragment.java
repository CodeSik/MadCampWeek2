package com.example.project2.ui.phonebook;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2.R;

import java.io.IOException;
import java.util.ArrayList;

public class PhoneBookFragment extends Fragment {
    protected static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    protected static final int PERMISSIONS_REQUEST_SEND_SMS = 2;
    protected static final int PERMISSIONS_CALL_PHONE = 3;
    protected static final int PERMISSIONS_REQUEST_ALL = 4;
    private static String[] requiredPermissions = new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE
    };
    private PhoneBookViewModel phoneBookViewModel;

    private PhoneBookAdapter adapter;
    private LinearLayoutManager layoutManager;

    private ListView listview;
    private ArrayAdapter searchAdapter;
    private SearchView searchView;
    private ArrayList<JsonData> backupList ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PhoneBookViewModelFactory factory = new PhoneBookViewModelFactory(this.getContext());
        phoneBookViewModel = ViewModelProviders.of(getActivity(), factory).get(PhoneBookViewModel.class);
        View root = inflater.inflate(R.layout.fragment_phonebook, container, false);

        adapter = new PhoneBookAdapter(new ArrayList<JsonData>(), getContext());
        backupList = new ArrayList<>();
       // searchAdapter = new ArrayAdapter(root.getContext(), R.layout.fragment_phonebook);

        new JsonTask().execute("http://192.249.19.244:1180/phonebook");
        final Observer<ArrayList<JsonData>> contactObserver = new Observer<ArrayList<JsonData>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<JsonData> newContacts) {
                adapter.updateItems(newContacts);
            }
        };

        RecyclerView recyclerView = root.findViewById(R.id.pb_recycler_view);
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        initializeContacts();
        requestRequiredPermissions();
        phoneBookViewModel.getContacts().observe(getViewLifecycleOwner(), contactObserver);

        setHasOptionsMenu(true); // For option menu
        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS:
            case PERMISSIONS_REQUEST_ALL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    phoneBookViewModel.initializeContacts();
        }
    }

    private void initializeContacts() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            ArrayList<JsonData> data = phoneBookViewModel.getContacts().getValue();
            if (data == null)
                phoneBookViewModel.initializeContacts();
            else
                adapter.updateItems(phoneBookViewModel.getContacts().getValue());
                backupList.addAll(phoneBookViewModel.getContacts().getValue());


        }
    }

    private void requestRequiredPermissions() {
        boolean allGranted = true;
        for (String permission : PhoneBookFragment.requiredPermissions) {
            boolean granted = ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
            allGranted = allGranted && granted;
        }

        if (!allGranted)
            requestPermissions(requiredPermissions, PERMISSIONS_REQUEST_ALL);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        adapter.notifyDataSetChanged();
        inflater.inflate(R.menu.top_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);

        searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
               // listview.setAdapter(searchAdapter);
                //adapter.fillter(query);
                Log.d("submitted: ",query);
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {


                Log.d("Changed: ",newText+backupList.size());
                //TODO: 필터 관련 소스 Filterable 인터페이스를 Adapter 클래스에 구현하자.
                // Here is where we are going to implement the filter logic

                if(newText.length() >0)
                {
                    adapter.fillter(newText,backupList); // 필터를 통해서 현재 보여주는 값 수정함.
                    //TODO: 현재 검색이 안될 경우 clear를 통해 초기화 됌. 최종으로 축소되었을때 backup
                    Log.d("Changed: ",newText+backupList.size());
                }
                else{

                }
                return true;
            }

        });

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {

                //adapter.getListViewItemList().clear();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                adapter.getListViewItemList().clear();
                adapter.getListViewItemList().addAll(backupList);
                adapter.notifyDataSetChanged();

                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

}

