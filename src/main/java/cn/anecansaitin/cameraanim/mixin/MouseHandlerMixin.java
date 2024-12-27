package cn.anecansaitin.cameraanim.mixin;

import cn.anecansaitin.cameraanim.client.listener.MouseInput;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
//    @Inject(method = "turnPlayer",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V")
//    )
    public void wrap$onMove(double movementTime, CallbackInfo ci, @Local(ordinal = 4) double x, @Local(ordinal = 5) double y) {
//        MouseInput.onMouseDragged((float) x, (float) y);
    }
}