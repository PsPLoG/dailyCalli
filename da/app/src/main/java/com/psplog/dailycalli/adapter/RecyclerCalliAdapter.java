package com.psplog.dailycalli.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.psplog.dailycalli.R;
import com.psplog.dailycalli.activity.CaillContentActivity;
import com.psplog.dailycalli.activity.MoreCalliActivity;
import com.psplog.dailycalli.activity.ProfileActivity;
import com.psplog.dailycalli.item.Cali_Item;

import java.util.List;

public class RecyclerCalliAdapter extends RecyclerView.Adapter<RecyclerCalliAdapter.ViewHolder> {
    Context context;
    List<Cali_Item> items;
    int item_layout;
    int colCount;

    final int VIEWTYPE_HEADER = 1;
    final int VIEWTYPE_CONTENT = 0;


    public RecyclerCalliAdapter(Context context, List<Cali_Item> items, int item_layout, int colCount) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
        if(colCount==3);
        else if(colCount!=4)
        {
            colCount=1;
        }
        this.colCount = colCount;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=null;

        if(viewType == VIEWTYPE_CONTENT)
        {
            if(colCount!=4 &&colCount!=3 )
                v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_calli_item, null);
            else
                v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_newcalli_item, null);
            v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        }
        else if(viewType >= VIEWTYPE_HEADER)
        {
            v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_header_item, null);
            v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        }
        return new ViewHolder(v,viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(holder.viewType >= VIEWTYPE_HEADER)
        {
            if(holder.viewType == 1)
                holder.tv_nickname.setText("이번주 최고 작품 ");
            else if(holder.viewType == 2)
                holder.tv_nickname.setText("요즘 핫한 캘리그라피 ");
            else if(holder.viewType == 3)
                holder.tv_nickname.setText("최신 캘리그라피 ");
            else if(holder.viewType == 4)
                holder.tv_nickname.setText("김민석의 추천! ");

            holder.tv_calli_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,MoreCalliActivity.class);
                    intent.putExtra("order",holder.viewType);
                    context.startActivity(intent);
                }
            });

        } else if(holder.viewType == VIEWTYPE_CONTENT) {
            int calPosition;
            if(colCount==4) {
                calPosition = position - ((position / (colCount+1)) + 1);
//                if((1+position)%(colCount+1)==0)// 5배수일때 숫자증가
//                    calPosition++;
            }
            else
                calPosition=position;

            Log.d("asd","잘못된 p:"+position+" col:"+colCount+" calPositon:"+calPosition);
            if(items.size()==0)
                return;
            if(items.get(calPosition).calli_id==-1) {
                Log.d("asd","잘못된 아이템");
                return;
            }
            //asd -1
            final Cali_Item item = items.get(calPosition);
            Log.d("asd","caid"+item.getCalli_id());
            final ImageView imageVIew = holder.image;
            final ImageView imageVIew2 = holder.userImg;
            Glide.with(context).load(item.getCalli_img()).into(imageVIew);
           // if((item.getUser_img().equals("")|| item.getUser_img()==null || imageVIew2==null)==true)
            if(!(item.getUser_img()==null) || item.getUser_img().equals(""))
                Glide.with(context).load(item.getUser_img()).into(imageVIew2);
            imageVIew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,CaillContentActivity.class);
                    intent.putExtra("postid",item.getCalli_id());
                    context.startActivity(intent);
                }
            });
            holder.tv_calli_txt.setText(item.getCalli_txt());
            holder.tv_nickname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,ProfileActivity.class);
                    intent.putExtra("user_id",item.getUser_id());
                    context.startActivity(intent);
                }
            });
            holder.tv_nickname.setText(item.getUser_nickname());
            holder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, item.getTitle(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(colCount==3)
            return  VIEWTYPE_CONTENT;
        else if(position%5==0 && colCount==4)
            return VIEWTYPE_HEADER+position/colCount;
        else
            return  VIEWTYPE_CONTENT;
    }

    @Override
    public int getItemCount() {
        if(colCount==4)
        return this.items.size()+colCount;
        else return this.items.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image,userImg;
        TextView tv_nickname, tv_calli_txt;
        CardView cardview;
        int viewType;
        public ViewHolder(View itemView,int viewType) {
            super(itemView);
            //itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            this.viewType=viewType;
            if(viewType >= VIEWTYPE_HEADER)
            {
                tv_nickname = itemView.findViewById(R.id.tv_titile);
                tv_calli_txt =  itemView.findViewById(R.id.tv_more);
            }
            else if(viewType == VIEWTYPE_CONTENT) {
                userImg =(ImageView) itemView.findViewById(R.id.tv_carditem_userimg);
                image = (ImageView) itemView.findViewById(R.id.imageview_carditem_calli);
                tv_calli_txt = (TextView) itemView.findViewById(R.id.textview_carditem_content);
                tv_nickname = (TextView) itemView.findViewById(R.id.textView_cardItem_nickname);
                cardview = (CardView) itemView.findViewById(R.id.cardview);
            }
        }
    }
}
