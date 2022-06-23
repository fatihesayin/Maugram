package com.example.maugramsocial.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maugramsocial.Adapter.MessageAdapter;
import com.example.maugramsocial.Model.Message;
import com.example.maugramsocial.R;
import com.example.maugramsocial.databinding.ActivityMessageBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {

    MessageAdapter adapter;
    ArrayList<Message> mMessages;

    ActivityMessageBinding binding;
    String senderRoom, receiverRoom;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        recyclerView = findViewById(R.id.recyclerView_activity_message);
        Toolbar toolbar = findViewById(R.id.toolbar_message_activity);
        ImageView btnSend = findViewById(R.id.activity_message_send_message);
        EditText edtMessage = findViewById(R.id.activity_message_message_text);

        setSupportActionBar(toolbar);

        mMessages = new ArrayList<>();

        String userName = getIntent().getStringExtra("userName");
        String receiverUid = getIntent().getStringExtra("id");
        String senderUid = FirebaseAuth.getInstance().getUid();

        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        adapter = new MessageAdapter(this, mMessages, senderRoom, receiverRoom);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        DatabaseReference getmsgRef = FirebaseDatabase.getInstance().getReference().child("Chats")
                .child(senderRoom)
                .child("Messages");
        getmsgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mMessages.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Message message = dataSnapshot.getValue(Message.class);
                    message.setMessageId(dataSnapshot.getKey());
                    mMessages.add(message);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTxt= edtMessage.getText().toString();

                Date date = new Date();
                Message message = new Message(messageTxt, senderUid, date.getTime());

                String randomkey = FirebaseDatabase.getInstance().getReference().push().getKey();

                DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference().child("Chats")
                        .child(senderRoom)
                        .child("Messages")
                        .child(randomkey);
                senderRef.setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        DatabaseReference receiveRef = FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(receiverRoom)
                                .child("Messages")
                                .child(randomkey);
                        receiveRef.setValue(message);
                     }
                });
                edtMessage.setText("");

                HashMap<String, Object> lastMsgObject = new HashMap<>();
                lastMsgObject.put("lastMsg",message.getMessage());
                lastMsgObject.put("lastMsgTime", date.getTime());

                FirebaseDatabase.getInstance().getReference().child("Chats").child(senderRoom).updateChildren(lastMsgObject);
                FirebaseDatabase.getInstance().getReference().child("Chats").child(receiverRoom).updateChildren(lastMsgObject);


            }
        });


        getSupportActionBar().setTitle(userName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}