import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PrometheusMemorySuggestion {

    public static void main(String[] args) {
        String endpointUrl = "http://localhost:8080/actuator/prometheus"; // Altere para o URL do seu endpoint

        try {
            URL url = new URL(endpointUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                long totalMemoryUsed = 0;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("jvm_memory_used_bytes")) {
                        String[] parts = line.split(" ");
                        if (parts.length >= 2) {
                            String value = parts[1];
                            totalMemoryUsed += Long.parseLong(value);
                        }
                    }
                }

                reader.close();

                if (totalMemoryUsed > 0) {
                    suggestJvmMemory(totalMemoryUsed);
                } else {
                    System.out.println("Não foi possível obter informações sobre o consumo de memória.");
                }
            } else {
                System.out.println("Falha na requisição. Código de resposta: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void suggestJvmMemory(long memoryUsed) {
        long xms = memoryUsed + (memoryUsed / 2); // Xms: Consumo atual + metade do consumo
        long xmx = memoryUsed * 2; // Xmx: Consumo atual * 2

        System.out.println("Sugestão de valores para -Xms: " + formatMemorySize(xms));
        System.out.println("Sugestão de valores para -Xmx: " + formatMemorySize(xmx));
    }

    private static String formatMemorySize(long bytes) {
        long kilo = 1024;
        long mega = kilo * kilo;
        long giga = mega * kilo;

        if (bytes >= giga) {
            return String.format("%.2f GB", (double) bytes / giga);
        } else if (bytes >= mega) {
            return String.format("%.2f MB", (double) bytes / mega);
        } else if (bytes >= kilo) {
            return String.format("%.2f KB", (double) bytes / kilo);
        } else {
            return bytes + " bytes";
        }
    }
}
