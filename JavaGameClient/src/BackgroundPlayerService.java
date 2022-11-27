import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

//메인스레드 바쁨 - 키보드 이벤트 처리하기 바쁨
//백그라운에서 계속 관찰
public class BackgroundPlayerService implements Runnable{

    private BufferedImage image;
    private Player player;
    private List<Bubble> bubbleList;

    //플레이어, 버블
    public BackgroundPlayerService (Player player) {
        this.player = player;
        this.bubbleList = player.getBubbleList();

        try {
            image = ImageIO.read(new File("image/BackgroundMapService.png"));
        	//image = ImageIO.read(new File(BackgroundPlayerService.class.getResource("image/BackgroundMapService.png")));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        while(player.getState() == 0) {

            // 1. 버블 충돌 체크
            for(int i =0; i<bubbleList.size(); i++) {
                Bubble bubble = bubbleList.get(i);

                if(bubble.getState() == 1) {
                    if ((Math.abs(player.getX() - bubble.getX()) < 10) &&
                            (Math.abs(player.getY() - bubble.getY()) > 0 && Math.abs(player.getY() - bubble.getY()) < 50)) {
                        //System.out.println("적군 사살 완료");
                        bubble.clearBubbled();
                        break;
                    }
                }
            }


            // 2. 벽 충돌 체크
            //플레이어에 위치에 따른 색상확인(캐릭터의 좌상단이 0,0)
            Color leftColor = new Color(image.getRGB(player.getX() - 10, player.getY() + 25));
            Color rightColor = new Color(image.getRGB(player.getX() +50 + 15, player.getY() + 25 ));
            //-2가 나온다는 뜻은 바닥에 색깔이 없이 흰색
            int bottomColor = image.getRGB(player.getX() +10, player.getY() + 50 + 5 ) +
                    image.getRGB(player.getX()+50 - 10, player.getY() + 50 + 5 );

            //바닥 충돌 확인
            if(bottomColor != -2) { //-1은 색상이 없다는 뜻
                //System.out.println("바닥에 충돌함");
                player.setDown(false);
            }
            //낭떠러지
            else { // -2일 때 실행 됨 => 내 바닥 색깔이 하얀색
                if(!player.isUp() && !player.isDown()) {
                    player.down();
                }

            }

            //외벽 충돌 확인
            if(leftColor.getRed()==255 && leftColor.getGreen()==0 && leftColor.getBlue()==0) {
                //System.out.println("왼쪽 벽에 충돌함");
                player.setLeftWallCrash(true);
                player.setLeft(false);
            }
            else if(rightColor.getRed()==255 && rightColor.getGreen()==0 && rightColor.getBlue()==0) {
                //System.out.println("오른쪽 벽에 충돌함");
                player.setRightWallCrash(true);
                player.setRight(false);
            }
            else { //아무것도 충돌하지 않았을 때
                player.setLeftWallCrash(false);
                player.setRightWallCrash(false);
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
