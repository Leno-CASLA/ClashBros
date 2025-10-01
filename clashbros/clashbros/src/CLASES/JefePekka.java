package CLASES;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class JefePekka {

    private int x, y;
    private int width = 64, height = 96;
    private int velX = 2;
    private int platformStartX, platformWidth;
    private boolean movingRight = true;
    private boolean alive = true;

    private BufferedImage idleRight, idleLeft;
    private BufferedImage walkRight, walkLeft;
    private BufferedImage attackRight, attackLeft;
    private BufferedImage deadRight, deadLeft;
    private BufferedImage deadImage;

    private int stepCounter = 0;
    private int stepDelay = 20;
    private boolean toggleStep = false;

    private final int PISADA_TOLERANCE = 10;
    private final int DETECTION_RANGE = 150;
    private final int ATTACK_RANGE = 50;

    private int pisadasRestantes = 8;
    private boolean attacking = false;

    public JefePekka(int startX, int startY, int platformStartX, int platformWidth) {
        this.x = startX;
        this.y = startY;
        this.platformStartX = platformStartX;
        this.platformWidth = platformWidth;

        try {
            idleRight = ImageIO.read(new File("res/pekka.png"));
            idleLeft = ImageIO.read(new File("res/pekka 2.png"));
            walkRight = ImageIO.read(new File("res/pekka caminando.png"));
            walkLeft = ImageIO.read(new File("res/pekka caminando 2.png"));
            attackRight = ImageIO.read(new File("res/pekka pegando.png"));
            attackLeft = ImageIO.read(new File("res/pekka pegando 2.png"));
            deadRight = ImageIO.read(new File("res/pekka muerto.png"));
            deadLeft = ImageIO.read(new File("res/pekka muerto 2.png"));
        } catch (IOException e) {
            System.err.println("Error al cargar imágenes del jefe");
            e.printStackTrace();
            idleRight = idleLeft = walkRight = walkLeft = attackRight = attackLeft = deadRight = deadLeft =
                    new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
    }

    public void update(ArrayList<BloquePiso> bloquesPiso, Jugador jugador) {
        if (!alive) return;

        Rectangle jefeRect = getBounds();
        Rectangle jugadorRect = jugador.getBounds();

        // Muerte por pisada desde arriba
        if (jugador.getY() + jugador.getHeight() - PISADA_TOLERANCE <= y &&
            jugadorRect.intersects(jefeRect)) {
            pisadasRestantes--;
            jugador.setVelY(-10);
            if (pisadasRestantes <= 0) {
                alive = false;
                deadImage = deadLeft;
                return;
            }
        }

        // Movimiento horizontal
        if (movingRight) {
            x += velX;
            if (x + width >= platformStartX + platformWidth) movingRight = false;
        } else {
            x -= velX;
            if (x <= platformStartX) movingRight = true;
        }

        // Solo detección en la dirección que mira
        int distanciaX = jugador.getX() + jugador.getWidth()/2 - (x + width/2);
        int distanciaY = Math.abs(jugador.getY() + jugador.getHeight()/2 - (y + height/2));

        attacking = false;
        if (Math.abs(distanciaX) < DETECTION_RANGE && distanciaY < 40) {
            if ((movingRight && distanciaX > 0) || (!movingRight && distanciaX < 0)) {
                attacking = true;
                if (Math.abs(distanciaX) < ATTACK_RANGE) {
                    if (jugador.isPoweredUp()) jugador.revertToNormal();
                    else jugador.setY(jugador.getY() + 1000);
                }
            }
        }

        // Animación caminando
        stepCounter++;
        if (stepCounter >= stepDelay) {
            stepCounter = 0;
            toggleStep = !toggleStep;
        }
    }

    public void render(Graphics g, int cameraX) {
        BufferedImage img;
        int renderX = x - cameraX;
        int renderY = y;

        if (!alive) {
            img = deadImage;
            // Agrandar imagen al 1.5x y pegar un poco más abajo de la plataforma
            int deadWidth = (int)(width * 1.5);
            int deadHeight = (int)(height * 1.5);
            int offsetY = 15; // ajuste hacia abajo
            renderY = y + height - deadHeight + offsetY + 20; // base un poco por debajo de la plataforma
            g.drawImage(img, renderX, renderY, deadWidth, deadHeight, null);
            return;
        }


        if (attacking) {
            img = movingRight ? attackRight : attackLeft;
        } else {
            img = toggleStep ? (movingRight ? walkRight : walkLeft) : (movingRight ? idleRight : idleLeft);
        }

        g.drawImage(img, renderX, renderY, width, height, null);
    }
    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }

    // Getters
    public boolean isAlive() { return alive; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getPisadasRestantes() { return pisadasRestantes; }
}