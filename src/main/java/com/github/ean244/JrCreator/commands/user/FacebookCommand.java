package com.github.ean244.JrCreator.commands.user;

import com.github.ean244.JrCreator.commands.CommandMeta;
import com.github.ean244.JrCreator.commands.Commands;
import com.github.ean244.JrCreator.perms.PermissionLevel;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

@CommandMeta(aliases = { "fb" }, name = "facebook", permission = PermissionLevel.USER)
public class FacebookCommand implements Commands {

	@Override
	public boolean onExecute(TextChannel channel, Guild guild, Member member, String[] args) {
		if (args.length != 0)
			return false;
		
		channel.sendMessage("https://www.facebook.com/icreatorz.pg").queue();
		return true;
	}

}
