package soham.sk.aisearch;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.credential.KeyCredential;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchClientBuilder;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.plugin.KernelPlugin;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.nio.file.Path;

@Configuration
public class ConfigBeans {

    @Value("${sk.azureopenai.key}") String apiKey;
    @Value("${sk.azureopenai.endpoint}") String endpoint;
    @Value("${sk.azureopenai.deploymentname}") String deploymentName;
    @Value("${azure.ai.search.key}") String aiSearchApiKey;
    @Value("${azure.ai.search.endpoint}") String aiSearchEndpoint;
    @Value("${azure.ai.search.index.name}") String aiSearchIndexName;


    @Bean
    public SearchClient aiSearchClient() {
        return new SearchClientBuilder()
                .endpoint(aiSearchEndpoint)
                .credential(new AzureKeyCredential(aiSearchApiKey))
                .indexName(aiSearchIndexName)
                .buildClient();
    }

    private OpenAIAsyncClient openAIAsyncClient() {
        if(StringUtils.hasLength(endpoint)) {
            return new OpenAIClientBuilder()
                    .endpoint(endpoint)
                    .credential(new AzureKeyCredential(apiKey))
                    .buildAsyncClient();
        }
        return new OpenAIClientBuilder()
                .credential(new KeyCredential(apiKey))
                .buildAsyncClient();
    }

    private ChatCompletionService chatCompletionService() {
        return OpenAIChatCompletion.builder()
                .withOpenAIAsyncClient(openAIAsyncClient())
                .withModelId(deploymentName)
                .build();
    }

    @Bean
    public Kernel kernel(AISearchService aiSearchService) {
        KernelPlugin kernelPlugin = KernelPluginFactory.createFromObject(
                aiSearchService,
                "ResumeFinder");

        KernelPlugin resumeSummarizer = KernelPluginFactory
                .importPluginFromDirectory(Path.of("src/main/resources/plugins"),
                        "Resume",null);

        return Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService())
                .withPlugin(kernelPlugin)
                .withPlugin(resumeSummarizer)
                .build();
    }
}
