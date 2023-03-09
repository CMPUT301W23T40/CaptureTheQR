package com.cmput301w23t40.capturetheqr;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
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
    static final String TAG ="Database update";
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

        // By default only show that Player's QR Codes
        // TODO: nothing shows initially (maybe since we aren't using DB properly yet?)
        showPlayerQR();

        qrCodeList.setOnItemClickListener((QRCodeList.OnItemClickListener) (view, position) -> {
            Intent intent = new Intent(getApplicationContext(),QRDetailsActivity.class);
            // TODO: right now this assumes that QRCode is identifiable by the hashValue but
            // it's actually unique by (hashValue, location) so we need to change this later

            // intent.putExtra("qrcode", qrCodeList.getCode(position).getHashValue());
            // TODO: have hardcoded hashValue for now to test
            intent.putExtra("qrcode", "696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6");
            startActivity(intent);
        });


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

                if (qrCodeDataList.size() > 0) {
                    DB.delQRCodeInDB(qrCodeDataList.get(viewHolder.getBindingAdapterPosition()).getHashValue());

                    qrCodeList.notifyDataSetChanged();

                }
            }
        }).attachToRecyclerView(qrCodeView);


        //real time updates from Firebase
//        collectionRefQR=DB.getCollectionReferenceQR();
        DB.getCollectionReferenceQR().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
            FirebaseFirestoreException error) {
                // Clear the old list
                qrCodeDataList.clear();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                {
                    Log.d(TAG, String.valueOf(doc.getData()));

                    //below commented code is for reference, will be removed in a future PR

                    //String qrcode = doc.getId();
                    //String qrid = (String) doc.getData().get("codeName");
                    //qrCodeDataList.add(new QRCode(qrcode, qrid,visFake, 10));
                }
                qrCodeList.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.qrlibrary_actions, menu);
        return true;
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
            case R.id.action_allqr:
                showAllQR();
                return true;
            case R.id.action_myqr:
                showPlayerQR();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: for these methods actually get data from Firestore
    // TODO: add logic to only show Player's QR (rn just hardcoded data)
    // TODO: right now, only qr code name is being displayed
    private void showPlayerQR() {
        qrCodeDataList = new ArrayList<>();
        String visFake = new String("vis\nfake\nlist");

        QRCode qr1 = new QRCode("fakeHash", "myQR1", visFake, 10, null);
        QRCode qr2 = new QRCode("fakeHash", "myQR2", visFake, 20, null);
        qrCodeDataList.addAll(Arrays.asList(qr1, qr2));
        qrCodeList = new QRCodeList(this, qrCodeDataList);

        qrCodeView.setAdapter(qrCodeList);
    }

    private void showAllQR() {
        qrCodeDataList = new ArrayList<>();
        String visFake = new String("vis\nfake\nlist");

        QRCode qr1 = new QRCode("fakeHash", "myQR1", visFake, 10, null);
        QRCode qr2 = new QRCode("fakeHash", "myQR2", visFake, 20, null);
        QRCode qr3 = new QRCode("fakeHash", "otherPlayerQR", visFake, 50, null);
        qrCodeDataList.addAll(Arrays.asList(qr1, qr2, qr3));
        qrCodeList = new QRCodeList(this, qrCodeDataList);

        qrCodeView.setAdapter(qrCodeList);
    }
}
