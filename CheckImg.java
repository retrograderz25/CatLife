import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class CheckImg {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("assets/images/HUD/Cat/orange/WALK.png"));
        System.out.println("WALK: " + img.getWidth() + "x" + img.getHeight());
        BufferedImage img2 = ImageIO.read(new File("assets/images/HUD/Cat/orange/ATTACK.png"));
        System.out.println("ATTACK: " + img2.getWidth() + "x" + img2.getHeight());
    }
}