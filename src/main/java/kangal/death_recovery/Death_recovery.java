package kangal.death_recovery;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Death_recovery implements ModInitializer {
	public static final String MOD_ID = "death_recovery";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		//Called register command function
		CommandRegistrationCallback.EVENT.register((dispatcher, commandBuildContext, commandSender) -> {
			DeathrecoveryCommands.register(dispatcher);
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer player = handler.getPlayer();

			if (!player.hasAttached(ModComponents.deathrecover)){
				player.setAttached(ModComponents.deathrecover,3);
			}
		});

	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
