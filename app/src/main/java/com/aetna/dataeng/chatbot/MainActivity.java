package com.aetna.dataeng.chatbot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aetna.dataeng.chatbot.myapplication.R;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ChatView chatView = (ChatView) findViewById(R.id.chat_view);
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener(){
            @Override
            public boolean sendMessage(ChatMessage chatMessage){
                // perform actual message sending
                String msg = chatMessage.getMessage();
                performNetworkCall(msg, chatView);
                return true;
            }
        });
    }

    private void performNetworkCall(String msg, ChatView chatView) {
        final String url = "http://10.0.0.81:8080/ChatBotNLPService/chat/chatservice/interact";
        final ChatView chatView1 = chatView;
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("message",msg);
        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ChatMessage message = null;
                        try {
                            message = new ChatMessage(response.getString("message"), System.currentTimeMillis(), ChatMessage.Type.RECEIVED);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            VolleyLog.e("JSON Error");
                        }
                        VolleyLog.v("Response:%n %s", response.toString());
                        chatView1.addMessage(message);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue().add(req);

    }
}
