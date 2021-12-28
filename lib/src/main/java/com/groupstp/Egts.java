package com.groupstp;

import com.groupstp.model.Package;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;

public class Egts {
    private final Socket socket;
    private final DataOutputStream out;
    private final DataInputStream br;
    private short packageId = 0;

    public Egts(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new DataOutputStream(socket.getOutputStream());
        br = new DataInputStream(socket.getInputStream());
        Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
            try {
                out.close();
                br.close();
                socket.close();
                System.out.println("The server is shut down!");
            } catch (IOException e) { /* failed */ }
        }});
    }

    public byte[] sendMessage(double lat, double lng, int speed, int dir, int objectId, Date date, boolean isMove) {
        Package message = new Package(lat, lng, speed, dir, objectId, date, isMove, ++packageId);
        byte[] byteArray = new byte[2048];
        try {
            byte[] messageByte = message.encode();
            out.write(messageByte);
            br.read(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trim(byteArray);
    }

    private byte[] trim(byte[] bytes) {
        int i = bytes.length - 1;
        for (;i >= 0; i--) {
            if (bytes[i] != 0x00) {
                break;
            }
        }
        return Arrays.copyOf(bytes, ++i);
    }
}
