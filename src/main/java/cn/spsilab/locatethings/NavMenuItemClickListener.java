package cn.spsilab.locatethings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import cn.spsilab.locatethings.loginmodule.LoginActivity;

/**
 * Created by Feng on 2/17/2017.
 * navigation
 */
class NavMenuItemClickListener implements NavigationView.OnNavigationItemSelectedListener {

    private MainActivity mainActivity;
    private DrawerLayout drawerLayout;


    public NavMenuItemClickListener(MainActivity mainActivity, DrawerLayout drawerLayout) {
        this.mainActivity = mainActivity;
        this.drawerLayout = drawerLayout;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Bundle bundle = new Bundle();
        switch (item.getItemId()) {
            case R.id.menu_user_login: {
                Intent intent = new Intent(mainActivity, LoginActivity.class);
                mainActivity.startActivityForResult(intent, mainActivity.getResources().getInteger(R.integer.LOGIN_STATUS));
            }
            break;
            case R.id.menu_user_info: {
                bundle.putString("show_text", item.getTitle().toString());
                Fragment showFragment = new ShowFragment();
                showFragment.setArguments(bundle);
                mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.main_content, showFragment, null).addToBackStack(String.valueOf(item.getItemId())).commit();

            }
            break;
            case R.id.menu_app_about: {
                bundle.putString("show_text", item.getTitle().toString());
                Fragment showFragment = new ShowFragment();
                showFragment.setArguments(bundle);
                mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.main_content, showFragment, null).addToBackStack(String.valueOf(item.getItemId())).commit();

            }
            case R.id.menu_user_logout: {
                NetworkService.getInstance().logout(mainActivity);
                mainActivity.logout();
            }
            break;
            default:
                return false;
        }

       // close this navigation view
        drawerLayout.closeDrawers();
        return true;
    }
}
