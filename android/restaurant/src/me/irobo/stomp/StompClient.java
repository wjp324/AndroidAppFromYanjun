package me.irobo.stomp;

import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.net.Socket;
import javax.security.auth.login.LoginException;

import java.lang.InterruptedException;

import java.net.URISyntaxException;
import java.net.URI;


import de.roderick.weberknecht.*;

import org.json.JSONObject;
import org.json.JSONException;





/**
 * Implements a Stomp client connection to a Stomp server via the network.  
 *
 * Example:
 * <pre>
 *   Client c = new Client( "localhost", 61626, "ser", "ser" );
 *   c.subscribe( "/my/channel", new StompListener() { ... } );
 *   c.subscribe( "/my/other/channel", new StompListener() { ... } );
 *   c.send( "/other/channel", "Some message" );
 *   // ...
 *   c.disconnect();
 * </pre>
 *
 * @see Stomp
 *
 * (c)2005 Sean Russell
 */
public class StompClient extends Stomp implements MessageReceiver {
    private Thread _listener;
    private OutputStream _output;
    private InputStream _input;
    private Socket _socket;
    private WebSocket _ws;

    //    private static final int MAX_CONNECT_ATTEMPT = 20000;
    private static final int MAX_CONNECT_ATTEMPT = 3600*24;
    private int conn_atmpt_cn = 0;
    
    /**
     * Connects to a server
     *
     * Example:
     * <pre>
     *   Client stomp_client = new Client( "host.com", 61626 );
     *   stomp_client.subscribe( "/my/messages" );
     *   ...
     * </pre>
     *
     * @see Stomp
     * @param server The IP or host name of the server
     * @param port The port the server is listening on
     */
    public StompClient( String server, int port, String login, String pass ) throws IOException, LoginException {
    //    public StompClient( String server, String login, String pass ) throws IOException, LoginException {
        //        _socket = new Socket( server, port );
        //        _input = _socket.getInputStream();
        //        _output = _socket.getOutputStream();

        try {
            //    URI url = new URI("ws://127.0.0.1:8080/test");
            URI url = new URI(server);
            _ws = new WebSocket(url);

            _ws.setEventHandler(new WebSocketEventHandler() {
                    public void onOpen()
                    {
                        System.out.println("------------------------open--------------------------------------------------------");
                    }

                    public void onMessage(WebSocketMessage message)
                    {
                        //                        System.out.println("---------------------------------------------------------------------------------------------------received message: " + message.getText());
                        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@pppp111");
                        new StompReceiver(StompClient.this, message.getText());

                        /*                        Map tHead = new HashMap<String, String>();
                        tHead.put("destination","/spider/app/addevefromweb");
                        tHead.put("content-length","30");
                        transmit( StompCommand.SEND, tHead, "{'name':'uuuuuuu66666666','sec':3960}" );
                        */

                        /*                        Map thead1 = new HashMap<String, String>();
                        thead1.put("destination","/spider/topic/getwebevents");
                        thead1.put("id","andsub-0");
                        //                        thead1.put("receipt","555");
                        
                        transmit( StompCommand.SUBSCRIBE, thead1, null);
                        */

                        /*                        Map tHead = new HashMap<String, String>();
                        tHead.put("destination","/spider/app/addevefromweb");
                        tHead.put("content-length","33");

                        try{

                            JSONObject tCon = new JSONObject();
                            tCon.put("name", "cccccc8888");
                            tCon.put("sec", 33300);

                            transmit( StompCommand.SEND, tHead, tCon.toString());
                        }
                        catch(JSONException e){
                            e.printStackTrace();
                        }
                        
                        Map thead2 = new HashMap<String, String>();
                        thead2.put("destination","/spider/app/addevefromweb");
                        thead2.put("content-length","33");


                        try{

                            JSONObject tCon = new JSONObject();
                            tCon.put("name", "cccccc9999");
                            tCon.put("sec", 35300);

                            transmit( StompCommand.SEND, thead2, tCon.toString());
                        }
                        catch(JSONException e){
                            e.printStackTrace();
                        }

                        */
                        
                        
                        
                    }

                    public void onClose()
                    {
                        System.out.println("---------------------------------------------------------------close");
                        System.out.println("---------------------------------------------------------------connect again");
                        //connect();
                        tryConnect();
                    }

                    public void onError(IOException ex)
                    {
                        System.out.println("---------------------------------------------------------------error");
                    }
            

                    public void onPing() {}
                    public void onPong() {}
                });
            
            /*            _socket = _ws.getSocket();
            _input = _socket.getInputStream();
            _output = _socket.getOutputStream();
            */
            connect();
            /*            _ws.connect();
            Map thead1 = new HashMap<String, String>();
            thead1.put("accept-version","1.1,1.0");
            thead1.put("heart-beat","10000,10000");
            transmit( StompCommand.CONNECT, thead1, null);
            */
            
            /*            Map thead31 = new HashMap<String, String>();
            thead31.put("destination","/spider/topic/getwebevents");
            thead31.put("id","andsub-0");
                        //                        thead31.put("receipt","555");
                        
            transmit( StompCommand.SUBSCRIBE, thead31, null);
            */
            
            

            /*            Map tHead = new HashMap<String, String>();
            tHead.put("destination","/spider/app/addevefromweb");
            tHead.put("content-length","30");
            transmit( StompCommand.SEND, tHead, "{'name':'uuuuuuu66666666','sec':3960}" );
            */
        }
        catch (WebSocketException wse) {
            wse.printStackTrace();
        }
        catch (URISyntaxException use) {
            use.printStackTrace();
        }            

        //        _listener = new Receiver( this, _input );
        //        _listener.start();

        // Connect to the server
        /*        HashMap header = new HashMap();
        header.put( "login", login );
        header.put( "passcode", pass );
        transmit( StompCommand.CONNECT, header, null );
        try {
            String error = null;
            while (!isConnected() && ((error=nextError()) == null)) { 
                Thread.sleep(100); 
            }
            if (error != null) throw new LoginException( error );
        } catch (InterruptedException e) {
        }
        */
    }

    public void connect()
    {
        _ws.connect();
        Map thead1 = new HashMap<String, String>();
        thead1.put("accept-version","1.1,1.0");
        thead1.put("heart-beat","10000,10000");
        transmit( StompCommand.CONNECT, thead1, null);
    }

    public void tryConnect()
    {
        while(conn_atmpt_cn<MAX_CONNECT_ATTEMPT){
            try{
                _ws.connect();
                System.out.println("tttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt web socket reconnect ok!");

                Map thead1 = new HashMap<String, String>();
                thead1.put("accept-version","1.1,1.0");
                thead1.put("heart-beat","10000,10000");
                transmit( StompCommand.CONNECT, thead1, null);



                reRegSubscribes();
                

                /*                //fake add to trigger receive
                Map tHead = new HashMap<String, String>();
                tHead.put("destination","/spider/app/addevefromweb");
                tHead.put("content-length","30");
                transmit( StompCommand.SEND, tHead, "{'name':'fake','sec':3960}" );


                Map thead66 = new HashMap<String, String>();
                thead66.put("destination","/spider/app/addevefromweb");
                thead66.put("content-length","30");
                transmit( StompCommand.SEND, thead66, "{'name':'fake66','sec':3960}" );
                */
                
                
                conn_atmpt_cn = 0;
                break;
            }
            catch(WebSocketException e){
                System.out.println("tttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt got websocket exception");
                System.out.println("tttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttring to reconnect to websocket " +"attempt:"+conn_atmpt_cn);
                conn_atmpt_cn++;
                e.printStackTrace();


                try{
                    Thread.sleep(1000);
                }
                catch(InterruptedException ex){
                    ex.printStackTrace();
                }
                
                continue;
            }
            
        }
    }

    public boolean isClosed() { return _socket.isClosed(); }

    public void disconnect( Map header ) {
        if (!isConnected()) return;
        transmit( StompCommand.DISCONNECT, header, null );
        _listener.interrupt();
        Thread.yield();
        try { _input.close();  } catch (IOException e) {/* We ignore these. */}
        try { _output.close(); } catch (IOException e) {/* We ignore these. */}
        try { _socket.close(); } catch (IOException e) {/* We ignore these. */}
        _connected = false;
    }

    /**
     * Transmit a message to the server
     */
    public void transmit( StompCommand c, Map h, String b ) {
        try {
            //            StompTransmitter.transmit( c, h, b, _output );
            StompTransmitter.transmit( c, h, b, _ws );
        } catch (Exception e) {
            receive( StompCommand.ERROR, null, e.getMessage() );
        }
    }

    public void connectedInit()
    {
        //fake add to trigger receive
        /*        Map tHead = new HashMap<String, String>();
        tHead.put("destination","/spider/app/addevefromweb");
        tHead.put("content-length","30");
        transmit( StompCommand.SEND, tHead, "{'name':'fake','sec':3960}" );


        Map thead66 = new HashMap<String, String>();
        thead66.put("destination","/spider/app/addevefromweb");
        thead66.put("content-length","30");
        transmit( StompCommand.SEND, thead66, "{'name':'fake66','sec':3960}" );
        */

        Map tHead = new HashMap<String, String>();
        tHead.put("destination","/spider/app/addevefromweb");
        tHead.put("content-length","26");

        try{

            JSONObject tCon = new JSONObject();
            tCon.put("name", "fake");
            tCon.put("sec", 3000);

            transmit( StompCommand.SEND, tHead, tCon.toString());
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        /*        Map thead55 = new HashMap<String, String>();
        thead55.put("destination","/spider/app/addevefromweb");
        thead55.put("content-length","28");

        try{

            JSONObject tCon = new JSONObject();
            tCon.put("name", "fake77");
            tCon.put("sec", 3000);

            transmit( StompCommand.SEND, thead55, tCon.toString());
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        */
        
    }
}
