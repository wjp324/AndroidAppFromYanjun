package com.futcore.restaurant.ui;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.net.Uri;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.File;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.app.AlertDialog;

import android.provider.MediaStore;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.futcore.restaurant.util.LocationHelper;
import com.futcore.restaurant.util.AlertUtil;
import com.futcore.restaurant.util.LocationHelper.LocationResult;


import android.content.DialogInterface;
import android.content.Intent;

import com.actionbarsherlock.app.ActionBar;
import com.futcore.restaurant.Constants;
import com.futcore.restaurant.R;
import com.futcore.restaurant.WordPress;
import com.futcore.restaurant.custom_view.NoSwipeViewPager;
//import com.futcore.restaurant.ui.member.MemberIndexFragment;
import com.futcore.restaurant.util.AlertUtil;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.joanzapata.android.iconify.Iconify.IconValue;

//import android.support.v13.app.FragmentStatePagerAdapter;

public class MainActivity extends WPActionBarActivity implements OnClickListener
{
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";

	private String mCurrentPhotoPath;
    

    
    protected LinearLayout tab1;
    protected LinearLayout tab2;
    protected LinearLayout tab3;
    protected LinearLayout tab4;
    //    protected LinearLayout tab5;
    protected LinearLayout tab6;
    
    private Integer mCurrentTab = 1;

    private int mCurrentActivityRequest = -1;
    
   /**
     * The number of pages (wizard steps) to show in this demo.
     */
    
    private static final int NUM_PAGES = 5;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    //    private ViewPager mPager;
    private NoSwipeViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    private Boolean doubleBackToExitPressedOnce = false;

    private LEventsFragment musicSecFragment;

    private String mMediaCapturePath = "";

    private static final int ACTIVITY_REQUEST_CODE_PICTURE_LIBRARY = 0;
    private static final int ACTIVITY_REQUEST_CODE_TAKE_PHOTO = 1;
    private static final int ACTIVITY_REQUEST_CODE_VIDEO_LIBRARY = 2;
    private static final int ACTIVITY_REQUEST_CODE_TAKE_VIDEO = 3;
    private static final int ACTIVITY_REQUEST_CODE_CREATE_LINK = 4;
    private static final int ACTIVITY_REQUEST_CODE_SELECT_CATEGORIES = 5;
    private static final int ACTIVITY_REQUEST_CODE_ADD_REVIEW = 6;
    private static final int ACTIVITY_REQUEST_CODE_CROP_IMAGE = 7;
    private static final int ACTIVITY_REQUEST_CODE_LOGIN = 8;

    private static final int ACTIVITY_NEW_ITEM = 9;

    //    private ContentsFragment contentsFragment;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //        Toast.makeText(this, "Pull Up!", Toast.LENGTH_SHORT).show();
        // Special check for a null database (see #507)
        if (WordPress.wpDB == null) {
            Toast.makeText(this, R.string.fatal_db_error, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        createMenuDrawer(R.layout.main);
        
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setHomeButtonEnabled(false);
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(false);

        // Instantiate a ViewPager and a PagerAdapter.
        //        mPager = (ViewPager) findViewById(R.id.mainPager);
        mPager = (NoSwipeViewPager) findViewById(R.id.mainPager);

        List<Fragment> fragments = getFragments();
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),fragments);
        mPager.setAdapter(mPagerAdapter);        

        mPager.setOffscreenPageLimit(5);
        
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)                
                {
                    //                    AlertUtil.showAlert(MainActivity.this, R.string.required_fields, String.valueOf(positionOffset));            
                }

                public void onPageSelected(int position)
                {
                    //                    AlertUtil.showAlert(MainActivity.this, R.string.required_fields, String.valueOf(position));            
                    mCurrentTab = position+1;
                    updateTab();
                }

                public void onPageScrollStateChanged(int state)
                {
                }
            });

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

        //        new GetCompanyInfoTask().execute("1");
    }

    //    public void onClick(View v)
    //    {
    //        super.onClick(v);
    //        int id = v.getId();
    //    }
    
    protected void createMenuDrawer(int contentViewID)
    {
        super.createMenuDrawer(contentViewID);

        cTabText = (TextView) findViewById(R.id.tabTitle1);
        cTabImage = (ImageView) findViewById(R.id.tabIcon1);


        ImageView cTabImage2 = (ImageView) findViewById(R.id.tabIcon2);
        ImageView cTabImage4 = (ImageView) findViewById(R.id.tabIcon4);
        //        cTabImage4 = (ImageView) findViewById(R.id.tabIcon4);
        //        cTabImage3 = (ImageView) findViewById(R.id.tabIcon3);
        //        cTabImage6 = (ImageView) findViewById(R.id.tabIcon6);

        //        cTabImage2.setImageDrawable(new IconDrawable(this, IconValue.fa_bell_o).actionBarSize()).setColorFilter(getResources().getColor(R.color.tabColor));
        //        cTabImage2.setImageDrawable(new IconDrawable(this, IconValue.fa_bell_o).actionBarSize());
        cTabImage2.setImageDrawable(new IconDrawable(this, IconValue.fa_heart_o).actionBarSize());
        cTabImage2.setColorFilter(getResources().getColor(R.color.tabColor));

        cTabImage4.setImageDrawable(new IconDrawable(this, IconValue.fa_camera).actionBarSize());
        cTabImage4.setColorFilter(getResources().getColor(R.color.tabColor)); 
        
        //        cTabImage.setImageDrawable(new IconDrawable(this, IconValue.fa_heart_o).actionBarSize());
        //        cTabImage.setImageDrawable(new IconDrawable(this, IconValue.fa_heart).actionBarSize());
        cTabImage.setImageDrawable(new IconDrawable(this, IconValue.fa_location_arrow).actionBarSize());

        cTabText.setTextColor(getResources().getColor(R.color.g_g));
        cTabImage.setColorFilter(getResources().getColor(R.color.g_g));
    }
    
    /*    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.index, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.search_con:
            AlertUtil.showAlert(MainActivity.this, R.string.required_fields, R.string.url_username_password_required);            
            //            Intent i=new Intent(class1.this, clas2.class);
            //            startActivity(i);
            return true;
        }
        return false;
    }

    */


    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
             doubleBackToExitPressedOnce=false;   

            }
        }, 2000);

        
        /*        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
        */
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    //    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;
        
        public ScreenSlidePagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
            super(fragmentManager);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            //            return new MainPageFragment();
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
            //            return NUM_PAGES;
        }
    }


    private List<Fragment> getFragments(){
        List<Fragment> fList = new ArrayList<Fragment>();
        //        fList.add(MainPageFragment.newInstance("Fragment 1"));
        //        fList.add(new IndexFragment());
        //        fList.add(IndexFragment.newInstance(mPager));
        
        musicSecFragment = LEventsFragment.newInstance();
        fList.add(RecoFragment.newInstance());
        //        fList.add(PromosFragment.newInstance());
        //        fList.add(EventsFragment.newInstance());
        fList.add(OngoingFragment.newInstance());
//        fList.add(TakeawaysFragment.newInstance());
//        fList.add(LEventsFragment.newInstance());
        fList.add(musicSecFragment);
        //        fList.add(ContentsFragment.newInstance());
        //        fList.add(EventsFragment.newInstance());
        //        fList.add(MemberIndexFragment.newInstance());
        //        fList.add(new TakeawaysFragment());
        //        fList.add(MainPageFragment.newInstance("Fragment 3"));
        //        fList.add(new MainPageFragment());
        //        fList.add(new MainPageFragment());
        //        fList.add(new MainPageFragment());
        return fList;    
    }

    public void onClick(View v)
    {
        int id = v.getId();
        switch(id){
        case R.id.tab1:
            {
                toPage0();
            }
            break;
        case R.id.tab2:
            {
                toPage1();
            }
            break;
        case R.id.tab3:
            {
                toPage2();
            }
            break;
        case R.id.tab4:
            {
                dispatchTakePictureIntent();
                //                launchCamera();
                ////////
                //               toPage2();
            }
            break;
        case R.id.tab6:
            {
                toPage3();
            }
            break;
        }
    }

    private void toPage0()
    {
        mPager.setCurrentItem(0, false);
    }

    private void toPage1()
    {
        mPager.setCurrentItem(1, false);
        //        AlertUtil.showAlert(MainActivity.this, R.string.required_fields,"jjjjjjjjjjjjkkkkkkkkkkkk");
    }

    private void toPage2()
    {
        mPager.setCurrentItem(2, false);
    }
    
    private void toPage3()
    {
        mPager.setCurrentItem(3, false);
    }
    
    private void toPage4()
    {
        mPager.setCurrentItem(4, false);
    }
    

    private void updateTab()
    {
        LinearLayout tabCon = (LinearLayout)findViewById(R.id.tabCon);
        HorizontalScrollView tabConScroll = (HorizontalScrollView)findViewById(R.id.tabConScroll);
        int c1 = tabCon.getChildCount();
        for (int i=0; i < c1; i++){
            LinearLayout v = (LinearLayout)tabCon.getChildAt(i);
            ImageView imgV = (ImageView)v.getChildAt(0);
            TextView textV = (TextView)v.getChildAt(1);
            textV.setTextColor(getResources().getColor(R.color.tabColor));
            //            cTabImage.setColorFilter(null);
            cTabImage.setColorFilter(getResources().getColor(R.color.tabColor));            

        }

        ImageView cTabImage1 = (ImageView) findViewById(R.id.tabIcon1);
        ImageView cTabImage2 = (ImageView) findViewById(R.id.tabIcon2);
        
        
        
        switch(mCurrentTab){
        case 1:
            cTabText = (TextView) findViewById(R.id.tabTitle1);
            cTabImage = (ImageView) findViewById(R.id.tabIcon1);
            //            cTabImage1.setImageDrawable(new IconDrawable(this, IconValue.fa_heart).actionBarSize());
            cTabImage1.setImageDrawable(new IconDrawable(this, IconValue.fa_location_arrow).actionBarSize());
            cTabImage1.setColorFilter(getResources().getColor(R.color.g_g));
            //            cTabImage2.setImageDrawable(new IconDrawable(this, IconValue.fa_bell_o).actionBarSize());
            cTabImage2.setImageDrawable(new IconDrawable(this, IconValue.fa_heart_o).actionBarSize());
            cTabImage2.setColorFilter(getResources().getColor(R.color.tabColor));
            tabConScroll.fullScroll(HorizontalScrollView.FOCUS_LEFT);
            break;
        case 2:
            cTabText = (TextView) findViewById(R.id.tabTitle2);
            cTabImage = (ImageView) findViewById(R.id.tabIcon2);
            //            cTabImage1.setImageDrawable(new IconDrawable(this, IconValue.fa_heart_o).actionBarSize());
            cTabImage1.setImageDrawable(new IconDrawable(this, IconValue.fa_location_arrow).actionBarSize());
            cTabImage1.setColorFilter(getResources().getColor(R.color.tabColor));
            //            cTabImage2.setImageDrawable(new IconDrawable(this, IconValue.fa_bell).actionBarSize());
            cTabImage2.setImageDrawable(new IconDrawable(this, IconValue.fa_heart).actionBarSize());
            cTabImage2.setColorFilter(getResources().getColor(R.color.g_g));
            tabConScroll.fullScroll(HorizontalScrollView.FOCUS_LEFT);
            break;
        case 3:
            cTabText = (TextView) findViewById(R.id.tabTitle3);
            cTabImage = (ImageView) findViewById(R.id.tabIcon3);
            tabConScroll.fullScroll(HorizontalScrollView.FOCUS_LEFT);
            break;
        case 4:
            //            cTabText = (TextView) findViewById(R.id.tabTitle4);
            //            cTabImage = (ImageView) findViewById(R.id.tabIcon4);
            //            cTabText = (TextView) findViewById(R.id.tabTitle6);
            //            cTabImage = (ImageView) findViewById(R.id.tabIcon6);
            //            tabConScroll.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            break;
        }
        
        cTabText.setTextColor(getResources().getColor(R.color.g_g));
        cTabImage.setColorFilter(getResources().getColor(R.color.g_g));
        //        tab4.setColorFilter(getResources().getColor(R.color.g_g));
    }



	private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //        File storageDir = Environment.getExternalStoragePublicDirectory(
        //                                                                        Environment.DIRECTORY_PICTURES);

        File storageDir = new File("/storage/emulated/legacy/yanjtest");
        File image = File.createTempFile(
                                         imageFileName,  /* prefix */
                                         ".jpg",         /* suffix */
                                         storageDir      /* directory */
                                         );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;        
	}

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                //                ...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk11111000000099");
                System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk"+mCurrentPhotoPath);
                
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                           Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }    

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }

        //        if (data != null || ((requestCode == ACTIVITY_REQUEST_CODE_TAKE_PHOTO || requestCode == ACTIVITY_REQUEST_CODE_TAKE_VIDEO))) {
        if (data != null || ((requestCode == ACTIVITY_REQUEST_CODE_TAKE_PHOTO))) {
            Bundle extras;

            switch (requestCode) {
            case ACTIVITY_REQUEST_CODE_PICTURE_LIBRARY:
                Uri imageUri = data.getData();
                AlertUtil.showAlert(this, R.string.required_fields, String.valueOf(imageUri));
                //                    verifyImage(imageUri);
                break;
            case ACTIVITY_REQUEST_CODE_CROP_IMAGE:
                Uri imageUri1 = data.getData();
                AlertUtil.showAlert(this, R.string.required_fields, String.valueOf(imageUri1));
                break;
            case ACTIVITY_REQUEST_CODE_TAKE_PHOTO:
                //            case REQUEST_TAKE_PHOTO:
                Intent i = new Intent(this, NewItemActivity.class);
                //                Uri imageUri2 = data.getData();
                //                AlertUtil.showAlert(this, R.string.required_fields, String.valueOf(imageUri2));
                //        startActivityForResult();
                i.putExtra("imgdir", mCurrentPhotoPath);
                startActivityForResult(i, ACTIVITY_NEW_ITEM);
                break;
            }
        }
        //        AlertUtil.showAlert(this, R.string.required_fields, R.string.url_username_password_required);
    }
}
