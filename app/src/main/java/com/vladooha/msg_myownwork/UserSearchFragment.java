package com.vladooha.msg_myownwork;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Vladooha on 01.04.2018.
 */

public class UserSearchFragment extends Fragment {
    // Variables for search
    String request = null;

    // Screen view-elements
    RecyclerView userList;
    SearchView searcher;

    // Resources cahce
    Resources res;

    // Containers for intents
    public static final String CONTAINER_UID = "com.vladooha.myMessenger.userSearchFragment.UID";

    // Log constants
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

        searcher = (SearchView) v.findViewById(R.id.search_user_find);
        searcher.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request = searcher.getQuery().toString();
                userList.swapAdapter(setResListAdapter(), true);
                Log.d(MyLogs, "OnSearchClickListener()");
            }
        });
        searcher.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                request = searcher.getQuery().toString();
                userList.setLayoutManager(new LinearLayoutManager(getActivity()));
                userList.setAdapter(setResListAdapter());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return v;
    }

    // Helping methods
    private UserSearchResultAdapter setResListAdapter() {
        if (request != null && request.length() > 2) {
            Log.d(MyLogs, "User searcher got a request");
            WebServer webserver = new WebServer();
            webserver.setResources(res, getActivity().getFilesDir());
            String serverAnswer = null;
            try {
                serverAnswer = webserver.makeAuthRequest(res.getString(R.string.cmd_find),
                        res.getString(R.string.key_nick) + request);
            } catch (NotBindedException e) {
                e.printStackTrace();
                return new UserSearchResultAdapter(new UserObj[0]);
            }

            if (serverAnswer != null && serverAnswer.startsWith(res.getString(R.string.key_nick)))
            {
                Log.d(MyLogs, "We got users info");
                String[] searchResultUnform = serverAnswer.split(":");
                ArrayList<UserObj> searchRes = new ArrayList<>();
                for (int i = 0; i < searchResultUnform.length - 2; ++i) {
                    UserObj userBuff = new UserObj(res);
                    userBuff.setUserInfo(
                            searchResultUnform[i].replace("|", ":") + ":");
                    searchRes.add(userBuff);
                }
                UserObj[] searchResults = searchRes.toArray(new UserObj[searchRes.size()]);
                Log.d(MyLogs, String.valueOf(searchResults.length) + " user's found");
                for (UserObj user : searchResults) {
                    Log.d(MyLogs, "User: " + user.getNick());
                }
                return new UserSearchFragment.UserSearchResultAdapter(searchResults);
            } else {
                Log.d(MyLogs, "Bad server's answer");
                return new UserSearchFragment.UserSearchResultAdapter(new UserObj[0]);
            }

        } else {
            Log.d(MyLogs, "User searcher got bad request");
            return new UserSearchFragment.UserSearchResultAdapter(new UserObj[0]);
        }
    }

    // Helping sub-classes
    private class SearchResultHolder extends RecyclerView.ViewHolder {
        // Screen view-elements
        private TextView nicknameText;
        private ImageView avatarPic;
        private String id;

        public SearchResultHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.rec_list_users, parent, false));

            nicknameText = itemView.findViewById(R.id.list_user_search_nick);
            avatarPic = itemView.findViewById(R.id.list_user_search_avatar);
            id = null;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (id != null) {
                        Intent intent = new Intent(getContext(), UserPageActivity.class);
                        // TODO: It's mock
                        intent.putExtra(CONTAINER_UID, id);
                        startActivity(intent);
                    }
                }
            });
        }

        public void bind(UserObj searchResult) {
            String nicknameTextBuff = searchResult.getNick();
            if (nicknameTextBuff != null) {
                nicknameText.setText(nicknameTextBuff);
            }

            Bitmap avatarPicBuff = searchResult.getPic();
            if (avatarPicBuff != null) {
                avatarPic.setImageBitmap(avatarPicBuff);
            }

            id = searchResult.getId();
        }
    }

    private class UserSearchResultAdapter extends RecyclerView.Adapter<SearchResultHolder> {
        private UserObj[] arr;

        public UserSearchResultAdapter(UserObj[] _arr) {
            arr = _arr;
        }

        @Override
        public SearchResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SearchResultHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(SearchResultHolder holder, int position) {
            UserObj dataBuff = arr[position];
            holder.bind(dataBuff);
        }

        @Override
        public int getItemCount() {
            if (arr != null) {
                return arr.length;
            } else {
                return 0;
            }
        }
    }
}