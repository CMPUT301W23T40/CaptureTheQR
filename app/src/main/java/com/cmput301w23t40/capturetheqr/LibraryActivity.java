package com.cmput301w23t40.capturetheqr;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * This class defines the UI page for the QR Code Library
 */
public class LibraryActivity extends AppCompatActivity {
    private RecyclerView qrCodeView;
    private ArrayList<QRCode> qrCodeDataList = new ArrayList<>();
    private QRCodeList qrCodeList = new QRCodeList(this, qrCodeDataList);
    private Boolean onUserView = Boolean.TRUE;


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
        showPlayerQR();

        qrCodeList.setOnItemClickListener((QRCodeList.OnItemClickListener) (view, position) -> {
            Intent intent = new Intent(getApplicationContext(),QRDetailsActivity.class);
            intent.putExtra("qrcode", (QRCode) qrCodeList.getCode(position));
            intent.putExtra("onUserView", onUserView);
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
                if (onUserView)
                    onSwiped(viewHolder, 1);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (onUserView && qrCodeDataList.size() > 0) {
                    Integer pos = viewHolder.getBindingAdapterPosition();
                    DB.getPlayer(FirstTimeLogInActivity.getDeviceID(LibraryActivity.this), new DB.CallbackGetPlayer() {
                        @Override
                        public void onCallBack(Player player) {
                            DB.deleteScannerFromQRCode(qrCodeDataList.get(pos).getHashValue(), player.getUsername(), new DB.Callback() {
                                @Override
                                public void onCallBack() {
                                    // remove locally / from UI as well
                                    showPlayerQR();
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Cannot delete from this view. Please click 'MY QRS' to delete",Toast.LENGTH_LONG).show();
                     showAllQR();
                }
            }
        }).attachToRecyclerView(qrCodeView);
    }

    // TODO comments
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

    /**
     * Display all the QRCodes of this user
     */
    private void showPlayerQR() {
        onUserView = Boolean.TRUE;
        DB.getPlayer(FirstTimeLogInActivity.getDeviceID(this), new DB.CallbackGetPlayer(){
            @Override
            public void onCallBack(Player player) {
                DB.getUsersQRCodes(player, new DB.CallbackGetUsersQRCodes() {
                    @Override
                    public void onCallBack(ArrayList<QRCode> myQRCodes) {
                        qrCodeDataList = myQRCodes;
                        qrCodeList = new QRCodeList(getApplicationContext(), qrCodeDataList);
                        qrCodeView.setAdapter(qrCodeList);
                    }
                });
            }
        });
    }

    /**
     * Display all the QRCodes of all players
     */
    private void showAllQR() {
        onUserView = Boolean.FALSE;
        DB.getAllQRCodes(new DB.CallbackGetAllQRCodes() {
            @Override
            public void onCallBack(ArrayList<QRCode> allQRCodes) {
                qrCodeDataList = allQRCodes;
                qrCodeList = new QRCodeList(getApplicationContext(), qrCodeDataList);
                qrCodeView.setAdapter(qrCodeList);
            }
        });
    }
}
