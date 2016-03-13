package com.okapi.stalker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.StudentActivity;
import com.okapi.stalker.data.DataBaseHandler;
import com.okapi.stalker.data.storage.type.Student;
import com.okapi.stalker.fragment.adapters.MyFriendsAdapter;

import java.io.Serializable;

public class FriendsFragment extends Fragment {

    MyFriendsAdapter myFriendsAdapter;

    public FriendsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myFriendsAdapter = new MyFriendsAdapter(getActivity());
        DataBaseHandler.myFriendsAdapter = myFriendsAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.list_friends);
        listView.setAdapter(myFriendsAdapter);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> a, View v, int position, long l) {
                        Intent intent = new Intent(getActivity(), StudentActivity.class);
                        intent.putExtra("student", (Serializable) a.getAdapter().getItem(position));
                        getActivity().startActivity(intent);
                    }
                });
        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, getString(R.string.remove_friend));
        menu.add(0, v.getId(), 0, getString(R.string.remove_all));
    }
    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        if(item.getTitle()==getString(R.string.remove_friend)){
            Student student = (Student)myFriendsAdapter.getItem(info.position);
            DataBaseHandler db = new DataBaseHandler(getActivity());
            db.deleteFriend(student.key());
            Toast.makeText(getContext(), student.name + getString(R.string.xxx_has_removed), Toast.LENGTH_LONG).show();
        }
        else if(item.getTitle()==getString(R.string.remove_all)){
            DataBaseHandler db = new DataBaseHandler(getActivity());
            db.deleteAllFriends();
            Toast.makeText(getContext(), getString(R.string.all_friends_removed), Toast.LENGTH_LONG).show();
        }else{
            return false;
        }
        return true;
    }


}
