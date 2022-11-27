

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/** 스레드 사용하지 않으니까 runnable할 필요가 없다.
 * 플레이어가 움직이는 방향이 어디인지 버블이 벽에 충돌했을 때 올려줌
 */

public class BackgroundBubbleService{

    private BufferedImage image;
    private Bubble bubble;

    public BackgroundBubbleService(Bubble bubble) {
        this.bubble = bubble;

        try {
            image = ImageIO.read(new File("image/BackgroundMapService.png"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean leftWall(){
        Color leftColor = new Color(image.getRGB(bubble.getX() - 10, bubble.getY() + 25));
        if(leftColor.getRed()==255 && leftColor.getGreen()==0 && leftColor.getBlue()==0) {
            return true; // bubble에서 while문이 아니라 for문이므로 그냥 true만 리턴해주면 break
        }

        return false;
    }

    public boolean rightWall(){
        Color rightColor = new Color(image.getRGB(bubble.getX() +50 + 15, bubble .getY() + 25 ));
        if(rightColor.getRed()==255 && rightColor.getGreen()==0 && rightColor.getBlue()==0) {
            return true;
        }
        return false;
    }

    public boolean topWall(){
        Color topColor = new Color(image.getRGB(bubble.getX()+25, bubble.getY() -10));
        if(topColor.getRed()==255 && topColor.getGreen()==0 && topColor.getBlue()==0) {
            return true;
        }
        return false;
    }
}
