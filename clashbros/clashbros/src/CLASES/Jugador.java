package CLASES;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Jugador {

    private int x, y;
    private int velX, velY;
    private int width, height;
    private boolean isJumping = false;
    private final int GRAVITY = 1;

    private EventoTeclado eventoTeclado;
    private boolean poweredUp = false;
    private int normalWidth, normalHeight;
    private int poweredUpWidth, poweredUpHeight;

    private boolean sliding = false; // deslizamiento sobre bandera

    private BufferedImage[] pasosDerecha;
    private BufferedImage[] pasosIzquierda;
    private int stepIndex = 0;
    private int stepCounter = 0;
    private int stepDelay = 5; // frames por paso

    private BufferedImage currentImage;

    public interface GameCallbacks {
        void addScore(int points);
        void addMoneda(Moneda moneda);
        void addHongo(Hongo hongo);
        void setGameOver();
    }

    private GameCallbacks callbacks;
    private int screenHeight;

    public Jugador(int x, int y, EventoTeclado eventoTeclado, GameCallbacks callbacks, int screenHeight) {
        this.x = x;
        this.y = y;
        this.velX = 0;
        this.velY = 0;
        this.eventoTeclado = eventoTeclado;
        this.callbacks = callbacks;
        this.screenHeight = screenHeight;

        normalWidth = 50;
        normalHeight = 64;
        poweredUpWidth = 70;
        poweredUpHeight = 96;
        width = normalWidth;
        height = normalHeight;

        pasosDerecha = new BufferedImage[5];
        pasosIzquierda = new BufferedImage[5];

        String[] archivosDerecha = {
            "/PrimerPasoReyAzulDerecha.png",
            "/SegundoPasoReyAzulDerecha.png",
            "/ReyAzulMedioDerecha.png",
            "/CuartoPasoReyAzulDerecha.png",
            "/QuintoPasoReyAzulDerecha.png"
        };

        String[] archivosIzquierda = {
            "/PrimerPasoReyAzulIzquierda.png",
            "/SegundoPasoReyAzulIzquierda.png",
            "/ReyAzulMedioIzquierda.png",
            "/CuartoPasoReyAzulIzquierda.png",
            "/QuintoPasoReyAzulIzquierda.png"
        };

        try {
            for (int i = 0; i < 5; i++) {
                BufferedImage imgD = ImageIO.read(Objects.requireNonNull(getClass().getResource(archivosDerecha[i])));
                pasosDerecha[i] = scaleImage(imgD, normalWidth, normalHeight);

                BufferedImage imgI = ImageIO.read(Objects.requireNonNull(getClass().getResource(archivosIzquierda[i])));
                pasosIzquierda[i] = scaleImage(imgI, normalWidth, normalHeight);
            }
            currentImage = pasosDerecha[2];
        } catch (IOException | NullPointerException e) {
            System.err.println("Error al cargar imágenes de pasos");
            e.printStackTrace();
            currentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
    }

    private BufferedImage scaleImage(BufferedImage original, int w, int h) {
        BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.drawImage(original, 0, 0, w, h, null);
        g2.dispose();
        return scaled;
    }

    public void update(ArrayList<Enemigo> enemigos,
            ArrayList<BloqueNormal> bloquesNormales,
            ArrayList<BloqueConHongoOMoneda> bloquesConHongoOMoneda,
            ArrayList<Moneda> monedas, // Ahora Moneda tiene un update propio
            ArrayList<Hongo> hongos,
            ArrayList<BloquePiso> bloquesPiso) {

        if (sliding) {
            slideToFlag();
            return;
        }

        int moveSpeed = eventoTeclado.isKeyDown(KeyEvent.VK_SHIFT) ? 8 : 5;
        boolean moving = false;

        velX = 0;
        if (eventoTeclado.isKeyDown(KeyEvent.VK_A)) { velX = -moveSpeed; moving = true; }
        if (eventoTeclado.isKeyDown(KeyEvent.VK_D)) { velX = moveSpeed; moving = true; }
        if (eventoTeclado.isKeyDown(KeyEvent.VK_SPACE) && !isJumping) jump();

        x += velX;
        y += velY;
        velY += GRAVITY;

        updateAnimation(moving); // ¡El método faltante, añadido aquí!

        Rectangle playerBounds = getBounds();

        // Colisión con piso
        for (BloquePiso bp : bloquesPiso) {
            Rectangle pisoBounds = bp.getBounds();
            if (playerBounds.intersects(pisoBounds) && velY >= 0) {
                y = bp.getY() - height;
                velY = 0;
                setOnGround(true);
                playerBounds = getBounds();
            }
        }

        // Colisión con bloques normales
        for (BloqueNormal b : bloquesNormales) {
            handleBlockCollision(b.getBounds());
        }

        // Colisión con bloques con contenido
        for (BloqueConHongoOMoneda b : bloquesConHongoOMoneda) {
            Rectangle bloqueBounds = b.getBounds();
            if (getBounds().intersects(bloqueBounds)) {
                // Si la colisión es desde arriba (aterrizando sobre el bloque)
                if (playerBounds.y + playerBounds.height <= bloqueBounds.y + velY) {
                    y = bloqueBounds.y - height;
                    velY = 0;
                    setOnGround(true);
                }
                // Si la colisión es desde abajo (golpeando la cabeza del bloque)
                else if (getBounds().y >= bloqueBounds.y + bloqueBounds.height + velY) {
                    y = bloqueBounds.y + bloqueBounds.height;
                    velY = 0;
                    if (!b.isHit()) {
                        b.setHit(true);
                        switch (b.getContenido()) {
                            case MONEDA: callbacks.addMoneda(new Moneda(b.getX() + b.getWidth()/2 - 16, b.getY() - b.getHeight())); break;
                            case HONGO: callbacks.addHongo(new Hongo(b.getX(), b.getY() - b.getHeight())); break;
                        }
                    }
                }
                // Si la colisión es desde los lados
                else {
                    if (playerBounds.x < bloqueBounds.x) {
                        x = bloqueBounds.x - width;
                    } else {
                        x = bloqueBounds.x + bloqueBounds.width;
                    }
                    velX = 0;
                }
                playerBounds = getBounds();
            }
        }

        // Colisión con hongos
        for (int i = 0; i < hongos.size(); i++) {
            Hongo h = hongos.get(i);
            if (h.isVisible() && playerBounds.intersects(h.getBounds())) {
                hongos.remove(i);
                i--;
                powerUp();
                callbacks.addScore(500);
            }
        }

        // Colisión con enemigos
        for (int i = 0; i < enemigos.size(); i++) {
            Enemigo e = enemigos.get(i);
            if (playerBounds.intersects(e.getBounds())) {
                if (velY > 0 && (y + height) <= (e.getY() + 10)) {
                    enemigos.remove(i);
                    i--;
                    velY = -10;
                    callbacks.addScore(100);
                } else {
                    if (poweredUp) revertToNormal();
                    else callbacks.setGameOver();
                    break;
                }
            }
        }
        for (int i = 0; i < monedas.size(); i++) {
            if (!monedas.get(i).isVisible()) {
                monedas.remove(i);
                i--;
            }
        }
        if (y > screenHeight) callbacks.setGameOver();
    }

    private void updateAnimation(boolean moving) {
        if (!moving) { currentImage = pasosDerecha[2]; return; }
        stepCounter++;
        if (stepCounter >= stepDelay) { stepCounter = 0; stepIndex = (stepIndex + 1) % 5; }
        currentImage = velX > 0 ? pasosDerecha[stepIndex] : pasosIzquierda[stepIndex];
    }
    
    private void handleBlockCollision(Rectangle bloqueBounds) {
        Rectangle playerBounds = getBounds();
        if (playerBounds.intersects(bloqueBounds)) {
            // Colisión desde arriba (aterrizando sobre el bloque)
            if (playerBounds.y + playerBounds.height <= bloqueBounds.y + velY) {
                y = bloqueBounds.y - height;
                velY = 0;
                setOnGround(true);
            }
            // Colisión desde abajo (golpeando la cabeza)
            else if (playerBounds.y >= bloqueBounds.y + bloqueBounds.height + velY) {
                y = bloqueBounds.y + bloqueBounds.height;
                velY = 0;
            }
            // Colisión desde los lados
            else {
                if (playerBounds.x < bloqueBounds.x) {
                    x = bloqueBounds.x - width;
                } else {
                    x = bloqueBounds.x + bloqueBounds.width;
                }
                velX = 0;
            }
        }
    }

    private void slideToFlag() { x += 4; }
    public void setSliding(boolean sliding) { this.sliding = sliding; }

    public void render(Graphics g, int cameraX) { g.drawImage(currentImage, x - cameraX, y, width, height, null); }

    public void jump() { if (!isJumping) { velY = -15; isJumping = true; } }
    public void powerUp() { if (!poweredUp) { poweredUp = true; width = poweredUpWidth; height = poweredUpHeight; } }
    public void revertToNormal() {
        if (poweredUp) {
            poweredUp = false;
            width = normalWidth;
            height = normalHeight;
        }
    }

    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }
    public void setOnGround(boolean onGround) { if (onGround) isJumping = false; }

    // Getters y setters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getVelX() { return velX; }
    public int getVelY() { return velY; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setVelX(int velX) { this.velX = velX; }
    public void setVelY(int velY) { this.velY = velY; }
    public boolean isPoweredUp() { return poweredUp; }
}