package Radium.Editor;

import Radium.Engine.Input.Input;
import Radium.Engine.Input.Keys;
import Radium.Engine.SceneManagement.SceneManager;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Can manage, create, and listen to key bindings
 */
public class KeyBindManager {

    private static Hashtable<Keys[], Runnable> keybindActions = new Hashtable<>();
    private static List<Boolean> keybindDown = new ArrayList<>();

    protected KeyBindManager() {}

    /**
     * Creates a keybind
     * @param keys Keys pressed to trigger keybind
     * @param action Keybind callback
     */
    public static void RegisterKeybind(Keys[] keys, Runnable action) {
        keybindActions.put(keys, action);
        keybindDown.add(false);
    }

    /**
     * Initialize basic keybindings
     */
    public static void Initialize() {
        RegisterKeybind(new Keys[] { Keys.Delete }, () -> {
            if (SceneHierarchy.current != null) {
                SceneHierarchy.current.Destroy();
                SceneHierarchy.current = null;
            }
        });

        RegisterKeybind(new Keys[] { Keys.LeftCtrl, Keys.S }, () -> {
            SceneManager.GetCurrentScene().Save();
        });
    }

    /**
     * Listens for keybinds
     */
    public static void Update() {
        int index = 0;
        for (Keys[] keys : keybindActions.keySet()) {
            boolean runAction = true;
            for (Keys key : keys) {
                if (!Input.GetKey(key)) {
                    runAction = false;
                }
            }

            if (runAction && !keybindDown.get(index)) {
                keybindActions.get(keys).run();
                keybindDown.set(index, true);
            } else if (!runAction && keybindDown.get(index)) {
                keybindDown.set(index, false);
            }
            index++;
        }
    }

}