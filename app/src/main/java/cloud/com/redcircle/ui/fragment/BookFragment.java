package cloud.com.redcircle.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import cloud.com.redcircle.R;
import cloud.com.redcircle.api.HttpRequestHandler;
import cloud.com.redcircle.api.RedCircleManager;
import cloud.com.redcircle.ui.PinnedSectionListView;
import cloud.com.redcircle.utils.AccountUtils;
import io.rong.imkit.RongIM;

/**
 * Created by zhan on 16/4/25.
 */
public class BookFragment extends ListFragment implements View.OnClickListener, AccountUtils.OnAccountListener {

    protected JSONObject mUser;
    protected JSONArray mData;
    protected boolean mIsLogin;

    @Override
    public void onLogout() {

    }

    @Override
    public void onLogin(JSONObject member) {

    }

    static class SimpleAdapter extends ArrayAdapter<Item> implements PinnedSectionListView.PinnedSectionListAdapter {

        private static final int[] COLORS = new int[] {
                R.color.green_light, R.color.orange_light,
                R.color.blue_light, R.color.red_light };

        public SimpleAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
            generateDataset(new JSONArray(), false);

        }

        public void generateDataset(JSONArray data, boolean clear)  {
            if (clear) clear();


            try {
                final int  sectionsNumber = data.length();

                int sectionPosition = 0, listPosition = 0;
                prepareSections(sectionsNumber);
                for (char i=0; i<sectionsNumber; i++) {
                    JSONObject ffriends = data.getJSONObject(i);
                    JSONObject friend = ffriends.getJSONObject("friend");
                    Item section = new Item(Item.SECTION, friend.getString("friendPhone"));
                    section.sectionPosition = sectionPosition;
                    section.listPosition = listPosition++;
                    onSectionAdded(section, sectionPosition);
                    add(section);

                    JSONArray friends = ffriends.getJSONArray("ffriend");
                    final int itemsNumber = friends.length();
                    for (int j=0;j<itemsNumber;j++) {
                        JSONObject ffriend = friends.getJSONObject(j);
                        Item item = new Item(Item.ITEM, ffriend.getString("mePhone"));
                        item.sectionPosition = sectionPosition;
                        item.listPosition = listPosition++;
                        add(item);
                    }

                    sectionPosition++;

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }




        }

        public void generateDataset(char from, char to, boolean clear) {

            if (clear) clear();

            final int sectionsNumber = to - from + 1;
            prepareSections(sectionsNumber);

            int sectionPosition = 0, listPosition = 0;
            for (char i=0; i<sectionsNumber; i++) {
                Item section = new Item(Item.SECTION, String.valueOf((char)('A' + i)));
                section.sectionPosition = sectionPosition;
                section.listPosition = listPosition++;
                onSectionAdded(section, sectionPosition);
                add(section);

                final int itemsNumber = (int) Math.abs((Math.cos(2f*Math.PI/3f * sectionsNumber / (i+1f)) * 25f));
                for (int j=0;j<itemsNumber;j++) {
                    Item item = new Item(Item.ITEM, section.text.toUpperCase(Locale.ENGLISH) + " - " + j);
                    item.sectionPosition = sectionPosition;
                    item.listPosition = listPosition++;
                    add(item);
                }

                sectionPosition++;
            }
        }

        protected void prepareSections(int sectionsNumber) { }
        protected void onSectionAdded(Item section, int sectionPosition) { }

        @Override public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTextColor(Color.DKGRAY);
            view.setTag("" + position);
            Item item = getItem(position);
            if (item.type == Item.SECTION) {
                //view.setOnClickListener(PinnedSectionListActivity.this);
                view.setBackgroundColor(parent.getResources().getColor(COLORS[item.sectionPosition % COLORS.length]));
            }
            return view;
        }

        @Override public int getViewTypeCount() {
            return 2;
        }

        @Override public int getItemViewType(int position) {
            return getItem(position).type;
        }

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return viewType == Item.SECTION;
        }

    }

    static class FastScrollAdapter extends SimpleAdapter implements SectionIndexer {

        private Item[] sections;

        public FastScrollAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
        }

        @Override protected void prepareSections(int sectionsNumber) {
            sections = new Item[sectionsNumber];
        }

        @Override protected void onSectionAdded(Item section, int sectionPosition) {
            sections[sectionPosition] = section;
        }

        @Override public Item[] getSections() {
            return sections;
        }

            @Override public int getPositionForSection(int section) {
            if (section >= sections.length) {
                section = sections.length - 1;
            }
            return sections[section].listPosition;
        }

        @Override public int getSectionForPosition(int position) {
            if (position >= getCount()) {
                position = getCount() - 1;
            }
            return getItem(position).sectionPosition;
        }

    }

    static class Item {

        public static final int ITEM = 0;
        public static final int SECTION = 1;

        public final int type;
        public final String text;

        public int sectionPosition;
        public int listPosition;

        public Item(int type, String text) {
            this.type = type;
            this.text = text;
        }

        @Override public String toString() {
            return text;
        }

    }

    private boolean hasHeaderAndFooter;
    private boolean isFastScroll;
    private boolean addPadding;
    private boolean isShadowVisible = true;
    private int mDatasetUpdateCount;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIsLogin = AccountUtils.isLogined(getActivity());
        if (mIsLogin) {
            mUser = AccountUtils.readLoginMember(getActivity());
        }
        AccountUtils.registerAccountListener(this);
        setHasOptionsMenu(true);



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book, container, false);
        if (savedInstanceState != null) {
            isFastScroll = savedInstanceState.getBoolean("isFastScroll");
            addPadding = savedInstanceState.getBoolean("addPadding");
            isShadowVisible = savedInstanceState.getBoolean("isShadowVisible");
            hasHeaderAndFooter = savedInstanceState.getBoolean("hasHeaderAndFooter");
        }


        return rootView;




    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        SimpleAdapter adapter = (SimpleAdapter) getListAdapter();

        if (adapter == null) {
            initializeHeaderAndFooter();
            initializeAdapter();
            initializePadding();
            getFriends();
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("isFastScroll", isFastScroll);
        outState.putBoolean("addPadding", addPadding);
        outState.putBoolean("isShadowVisible", isShadowVisible);
        outState.putBoolean("hasHeaderAndFooter", hasHeaderAndFooter);
    }




    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Item item = (Item) getListView().getAdapter().getItem(position);
        if (item != null) {

            //启动会话界面
            if (RongIM.getInstance() != null)
                RongIM.getInstance().startPrivateChat(this.getActivity(), item.text, item.text);

            Toast.makeText(this.getActivity(), "Item " + position + ": " + item.text, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this.getActivity(), "Item " + position, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main,menu);
        super.onCreateOptionsMenu(menu, inflater);

        menu.getItem(0).setChecked(isFastScroll);
        menu.getItem(1).setChecked(addPadding);
        menu.getItem(2).setChecked(isShadowVisible);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_fastscroll:
                isFastScroll = !isFastScroll;
                item.setChecked(isFastScroll);
                initializeAdapter();
                break;
            case R.id.action_addpadding:
                addPadding = !addPadding;
                item.setChecked(addPadding);
                initializePadding();
                break;
            case R.id.action_showShadow:
                isShadowVisible = !isShadowVisible;
                item.setChecked(isShadowVisible);
                ((PinnedSectionListView)getListView()).setShadowVisible(isShadowVisible);
                break;
            case R.id.action_showHeaderAndFooter:
                hasHeaderAndFooter = !hasHeaderAndFooter;
                item.setChecked(hasHeaderAndFooter);
                initializeHeaderAndFooter();
                break;
            case R.id.action_updateDataset:
                updateDataset();
                break;
        }
        return true;
    }

    private void updateDataset() {
        mDatasetUpdateCount++;
        SimpleAdapter adapter = (SimpleAdapter) getListAdapter();
        switch (mDatasetUpdateCount % 4) {
            case 0: adapter.generateDataset('A', 'B', true); break;
            case 1: adapter.generateDataset('C', 'M', true); break;
            case 2: adapter.generateDataset('P', 'Z', true); break;
            case 3: adapter.generateDataset('A', 'Z', true); break;
        }
        adapter.notifyDataSetChanged();
    }

    private void updateDataset(JSONArray data) {
        SimpleAdapter adapter = (SimpleAdapter) getListAdapter();
        adapter.generateDataset(data,true);
        adapter.notifyDataSetChanged();
    }

    private void initializePadding() {
        float density = getResources().getDisplayMetrics().density;
        int padding = addPadding ? (int) (16 * density) : 0;
        getListView().setPadding(padding, padding, padding, padding);
    }

    private void initializeHeaderAndFooter() {
        setListAdapter(null);
        if (hasHeaderAndFooter) {
            ListView list = getListView();

            LayoutInflater inflater = LayoutInflater.from(this.getActivity());
            TextView header1 = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, list, false);
            header1.setText("First header");
            list.addHeaderView(header1);

            TextView header2 = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, list, false);
            header2.setText("Second header");
            list.addHeaderView(header2);

            TextView footer = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, list, false);
            footer.setText("Single footer");
            list.addFooterView(footer);
        }
        initializeAdapter();
    }

    @SuppressLint("NewApi")
    private void initializeAdapter() {
        getListView().setFastScrollEnabled(isFastScroll);
        if (isFastScroll) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                getListView().setFastScrollAlwaysVisible(true);
            }
            setListAdapter(new FastScrollAdapter(this.getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1));
        } else {
            setListAdapter(new SimpleAdapter(this.getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1));
        }
    }

        @Override
    public void onClick(View v) {
        Toast.makeText(this.getActivity(), "Item: " + v.getTag() , Toast.LENGTH_SHORT).show();
    }

    private void getFriends() {
        String mePhone = null;
        try {
            mePhone = mUser.getString("mePhone");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RedCircleManager.getAllFriends(this.getActivity(), mePhone, new HttpRequestHandler<JSONArray>() {

            @Override
            public void onSuccess(JSONArray data) {
                mData = data;
                updateDataset(data);
            }

            @Override
            public void onSuccess(JSONArray data, int totalPages, int currentPage) {

            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

}
