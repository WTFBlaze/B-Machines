package me.wtfblaze.bmachines.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.wtfblaze.bmachines.BMachines;
import me.wtfblaze.bmachines.screen.renderer.EnergyInfoArea;
import me.wtfblaze.bmachines.screen.renderer.FluidTankRenderer;
import me.wtfblaze.bmachines.util.MouseUtil;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.TooltipFlag;

import java.util.Optional;

public class StoneInfusionGeneratorScreen extends AbstractContainerScreen<StoneInfusionGeneratorMenu>
{
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(BMachines.MODID, "textures/gui/stone_infusion_generator_gui.png");
    private EnergyInfoArea energyInfoArea;
    private FluidTankRenderer renderer;

    public StoneInfusionGeneratorScreen(StoneInfusionGeneratorMenu menu, Inventory inv, Component component)
    {
        super(menu, inv, component);
    }

    @Override
    protected void init() {
        super.init();
        assignEnergyInfoArea();
        assignFluidRenderer();
    }

    private void assignFluidRenderer() {
        renderer = new FluidTankRenderer(10000, true, 16, 72);
    }

    private void assignEnergyInfoArea() {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        energyInfoArea = new EnergyInfoArea(x + 8, y + 15, menu.blockEntity.getEnergyStorage());
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY)
    {
        super.renderLabels(stack, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderEnergyAreaTooltips(stack, mouseX, mouseY, x, y);
        renderFluidAreaTooltips(stack, mouseX, mouseY, x, y);
    }

    private void renderEnergyAreaTooltips(PoseStack pPoseStack, int pMouseX, int pMouseY, int x, int y) {
        if (isMouseAboveArea(pMouseX, pMouseY, x, y, 8, 15, 5, 56))
            renderTooltip(pPoseStack, energyInfoArea.getTooltips(), Optional.empty(), pMouseX - x, pMouseY - y);
    }

    private void renderFluidAreaTooltips(PoseStack pPoseStack, int pMouseX, int pMouseY, int x, int y) {
        if (isMouseAboveArea(pMouseX, pMouseY, x, y, 152, 8)){
            renderTooltip(pPoseStack, renderer.getTooltip(menu.getFluidStack(), TooltipFlag.Default.NORMAL),
                    Optional.empty(), pMouseX - x, pMouseY - y);
        }
    }

    @Override
    protected void renderBg(PoseStack stack, float pPartialTick, int pMouseX, int pMouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        this.blit(stack, x, y, 0, 0, imageWidth, imageHeight);

        if (menu.isInfusing())
            blit(stack, x + 82, y + 37, 176, 0, 11, menu.getScaledProgress());

        energyInfoArea.draw(stack);
        renderer.render(stack, x + 152, y + 8, menu.getFluidStack());
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta)
    {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }

    private boolean isMouseAboveArea(int mouseX, int mouseY, int x, int y, int offsetX, int offsetY){
        return MouseUtil.isMouseOver(mouseX, mouseY, x + offsetX, y + offsetY, renderer.getWidth(), renderer.getHeight());
    }

    private boolean isMouseAboveArea(int mouseX, int mouseY, int x, int y, int offsetX, int offsetY, int width, int height){
        return MouseUtil.isMouseOver(mouseX, mouseY, x + offsetX, y + offsetY, width, height);
    }
}
