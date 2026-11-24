package kangal.death_recovery;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.level.Level;

import java.util.Set;

public class DeathrecoveryCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("death").executes(DeathrecoveryCommands::executeDeathCountList)
                .then(Commands.literal("recover").executes(DeathrecoveryCommands::recoverDeath))
                .then(Commands.literal("grant")
                        .requires(sources -> sources.permissions().hasPermission(Permissions.COMMANDS_ADMIN))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(DeathrecoveryCommands::grantDeathRecover))
                )
        );


    }

    private static int executeDeathCountList(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        assert player != null;
        Integer deathCount = player.getAttachedOrElse(ModComponents.deathrecover,3);

        context.getSource().sendSuccess( () -> Component.literal("You have "+deathCount+" left"), false);


        return Command.SINGLE_SUCCESS;
    }

    private static int recoverDeath(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        assert player != null;

        if (player.getLastDeathLocation().isEmpty()){
            context.getSource().sendSuccess( () -> Component.literal("You have not been death yet"), false);
            return Command.SINGLE_SUCCESS;
        }

        if (player.getAttached(ModComponents.deathrecover) == 0){
            context.getSource().sendSuccess( () -> Component.literal("You have no recover left"), false);
            return Command.SINGLE_SUCCESS;
        }

        BlockPos playerdeathLocation = player.getLastDeathLocation().get().pos();
        ResourceKey<Level> deathWorld = player.getLastDeathLocation().get().dimension();
        MinecraftServer server = context.getSource().getServer();
        ServerLevel targetworld = server.getLevel(deathWorld);

        context.getSource().sendSuccess( () -> Component.literal("Teleporting to last death location"), false);
        player.modifyAttached(ModComponents.deathrecover,
                deathleft -> deathleft -1);

        player.teleportTo(targetworld,
                playerdeathLocation.getX(),
                playerdeathLocation.getY(),
                playerdeathLocation.getZ(),
                Set.of(),
                player.getXRot(),
                player.getYRot(),
                false
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int grantDeathRecover(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        ServerPlayer player = EntityArgument.getPlayer(context, "player");

        player.modifyAttached(ModComponents.deathrecover,
                deathleft -> deathleft +1);

        context.getSource().sendSuccess( () -> Component.literal("You have granted "+player.getName().getString()+" 1 more death recover"), true);
        ServerPlayer serverPlayer = context.getSource().getPlayer();
        assert serverPlayer != null;
        player.sendSystemMessage( Component.literal(serverPlayer.getName().getString() + " has granted you 1 more death recover"));

        return Command.SINGLE_SUCCESS;
    }

}
