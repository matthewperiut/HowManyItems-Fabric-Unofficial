package net.glasslauncher.hmifabric.mixin;

import net.glasslauncher.hmifabric.HowManyItemsClient;
import net.minecraft.client.Minecraft;
import net.minecraft.sortme.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    private long clock = 0L;

    @Shadow
    private Minecraft minecraft;

    @Inject(method = "method_1844", at = @At(value = "TAIL"))
    private void onTick(float delta, CallbackInfo ci) {
        long newClock = 0L;
        if (minecraft.level != null && HowManyItemsClient.thisMod != null) {
            newClock = minecraft.level.getLevelTime();
            if (newClock != clock) {
                HowManyItemsClient.thisMod.onTickInGame(minecraft);
            }
            if (minecraft.currentScreen != null) {
                HowManyItemsClient.thisMod.onTickInGUI(minecraft, minecraft.currentScreen);
            }
        }
        clock = newClock;
    }

}
