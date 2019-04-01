/*
 * MIT License
 *
 * Copyright (c) 2017-2019 RiiConnect24 and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package xyz.rc24.bot.commands.tools;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.Config;

/**
 * @author Artuto
 */

public class MailPatchCmd extends Command
{
    private final Config config;

    public MailPatchCmd(Config config)
    {
        this.config = config;
        this.name = "patch";
        this.help = "Patches your `nwc24msg.cfg` for use with RiiConnect24.";
        this.category = Categories.TOOLS;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = false;
    }

    @Override
    public void execute(CommandEvent event)
    {
        if(config.isMailPatchEnabled())
        {
            event.replyInDm("Drag and drop your `nwc24msg.cfg` here, and I'll patch it!",
                    (success) -> event.reactSuccess(),
                    (failure) -> event.replyError("Hey, " + event.getAuthor().getAsMention() +
                            ": I couldn't DM you. Make sure your DMs are enabled."));
        }
        else
            event.replyError("The `patch` command has been disabled by the bot owner!");
    }
}