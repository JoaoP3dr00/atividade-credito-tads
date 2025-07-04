package com.tads.credito.decorator;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class APIClient implements ScoreClient {
    private final RestTemplate restTemplate;
    private static final String API_URL = "https://score.hsborges.dev/api/score?cpf=";

    public APIClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public int getScore(String cpf) {
        String url = API_URL + cpf;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Integer> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Integer.class
            );

            if(response.getBody() != null)
                return response.getBody();
            else
                throw new Exception("a API não retornou resultados");
        } catch (final HttpClientErrorException httpClientErrorException) {
            System.err.println("Erro ao consultar API: " + httpClientErrorException.getMessage());
            throw new HttpClientErrorException(httpClientErrorException.getStatusCode(), "Falha na comunicação com o serviço de score");
        } catch (final Exception ex){
            throw new RuntimeException(ex.getMessage());
        }
    }
}