/*
 * MIT License
 *
 * Copyright (c) 2017-2021 RiiConnect24 and its contributors
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

package xyz.rc24.bot.config;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.rc24.bot.Const;
import xyz.rc24.bot.config.bot.BotConfig;

import javax.security.auth.login.LoginException;

@Configuration
public class DiscordConfig
{
    @Bean
    public CommandClient commandClient(BotConfig config)
    {
        CommandClientBuilder client = new CommandClientBuilder()
                .setActivity(config.getJDAConfig().getActivity())
                .setStatus(config.getJDAConfig().getOnlineStatus())
                .setEmojis(Const.SUCCESS_E, Const.WARN_E, Const.ERROR_E)
                .setLinkedCacheSize(40)
                .setOwnerId(config.getJDAConfig().getOwner())
                .setCoOwnerIds(config.getJDAConfig().getCoOwners())
                .setPrefix("@mention")
                .setServerInvite("https://discord.gg/5rw6Tur")
                /*.setGuildSettingsManager(getGuildSettingsDataManager())
                .setScheduleExecutor(botScheduler)*/
                /*.addCommands(
                        // Bot administration
                        new Bash(), new Eval(this), new Shutdown(),

                        // General
                        new BirthdayCmd(this), new FlagCmd(this), new InviteCmd(),
                        new PingCmd(), new RiiTagCmd(this), new SetBirthdayCmd(this),

                        // Tools
                        new DefaultAddCmd(this), new PrefixCmd(getGuildSettingsDataManager()),
                        new StatsCmd(this),

                        // Wii-related
                        new AddCmd(this), new CodeCmd(this), new BlocksCmd(),
                        new ErrorInfoCmd(this), new DNS(), new WadsCmd(), new WiiWare())*/;

        /*if(!(dataDogStatsListener == null))
            client.setListener(dataDogStatsListener);*/

        return client.build();
    }

    @Bean
    public JDA jda(CommandClient client, BotConfig config) throws LoginException
    {
        JDABuilder builder = JDABuilder.createLight(config.getJDAConfig().getToken())
                .setEnabledIntents(Const.INTENTS)
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.playing("loading..."))
                .addEventListeners(client);

        /*if(!(dataDogStatsListener == null))
            builder.addEventListeners(dataDogStatsListener);*/

        return builder.build();
    }
}