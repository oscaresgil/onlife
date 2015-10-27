package com.objective4.app.onlife.Fragments.Emoticons;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.objective4.app.onlife.Adapters.AdapterEmoticon;
import com.objective4.app.onlife.BlockActivity.ActivityBlockBase;
import com.objective4.app.onlife.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentHands extends Fragment{
    private List<String> emoticonImages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emoticonImages = new ArrayList<>();
        for (int i=1; i<getActivity().getResources().getInteger(R.integer.emoticon_hand_number)+1; i++){
            emoticonImages.add("hand_"+i);
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
                ((ActivityBlockBase) getActivity()).setImage(adapterEmoticon.getItem(position));
            }
        });
        return gridView;
    }
}
