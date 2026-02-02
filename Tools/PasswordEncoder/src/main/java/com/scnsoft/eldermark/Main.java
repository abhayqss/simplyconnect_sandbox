package com.scnsoft.eldermark;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Please specify a password to encode as a program argument");
            System.exit(0);
        }

        PasswordEncoder passwordEncoder = new StandardPasswordEncoder();
        String passwordEncoded = passwordEncoder.encode(args[0]);
        System.out.println(passwordEncoded);
    }
}
