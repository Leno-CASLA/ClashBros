package CLASES;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class BanderaMeta {
    private int x, y;
    private BufferedImage imagen;
    private final int width = 256;   // ancho de la bandera
    private final int height = 512;  // alto de la bandera

    public BanderaMeta(int x, int y) {
        this.x = x;
        this.y = y;
        try {
            imagen = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/BanderaMeta.png")));
        } catch (IOException e) {
            System.err.println("No se pudo cargar 'BanderaMeta.png'");
            e.printStackTrace();
        }
    }

    public void render(Graphics g, int cameraX) {
        if (imagen != null) {
            g.drawImage(imagen, x - cameraX, y - height, width, height, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x - cameraX, y - height, width, height);
        }
    }

    public Rectangle getBounds() {
        // Solo el mástil como hitbox
        int poleWidth = 32; // ancho reducido (mástil)
        return new Rectangle(x + width / 2 - poleWidth / 2, y - height, poleWidth, height);
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
