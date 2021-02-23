package com.example.myProject_HealthyRecipesApp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class LinearAdapter_pt extends RecyclerView.Adapter<LinearAdapter_pt.ViewHolder> {
    private final Context myContext;
    private final List<Map<String, Object>> myImgList;
    private final LayoutInflater myLayoutFlater;
    //private final ImageView myImageViewMain;

    public LinearAdapter_pt(Context context, List<Map<String, Object>> imgList) {
        myContext = context;
        myImgList = imgList;
        myLayoutFlater = LayoutInflater.from(context);
        // myImageViewMain = imageViewMain;
    }

    // 建立 ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iv_imgId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // 宣告元件
            iv_imgId = (ImageView) itemView.findViewById(R.id.iv_item02);


        } //end constrctor
    } //end ViewHolder()


    // 連接剛才寫的layout檔案，return一個View
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 連結項目布局檔 picture_layout.xml
        View view = myLayoutFlater.inflate(R.layout.item02_layout, parent, false);
        LinearAdapter_pt.ViewHolder viewHolder = new LinearAdapter_pt.ViewHolder(view);

        return viewHolder;

    }

    // 在這裡取得元件的控制(每個item內的控制)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> map = myImgList.get(position);
        int num = (int) map.get("image");

        // 設置 img 要顯示的內容
        holder.iv_imgId.setImageResource(num);
        holder.iv_imgId.setTag(position);
    }

    // 取得顯示數量，return一個int，通常都會return陣列長度(arrayList.size)
    @Override
    public int getItemCount() {
        return myImgList.size();
    }
}
