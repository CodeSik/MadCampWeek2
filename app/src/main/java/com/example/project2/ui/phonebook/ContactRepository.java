package com.example.project2.ui.phonebook;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;

public class ContactRepository {
    private Context context;

    public ContactRepository(Context context) {
        this.context = context;
    }

    private String fetchPhoneNumber(ContentResolver cr, String id) {
        Cursor phoneCursor = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                null,
                null
        );
        String number = "";

        if (phoneCursor.moveToFirst())
            number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

        phoneCursor.close();
        return number;
    }

    private String fetchEmail(ContentResolver cr, String id) {
        Cursor emailCursor = cr.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id,
                null,
                null
        );

        String email = "";
        if (emailCursor.moveToFirst())
            email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

        emailCursor.close();
        return email;
    }

    private Uri fetchPhotoUri(ContentResolver cr, String id) {
        try {
            Cursor cursor = cr.query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + " = " + id + " AND " + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'",
                    null,
                    null
            );
            if (cursor == null || !cursor.moveToFirst())
                return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    public ArrayList<JsonData> getContactList() {
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        ArrayList<JsonData> contacts = new ArrayList<JsonData>();

        if (cur == null || cur.getCount() == 0)
            return contacts;

        while (cur != null && cur.moveToNext()) {
            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String number = fetchPhoneNumber(cr, id);
            String email = fetchEmail(cr, id);
            Uri photo = fetchPhotoUri(cr, id);

            contacts.add(new JsonData(name, number, email, photo));
        }

        if (cur != null)
            cur.close();

        return contacts;
    }
}
