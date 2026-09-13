package com.catface.codingtest.command;

import com.catface.codingtest.manager.KeyframeManager;
import com.catface.codingtest.model.CameraKeyframe;
import com.catface.codingtest.model.KeyframeSequence;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class CameraCommand {

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher,
            KeyframeManager keyframeManager) {

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

                        // update for '/camkey add <name>'
                        .then(
                                Commands.literal("add")
                                        .then(
                                                Commands.argument(
                                                        "name",
                                                        StringArgumentType.word()
                                                )
                                                        .executes(context -> {

                                                            String name =
                                                                    StringArgumentType.getString(
                                                                            context,
                                                                            "name"
                                                                    );

                                                            ServerPlayer player =
                                                                    context.getSource()
                                                                            .getPlayerOrException();

                                                            CameraKeyframe keyframe =
                                                                    new CameraKeyframe(
                                                                            name,
                                                                            player.getX(),
                                                                            player.getY(),
                                                                            player.getZ(),
                                                                            player.getYRot(),
                                                                            player.getXRot()
                                                                    );

                                                            KeyframeSequence sequence =
                                                                    keyframeManager
                                                                            .getOrCreateSequence(name);

                                                            sequence.addKeyframe(keyframe);

                                                            context.getSource().sendSuccess(
                                                                    () -> Component.literal(
                                                                            "KeyFrame Captured '"
                                                                            + name
                                                                            +"' at ("
                                                                            + String.format(
                                                                                    "%.2f, %.2f, %.2f",
                                                                                    player.getX(),
                                                                                    player.getY(),
                                                                                    player.getZ()
                                                                            )
                                                                            + ")"
                                                                    ),
                                                                    false
                                                            );

                                                            return 1;
                                                        })
                                        )
                        )
        );
    }
}
