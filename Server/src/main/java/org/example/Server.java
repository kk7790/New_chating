package org.example;

import org.json.JSONObject;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class Server {
    //    Share sh = new Share();
    int PORT = 9000;
    Socket socket;
    ServerSocket serverSocket;
    LinkedList<JSONObject> socketList;
    Map<Socket, Integer> socketMap;
    JSONObject jsonObject;

    int userNumber = 0;
    String userName;
    Integer count = 0;
    String msg;
    int indexCheck;

    public Server() throws IOException {
        userNumber++;
        serverSocket = new ServerSocket(PORT);
        socketList = new LinkedList<>();
        socketMap = new HashMap<>();//hash map 대신해서 jsonObject 로 바꿀꺼임
        jsonObject = new JSONObject();

        try {
            System.out.println("서버 실행");
            while (true) {
                socket = serverSocket.accept();
                System.out.println("클라이언트 접속");

//                System.out.println(jsonObject);
//                socketMap.put(socket, ++id); //socketList와 jsonObject가 생겨서 중복 소스로 인해 삭제
//                socketList.add(socket);  // 소켓맵과 중복 소스로 인해 삭제

                //클라이언트마다 스레드 생성
                //스레드가 없을 경우 readUTF() 메서드에서 block 발생
                new Thread(() -> {
                    //사용자 접속시 사용자의 json생성
                    jsonObject.put("socket", socket);
                    jsonObject.put("id", userNumber);
                    jsonObject.put("messageCount", 0);
                    socketList.add(jsonObject);
                    try {
                        DataInputStream in = new DataInputStream(socket.getInputStream());
                        revMsg(in, socket);
                    } catch (IOException e) {
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void revMsg(DataInputStream in, Socket client) {
        try {
            while (true) { //사용자 한테 메세지 받기 무한루프 시작
                msg = in.readUTF();
                socketList.forEach(info -> {
                    //메세지 갯수 카운트를 위해서
                    if (info.getJSONObject("socket").equals(client)) {
                        count = jsonObject.getInt("messageCount");
                    }
                    if (count == 0) { //메세지 갯수가 없으면 처음 사용자가 입력한건 닉네임으로 지정해줌
                        jsonObject.put("userName", msg); //이게 rcv에서만 적용되는지, 전체인지 확인해봐야함
                        for (int i = 0; i < socketList.size(); i++) {
                            firstSendMsg(client, msg);
                        }
//                        socketList.stream().filter(f -> f.getJSONObject("socket").equals(client)).collect(Collectors.toList());
                    } else { // 사용자 이름 입력시 채팅 보내기
                        userName = jsonObject.getString("userName");
                        System.out.println(userName + "님 : " + msg);
                        for (int i = 0; i < socketList.size(); i++) {
                            sendMsg(client, msg);
                        }
                    }
                });
//                socketList.forEach(info ->{
//                    if(!info.getJSONObject("count").equals("0")){
//                        for (int i = 0; i < socketMap.size(); i++) {
//                            sendMsg(client, msg);
//                        }
//                    }
//                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMsg(Socket client, String msg) {
//        Integer id = socketMap.get(client); //얘는 기존 코드
//        socketList.forEach(info->{ //socketMap 안쓰려고 새로 작성한 부분
//            if(info.getJSONObject("socket").equals(client)){
//                userName = jsonObject.getString("userName");
//            }
//        });
        //close 한 사용자 인덱스 번호 찾아서 삭제하기
            if (msg.equals("close")) {
                indexCheck = 0;
                socketList.forEach(info -> {
                    if (info.getJSONObject("client").equals(client)) {
                        socketList.remove(indexCheck);
                        System.out.println("사용자 삭제 완료");
                    }
                    indexCheck++;
                });

            } else {
                socketMap.forEach((key, value) -> {
                    try {
                        OutputStream out = key.getOutputStream();
                        PrintWriter writer = new PrintWriter(out, true);
                        writer.println(userName + "번님 : " + msg);
                    } catch (IOException e) {
                    }
                });

                // 아래 코드의 문제점 1.for문은 foreach와 중복된 반복문
                // 2. socketmap을 사용해도 되는데 socketlist로 인해 중복으로 인해 수정
//            for (int i = 0; i < socketMap.size(); i++) {
//                socketList.stream().forEach(so -> {
//                    try {
//                        OutputStream out = socket.getOutputStream();
                //            -> socket 이 아니라 so를 사용해야 하는데, socket을 사용해 최종 사용자한테만 값이
                // 전달 됐던 문제 발생
//                        PrintWriter writer = new PrintWriter(out, true);
//                        writer.println(id + "번님 : " + msg);
//                    } catch (IOException e) {
//                    }
//                });
//            }
            }

    }


    public void firstSendMsg(Socket client, String msg) {
        socketMap.forEach((key, value) -> {
            try {
                OutputStream out = key.getOutputStream();
                PrintWriter writer = new PrintWriter(out, true);
                writer.println(msg + "번님이 입장하셨습니다.");
            } catch (IOException e) {
            }
        });
    }

//    @Override
//    public void run() {
//        try {
//            while (true) {
//                socket = serverSocket.accept();
//                System.out.println("클라이언트 접속");
//                System.out.println(socket);
//                id++;
//                socketMap.put(socket, id);
//                socketList.add(socket);
//
//                //클라이언트마다 스레드 생성
//                //스레드가 없을 경우 readUTF() 메서드에서 block 발생
//                DataInputStream in = new DataInputStream(socket.getInputStream());
//                revMsg(in, socket);
//                Thread.sleep(1000);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
