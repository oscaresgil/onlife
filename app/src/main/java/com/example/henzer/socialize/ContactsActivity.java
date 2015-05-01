package com.example.henzer.socialize;

import android.support.v4.app.ListFragment;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ContactsActivity extends ListFragment {
    /*private ListView contacts;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contacts = getListView();
        ArrayList<AllContactsFriends> arreglo = new ArrayList<>();
        arreglo.add(new AllContactsFriends("1231234123412341234","FUCKING PRUEBA",null));
        FlipSettings settings = new FlipSettings.Builder().defaultPage(0).build();
        //contacts.setAdapter(new ContactsAdapter(getActivity(), MainActivity.friends, settings));
        contacts.setAdapter(new ContactsAdapter(getActivity(), arreglo, settings));
        contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AllContactsFriends contact = (AllContactsFriends) contacts.getAdapter().getItem(position);
                Toast.makeText(getActivity(), contact.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.contacts_view, container, false);
        return v;
    }

    class ContactsAdapter extends BaseFlipAdapter<AllContactsFriends> {
        public ContactsAdapter(Context context, List<AllContactsFriends> items, FlipSettings settings) {
            super(context, items, settings);
        }

        @Override
        public View getPage(int i, View view, ViewGroup viewGroup, AllContactsFriends allContactsFriends, AllContactsFriends allContactsFriends2) {
            final ContactsHolder holder;
            if (view == null){
                holder = new ContactsHolder();
                view = getActivity().getLayoutInflater().inflate(R.layout.contacts, viewGroup, false);
                holder.leftAvatar = (ImageView) view.findViewById(R.id.first_image);
                holder.rightAvatar = (ImageView) view.findViewById(R.id.second_image);
                holder.name = (TextView) holder.infoPage.findViewById(R.id.name);
                view.setTag(holder);
            }
            else{
                holder = (ContactsHolder) view.getTag();
            }
            if (i==1){
                holder.leftAvatar.setImageBitmap(allContactsFriends.getImageAvatar());
                if (allContactsFriends2!=null){
                    holder.rightAvatar.setImageBitmap(allContactsFriends2.getImageAvatar());
                }
            }
            else{
                fillContact(holder,i==0? allContactsFriends:allContactsFriends2);
                holder.infoPage.setTag(holder);
                return holder.infoPage;
            }
            return view;
        }

        @Override
        public int getPagesCount() {
            return 0;
        }
    }

    private void fillContact(ContactsHolder holder, AllContactsFriends friend){
        if (friend==null) return;
        holder.infoPage.setBackgroundColor(getResources().getColor(friend.getBackground()));
        holder.name.setText(friend.getName());
    }

    class ContactsHolder{
        ImageView leftAvatar;
        ImageView rightAvatar;
        View infoPage;
        TextView name;
    }*/


}