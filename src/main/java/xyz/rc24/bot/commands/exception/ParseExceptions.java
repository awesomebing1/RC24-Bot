package xyz.rc24.bot.commands.exception;

import java.util.Arrays;
import java.util.Collection;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

public interface ParseExceptions {
	public Dynamic2CommandExceptionType NOT_VALID = new Dynamic2CommandExceptionType((type, value) -> new LiteralMessage(type + "`" + value + "` is not valid for this command."));
	public Dynamic3CommandExceptionType NOT_VALID_BECAUSE = new Dynamic3CommandExceptionType((type, value, reason) -> new LiteralMessage(type + "`" + value + "` is not valid for this command because " + reason));
	public Dynamic2CommandExceptionType NONEXISTANT = new Dynamic2CommandExceptionType((type, value) -> new LiteralMessage(type + " `" + value + "` does not exist or could not be found."));
	
	public DynamicCommandExceptionType INVALID_CHOICE = new DynamicCommandExceptionType((value) -> new LiteralMessage("Invalid choice, must choose from one of the following: `" + Arrays.toString(((Collection<?>)value).toArray()) + "`"));
}