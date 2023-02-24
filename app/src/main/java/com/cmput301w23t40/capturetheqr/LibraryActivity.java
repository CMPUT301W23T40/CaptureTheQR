package com.cmput301w23t40.capturetheqr;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class defines the UI page for the QR Code Library
 */
public class LibraryActivity extends AppCompatActivity {

    RecyclerView qrCodeView;
    ArrayList<QRCode> qrCodeDataList;
    QRCodeList qrCodeList;

    /**
     * override Activity onCreate method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Display list of QR Codes
        qrCodeView = findViewById(R.id.qrcode_list);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        qrCodeView.setLayoutManager(manager);

        // TODO: using fake data rn (waiting for Firestore integration)
        // TODO: right now, only qr code name is being displayed
        qrCodeDataList = new ArrayList<>();
        ArrayList<String> visFake = new ArrayList<String>(Arrays.asList("vis", "fake", "list"));

        QRCode qr1 = new QRCode("fakeHash", "BFG5DGW54", visFake, 10);
        QRCode qr2 = new QRCode("fakeHash", "name2", visFake, 20);
        qrCodeDataList.addAll(Arrays.asList(qr1, qr2));
        qrCodeList = new QRCodeList(this, qrCodeDataList);

        qrCodeView.setAdapter(qrCodeList);
        FirebaseFirestore database;

        database = FirebaseFirestore.getInstance();
        // adding a reference to QR code collection
        final CollectionReference collectionReference = database.collection("qrcode");

        /* The ItemTouchHelper swipe-to-delete functionality below was copied (and altered) from:
            author: https://auth.geeksforgeeks.org/user/chaitanyamunje
            url: https://www.geeksforgeeks.org/swipe-to-delete-and-undo-in-android-recyclerview/
            last updated: Oct 5, 2021
            altered: removed Snackbar functionality that allows for undoing the deletion and changed the onMove so that it calls onSwiped (entry is
                     deleted even when the user only partially swipes the item)
        */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView cityList, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                onSwiped(viewHolder, 1);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // TODO: delete via Firestore (so changes are persistent)

                //qrCodeDataList.remove(viewHolder.getBindingAdapterPosition());

                if(qrCodeDataList.size()>0) {

                    database.collection("qrcode").document(qrCodeDataList.get(viewHolder.getBindingAdapterPosition()).getCodeName())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
// These are a method which gets executed when the task is succeeded

                                    Log.d(TAG, "Data has been deleted successfully!");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Data could not be deleted!" + e.toString());
                                }
                            });
                }

                qrCodeList.notifyDataSetChanged();

            }
        }).attachToRecyclerView(qrCodeView);

        //real time updates from Firebase
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
            FirebaseFirestoreException error) {
                // Clear the old list
                //qrCodeDataList.clear();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                {
                    Log.d(TAG, String.valueOf(doc.getData().get("BFG5DGW54")));
                    String qrcode = doc.getId();
                    String qrid = (String) doc.getData().get("BFG5DGW54");
                    qrCodeDataList.remove(new QRCode(qrcode, qrid,visFake, 10)); // Adding the cities and provinces from FireStore
                }
                qrCodeList.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud

            }
        });





    }

    /**
     * override Activity onOptionsItemSelection method for our actionBar back button
     * @param item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
