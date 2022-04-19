package Radium.Components.Graphics;

import Radium.Component;
import Radium.Graphics.RendererType;
import Radium.Graphics.Renderers.Renderer;
import Radium.Graphics.Renderers.Renderers;
import Radium.Graphics.Texture;
import Radium.PerformanceImpact;
import RadiumEditor.Annotations.RunInEditMode;
import RadiumEditor.Console;
import org.lwjgl.opengl.GL11;

/**
 * Updates and renders the mesh
 */
@RunInEditMode
public class MeshRenderer extends Component {

    private transient Renderer renderer;
    /**
     * The rendering system to use
     */
    public RendererType renderType = RendererType.Lit;
    /**
     * If enabled, will cull back faces of object
     */
    public boolean cullFaces = false;

    /**
     * Create empty mesh renderer with default rendering settings
     */
    public MeshRenderer() {
        icon = new Texture("EngineAssets/Editor/Icons/meshrenderer.png").textureID;
        renderer = Renderers.renderers.get(renderType.ordinal());

        name = "Mesh Renderer";
        description = "Renders mesh data held in MeshFilter component";
        impact = PerformanceImpact.Low;
        submenu = "Graphics";
    }

    
    public void Start() {

    }

    
    public void Update() {
        if (cullFaces) GL11.glEnable(GL11.GL_CULL_FACE);
        renderer.Render(gameObject);
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    
    public void Stop() {

    }

    
    public void OnAdd() {

    }

    
    public void OnRemove() {
        if (gameObject.ContainsComponent(Outline.class)) {
            Console.Error("Outline depends on Mesh Renderer");
            gameObject.RemoveComponent(Outline.class);
        }
    }

    
    public void UpdateVariable() {
        renderer = Renderers.renderers.get(renderType.ordinal());
        if (gameObject.ContainsComponent(Outline.class)) {
            gameObject.GetComponent(Outline.class).shader = Renderers.GetRenderer(renderType).shader;
        }
    }

    
    public void GUIRender() {

    }

}