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
import com.psplog.dailycalli.activity.ProfileActivity;
import com.psplog.dailycalli.item.Comment_item;

import java.util.List;

public class RecyclerCommentAdapter extends RecyclerView.Adapter<RecyclerCommentAdapter.ViewHolder> {
    Context context;
    List<Comment_item> items;
    int item_layout;
    int colCount;

    public RecyclerCommentAdapter(Context context, List<Comment_item> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=null;
        v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_comment_item, null);
        v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(v,viewType);
    }


//        this.com_id = com_id;
//        this.user_id = user_id;
//        this.com_txt = com_txt;
//        this.com_date = com_date;
//        this.com_parent = com_parent;
//        this.com_seq = com_seq;
//        this.user_nickname = user_nickname;
//        this.user_img = user_img;
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Comment_item item = items.get(position);
        final ImageView imageVIew = holder.userImg;
        Glide.with(context).load(item.getUser_img()).into(imageVIew);
        holder.tv_comment_content.setText(item.getCom_txt());
        holder.nickname.setText(item.getUser_nickname());
        holder.nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ProfileActivity.class);
                intent.putExtra("user_id",item.getUser_id());
                context.startActivity(intent);
            }
        });
        holder.tv_date.setText(item.getCom_date());
        if(position==getItemCount()-1)
            holder.lay.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImg;
        LinearLayout lay;
        TextView tv_comment_content, tv_date,nickname;
        CardView cardview;
        int viewType;
        public ViewHolder(View itemView,int viewType) {
            super(itemView);
            //itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            lay = (LinearLayout) itemView.findViewById(R.id.liner);
            this.viewType=viewType;
            userImg = (ImageView) itemView.findViewById(R.id.imageView3);
            tv_comment_content = (TextView) itemView.findViewById(R.id.textView_cardItem_com_content);
            nickname =(TextView) itemView.findViewById(R.id.textView_cardItem_nickname);
            tv_date = (TextView) itemView.findViewById(R.id.textView_carditem_date);
            cardview = (CardView) itemView.findViewById(R.id.cardview);
        }
    }
}
