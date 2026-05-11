package com.launchgate.aievaluation.service;

import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class GithubRepositoryVerifier {
    private static final Pattern GITHUB_REPOSITORY_URL = Pattern.compile("^https://github\\.com/([^/]+)/([^/]+?)(?:\\.git)?/?$");

    private final RestClient restClient;

    public boolean isGithubRepositoryUrl(String value) {
        return value != null && GITHUB_REPOSITORY_URL.matcher(value.trim()).matches();
    }

    public boolean exists(String value) {
        var matcher = GITHUB_REPOSITORY_URL.matcher(value.trim());
        if (!matcher.matches()) {
            return false;
        }
        var owner = matcher.group(1);
        var repository = matcher.group(2);
        try {
            var response = restClient.get()
                    .uri("https://api.github.com/repos/{owner}/{repository}", owner, repository)
                    .retrieve()
                    .toBodilessEntity();
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception exception) {
            return false;
        }
    }
}
