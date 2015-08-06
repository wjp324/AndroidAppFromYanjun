package me.irobo.stomp;

import java.util.Map;
import java.util.Iterator;
import java.io.OutputStream;
import java.io.IOException;

import de.roderick.weberknecht.*;

/**
 * (c)2005 Sean Russell
 */
class StompTransmitter {
    //  public static void transmit( StompCommand c, Map h, String b, java.io.OutputStream out ) throws IOException {
    public static void transmit( StompCommand c, Map h, String b,  WebSocket ws) throws IOException, WebSocketException {
      System.out.println("44ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss");

      System.out.println(c.toString());
      System.out.println(b);
      

      
      StringBuffer message = new StringBuffer( c.toString() );
      message.append( "\n" );

      if (h != null) {
          for (Iterator keys = h.keySet().iterator(); keys.hasNext(); ) {
              String key = (String)keys.next();
              String value = (String)h.get(key);
              message.append( key );
              message.append( ":" );
              message.append( value );
              message.append( "\n" );
          }
      }
      message.append( "\n" );
      
      if (b != null) message.append( b );
      
      message.append( "\000" );

      System.out.println("111===================================================================");

      System.out.println(message.toString());
      

      System.out.println("11111===================================================================");
      
      //      out.write( message.toString().getBytes( StompCommand.ENCODING ) );
      ws.send(message.toString());
  }
}
