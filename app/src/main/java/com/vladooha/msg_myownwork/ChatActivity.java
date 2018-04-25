package com.vladooha.msg_myownwork;

import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Vladooha on 11.04.2018.
 */

public class ChatActivity extends AppCompatActivity {
    RecyclerView chatList;
    Resources res;

    private static final String MyLogs = "MyLogs";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        res = getResources();

        setTitle(R.string.title_chat);

        chatList = (RecyclerView) findViewById(R.id.recycle_chat);
        chatList.setLayoutManager(new LinearLayoutManager(this));
        chatList.setAdapter(setResListAdapter());
    }

    // Helping methods
    // TODO: It's mock!
    private ChatActivity.ChatAdapter setResListAdapter() {
       String[] searchResults = new String[2];
        searchResults[0] = "0SASSASSASSASSASSASSASSASSASSASSASSASSASSASSASSASSASSASSASSASSASSAS";
        searchResults[1] = "1LOL";
        return new ChatActivity.ChatAdapter(searchResults);
    }

    // Helping sub-classes
    private class ChatHolder extends RecyclerView.ViewHolder {
        // Screen view-elements
        private TextView message;
        private LinearLayout layout;

        public ChatHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.rec_list_chat, parent, false));

            message = itemView.findViewById(R.id.list_chat_message);
            layout = itemView.findViewById(R.id.list_chat_layout);
        }

        public void bind(String searchResult) {
            if(searchResult.startsWith("0")) {
                layout.setGravity(Gravity.RIGHT);
            }

            Point display = new Point();
            getWindowManager().getDefaultDisplay().getSize(display);
            message.setMaxWidth(display.x / 2 - 10);
            message.setText(searchResult.substring(1));
        }
    }

    private class ChatAdapter extends RecyclerView.Adapter<ChatActivity.ChatHolder> {
        private String[] arr;

        public ChatAdapter(String[] arr) {
            this.arr = arr;
        }

        @Override
        public ChatActivity.ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ChatActivity.ChatHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ChatActivity.ChatHolder holder, int position) {
            String dataBuff = arr[position];
            holder.bind(dataBuff);
        }

        @Override
        public int getItemCount() {
            return arr.length;
        }
    }
}
