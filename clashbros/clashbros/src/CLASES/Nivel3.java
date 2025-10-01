package CLASES;

import java.util.ArrayList;

public class Nivel3 {

    private ArrayList<BloquePiso> bloquesPiso = new ArrayList<>();
    private ArrayList<BloqueNormal> bloquesNormales = new ArrayList<>();
    private ArrayList<BloqueConHongoOMoneda> bloquesConHongoOMoneda = new ArrayList<>();
    private ArrayList<Enemigo> enemigos = new ArrayList<>();
    private ArrayList<Hongo> hongos = new ArrayList<>();
    private ArrayList<Moneda> monedas = new ArrayList<>();
    private ArrayList<BanderaMeta> banderas = new ArrayList<>();
    private ArrayList<JefePekka> jefes = new ArrayList<>();
    private JefePekka jefe;

    private int screenHeight;
    private final int BLOCK_WIDTH = 64;
    private final int JUMP_GAP_DISTANCE = BLOCK_WIDTH * 2; // máximo salto horizontal seguro

    public Nivel3(int screenHeight) {
        this.screenHeight = screenHeight;
        inicializarNivel();
    }

    private void inicializarNivel() {
        int groundY = screenHeight - 64;
        int currentX = 0;

        // --------------------------
        // --------------------------
        for (int i = 0; i < 20; i++) {
            bloquesPiso.add(new BloquePiso(currentX, groundY));
            currentX += BLOCK_WIDTH;
        }

        bloquesConHongoOMoneda.add(new BloqueConHongoOMoneda(6 * BLOCK_WIDTH, groundY - 96 - 86,
                BloqueConHongoOMoneda.Contenido.MONEDA));
        bloquesConHongoOMoneda.add(new BloqueConHongoOMoneda(10 * BLOCK_WIDTH, groundY - 96 - 86,
                BloqueConHongoOMoneda.Contenido.HONGO));

        // --------------------------
        // --------------------------
        currentX += JUMP_GAP_DISTANCE;
        for (int i = 0; i < 6; i++) {
            bloquesPiso.add(new BloquePiso(currentX + i * BLOCK_WIDTH, groundY - 96));
        }
        enemigos.add(new Enemigo(currentX + 2 * BLOCK_WIDTH, groundY - 96 - 48));
        currentX += 6 * BLOCK_WIDTH + JUMP_GAP_DISTANCE;

        // --------------------------
        // --------------------------
        for (int i = 0; i < 12; i++) {
            if (i % 3 == 2) currentX += BLOCK_WIDTH; // hueco de 1 bloque
            else {
                bloquesPiso.add(new BloquePiso(currentX, groundY));
                currentX += BLOCK_WIDTH;
            }
        }
        currentX += JUMP_GAP_DISTANCE;

        // --------------------------
        // --------------------------
        for (int i = 0; i < 3; i++) {
            bloquesNormales.add(new BloqueNormal(currentX + i * BLOCK_WIDTH, groundY - 96));
            if (i % 2 == 0)
                bloquesConHongoOMoneda.add(new BloqueConHongoOMoneda(currentX + i * BLOCK_WIDTH, groundY - 128 - 160,
                        BloqueConHongoOMoneda.Contenido.MONEDA));
        }
        currentX += 4 * BLOCK_WIDTH;

        // --------------------------
        // --------------------------
        int steps = 3;
        for (int s = 0; s < steps; s++) {
            for (int h = 0; h <= s; h++) {
                bloquesNormales.add(new BloqueNormal(currentX + s * BLOCK_WIDTH, groundY - BLOCK_WIDTH * (h + 1)));
            }
        }
        bloquesConHongoOMoneda.add(new BloqueConHongoOMoneda(currentX + (steps - 1) * BLOCK_WIDTH,
                groundY - BLOCK_WIDTH * (steps + 1) - 128, BloqueConHongoOMoneda.Contenido.HONGO));
        currentX += (steps + 2) * BLOCK_WIDTH;

        // --------------------------
        // --------------------------
        for (int p = 0; p < 4; p++) {
            bloquesNormales.add(new BloqueNormal(currentX + p * BLOCK_WIDTH, groundY - (80 + p * 32)));
            monedas.add(new Moneda(currentX + p * BLOCK_WIDTH + 16,
                    groundY - (80 + p * 32) - 128));
        }
        currentX += 5 * BLOCK_WIDTH;

        // -------------------------
        // --------------------------
        for (int i = 0; i < 6; i++) {
            int yOffset = (i % 2 == 0) ? 128 : 80;
            bloquesNormales.add(new BloqueNormal(currentX + i * (BLOCK_WIDTH + 16), groundY - yOffset));
        }
        currentX += 6 * (BLOCK_WIDTH + 16);

        // --------------------------
        // --------------------------
        currentX += BLOCK_WIDTH; // offset inicial
        bloquesPiso.add(new BloquePiso(currentX, groundY));
        currentX += BLOCK_WIDTH * 2; // hueco doble
        bloquesPiso.add(new BloquePiso(currentX, groundY));
        currentX += BLOCK_WIDTH * 3;

        // --------------------------
        // --------------------------
        int bossPlatformLength = 20;
        int bossY = groundY - 64;
        int bossStartX = currentX;

        // Piso del jefe
        for (int i = 0; i < bossPlatformLength; i++)
            bloquesPiso.add(new BloquePiso(currentX + i * BLOCK_WIDTH, bossY));

        // Enemigos normales

        // 👑 Jefe Pekka simple
        jefe = new JefePekka(
                bossStartX + 8 * BLOCK_WIDTH, // posición inicial
                bossY - 96,                   // altura sobre la plataforma
                bossStartX,                   // inicio de la plataforma
                bossPlatformLength * BLOCK_WIDTH // ancho de la plataforma
        );
        jefes.add(jefe);

        currentX += bossPlatformLength * BLOCK_WIDTH + JUMP_GAP_DISTANCE;

        // --------------------------
        // --------------------------
        int wallX = currentX;
        for (int h = 0; h < 8; h++)
            bloquesNormales.add(new BloqueNormal(wallX, groundY - h * BLOCK_WIDTH));
        banderas.add(new BanderaMeta(wallX - 300, groundY - 64));
    }

    // Getters
    public ArrayList<BloquePiso> getBloquesPiso() { return bloquesPiso; }
    public ArrayList<BloqueNormal> getBloquesNormales() { return bloquesNormales; }
    public ArrayList<BloqueConHongoOMoneda> getBloquesConHongoOMoneda() { return bloquesConHongoOMoneda; }
    public ArrayList<Enemigo> getEnemigos() { return enemigos; }
    public ArrayList<Hongo> getHongos() { return hongos; }
    public ArrayList<Moneda> getMonedas() { return monedas; }
    public ArrayList<BanderaMeta> getBanderas() { return banderas; }
    public ArrayList<JefePekka> getJefes() { return jefes; }
    public JefePekka getJefe() { return jefe; }
}
