package com.futcore.restaurant.ui;

import android.support.v4.app.FragmentManager;

import com.futcore.restaurant.SpiderService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import javax.security.auth.login.LoginException;

import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.DeleteBuilder;


import org.apache.http.util.EntityUtils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;

import android.content.Context;

import com.futcore.restaurant.util.AlertUtil;

import java.sql.SQLException;

//import com.futcore.restaurant.SendRecoService;
import com.futcore.restaurant.SendNewRecoService;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.futcore.restaurant.R;
import com.futcore.restaurant.util.JSONReader;

import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;

import android.widget.TextView;
import android.widget.Button;
import android.widget.LinearLayout;

import android.view.LayoutInflater;

import com.futcore.restaurant.models.*;

import org.apache.http.HttpEntity;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;


import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import com.futcore.restaurant.models.ManCertPlace;

import com.futcore.restaurant.models.DatabaseHelper;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import com.j256.ormlite.dao.Dao;

import com.futcore.restaurant.models.DatabaseHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import com.j256.ormlite.table.TableUtils;

import android.app.Activity;

/*import org.java_websocket.WebSocketImpl;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.drafts.Draft_75;
import org.java_websocket.drafts.Draft_76;
import org.java_websocket.handshake.ServerHandshake;
*/

import java.net.URISyntaxException;
import java.net.URI;

/*import org.projectodd.stilts.stomp.client.StompClient;
import org.projectodd.stilts.stomp.client.ClientSubscription;
import org.projectodd.stilts.stomp.Subscription.AckMode;
import org.projectodd.stilts.stomp.StompException;

import org.projectodd.stilts.stomp.client.helpers.MessageAccumulator;
*/

import me.irobo.stomp.*;
import de.roderick.weberknecht.*;

import java.util.concurrent.TimeoutException;
import javax.net.ssl.SSLException;

//import net.ser1.stomp.Client;
//import net.ser1.stomp.Listener;

import android.os.AsyncTask;


public class JustTestActivity extends Activity
{
    private static final String SOCKET_URL = "";
    //    private WebSocketClient cc;
    //    private WebSocket websocket;
    //    private Client mClient;
    //    private StompClient client;
    private String mChannel = "/spider/topic/getwebevents";
    private StompClient mClient;
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.just_test);
        
        checkAndStartSpiderService();
        

        //        new TestWebSocketTask().execute();

        /*        String ip = "ws://192.172.6.39/client";
        int port = 8080;

        String channel = "/message/add";
        Client c;

        try {
            c = new Client( ip, port, "", "" );
            Log.i("Stomp", "Connection established");
            c.subscribe( channel, new Listener() {
                    public void message( Map header, String message ) {
                        Log.i("Stomp", "Message received!!!");
                    }
                });

        } catch (IOException ex) {
            Log.e("Stomp", ex.getMessage());
            ex.printStackTrace();

        } catch (LoginException ex) {
            Log.e("Stomp", ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            Log.e("Stomp", ex.getMessage());
            ex.printStackTrace();
        }
        */

        

        

        System.out.println("cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccreate just test activity");
        
        
        //        Client c = new Client( "localhost", 61626, "ser", "ser" );
        //        c.subscribe( "foo-channel", my_listener );
        //        c.subscribe( "foo-channel", other_listener );
        //        c.unsubscribe( "foo-channel", other_listener );  // Unsubscribe only one listener
        //        c.unsubscribe( "foo-channel" );   // Unsubscribe all listeners
        //        c.disconnect();




        

        /*        Client c = new Client( "localhost", 61626, "ser", "ser" );
        c.subscribe( "foo-channel", my_listener );
        c.unsubscribe( "foo-channel", other_listener );  // Unsubscribe only one listener
        c.unsubscribe( "foo-channel" );   // Unsubscribe all listeners
        c.disconnect();
        */
        
        //        StompClient client = new StompClient( "localhost" );
        /*        try{
            
        StompClient client = new StompClient( "http://121.40.94.93/spider/hello" );
        client.connect();

        ClientSubscription subscription1 = 
            //            client.subscribe( "/queues/foo" )
            client.subscribe( "http://121.40.94.93/spider/topic/getwebevents" )
            //            .withMessageHandler( new Acumulator( "one" ) )
            //            .withMessageHandler( new MessageAcc )
            .withAckMode( AckMode.CLIENT_INDIVIDUAL )
            .start();

        System.out.println("cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccreate just test activity subbbbbed");
        }
        catch(URISyntaxException e){
            e.printStackTrace();
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
        catch(StompException e){
            e.printStackTrace();
        }
        catch(TimeoutException e){
            e.printStackTrace();
        }
        catch(SSLException e){
            e.printStackTrace();
        }
        */
        

        /*        ClientSubscription subscription2 = 
                  client.subscribe( "/queues/foo" )
                  .withMessageHandler( new Acumulator( "two" ) )
                  .withAckMode( AckMode.AUTO )
                  .start();

                  ClientTransaction tx = client.begin();

                  for (int i = 0; i < 10; ++i) {
                  tx.send( 
                  StompMessages.createStompMessage( "/queues/foo", "msg-" + i ) 
                  );
                  }

                  tx.commit();

        subscription1.unsubscribe();
        subscription2.unsubscribe();

        client.disconnect();
        */
    }


    /*    private class TestWebSocketTask extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... urls)
        {


        try{
            
        StompClient client = new StompClient( "ws://121.40.94.93:8080/spider/hello" );
        client.connect();

        ClientSubscription subscription1 = 
            //            client.subscribe( "/queues/foo" )
            client.subscribe( "http://121.40.94.93/spider/topic/getwebevents" )
            //            .withMessageHandler( new Acumulator( "one" ) )
            //            .withMessageHandler( new MessageAcc )
            .withAckMode( AckMode.CLIENT_INDIVIDUAL )
            .start();

             System.out.println("cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccreate just test activity subbbbbed");
        }
        catch(URISyntaxException e){
            e.printStackTrace();
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
        catch(StompException e){
            e.printStackTrace();
        }
        catch(TimeoutException e){
            e.printStackTrace();
        }
        catch(SSLException e){
            e.printStackTrace();
        }
        
            

            return "fake";
        }

        protected void onPostExecute(String result)
        {
        }
    }
    */
    


    /*    private class TestWebSocketTask extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... urls)
        {

            try {
                //    URI url = new URI("ws://127.0.0.1:8080/test");
    
                URI url = new URI("ws://121.40.94.93:8080/spider/hello");
                websocket = new WebSocket(url);

                // Register Event Handlers
                websocket.setEventHandler(new WebSocketEventHandler() {
                        public void onOpen()
                        {
                            System.out.println("------------------------open--------------------------------------------------------");
                        }

                        public void onMessage(WebSocketMessage message)
                        {
                            System.out.println("---------------------------------------------------------------------------------------------------received message: " + message.getText());
                        }

                        public void onClose()
                        {
                            System.out.println("---------------------------------------------------------------close");
                        }

                        public void onError(IOException ex)
                        {
                            System.out.println("---------------------------------------------------------------error");
                        }
            

                        public void onPing() {}
                        public void onPong() {}
                    });

                // Establish WebSocket Connection
                websocket.connect();

                // Send UTF-8 Text
                //    websocket.send("hello world");

                // Close WebSocket Connection
                //    websocket.close();
            }
            catch (WebSocketException wse) {
                wse.printStackTrace();
            }
            catch (URISyntaxException use) {
                use.printStackTrace();
            }            
            

            return "fake";
        }

        protected void onPostExecute(String result)
        {
        }
    }
    */
    


    /*    private class TestWebSocketTask extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... urls)
        {
			try {
				// cc = new ChatClient(new URI(uriField.getText()), area, ( Draft ) draft.getSelectedItem() );
                //                 cc = new WebSocketClient( new URI( uriField.getText() )) {
                 cc = new WebSocketClient( new URI( "ws://121.40.94.93/spider/hello" )) {

					@Override
					public void onMessage( String message ) {
                        System.out.println("1111kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
					}

					@Override
					public void onOpen( ServerHandshake handshake ) {
                        System.out.println("222kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
					}

					@Override
					public void onClose( int code, String reason, boolean remote ) {
                        System.out.println("3333kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
					}

					@Override
					public void onError( Exception ex ) {
                        System.out.println("44444kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
					}
				};

                 System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");

				cc.connect();
			} catch ( URISyntaxException ex ) {
                ex.printStackTrace();
                //				ta.append( uriField.getText() + " is not a valid WebSocket URI\n" );
			}
            return "fake";
        }

        protected void onPostExecute(String result)
        {
        }
    }
    */

    private class TestWebSocketTask extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... urls)
        {
            //            String ip = "http://121.40.94.93/spider/hello";
            //            String ip = "http://121.40.94.93:8080/spider/hello";
            //            int port = 8080;
            //            String ip = "121.40.94.93/spider/hello";
            String ip = "ws://121.40.94.93:8080/spider/hello";
            int port = 80;

            try {
                //            c = new Client( ip, port, "", "" );
                mClient = new StompClient( ip, port, "", "" );
                //            Log.i("Stomp", "Connection established");
                //            c.subscribe( channel, new Listener() {
                mClient.subscribe( mChannel, new StompListener() {
                        public void message( Map header, String message ) {
                            System.out.println("11111111111111111!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+message);
                            //Log.i("Stomp", "Message received!!!");
                        }
                    });

            } catch (IOException ex) {
                //            Log.e("Stomp", ex.getMessage());
                ex.printStackTrace();

            } catch (LoginException ex) {
                //            Log.e("Stomp", ex.getMessage());
                ex.printStackTrace();
            } catch (Exception ex) {
                //            Log.e("Stomp", ex.getMessage());
                ex.printStackTrace();
            }

            return "fake";
        }

        protected void onPostExecute(String result)
        {
        }
    }

    @Override
    //    protected void onDestroy() {
    public void onDestroy() {
        super.onDestroy();
        /*        try{
            
            client.disconnect();
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
        catch(TimeoutException e){
            e.printStackTrace();
        }
        catch(StompException e){
            e.printStackTrace();
        }
        */

        //cc.close();
        //        websocket.close();
        //        mClient.unsubscribe(mChannel);
        //        mClient.disconnect();
    }


    private void checkAndStartSpiderService()
    {
        if(!isMyServiceRunning(SpiderService.class)){
            System.out.println("+++++++++++++++++++++===================================+++++++++++++++++++++++++++++++++++++++++");
            Intent intent = new Intent(this, SpiderService.class);
            stopService(intent);
            startService(intent);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
    
}

