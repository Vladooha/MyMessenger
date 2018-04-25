package com.vladooha.msg_myownwork;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Vladooha on 01.04.2018.
 */

public class UserSearchFragment extends Fragment {
    // Screen view-elements
    RecyclerView userList;

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
        View v = inflater.inflate(R.layout.fragment_user_search, container, false);

        getActivity().setTitle(R.string.title_user_search);
//        dialList = v.findViewById(R.id.list_dialogs);
//        dialList.setAdapter(new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, res.getStringArray(R.array.monthlist)));

        userList = (RecyclerView) v.findViewById(R.id.recycle_user_search);
        userList.setLayoutManager(new LinearLayoutManager(getActivity()));
        userList.setAdapter(setResListAdapter());

        return v;
    }

    // Helping methods
    // TODO: It's mock!
    private UserSearchResultAdapter setResListAdapter() {
        UserObj[] searchResults = new UserObj[2];
        searchResults[0] = new UserObj(res);
        searchResults[1] = new UserObj(res);
        return new UserSearchFragment.UserSearchResultAdapter(searchResults);
    }

    // Helping sub-classes
    private class MessagesHolder extends RecyclerView.ViewHolder {
        // Screen view-elements
        private TextView nicknameText;
        private TextView birthdayText;
        private ImageView avatarPic;

        public MessagesHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.rec_list_dialogs, parent, false));

            nicknameText = itemView.findViewById(R.id.list_user_search_nick);
            birthdayText = itemView.findViewById(R.id.list_user_search_birth);
            avatarPic = itemView.findViewById(R.id.list_user_search_avatar);
        }

        public void bind(UserObj searchResult) {
            String nicknameTextBuff = searchResult.getNick();
            if (nicknameTextBuff != null) {
                nicknameText.setText(nicknameTextBuff);
            }

            Calendar birth = searchResult.getBirth();
            if (birth != null) {
                String birthdayTextBuff = birth.toString();
                if (birthdayTextBuff != null) {
                    birthdayText.setText(res.getText(R.string.birth_form).toString() + birthdayTextBuff);
                } else {
                    birthdayText.setText(res.getText(R.string.birth_form).toString()
                            + res.getText(R.string.birth_absent).toString());
                }
            }

            Bitmap avatarPicBuff = searchResult.getPic();
            if (avatarPicBuff != null) {
                avatarPic.setImageBitmap(avatarPicBuff);
            }
        }
    }

    private class UserSearchResultAdapter extends RecyclerView.Adapter<UserSearchFragment.MessagesHolder> {
        private UserObj[] arr;

        public UserSearchResultAdapter(UserObj[] _arr) {
            arr = _arr;
        }

        @Override
        public UserSearchFragment.MessagesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UserSearchFragment.MessagesHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(UserSearchFragment.MessagesHolder holder, int position) {
            UserObj dataBuff = arr[position];
            holder.bind(dataBuff);
        }

        @Override
        public int getItemCount() {
            return arr.length;
        }
    }
}