package com.cucsijuan.contactmanager;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static int REQUEST_CONTACT = 85;
    private ContactListFragment inboxFragment;
    //private ContactFormFragment fragment2;
    private CallFragment callFragment;
    private static FloatingActionButton fab;
    private static FloatingActionButton fabEdit;
    private FragmentManager manager;
    private static ContactViewFragment contactViewFragment;
    DrawerLayout drawer;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabEdit = (FloatingActionButton) findViewById(R.id.fabEdit);
        inboxFragment = new ContactListFragment();

        callFragment = new CallFragment();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fabEdit.hide();
        manager = getFragmentManager();
        //checkea cual fragment estaba abierto
        if (manager.findFragmentByTag("contactForm") == null) {
            if(manager.findFragmentByTag("callFragment") == null){
                if (manager.findFragmentByTag("editFragment")== null){
                    setFragment(0);
                    navigationView.getMenu().getItem(0).setChecked(true);
                }else{
                    setFragment(4);

                }
            }else{
                setFragment(2);
            }
        } else {
            setFragment(1);
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (manager.findFragmentByTag("contactForm") != null && manager.findFragmentByTag("contactForm").isVisible()) {
                    ContactFormFragment tempForm = (ContactFormFragment) manager.findFragmentByTag("contactForm");
                    if (tempForm.saveContact() == true){
                        setFragment(0);
                    }
                } else if (contactViewFragment!=null && contactViewFragment.isVisible()) {
                        //TODO:terminar esto


                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+ contactViewFragment.getPhone()));

                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);


                }else{
                    setFragment(1);

                }

            }
        });

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(3);
            }
        });


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (manager.findFragmentByTag("contactForm") != null && manager.findFragmentByTag("contactForm").isResumed()) {
                fab.setImageDrawable(getDrawable(R.drawable.ic_dialog_add));
            }else if (manager.findFragmentByTag("editFragment") != null && manager.findFragmentByTag("editFragment").isResumed()){
                fab.setImageDrawable(getDrawable(R.drawable.ic_dialog_add));
            }else if (manager.findFragmentByTag("callFragment") != null && manager.findFragmentByTag("callFragment").isResumed()){
                fab.show();
                navigationView.getMenu().getItem(0).setChecked(true);
            }
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_contactos) {
            setFragment(0);
            manager.popBackStack(manager.getBackStackEntryCount(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fab.show();
        } else if (id == R.id.nav_telefono) {
            setFragment(2);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setFragment(int position) {
        FragmentTransaction fragmentTransaction;
        switch (position) {
            case 0:
                fragmentTransaction = manager.beginTransaction();
                fragmentTransaction.replace(R.id.content_main, inboxFragment);
                fragmentTransaction.commit();
                fab.setImageDrawable(getDrawable(R.drawable.ic_dialog_add));
                break;
            case 1:
                ContactFormFragment fragment2 = new ContactFormFragment();
                getIntent().putExtra("personID", 0);
                fragmentTransaction= manager.beginTransaction();
                fragmentTransaction.replace(R.id.content_main, fragment2, "contactForm");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                fab.setImageDrawable(getDrawable(R.drawable.ic_menu_ok));
                break;
            case 2:

                fragmentTransaction = manager.beginTransaction();

                fragmentTransaction.replace(R.id.content_main, callFragment,"callFragment");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                fab.hide();
                fabEdit.hide();
                break;
            case 3:
                fabEdit.hide();
                ContactFormFragment fragment3 = new ContactFormFragment();
                fragmentTransaction = manager.beginTransaction();

                fragmentTransaction.replace(R.id.content_main, fragment3, "contactForm");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                fab.setImageDrawable(getDrawable(R.drawable.ic_menu_ok));
                break;
            case 4:
                fragmentTransaction = manager.beginTransaction();
                fragmentTransaction.replace(R.id.content_main, contactViewFragment,"editFragment");
                fab.setImageDrawable(getDrawable(R.drawable.ic_dialog_dial));
                fragmentTransaction.commit();
        }
    }

    public static void setFabEditVisible(boolean visible) {
        if (visible){
            fabEdit.show() ;
        }else{
            fabEdit.hide();
        }
    }

    public static FloatingActionButton getFab (){
        return fab;
    }

    public static void setContactViewFragment (ContactViewFragment fragment){
        contactViewFragment = fragment;
    }
}
