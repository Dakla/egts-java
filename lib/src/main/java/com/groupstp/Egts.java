package com.groupstp;

import com.groupstp.model.EgtsMessage;
import com.groupstp.model.Package;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class Egts {
    private static short packageId = 0;

    public static void main(String[] args) {
    }

    public static byte[] sendMessage(String host, int port, EgtsMessage egtsMessage) {
        try (Socket socket = new Socket(host, port);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream br = new DataInputStream(socket.getInputStream()))
        {
            Package message = new Package(
                    egtsMessage.lat,
                    egtsMessage.lng,
                    egtsMessage.speed,
                    egtsMessage.dir,
                    egtsMessage.objectId,
                    egtsMessage.date,
                    egtsMessage.isMove,
                    ++packageId
            );
            byte[] byteArray = new byte[2048];
            byte[] messageByte = message.encode();
            out.write(messageByte);
            br.read(byteArray);
            return trim(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    private static byte[] trim(byte[] bytes) {
        int i = bytes.length - 1;
        for (;i >= 0; i--) {
            if (bytes[i] != 0x00) {
                break;
            }
        }
        return Arrays.copyOf(bytes, ++i);
    }
}
