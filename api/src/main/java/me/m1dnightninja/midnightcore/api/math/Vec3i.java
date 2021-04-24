package me.m1dnightninja.midnightcore.api.math;

import me.m1dnightninja.midnightcore.api.config.InlineSerializer;

public class Vec3i {
    private final int[] data;

    public Vec3i(int x, int y, int z) {
        this.data = new int[]{x, y, z};
    }

    public int getX() {
        return this.data[0];
    }

    public int getY() {
        return this.data[1];
    }

    public int getZ() {
        return this.data[2];
    }

    public double distance(Vec3i vec2) {
        return Math.sqrt(this.getX() - vec2.getX() ^ 2 + (this.getY() - vec2.getY()) ^ 2 + (this.getZ() - vec2.getZ()) ^ 2);
    }

    public static Vec3i parse(String str) {

        if(str == null || str.length() == 0 || !str.contains(",")) return null;
        String[] xyz = str.split(",");

        try {
            int x = Integer.parseInt(xyz[0]);
            int y = Integer.parseInt(xyz[1]);
            int z = Integer.parseInt(xyz[2]);

            return new Vec3i(x,y,z);
        } catch (NumberFormatException ex) {
            return null;
        }

    }

    public Vec3i add(int i) {
        return new Vec3i(data[0] + i, data[1] + i, data[2] + i);
    }

    public Vec3i multiply(int i) {
        return new Vec3i(data[0] * i, data[1] * i, data[2] * i);
    }

    public Vec3i add(Vec3i i) {
        return new Vec3i(data[0] + i.data[0], data[1] + i.data[1], data[2] + i.data[2]);
    }

    public Vec3i multiply(Vec3i i) {
        return new Vec3i(data[0] * i.data[0], data[1] * i.data[1], data[2] * i.data[2]);
    }

    public Vec3i subtract(int i) {
        return new Vec3i(data[0] - i, data[1] - i, data[2] - i);
    }

    public Vec3i subtract(Vec3i i) {
        return new Vec3i(data[0] - i.data[0], data[1] - i.data[1], data[2] - i.data[2]);
    }


    public static final InlineSerializer<Vec3i> SERIALIZER = new InlineSerializer<Vec3i>() {
        @Override
        public Vec3i deserialize(String s) {
            return parse(s);
        }

        @Override
        public String serialize(Vec3i object) {
            return toString();
        }
    };
}

