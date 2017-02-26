package cn.spsilab.locatethings;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.spsilab.locatethings.Data.LittleItem;
import cn.spsilab.locatethings.Data.LocateThingsDatabase;
import cn.spsilab.locatethings.Data.TestData;
import cn.spsilab.locatethings.module.ResponseResult;

public class MainActivity extends AppCompatActivity implements
        ItemListRecyclerAdapter.ItemAdapterOnClickHandler, ItemOperateHandler, NetworkService.NetworkCallback {
    private final String TAG = "Main Activity";

    private DrawerLayout drawerLayout;

    private Toolbar toolbar;

    private NavigationView navigationView;
    private RecyclerView mRecycerView;

    private LinearLayoutManager mLayoutManager;
    private ItemListRecyclerAdapter mRecyclerViewAdapter;

    private LocateThingsDatabase mDatabase;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind view component.
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mRecycerView = (RecyclerView) findViewById(R.id.recyclerview_item_list);

        // instantiate layoutManager and RecyclerView adapter.
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewAdapter = new ItemListRecyclerAdapter(this);

        // init database.
        mDatabase = LocateThingsDatabase.getInstance(this);
        // insert test data.
        mDatabase.insertTestData();

        // set adapter data.
        ArrayList<LittleItem> itemsArray = mDatabase.getItemsByUserId(TestData.fakeUserId);
        mRecyclerViewAdapter.setItemsArrayList(itemsArray);

        // enable RecyclerView.
        mRecycerView.setHasFixedSize(true);
        mRecycerView.setLayoutManager(mLayoutManager);
        mRecycerView.setAdapter(mRecyclerViewAdapter);


        setSupportActionBar(toolbar);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.on_drawer_open, R.string.on_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

//        findViewById(R.id.btn_find).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this,"find",Toast.LENGTH_SHORT).show();
//            }
//        });

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavMenuItemClickListener(this, drawerLayout, mRecycerView, toolbar));
        getSupportFragmentManager().addOnBackStackChangedListener(new android.support.v4.app.FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    // in main activity view
                    mRecycerView.setVisibility(View.VISIBLE);
                    // drawerlayout can drawer
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
                    toolbar.setTitle("Locate things");
                    toolbar.getMenu().setGroupVisible(0, true);
                }

            }
        });

        // when open the app auto login
        if (NetworkService.checkIsLogin(this)) {
            loginSucess();
        }

    }


    /**
     * when back press , close the drawerlayout
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "back 1");
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            Log.d(TAG, "back 2");
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Log.d(TAG, "back 3");
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_add:
                //Toast.makeText(this,"add",Toast.LENGTH_SHORT).show();
                FragmentManager fm = getFragmentManager();
                AddItemDialog addDialog = new AddItemDialog();
                addDialog.show(fm, "AddItemDialog");
                break;
            case R.id.btn_setting:
                Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void listItemOnClick(int inListPosi) {
        FragmentManager fm = getFragmentManager();
        ItemDetailDialog itemDetailDialog = new ItemDetailDialog();

        // attach data.
        Bundle args = new Bundle();
        args.putInt("inListPosi", inListPosi);
        args.putSerializable("item", mRecyclerViewAdapter.getItemsArrayList().get(inListPosi));
        itemDetailDialog.setArguments(args);

        // show dialog.
        itemDetailDialog.show(fm, "itemDetailDialog");
    }

    @Override
    public void onItemChange(int inAdapterPosi, LittleItem item) {
        long itemId = mRecyclerViewAdapter.getItemsArrayList().get(inAdapterPosi).getItemId();
        // update sql
        mDatabase.updateItemById(itemId, item);
        // update recyclerView adapter
        mRecyclerViewAdapter.adapterListChangeItem(inAdapterPosi, item);
    }

    @Override
    public void onAddItem(LittleItem newItem) {
        // update sql
        mDatabase.addItem(newItem);
        // update recyclerView adapter.
        mRecyclerViewAdapter.adapterListAddItem(newItem);
    }

    @Override
    public void onItemRemove(int inAdapterPosi) {
        long itemId = mRecyclerViewAdapter.getItemsArrayList().get(inAdapterPosi).getItemId();
        // update sql
        mDatabase.removeItemById(itemId);
        // update recyclerView adapter.
        mRecyclerViewAdapter.adapterListRemoveItem(inAdapterPosi);
    }

    /**
     * if you login on the loginActivity , this method will be invoke
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == getResources().getInteger(R.integer.LOGIN_STATUS)) {
            if (resultCode == getResources().getInteger(R.integer.LOGIN_SUCCESS)) {
                loginSucess();
                return;
            }
            if (requestCode == getResources().getInteger(R.integer.LOGIN_FAILED)) {
                Log.d(TAG, data.getStringExtra("msg"));
            }
        } else if (requestCode == getResources().getInteger(R.integer.SELECT_IMG)) {
            Uri selectedImageUri = data.getData();
            Log.d(TAG, selectedImageUri.getPath());
            Toast.makeText(this, "select img", Toast.LENGTH_SHORT).show();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * network callback
     */
    @Override
    public void onSuccess(ResponseResult result) {
        if (result.getStatus() == getResources().getInteger(R.integer.LOGIN_SUCCESS)) {
            loginSucess();
        } else if (result.getStatus() == getResources().getInteger(R.integer.UPDATE_SUCCESS)) {
            onUserinfoUpdate();
        }
    }

    /**
     * network callback
     */
    @Override
    public void onFailure(ResponseResult result, Throwable t) {

    }

    /**
     * if login success, the nav will show userInfo,logout btn,and remove login btn
     * set the header img and user name
     */
    private void loginSucess() {
        //remove login btn
        navigationView.getMenu().getItem(0).setVisible(false);
        navigationView.getMenu().getItem(1).setVisible(true);
        navigationView.getMenu().getItem(3).setVisible(true);
        onUserinfoUpdate();
    }

    private void onUserinfoUpdate() {
        View view = navigationView.getHeaderView(0);
        //change the header img and username
        ImageView userHeaderImg = (ImageView) view.findViewById(R.id.img_user_header);
        TextView userNameText = (TextView) view.findViewById(R.id.str_user_name);
        LocateThings locateThings = (LocateThings) getApplicationContext();

        if (locateThings.getUser() != null) {
            userNameText.setText(locateThings.getUser().getName());
            NetworkService.getInstance().getPicture(locateThings.getUser().getPhoto(), userHeaderImg, R.drawable.user);
        }
    }

    /**
     * show login btn,change default user header image
     */
    public void logout() {
        //remove logout btn
        navigationView.getMenu().getItem(3).setVisible(false);
        navigationView.getMenu().getItem(1).setVisible(false);
        navigationView.getMenu().getItem(0).setVisible(true);
        //if login ,change the header img and username
        ImageView userHeaderImg = (ImageView) navigationView.findViewById(R.id.img_user_header);
        TextView userNameText = (TextView) navigationView.findViewById(R.id.str_user_name);
        userHeaderImg.setImageResource(R.drawable.user);
        userNameText.setText("login");
    }
}