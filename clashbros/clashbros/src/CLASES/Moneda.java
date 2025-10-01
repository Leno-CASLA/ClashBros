package CLASES;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;
import java.util.ArrayList; // Importa ArrayList

public class Moneda {
    private int x, y;
    private int width, height;
    private BufferedImage imagenMoneda;
    private int vidaUtil = 40; // Durará 40 cuadros
    private boolean isVisible = true;
    private int velY = -5; // Velocidad de subida

    public Moneda(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 32;
        this.height = 32;
        try {
            imagenMoneda = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/Moneda.jpeg")));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("No se pudo cargar la imagen de la moneda.");
        }
    }

    public void update() {
        if (!isVisible) return; // Si no es visible, no se actualiza

        y += velY;
        velY += 1; // La gravedad la hace caer después de subir
        vidaUtil--; // Reduce la vida útil
        
        if (vidaUtil <= 0) {
            isVisible = false; // La moneda ya no es visible
        }
    }
    
    // Este método es crucial para que se borre del juego
    public boolean isVisible() {
        return isVisible;
    }

    public void render(Graphics g, int cameraX) {
        if (isVisible && imagenMoneda != null) {
            g.drawImage(imagenMoneda, x - cameraX, y, width, height, null);
        }
    }

    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }
    public int getX() { return x; }
    public int getY() { return y; }
}