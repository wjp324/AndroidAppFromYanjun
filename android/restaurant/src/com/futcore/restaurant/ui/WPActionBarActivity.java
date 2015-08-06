package com.futcore.restaurant.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
//import android.widget.HorizontalScrollView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.actionbarsherlock.internal.widget.IcsSpinner;
import com.actionbarsherlock.view.MenuItem;
import com.futcore.restaurant.Constants;
import com.futcore.restaurant.R;
import com.futcore.restaurant.WordPress;
import com.futcore.restaurant.models.Blog;
//import com.futcore.restaurant.ui.accounts.NewAccountActivity;
//import com.futcore.restaurant.ui.comments.CommentsActivity;
//import com.futcore.restaurant.ui.member.MemberIndexActivity;
//import com.futcore.restaurant.ui.notifications.NotificationsActivity;
//import com.futcore.restaurant.ui.posts.EditPostActivity;
//import com.futcore.restaurant.ui.posts.PagesActivity;
//import com.futcore.restaurant.ui.posts.PostsActivity;
//import com.futcore.restaurant.ui.prefs.PreferencesActivity;
import com.futcore.restaurant.ui.reader.ReaderActivity;
import com.futcore.restaurant.util.DeviceUtils;
import com.futcore.restaurant.util.StringUtils;

/**
 * Base class for Activities that include a standard action bar and menu drawer.
 */
public abstract class WPActionBarActivity extends SherlockFragmentActivity implements OnClickListener {

    private static final String TAG = "WPActionBarActivity";

    /**
     * Request code used when no accounts exist, and user is prompted to add an
     * account.
     */
    static final int ADD_ACCOUNT_REQUEST = 100;
    /**
     * Request code for reloading menu after returning from  the PreferencesActivity.
     */
    static final int SETTINGS_REQUEST = 200;
    /**
     * Request code for re-authentication
     */
    static final int AUTHENTICATE_REQUEST = 300;
    
    /**
     * Used to restore active activity on app creation
     */
    protected static final int READER_ACTIVITY = 0;
    protected static final int POSTS_ACTIVITY = 1;
    protected static final int PAGES_ACTIVITY = 2;
    protected static final int COMMENTS_ACTIVITY = 3;
    protected static final int STATS_ACTIVITY = 4;
    protected static final int QUICK_PHOTO_ACTIVITY = 5;
    protected static final int QUICK_VIDEO_ACTIVITY = 6;
    protected static final int VIEW_SITE_ACTIVITY = 7;
    protected static final int DASHBOARD_ACTIVITY = 8;
    protected static final int NOTIFICATIONS_ACTIVITY = 9;
    
    protected static final String LAST_ACTIVITY_PREFERENCE = "wp_pref_last_activity";
    
    protected MenuDrawer mMenuDrawer;
    private static int[] blogIDs;
    protected boolean isAnimatingRefreshButton;
    protected boolean shouldAnimateRefreshButton;
    protected boolean mShouldFinish;
    private boolean mIsXLargeDevice;
    private boolean mBlogSpinnerInitialized;
    private boolean mReauthCanceled;

    private MenuAdapter mAdapter;
    protected List<MenuDrawerItem> mMenuItems = new ArrayList<MenuDrawerItem>();
    //    private ListView mListView;
    //    private HorizontalListView mListView;
    private HorizontalScrollView mListView;
    protected LinearLayout tab1;
    protected LinearLayout tab2;
    protected LinearLayout tab3;
    protected LinearLayout tab4;
    //    protected LinearLayout tab5;
    protected LinearLayout tab6;

    protected TextView cTabText;
    protected ImageView cTabImage;
    
    private IcsSpinner mBlogSpinner;
    protected boolean mFirstLaunch = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4)
            mIsXLargeDevice = true;
        
        // configure all the available menu items
        mMenuItems.add(new ReaderMenuItem());
        mMenuItems.add(new StatsMenuItem());
        mMenuItems.add(new ViewSiteMenuItem());

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setHomeButtonEnabled(false);
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(false);

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isAnimatingRefreshButton) {
            isAnimatingRefreshButton = false;
        }
        if (mShouldFinish) {
            overridePendingTransition(0, 0);
            finish();
        } else {
            WordPress.shouldRestoreSelectedActivity = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    public void onClick(View v)
    {
        int id = v.getId();
        //        if(id==R.id.tab1&&this.getClass()!=IndexActivity.class){
        switch(id){
        case R.id.tab1:
            {
                //                Intent i = new Intent(WPActionBarActivity.this, IndexActivity.class);
                //            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            
                //                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                //                startActivity(i);
                //                WPActionBarActivity.this.overridePendingTransition(0, 0);
            }
            break;
        case R.id.tab2:
            {
                //                Intent i = new Intent(WPActionBarActivity.this, PromosActivity.class);
                //            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                startActivity(i);
//                WPActionBarActivity.this.overridePendingTransition(0, 0);
            }
            break;
        case R.id.tab3:
            {
                //                Intent i = new Intent(WPActionBarActivity.this, TakeawaysActivity.class);
                //            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                //                startActivity(i);
                //                WPActionBarActivity.this.overridePendingTransition(0, 0);
            }
            break;
        case R.id.tab4:
            {

            }
            break;
        case R.id.tab6:
            {
            }
            break;
        }

        /*        if(id==R.id.tab1){
        
            Intent i = new Intent(WPActionBarActivity.this, IndexActivity.class);
            //            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            //            AlertUtil.showAlert(this, R.string.required_fields, R.string.url_username_password_required);
        }
        else if(id==R.id.tab2){
            Intent i = new Intent(WPActionBarActivity.this, PromoActivity.class);
            //            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            //            AlertUtil.showAlert(this, R.string.required_fields, R.string.url_username_password_required);            
        }
        */
    }

    private void toIndex()
    {

    }
    
    private void refreshUI(){
        // the current blog may have changed while we were away
        //        setupCurrentBlog();
        //        if (mMenuDrawer != null) {
        //            updateMenuDrawer();
        //        }
        //        Intent i = new Intent(WPActionBarActivity.this, IndexActivity.class);
        //        startActivity(i);
        //        WPActionBarActivity.this.overridePendingTransition(0, 0);

            

        //        Blog currentBlog = WordPress.getCurrentBlog();

        //        if (currentBlog != null && mListView != null && mListView.getHeaderViewsCount() > 0) {
        /*        if (currentBlog != null && mListView != null && mListView.getCount() > 0) {
        //        if (currentBlog != null && mListView != null && mListView.getChildCount() > 0) {
            for (int i = 0; i < blogIDs.length; i++) {
                if (blogIDs[i] == currentBlog.getId()) {
                    mBlogSpinner.setSelection(i);
                }
            }
        }
        */
    }
    
    /**
     * Create a menu drawer and attach it to the activity.
     * 
     * @param contentViewID {@link View} of the main content for the activity.
     */
    protected void createMenuDrawer(int contentViewID) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMenuDrawer = attachMenuDrawer();
        mMenuDrawer.setContentView(contentViewID);
        
        initMenuDrawer();
    }

    /**
     * Create a menu drawer and attach it to the activity.
     * 
     * @param contentView {@link View} of the main content for the activity.
     */
    protected void createMenuDrawer(View contentView) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        mMenuDrawer = attachMenuDrawer();
        mMenuDrawer.setContentView(contentView);

        initMenuDrawer();
    }
    
    /**
     * Attach a menu drawer to the Activity
     * Set to be a static drawer if on a landscape x-large device
     */
    private MenuDrawer attachMenuDrawer() {
        MenuDrawer menuDrawer = null;
        if (mIsXLargeDevice) {
            // on a x-large screen device
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                menuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.STATIC, Position.LEFT);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                menuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.OVERLAY);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                menuDrawer.setDrawerIndicatorEnabled(true);
            }
        } else {
            //            menuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.OVERLAY);
            menuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.STATIC, Position.BOTTOM);
            //            menuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.OVERLAY);
            menuDrawer.setDrawerIndicatorEnabled(true);
        }
        int shadowSizeInPixels = getResources().getDimensionPixelSize(R.dimen.menu_shadow_width);
        menuDrawer.setDropShadowSize(shadowSizeInPixels);
        menuDrawer.setDropShadowColor(getResources().getColor(R.color.md__shadowColor));
        menuDrawer.setSlideDrawable(R.drawable.ic_drawer);
        return menuDrawer;
    }
    
    /**
     * Create menu drawer ListView and listeners
     */
    private void initMenuDrawer() {
    //    protected void initMenuDrawer() {
        //        mListView = new ListView(this);
        //        mListView = new HorizontalListView(this);
        //        mListView = (HorizontalListView)findViewById(R.id.tablistview);
        mListView = (HorizontalScrollView) findViewById(R.id.tabConScroll);
        //        mListView = new HorizontalScrollView(this);
        //        //        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //        mListView.setDivider(null);
        //        mListView.setDividerHeight(0);
        //        mListView.setCacheColorHint(android.R.color.transparent);
        mAdapter = new MenuAdapter(this);
        /*        String[] blogNames = getBlogNames();
        if (blogNames.length > 1) {
            addBlogSpinner(blogNames);
        }
        */

        /*        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // account for header views
                //                int menuPosition = position - mListView.getHeaderViewsCount();
                int menuPosition = position - mListView.getCount();
                // bail if the adjusted position is out of bounds for the adapter
                if (menuPosition < 0 || menuPosition >= mAdapter.getCount())
                    return;
                MenuDrawerItem item = mAdapter.getItem(menuPosition);
                // if the item has an id, remember it for launch
                if (item.hasItemId()){
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(WPActionBarActivity.this);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt(LAST_ACTIVITY_PREFERENCE, item.getItemId());
                    editor.commit();
                }
                // only perform selection if the item isn't already selected
                if (!item.isSelected())
                    item.selectItem();
                // save the last activity preference
                // close the menu drawer
                mMenuDrawer.closeMenu();
                // if we have an intent, start the new activity
            }
        });
        */
        /*        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
                mMenuDrawer.invalidate();
            }
        });
        */
        //        mMenuDrawer.setMenuView(mListView);
        //        mMenuDrawer.
        mMenuDrawer.setMenuView(R.layout.bottom_tabs);

        tab1 = (LinearLayout) findViewById(R.id.tab1);
        tab2 = (LinearLayout) findViewById(R.id.tab2);
        tab3 = (LinearLayout) findViewById(R.id.tab3);
        tab4 = (LinearLayout) findViewById(R.id.tab4);
        //        tab5 = (LinearLayout) findViewById(R.id.tab5);
        tab6 = (LinearLayout) findViewById(R.id.tab6);

        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        tab3.setOnClickListener(this);
        tab4.setOnClickListener(this);
        //        tab5.setOnClickListener(this);
        tab6.setOnClickListener(this);
        
        tab1.setLayoutParams(new LinearLayout.LayoutParams(getWindowManager().getDefaultDisplay().getWidth()/5,LayoutParams.MATCH_PARENT));
        tab2.setLayoutParams(new LinearLayout.LayoutParams(getWindowManager().getDefaultDisplay().getWidth()/5,LayoutParams.FILL_PARENT));
        tab3.setLayoutParams(new LinearLayout.LayoutParams(getWindowManager().getDefaultDisplay().getWidth()/5,LayoutParams.FILL_PARENT));
        tab4.setLayoutParams(new LinearLayout.LayoutParams(getWindowManager().getDefaultDisplay().getWidth()/5,LayoutParams.FILL_PARENT));
        //        tab5.setLayoutParams(new LinearLayout.LayoutParams(getWindowManager().getDefaultDisplay().getWidth()/5,LayoutParams.FILL_PARENT));
        tab6.setLayoutParams(new LinearLayout.LayoutParams(getWindowManager().getDefaultDisplay().getWidth()/5,LayoutParams.FILL_PARENT));
        
        //mListView.setAdapter(mAdapter);
        updateMenuDrawer();
    }

    private void addBlogSpinner(String[] blogNames) {
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout spinnerWrapper = (LinearLayout) layoutInflater.inflate(R.layout.blog_spinner, null);
        
        spinnerWrapper.setOnClickListener(new OnClickListener() {
                
            @Override
            public void onClick(View v) {
                if (mBlogSpinner != null) {
                    mBlogSpinner.performClick();
                }
            }
        });
        
        mBlogSpinner = (IcsSpinner) spinnerWrapper.findViewById(R.id.blog_spinner);
        mBlogSpinner.setOnItemSelectedListener(mItemSelectedListener);
        SpinnerAdapter mSpinnerAdapter = new ArrayAdapter<String>(getSupportActionBar()
                .getThemedContext(),
                R.layout.sherlock_spinner_dropdown_item, blogNames);
        mBlogSpinner.setAdapter(mSpinnerAdapter);
 //       mListView.addHeaderView(spinnerWrapper);
    }

    protected void startActivityWithDelay(final Intent i) {

        if (mIsXLargeDevice && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Tablets in landscape don't need a delay because the menu drawer doesn't close
            startActivity(i);
        } else {
            // When switching to LAST_ACTIVITY_PREFERENCE onCreate we don't need to delay
            if (mFirstLaunch) {
                startActivity(i);
                return;
            }
            // Let the menu animation finish before starting a new activity
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(i);
                }
            }, 400);
        }
    }

    /**
     * Update all of the items in the menu drawer based on the current active
     * blog.
     */
    protected void updateMenuDrawer() {
        mAdapter.clear();
        // iterate over the available menu items and only show the ones that should be visible
        Iterator<MenuDrawerItem> availableItems = mMenuItems.iterator();
        while(availableItems.hasNext()){
            MenuDrawerItem item = availableItems.next();
            if (item.isVisible()) {
                mAdapter.add(item);
            }
        }
        mAdapter.notifyDataSetChanged();

    }
        
    public static class MenuAdapter extends ArrayAdapter<MenuDrawerItem> {
        MenuAdapter(Context context) {
            super(context, R.layout.menu_drawer_row, R.id.menu_row_title, new ArrayList<MenuDrawerItem>());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            MenuDrawerItem item = getItem(position);

            TextView titleTextView = (TextView) view.findViewById(R.id.menu_row_title);
            titleTextView.setText(item.getTitleRes());

            ImageView iconImageView = (ImageView) view.findViewById(R.id.menu_row_icon);
            iconImageView.setImageResource(item.getIconRes());
            // Hide the badge always
            //            view.findViewById(R.id.menu_row_badge).setVisibility(View.GONE);

            if (item.isSelected()) {
                // http://stackoverflow.com/questions/5890379/setbackgroundresource-discards-my-xml-layout-attributes
                /*                int bottom = view.getPaddingBottom();
                int top = view.getPaddingTop();
                int right = view.getPaddingRight();
                int left = view.getPaddingLeft();
                view.setBackgroundResource(R.drawable.menu_drawer_selected);
                view.setPadding(left, top, right, bottom);
                */
                //                view.findViewById()
                //                iconImageView.setColorFilter(getResources().getColor(R.color.md__iconCurrentTintColor));
                iconImageView.setColorFilter(R.color.md__iconCurrentTintColor);
                //                titleTextView.setTextColor(R.color.md__iconCurrentTintColor);
            } else {
                view.setBackgroundResource(R.drawable.md_list_selector);
            }
            // allow the menudrawer item to configure the view
            item.configureView(view);

            return view;
        }
    }


    /**
     * Called when the activity has detected the user's press of the back key.
     * If the activity has a menu drawer attached that is opened or in the
     * process of opening, the back button press closes it. Otherwise, the
     * normal back action is taken.
     */
    @Override
    public void onBackPressed() {
        /*        if (mMenuDrawer != null) {
            final int drawerState = mMenuDrawer.getDrawerState();
            if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
                mMenuDrawer.closeMenu();
                return;
            }
            }
        */
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    /**
     * Get the names of all the blogs configured within the application. If a
     * blog does not have a specific name, the blog URL is returned.
     * 
     * @return array of blog names
     */
    private static String[] getBlogNames() {
        List<Map<String, Object>> accounts = WordPress.wpDB.getAccounts();

        int blogCount = accounts.size();
        blogIDs = new int[blogCount];
        String[] blogNames = new String[blogCount];

        for (int i = 0; i < blogCount; i++) {
            Map<String, Object> account = accounts.get(i);
            String name;
            if (account.get("blogName") != null) {
                name = StringUtils.unescapeHTML(account.get("blogName").toString());
            } else {
                name = account.get("url").toString();
            }
            blogNames[i] = name;
            blogIDs[i] = Integer.valueOf(account.get("id").toString());
        }

        return blogNames;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*        switch (requestCode) {
            case ADD_ACCOUNT_REQUEST:
                if (resultCode == RESULT_OK) {
                    // new blog has been added, so rebuild cache of blogs and
                    // setup current blog
                    getBlogNames();
                    setupCurrentBlog();
                    initMenuDrawer();
                    mMenuDrawer.openMenu(false);
                    WordPress.registerForCloudMessaging(this);
                } else {
                    finish();
                }
                break;
            case SETTINGS_REQUEST:
                if (resultCode == RESULT_OK) {
                    if (mMenuDrawer != null) {
                        updateMenuDrawer();
                        String[] blogNames = getBlogNames();
                        // If we need to add or remove the blog spinner, init the drawer again
                        //                        if ((blogNames.length > 1 && mListView.getHeaderViewsCount() == 0)
                        //                                || blogNames.length == 1 && mListView.getHeaderViewsCount() > 0)
                        if ((blogNames.length > 1 && mListView.getCount() == 0)
                            || blogNames.length == 1 && mListView.getCount() > 0)
                        //                        if ((blogNames.length > 1 && mListView.getChildCount() == 0)
                        //                            || blogNames.length == 1 && mListView.getChildCount() > 0)
                            this.initMenuDrawer();
                        else if (blogNames.length > 1 && mBlogSpinner != null) {
                            SpinnerAdapter mSpinnerAdapter = new ArrayAdapter<String>(
                                    getSupportActionBar()
                                            .getThemedContext(),
                                    R.layout.sherlock_spinner_dropdown_item, blogNames);
                            mBlogSpinner.setAdapter(mSpinnerAdapter);
                        }
                        
                        if (blogNames.length >= 1) {
                            setupCurrentBlog();
                            onBlogChanged();
                        }
                        WordPress.registerForCloudMessaging(this);
                    }
                }
                break;
            case AUTHENTICATE_REQUEST:
                if (resultCode == RESULT_CANCELED) {
                    mReauthCanceled = true;
                    Intent i = new Intent(this, NewAccountActivity.class);
                    startActivityForResult(i, ADD_ACCOUNT_REQUEST);
                } else {
                    WordPress.registerForCloudMessaging(this);
                }
                break;
        }
        */
    }
    
    private IcsAdapterView.OnItemSelectedListener mItemSelectedListener = new IcsAdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(IcsAdapterView<?> arg0, View arg1, int position, long arg3) {
            // http://stackoverflow.com/questions/5624825/spinner-onitemselected-executes-when-it-is-not-suppose-to/5918177#5918177
            if (!mBlogSpinnerInitialized) {
                mBlogSpinnerInitialized = true;
            } else {
                WordPress.setCurrentBlog(blogIDs[position]);
                updateMenuDrawer();
                onBlogChanged();
            }
        }

        @Override
        public void onNothingSelected(IcsAdapterView<?> arg0) { 
        }
    };

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mMenuDrawer != null) {
                mMenuDrawer.toggleMenu();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the user changes the active blog.
     */
    public void onBlogChanged() {
        WordPress.wpDB.updateLastBlogId(WordPress.currentBlog.getId());
        // the menu may have changed, we need to change the selection if the selected item
        // is not available in the menu anymore
        Iterator<MenuDrawerItem> itemIterator = mMenuItems.iterator();
        while(itemIterator.hasNext()){
            MenuDrawerItem item = itemIterator.next();
            // if the item is selected, but it's no longer visible we need to
            // select the first available item from the adapter
            if (item.isSelected() && !item.isVisible()) {
                // then select the first item and activate it
                mAdapter.getItem(0).selectItem();
                // if it has an item id save it to the preferences
                if (item.hasItemId()){
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(WPActionBarActivity.this);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt(LAST_ACTIVITY_PREFERENCE, item.getItemId());
                    editor.commit();
                }
                break;
            }
        }
    }

    public void startAnimatingRefreshButton(MenuItem refreshItem) {
        if (refreshItem != null && !isAnimatingRefreshButton) {
            isAnimatingRefreshButton = true;
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ImageView iv = (ImageView) inflater.inflate(
                    getResources().getLayout(R.layout.menu_refresh_view), null);
            RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF,
                    0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setInterpolator(new LinearInterpolator());
            anim.setRepeatCount(Animation.INFINITE);
            anim.setDuration(1400);
            iv.startAnimation(anim);
            refreshItem.setActionView(iv);
        }
    }

    public void stopAnimatingRefreshButton(MenuItem refreshItem) {
        isAnimatingRefreshButton = false;
        if (refreshItem != null && refreshItem.getActionView() != null) {
            refreshItem.getActionView().clearAnimation();
            refreshItem.setActionView(null);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
 
        if (mIsXLargeDevice) {
            if (mMenuDrawer != null) {
                // Re-attach the drawer if an XLarge device is rotated, so it can be static if in landscape
                View content = mMenuDrawer.getContentContainer().getChildAt(0);
                if (content != null) {
                    mMenuDrawer.getContentContainer().removeView(content);
                    mMenuDrawer = attachMenuDrawer();
                    mMenuDrawer.setContentView(content);
                    initMenuDrawer();
                }
            }
        }
 
        super.onConfigurationChanged(newConfig);
    }

    private class ReaderMenuItem extends MenuDrawerItem {
        
        ReaderMenuItem(){
            super(READER_ACTIVITY, R.string.reader, R.drawable.dashboard_icon_subs);
        }
        
        @Override
        public Boolean isVisible(){
            return WordPress.hasValidWPComCredentials(WPActionBarActivity.this);
        }
        
        @Override
        public Boolean isSelected(){
            return WPActionBarActivity.this instanceof ReaderActivity;
        }
        @Override
        public void onSelectItem(){
            if (!(WPActionBarActivity.this instanceof ReaderActivity))
                mShouldFinish = true;
            int readerBlogID = WordPress.wpDB.getWPCOMBlogID();
            Intent intent = new Intent(WPActionBarActivity.this, ReaderActivity.class);
            intent.putExtra("id", readerBlogID);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityWithDelay(intent);
        }
    }

    private class StatsMenuItem extends MenuDrawerItem {
        StatsMenuItem(){
            super(STATS_ACTIVITY, R.string.tab_stats, R.drawable.dashboard_icon_stats);
        }
        @Override
        public Boolean isSelected(){
            //            return WPActionBarActivity.this instanceof StatsActivity;
            return false;
        }
        @Override
        public void onSelectItem(){
        }
    }

    private class ViewSiteMenuItem extends MenuDrawerItem {
        ViewSiteMenuItem(){
            super(VIEW_SITE_ACTIVITY, R.string.view_site, R.drawable.dashboard_icon_view);
        }
        @Override
        public Boolean isSelected(){
            //            return WPActionBarActivity.this instanceof ViewSiteActivity;
            return false;
        }
        @Override
        public void onSelectItem(){
        }
    }

}
