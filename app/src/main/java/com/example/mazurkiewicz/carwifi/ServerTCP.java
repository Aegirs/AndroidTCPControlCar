package com.example.mazurkiewicz.carwifi;

/**
 * Created by mazurkiewicz on 18/03/15.
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class ServerTCP {
    ServerSocket serverSocket;
    static final int SocketServerPORT = 9090;
    int nbClient = 0;
    Socket scoketClient[] = new Socket[100];

    ServerTCP() {
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public void DestroyServer() {

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public int getLocalPort() {
        return serverSocket.getLocalPort();
    }

    public Socket[] getClient() {
        return scoketClient;
    }

    public String getInfoClient(Socket socket) {
        return socket.getInetAddress() + ":" + socket.getPort();
    }

    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }

    private class SocketServerThread extends Thread {

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(SocketServerPORT);

                while (true) {
                    Socket socket = serverSocket.accept();

                    if ( nbClient < 100 ) {
                        String name = getInfoClient(socket);

                        scoketClient[nbClient] = socket;
                        nbClient++;

                        Thread threadCLient = new ClientTCP(socket, nbClient,name);
                        threadCLient.start();
                    }
                    else {
                        OutputStream outputStream = socket.getOutputStream();
                        PrintStream printStream = new PrintStream(outputStream);
                        printStream.print("Sorry, Server full.\nTry again later\n");
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class ClientTCP extends Thread {
        private Socket hostThreadSocket;
        int cnt;
        String pseudo;

        ClientTCP(Socket socket, int c,String name) {
            hostThreadSocket = socket;
            cnt = c;
            pseudo = name;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "Hello from Android TCP Server, you are #" + cnt + "\n";

            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);

                InputStream in = hostThreadSocket.getInputStream();
                byte[] data      = new byte[1024];

                String msgIn = "";
                int bytesRead = 0;

                // problem impossible d'avoir msg.equals("end") == true !!
                while ( !(msgIn.equals("end\n")) && (bytesRead > -1) ) {
                    bytesRead=in.read(data);
                    msgIn = "";

                    for(int i =0; i < bytesRead;i++ ) {
                        msgIn += (char)data[i];
                    }

                    System.out.println(pseudo + " : " +  msgIn);
                    sendAllClient(pseudo + " : " +  msgIn);
                }
                in.close();
                printStream.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                //message += "Something wrong! " + e.toString() + "\n";
            }
        }
    }

    public void sendAllClient(String message) {
        for(int i=0; i < nbClient;i++) {
            OutputStream outputStream = null;
            try {
                outputStream = scoketClient[i].getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}