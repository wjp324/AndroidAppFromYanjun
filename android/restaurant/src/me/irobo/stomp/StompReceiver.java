package me.irobo.stomp;

import java.io.*;
import java.util.HashMap;

public class StompReceiver
{
    private MessageReceiver _receiver;
    private BufferedReader _input;
    private String _stream;

    public StompReceiver(MessageReceiver m , String msg)
    {
        //        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@pppp");
        //        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+msg);
        setup(m, msg);
        //        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@xxxxxx");
        start();
    }

    protected void setup(MessageReceiver m, String input)
    {
        _receiver = m;
        //        try{
            _stream = input;
            InputStream is = new ByteArrayInputStream(input.getBytes());
            _input = new BufferedReader(new InputStreamReader(is));
            //            _input = new BufferedReader(new InputStreamReader(is,StompCommand.ENCODING));
            
            //        }catch (UnsupportedEncodingException e) {
            //            e.printStackTrace();
            //        }
    }

    protected void start()
    {
        try {
            // Get command
            if (_input.ready()) {
                String command = _input.readLine();
                if (command.length() > 0) {
                    try {
                        StompCommand c = StompCommand.valueOf( command );
                        // Get headers
                        HashMap headers = new HashMap();
                        String header;
                        while ((header = _input.readLine()).length() > 0) {
                            int ind = header.indexOf( ':' );
                            String k = header.substring( 0, ind );
                            String v = header.substring( ind+1, header.length() );
                            headers.put(k.trim(),v.trim());
                        }
                        // Read body
                        StringBuffer body = new StringBuffer();
                        int b;
                        while ((b = _input.read()) != 0) {
                            body.append( (char)b );
                        }

                        System.out.println("11@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+body.toString());
                        

                        try {
                            _receiver.receive( c, headers, body.toString() );
                        } catch (Exception e) {
                            // We ignore these errors; we don't want client code
                            // crashing our listener.

                            e.printStackTrace();
                        }
                    } catch (Error e) {
                        try {
                            while (_input.read() != 0);
                        } catch (Exception ex) { }
                        try {
                            _receiver.receive( StompCommand.ERROR, null, e.getMessage()+"\n" );
                        } catch (Exception ex) {
                            // We ignore these errors; we don't want client code
                            // crashing our listener.

                            e.printStackTrace();
                        }
                    }
                }
            } else {
                if (_receiver.isClosed()) {
                    _receiver.disconnect();
                    return;
                }
            }
        } catch (IOException e) {
            // What do we do with IO Exceptions?  Report it to the receiver, and 
            // exit the thread.
            System.err.println("Stomp exiting because of exception");
            e.printStackTrace( System.err );
            _receiver.receive( StompCommand.ERROR, null, e.getMessage() );
        } catch (Exception e) {
            System.err.println("Stomp exiting because of exception");
            e.printStackTrace( System.err );
            _receiver.receive( StompCommand.ERROR, null, e.getMessage() );
        }
        
    }
}

