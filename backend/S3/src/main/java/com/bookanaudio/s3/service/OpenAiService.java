package com.bookanaudio.s3.service;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.bookanaudio.s3.util.Prompts;

import java.io.IOException;

@Service
public class OpenAiService {

    private final String openaiUrl;
    private final String openaiKey;
    private final OkHttpClient client;
    private final Prompts prompts;

    @Autowired
    public OpenAiService(
            @Value("${openai_url}") String openaiUrl,
            @Value("${openai_key}") String openaiKey,
            OkHttpClient client,
            Prompts prompts
    ) {
        this.openaiUrl = openaiUrl;
        this.openaiKey = openaiKey;
        this.client = client;
        this.prompts = prompts;
    }

    public String getChapterNumber(String pageText) {
        try {
            String promptTemplate = prompts.getPrompt("chapterPrompt");
            String prompt = promptTemplate.replace("{{pageText}}", pageText);
            System.out.println("Generated Prompt: " + prompt);  

            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "system").put("content", "You are a helpful assistant."));
            messages.put(new JSONObject().put("role", "user").put("content", prompt));

            JSONObject json = new JSONObject();
            json.put("model", "gpt-3.5-turbo");
            json.put("messages", messages);
            json.put("max_tokens", 50);
            json.put("temperature", 0.2);
            System.out.println("Request JSON: " + json.toString()); 

            RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(openaiUrl)
                    .header("Authorization", "Bearer " + openaiKey)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    System.out.println("Raw Response from OpenAI: " + responseBody); 

                    JSONObject responseObject = new JSONObject(responseBody);
                    String jsonResponse = responseObject.getJSONArray("choices")
                            .getJSONObject(0).getJSONObject("message")
                            .getString("content").trim();
                    System.out.println("Parsed JSON Response: " + jsonResponse); 

                    JSONObject pageResponse = new JSONObject(jsonResponse);
                    String chapterNumber = pageResponse.getString("chapterNumber");
                    System.out.println("Chapter Number: " + chapterNumber); 
                    return chapterNumber;
                } else {
                    System.err.println("Unexpected response from OpenAI: " + (response.body() != null ? response.body().string() : "No response body"));
                    return "";
                }
            }
        } catch (Exception e) {
            System.err.println("Error in getChapterNumber: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

}
