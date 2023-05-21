/*
 * MIT License
 *
 * Copyright (c) 2017-2020 RiiConnect24 and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.rc24.bot.commands.wii;

import com.mojang.brigadier.CommandDispatcher;

import net.dv8tion.jda.api.entities.User;

import xyz.rc24.bot.commands.CommandContext;
import xyz.rc24.bot.commands.Commands;
import xyz.rc24.bot.commands.argument.DiscordUserArgumentType;
import xyz.rc24.bot.core.BotCore;
import xyz.rc24.bot.core.entities.CodeType;
import xyz.rc24.bot.core.entities.GuildSettings;
import xyz.rc24.bot.user.ConsoleUser;
import xyz.rc24.bot.utils.FormatUtil;

import java.util.Map;

/**
 * @author Artuto, Gamebuster
 */

public class AddCmd {
    
    public static void register(CommandDispatcher<CommandContext> dispatcher) {
    	dispatcher.register(Commands.global("add")
    		.then(Commands.argument("friend", new DiscordUserArgumentType())
    			.executes((context) -> {
    				execute(context.getSource(), context.getArgument("friend", User.class));
    				return 1;
    			})	
    		)
    	);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private static void execute(CommandContext context, User friend) {

    		if(!context.isDiscordContext()) {
    			context.replyDiscordOnlyCommand();
    			return;
    		}
    	
    		BotCore core = context.getBot().getCore();
    		CodeType codeType = CodeType.WII;
    		
    		if(context.isGuildContext()) {
    			GuildSettings gs = context.getBot().getGuildSettingsDataManager().getSettings(context.getServer());
    			codeType = gs.getDefaultAddType();
    		}

            if(friend instanceof ConsoleUser || friend.isSystem()) { 
            	context.queueMessage("You cannot add a system user!", true, false);
            	return;
            }
            if(friend.equals(context.getAuthor())) {
                context.queueMessage("You can't add yourself!", true, false);
                return;
            }
            if(friend.isBot()) {
                context.queueMessage("You can't add bots!", true, false);
                return;
            }

            Map<String, String> authorTypeCodes = core.getCodesForType(codeType, context.getAuthor().getIdLong());
            if(authorTypeCodes.isEmpty())
            {
                context.queueMessage("**" + context.getEffectiveName() + "** has not added any friend codes!", true, false);
                return;
            }

            Map<String, String> targetTypeCodes = core.getCodesForType(codeType, friend.getIdLong());
            if(targetTypeCodes.isEmpty())
            {
            	context.queueMessage("**" + context.getEffectiveNameOf(friend) + "** has not added any friend codes!", true, false);
                return;
            }

            
            CommandContext privateContext = context.getPrivateContext();
            CommandContext friendPrivateContext = new CommandContext(friend);
            
            // Send target's code to author
            privateContext.queueMessage(getAddMessageHeader(codeType, context,
                    false) + "\n\n" + FormatUtil.getCodeLayout(authorTypeCodes),
                    false, false, (failure) -> context.getChannel().sendMessage("Hey, " + context.getAuthor().getAsMention() +
                    ": I couldn't DM you. Make sure your DMs are enabled."));

            // Send author's code to target
            friendPrivateContext.queueMessage(getAddMessageHeader(codeType, context,
                        false) + "\n\n" + FormatUtil.getCodeLayout(authorTypeCodes),
                        false, false, (failure) -> context.getChannel().sendMessage("Hey, " + friend.getAsMention() +
                        ": I couldn't DM you. Make sure your DMs are enabled."));
    }

    @SuppressWarnings("rawtypes")
	private static String getAddMessageHeader(CodeType type, CommandContext context, boolean isCommandRunner) {
        if(!(isCommandRunner))
        	//use tag because the recipient may not share the same server the command was executed on, causing it to appear like unknown user was requesting
            return "**" + context.getAuthor().getAsTag() + "** has requested to add your " + type.getDisplayName() + " friend code(s)!";
        else
            return "You have requested to add **" + context.getEffectiveName()+ "**'s " + type.getDisplayName() + " friend code(s).";
    }
}
