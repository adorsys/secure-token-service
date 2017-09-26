package de.adorsys.sts.worksheetloader;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReadUserCredentials {

    private List<ServerAndUserEncKey> serverAndUserEncKeyList;

    private String login;
    private String password;

    @Getter
    @Builder
    public static class ServerAndUserEncKey {
        private String serverAudienceName;
        private String userEncKey;
    }
}
