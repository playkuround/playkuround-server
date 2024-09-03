package com.playkuround.playkuroundserver.domain.auth.email.application;

import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class CodeGenerator {

    public String generateCode(Set<CodeType> codeTypeSet, long codeLength) {
        validateParameters(codeTypeSet, codeLength);

        String characters = createCharacters(codeTypeSet);

        Random random = new Random();
        return random.ints(codeLength, 0, characters.length())
                .mapToObj(characters::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private void validateParameters(Set<CodeType> codeTypeSet, long codeLength) {
        Objects.requireNonNull(codeTypeSet, "codeTypeSet must not be null");
        if (codeTypeSet.isEmpty()) {
            throw new IllegalArgumentException("codeTypeSet must not be empty");
        }
        if (codeLength <= 0) {
            throw new IllegalArgumentException("codeLength must be greater than 0");
        }
    }

    private String createCharacters(Set<CodeType> codeTypeSet) {
        StringBuilder characters = new StringBuilder();
        if (codeTypeSet.contains(CodeType.NUMBER)) {
            characters.append("0123456789");
        }
        if (codeTypeSet.contains(CodeType.ALPHABET_LOWERCASE)) {
            characters.append("abcdefghijklmnopqrstuvwxyz");
        }
        if (codeTypeSet.contains(CodeType.ALPHABET_UPPERCASE)) {
            characters.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        }
        return characters.toString();
    }

    public enum CodeType {
        NUMBER, ALPHABET_LOWERCASE, ALPHABET_UPPERCASE
    }
}
