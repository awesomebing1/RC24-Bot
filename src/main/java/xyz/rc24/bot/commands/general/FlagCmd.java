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

package xyz.rc24.bot.commands.general;

import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.commands.Commands;
import xyz.rc24.bot.commands.Dispatcher;
import xyz.rc24.bot.commands.RiiContext;
import xyz.rc24.bot.commands.argument.FlagArgumentType;
import xyz.rc24.bot.core.entities.Flag;

public class FlagCmd 
{

    public static void register(Dispatcher dispatcher) {
    	dispatcher.register(Commands.base("flag", "Sets the flag in your code lookup", null).requires((context) -> context.isDiscordContext(), RiiContext.requiresDiscordContext)
    		.then(Commands.argument("flag", FlagArgumentType.COUNTRIES)
    			.executes((context) -> {
    				execute(context.getSource(), context.getArgument("flag", Flag.class));
    				return 1;
    			})
    		)
    	);
    }

    private static void execute(RiiContext context, Flag flag) {
    	if(!context.isDiscordContext()) {
    		context.replyDiscordOnlyCommand();
    	}
        if(flag == Flag.UNKNOWN)
        {
            context.queueMessage("Unknown country!", true, false);
            return;
        }

        boolean success = RiiConnect24Bot.getInstance().getCodeDataManager().setFlag(context.getUser().getIdLong(), flag.getEmote());

        if(success) {
            context.queueMessage("Updated successfully!", true, false);
        }
        else {
            context.queueMessage("Error updating your flag! Please contact a developer.", true, false);
        }
    }
}
