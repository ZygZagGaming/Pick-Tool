package com.zygzag.picktool;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@Mod("picktool")
public class PickTool {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final KeyBinding KEYBIND = new KeyBinding("key.picktool.picktool", GLFW.GLFW_KEY_GRAVE_ACCENT, "category.picktool.picktool");

    public PickTool() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) { }

    private void doClientStuff(final FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(KEYBIND);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) { }

    private void processIMC(final InterModProcessEvent event) { }

    @Mod.EventBusSubscriber(modid = "picktool")
    private static class InputHandler {
        @SubscribeEvent
        public static void onKeyPress(final TickEvent.ClientTickEvent event) {
            Minecraft mc = Minecraft.getInstance();

            if (KEYBIND.consumeClick() && mc.hitResult != null && mc.hitResult.getType() != RayTraceResult.Type.MISS && mc.player != null && mc.level != null && mc.gameMode != null) {

                RayTraceResult target = mc.hitResult;
                World world = mc.level;
                ItemStack i = null;

                if (target.getType() == RayTraceResult.Type.BLOCK) {
                    BlockPos pos = ((BlockRayTraceResult) target).getBlockPos();
                    BlockState state = world.getBlockState(pos);

                    if (state.isAir(world, pos)) return;

                    PlayerInventory inv = mc.player.inventory;
                    List<ItemStack> hotbar = inv.items.subList(0, 9);

                    for (ItemStack stack : inv.items) {
                        if (stack.getItem().getToolTypes(stack).contains(state.getHarvestTool()) || ((state.getBlock() == Blocks.COBWEB || state.getBlock() == Blocks.BAMBOO) && stack.getItem() instanceof SwordItem)) {
                            mc.player.inventory.setPickedItem(stack);
                            i = stack;
                            break;
                        }
                    }
                }
            }
        }
    }
}
