package CLASES;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Moneda {
    private int x, y;
    private BufferedImage imagen;
    private int width = 32, height = 32;
    private boolean isVisible = true;
    private long creationTime; // Nuevo: guarda el momento de la creación

    public Moneda(int x, int y) {
        this.x = x;
        this.y = y;
        this.creationTime = System.currentTimeMillis(); // Nuevo: establece el tiempo de creación
        try {
            imagen = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/Moneda.jpeg")));
        } catch (IOException e) {
            System.err.println("No se pudo cargar la imagen 'Moneda.jpeg'");
            e.printStackTrace();
        }
    }

    public void render(Graphics g, int cameraX) {
        if (isVisible) {
            if (imagen != null) {
                g.drawImage(imagen, x - cameraX, y, width, height, null);
            } else {
                g.setColor(Color.YELLOW);
                g.fillOval(x - cameraX, y, width, height);
            }
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    // Nuevo: Getter para el tiempo de creación
    public long getCreationTime() {
        return creationTime;
    }

    // Métodos para controlar la visibilidad de la moneda
    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}