package com.vcque.prompto.pipelines;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.vcque.prompto.contexts.PromptoContext;
import com.vcque.prompto.outputs.PromptoOutput;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Represents a Prompto pipeline which defines the steps needed to query ChatGPT and produces the desired output.
 * There is:
 * - Which context information are used for the query
 * - The format of the desired output of ChatGPT
 * - The effects to apply to the initial scope once the results have been retrieved
 *
 * @param <T> the output type of the pipeline
 */
@Data
@Builder
public class PromptoPipeline<T> {
    private String name;
    private String defaultInput;
    private List<PromptoRetrieverDefinition> retrievers;
    private PromptoOutput<T> output;
    private BiConsumer<T, Scope> execution;

    /**
     * Represents the scope of a pipeline execution, containing project, editor, and PSI element information.
     */
    public record Scope(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
    }

    /**
     * Determines if the pipeline is available based on the provided scope.
     * A pipeline is considered available if all non-optional contexts are available given the scope.
     *
     * @param scope the scope containing the IntelliJ project, editor, and PSI element
     * @return true if the pipeline is available, false otherwise
     */
    public boolean isAvailable(Scope scope) {
        if (retrievers == null) {
            return true;
        }
        return retrievers.stream().allMatch(c -> c.optional || c.getRetriever().isAvailable(scope.project(), scope.editor(), scope.element()));
    }

    /**
     * Generates a list of ChatMessage instances based on the available contexts for the given scope.
     * The method filters the available contexts, maps each context to its corresponding ChatMessage, and returns the list of messages.
     *
     * @param scope the scope containing the IntelliJ project, editor, and PSI element
     * @return a list of ChatMessage instances
     */
    public List<PromptoContext> retrieveContexts(Scope scope) {
        if (retrievers == null) {
            return List.of();
        }
        var project = scope.project();
        var editor = scope.editor();
        var element = scope.element();
        return retrievers.stream().filter(c -> c.getRetriever().isAvailable(project, editor, element))
                .flatMap(c -> c.getRetriever().retrieveContexts(project, editor, element).stream())
                .distinct()
                .toList();
    }

}

