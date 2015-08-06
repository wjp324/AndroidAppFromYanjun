package me.irobo.stomp;

import java.io.OutputStream;
import java.io.IOException;

/**
 * (c)2005 Sean Russell
 */
public class StompCommand {
  public final static String ENCODING = "US-ASCII";
  private String _command;

  private StompCommand( String msg ) { 
    _command = msg;
  }
  public static StompCommand SEND = new StompCommand( "SEND" ),
         SUBSCRIBE = new StompCommand( "SUBSCRIBE" ),
         UNSUBSCRIBE = new StompCommand( "UNSUBSCRIBE" ),
         BEGIN = new StompCommand( "BEGIN" ),
         COMMIT = new StompCommand( "COMMIT" ),
         ABORT = new StompCommand( "ABORT" ),
         DISCONNECT = new StompCommand( "DISCONNECT" ),
         CONNECT = new StompCommand( "CONNECT" );

  public static StompCommand MESSAGE = new StompCommand( "MESSAGE" ),
         RECEIPT = new StompCommand( "RECEIPT" ),
         CONNECTED = new StompCommand( "CONNECTED" ),
         ERROR = new StompCommand( "ERROR" );

  public static StompCommand valueOf( String v ) {
    v = v.trim();
    if (v.equals("SEND")) return SEND;
    else if (v.equals( "SUBSCRIBE" )) return SUBSCRIBE;
    else if (v.equals( "UNSUBSCRIBE" )) return UNSUBSCRIBE;
    else if (v.equals( "BEGIN" )) return BEGIN;
    else if (v.equals( "COMMIT" )) return COMMIT;
    else if (v.equals( "ABORT" )) return ABORT;
    else if (v.equals( "CONNECT" )) return CONNECT;
    else if (v.equals( "MESSAGE" )) return MESSAGE;
    else if (v.equals( "RECEIPT" )) return RECEIPT;
    else if (v.equals( "CONNECTED" )) return CONNECTED;
    else if (v.equals( "DISCONNECT" )) return DISCONNECT;
    else if (v.equals( "ERROR" )) return ERROR;
    throw new Error( "Unrecognised command "+v );
  }

  public String toString() {
    return _command;
  }
}

