package info.sciman.alchemicalbricks.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import info.sciman.alchemicalbricks.AlchemicalBricksMod;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class AlchemicAltarScreen extends HandledScreen<ScreenHandler> {
    private static final Identifier TEXTURE = AlchemicalBricksMod.id("textures/gui/container/alchemical_workbench_gui.png");

    AlchemicAltarScreenHandler screenHandler;


    public AlchemicAltarScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        screenHandler = (AlchemicAltarScreenHandler) handler;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1f,1f,1f,1f);
        client.getTextureManager().bindTexture(TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices,x,y,0,0,backgroundWidth,backgroundHeight);

        // Draw entropy meter
        int progress = (int) (screenHandler.getEntropyPercentage()*80);
        if (progress > 0) {
            drawTexture(matrices,x+50,y+26,0,166, progress,7);
        }
        progress = screenHandler.getConversionProgress();
        if (progress > 0) {
            drawTexture(matrices,x+73,y+41,176,14, progress,17);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices,mouseX,mouseY,delta);
        drawMouseoverTooltip(matrices,mouseX,mouseY);
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack matrices, int x, int y) {

        if (x > this.x+50 && y > this.y+26 && x < this.x+150 && y < this.y+33) {
            int entropy = (int) (screenHandler.getEntropyPercentage()*100);
            Text text = new TranslatableText("gui.alchemicalbricks.altar.entropy").append(new LiteralText(entropy+"%"));
            renderTooltip(matrices,text,x,y);
        }

        super.drawMouseoverTooltip(matrices, x, y);
    }

    @Override
    protected void init() {
        super.init();
        // Center title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}
