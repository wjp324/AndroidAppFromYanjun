package me.irobo.stomp;

import java.util.Map;

public interface StompListener {
  public void message( Map headers, String body );
}
