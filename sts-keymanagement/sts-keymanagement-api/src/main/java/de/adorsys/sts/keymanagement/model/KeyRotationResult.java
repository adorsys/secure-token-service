package de.adorsys.sts.keymanagement.model;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class KeyRotationResult {

    private List<String> removedKeys;
    private List<String> futureKeys;
    private List<String> generatedKeys;
}
