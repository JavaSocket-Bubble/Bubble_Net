
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

@Getter
@Setter

//class Enemy => new 가능한 애들!! 게임에 존재할 수 존재할 수 있음.(추상메소드를 가질 수 없다)
public class Enemy extends JLabel implements Moveable {

    private BubbleFrame mContext;
    private Player player; // 플레이어 추가(충돌 시 사망)
    private Player2 player2;

    //위치상태
    private int x;
    private int y;

    //적군의 방향
    private EnemyWay enemyWay;

    //움직임 상태
    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;

    private int state; //0(살아있는 상태), 1(물방울에 닫힌 상태)


    //적군 속도 상태(고정이므로 상수)
    private final int SPEED = 3;
    private final int JUMPSPEED = 1; //up, down

    private ImageIcon enemyR, enemyL;

    public Enemy(BubbleFrame mContext, EnemyWay enemyWay) {
        this.mContext = mContext;
        this.player = mContext.getPlayer(); //플레이어 충돌 확인
        this.player2 = mContext.getPlayer2();
        initObject();
        initSetting();
        initBackgroundEnemyService();
        //right();
        initEnemyDirection(enemyWay);
    }

    public void initObject() {
//        enemyR = new ImageIcon(Enemy.class.getResource("image/enemyR.png"));
//        enemyL = new ImageIcon(Enemy.class.getResource("image/enemyL.png"));
    	enemyR = new ImageIcon("image/enemyR.png");
        enemyL = new ImageIcon("image/enemyL.png");
    }

    public void initSetting() {
        //천장에서 시작(제일 꼭대기)
        x = 480 ;
        y = 178;

        left = false;
        right = false;
        up = false;
        down = false;

        state = 0;

//        enemyWay = EnemyWay.RIGHT;
//        this.setIcon(enemyR);
        setSize(50,50);
        setLocation(x,y);
    }

    private void initEnemyDirection(EnemyWay enemyWay) {
        if(EnemyWay.RIGHT == enemyWay) {
            enemyWay = EnemyWay.RIGHT;
            setIcon(enemyR);
            right();
        }else {
            enemyWay = EnemyWay.LEFT;
            setIcon(enemyL);
            left();
        }
    }

    private void initBackgroundEnemyService() {
        new Thread(new BackgroundEnemyService(this)).start();
    }

    @Override
    public void left() {
        //System.out.println("left"); //스레드 생성
        enemyWay = EnemyWay.LEFT;
        left = true; //움직이는 중

        new Thread(() -> { //runnable과 동일(람다식)
            while (left) { // 스레드 종료(while없으면 계속 생성종료반복으로 낭비심함)
                setIcon(enemyL);
                x = x - SPEED;
                setLocation(x, y);
                //플레이어와 충돌
                if (Math.abs(x - player.getX()) < 50 && Math.abs(y - player.getY()) < 50) {
                    if (player.getState() == 0 && getState() == 0)
                        player.die();
                }
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
        enemyWay = EnemyWay.RIGHT;
        right = true;

        new Thread(() -> { //runnable과 동일(람다식)
            while (right) {
                setIcon(enemyR);
                x = x + SPEED;
                setLocation(x, y);
                //플레이어와 충돌
                if (Math.abs(x - player.getX()) < 50 && Math.abs(y - player.getY()) < 50) {
                    if (player.getState() == 0 && getState() == 0)
                        player.die();
                }
                try {
                    Thread.sleep(10);//0.01초
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }


    @Override
    public void up() {
        //System.out.println("up");
        up = true;
        new Thread(() -> {
            for(int i=0; i<130/JUMPSPEED; i++) {
                y -= JUMPSPEED;
                setLocation(x,y);
                //플레이어와 충돌
                if (Math.abs(x - player.getX()) < 50 && Math.abs(y - player.getY()) < 50) {
                    if (player.getState() == 0 && getState() == 0)
                        player.die();
                }
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
                //플레이어와 충돌
                if (Math.abs(x - player.getX()) < 50 && Math.abs(y - player.getY()) < 50) {
                    if (player.getState() == 0 && getState() == 0)
                        player.die();
                }
                try {
                    Thread.sleep(3 );
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            down = false;
        }).start();
    }
}
