package com.che.teslacoil.client;

import com.che.teslacoil.TeslaCoilMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = TeslaCoilMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class TeslaArcManager {
    private static final List<Arc> ARCS = new ArrayList<>();

    public static void addArc(BlockPos coilPos, int targetEntityId) {
        ARCS.add(new Arc(coilPos.immutable(), targetEntityId, 8));
    }

    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        PoseStack pose = event.getPoseStack();
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cam = camera.getPosition();

        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        VertexConsumer lines = buffers.getBuffer(RenderType.lines());

        Iterator<Arc> it = ARCS.iterator();
        while (it.hasNext()) {
            Arc arc = it.next();
            Entity target = mc.level.getEntity(arc.targetEntityId);
            if (target == null || arc.life-- <= 0) {
                it.remove();
                continue;
            }

            // Top center of the physical three-block-tall Tesla coil.
            Vec3 start = new Vec3(arc.coilPos.getX() + 0.5, arc.coilPos.getY() + 3.02, arc.coilPos.getZ() + 0.5);
            Vec3 end = target.getBoundingBox().getCenter();

            drawBolt(pose, lines, start.subtract(cam), end.subtract(cam),
                    arc.coilPos.asLong() ^ target.getId() ^ (mc.level.getGameTime() * 31L));
        }

        buffers.endBatch(RenderType.lines());
    }

    private static void drawBolt(PoseStack pose, VertexConsumer out, Vec3 start, Vec3 end, long seed) {
        Random random = new Random(seed);
        int segments = 14;
        Vec3 previous = start;

        for (int i = 1; i <= segments; i++) {
            double t = i / (double) segments;
            Vec3 point = start.lerp(end, t);

            if (i != segments) {
                double fade = Math.sin(Math.PI * t);
                double jitter = 0.18 * fade;
                point = point.add(
                        (random.nextDouble() - 0.5) * jitter,
                        (random.nextDouble() - 0.5) * jitter,
                        (random.nextDouble() - 0.5) * jitter);
            }

            line(pose, out, previous, point, 0.45f, 0.85f, 1.0f, 1.0f);
            previous = point;
        }
    }

    private static void line(PoseStack pose, VertexConsumer out, Vec3 a, Vec3 b,
                             float r, float g, float bl, float alpha) {
        Vec3 n = b.subtract(a).normalize();
        PoseStack.Pose p = pose.last();

        out.vertex(p.pose(), (float)a.x, (float)a.y, (float)a.z)
                .color(r, g, bl, alpha)
                .normal(p.normal(), (float)n.x, (float)n.y, (float)n.z)
                .endVertex();
        out.vertex(p.pose(), (float)b.x, (float)b.y, (float)b.z)
                .color(1.0f, 1.0f, 1.0f, alpha)
                .normal(p.normal(), (float)n.x, (float)n.y, (float)n.z)
                .endVertex();
    }

    private static final class Arc {
        private final BlockPos coilPos;
        private final int targetEntityId;
        private int life;

        private Arc(BlockPos coilPos, int targetEntityId, int life) {
            this.coilPos = coilPos;
            this.targetEntityId = targetEntityId;
            this.life = life;
        }
    }

    private TeslaArcManager() {}
}
