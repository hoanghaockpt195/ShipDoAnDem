package shipdoandem.amytateam.org.shipdoandem.adapter.viewholder;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import shipdoandem.amytateam.org.shipdoandem.R;
import shipdoandem.amytateam.org.shipdoandem.databases.DbContext;
import shipdoandem.amytateam.org.shipdoandem.databases.models.Food;

/**
 * Created by ToanTV on 3/19/2017.
 */

public class FoodInCartViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.iv_food)
    ImageView ivfood;
    @BindView(R.id.tv_price)
    TextView price;
    @BindView(R.id.tv_price_old)
    TextView priceOld;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_percent)
    TextView tvPercent;
    @BindView(R.id.iv_increase)
    ImageView ivIncrease;
    @BindView(R.id.iv_decrease)
    ImageView ivDecrease;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.iv_delete)
    ImageView ivDelete;

    public TextView getTvCount() {
        return tvCount;
    }

    public ImageView getIvDelete() {
        return ivDelete;
    }

    public ImageView getIvIncrease() {
        return  ivIncrease;
    }

    public ImageView getIvDecrease() {
        return ivDecrease;
    }

    public FoodInCartViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
    public void bind(Food food) {
//        if(food.getQuantityInCart() == 0){
//            DbContext.getInstance().delete(food);
//            return;
//        }
        Picasso.with(itemView.getContext()).load(food.getUrl()).into(ivfood);
        tvName.setText(food.getName());
        tvPercent.setText(String.format("%s", food.getPercent())+"%");
        price.setText(String.format("%s đ",food.getPriceNew()));
        priceOld.setText(String.format("%s đ",food.getPriceOld()));
        priceOld.setPaintFlags(priceOld.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        tvCount.setText(String.format("%s",food.getQuantityInCart()));
    }
}
