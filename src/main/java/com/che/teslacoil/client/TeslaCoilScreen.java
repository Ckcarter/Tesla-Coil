package com.che.teslacoil.client;

import com.che.teslacoil.menu.TeslaCoilMenu;
import com.che.teslacoil.network.ModNetworking;
import com.che.teslacoil.network.WhitelistActionPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class TeslaCoilScreen extends AbstractContainerScreen<TeslaCoilMenu> {
    private EditBox playerName;
    private final List<String> names = new ArrayList<>();
    private int selected = -1;

    public TeslaCoilScreen(TeslaCoilMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 220;
        imageHeight = 190;
    }

    @Override
    protected void init() {
        super.init();
        playerName = new EditBox(font, leftPos + 12, topPos + 32, 135, 20, Component.literal("Player name"));
        playerName.setHint(Component.literal("Player name"));
        addRenderableWidget(playerName);

        addRenderableWidget(Button.builder(Component.literal("Add"), b -> {
            String name = playerName.getValue().trim();
            if (!name.isEmpty()) {
                ModNetworking.CHANNEL.sendToServer(new WhitelistActionPacket(menu.getCoilPos(), 0, name));
                playerName.setValue("");
            }
        }).bounds(leftPos + 152, topPos + 32, 55, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Remove"), b -> {
            if (selected >= 0 && selected < names.size()) {
                ModNetworking.CHANNEL.sendToServer(new WhitelistActionPacket(menu.getCoilPos(), 1, names.get(selected)));
                selected = -1;
            }
        }).bounds(leftPos + 12, topPos + 158, 90, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Refresh"), b ->
                ModNetworking.CHANNEL.sendToServer(new WhitelistActionPacket(menu.getCoilPos(), 2, ""))
        ).bounds(leftPos + 117, topPos + 158, 90, 20).build());

        ModNetworking.CHANNEL.sendToServer(new WhitelistActionPacket(menu.getCoilPos(), 2, ""));
    }

    public void setNames(List<String> newNames) {
        names.clear();
        names.addAll(newNames);
        if (selected >= names.size()) selected = -1;
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF20252B);
        g.fill(leftPos + 5, topPos + 5, leftPos + imageWidth - 5, topPos + imageHeight - 5, 0xFF303840);
        g.fill(leftPos + 10, topPos + 58, leftPos + 210, topPos + 152, 0xFF15191D);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g);
        super.render(g, mouseX, mouseY, partialTick);
        g.drawString(font, "Player Whitelist", leftPos + 12, topPos + 10, 0xFFFFFF, false);

        int y = topPos + 64;
        for (int i = 0; i < names.size() && i < 9; i++) {
            boolean hover = mouseX >= leftPos + 12 && mouseX <= leftPos + 205 &&
                    mouseY >= y - 2 && mouseY <= y + 9;
            if (i == selected) g.fill(leftPos + 11, y - 2, leftPos + 207, y + 10, 0xFF466A86);
            else if (hover) g.fill(leftPos + 11, y - 2, leftPos + 207, y + 10, 0xFF343F48);
            g.drawString(font, names.get(i), leftPos + 15, y, 0xFFFFFF, false);
            y += 10;
        }
        playerName.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int y = topPos + 64;
        for (int i = 0; i < names.size() && i < 9; i++) {
            if (mouseX >= leftPos + 12 && mouseX <= leftPos + 205 &&
                    mouseY >= y - 2 && mouseY <= y + 9) {
                selected = i;
                return true;
            }
            y += 10;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {}

    @Override
    public boolean isPauseScreen() { return false; }
}
