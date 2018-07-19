package com.vladooha.msg_myownwork;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Vladooha on 25.03.2018.
 */

public class MessagesFragment extends Fragment {
    // Screen view-elements
    //    ListView dialList;
    RecyclerView resList;

    // Constant for croos-activity iteraction
    final static String CONTAINER_UID = "com.vladooha.myMessenger.messageFragment.UID";

    // Resources cahce
    Resources res;

    private static final String MyLogs = "MyLogs";

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        res = getResources();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_messages, container, false);

        getActivity().setTitle(R.string.title_messages);

        resList = (RecyclerView) v.findViewById(R.id.recycle_messages);
        resList.setLayoutManager(new LinearLayoutManager(getActivity()));
        resList.setAdapter(setResListAdapter());
        resList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        return v;
    }

    // Helping methods
    // TODO: It's mock!
    private DialogsAdapter setResListAdapter() {
        UserObj[] dialogs = new UserObj[3];
        dialogs[0] = new UserObj(res);
        dialogs[1] = new UserObj(res);
        dialogs[2] = new UserObj(res);
        return new DialogsAdapter(dialogs);
    }

    // Helping sub-classes
    private class DialogsHolder extends RecyclerView.ViewHolder {
        // Screen view-elements
        private TextView nicknameText;
        private ImageView avatarPic;

        public DialogsHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.rec_list_dialogs, parent, false));

            nicknameText = itemView.findViewById(R.id.list_dial_nick);
            avatarPic = itemView.findViewById(R.id.list_dial_avatar);
        }

        public void bind(final UserObj dialog) {
            String nicknameTextBuff = dialog.getNick();
            if (nicknameTextBuff != null) {
                nicknameText.setText(nicknameTextBuff);
            }
            nicknameTextBuff = null;

            Bitmap avatarPicBuff = dialog.getPic();
            if (avatarPicBuff != null) {
                avatarPic.setImageBitmap(avatarPicBuff);
            }
            avatarPicBuff = null;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    // TODO: It's mock
                    intent.putExtra(CONTAINER_UID, "0");
                    startActivity(intent);
                }
            });
        }
    }

    private class DialogsAdapter extends RecyclerView.Adapter<DialogsHolder> {
        private UserObj[] arr;

        public DialogsAdapter(UserObj[] _arr) {
            arr = _arr;
        }

        @Override
        public DialogsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DialogsHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(DialogsHolder holder, int position) {
            UserObj dataBuff = arr[position];
            holder.bind(dataBuff);
        }

        @Override
        public int getItemCount() {
            return arr.length;
        }
    }
}
