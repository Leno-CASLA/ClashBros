package CLASES;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;
import PRINCIPAL.ClashBros;

public class Enemigo {
    private int x, y;
    private int velX = -1;
    private final int GRAVITY = 1;
    private int velY = 0;
    private int width = 48, height = 48;
    private int groundY = ClashBros.screenHeight - 68;
    private BufferedImage imagen;

    public Enemigo(int x, int y) {
        this.x = x; 
        this.y = y;
        try {
            imagen = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/Duende.png")));
        } catch (IOException e) {
            System.err.println("No se pudo cargar la imagen 'Duende.png'");
            e.printStackTrace();
        }
    }

    public void update() {
        x += velX;
        y += velY;
        velY += GRAVITY;

        // Mantenerlo en el suelo
        if (y + height >= groundY && velY > 0) {
            y = groundY - height;
            velY = 0;
        }
    }

    public void render(Graphics g, int cameraX) {
        if (imagen != null) {
            g.drawImage(imagen, x - cameraX, y, width, height, null);
        } else {
            g.setColor(Color.GREEN);
            g.fillRect(x - cameraX, y, width, height);
        }
    }

    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
