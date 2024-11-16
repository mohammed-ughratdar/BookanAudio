package com.bookanaudio.s3.util;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class Prompts {

    public Prompts() {
        promptMap.put("chapterPrompt", page_prompt);
    }

    private final String page_prompt = "###Background: For audios associated to a book it is cumbersome to manually"
            + "assign audios to each page of the book. We are trying to make this process automated by finding the chapter of"
            + "a page and the naming scheme of the audio file which contains the chapter number. "
            + "###Task: Your task is to simply find the chapter number from the text provided which can then be added to the page entity. "
            + "Every book will be divided in chapters and chapter numbers are usually provided in the headers or the footers of a page. "
            + "It can also be that the books be in multiple languages and therefore Chapter is a different word in different languages. "
            + "###Examples: 'Chapter 5' Here 5 is the chapter. 'Lektion 2' Here the word is in german and means Chapter 2. "
            + "The text provided is from a page of a book, and it can be that the page is from the index, "
            + "front page, or last page, in which case it may not have a chapter number. There can also be empty pages. In such cases do not return a chapterNumber "
            + "but instead an empty value of 'chapterNumber' key. Example: {'chapterNumber': ''}"
            + "###Output: Return a JSON as an example {'chapterNumber': '100'} where 100 is a chapter number in string and if you cannot find chapter number then return "
            + "a JSON in such format {'chapterNumber': ''} "
            + "###Example Outputs: {'chapterNumber':'4'}, {'chapterNumber':'10'}, {'chapterNumber':''}, {'chapterNumber':'20'}"
            + "###Remember: Every response should be in JSON format with braces otherwise code will break."
            + "The page content begins from here: {{pageText}}";

    private final Map<String, String> promptMap = new HashMap<>();

    public String getPrompt(String promptKey) {
        return promptMap.get(promptKey); 
    }
}
