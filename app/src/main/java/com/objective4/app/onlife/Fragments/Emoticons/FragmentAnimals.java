package com.objective4.app.onlife.Fragments.Emoticons;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.objective4.app.onlife.Adapters.AdapterEmoticon;
import com.objective4.app.onlife.BlockActivity.ActivityFriendBlock;
import com.objective4.app.onlife.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentAnimals extends Fragment {
    private List<String> emoticonImages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emoticonImages = new ArrayList<>();
        for (int i=1; i<getActivity().getResources().getInteger(R.integer.emoticon_animal_number)+1; i++){
            emoticonImages.add("a"+i);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GridView gridView = (GridView) getActivity().getLayoutInflater().inflate(R.layout.layout_emoticon,container,false);
        final AdapterEmoticon adapterEmoticon = new AdapterEmoticon(getActivity(),emoticonImages);
        gridView.setAdapter(adapterEmoticon);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((ActivityFriendBlock) getActivity()).setImage(adapterEmoticon.getItem(position));
            }
        });
        return gridView;
    }
}
