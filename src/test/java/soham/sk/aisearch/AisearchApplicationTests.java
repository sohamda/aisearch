package soham.sk.aisearch;

import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
import com.microsoft.semantickernel.services.ServiceNotFoundException;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AisearchApplicationTests {

	@Autowired
	Kernel kernel;

	@Test
	void contextLoads() {
	}

	@Test
	void validateResumeSearch() throws ServiceNotFoundException {

		ChatCompletionService chatCompletionService = this.kernel.getService(ChatCompletionService.class);
		InvocationContext invocationContext = InvocationContext.builder()
				.withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
				.build();
		ChatHistory chatHistory = new ChatHistory("You are a resume search assistant who helps to find the matching resumes or cv to a requirement");
		chatHistory.addUserMessage("please find me some resumes who has experience in SQL Server");
		ChatMessageContent<?> result = chatCompletionService
				.getChatMessageContentsAsync(chatHistory, kernel, invocationContext).block().get(0);

		System.out.println("Result > " + result.getContent());
	}

}
