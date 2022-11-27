
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.util.List;

@Getter
@Setter

public class Bubble extends JLabel implements Moveable {

    //의존성 콤포지션
    private Player player;
    private Player2 player2;
    //private Enemy enemy;
    private BackgroundBubbleService backgroundBubbleService;
    //private BackgroundPlayerService2 backgroundPlayerService2;
    private BubbleFrame mContext;
    private List<Enemy> enemyList; // 적들
    private Enemy removeEnemy = null; // 적 제거 변수

    // player1 위치상태
    private int x;
    private int y;

    //player2 위치상태
    private int x2;
    private int y2;

    //움직임 상태
    private boolean left;
    private boolean right;
    private boolean up;

    //적군을 맞춘 상태
    private int state; //0(물방울), 1(적을 가둔 물방울)

    private ImageIcon bubble; //물방울
    private ImageIcon bubbled; //적을 가둔 물방울
    private ImageIcon bomb; //물방울이 터진 상태

    public Bubble(BubbleFrame mContext) { //mContext에 정보 다 들어있으므로 필요 X, Player player생성 필요 X
        this.mContext = mContext;
        this.player = mContext.getPlayer(); //플레이어의 x,y좌표를 알고 버블 좌표를 만들 수 있음
        this.enemyList = mContext.getEnemyList();
        initObject();
        initSetting();
        //initThread();
    }

    private void initObject() {
        bubble = new ImageIcon("image/bubble.png");
        bubbled = new ImageIcon("image/bubbled.png");
        bomb = new ImageIcon("image/bomb.png");

        backgroundBubbleService = new BackgroundBubbleService(this);
    }

    private void initSetting() {
        left = false;
        right = false;
        up = false;

        x = player.getX();
        y = player.getY();

//        x2 = player2.getX();
//        y2 = player2.getY();

        setIcon(bubble);
        setSize(50,50);
        state = 0;
    }

//    private void initThread() {
//        //버블은 동시이동 없으므로 스레드 하나만 필요
//        new Thread(()->{
//            if(player.getPlayerWay()== PlayerWay.LEFT) {
//                left();
//            }
//            else {
//                right();
//            }
//        }).start();
//    }

    @Override
    public void left() {
        left = true;

        //범위를 줘야 하므로 while문 대신 for문 사용
        Stop: for (int i=0; i<400; i++) {
            x--;
            //x2--;
            setLocation(x,y); //이동
            //setLocation(x2,y2); //이동

            if(backgroundBubbleService.leftWall()) {//true면 벽에 부딪힌 것
                left = false;
                break;
            }

            //절댓값으로 x: 10, y: 0~50
            for (Enemy enemy : enemyList) {
                if ((Math.abs(x - enemy.getX()) < 10) &&
                        (Math.abs(y - enemy.getY()) > 0 && Math.abs(y - enemy.getY()) < 50)) {
                    //System.out.println("물방울이 적군과 충돌함");
                    if (enemy.getState() == 0) {
                        attack(enemy);
                        break Stop;
                    }
                }
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        up();

    }

    @Override
    public void right() {
        right = true;

        Stop: for (int i=0; i<400; i++) {
            x++;
            setLocation(x,y); //이동

            if(backgroundBubbleService.rightWall()) {//true면 벽에 부딪힌 것
                right = false;
                break;
            }

            //절댓값으로 x: 10, y: 0~50
            for (Enemy enemy : enemyList) {
                if ((Math.abs(x - enemy.getX()) < 10) &&
                        (Math.abs(y - enemy.getY()) > 0 && Math.abs(y - enemy.getY()) < 50)) {
                    //System.out.println("물방울이 적군과 충돌함");
                    if (enemy.getState() == 0) {
                        attack(enemy);
                        break Stop;
                    }
                }
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        up();
    }

    @Override
    public void up() {
        up = true;
        //while문 사용 시 지구 끝까지 올라감
        while(up) {
            y--;
            setLocation(x,y); //이동

            if(backgroundBubbleService.topWall()) {//true면 벽에 부딪힌 것
                up = false;
                break;
            }

            try {
                if(state == 0){ //기본 물방울
                    Thread.sleep(1);
                } else { //적을 가둔 물방울
                    Thread.sleep(10);
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //up이 끝나면 버블이 소멸되야 함
        if(state == 0) { //버블 상태가 0일때(안 맞췄을때)
            clearBubble(); //천장에 버블이 도착하고 나서 3초 후에 메모리에서 소멸
        }
    }

    @Override
    public void attack(Enemy enemy) { //적군 가두기
        state = 1; //물방울 상태
        enemy.setState(1); //적군 물방울 상태
        setIcon(bubbled);
        removeEnemy = enemy;
        mContext.remove(enemy); //메모리에서 사라지게 하기(가비지 컬렉션 -> 즉시 발동하지 않음)
        mContext.repaint(); //깔끔하게 지워지기 위해 다시 그리기
    }

    //행위 -> clear(동사) -> bubble(목적어)
    private void clearBubble() { //다른 곳에서 호출하지 않고 up이 끝나면 호출하기 때문에 private사용해도 됨
        try {
            Thread.sleep(3000); //3초 잠
            setIcon(bomb);
            Thread.sleep(500);
            //버블 프레임 전체 다시 그리기(버블을 없애야 하므로), 버블프레임의 정보가 필요함(이때 main클래스를 호출)
            mContext.getPlayer().getBubbleList().remove(this);
            mContext.remove(this); //BubbleFrame의 bubble이 메모리에서 소멸된다.
            mContext.repaint(); //BubbleFrame의 전체를 다시 그린다.(메모리에서 없는 건 그리지 않음)

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearBubbled() { //적군이 든 버블 없애기
        new Thread(()->{
            //System.out.println("clearBubbled");
            try {
                up = false;
                setIcon(bomb);
                Thread.sleep(1000);
                mContext.getPlayer().getBubbleList().remove(this);
                mContext.getEnemyList().remove(removeEnemy); // 컨텍스트에 enemy 삭제
                mContext.remove(this);
                mContext.repaint();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }
}
