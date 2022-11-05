package gg.mineral.server.util.messages;

import dev.zerite.craftlib.chat.component.BaseChatComponent;
import dev.zerite.craftlib.chat.component.StringChatComponent;
import dev.zerite.craftlib.chat.type.ChatColor;

public class Messages {
        public static final BaseChatComponent DISCONNECT_UNKNOWN = new StringChatComponent(
                        ChatColor.RED + "You have been disconnected from the server.");
        public static final BaseChatComponent DISCONNECT_CAN_NOT_AUTHENTICATE = new StringChatComponent(
                        ChatColor.RED + "Your account was unable to be authenticated.");
}
