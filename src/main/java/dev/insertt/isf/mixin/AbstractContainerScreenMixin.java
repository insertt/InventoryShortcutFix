package dev.insertt.isf.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil.KeyCode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

    private static Field KEY_CODE_FIELD;

    static {
        for (Field field : KeyBinding.class.getDeclaredFields()) {
            if (field.getType() == KeyCode.class && ! Modifier.isFinal(field.getModifiers())) {
                field.setAccessible(true);
                KEY_CODE_FIELD = field;
                break;
            }
        }
    }

    @ModifyArg(method = "mouseClicked(DDI)Z",
               at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(JI)Z"), index = 1)
    public int properKeyCode(int keyCode) {
        KeyCode code;
        try {
            code = (KeyCode) KEY_CODE_FIELD.get(MinecraftClient.getInstance().options.keySneak);
        }
        catch (IllegalAccessException ex) {
            throw new RuntimeException("Could not retrieve sneak keybind!", ex);
        }

        switch (keyCode) {
            case 340:
            case 344:
                return code.getKeyCode();
            default:
                return keyCode;
        }
    }

}
