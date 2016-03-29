package com.app.learniatstudent.XMPPClient;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.app.learniatstudent.Constants.SLConstants;
import com.app.learniatstudent.Utils.CommonUtils;
import com.app.learniatstudent.Utils.ObservingService;
import com.google.gson.Gson;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by macbookpro on 22/02/16.
 */
public class XMPPController {
    public static XMPPController _instance = null;
    private static Context _context;
    XMPPConnection connection = null;
    private boolean joined = false;
    private String roomName;
    private static Handler handler;

    private XMPPController(){}

    // Singleton pattern
    public static XMPPController getInstance(Context ctx){
        if(_instance == null){
            _instance = new XMPPController();
            _context = ctx;
        }
        if(handler == null){
            handler = new Handler();
        }
        return _instance;
    }
    public XMPPConnection getXMPPConnection(){
        return connection;
    }

    public void loginToXMPPServer(String userId, String pwd,String observer){
        new ConnectionTask(userId,pwd,observer).execute();
    }

    private class ConnectionTask extends AsyncTask {
        public static final String HOST = "52.76.85.25";
        public static final int PORT = 5222;
        public static final String SERVICE = "52.76.85.25";
        private String userID, userPassword, notificationsObserver;

        public ConnectionTask(String userId, String pwd, String obsrver) {
            userID = userId;
            userPassword = pwd;
            notificationsObserver = obsrver;
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT, SERVICE);
            connConfig.setReconnectionAllowed(true);
            if(connection ==null || !connection.isConnected()){
                connection = new XMPPConnection(connConfig);
                try {
                    connection.connect();
                    if(connection.isConnected()){
                        connection.login(userID, userPassword);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    connection = null;
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            try{
                if(connection != null){
                    connection.addConnectionListener(xmppConnectionListner);
                    if(!notificationsObserver.equalsIgnoreCase("")){
                        ObservingService.sharedManager(_context).postNotification(notificationsObserver, true);
                    }
                    // chat Packet receiving filter
                    PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
                    connection.addPacketListener(new PacketListener() {
                        public void processPacket(Packet packet) {
                            Message message = (Message) packet;
                            String body = message.getBody();
                            String from = message.getFrom();
                            processMessages(message);
                        }
                    }, filter);

                    PacketFilter responseFilter = new MessageTypeFilter(Message.Type.groupchat);

                    if(responseFilter == null){
                        return;
                    }
                            connection.addPacketListener(new PacketListener() {
                                @Override
                                public void processPacket(Packet packet) {
                                    Message message = (Message) packet;
                                    String body = message.getBody();
                                    String from = message.getFrom();
                                    processMessages(message);
                                }
                            },responseFilter);
                }
                else {
                    if(!notificationsObserver.equalsIgnoreCase("")){
                        ObservingService.sharedManager(_context).postNotification(notificationsObserver, false);
                    }
                }
            }catch (Exception w){
                CommonUtils.getInstance(_context).hideProgressDialog();
            }
        }
    }

    ConnectionListener xmppConnectionListner = new ConnectionListener() {
        @Override
        public void connectionClosed() {
        }

        @Override
        public void connectionClosedOnError(Exception e) {
        }

        @Override
        public void reconnectingIn(final int i) {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    handler.post(new Runnable() { // This thread runs in the UI
                        @Override
                        public void run() {
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                    .detectAll()
                                    .penaltyLog()
                                    .build();
                            StrictMode.setThreadPolicy(policy);
                            Toast.makeText(_context, "Stream re-connection...", Toast.LENGTH_SHORT).show();
                            try {
                                connection.disconnect();
                                CommonUtils.getInstance(_context).showXMPPReConnectPopUp("Stream Disconnected", "Do you want to reconnect?", _context);
//                                connection.connect();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            };
            new Thread(runnable).start();

        }

        @Override
        public void reconnectionSuccessful() {

        }

        @Override
        public void reconnectionFailed(Exception e) {

        }
    };

    // Process received message
    public void processMessages(Message msg){
        if(msg ==null) {
            return;
        }
        String body = msg.getBody();
        int fromCode = 0;
        int from = 0;
        JSONObject bodyObject = null;
        JSONObject obj = getJSONFromXML(body);
        try {
            JSONObject rootObject = obj.getJSONObject("Message");
            bodyObject = rootObject.getJSONObject("Body");
            fromCode = rootObject.getInt("Type");
            from = rootObject.getInt("From");
            if(from > 0){
                CommonUtils.getInstance(_context).setTeacherID(from);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
       switch (fromCode){
           case SLConstants.kTimeExtended:
                if(bodyObject !=null){
                    try {
                        if(bodyObject.getString("timedelay") !=null){
                            CommonUtils.getInstance(_context).setClassTimeExtendMessage(bodyObject.getString("timedelay"));
                            ObservingService.sharedManager(_context).postNotification(SLConstants.kXMPPMsgTimeExtended, true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
               break;
           case SLConstants.kSeatingChanged:
               if(bodyObject !=null){
                   ObservingService.sharedManager(_context).postNotification(SLConstants.kXMPPMsgSeatChanged, true);
               }
               break;

           case SLConstants.kTeacherEndsSession:
               if(bodyObject !=null){
                   ObservingService.sharedManager(_context).postNotification(SLConstants.kXMPPMsgTeacherEndsSessions, true);
               }
               break;
           case SLConstants.kStudentSentBenchState:

               default:

       }
    }

    private JSONObject getJSONFromXML(String body){
        JSONObject result = null;
        try {
            result = XML.toJSONObject(body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Join the room
    public synchronized void join(String completeAddress, long timeout) throws XMPPException {

        if(connection == null){
            return;
        }
        boolean gr = createGroup(completeAddress);
        if(!gr){
            return;
        }
            // Wait for a presence packet back from the server.
            PacketFilter responseFilter = new MessageTypeFilter(Message.Type.groupchat);
            if(responseFilter == null){
                return;
            }
            PacketCollector response;
            try {

                Presence joinPresence = new Presence(Presence.Type.available);
                joinPresence.setTo(completeAddress);



                response = connection.createPacketCollector(responseFilter);
                // Send join packet.
                connection.sendPacket(joinPresence);

                // Wait up to a certain number of seconds for a reply.
                Presence presence = (Presence)response.nextResult(timeout);
                response.cancel();
                if (presence == null) {
                    throw new XMPPException("No response from server.");
                }
                else if (presence.getError() != null) {
                    throw new XMPPException(presence.getError());
                }
                this.roomName = completeAddress;
                joined = true;
            }
            catch (Exception e){
                e.printStackTrace();
            }
    }

    public boolean createGroup(String groupName) {
        if (connection == null)
            return false;
        try {
            connection.getRoster().createGroup(groupName);
            Log.v("Group created : ", groupName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void sendMessage(String status) {
        if(connection !=null && connection.isConnected()){
            int tId = CommonUtils.getInstance(_context).getTeacherID();
            if(tId == 0){
                return;
            }
            Message msg = new Message(String.valueOf(tId), Message.Type.chat);
            msg.setBody("<BenchState>"+status+"</BenchState>");
            msg.setFrom(connection.getUser());
            msg.setProperty("Type","220");
            connection.sendPacket(msg);
        }
        else {
            Toast.makeText(_context,"Connection to xmpp server not established",Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isJoined() {
        return joined;
    }
    /**
     * Leave the chat room.
     */
    public synchronized void leave(String completeAddress) {
        // If not joined already, do nothing.
        if (!joined) {
            return;
        }
        // We leave a room by sending a presence packet where the "to"
        // field is in the form "roomName@service/nickname"
        Presence leavePresence = new Presence(Presence.Type.unavailable);
        leavePresence.setTo(completeAddress);
        connection.sendPacket(leavePresence);
        // Reset participant information.
//        participants = new ArrayList();
        roomName = null;
        joined = false;
    }

    public String getNickname() {
        return roomName;
    }
}
