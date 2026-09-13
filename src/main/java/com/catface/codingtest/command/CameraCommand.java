package com.catface.codingtest.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class CameraCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("camkey")
                        .executes(context -> {
                            context.getSource().sendSuccess(
                                    () -> Component.literal(
                                            "Command is working."
                                    ),
                                    false
                            );

                            return 1;
                        })
        );
    }
}
