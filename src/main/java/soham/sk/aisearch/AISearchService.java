package soham.sk.aisearch;

import com.azure.core.util.Context;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.models.SearchOptions;
import com.azure.search.documents.util.SearchPagedIterable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AISearchService {

    Logger log = LoggerFactory.getLogger(AISearchService.class);

    private final SearchClient aiSearchClient;
    private final ObjectMapper objectMapper;

    public AISearchService(SearchClient aiSearchClient, ObjectMapper objectMapper) {
        this.aiSearchClient = aiSearchClient;
        this.objectMapper = objectMapper;
    }

    @DefineKernelFunction(
            name = "searchForResumes",
            description = "Search resumes for a given query",
            returnType = "string")
    public String searchForResumes(
            @KernelFunctionParameter(description = "resume list for the query", name = "query")
            String query) throws IOException {

        log.debug("Resume search on {}", query);

        SearchOptions options = new SearchOptions()
                .setIncludeTotalCount(true).setTop(3).setSelect("content", "title");

        SearchPagedIterable searchResults = aiSearchClient.search(query, options, Context.NONE);
        List<Resumes> resumesList = searchResults.stream()
                .map(resume -> resume.getDocument(Resumes.class)).collect(Collectors.toList());

        return this.objectMapper.writeValueAsString(resumesList);
    }
}
