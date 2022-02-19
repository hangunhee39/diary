package org.techtown.diary;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import lib.kingja.switchbutton.SwitchMultiButton;

public class Fragment1 extends Fragment {
    private static final String TAG ="Fragment1";

    RecyclerView recyclerView;
    NoteAdapter adapter;

    Context context;
    OnTabItemSelectedListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView=(ViewGroup) inflater.inflate(R.layout.fragment1,container,false);

        initUI(rootView);

        loadNoteListDate();

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context =context;

        if (context instanceof OnTabItemSelectedListener){
            listener =(OnTabItemSelectedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (context !=null){
            context =null;
            listener=null;
        }
    }


    private void initUI(ViewGroup rootView){

        Button todayWriteButton =rootView.findViewById(R.id.todayWriteButton);
        todayWriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    listener.onTabSelected(1);
                }
            }
        });

        SwitchMultiButton switchButton = rootView.findViewById(R.id.switchButton);
        switchButton.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int position, String tabText) {
                Toast.makeText(context,tabText,Toast.LENGTH_SHORT).show();
                adapter.swichLayout(position);
                adapter.notifyDataSetChanged();
            }
        });

        recyclerView =rootView.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager =new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter=new NoteAdapter();

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnNoteItemClickListener() {
            @Override
            public void onItemClick(NoteAdapter.ViewHolder holder, View view, int position) {
                Note item =adapter.getItem(position);
                Toast.makeText(getContext(),"아이템 선택됨"+item.getContents(),Toast.LENGTH_LONG).show();

                if (listener!=null){
                    listener.showFragment2(item);
                }
            }
        });

    }

    public int loadNoteListDate(){
        AppConstants.println("loadNoteListData called.");
        String sql ="select _id, WEATHER, ADDRESS, LOCATION_X, LOCATION_Y, CONTENTS, MOOD, PICTURE, CREATE_DATE, MODIFY_DATE from "
                + NoteDatabase.TABLE_NOTE+" order by CREATE_DATE desc";

        int recordCount = -1;
        NoteDatabase database =NoteDatabase.getInstance(context);
        if (database !=null){
            Cursor outCuror =database.rawQuery(sql);

            recordCount=outCuror.getCount();
            AppConstants.println("record Count : "+recordCount+"\n");

            ArrayList<Note> items =new ArrayList<Note>();

            for (int i=0; i<recordCount; i++){
                outCuror.moveToNext();

                int _id =outCuror.getInt(0);
                String weather =outCuror.getString(1);
                String address =outCuror.getString(2);
                String locationX =outCuror.getString(3);
                String locationY =outCuror.getString(4);
                String contents =outCuror.getString(5);
                String mood =outCuror.getString(6);
                String picture =outCuror.getString(7);
                String dateStr =outCuror.getString(8);
                String createDateStr =null;
                if (dateStr != null && dateStr.length()>10){
                    try {
                        Date inDate=AppConstants.dateFormat4.parse(dateStr);
                        createDateStr =AppConstants.dateFormat3.format(inDate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    createDateStr="";
                }

                AppConstants.println("#"+i+"->"+_id+","+weather+","+
                        address+","+locationX+","+locationY+","+contents+
                        ","+mood+","+picture+","+createDateStr);
                items.add(new Note(_id, weather,address,locationX, locationY, contents, mood,
                        picture,createDateStr));
            }

            outCuror.close();

            adapter.setItems(items);
            adapter.notifyDataSetChanged();

        }
        return recordCount;
    }
}
