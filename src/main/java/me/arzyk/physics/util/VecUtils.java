package me.arzyk.physics.util;

import com.bulletphysics.linearmath.Transform;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.nio.FloatBuffer;

public class VecUtils {
    private static float[] buffer = new float[16];

    public static FloatBuffer setBufferFromTransform(FloatBuffer matrixBuffer, Transform transform) {
        transform.getOpenGLMatrix(buffer);
        matrixBuffer.clear();
        matrixBuffer.put(buffer);
        matrixBuffer.flip();
        return matrixBuffer;
    }

    public static Vector3f toVector3f(Vec3d vector) {
        return new Vector3f((float) vector.getX(), (float) vector.getY(), (float) vector.getZ());
    }

    public static Quat4f toQuat4f(Quaternion quat) {
        return new Quat4f(quat.getX(), quat.getY(), quat.getZ(), quat.getZ());
    }

    public static Vec3d toVec3d(Vector3f vector) {
        return new Vec3d(vector.x,vector.y,vector.z);
    }

    public static Quaternion toQuaternion(Quat4f quat) {
        return new Quaternion(quat.x,quat.y,quat.z,quat.w);
    }
}
