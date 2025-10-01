package CLASES;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class BloquePiso {
    private int x, y;
    private BufferedImage imagen;
    private final int width = 64;
    private final int height = 64;

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

    public void handleCollision(Jugador jugador) {
        Rectangle bloqueRect = getBounds();
        Rectangle jugadorRect = jugador.getBounds();

        if (jugadorRect.intersects(bloqueRect)) {
            // Colisión desde arriba (aterrizaje)
            if (jugador.getVelY() > 0 && 
                jugadorRect.y + jugadorRect.height - jugador.getVelY() <= bloqueRect.y) {
                
                jugador.setY(bloqueRect.y - jugador.getHeight());
                jugador.setVelY(0);
                jugador.setOnGround(true); // Permite un nuevo salto
            }
            // Colisión desde abajo
            else if (jugador.getVelY() < 0 && 
                     jugadorRect.y >= bloqueRect.y + bloqueRect.height) {
                jugador.setY(bloqueRect.y + bloqueRect.height);
                jugador.setVelY(0);
            }
            // Colisión lateral
            else {
                if (jugadorRect.x < bloqueRect.x) {
                    jugador.setX(bloqueRect.x - jugador.getWidth());
                } else {
                    jugador.setX(bloqueRect.x + bloqueRect.width);
                }
                jugador.setVelX(0); // Frena el movimiento lateral
            }
        }
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
