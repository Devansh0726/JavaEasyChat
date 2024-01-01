package com.example.javaeasychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.javaeasychat.FirebaseThings.AndroidUtil;
import com.example.javaeasychat.FirebaseThings.FirebaseUtil;
import com.example.javaeasychat.adapter.ItemChatRecyclerAdapter;
import com.example.javaeasychat.adapter.ItemSearchAdapter;
import com.example.javaeasychat.model.ChatMessageModel;
import com.example.javaeasychat.model.ChatroomModel;
import com.example.javaeasychat.model.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {


    UserModel otherUser;
    TextView OtherUsername;
    String chatroomId;
    ChatroomModel chatroomModel;
    ItemChatRecyclerAdapter chatRecyclerAdapter;

    EditText etEnterMessage;
    RecyclerView rvChat;
    ImageView ivBack;

    ImageButton btnSendMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(),otherUser.getUserId());
        OtherUsername = findViewById(R.id.other_username);
        etEnterMessage = findViewById(R.id.etEnterMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        rvChat = findViewById(R.id.rvChat);
        ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        OtherUsername.setText(otherUser.getUsername());

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etEnterMessage.getText().toString().trim();
                if (message.isEmpty())
                    return;
                sendMessageToUser(message);
            }
        });


        getOrCreateChatroomModel();
        setUpChatRecyclerView();


    }

    void setUpChatRecyclerView(){

        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        chatRecyclerAdapter = new ItemChatRecyclerAdapter(options ,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        rvChat.setLayoutManager(manager);
        rvChat.setAdapter(chatRecyclerAdapter);
        chatRecyclerAdapter.startListening();
        chatRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                rvChat.smoothScrollToPosition(0);
            }
        });
    }

    void sendMessageToUser(String message){

        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);



        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){
                            etEnterMessage.setText("");
                        }
                    }
                });

    }



//THIS METHOD IS FOR CREATING CHATROOM B/W 2 USER FROM WHICH DATA WILL BE SAVE OF 2 USER CONVERSATION IN FIREBASE
    void getOrCreateChatroomModel(){
      FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
          @Override
          public void onComplete(@NonNull Task<DocumentSnapshot> task) {
              if (task.isSuccessful()){
                 chatroomModel = task.getResult().toObject(ChatroomModel.class);
//        IF USER ALREADY HAD CHAT THEN NO NEW ROOM WILL BE CREATED
                 if (chatroomModel==null){
                     chatroomModel = new ChatroomModel(
                             chatroomId,
                             Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                             Timestamp.now(),
                             ""

                     );
                     FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                 }
              }
          }
      });
    }
}