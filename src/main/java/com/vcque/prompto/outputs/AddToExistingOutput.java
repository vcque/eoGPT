package com.vcque.prompto.outputs;

import com.theokanning.openai.completion.chat.ChatMessage;
import com.vcque.prompto.Prompts;
import com.vcque.prompto.Utils;

public class AddToExistingOutput implements PromptoOutput<String> {

    @Override
    public ChatMessage chatMessage() {
        return Prompts.addToExistingOutput();
    }

    @Override
    public String extractOutput(String assistantMessage) {
        var editorContents = Utils.extractEditorContent(assistantMessage);
        return editorContents.isEmpty() ? assistantMessage.trim() : editorContents.get(0).trim();
    }

}
