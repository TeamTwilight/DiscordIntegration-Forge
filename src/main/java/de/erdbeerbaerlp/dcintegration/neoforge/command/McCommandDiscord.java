package de.erdbeerbaerlp.dcintegration.neoforge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.erdbeerbaerlp.dcintegration.common.minecraftCommands.MCSubCommand;
import de.erdbeerbaerlp.dcintegration.common.minecraftCommands.McCommandRegistry;
import de.erdbeerbaerlp.dcintegration.common.storage.Configuration;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;


public class McCommandDiscord {
    public McCommandDiscord(CommandDispatcher<CommandSourceStack> dispatcher) {
        final LiteralArgumentBuilder<CommandSourceStack> l = Commands.literal("discord");
        if (Configuration.instance().ingameCommand.enabled)
            l.executes((ctx) -> {
            ctx.getSource().sendSuccess(()->ComponentUtils.mergeStyles(Component.literal(Configuration.instance().ingameCommand.message),
                            Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(Configuration.instance().ingameCommand.hoverMessage)))
                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Configuration.instance().ingameCommand.inviteURL))), false);
            return 0;
        });
        for (MCSubCommand cmd : McCommandRegistry.getCommands()) {
            l.then(Commands.literal(cmd.getName()));
        }
        dispatcher.register(l);
    }
}
