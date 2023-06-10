import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class APIDocumentationGenerator {
    public static void main(String[] args) {
        String json = "{\"id\": \"11111111\",\"request\": {\"url\": \"/api/test\",\"method\": \"GET\",\"bodyPatterns\": [{\"equalTojson\": \"{}\"}]},\"response\": {\"status\": 200,\"body\": \"{ \\\"test\\\": \\\"data\\\" }\",\"headers\": {\"Content-Type\": \"application/json\"}}}";

        Gson gson = new Gson();
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        String id = jsonObject.get("id").getAsString();
        JsonObject request = jsonObject.getAsJsonObject("request");
        String url = request.get("url").getAsString();
        String method = request.get("method").getAsString();
        String bodyPattern = request.getAsJsonArray("bodyPatterns").get(0).getAsJsonObject().get("equalTojson").getAsString();
        JsonObject response = jsonObject.getAsJsonObject("response");
        int statusCode = response.get("status").getAsInt();
        String responseBody = response.get("body").getAsString();
        String contentType = response.getAsJsonObject("headers").get("Content-Type").getAsString();

        // Generate Markdown documentation
        StringBuilder markdown = new StringBuilder();
        markdown.append("# API Documentation\n\n");
        markdown.append("## Request\n\n");
        markdown.append("- URL: `").append(url).append("`\n");
        markdown.append("- Method: `").append(method).append("`\n");
        markdown.append("- Body Pattern: `").append(bodyPattern).append("`\n\n");
        markdown.append("## Response\n\n");
        markdown.append("- Status Code: `").append(statusCode).append("`\n");
        markdown.append("- Body:\n```json\n").append(responseBody).append("\n```\n");
        markdown.append("- Headers:\n  - Content-Type: `").append(contentType).append("`\n");

        System.out.println(markdown.toString());
    }
}
