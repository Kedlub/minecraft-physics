package me.arzyk.physics.network;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class DataSerializers {
    public static final TrackedDataHandler<Quat4f> QUAT4F = new TrackedDataHandler<>() {

        @Override
        public void write(PacketByteBuf buf, Quat4f value) {
            buf.writeFloat(value.x);
            buf.writeFloat(value.y);
            buf.writeFloat(value.z);
            buf.writeFloat(value.w);
        }

        @Override
        public Quat4f read(PacketByteBuf buf) {
            return new Quat4f(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
        }

        @Override
        public Quat4f copy(Quat4f value) {
            return value;
        }
    };

    public static final TrackedDataHandler<Vector3f> VECTOR3F = new TrackedDataHandler<>() {

        @Override
        public void write(PacketByteBuf buf, Vector3f value) {
            buf.writeFloat(value.x);
            buf.writeFloat(value.y);
            buf.writeFloat(value.z);
        }

        @Override
        public Vector3f read(PacketByteBuf buf) {
            return new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
        }

        @Override
        public Vector3f copy(Vector3f value) {
            return value;
        }
    };

    public static void register() {
        TrackedDataHandlerRegistry.register(QUAT4F);
        TrackedDataHandlerRegistry.register(VECTOR3F);
    }
}
