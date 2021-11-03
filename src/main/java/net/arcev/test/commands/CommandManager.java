package net.arcev.test.commands;

import cloud.commandframework.CommandTree;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.keys.SimpleCloudKey;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;
import cloud.commandframework.permission.PredicatePermission;
import lombok.SneakyThrows;
import net.arcev.test.Main;
import net.arcev.test.commands.impl.EchoCommand;
import net.arcev.test.utils.GamemodeCheck;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static net.kyori.adventure.text.Component.text;

public class CommandManager {

    private PaperCommandManager<CommandSender> manager;
    private BukkitAudiences bukkitAudiences;
    private MinecraftHelp<CommandSender> minecraftHelp;
    private CommandConfirmationManager<CommandSender> confirmationManager;
    private AnnotationParser<CommandSender> annotationParser;

    private final Main plugin;

    public CommandManager(Main plugin) {
        this.plugin = plugin;
    }

    @SneakyThrows
    public void init() {

        //Command Manager Start
        final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction =
                AsynchronousCommandExecutionCoordinator.<CommandSender>newBuilder().build();
        final Function<CommandSender, CommandSender> mapperFunction = Function.identity();
        this.manager = new PaperCommandManager<>(
                /* Owning plugin */ plugin,
                /* Coordinator function */ executionCoordinatorFunction,
                /* Command Sender -> C */ mapperFunction,
                /* C -> Command Sender */ mapperFunction
        );
        //Minecraft Help Menu
        this.bukkitAudiences = BukkitAudiences.create(plugin);
        this.minecraftHelp = new MinecraftHelp<>(
                /* Help Prefix */ "/example help",
                /* Audience mapper */ this.bukkitAudiences::sender,
                /* Manager */ this.manager
        );
        // *Brigadier Support Check
        if (this.manager.queryCapability(CloudBukkitCapabilities.BRIGADIER)) {
            this.manager.registerBrigadier();
        }
        // *Async Completion Support Check
        if (this.manager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            this.manager.registerAsynchronousCompletions();
        }
        // Confirmation manager
        this.confirmationManager = new CommandConfirmationManager<>(
                /* Timeout */ 30L,
                /* Timeout unit */ TimeUnit.SECONDS,
                /* Action when confirmation is required */ context -> context.getCommandContext().getSender().sendMessage(
                ChatColor.RED + "Confirmation required. Confirm using /example confirm."),
                /* Action when no confirmation is pending */ sender -> sender.sendMessage(
                ChatColor.RED + "You don't have any pending commands.")
        );
        this.confirmationManager.registerConfirmationProcessor(this.manager);
        // Annotation parser Start
        final Function<ParserParameters, CommandMeta> commandMetaFunction = p ->
                CommandMeta.simple()
                        // This will allow you to decorate commands with descriptions
                        .with(CommandMeta.DESCRIPTION, p.get(StandardParameters.DESCRIPTION, "No description"))
                        .build();
        this.annotationParser = new cloud.commandframework.annotations.AnnotationParser<>(
                /* Manager */ this.manager,
                /* Command sender type */ CommandSender.class,
                /* Mapper for command meta instances */ commandMetaFunction
        );
        new MinecraftExceptionHandler<CommandSender>()
                .withInvalidSyntaxHandler()
                .withInvalidSenderHandler()
                .withNoPermissionHandler()
                .withArgumentParsingHandler()
                .withCommandExecutionHandler()
                .withDecorator(
                        component -> text()
                                .append(text("[", NamedTextColor.DARK_GRAY))
                                .append(text("Bakugo", NamedTextColor.BLUE))
                                .append(text("] ", NamedTextColor.DARK_GRAY))
                                .append(component).build()
                ).apply(this.manager, this.bukkitAudiences::sender);
        // Annotation Parser End
        this.registerCommands();
        Bukkit.getLogger().info("CommandManager successfully initialized.");
    }

    private void registerCommands() {
        this.annotationParser.registerBuilderModifier(
                GamemodeCheck.class,
                (requirement, builder) -> builder.permission(
                        PredicatePermission.of(SimpleCloudKey.of("gamemode"), sender ->
                                !(sender instanceof Player) || ((Player) sender).getGameMode() == requirement.value()
                        )
                )
        );
        this.annotationParser.parse(EchoCommand.class);
    }

}
