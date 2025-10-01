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
            // Colisión desde arriba (aterrizaje)
            // Se verifica que el jugador esté cayendo (getVelY > 0)
            // y que su posición anterior estaba por encima del bloque
            if (jugador.getVelY() > 0 && 
                jugadorRect.y + jugadorRect.height - jugador.getVelY() <= bloqueRect.y) {
                
                jugador.setY(bloqueRect.y - jugador.getHeight());
                jugador.setVelY(0);
                jugador.setOnGround(true); // Permite un nuevo salto
            }
            // Colisión desde abajo (romper)
            else if (jugador.getVelY() < 0 && 
                     jugadorRect.y >= bloqueRect.y + bloqueRect.height) {
                jugador.setY(bloqueRect.y + bloqueRect.height);
                jugador.setVelY(0);
                if (jugador.isPoweredUp()) isBroken = true;
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

    // Getters y setters
    public boolean isBroken() { return isBroken; }
    public void setBroken(boolean broken) { this.isBroken = broken; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
