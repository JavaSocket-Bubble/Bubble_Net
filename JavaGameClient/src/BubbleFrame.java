
import lombok.Getter;
import lombok.Setter;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class BubbleFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
    private Socket socket; // 연결소켓
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;

    private ObjectInputStream ois;
    private ObjectOutputStream oos2;

    private String UserName;
    private String ipAddr;
    private String portNo;
    
    private JLabel backgroundMap;
    private Player player;
    private Player2 player2;
    private BubbleFrame mContext = this;
    //private Enemy enemy;
    private List<Enemy> enemyList; //컬렉션으로 관리

    public BubbleFrame(String username, String ip_addr, String port_no) {
    	UserName = username;
    	try {
    		socket = new Socket(ip_addr, Integer.parseInt(port_no));
        	oos2 = new ObjectOutputStream(socket.getOutputStream());
            oos2.flush();
            ois = new ObjectInputStream(socket.getInputStream());
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("오류인가 요기가");
		}
    	//JavaGameClientView client = new JavaGameClientView(username, ip_addr, port_no);
    	      
        initObject();
        initSetting();
        initListener();
        setVisible(true);
    }

    public void initObject() {
        //backgroundMap = new JLabel(new ImageIcon(BubbleFrame.class.getResource("image/BackgroundMap.png")));
        backgroundMap = new JLabel(new ImageIcon(getClass().getResource("image/BackgroundMap.png")));
        
        setContentPane(backgroundMap); //JLabel을 JPanel로 바꿔버림
        player = new Player(mContext);
        add(player);
        player2 = new Player2(mContext);
        add(player2);
//        enemy = new Enemy(mContext);
//        add(enemy);
        enemyList = new ArrayList<>();
        enemyList.add(new Enemy(mContext, EnemyWay.RIGHT));
        enemyList.add(new Enemy(mContext, EnemyWay.LEFT));
        for(Enemy e : enemyList) add(e);
        //new BGM();
    }

    public void initSetting() {
        setSize(1000, 640);
        setLayout(null); //absoulte 레이아웃(자유롭게 그림을 그릴 수 있다.
        setLocationRelativeTo(null); //JFrame 가운데 배치하기
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //x버튼으로 창을 끌 때 JVM같이 종료
    }

    public void initListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            //키보드 클릭 핸들러 이벤트
            public void keyPressed(KeyEvent e) {
                //System.out.println(e.getKeyCode());
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        //left가 private boolean이므로 게터 사용시 is라고 해야함.
                        if(!player.isLeft() && !player.isLeftWallCrash()) {
                            player.left();
                            //sendObject();
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if(!player.isRight() && !player.isRightWallCrash()) {
                            player.right();
                            ChatMsg msg = new ChatMsg(UserName, "300", "right");
                            System.out.println(msg);
                            SendObject(msg);
                            
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if(!player.isUp() && !player.isDown()) {
                            player.up();
                        }
                        break;
                    case KeyEvent.VK_SPACE:
                        //Bubble bubble = new Bubble(mContext);
                        //add(bubble);
                        player.attack();
                        break;
                }

                switch (e.getKeyChar()) {
                    case 'a':
                        if(!player2.isLeft() && !player2.isLeftWallCrash()) {
                            player2.left();
                        }
                        break;
                    case 'd':
                        if(!player2.isRight() && !player2.isRightWallCrash()) {
                            player2.right();
                        }
                        break;
                    case 'w':
                        if(!player2.isUp() && !player2.isDown()) {
                            player2.up();
                        }
                        break;
                    case 'k': //버블
                        player2.attack();
                        break;
                }
            }

            //키보드 해제 핸들러 이벤트
            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        player.setLeft(false);
                        break;
                    case KeyEvent.VK_RIGHT:
                        player.setRight(false);
                        break;
                }

                switch (e.getKeyChar()) {
                    case 'a':
                        player2.setLeft(false);
                        break;
                    case 'd':
                        player2.setRight(false);
                        break;
                }
            }
        });
    }

    
    //jyjyjyjy
    public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
        try {
            oos2.writeObject(ob);
        } catch (IOException e) {
            // textArea.append("메세지 송신 에러!!\n");
//            AppendText("SendObject Error");
        	System.out.println("SendObject Error");
        }
    }
    
    
    //정은 맘대로 해보기
//    
// // Server Message를 수신해서 화면에 표시
//    class ListenNetwork extends Thread {
//        public void run() {
//            while (true) {
//                try {
//
//                    Object obcm = null;
//                    String msg = null;
//                    ChatMsg cm;
//                    try {
//                        obcm = ois.readObject();
//                    } catch (ClassNotFoundException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                        break;
//                    }
//                    if (obcm == null)
//                        break;
//                    if (obcm instanceof ChatMsg) {
//                        cm = (ChatMsg) obcm;
//                        msg = String.format("[%s]\n%s", cm.UserName, cm.data);
//                    } else
//                        continue;
//                    switch (cm.code) {
////                        case "200": // chat message
////                            if (cm.UserName.equals(UserName))
////                                AppendTextR(msg); // 내 메세지는 우측에
////                            else
////                                AppendText(msg);
////                            break;
////                        case "300": // Image 첨부
////                            if (cm.UserName.equals(UserName))
////                                AppendTextR("[" + cm.UserName + "]");
////                            else
////                                AppendText("[" + cm.UserName + "]");
////                            AppendImage(cm.img);
////                            break;
//                        case "500": // Mouse Event 수신
//                            //DoMouseEvent(cm);
//                        	//sendKeyEvent(cm);
//                            break;
//                    }
//                } catch (IOException e) {
//                    System.out.println("ois.readObject() error");
//                    try {
////						dos.close();
////						dis.close();
//                        ois.close();
//                        oos.close();
//                        socket.close();
//
//                        break;
//                    } catch (Exception ee) {
//                        break;
//                    } // catch문 끝
//                } // 바깥 catch문끝
//
//            }
//        }
//    }
//    
//
//    // Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
//    public byte[] MakePacket(String msg) {
//        byte[] packet = new byte[BUF_LEN];
//        byte[] bb = null;
//        int i;
//        for (i = 0; i < BUF_LEN; i++)
//            packet[i] = 0;
//        try {
//            bb = msg.getBytes("euc-kr");
//        } catch (UnsupportedEncodingException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            System.exit(0);
//        }
//        for (i = 0; i < bb.length; i++)
//            packet[i] = bb[i];
//        return packet;
//    }
//
//
//    public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
//        try {
//            oos.writeObject(ob);
//        } catch (IOException e) {
//            // textArea.append("메세지 송신 에러!!\n");
//            System.out.println("SendObject Error");
//        }
//    }
//    
//    public void sendKeyEvent(KeyEvent e) {
//		ChatMsg cm = new ChatMsg(UserName, "500", "Key");
//		cm.key_e = e;
//		SendObject(cm);
//  }
    

}
