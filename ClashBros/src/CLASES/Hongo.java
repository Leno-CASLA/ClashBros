package CLASES;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Hongo {
    private int x, y;
    private int velX = 2;
    private final int GRAVITY = 1;
    private int velY = 0;
    private BufferedImage imagen;
    private int width = 32, height = 32;
    private boolean isVisible = true;

    public Hongo(int x, int y) {
        this.x = x;
        this.y = y;
        try {
            imagen = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/Hongo.jpeg")));
        } catch (IOException e) {
            System.err.println("No se pudo cargar la imagen 'Hongo.jpeg'");
            e.printStackTrace();
        }
    }

    public void update(ArrayList<BloqueNormal> bloquesNormales,
                       ArrayList<BloqueConHongoOMoneda> bloquesConHongoOMoneda,
                       ArrayList<BloquePiso> bloquesPiso) {
        x += velX;
        y += velY;
        velY += GRAVITY;

        // Colisión con bloques normales
        for (BloqueNormal b : bloquesNormales) {
            if (getBounds().intersects(b.getBounds())) {
                if (y + height > b.getY() && velY > 0) {
                    y = b.getY() - height;
                    velY = 0;
                }
            }
        }

        // Colisión con bloques con ítem
        for (BloqueConHongoOMoneda b : bloquesConHongoOMoneda) {
            if (getBounds().intersects(b.getBounds())) {
                if (y + height > b.getY() && velY > 0) {
                    y = b.getY() - height;
                    velY = 0;
                }
            }
        }

        // 🔹 Colisión con bloques de piso
        for (BloquePiso bp : bloquesPiso) {
            if (getBounds().intersects(bp.getBounds())) {
                if (y + height > bp.getY() && velY > 0) {
                    y = bp.getY() - height;
                    velY = 0;
                }
            }
        }
    }

    public void changeDirection() {
        velX *= -1;
    }

    public void render(Graphics g, int cameraX) {
        if (isVisible) {
            if (imagen != null) {
                g.drawImage(imagen, x - cameraX, y, width, height, null);
            } else {
                g.setColor(Color.MAGENTA);
                g.fillRect(x - cameraX, y, width, height);
            }
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean visible) { this.isVisible = visible; }
}
