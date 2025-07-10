package com.github.GanenkovNA.ssh.host;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class HostConfigReader {
    public static HostConnectionConfigDto getHostConnectionConfig(String pathToJson){
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream inputStream = HostConfigReader.class
                    .getResourceAsStream(pathToJson);
            return mapper.readValue(inputStream, HostConnectionConfigDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
