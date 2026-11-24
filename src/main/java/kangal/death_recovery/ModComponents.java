package kangal.death_recovery;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;
import com.mojang.serialization.Codec;

public final class ModComponents {

    public static final AttachmentType<Integer> deathrecover =
            AttachmentRegistry.create(
                    Identifier.fromNamespaceAndPath("deathrecovery","left"),
                    integerBuilder -> integerBuilder
                            .initializer(() -> 3)
                            .persistent(Codec.INT)
                            .copyOnDeath()
            );
}
