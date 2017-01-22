package com.ifridgeTeam.ifridge.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

import static com.ifridgeTeam.ifridge.MainActivity.LOG_TAG;


/**
 * Created by Abel on 1/21/2017.
 */

public class FridgeNetworkHelper extends AsyncTask<Void, Void, String> {

    private String dstAddress = "192.168.1.101";
    private int dstPort = 5000;
    private String message = "recipe";
    private String response = "";
    private FridgeOnResponseListener listener;

    public FridgeNetworkHelper(FridgeOnResponseListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... arg0) {

        Socket socket = null;

        try {
            socket = new Socket(dstAddress, dstPort);

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(message);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
                    1024);
            byte[] buffer = new byte[1024];

            int bytesRead;
            InputStream inputStream = socket.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
			/*
             * notice: inputStream.read() will block if no data return
			 */
            socket.setSoTimeout(10000);
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8");
                if (response.substring(response.length()-2, response.length())
                        .equals("\r\n")) {
                    return response.substring(0, response.length() - 2);
                }
            }

        } catch (SocketTimeoutException e) {
            Log.e(LOG_TAG, "Timeout " + e);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error connecting to socket: " + e);
            return null;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        listener.onResponse(result);
        super.onPostExecute(result);
    }

    public interface FridgeOnResponseListener {
        void onResponse(String response);
    }
}
