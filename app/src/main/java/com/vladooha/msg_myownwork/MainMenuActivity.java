package com.vladooha.msg_myownwork;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import java.io.File;

public class MainMenuActivity extends AppCompatActivity {
    // Screen view-elements
    DrawerLayout mainLayout;
    ActionBarDrawerToggle toggleButton;
    ActionBar actBar;
    NavigationView leftMenu;
    Fragment mainFr;

    private static final String MyLogs = "MyLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        // Messages list pre-set
        FragmentManager frManager = getSupportFragmentManager();
        mainFr = frManager.findFragmentById(R.id.fragcont_mainmenu);
        if (null == mainFr) {setTitle(R.string.title_messages);
            mainFr = new MessagesFragment();
            frManager.beginTransaction()
                    .add(R.id.fragcont_mainmenu, mainFr)
                    .commit();
        }

        // Setting the view-elements
        actBar = getSupportActionBar();
        mainLayout = findViewById(R.id.mainmenu_mainlayout);
        toggleButton = new ActionBarDrawerToggle(this, mainLayout, R.string.menu_open, R.string.menu_close);
        leftMenu = findViewById(R.id.leftmenu);

        // Menu pre-set
        mainLayout.addDrawerListener(toggleButton);
        toggleButton.syncState();
        actBar.setDisplayHomeAsUpEnabled(true);
        leftMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_dialogs:
                        mainFr = new MessagesFragment();
                        break;
                    case R.id.menu_search:
                        mainFr = new UserSearchFragment();
                        break;
                    case R.id.menu_log_out:
                        try {
                            File tokenFile = new File(getFilesDir(), getResources().getString(R.string.ext_token_file));
                            if (tokenFile.exists()) {
                                tokenFile.delete();
                            }
                        } catch (Resources.NotFoundException e) {
                            Log.d(MyLogs, "Resource doesn't found =(");
                        } catch (SecurityException e) {
                            Log.d(MyLogs, "File can't be deleted =(");
                        } finally {
                            startActivity(new Intent(getApplicationContext(), LogInActivity.class));
                            finish();
                        }
                        break;
                    default:
                        return false;
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.mainmenu_mainlayout);
                drawer.closeDrawer(GravityCompat.START);

                FragmentManager frManager = getSupportFragmentManager();
                frManager.beginTransaction()
                        .replace(R.id.fragcont_mainmenu, mainFr)
                        .commit();

                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggleButton.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
