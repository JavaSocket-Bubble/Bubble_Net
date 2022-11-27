
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class BubbleFrame extends JFrame {

    private JLabel backgroundMap;
    private Player player;
    private Player2 player2;
    private BubbleFrame mContext = this;
    //private Enemy enemy;
    private List<Enemy> enemyList; //컬렉션으로 관리

    public BubbleFrame() {
        initObject();
        initSetting();
        initListener();
        setVisible(true);
    }

    public void initObject() {
        backgroundMap = new JLabel(new ImageIcon(BubbleFrame.class.getResource("image/BackgroundMap.png")));
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
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if(!player.isRight() && !player.isRightWallCrash()) {
                            player.right();
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


}
