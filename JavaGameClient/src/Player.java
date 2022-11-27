
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

//class Player => new 가능한 애들!! 게임에 존재할 수 존재할 수 있음.(추상메소드를 가질 수 없다)
public class Player extends JLabel implements Moveable {

    private BubbleFrame mContext;
    private List<Bubble> bubbleList;

    //위치상태
    private int x;
    private int y;

    //플레이어의 방향
    private PlayerWay playerWay;

    //움직임 상태
    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;

    //플레이어 상태
    private int state = 0; // 0(살아있음), 1(사망)

    //벽에 출돌한 상태
    private boolean leftWallCrash;
    private boolean rightWallCrash;

    //플레이어 속도 상태(고정이므로 상수)
    private final int SPEED = 4;
    private final int JUMPSPEED = 2; //up, down

    private ImageIcon playerR, playerL, playerRdie, playerLdie;

    public Player(BubbleFrame mContext) {
        this.mContext = mContext;
        initObject();
        initSetting();
        initBackgroundPlayerService();
    }

    public void initObject() {
        playerR = new ImageIcon(getClass().getResource("image/playerR.png"));
        playerL = new ImageIcon(getClass().getResource("image/playerL.png"));
        playerRdie = new ImageIcon(getClass().getResource("image/playerRDie.png"));
        playerLdie = new ImageIcon(getClass().getResource("image/playerLDie.png"));
        bubbleList = new ArrayList<>();
    }

    public void initSetting() {
        x = 80 ;
        y = 535;

        left = false;
        right = false;
        up = false;
        down = false;

        leftWallCrash = false;
        rightWallCrash = false;

        playerWay = PlayerWay.RIGHT;
        this.setIcon(playerR);
        setSize(50,50);
        setLocation(x,y);
    }

    private void initBackgroundPlayerService() {
        new Thread(new BackgroundPlayerService(this)).start();
    }

    @Override
    public void attack() {
        new Thread(()->{
            Bubble bubble = new Bubble(mContext);
            mContext.add(bubble);
            bubbleList.add(bubble);
            if(playerWay == PlayerWay.LEFT) {
                bubble.left();
            }
            else {
                bubble.right();
            }
        }).start();
    }

    @Override
    public void left() {
        //System.out.println("left"); //스레드 생성
        playerWay = PlayerWay.LEFT;
        left = true; //움직이는 중

        new Thread(() -> { //runnable과 동일(람다식)
            while (left && getState() == 0) { // 스레드 종료(while없으면 계속 생성종료반복으로 낭비심함)
                setIcon(playerL);
                x = x - SPEED;
                setLocation(x, y);
                try { //sleep 안하면 너무 빨라서 우리 눈에 훅 하고 지나감
                    Thread.sleep(10);//0.01초
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }).start();


    }

    @Override
    public void right() {
        //System.out.println("right");
        playerWay = PlayerWay.RIGHT;
        right = true;
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        new Thread(() -> { //runnable과 동일(람다식)
            while (right && getState() == 0) {
                setIcon(playerR);
                x = x + SPEED;
                setLocation(x, y);
                try {
                    Thread.sleep(10);//0.01초
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }

    //left + up, right + up
    @Override
    public void up() {
        //System.out.println("up");
        up = true;
        new Thread(() -> {
            for(int i=0; i<130/JUMPSPEED; i++) {
                y -= JUMPSPEED;
                setLocation(x,y);
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            up = false; //키보드 뗄 때 false
            down();
        }).start();
    }

    @Override
    public void down() {
        //System.out.println("down");
        down = true;
        new Thread(() -> {
            //while문 사용시 지구 끝까지, for문 사용시 범위 존재
            //for(int i=0; i<130/JUMPSPEED; i++) {
            while (down) {
                y += JUMPSPEED;
                setLocation(x,y);
                try {
                    Thread.sleep(3 );
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            down = false;
        }).start();
    }

    public void die() {
        new Thread(() -> {
            setState(1);
            setIcon(PlayerWay.RIGHT == playerWay ? playerRdie : playerLdie);
            try {
                if(!isUp() && !isDown()) up();
                Thread.sleep(2000);
                mContext.remove(this);
                mContext.repaint();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.println("플레이어 사망");
        }).start();
    }
}
