package Engine.Components.Physics;

import Editor.Console;
import Engine.Component;
import Engine.Graphics.Texture;
import Engine.Math.Vector.Vector3;
import Engine.PerformanceImpact;
import Engine.Physics.ColliderType;
import Engine.Physics.ForceMode;
import Engine.Physics.PhysicsManager;
import Engine.Physics.PhysxUtil;
import imgui.ImGui;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.geomutils.*;
import physx.physics.*;

public class Rigidbody extends Component {

    public float mass = 1f;
    public boolean applyGravity = true;
    public boolean lockRotation = false;

    public ColliderType collider = ColliderType.Box;

    private transient PxRigidDynamic body;

    private float radius = 0.5f;
    private float height = 1;
    private Vector3 colliderScale = Vector3.One;

    public Rigidbody() {
        description = "A body that handles collisions and physics";
        impact = PerformanceImpact.Medium;
        icon = new Texture("EngineAssets/Editor/Icons/rigidbody.png").textureID;
    }

    public Rigidbody(Vector3 colliderScale) {
        description = "A body that handles collisions and physics";
        impact = PerformanceImpact.Medium;
        icon = new Texture("EngineAssets/Editor/Icons/rigidbody.png").textureID;

        this.colliderScale = colliderScale;
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {
        if (!applyGravity) {
            body.setLinearVelocity(new PxVec3(0, 0, 0));
        } if (lockRotation) {
            PxTransform pose = body.getGlobalPose();
            pose.setQ(PhysxUtil.SetEuler(gameObject.transform.rotation));
            body.setGlobalPose(pose);
        }

        gameObject.transform.position = PhysxUtil.FromPx3(body.getGlobalPose().getP());
        gameObject.transform.rotation = PhysxUtil.GetEuler(body.getGlobalPose().getQ());
    }

    @Override
    public void Stop() {
        ResetBody();
    }

    @Override
    public void OnAdd() {
        CreateBody();
    }

    @Override
    public void OnRemove() {

    }

    @Override
    public void UpdateVariable() {
        CreateBody();

        body.setMass(mass);
    }

    @Override
    public void GUIRender() {
        if (collider == ColliderType.Box) {
            float[] imVec = { colliderScale.x, colliderScale.y, colliderScale.z };
            if (ImGui.dragFloat3("Collider Scale", imVec)) {
                colliderScale.Set(imVec[0], imVec[1], imVec[2]);

                UpdateVariable();
            }
        } else if (collider == ColliderType.Sphere) {
            float[] imFloat = { radius };
            if (ImGui.dragFloat("Collider Radius", imFloat)) {
                radius = imFloat[0];
                UpdateVariable();
            }
        } else if (collider == ColliderType.Capsule) {
            float[] imRadius = { radius };
            if (ImGui.dragFloat("Collider Radius", imRadius)) {
                radius = imRadius[0];
                UpdateVariable();
            }

            float[] imHeight = { height };
            if (ImGui.dragFloat("Collider Height", imHeight)) {
                height = imHeight[0];
                UpdateVariable();
            }
        }
    }

    public PxRigidDynamic GetBody() {
        return body;
    }

    private void CreateBody() {
        if (body != null) {
            PhysicsManager.GetPhysicsScene().removeActor(body);
        }
        body = null;

        PxMaterial material = PhysicsManager.GetPhysics().createMaterial(0.5f, 0.5f, 0.5f);
        PxShapeFlags shapeFlags = new PxShapeFlags((byte) (PxShapeFlagEnum.eSCENE_QUERY_SHAPE | PxShapeFlagEnum.eSIMULATION_SHAPE));
        PxTransform tmpPose = new PxTransform(PhysxUtil.ToPx3(gameObject.transform.position), PhysxUtil.SetEuler(gameObject.transform.rotation));
        PxFilterData tmpFilterData = new PxFilterData(1, 1, 0, 0);

        PxGeometry geometry = null;

        if (collider == ColliderType.Box) {
            geometry = new PxBoxGeometry(colliderScale.x / 2, colliderScale.y / 2, colliderScale.z / 2);
        } else if (collider == ColliderType.Sphere) {
            geometry = new PxSphereGeometry(radius);
        } else if (collider == ColliderType.Capsule) {
            geometry = new PxCapsuleGeometry(radius, height / 2);
        }

        PxShape shape = PhysicsManager.GetPhysics().createShape(geometry, material, true, shapeFlags);
        body = PhysicsManager.GetPhysics().createRigidDynamic(tmpPose);
        shape.setSimulationFilterData(tmpFilterData);

        body.attachShape(shape);
        body.setMass(mass);

        shape.release();

        PhysicsManager.GetPhysicsScene().addActor(body);
    }

    private void ResetBody() {
        body.setGlobalPose(new PxTransform(PhysxUtil.ToPx3(gameObject.transform.position), PhysxUtil.SetEuler(gameObject.transform.rotation)));
        body.setLinearVelocity(new PxVec3(0, 0, 0));
        body.setAngularVelocity(new PxVec3(0, 0, 0));
    }

    public void AddForce(Vector3 force) {
        body.addForce(PhysxUtil.ToPx3(force));
    }

    public void AddForce(Vector3 force, ForceMode forceMode) {
        int mode = PxForceModeEnum.eFORCE;
        switch (forceMode) {
            case Acceleration:
                mode = PxForceModeEnum.eACCELERATION;
                break;
            case Force:
                mode = PxForceModeEnum.eFORCE;
                break;
            case Impulse:
                mode = PxForceModeEnum.eIMPULSE;
                break;
        }

        body.addForce(PhysxUtil.ToPx3(force), mode);
    }

    public void AddTorque(Vector3 torque) {
        body.addTorque(PhysxUtil.ToPx3(torque));
    }

}