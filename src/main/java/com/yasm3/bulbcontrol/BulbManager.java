package com.yasm3.bulbcontrol;

import java.net.*;
import java.util.Vector;

public class BulbManager {

    private final Vector<Bulb> bulbs;

    public BulbManager() {
        bulbs = new Vector<Bulb>();
        fetch();
    }

    public void fetch() {
        // Yeelight SSDP Multicast
        String multicastAddress = "239.255.255.250";
        int port = 1982;
        long waitDuration = 5000L;
        long startTime = System.currentTimeMillis();

        // Search Message
        String message = "M-SEARCH * HTTP/1.1\r\n" +
                "HOST: 239.255.255.250:1982\r\n" +
                "MAN: \"ssdp:discover\"\r\n" +
                "ST: wifi_bulb\r\n";

        byte[] buffer = message.getBytes();

        // Create socket
        try {
            InetAddress group = InetAddress.getByName(multicastAddress);
            MulticastSocket socket = new MulticastSocket();

            // Bind to local IPv4 interface
            socket.setNetworkInterface(NetworkInterface.getByInetAddress(InetAddress.getByName("192.168.1.15")));

            // Prepare search UDP packet
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
            socket.send(packet);
            System.out.println("Message envoyé en mutlicast: " + message);

            // Prepare response
            byte[] receiveBuffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

            System.out.println("Recherche...");
            while (System.currentTimeMillis() - startTime < waitDuration) {
                try {
                    socket.setSoTimeout(1000);
                    socket.receive(responsePacket);
                    String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
                    bulbs.add(new Bulb(response));
                    if (bulbs.size() > 1) {
                        Bulb beforeLast = bulbs.get(bulbs.size() - 2);
                        if (bulbs.getLast().getHost().equals(beforeLast.getHost())) bulbs.removeLast();
                    } else {
                        System.out.println("Lampe trouvé à " + bulbs.getLast().getHost());
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("Socket Timeout");
                }
            }
            socket.close();
            System.out.println("FIN DE LA RECHERCHE...");
            System.out.println(bulbs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
