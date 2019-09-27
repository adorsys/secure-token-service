package de.adorsys.sts.cryptoutils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.lang.reflect.Type;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class BaseTypeString implements Serializable, Type {

    private static final long serialVersionUID = -3688536509839905360L;

    private final String value;
}
