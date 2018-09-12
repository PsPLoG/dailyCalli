package com.psplog.dailycalli.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.psplog.dailycalli.R;
import com.psplog.dailycalli.activity.LearnContentActivity;
import com.psplog.dailycalli.item.CalliLearn_Item;

import java.util.List;

public class RecyclerLearnAdapter extends RecyclerView.Adapter<RecyclerLearnAdapter.ViewHolder> {
    Context context;
    List<CalliLearn_Item> items;
    int item_layout;
    int colCount;

    public RecyclerLearnAdapter(Context context, List<CalliLearn_Item> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=null;
        v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_learn_item, null);
        v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(v,viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CalliLearn_Item item = items.get(position);
        final ImageView imageVIew = holder.guide_img;
        holder.lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,LearnContentActivity.class);
                intent.putExtra("guide_id",item.getGuide_id());
                context.startActivity(intent);
            }
        });
        Glide.with(context).load(item.getGuide_img()).into(imageVIew);
        holder.tv_learn_title.setText(item.getGuide_title());
        String tag="";
        for(int i=0;i<item.getGuide_tag().size();i++)
            tag+="#"+item.getGuide_tag().get(i)+" ";
        holder.tv_learn_tag.setText(tag);

        for(int i=0;i<item.getSubItem().size();i++)
            Glide.with(context).load(item.getSubItem().get(i).getCalli_img()).into(holder.t[i]);

    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView guide_img;
        ImageView t[];
        TextView tv_learn_title, tv_learn_tag;
        CardView cardview;
        int viewType;
        LinearLayout lay;
        public ViewHolder(View itemView,int viewType) {
            super(itemView);
            //itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            this.viewType=viewType;
            lay = itemView.findViewById(R.id.listitem);
            t=new ImageView[6];
            guide_img = (ImageView) itemView.findViewById(R.id.trace_img);
            tv_learn_title = (TextView) itemView.findViewById(R.id.tv_learn_title);
            tv_learn_tag = (TextView) itemView.findViewById(R.id.tv_learn_tag);
            cardview = (CardView) itemView.findViewById(R.id.cardview);

            t[0]=(ImageView) itemView.findViewById(R.id.learn_img_1);
            t[1]=(ImageView) itemView.findViewById(R.id.learn_img_2);
            t[2]=(ImageView) itemView.findViewById(R.id.learn_img_3);
            t[3]=(ImageView) itemView.findViewById(R.id.learn_img_4);
            t[4]=(ImageView) itemView.findViewById(R.id.learn_img_5);
            t[5]=(ImageView) itemView.findViewById(R.id.learn_img_6);
        }
    }
}
