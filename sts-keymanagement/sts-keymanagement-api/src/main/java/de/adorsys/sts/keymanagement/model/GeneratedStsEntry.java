package de.adorsys.sts.keymanagement.model;

import de.adorsys.keymanagement.api.types.template.ProvidedKeyTemplate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GeneratedStsEntry<T extends ProvidedKeyTemplate> {

    private final StsKeyEntry entry;
    private final T key;
}
