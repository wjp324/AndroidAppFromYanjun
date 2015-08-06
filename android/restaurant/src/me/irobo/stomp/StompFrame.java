package me.irobo.stomp;

import java.util.Map;

public class StompFrame {
    private StompCommand _command;
    private Map _headers;
    private String _body;
    protected StompFrame( StompCommand c, Map h, String b ) {
        _command = c;
        _headers = h;
        _body = b;
    }
    public Map headers() { return _headers; }
    public String body() { return _body; }
    public StompCommand command() { return _command; }
}



