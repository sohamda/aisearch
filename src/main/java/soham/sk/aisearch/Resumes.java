package soham.sk.aisearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Resumes {

    @JsonProperty("content")
    public String content;
    @JsonProperty("title")
    public String title;
}
