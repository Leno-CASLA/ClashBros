package CLASES;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class EventoTeclado extends KeyAdapter {

    private HashMap<Integer, Boolean> keys = new HashMap<>();

    public EventoTeclado() {
        keys.put(KeyEvent.VK_A, false);
        keys.put(KeyEvent.VK_D, false);
        keys.put(KeyEvent.VK_SPACE, false);
        keys.put(KeyEvent.VK_SHIFT, false); // 👈 ahora también detecta Shift
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys.put(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys.put(e.getKeyCode(), false);
    }

    public boolean isKeyDown(int key) {
        return keys.getOrDefault(key, false);
    }
}
