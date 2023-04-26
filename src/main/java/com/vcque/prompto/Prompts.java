package com.vcque.prompto;

import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.vcque.prompto.contexts.PromptoContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * regroups chat messages. Might want to put that in settings in the future.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Prompts {

    /**
     * Conversation initializer.
     */
    public static ChatMessage codingAssistant() {
        return new ChatMessage(
                ChatMessageRole.SYSTEM.value(),
                """
                        You are Prompto, an intelliJ coding assistant helping the user completing its programming tasks.
                        """.trim()
        );
    }

    public static ChatMessage rewriteMethodOutput() {
        return new ChatMessage(
                ChatMessageRole.SYSTEM.value(),
                """
                        Your task is to rewrite the context method based on the user's next input.
                        The first code block you provide will replace the context method in the user's editor.
                        """.trim()
        );
    }

    public static ChatMessage shortAnswerOutput() {
        return new ChatMessage("system",
                """
                        Your task is to answer the user's next question based on the provided context.
                        Answer with only the most relevant information and with the least words possible.
                        """
        );
    }

    public static ChatMessage addToExistingOutput() {
        return new ChatMessage(ChatMessageRole.SYSTEM.value(),
                """
                        Your task is to generate content based on the user's next input.
                        Respond with a code snippet that will be inserted in the current editor.
                        """
        );
    }

    public static ChatMessage answermeOutput() {
        return new ChatMessage(
                ChatMessageRole.USER.value(),
                """
                        I will instruct you a task about the provided file.
                        Do answer truthfully. If you don't know how to do the task, say so and provide the reasons why.
                        """
        );
    }

    public static ChatMessage userInput(String userInput) {
        return new ChatMessage(ChatMessageRole.USER.value(), userInput);
    }

    public static ChatMessage promptoContext(PromptoContext state) {
        return new ChatMessage(
                ChatMessageRole.SYSTEM.value(),
                """
                        `%s-%s`: This is %s
                        ```
                        %s
                        ```
                        """.formatted(
                        state.getType().name(),
                        state.getId(),
                        state.getType().description,
                        state.getValue()
                )
        );
    }

    public static ChatMessage promptoContextFormat() {
        var exampleState = PromptoContext.builder()
                .id("state_id")
                .type(PromptoContext.Type.EXAMPLE)
                .value("$state_value")
                .build();
        var exampleFormat = promptoContext(exampleState).getContent();

        return new ChatMessage(
                ChatMessageRole.SYSTEM.value(),
                """
                        In the next messages, you will receive context information useful to your task. It will have the following format:
                        %s
                        """.formatted(exampleFormat)
        );
    }
}
