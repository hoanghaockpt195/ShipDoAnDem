package shipdoandem.amytateam.org.shipdoandem.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import shipdoandem.amytateam.org.shipdoandem.R;
import shipdoandem.amytateam.org.shipdoandem.activities.FoodInformationActivity;
import shipdoandem.amytateam.org.shipdoandem.activities.MainActivity;
import shipdoandem.amytateam.org.shipdoandem.adapter.FoodAdapter;
import shipdoandem.amytateam.org.shipdoandem.databases.DbContext;
import shipdoandem.amytateam.org.shipdoandem.evenbus.GetAllFoodFaileEvent;
import shipdoandem.amytateam.org.shipdoandem.evenbus.GetAllFoodHotFailedEvent;
import shipdoandem.amytateam.org.shipdoandem.evenbus.GetAllFoodHotSuccessEvent;
import shipdoandem.amytateam.org.shipdoandem.evenbus.GetAllFoodSuccusEvent;
import shipdoandem.amytateam.org.shipdoandem.evenbus.OnClickItemEvent;
import shipdoandem.amytateam.org.shipdoandem.evenbus.OnClickItemHotEvent;
import shipdoandem.amytateam.org.shipdoandem.evenbus.SendRequestEvent;
import shipdoandem.amytateam.org.shipdoandem.evenbus.SentFood;
import shipdoandem.amytateam.org.shipdoandem.evenbus.TypeRequestEvent;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class MostViewFragment extends Fragment {
    @BindView(R.id.rv_food)
    RecyclerView rvFood;

    @BindView(R.id.rl_most_view)
    RelativeLayout rlMostView;

    @BindView(R.id.iv_oops)
    ImageView ivOops;

    @BindView(R.id.swipeRefreshLayout)
    PullRefreshLayout layout;

    private ProgressDialog progress;

    private FoodAdapter foodAdapter = new FoodAdapter(this.getContext());

    public MostViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_most_view, container, false);
        setupUI(view);
        return view;
    }

    private void setupUI(View view) {
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        ivOops.setBackgroundResource(R.drawable.ic_oops);

        loadAllFood();

        // listen refresh event
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                layout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DbContext.instance.getAllFoodHot();
                    }
                }, 3000);
            }
        });



        rlMostView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbContext.instance.getAllFoodHot();
                layout.setRefreshing(true);
            }
        });

    }

    @Subscribe
    void OnClickItem(OnClickItemEvent event){
        Intent intent = new Intent(getContext(),FoodInformationActivity.class);
        getContext().startActivity(intent);
    }

    public void loadAllFood() {
        if(DbContext.instance.getFoodsHot().size()==0) {
            DbContext.instance.getAllFoodHot();
            progress = ProgressDialog.show(this.getContext(), "Xin chờ",
                    "Đang tải", true);
            progress.show();
        }else {
            rvFood.setAdapter(foodAdapter);
            rvFood.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        }

    }

    @Subscribe
    public void onLoadFoodSuccess(GetAllFoodHotSuccessEvent event) {
        if(progress != null)
            progress.dismiss();
        layout.setRefreshing(false);
        rvFood.setAdapter(foodAdapter);
        rvFood.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        ivOops.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onLoadDataFailed(GetAllFoodHotFailedEvent event) {
        ivOops.setVisibility(View.VISIBLE);
        Toast.makeText(this.getContext(), "Lỗi kết nối. Kiểm tra đường truyền internet!", Toast.LENGTH_SHORT).show();
        progress.dismiss();
        layout.setRefreshing(false);
    }
    @Subscribe
    public void changeButtonMode(SendRequestEvent event){
        if(event.getTypeRequest() == TypeRequestEvent.CHANGE_BT_ADDTOCART){
            foodAdapter.notifyDataSetChanged();
        }
    }
}
