package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Server implements Runnable {
//    Share sh = new Share();
    int PORT= 9000;
    Socket socket;
    ServerSocket serverSocket;
    LinkedList<Socket> socketList;
    Map<Socket, Integer> socketMap;

    int id;

    public Server() throws IOException {
        id = 0;
        serverSocket = new ServerSocket(PORT++);
        socketList = new LinkedList<>();
        socketMap = new HashMap<>();
//        try {
//            serverSocket = new ServerSocket(9000);
//            socketList = new LinkedList<>();
//            socketMap = new HashMap<>();
//            int id = 0; // 아이디
//            System.out.println("서버 실행");
//            while (true) {
//                socket = serverSocket.accept();
//                System.out.println("클라이언트 접속");
//                System.out.println(socket);
//                socketMap.put(socket, ++id);
//                socketList.add(socket);
//
//                //클라이언트마다 스레드 생성
//                //스레드가 없을 경우 readUTF() 메서드에서 block 발생
//                new Thread(() -> {
//                    try {
//                        DataInputStream in = new DataInputStream(socket.getInputStream());
//                        revMsg(in, socket);
//                    } catch (IOException e) {
//                    }
//                }).start();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void revMsg(DataInputStream in, Socket client) {
        Integer id = socketMap.get(client);
        try {
            while (true) {
                String msg = in.readUTF();
                System.out.println(id + "번 : " + msg);
                for (int i = 0; i < socketMap.size(); i++) {
                    sendMsg(client, msg);
                }
                if (!msg.equals("")) {
                    sendMsg(client, msg);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMsg(Socket client, String msg) {
        Integer id = socketMap.get(client);
        if (msg.equals("close")) {
            socketMap.remove(id);
        } else {
            for (int i = 0; i < socketMap.size(); i++) {
                socketList.stream().forEach(so -> {
                    try {
                        OutputStream out = socket.getOutputStream();
                        PrintWriter writer = new PrintWriter(out, true);
                        writer.println(id + "번님 : " + msg);
                    } catch (IOException e) {
                    }
                });
            }
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                socket = serverSocket.accept();
                System.out.println("클라이언트 접속");
                System.out.println(socket);
                id++;
                socketMap.put(socket, id);
                socketList.add(socket);

                //클라이언트마다 스레드 생성
                //스레드가 없을 경우 readUTF() 메서드에서 block 발생
                DataInputStream in = new DataInputStream(socket.getInputStream());
                revMsg(in, socket);
                Thread.sleep(1000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
