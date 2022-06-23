package com.example.maugramsocial.Adapter;

import android.content.Context;
import android.provider.Telephony;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maugramsocial.Model.Message;
import com.example.maugramsocial.R;
import com.example.maugramsocial.databinding.MessageReceiveItemBinding;
import com.example.maugramsocial.databinding.MessageSentItemBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter{

    Context mContext;
    ArrayList<Message> mMessages;

    String senderRoom, receiverRoom;
    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;

    public MessageAdapter(Context mContext, ArrayList<Message> mMessages, String senderRoom, String receiverRoom){

        this.senderRoom=senderRoom;
        this.receiverRoom=receiverRoom;
        this.mContext=mContext;
        this.mMessages=mMessages;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==ITEM_SENT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_sent_item,parent,false);
            return new SentViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_receive_item,parent,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessages.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return ITEM_SENT;
        }
        else {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = mMessages.get(position);

        int reactions[] = new int[]{
            R.drawable.ic_fb_like,
            R.drawable.ic_fb_love,
            R.drawable.ic_fb_laugh,
            R.drawable.ic_fb_wow,
            R.drawable.ic_fb_sad,
            R.drawable.ic_fb_angry
        };

        ReactionsConfig config = new ReactionsConfigBuilder(mContext)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(mContext, config, (pos) -> {
            if (holder.getClass() == SentViewHolder.class){
                SentViewHolder viewHolder = (SentViewHolder) holder;
                viewHolder.binding.sentFeeling.setImageResource(reactions[pos]);
                viewHolder.binding.sentFeeling.setVisibility(View.VISIBLE);
            }else {
                ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
                viewHolder.binding.receiveFeeling.setImageResource(reactions[pos]);
            }

            message.setFeeling(pos);

            FirebaseDatabase.getInstance().getReference()
                    .child("Chats")
                    .child(senderRoom)
                    .child("Messages")
                    .child(message.getMessageId()).setValue(message);

            FirebaseDatabase.getInstance().getReference()
                    .child("Chats")
                    .child(receiverRoom)
                    .child("Messages")
                    .child(message.getMessageId()).setValue(message);

            return true; // true is closing popup, false is requesting a new selection
        });

        if (holder.getClass() == SentViewHolder.class){
            SentViewHolder viewHolder = (SentViewHolder)holder;
            viewHolder.binding.messageSentItemText.setText(message.getMessage());

            if(message.getFeeling() >= 0){
                //message.setFeeling(reactions[(int) message.getFeeling()]);
                viewHolder.binding.sentFeeling.setImageResource(reactions[(int)message.getFeeling()]);
                viewHolder.binding.sentFeeling.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.binding.sentFeeling.setVisibility(View.GONE);
            }

            viewHolder.binding.messageSentItemText.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });
        }
        else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.binding.messageReceiveItemText.setText(message.getMessage());

            if(message.getFeeling() >= 0){
                message.setFeeling(reactions[(int)message.getFeeling()]);
                viewHolder.binding.receiveFeeling.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.binding.receiveFeeling.setVisibility(View.GONE);
            }

            viewHolder.binding.messageReceiveItemText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder {
        MessageSentItemBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = MessageSentItemBinding.bind(itemView);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        MessageReceiveItemBinding binding;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = MessageReceiveItemBinding.bind(itemView);
        }
    }
}
