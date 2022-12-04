
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
    public Player player;
    public Player2 player2;
    private BubbleFrame mContext = this;
    //private Enemy enemy;
    private List<Enemy> enemyList; //컬렉션으로 관리
    
    public JavaGameClientView view;
    public int x;

    public BubbleFrame(String username, JavaGameClientView client_view) {
    	System.out.println();
    	UserName = username;
    	view = client_view;
    	
    	//JavaGameClientView client = new JavaGameClientView(username, ip_addr, port_no);
    	      
        initObject();
        initSetting();
        initListener();
        
        //keyPressed(KeyEvent e);
        setVisible(true);
    }

    public void doKeyPressed(ChatMsg cm) {
    	if(cm.data.equals("left")) {
    		if(!cm.UserName.equals(UserName)) {
        		player2.left();
        	}
    		else {
				player.left();
			}
    	}
    	else if(cm.data.equals("right")) {
    		if(!cm.UserName.equals(UserName)) {
        		player2.right();
        	}
    		else {
				player.right();
			}
    	}
    	else if(cm.data.equals("up")) {
    		if(!cm.UserName.equals(UserName)) {
        		player2.up();
        	}
    		else {
				player.up();
			}
    	}
    	else if(cm.data.equals("bubble")) {
    		if(!cm.UserName.equals(UserName)) {
        		player2.attack();
        	}
    		else {
				player.attack();
			}
    	}

    }
    
    public void doKeyReleased(ChatMsg cm) {
    	if(cm.data.equals("left-released")) {
    		if(!cm.UserName.equals(UserName))
    			player2.setLeft(false);
    		else {
    			player.setLeft(false);
    		}
    	}
    	else if(cm.data.equals("right-released")) {
    		if(!cm.UserName.equals(UserName))
    			player2.setRight(false);
    		else {
    			player.setRight(false);
    		}
    	}
    	
    }
    public void initObject() {
        //backgroundMap = new JLabel(new ImageIcon(BubbleFrame.class.getResource("image/BackgroundMap.png")));
        backgroundMap = new JLabel(new ImageIcon(getClass().getResource("image/BackgroundMap.png")));
        
        setContentPane(backgroundMap); //JLabel을 JPanel로 바꿔버림
        player = new Player(mContext, UserName, view);
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
        //setLocationRelativeTo(null); //JFrame 가운데 배치하기
        setLocation(0, 0);
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
                            //player.left();
                            ChatMsg msg = new ChatMsg(UserName, "300", "left");
                            view.SendObject(msg);
                            //keyPressed(e);
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if(!player.isRight() && !player.isRightWallCrash()) {
                            //player.right();
                            //x = player.getX();
                            //System.out.println(x);
                        	//keyPressed(e);
                            ChatMsg msg = new ChatMsg(UserName, "300", "right");
                            view.SendObject(msg);
                            
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if(!player.isUp() && !player.isDown()) {
                            //player.up();
                            ChatMsg msg = new ChatMsg(UserName, "300", "up");
                            view.SendObject(msg);
                        }
                        break;
                    case KeyEvent.VK_SPACE:
                        //Bubble bubble = new Bubble(mContext);
                        //add(bubble);
                        player.attack();
                        ChatMsg msg = new ChatMsg(UserName, "300", "bubble");
                        view.SendObject(msg);
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
                        //player.setLeft(false);
                    	ChatMsg msg = new ChatMsg(UserName, "300", "left-released");
                        view.SendObject(msg);  
                        break;
                    case KeyEvent.VK_RIGHT: 
                    	//player.setRight(false);
                    	ChatMsg msg2 = new ChatMsg(UserName, "300", "right-released");
                        view.SendObject(msg2);                        
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

    
   
    
    
    

}
