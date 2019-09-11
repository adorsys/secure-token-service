package de.adorsys.sts.keymanagement.model;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class KeyRotationResult {

    @Builder.Default
    private List<String> removedKeys = new ArrayList<>();

    @Builder.Default
    private List<String> futureKeys = new ArrayList<>();

    @Builder.Default
    private List<String> generatedKeys = new ArrayList<>();
}
