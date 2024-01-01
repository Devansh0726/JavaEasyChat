package com.example.javaeasychat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.javaeasychat.FirebaseThings.FirebaseUtil;
import com.example.javaeasychat.adapter.ItemSearchAdapter;
import com.example.javaeasychat.model.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchUserActivity extends AppCompatActivity {

    ImageView ivBack;
    EditText etSearch;
    ImageView ivSearchBtn;
    RecyclerView rvSearch;
    ItemSearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        ivBack = findViewById(R.id.ivBack);
        etSearch = findViewById(R.id.etSearch);
        ivSearchBtn = findViewById(R.id.ivSearchBtn);
        rvSearch = findViewById(R.id.rvSearch);

        etSearch.requestFocus();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ivSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchTerm = etSearch.getText().toString();
                if (searchTerm.isEmpty() || searchTerm.length()<3){
                    etSearch.setError("Invalid Username");
                }
                setUpSearchRecyclerView(searchTerm);
            }
        });

    }

    void setUpSearchRecyclerView(String searchTerms){
       Query query = FirebaseUtil.allUserCollectionReferences()
               .whereGreaterThanOrEqualTo("username", searchTerms);

       FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
               .setQuery(query, UserModel.class).build();

        adapter = new ItemSearchAdapter(options ,getApplicationContext());
        rvSearch.setLayoutManager(new LinearLayoutManager(this));
        rvSearch.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter!=null){
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter!=null){
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }
}