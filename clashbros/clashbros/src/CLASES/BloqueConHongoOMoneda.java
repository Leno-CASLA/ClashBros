package CLASES;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class BloqueConHongoOMoneda {
    public enum Contenido { MONEDA, HONGO }

    private int x, y;
    private BufferedImage imagenNormal, imagenVacio;
    private int width = 64, height = 64;
    private boolean isHit = false;
    private Contenido contenido;

    public BloqueConHongoOMoneda(int x, int y, Contenido contenido) {
        this.x = x;
        this.y = y;
        this.contenido = contenido;
        try {
            // Se corrigieron las rutas de las imágenes
            imagenNormal = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/BloqueConHongoOMoneda.jpeg")));
            imagenVacio = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/BloqueVacio.png"))); // Necesitas esta imagen
        } catch (IOException e) {
            System.err.println("No se pudo cargar las imágenes para BloqueConHongoOMoneda");
            e.printStackTrace();
        }
    }

    public void render(Graphics g, int cameraX) {
        if (imagenNormal != null && !isHit) {
            g.drawImage(imagenNormal, x - cameraX, y, width, height, null);
        } else if (imagenVacio != null && isHit) {
            g.drawImage(imagenVacio, x - cameraX, y, width, height, null);
        } else {
            g.setColor(Color.ORANGE);
            g.fillRect(x - cameraX, y, width, height);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void setHit(boolean hit) {
        this.isHit = hit;
    }

    public boolean isHit() {
        return isHit;
    }

    public Contenido getContenido() {
        return contenido;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}