package Engine.Objects;

import Editor.Console;
import Engine.Component;
import Engine.Components.Graphics.MeshRenderer;
import Engine.Math.Transform;
import Engine.SceneManagement.SceneManager;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.List;

public class GameObject implements Cloneable {

    public String name = "New Game Object";
    public Transform transform;

    private GameObject storedGameObject;

    private List<Component> components = new ArrayList<Component>();

    public GameObject() {
        transform = new Transform();
        SceneManager.GetCurrentScene().gameObjectsInScene.add(this);
    }

    public GameObject(boolean instantiate) {
        transform = new Transform();
        if (instantiate) SceneManager.GetCurrentScene().gameObjectsInScene.add(this);
    }

    public void OnPlay() {
        Clone();
    }

    public void OnStop() {
        name = storedGameObject.name;
        components = storedGameObject.components;
        transform = storedGameObject.transform;
    }

    public void Destroy() {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).OnRemove();
            RemoveComponent(components.get(i).getClass());
        }

        components.clear();

        SceneManager.GetCurrentScene().gameObjectsInScene.remove(this);
    }

    public <T extends Component> T GetComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (Exception e) {
                    Console.Error(e);
                }
            }
        }
        return null;
    }

    public Component AddComponent(Component component) {
        components.add(component);

        component.gameObject = this;
        component.OnAdd();

        return component;
    }

    public <T extends Component> void RemoveComponent(Class<T> componentClass) {
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                components.remove(i);

                return;
            }
        }
    }

    public List<Component> GetComponents() {
        return components;
    }

    public boolean ContainsComponent(Class component) {
        return GetComponent(component) != null;
    }

    public void Clone()
    {
        storedGameObject = new GameObject(false);

        storedGameObject.transform = new Transform();
        storedGameObject.transform.position = transform.position;
        storedGameObject.transform.rotation = transform.rotation;
        storedGameObject.transform.scale = transform.scale;

        storedGameObject.components = new ArrayList<>(components);
        storedGameObject.name = new String(name);
    }
}
