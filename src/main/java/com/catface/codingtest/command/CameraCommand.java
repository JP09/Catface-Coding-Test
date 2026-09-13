package com.catface.codingtest.command;

import com.catface.codingtest.CatfaceCodingTestModClient;
import com.catface.codingtest.manager.KeyframeManager;
import com.catface.codingtest.model.CameraKeyframe;
import com.catface.codingtest.model.KeyframeSequence;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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

                        //update for '/camkey play <sequence> <seconds>
                        .then(
                                Commands.literal("play")
                                        .then(
                                                Commands.argument("sequence", StringArgumentType.word())
                                                        .then(
                                                                Commands.argument("seconds", IntegerArgumentType.integer(1))
                                                                        .executes(context -> {
                                                                            String seqName = StringArgumentType.getString(context, "sequence");
                                                                            int seconds = IntegerArgumentType.getInteger(context, "seconds");

                                                                            KeyframeSequence sequence = keyframeManager.getSequence(seqName);

                                                                            if (sequence == null) {
                                                                                context.getSource().sendFailure(Component.literal("Sequence '" + seqName + "' does not exist."));
                                                                                return 0;
                                                                            }

                                                                            if (sequence.getKeyframes().size() < 2) {
                                                                                context.getSource().sendFailure(Component.literal("Sequence requires at least 2 keyframes to play."));
                                                                                return 0;
                                                                            }

                                                                            CatfaceCodingTestModClient.getCameraPlayback().start(sequence, seconds);

                                                                            context.getSource().sendSuccess(
                                                                                    () -> Component.literal("Playing sequence '" + seqName + "' over " + seconds + " seconds."),
                                                                                    false
                                                                            );

                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )

                        // update for '/camkey stop'
                        .then(
                                Commands.literal("stop")
                                        .executes(context -> {
                                            CatfaceCodingTestModClient.getCameraPlayback().stop();
                                            context.getSource().sendSuccess(() -> Component.literal("Camera playback stopped."), false);
                                            return 1;
                                        })
                        )

                        // update for '/camkey create <name>'
                        .then(
                                Commands.literal("create")
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

                                                            KeyframeSequence sequence =
                                                                    keyframeManager
                                                                            .getOrCreateSequence(name);

                                                            keyframeManager
                                                                    .setCurrentSequenceName(name);

                                                            context.getSource().sendSuccess(
                                                                    () -> Component.literal(
                                                                            "Created and selected sequence '"
                                                                            + sequence.getName()
                                                                            + "'"
                                                                    ),
                                                                    false
                                                            );

                                                            return 1;
                                                        })
                                        )
                        )

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
