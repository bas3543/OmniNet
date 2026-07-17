package com.omninet.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.omninet.R;
import com.omninet.ui.chat.ChatsFragment;
import com.omninet.ui.contacts.ContactsFragment;
import com.omninet.ui.feed.StatusFragment;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private BottomNavigationView bottomNav;
    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("OmniNet");

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        bottomNav = findViewById(R.id.bottomNav);

        setupFragments();
        setupViewPager();
        setupBottomNavigation();
    }

    private void setupFragments() {
        fragmentList.add(new ChatsFragment());
        fragmentList.add(new StatusFragment());
        fragmentList.add(new ContactsFragment());
    }

    private void setupViewPager() {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Chats");
        tabLayout.getTabAt(1).setText("Status");
        tabLayout.getTabAt(2).setText("Contacts");
    }

    private void setupBottomNavigation() {
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_chat) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (itemId == R.id.nav_status) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (itemId == R.id.nav_contacts) {
                viewPager.setCurrentItem(2);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class PagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        PagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
