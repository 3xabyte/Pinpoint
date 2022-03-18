package tech.mattmcburnie.pinpoint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private int maxPoints;
    private int selectedAvgPoint;


    private BottomNavigationView.OnNavigationItemSelectedListener navBarListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        /**
         * Creates a new Fragment depending on what the user selects.
         * @param item The item selected in the bottom nav bar.
         * @return Returns true.
         */
        @Override
        public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
            Fragment currentFragment = null;

            switch(item.getItemId()) {
                case R.id.b_nav_bar_1:
                    currentFragment = new LocationFragment();
                    break;
                case R.id.b_nav_bar_2:
                    currentFragment = new DatabaseFragment();
                    break;
                case R.id.b_nav_bar_3:
                    currentFragment = new SettingsFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();
            return true;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        maxPoints = this.convertMaxPoints(prefs.getInt("PointsRecorded", 0));

        BottomNavigationView navBar = findViewById(R.id.bottom_nav_bar);
        navBar.setOnNavigationItemSelectedListener(navBarListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LocationFragment()).commit();

    }

    public int convertMaxPoints(int i) {
        if(i == 0) {
            return 1;
        }
        else {
            return i * 5;
        }
    }

    public void setMaxPoints(int points) {
        this.maxPoints = points;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public void setPoint(int point) {
        this.selectedAvgPoint = point;
    }

    public int getPoint() {
        return this.selectedAvgPoint;
    }




}