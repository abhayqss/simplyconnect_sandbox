package com.scnsoft.simplyconnect.filedecrypt;

import com.scnsoft.simplyconnect.filedecrypt.facade.FilesEncryptionFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppCommandLineRunner implements CommandLineRunner {

    private static final String MODE_PARAM="mode";
    private static final String SOURCE_PARAM = "source";
    private static final String TARGET_FOLDER_PARAM = "targetFolder";

    @Autowired
    private FilesEncryptionFacade filesEncryptionFacade;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Decryption file application started");
        for (int i = 0; i < args.length; ++i) {
            System.out.println(args[i]);
        }
        if (args.length != 3) {
            throw new IllegalArgumentException("Please specify 'mode' (encrypt/decrypt) 'source' and 'targetFolder' parameters");
        }
        var modeParam = args[0].split("=");
        if (modeParam.length != 2 || !MODE_PARAM.equalsIgnoreCase(modeParam[0])) {
            throw new IllegalArgumentException("Please specify 'mode' parameter as 'mode=encrypt' or 'mode=decrypt'.");
        }
        var mode = CipherMode.valueOf(modeParam[1].toUpperCase());

        var sourceParam = args[1].split("=");
        if (sourceParam.length != 2 || !SOURCE_PARAM.equalsIgnoreCase(sourceParam[0])) {
            throw new IllegalArgumentException("Please specify 'source' parameter as 'source=value'. Source can be file or folder");
        }
        var source = sourceParam[1];
        var targetFolderParam = args[2].split("=");
        if (targetFolderParam.length != 2 || !TARGET_FOLDER_PARAM.equalsIgnoreCase(targetFolderParam[0])) {
            throw new IllegalArgumentException("Please specify 'targetFolder' parameter as 'targetFolder=value'");
        }
        var targetFolder = targetFolderParam[1];
        filesEncryptionFacade.process(mode, source, targetFolder);
        System.out.println("Decryption file application finished");
    }
}
