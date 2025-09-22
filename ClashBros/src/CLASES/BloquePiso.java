package CLASES;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class BloquePiso {
    private int x, y;
    private BufferedImage imagen;
    private final int width = 64;   // ancho fijo del bloque
    private final int height = 64;  // alto fijo del bloque

    public BloquePiso(int x, int y) {
        this.x = x;
        this.y = y;
        try {
            imagen = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/BloquePiso.png")));
        } catch (IOException e) {
            System.err.println("No se pudo cargar la imagen 'BloquePiso.png'");
            e.printStackTrace();
        }
    }

    public void render(Graphics g, int cameraX) {
        if (imagen != null) {
            g.drawImage(imagen, x - cameraX, y, width, height, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(x - cameraX, y, width, height);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
