package PRINCIPAL;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import CLASES.*;
import java.util.ArrayList;
import java.util.Objects;

public class ClashBros extends Canvas implements Runnable {

    private JFrame frame;
    private Thread thread;
    private boolean running = false;
    private Jugador jugador;
    private EventoTeclado eventoTeclado;
    private BufferedImage fondo;
    private BufferedImage fondo3;
    private BufferedImage coinImage;
    private int coinCount = 0;
    private int score = 0;

    public static int screenWidth;
    public static int screenHeight;
    private int cameraX = 0;

    private ArrayList<Enemigo> enemigos = new ArrayList<>();
    private ArrayList<BloqueNormal> bloquesNormales = new ArrayList<>();
    private ArrayList<BloqueConHongoOMoneda> bloquesConHongoOMoneda = new ArrayList<>();
    private ArrayList<Moneda> monedas = new ArrayList<>();
    private ArrayList<Hongo> hongos = new ArrayList<>();
    private ArrayList<BloquePiso> bloquesPiso = new ArrayList<>();
    private ArrayList<BanderaMeta> banderas = new ArrayList<>();

    private JefePekka jefe; // Jefe del nivel 3

    private boolean gameOver = false;
    private boolean nivelCompletado = false;
    private int nivelActual = 1; // 1 = nivel1, 3 = nivel3
    private boolean isPaused = false;

    public ClashBros() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        frame = new JFrame("Clash Bros.");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        gd.setFullScreenWindow(frame);

        frame.add(this);
        frame.setVisible(true);
        this.createBufferStrategy(3);

        Graphics gInit = this.getBufferStrategy().getDrawGraphics();
        gInit.setColor(Color.BLACK);
        gInit.fillRect(0, 0, gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight());
        gInit.dispose();
        this.getBufferStrategy().show();

        this.setFocusable(true);
        this.requestFocusInWindow();

        screenWidth = gd.getDisplayMode().getWidth();
        screenHeight = gd.getDisplayMode().getHeight();

        eventoTeclado = new EventoTeclado();
        addKeyListener(eventoTeclado);

        initGame();

        try {
            fondo = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/fondo1.png")));
            fondo3 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/fondolevel3.png")));
            coinImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/Moneda.jpeg")));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("No se pudo cargar alguna imagen.");
        }
    }

    private void initGame() {
        jugador = new Jugador(100, screenHeight - 64 * 2, eventoTeclado, new Jugador.GameCallbacks() {
            @Override public void addScore(int points) { score += points; }
            @Override public void addMoneda(Moneda moneda) { monedas.add(moneda); coinCount++; }
            @Override public void addHongo(Hongo hongo) { hongos.add(hongo); }
            @Override public void setGameOver() { gameOver = true; }
        }, screenHeight);

        enemigos.clear();
        bloquesNormales.clear();
        bloquesConHongoOMoneda.clear();
        monedas.clear();
        hongos.clear();
        bloquesPiso.clear();
        banderas.clear();

        nivelActual = 1;
        nivelCompletado = false;
        gameOver = false;
        coinCount = 0;
        score = 0;

        int posX = 0;
        while (posX < 12000) {
            if ((posX >= 5000 && posX < 6000) || (posX >= 9000 && posX < 9200)) {
                posX += 64;
            } else {
                bloquesPiso.add(new BloquePiso(posX, screenHeight - 64));
                posX += 64;
            }
        }

        enemigos.add(new Enemigo(800, 500));
        bloquesNormales.add(new BloqueNormal(1000, 490));
        bloquesNormales.add(new BloqueNormal(1064, 490));
        bloquesNormales.add(new BloqueNormal(1128, 490));
        bloquesNormales.add(new BloqueNormal(5150, 590));
        bloquesNormales.add(new BloqueNormal(5350, 490));
        bloquesNormales.add(new BloqueNormal(5550, 390));
        bloquesNormales.add(new BloqueNormal(5650, 290));

        bloquesConHongoOMoneda.add(new BloqueConHongoOMoneda(1200, 490, BloqueConHongoOMoneda.Contenido.MONEDA));
        bloquesConHongoOMoneda.add(new BloqueConHongoOMoneda(1300, 490, BloqueConHongoOMoneda.Contenido.HONGO));

        banderas.add(new BanderaMeta(10000, screenHeight - 64));
    }

    private void cargarNivel3() {
        enemigos.clear();
        bloquesNormales.clear();
        bloquesConHongoOMoneda.clear();
        monedas.clear();
        hongos.clear();
        bloquesPiso.clear();
        banderas.clear();

        Nivel3 nivel3 = new Nivel3(screenHeight);

        enemigos.addAll(nivel3.getEnemigos());
        bloquesNormales.addAll(nivel3.getBloquesNormales());
        bloquesConHongoOMoneda.addAll(nivel3.getBloquesConHongoOMoneda());
        hongos.addAll(nivel3.getHongos());
        monedas.addAll(nivel3.getMonedas());
        bloquesPiso.addAll(nivel3.getBloquesPiso());
        banderas.addAll(nivel3.getBanderas());

        jefe = nivel3.getJefe(); // Añadir jefe

        jugador.setX(100);
        jugador.setY(screenHeight - 64 * 2);
        jugador.setVelX(0);
        jugador.setVelY(0);
        jugador.setSliding(false);

        cameraX = 0;
        nivelActual = 3;

        gameOver = false;
        nivelCompletado = false;
        coinCount = 0;
        score = 0;
    }

    public synchronized void start() { if (running) return; running = true; thread = new Thread(this); thread.start(); }
    public synchronized void stop() { if (!running) return; running = false; try { thread.join(); } catch (InterruptedException e) { e.printStackTrace(); } }

    @Override
    public void run() {
        final double TARGET_FPS = 60.0;
        final double NS_PER_UPDATE = 1_000_000_000.0 / TARGET_FPS;
        long lastTime = System.nanoTime();
        double delta = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / NS_PER_UPDATE;
            lastTime = now;
            while (delta >= 1) { update(); delta--; }
            render();
            try { Thread.sleep(2); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        stop();
    }

    private void update() {
        if (!gameOver && !nivelCompletado) {
            jugador.update(enemigos, bloquesNormales, bloquesConHongoOMoneda, monedas, hongos, bloquesPiso);

            for (Enemigo e : enemigos) e.update();
            for (Hongo h : hongos) {
                h.update(bloquesNormales, bloquesConHongoOMoneda, bloquesPiso);
                for (BloqueNormal b : bloquesNormales) if (h.getBounds().intersects(b.getBounds())) h.changeDirection();
                for (BloqueConHongoOMoneda b : bloquesConHongoOMoneda) if (h.getBounds().intersects(b.getBounds())) h.changeDirection();
            }
            for (Moneda m : monedas) m.update();

            if (jefe != null)
                jefe.update(bloquesPiso, jugador);

            for (BanderaMeta b : banderas) {
                if (jugador.getBounds().intersects(b.getBounds())) {
                    jugador.setSliding(true);
                    if (nivelActual == 1) {
                        cargarNivel3();
                    } else {
                        nivelCompletado = true;
                    }
                }
            }
        }

        if (eventoTeclado.isKeyDown(java.awt.event.KeyEvent.VK_R)) {
            if (nivelActual == 1) restartGame();
            else if (nivelActual == 3) cargarNivel3();
        }
    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        Graphics g = bs.getDrawGraphics();

        cameraX = jugador.getX() - (screenWidth / 2);
        if (cameraX < 0) cameraX = 0;

        BufferedImage fondoActual = (nivelActual == 3) ? fondo3 : fondo;

        if (fondoActual != null) {
            int fondoWidth = fondoActual.getWidth();
            int startX = -(cameraX % fondoWidth);
            int currentWidth = startX + screenWidth + fondoWidth;
            for (int x = startX; x < currentWidth; x += fondoWidth) {
                g.drawImage(fondoActual, x, 0, null);
            }
        } else {
            g.setColor(new Color(135, 206, 235));
            g.fillRect(0, 0, screenWidth, screenHeight);
        }

        for (BloquePiso bp : bloquesPiso) bp.render(g, cameraX);
        for (BloqueNormal b : bloquesNormales) b.render(g, cameraX);
        for (BloqueConHongoOMoneda b : bloquesConHongoOMoneda) b.render(g, cameraX);
        for (Moneda m : monedas) m.render(g, cameraX);
        for (Hongo h : hongos) h.render(g, cameraX);
        for (Enemigo e : enemigos) e.render(g, cameraX);
        if (jefe != null) jefe.render(g, cameraX); // renderizar jefe
        for (BanderaMeta b : banderas) b.render(g, cameraX);

        jugador.render(g, cameraX);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 20, 45);

        if (coinImage != null) g.drawImage(coinImage, screenWidth - 100, 20, 32, 32, null);
        g.drawString("x " + coinCount, screenWidth - 60, 45);

        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, screenWidth, screenHeight);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("GAME OVER", screenWidth / 2 - 150, screenHeight / 2);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Presiona 'R' para reiniciar", screenWidth / 2 - 130, screenHeight / 2 + 40);
        }

        if (nivelCompletado) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, screenWidth, screenHeight);
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("¡NIVEL COMPLETADO!", screenWidth / 2 - 220, screenHeight / 2);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Presiona 'R' para reiniciar", screenWidth / 2 - 130, screenHeight / 2 + 40);
        }

        g.dispose();
        bs.show();
    }

    public void restartGame() {
        if (nivelActual == 1) {
            initGame();
        } else if (nivelActual == 3) {
            cargarNivel3();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClashBros().start());
    }
}
