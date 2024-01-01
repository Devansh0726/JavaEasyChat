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
import com.example.javaeasychat.model.ChatroomModel;
import com.example.javaeasychat.model.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public class ItemRecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, ItemRecentChatRecyclerAdapter.ViewHolder> {
    Context context;


    public ItemRecentChatRecyclerAdapter(FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;

    }

    @NonNull
    @Override
    public ItemRecentChatRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_chat_layout, parent, false);


        return new ViewHolder(v);
    }


    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ChatroomModel model) {

        FirebaseUtil.getOtherUserFromChatroom(model.getUserId())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());


                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);
                        holder.tvUsername.setText(otherUserModel.getUsername());
                        if (lastMessageSentByMe)
                            holder.tvLastMessageText.setText("You : " + model.getLastMessage());
                        else
                            holder.tvLastMessageText.setText(model.getLastMessage());
//                        holder.tvLastMessageTime.setText(model.getLastMessageTimestamp().toString());
                        holder.tvLastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(context, ChatActivity.class);
                                AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        });
                    }
                });


    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfile;
        TextView tvUsername, tvLastMessageText, tvLastMessageTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvLastMessageText = itemView.findViewById(R.id.tvLastMessageText);
            tvLastMessageTime = itemView.findViewById(R.id.tvLastMessageTime);
            tvUsername = itemView.findViewById(R.id.tvUsername);
        }
    }
}
