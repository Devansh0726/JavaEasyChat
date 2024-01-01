package com.example.javaeasychat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javaeasychat.ChatActivity;
import com.example.javaeasychat.FirebaseThings.AndroidUtil;
import com.example.javaeasychat.FirebaseThings.FirebaseUtil;
import com.example.javaeasychat.R;
import com.example.javaeasychat.model.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public class ItemSearchAdapter extends FirestoreRecyclerAdapter<UserModel,ItemSearchAdapter.ViewHolder> {
    Context context;


    public ItemSearchAdapter(FirestoreRecyclerOptions<UserModel> options ,Context context){
        super(options);
        this.context = context;

    }
    @NonNull
    @Override
    public ItemSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchuser_layout, parent, false);


        return new ViewHolder(v);
    }


    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull UserModel model) {

        holder.tvUsername.setText(model.getUsername());
        holder.tvPhone.setText(model.getPhone());
        if (model.getUserId().equals(FirebaseUtil.currentUserId())){
            holder.tvUsername.setText(model.getUsername()+" (Me)");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                AndroidUtil.passUserModelAsIntent(intent,model);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfile;
        TextView tvUsername, tvPhone;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvPhone = itemView.findViewById(R.id.tvPhoneNumber);
            tvUsername = itemView.findViewById(R.id.tvUsername);
        }
    }
}
