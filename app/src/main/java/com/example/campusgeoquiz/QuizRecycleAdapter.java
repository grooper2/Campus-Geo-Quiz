package com.example.campusgeoquiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.campusgeoquiz.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import com.example.campusgeoquiz.Product;
import model.Quiz;

public class QuizRecycleAdapter extends RecyclerView.Adapter<QuizRecycleAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;
    private CheckBox chk;

    public QuizRecycleAdapter(Context context, List<Product> productList){
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public QuizRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item,parent, false);
        return new ViewHolder(view, context);
    }



    @Override
    public void onBindViewHolder(@NonNull QuizRecycleAdapter.ViewHolder holder, int position) {
        Product product = productList.get(position);
        String imageUrl;

        imageUrl = product.getImage();

        Picasso.get().load(imageUrl)
                .placeholder(R.drawable.cpu)
                .fit()
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public ImageView image;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);

            context = ctx;
            image = itemView.findViewById(R.id.imageView);
            chk = itemView.findViewById(R.id.checkbox);

            image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    chk.setChecked(true);
                    return true;
                }
            });

        }
    }
}









//package com.example.campusgeoquiz;
//import android.content.Context;
//import android.text.format.DateUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.campusgeoquiz.Product;
//import com.example.campusgeoquiz.R;
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
//
//public class QuizRecycleAdapter extends RecyclerView.Adapter<QuizRecycleAdapter.ViewHolder> {
//
//    private Context context;
//    private List<Product> reflectionList;
//
//    public QuizRecycleAdapter(Context context, List<Product> reflectionList) {
//        this.context = context;
//        this.reflectionList = reflectionList;
//    }
//
//    @NonNull
//    @Override
//    public QuizRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
//
//
//        return new ViewHolder(view, context);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull QuizRecycleAdapter.ViewHolder holder, int position) {
//
//        Product reflection = reflectionList.get(position);
//        String imageUrl;
//        //holder.name.setText(reflection.getUserName());
//        imageUrl = reflection.getImage();
//
//        //use picasso library to download and show image
//        Picasso.get().load(imageUrl)
//                .placeholder(R.drawable.cpu)
//                .fit()
//                .into(holder.image);
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return reflectionList.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder{
//
//        public ImageView image;
//
//        public ViewHolder(@NonNull View itemView, Context ctx) {
//            super(itemView);
//            context = ctx;
//
//            image = itemView.findViewById(R.id.imageView);
//            //name = itemView.findViewById(R.id.username_account);
//
//
//        }
//    }
//}