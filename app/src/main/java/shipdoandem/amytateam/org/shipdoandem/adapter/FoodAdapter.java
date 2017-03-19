package shipdoandem.amytateam.org.shipdoandem.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import shipdoandem.amytateam.org.shipdoandem.R;
import shipdoandem.amytateam.org.shipdoandem.adapter.viewholder.FoodViewHolder;
import shipdoandem.amytateam.org.shipdoandem.databases.DbContext;
import shipdoandem.amytateam.org.shipdoandem.databases.models.Food;
import shipdoandem.amytateam.org.shipdoandem.databases.models.FoodRespon;
import shipdoandem.amytateam.org.shipdoandem.evenbus.IncreaseCountCartEvent;
import shipdoandem.amytateam.org.shipdoandem.evenbus.SentFoodEvent;

/**
 * Created by DUC THANG on 3/16/2017.
 */

public class FoodAdapter extends RecyclerView.Adapter<FoodViewHolder> {
    private Context context;
    private ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void clickItem();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FoodAdapter(Context context) {
        this.context = context;
    }

    public Context getContext() {

        return context;
    }

    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = android.view.LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_food, parent, false);
        FoodViewHolder foodViewHolder = new FoodViewHolder(itemView);
        return foodViewHolder;
    }

    @Override
    public void onBindViewHolder(FoodViewHolder holder, int position) {
        final Food food = DbContext.getInstance().allFoods().get(position);
        holder.bind(food);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener != null)
                {
                    EventBus.getDefault().postSticky(new SentFoodEvent(food));
                    itemClickListener.clickItem();
                }
            }
        });
        holder.getBtAddToCart().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new IncreaseCountCartEvent(food));
            }
        });
    }
    @Override
    public int getItemCount() {
        return DbContext.getInstance().allFoods().size();
    }
}
