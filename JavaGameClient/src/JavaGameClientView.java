

// JavaObjClientView.java ObjecStram 기반 Client
//실질적인 채팅 창
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

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.JToggleButton;
import javax.swing.JList;
import java.awt.Canvas;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JavaGameClientView extends JFrame {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtInput;
    
    private String UserName;
    private String ipAddr;
    private String portNo;
    
    private JButton btnSend;
    private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
    private Socket socket; // 연결소켓
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private JLabel lblUserName;
    // private JTextArea textArea;
    private JTextPane textArea;

    private Frame frame;
    private FileDialog fd;
    private JButton imgBtn;

    private JPanel panel;
    private JLabel lblMouseEvent;
    private Graphics gc;
    private int pen_size = 2; // minimum 2
    // 그려진 Image를 보관하는 용도, paint() 함수에서 이용한다.
    private Image panelImage = null;
    private Graphics gc2 = null;

    private final JLabel GameBackground = new JLabel("New label");
    
    public JavaGameClientView view;
    public BubbleFrame game;
    public int x;
    
    //public Player player;
    public Player2 player2;
    //public Player player = new Player(game, UserName, view);
    public Player player;
    public BubbleFrame mContext;
    public boolean IsReady = false;
    
    /**
     * Create the frame.
     * @throws BadLocationException
     */
    public JavaGameClientView(String username, String ip_addr, String port_no)  {
    	 view = this;
		 UserName = username;
		 ipAddr = ip_addr;
		 portNo = port_no;
		 player = new Player(mContext, username, view);
		 player2 = new Player2(mContext);
//----------------메인 창 세팅
        setUndecorated(false); // 위에 창을 없애준
        setTitle("BubbleBoggle");
        setSize(1000, 640); // 게임 화면 크기
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // x 버튼 누르면 꺼지게
        // setResizable(false); // 사이즈 조절 안되게
        getContentPane().setLayout(null);
        setVisible(true);
        //---------
        //setResizable(false);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setBounds(100, 100, 800, 634);
        //contentPane = new JPanel();
        //contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        //setContentPane(contentPane);
        //contentPane.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 10, 352, 471);
        getContentPane().add(scrollPane);
        scrollPane.setVisible(false);

        textArea = new JTextPane();
        textArea.setEditable(false);
        textArea.setFont(new Font("굴림체", Font.PLAIN, 14));
        scrollPane.setViewportView(textArea);

        txtInput = new JTextField();
        txtInput.setBounds(74, 489, 209, 40);
        txtInput.setVisible(false);
        getContentPane().add(txtInput);
        txtInput.setColumns(10);

//        btnSend = new JButton("Send");
//        btnSend.setFont(new Font("굴림", Font.PLAIN, 14));
//        btnSend.setBounds(295, 489, 69, 40);
//        getContentPane().add(btnSend);
//        btnSend.setVisible(false);

//        lblUserName = new JLabel("Name");
//        lblUserName.setBorder(new LineBorder(new Color(0, 0, 0)));
//        lblUserName.setBackground(Color.WHITE);
//        lblUserName.setFont(new Font("굴림", Font.BOLD, 14));
//        lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
//        lblUserName.setBounds(12, 539, 62, 40);
//        getContentPane().add(lblUserName);
//        lblUserName.setVisible(false);

        AppendText("User " + username + " connecting " + ip_addr + " " + port_no);
        UserName = username;
//        lblUserName.setText(username);


//        imgBtn = new JButton("+");
//        imgBtn.setFont(new Font("굴림", Font.PLAIN, 16));
//        imgBtn.setBounds(12, 489, 50, 40);
//        getContentPane().add(imgBtn);
//        imgBtn.setVisible(false);
//
//        JButton btnNewButton = new JButton("종 료");
//        btnNewButton.setFont(new Font("굴림", Font.PLAIN, 14));
//        btnNewButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                ChatMsg msg = new ChatMsg(UserName, "400", "Bye");
//                SendObject(msg);
//                System.exit(0);
//            }
//        });
//        btnNewButton.setBounds(295, 539, 69, 40);
//        getContentPane().add(btnNewButton);
//        btnNewButton.setVisible(false);

        panel = new JPanel();
        panel.setBorder(new LineBorder(new Color(0, 0, 0)));
        panel.setBackground(Color.WHITE);
        panel.setBounds(376, 10, 10, 10);
        //getContentPane().add(panel);
        panel.setVisible(false);
        gc = panel.getGraphics();

        // Image 영역 보관용. paint() 에서 이용한다.
//        panelImage = createImage(panel.getWidth(), panel.getHeight());
//        gc2 = panelImage.getGraphics();
//        gc2.setColor(panel.getBackground());
//        gc2.fillRect(0,0, panel.getWidth(),  panel.getHeight());
//        gc2.setColor(Color.BLACK);
//        gc2.drawRect(0,0, panel.getWidth()-1,  panel.getHeight()-1);

//        lblMouseEvent = new JLabel("<dynamic>");
//        lblMouseEvent.setHorizontalAlignment(SwingConstants.CENTER);
//        lblMouseEvent.setFont(new Font("굴림", Font.BOLD, 14));
//        lblMouseEvent.setBorder(new LineBorder(new Color(0, 0, 0)));
//        lblMouseEvent.setBackground(Color.WHITE);
//        lblMouseEvent.setBounds(376, 539, 400, 40);
//        getContentPane().add(lblMouseEvent);
//        lblMouseEvent.setVisible(false);

        GameBackground.setIcon(new ImageIcon(JavaGameClientView.class.getResource("./image/startPage.png")));
        GameBackground.setBounds(0, 0, 1000, 640);
        GameBackground.setVisible(true);
        getContentPane().add(GameBackground);

        initListener();

        try {
            socket = new Socket(ip_addr, Integer.parseInt(port_no));
//			is = socket.getInputStream();
//			dis = new DataInputStream(is);
//			os = socket.getOutputStream();
//			dos = new DataOutputStream(os);

            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());

            // SendMessage("/login " + UserName);
            ChatMsg obcm = new ChatMsg(UserName, "100", "Hello");
            SendObject(obcm);

            ListenNetwork net = new ListenNetwork();
            net.start();
            TextSendAction action = new TextSendAction();
            //btnSend.addActionListener(action);
            txtInput.addActionListener(action);
            txtInput.requestFocus();
        } catch (NumberFormatException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            AppendText("connect error");
        }

    }

    public void paint(Graphics g) {
        super.paint(g);
        // Image 영역이 가려졌다 다시 나타날 때 그려준다.
        //gc.drawImage(panelImage, 0, 0, this);
    }
    
    public void GameStart() {
    	System.out.println("hello");
    	ListenNetwork listen = new ListenNetwork();
    	listen.start();
    	game = new BubbleFrame(UserName, view);
    	view.setVisible(false);
    	game.setVisible(true);
    }

    // Server Message를 수신해서 화면에 표시
    class ListenNetwork extends Thread {
        public void run() {
            while (true) {
                try {

                    Object obcm = null;
                    String msg = null;
                    ChatMsg cm;
                    try {
                        obcm = ois.readObject();
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        break;
                    }
                    if (obcm == null)
                        break;
                    if (obcm instanceof ChatMsg) {
                        cm = (ChatMsg) obcm;
                        msg = String.format("[%s]\n%s", cm.UserName, cm.data);
                    } else
                        continue;
                    switch (cm.code) {
                        case "200": // chat message
                            if (cm.UserName.equals(UserName))
                                AppendTextR(msg); // 내 메세지는 우측에
                            else
                                AppendText(msg);
                            break;
                        case "300": 
                        	game.doKeyPressed(cm);
                        	game.doKeyReleased(cm);
                        	break;
                            
                        case "500": // Mouse Event 수신
                            //DoMouseEvent(cm);
                            break;
                            
                        case "800":
                        	System.out.println(cm);
                        	view.setVisible(false);
                        	if(cm.UserName.equals("server")){
	                        	System.out.println("받았어!!!800번");
	                        	view.setVisible(false);
	                        	game = new BubbleFrame(UserName, view);
	                        	game.setVisible(true);
	                        	game.requestFocus();
                        	}
                        	//GameStart();
                        	break;
                    }
                } catch (IOException e) {
                    AppendText("ois.readObject() error");
                    try {
//						dos.close();
//						dis.close();
                        ois.close();
                        oos.close();
                        socket.close();

                        break;
                    } catch (Exception ee) {
                        break;
                    } // catch문 끝
                } // 바깥 catch문끝

            }
        }
    }

    // keyboard enter key 치면 서버로 전송
    class TextSendAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Send button을 누르거나 메시지 입력하고 Enter key 치면
            if (e.getSource() == btnSend || e.getSource() == txtInput) {
                String msg = null;
                // msg = String.format("[%s] %s\n", UserName, txtInput.getText());
                msg = txtInput.getText();
                SendMessage(msg);
                txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
                txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
                if (msg.contains("/exit")) // 종료 처리
                    System.exit(0);
            }
        }
    }

    class ImageSendAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 액션 이벤트가 sendBtn일때 또는 textField 에세 Enter key 치면
            if (e.getSource() == imgBtn) {
                frame = new Frame("이미지첨부");
                fd = new FileDialog(frame, "이미지 선택", FileDialog.LOAD);
                // frame.setVisible(true);
                // fd.setDirectory(".\\");
                fd.setVisible(false);
                // System.out.println(fd.getDirectory() + fd.getFile());
                if (fd.getDirectory().length() > 0 && fd.getFile().length() > 0) {
                    ChatMsg obcm = new ChatMsg(UserName, "300", "IMG");
                    ImageIcon img = new ImageIcon(fd.getDirectory() + fd.getFile());
                    obcm.img = img;
                    SendObject(obcm);
                }
            }
        }
    }

    ImageIcon icon1 = new ImageIcon("src/icon1.jpg");

    public void AppendIcon(ImageIcon icon) {
        int len = textArea.getDocument().getLength();
        // 끝으로 이동
        textArea.setCaretPosition(len);
        textArea.insertIcon(icon);
    }

    // 화면에 출력
    public void AppendText(String msg) {
        // textArea.append(msg + "\n");
        // AppendIcon(icon1);
        msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
        //textArea.setCaretPosition(len);
        //textArea.replaceSelection(msg + "\n");

        StyledDocument doc = textArea.getStyledDocument();
        SimpleAttributeSet left = new SimpleAttributeSet();
        StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(left, Color.BLACK);
        doc.setParagraphAttributes(doc.getLength(), 1, left, false);
        try {
            doc.insertString(doc.getLength(), msg+"\n", left );
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int len = textArea.getDocument().getLength();
        textArea.setCaretPosition(len);


    }
    // 화면 우측에 출력
    public void AppendTextR(String msg) {
        msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
        StyledDocument doc = textArea.getStyledDocument();
        SimpleAttributeSet right = new SimpleAttributeSet();
        StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
        StyleConstants.setForeground(right, Color.BLUE);
        doc.setParagraphAttributes(doc.getLength(), 1, right, false);
        try {
            doc.insertString(doc.getLength(),msg+"\n", right );
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int len = textArea.getDocument().getLength();
        textArea.setCaretPosition(len);
        //textArea.replaceSelection("\n");

    }

    public void AppendImage(ImageIcon ori_icon) {
        int len = textArea.getDocument().getLength();
        textArea.setCaretPosition(len); // place caret at the end (with no selection)
        Image ori_img = ori_icon.getImage();
        Image new_img;
        ImageIcon new_icon;
        int width, height;
        double ratio;
        width = ori_icon.getIconWidth();
        height = ori_icon.getIconHeight();
        // Image가 너무 크면 최대 가로 또는 세로 200 기준으로 축소시킨다.
        if (width > 200 || height > 200) {
            if (width > height) { // 가로 사진
                ratio = (double) height / width;
                width = 200;
                height = (int) (width * ratio);
            } else { // 세로 사진
                ratio = (double) width / height;
                height = 200;
                width = (int) (height * ratio);
            }
            new_img = ori_img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            new_icon = new ImageIcon(new_img);
            textArea.insertIcon(new_icon);
        } else {
            textArea.insertIcon(ori_icon);
            new_img = ori_img;
        }
        len = textArea.getDocument().getLength();
        textArea.setCaretPosition(len);
        textArea.replaceSelection("\n");
        // ImageViewAction viewaction = new ImageViewAction();
        // new_icon.addActionListener(viewaction); // 내부클래스로 액션 리스너를 상속받은 클래스로
        // panelImage = ori_img.getScaledInstance(panel.getWidth(), panel.getHeight(), Image.SCALE_DEFAULT);

        gc2.drawImage(ori_img,  0,  0, panel.getWidth(), panel.getHeight(), panel);
        gc.drawImage(panelImage, 0, 0, panel.getWidth(), panel.getHeight(), panel);
    }

    // Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
    public byte[] MakePacket(String msg) {
        byte[] packet = new byte[BUF_LEN];
        byte[] bb = null;
        int i;
        for (i = 0; i < BUF_LEN; i++)
            packet[i] = 0;
        try {
            bb = msg.getBytes("euc-kr");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
        }
        for (i = 0; i < bb.length; i++)
            packet[i] = bb[i];
        return packet;
    }

    // Server에게 network으로 전송
    public void SendMessage(String msg) {
        try {
            // dos.writeUTF(msg);
//			byte[] bb;
//			bb = MakePacket(msg);
//			dos.write(bb, 0, bb.length);
            ChatMsg obcm = new ChatMsg(UserName, "200", msg);
            oos.writeObject(obcm);
        } catch (IOException e) {
            // AppendText("dos.write() error");
            AppendText("oos.writeObject() error");
            try {
//				dos.close();
//				dis.close();
                ois.close();
                oos.close();
                socket.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                System.exit(0);
            }
        }
    }

    public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
        try {
            oos.writeObject(ob);
        } catch (IOException e) {
            // textArea.append("메세지 송신 에러!!\n");
            AppendText("SendObject Error");
        }
    }

    public void initListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            //키보드 클릭 핸들러 이벤트
            public void keyPressed(KeyEvent e) {
            	ChatMsg msg = new ChatMsg(UserName, "800", "Ready");            	
                SendObject(msg);
//                ListenNetwork net = new ListenNetwork();
//                net.start();
            }
        });
    }
}

