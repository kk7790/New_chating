package org.example;

import java.util.ArrayList;
import java.util.List;

public class Share {
    public static int PORT = 9000;
    public static List<Integer> port = new ArrayList<Integer>();
    ArrayList<String> sendMessage = new ArrayList<>();

    //share에서 이름, 아이피, 채팅 갯수 저장하는 구조체
    String name;
    String ipAddress;
    int count;
}
