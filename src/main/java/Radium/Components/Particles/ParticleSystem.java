package Radium.Components.Particles;

import Radium.Color;
import Radium.Component;
import Radium.Graphics.Material;
import RadiumEditor.Annotations.RunInEditMode;
import RadiumEditor.Console;
import RadiumEditor.Debug.Gizmo.ComponentGizmo;
import Radium.Graphics.Mesh;
import Radium.Graphics.Texture;
import Radium.Math.Random;
import Radium.Math.Transform;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.ParticleSystem.Particle;
import Radium.ParticleSystem.ParticleBatch;
import Radium.ParticleSystem.ParticleRenderer;
import Radium.PerformanceImpact;
import Radium.Time;

/**
 * Creates and renders particles
 */
public class ParticleSystem extends Component {

    /**
     * Size of particles
     */
    public Vector2 particleScale = new Vector2(0.1f, 0.1f);
    /**
     * Color of particles
     */
    public Color color = new Color(1f, 1f, 1f);
    /**
     * Determines if particles are random colors
     */
    public boolean randomColors = false;
    /**
     * Determines if gravity applies to particles
     */
    public boolean applyGravity = true;
    /**
     * Number of particles created a second
     */
    public float emissionRate = 10;
    /**
     * Lifespan of each individual particle
     */
    public float particleLifespan = 5f;
    /**
     * Area in which particles can spawn
     */
    public float particleSpawnRange = 0.5f;
    /**
     * Rotation of particles at creation
     */
    public float startRotation = 0;
    /**
     * Determines if particles have a random rotation when created
     */
    public boolean randomRotation;
    /**
     * Plays when the application is played
     */
    public boolean playOnAwake = true;

    public Material material = new Material("EngineAssets/Textures/Misc/blank.jpg");

    private transient float emissionRateTime = 0;
    private transient float spawnTime = 0;
    private transient ParticleRenderer renderer;
    private transient ParticleBatch batch;

    private transient ComponentGizmo gizmo;

    private boolean playing = false;

    /**
     * Create an empty particle system
     */
    public ParticleSystem() {
        name = "Particle System";
        description = "Generates particles";
        impact = PerformanceImpact.Low;
        icon = new Texture("EngineAssets/Editor/Icons/particlesystem.png").textureID;
        submenu = "Particles";
    }

    @Override
    public void Start() {
        if (playOnAwake) PlayParticles();
    }

    @Override
    public void Update() {
        if (!playing) return;

        spawnTime += Time.deltaTime;
        if (spawnTime >= emissionRateTime) {
            Transform particleTransform = new Transform();
            particleTransform.position = new Vector3(gameObject.transform.position.x + Random.RandomFloat(-particleSpawnRange, particleSpawnRange), gameObject.transform.position.y, gameObject.transform.position.z + Random.RandomFloat(-particleSpawnRange, particleSpawnRange));
            particleTransform.rotation = new Vector3(0, 90, 90);
            particleTransform.scale = new Vector3(particleScale.x, particleScale.y, particleScale.x);

            float rotation = randomRotation ? Random.RandomFloat(0, 360) : startRotation;

            Particle particle = new Particle(particleTransform, batch, particleLifespan, Color.Green(), applyGravity, rotation);
            particle.color = color;
            if (randomColors) {
                Color col = new Color(Random.RandomFloat(0f, 1f), Random.RandomFloat(0f, 1f), Random.RandomFloat(0f, 1f));
                particle.color = col;
            }

            batch.particles.add(particle);
            spawnTime = 0;
        }

        renderer.Render();
    }

    @Override
    public void Stop() {
        renderer.batch.particles.clear();
    }

    @Override
    public void OnAdd() {
        ParticleBatch particleBatch = new ParticleBatch(Mesh.Plane(particleScale.x, particleScale.y));
        renderer = new ParticleRenderer(particleBatch);
        batch = renderer.batch;
        batch.material = material;

        emissionRateTime = 1 / emissionRate;

        gizmo = new ComponentGizmo(gameObject, new Texture("EngineAssets/Editor/Icons/particlesystem.png"));
    }

    @Override
    public void OnRemove() {
        gizmo.Destroy();
    }

    @Override
    public void UpdateVariable() {
        UpdateBatch();
    }

    @Override
    public void GUIRender() {

    }

    public void UpdateBatch() {
        batch.Destroy();

        ParticleBatch particleBatch = new ParticleBatch(Mesh.Plane(particleScale.x, particleScale.y));
        renderer = new ParticleRenderer(particleBatch);
        batch = renderer.batch;
        batch.material = material;

        emissionRateTime = 1 / emissionRate;
    }

    public void PlayParticles() {
        playing = true;
    }

    public void StopParticles() {
        playing = false;
        UpdateBatch();
    }

}
