package CLASES;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class BloqueNormal {
    private int x, y;
    private BufferedImage imagen;
    private int width = 64, height = 64;
    private boolean isBroken = false;

    public BloqueNormal(int x, int y) {
        this.x = x;
        this.y = y;
        try {
            imagen = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream("/BloqueNormal.png")
            ));
        } catch (IOException e) {
            System.err.println("No se pudo cargar la imagen 'BloqueNormal.png'");
            e.printStackTrace();
        }
    }

    public void render(Graphics g, int cameraX) {
        if (!isBroken) {
            if (imagen != null) {
                g.drawImage(imagen, x - cameraX, y, width, height, null);
            } else {
                g.setColor(Color.GRAY);
                g.fillRect(x - cameraX, y, width, height);
            }
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void handleCollision(Jugador jugador) {
        if (isBroken) return;

        Rectangle bloqueRect = getBounds();
        Rectangle jugadorRect = jugador.getBounds();

        if (jugadorRect.intersects(bloqueRect)) {
            // Colisión desde arriba → apoyarse
            if (jugador.getVelY() > 0 &&
                jugador.getY() + jugador.getHeight() <= y + 10) {
                jugador.setY(y - jugador.getHeight());
                jugador.setVelY(0);
                jugador.setOnGround(true); // Habilita salto de nuevo
            }
            // Colisión desde abajo → romper si tiene power-up
            else if (jugador.getVelY() < 0 &&
                     jugador.getY() >= y + height - 10) {
                jugador.setY(y + height);
                jugador.setVelY(0);
                if (jugador.isPoweredUp()) isBroken = true;
            }
            // Colisión lateral → frena movimiento
            else {
                if (jugador.getX() + jugador.getWidth() / 2 < x) {
                    jugador.setX(x - jugador.getWidth());
                } else {
                    jugador.setX(x + width);
                }
            }
        }
    }

    // Getters y setters
    public boolean isBroken() { return isBroken; }
    public void setBroken(boolean broken) { this.isBroken = broken; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
