package shipdoandem.amytateam.org.shipdoandem.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import shipdoandem.amytateam.org.shipdoandem.SharePref;
import shipdoandem.amytateam.org.shipdoandem.databases.DbContext;
import shipdoandem.amytateam.org.shipdoandem.evenbus.IncreaseCountCartEvent;
import shipdoandem.amytateam.org.shipdoandem.evenbus.UpdateSearchFood;
import shipdoandem.amytateam.org.shipdoandem.pager.Pager;
import shipdoandem.amytateam.org.shipdoandem.utils.BottomNavigationHelper;
import shipdoandem.amytateam.org.shipdoandem.R;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private static final String TAG = MainActivity.class.toString();

    @BindView(R.id.pager)
    ViewPager viewPager;

    @BindView(R.id.tl_tab)
    TabLayout tabLayout;

    Context context;
    FrameLayout circle;
    TextView countTextView;
    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        context = this;
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_dashboard) {
                    EventBus.getDefault().postSticky(new UpdateSearchFood());
                    Intent intent = new Intent(context, SearchFoodActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar_layout);
        //Creating our pager adapter
        Pager adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
    @Override
    protected void onRestart() {
        count = SharePref.instance.getSharedPreferences().getInt("COUNT", 0);
        if (0 < count) {
            countTextView.setText(String.valueOf(count));
            circle.setVisibility(View.VISIBLE);
        } else {
            circle.setVisibility(View.GONE);
        }
        super.onRestart();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.action_cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        circle = (FrameLayout) rootView.findViewById(R.id.fl_noti);
        countTextView = (TextView) rootView.findViewById(R.id.tv_count);
        count = SharePref.instance.getSharedPreferences().getInt("COUNT", 0);
        if (0 < count) {
            countTextView.setText(String.valueOf(count));
            circle.setVisibility(View.VISIBLE);
        } else {
            circle.setVisibility(View.GONE);
        }
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    @Subscribe
    public void doIncrease(IncreaseCountCartEvent event) {
        count = SharePref.instance.getSharedPreferences().getInt("COUNT", 0);
        count += event.getCount();
        SharePref.instance.getSharedPreferences().edit().putInt("COUNT", count).commit();
        Log.d(TAG, String.format("doIncrease: %s", count ));
        if (0 < count) {
            countTextView.setText(String.valueOf(count));
            circle.setVisibility(View.VISIBLE);
        } else {
            circle.setVisibility(View.GONE);
        }
        DbContext.instance.addOrUpdate(event.getFood());
        Log.d(TAG, "doIncrease: ");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cart:
                Log.d(TAG, "onOptionsItemSelected: ");
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
