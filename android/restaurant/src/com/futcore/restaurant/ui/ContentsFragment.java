package com.futcore.restaurant.ui;

import com.futcore.restaurant.util.*;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import android.content.ComponentName;

import android.os.AsyncTask;


import android.graphics.Color;

import android.media.MediaRecorder;
import android.media.AudioManager;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.futcore.restaurant.R;
import com.futcore.restaurant.WordPress;
///import android.support.v4.app.Fragment;

import com.futcore.restaurant.models.*;
import com.futcore.restaurant.service.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import java.io.File;
import java.io.FileInputStream;

import com.futcore.restaurant.service.MusicService.MusicBinder;

import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.view.View;
import android.widget.ListView;

import android.media.MediaPlayer;

import java.io.IOException;

import java.text.SimpleDateFormat;

import com.futcore.restaurant.RemoteControlReceiver;
import android.media.AudioManager;


//tika pdf
/*import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
*/

//import org.apache.pdfbox.searchengine.lucene.LucenePDFDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

//public class IndexFragment extends Fragment implements OnClickListener
public class ContentsFragment extends SherlockFragment implements OnClickListener
{
    private AudioManager am;
    public static final String MUSIC_DIR = "/storage/sdcard0/Music";
    private static final String TEST_RECORD_FILE = "/storage/sdcard0/Music/spider_test.3gp";

    private static String mFileName = TEST_RECORD_FILE;

    private boolean isRecording = false;
    
    private View mView;
    
    public String errorMsg = "";

    private Button addNew;

    /*    private Button mTestplay;
          private Button mTestplay1;
          private Button mTeststop;
    */


    private MediaPlayer mMediaPlayer;
    private MediaPlayer mMediaPlayer1;

    private MediaPlayer mRecordPlayer;

    private MediaRecorder mRecorder = null;
    //    private MediaRecorder mMediaRecorder;

    private Date mStartTime;
    private Date mTimest1;
    private Date mTimest2;

    private TextView mTestPdf;

    private long mTimeper1;
    private long mTimeper2;


    private android.os.Handler mStartOriHan;
    private android.os.Handler mStartRecordHan;
    private android.os.Handler mStartReplayHan;
    
    public static ContentsFragment newInstance() {
        ContentsFragment fragment = new ContentsFragment();
        //        fragment.mContent = content;
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //        mNum = getArguments() != null ? getArguments().getInt("num") : 0;
    }
    
    
    //    public void onCreate(Bundle savedInstanceState)
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state){
        
        setHasOptionsMenu(true);
        
        mView = inflater.inflate(R.layout.contents, parent, false);
        /*        mTestplay = (Button)mView.findViewById(R.id.testplay);
                  mTestplay1 = (Button)mView.findViewById(R.id.testplay1);
                  mTeststop = (Button)mView.findViewById(R.id.teststop);
        */

        //mMediaRecorder = new MediaRecorder();
        //        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT );
        //mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mTestPdf = (TextView)mView.findViewById(R.id.testpdfcon);
        
        PDFParser parser;
        String parsedText = "";  
        PDFTextStripper pdfStripper;
        PDDocument pdDoc = new PDDocument();  
        COSDocument cosDoc = new COSDocument();
        PDDocumentInformation pdDocInfo;


        String fileName = "/storage/sdcard0/Download/spider/test.pdf";
        File f = new File(fileName);  
          
        if (!f.isFile()) {  
            System.out.println("File " + fileName + " does not exist.");  
            return null;  
        }  
          
        try {  
            parser = new PDFParser(new FileInputStream(f));  
        } catch (Exception e) {
            e.printStackTrace();
            return null;  
        }  
          
        try {  
            parser.parse();  
            cosDoc = parser.getDocument();  
            pdfStripper = new PDFTextStripper();  
            pdfStripper.setStartPage(1); 
            pdfStripper.setEndPage(2);
            pdDoc = new PDDocument(cosDoc);  
            parsedText = pdfStripper.getText(pdDoc);   
        } catch (Exception e) {  
            e.printStackTrace();  
            try {  
                if (cosDoc != null) cosDoc.close();  
                if (pdDoc != null) pdDoc.close();  
            } catch (Exception e1) {  
                e1.printStackTrace();  
            }  
            //            return null;  
        }

        mTestPdf.setText(parsedText);
        
        //        System.out.println("Done.");  
        //        return parsedText;
        

        /*        InputStream is = null;
        try {
            is = new FileInputStream("/storage/sdcard0/Download/spider/test.pdf");
            ContentHandler contenthandler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            PDFParser pdfparser = new PDFParser();
            pdfparser.parse(is, contenthandler, metadata, new ParseContext());
            //            System.out.println(contenthandler.toString());
            mTestPdf.setText(contenthandler.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {

            if (is != null) {
                try{
                    
                    is.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
        }
        */


        return mView;
    }
    
    
    public void onResume()
    {
        super.onResume();
    }

    public void onClick(View v)
    {
        //        super.onClick(v);
        int id = v.getId();
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //        Typeface font = Typeface.createFromAsset( getActivity().getAssets(), "fontawesome-webfont.ttf" );
        
        inflater.inflate(R.menu.contents, menu);

        //        addNew = findViewById(R.id.addnew);
        //        addNew.setTypeface(font);
        
    }

    @Override
    public void onStart()
    {
        super.onStart();
        //        am.registerMediaButtonEventReceiver(RemoteControlReceiver);
        //        mAudioManager.registerMediaButtonEventReceiver(mRecoItem.getManItem().getItemName()));
    }

    @Override
    public void onStop()
    {
        super.onStop();
        //        am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
        //        am.unregisterMediaButtonEventReceiver(mMediaButtonReceiverComponent);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
}

