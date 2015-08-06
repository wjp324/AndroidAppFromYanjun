package com.xmlrpc;

import org.xmlpull.v1.XmlPullParserException;

import com.xmlrpc.android.MethodCall;
import com.xmlrpc.android.XMLRPCServer;

import android.app.IntentService;
import android.content.Intent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class XMLRPCServerTest extends IntentService {
    public XMLRPCServerTest() {
        super("XMLRPCServerTest");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            ServerSocket socket = new ServerSocket(8888);
            XMLRPCServer server = new XMLRPCServer();
            while (true) {
                Socket client = socket.accept();
                MethodCall call = server.readMethodCall(client);
                String name = call.getMethodName();
                if (name.equals("add")) {
                    ArrayList<Object> params = call.getParams();
                    // assume "add" method has two Integer params, so no checks done
                    int i0 = (Integer) params.get(0);
                    int i1 = (Integer) params.get(1);
                    server.respond(client, i0 + i1);
                } else
                if (name.equals("test")) {
                    Object[] arr = new Object[] {
                            "String", 1, false
                    };
                    server.respond(client, arr);
                } else {
                    server.respond(client, null);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }
}
