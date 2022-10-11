package com.example.examplemod

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent
import org.apache.logging.log4j.LogManager

@Mod(modid = "examplemod")
class ExampleMod {

    private val logger = LogManager.getLogger("Example Mod")

    init {
        logger.info("Hello from the example mod!")
    }

    @EventHandler
    fun onInitialization(event: FMLInitializationEvent) {
        logger.info("Hello from initialization!!!!")
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onKeyInput(event: KeyInputEvent) {
        logger.info("A key was pressed")
    }

}