package com.github.ean244.jrcreator.main;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ean244.jrcreator.commands.CommandRegistry;
import com.github.ean244.jrcreator.commands.admin.ListCommand;
import com.github.ean244.jrcreator.commands.admin.SetPrefixCommand;
import com.github.ean244.jrcreator.commands.dj.BackwardCommand;
import com.github.ean244.jrcreator.commands.dj.ClearPlayListCommand;
import com.github.ean244.jrcreator.commands.dj.ForwardCommand;
import com.github.ean244.jrcreator.commands.dj.JoinCommand;
import com.github.ean244.jrcreator.commands.dj.LeaveCommand;
import com.github.ean244.jrcreator.commands.dj.PauseCommand;
import com.github.ean244.jrcreator.commands.dj.PlayCommand;
import com.github.ean244.jrcreator.commands.dj.PlaylistCommand;
import com.github.ean244.jrcreator.commands.dj.RepeatCommand;
import com.github.ean244.jrcreator.commands.dj.ResumeCommand;
import com.github.ean244.jrcreator.commands.dj.RewindCommand;
import com.github.ean244.jrcreator.commands.dj.SkipCommand;
import com.github.ean244.jrcreator.commands.dj.StopCommand;
import com.github.ean244.jrcreator.commands.dj.YoutubeCommand;
import com.github.ean244.jrcreator.commands.user.AnnouncementCommand;
import com.github.ean244.jrcreator.commands.user.DocumentationCommand;
import com.github.ean244.jrcreator.commands.user.FacebookCommand;
import com.github.ean244.jrcreator.commands.user.GithubCommand;
import com.github.ean244.jrcreator.commands.user.GitlabCommand;
import com.github.ean244.jrcreator.commands.user.HelpCommand;
import com.github.ean244.jrcreator.config.AnnouncementHandler;
import com.github.ean244.jrcreator.db.DbHandler;
import com.github.ean244.jrcreator.dialogflow.AIManager;
import com.github.ean244.jrcreator.listener.CommandListener;
import com.github.ean244.jrcreator.listener.ConversationListener;
import com.github.ean244.jrcreator.listener.VoiceLeaveListener;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

public class JrCreator {
	private static final Logger LOGGER = LoggerFactory.getLogger(JrCreator.class);
	private static JDA jda;

	private JrCreator() {
	}

	public static void main(String[] args) throws LoginException {
		if (args.length != 2) {
			LOGGER.error("Failed to setup: Tokens required!");
			return;
		}

		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExeptionHandler());

		jda = new JDABuilder(AccountType.BOT).setToken(args[0]).buildAsync();

		AIManager.CLIENT_TOKEN = args[1];
		
		onEnable();

		startTerminalTask();
	}

	private static void startTerminalTask() {

		try (Terminal terminal = TerminalBuilder.terminal()) {
			Thread thread = new Thread(() -> {
				LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();

				while (true) {
					String input = reader.readLine("> ");

					if ("stop".equalsIgnoreCase(input)) {
						JrCreator.onDisable();
						return;

					} else if ("reload".equalsIgnoreCase(input)) {
						terminal.writer().println("Reloading application...");
						JrCreator.onDisable();
						JrCreator.onEnable();
						terminal.writer().println("Reloaded successfully!");

					} else if ("?".equalsIgnoreCase(input)) {
						terminal.writer().println("Commands:\nreload - reloads the program\nstop - stops the program");

					} else if ("".equalsIgnoreCase(input)) {
						continue;

					} else {
						terminal.writer().println("Unknown command! Do ? for a list of commands");
					}
				}
			});

			thread.start();

		} catch (IOException e) {
			LOGGER.error("Failed to setup terminal", e);
		}
	}

	private static void onEnable() {
		// connect to database
		DbHandler.getInstance().connDb();

		AnnouncementHandler.getInstance().readContent();

		LOGGER.info("Initializing listeners...");

		addListener();

		LOGGER.info("Listeners successfully initialized!");

		LOGGER.info("Registering commands...");

		registerCommands();
		
		LOGGER.info("Commands successfully Registered!");
	}

	private static void onDisable() {
		DbHandler.getInstance().closeConn();

		LOGGER.info("Shutting down listener thread pool...");

		CommandListener.THREAD_POOL.shutdown();

		LOGGER.info("Thread pool has shut down!");

		LOGGER.info("Shutting down JDA...");

		jda.shutdown();

		LOGGER.info("JDA has shut down!");
	}

	private static void addListener() {
		jda.addEventListener(new CommandListener());
		jda.addEventListener(new VoiceLeaveListener());
		jda.addEventListener(new ConversationListener());
	}

	private static void registerCommands() {
		CommandRegistry registry = CommandRegistry.getInstance();

		registry.register(new ListCommand());
		registry.register(new SetPrefixCommand());
		registry.register(new AnnouncementCommand());
		registry.register(new GithubCommand());
		registry.register(new YoutubeCommand());
		registry.register(new LeaveCommand());
		registry.register(new JoinCommand());
		registry.register(new PlayCommand());
		registry.register(new PlaylistCommand());
		registry.register(new StopCommand());
		registry.register(new SkipCommand());
		registry.register(new ClearPlayListCommand());
		registry.register(new PauseCommand());
		registry.register(new ResumeCommand());
		registry.register(new GitlabCommand());
		registry.register(new FacebookCommand());
		registry.register(new RewindCommand());
		registry.register(new RepeatCommand());
		registry.register(new ForwardCommand());
		registry.register(new BackwardCommand());
		registry.register(new HelpCommand());
		registry.register(new DocumentationCommand());
	}

	public static JDA getJda() {
		return jda;
	}
}
