package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread{
    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    Scanner sc;
    int count;

    public Client(){
        try{
           socket = new Socket("localhost", 9000);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            sc = new Scanner(System.in);

            while(true){
                if(count == 0){
                    sendMsg();
                }
                else {
                    revMsg();
                    sendMsg();
                }
            }
            //위 두개 순서 바꾸면 block 발생..??
        } catch (IOException e){
        }
    }

    public void close(){
        try{
            System.out.println("연결끊김");
            socket.close();
            in.close();
            out.close();
        }catch(IOException e){
        }
    }
    public void revMsg(){
        new Thread(()->{
            try{
                while (true){
                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    System.out.println(reader.readLine());
                }
            }catch (IOException e){
            }
        }).start();
    }
    public void sendMsg(){
            try{
                while(true){
                    if(count == 0){
                        System.out.println("닉네임 : ");
                    }
                    else{
                        System.out.println("서버로 보낼 말 : ");
                    }
                    String msg = sc.nextLine();
                    out.writeUTF(msg);
                    if(msg.equals("close")){
                        System.out.println("연결이 종료되었습니다");
                        out.writeUTF(msg);
                        close();
                        break;
                    }
                }
            }catch (IOException e){
            }
    }
}
