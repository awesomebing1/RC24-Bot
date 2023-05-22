package xyz.rc24.bot.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import xyz.rc24.bot.commands.CommandContext;
import xyz.rc24.bot.commands.Commands;
import xyz.rc24.bot.commands.argument.GlobalNode;

public class GlobalEventReceiver extends ListenerAdapter {

	private static final boolean debug = true;
	
	private final Sub sub = new Sub();
	
	public GlobalEventReceiver(){}
	
	@Override
	public void onGenericEvent(GenericEvent ge) {
		
		//we can do some logic here if necessary before the event is processed if necessary (ex: logging)
		sub.onEvent(ge);
	}
	
	private static final class Sub extends ListenerAdapter {
		@Override
		public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
			StringBuilder c = new StringBuilder(e.getName());
			for(OptionMapping arg : e.getOptions()) {
				c.append(' ');
				c.append(arg.getAsString().trim());
			}
			CommandContext context = new CommandContext(e);
			try {
				Commands.DISPATCHER.getSlashDispatcher().execute(c.toString() , context);
			} catch (Throwable t) {
				if(t.getMessage() != null && !t.getMessage().isBlank()) {
					context.sendThrowable(t);
				}
				else {
					context.sendThrowable(t);
				}
				if(!(t instanceof CommandSyntaxException)) {
					throw new RuntimeException(t);
				}
			}
		}
		
		/* not used for this bot yet
		
		@Override
		public void onButtonInteraction(ButtonInteractionEvent e) {
			try {
				Interactions.DISPATCHER.execute(e);
			} catch (CommandSyntaxException e1) {
				e1.printStackTrace();
			}
		}
		
		@Override
		public void onModalInteraction(ModalInteractionEvent e) {
			try {
				Interactions.execute(e);
			} catch (CommandSyntaxException e1) {
				e1.printStackTrace();
			}
		}
		
		@Override
		public void onStringSelectInteraction(StringSelectInteractionEvent e) {
			try {
				Interactions.execute(e);
			} catch (CommandSyntaxException e1) {
				e1.printStackTrace();
			}
		}
		
		*/
		
		@Override
		public void onGuildReady(GuildReadyEvent e) { //for development
			List<CommandData> commands = new ArrayList<>();
			Commands.DISPATCHER.getSlashDispatcher().getRoot().getChildren().forEach((command) -> {
				if(!debug) {
					if(command instanceof GlobalNode) {
						return; //Don't register global commands as guild commands if we're not in a dev environment
					}
				}
				SlashCommandData data = net.dv8tion.jda.api.interactions.commands.build.Commands.slash(command.getName(), command.getUsageText());
				
				if(command.getChildren().size() > 0) {
					if(command.getChildren().size() == 1) {
						
					}
					else {
						data.addOption(OptionType.STRING, "arguments", "arguments", true, true);
					}
				}
				
				commands.add(data);
			});
			
			e.getGuild().updateCommands().addCommands(commands).queue();
		}
		
		@Override
		public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent e) {
			
			String command = e.getName() + " " + e.getFocusedOption().getValue();
			final String arguments = e.getFocusedOption().getValue();
			String fixedArguments = e.getFocusedOption().getValue();
			
			boolean spaceAdded = false;
			
			if(fixedArguments.indexOf(' ') != -1) {
				fixedArguments = fixedArguments.substring(0, fixedArguments.lastIndexOf(' '));
			}
			else {
				fixedArguments = "";
			}
			ParseResults<CommandContext> parseResults = Commands.DISPATCHER.getSlashDispatcher().parse(command, new CommandContext<CommandAutoCompleteInteractionEvent>(e));
			List<Suggestion> suggestions;
			List<String> returnedSuggestions = new ArrayList<String>();
			try {
				suggestions = Commands.DISPATCHER.getSlashDispatcher().getCompletionSuggestions(parseResults, command.length()).get().getList();
			} catch (InterruptedException | ExecutionException ex) {
				ex.printStackTrace();
				return;
			}
			if(suggestions.size() > 25) {
				suggestions = suggestions.subList(0, 25);
			}
			System.out.println("Arguments:" + arguments);
			for(Suggestion suggestion : suggestions) {
				if(!spaceAdded) {
					returnedSuggestions.add(fixedArguments + " " + suggestion.getText());
				}
				else {
					returnedSuggestions.add(fixedArguments + arguments + " " + suggestion.getText());
				}
			}
			e.replyChoiceStrings(returnedSuggestions).queue();
		}
	}
	
}