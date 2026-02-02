package com.scnsoft.simplyconnect.bulkfileencrypt;

import com.scnsoft.simplyconnect.bulkfileencrypt.facade.FilesEncryptionFacade;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class AppCommandLineRunner implements CommandLineRunner {

    private static final String SOURCE_LIST_PARAM = "source";
    private static final String CHECK_IF_ALREADY_ENCRYPTED_PARAM = "checkEncrypted";

    @Autowired
    private FilesEncryptionFacade filesEncryptionFacade;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Encryption file application started");
        for (int i = 0; i < args.length; ++i) {
            System.out.println(args[i]);
        }
        Boolean checkEncrypted = null;
        if (args.length == 0) {
            throw new IllegalArgumentException("Please specify 'source' parameter");
        }
        var sourceParam = args[0].split("=");
        if (sourceParam.length != 2 || !SOURCE_LIST_PARAM.equalsIgnoreCase(sourceParam[0])) {
            throw new IllegalArgumentException("Please specify 'source' parameter as 'source=value1,value2'");
        }
        List<String> sourceList = Stream.of(sourceParam[1].split(",")).map(String::trim).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        if (args.length > 1) {
            var checkEncryptedParam = args[1].split("=");
            if (checkEncryptedParam.length != 2 || !CHECK_IF_ALREADY_ENCRYPTED_PARAM.equalsIgnoreCase(checkEncryptedParam[0])) {
                throw new IllegalArgumentException("Please specify 'checkEncrypted' parameter as 'checkEncrypted=true' or false");
            }
            checkEncrypted = BooleanUtils.toBooleanObject(checkEncryptedParam[1]);
        }
        filesEncryptionFacade.encryptAll(sourceList, checkEncrypted);
        System.out.println("Encryption file application finished");
    }
}
