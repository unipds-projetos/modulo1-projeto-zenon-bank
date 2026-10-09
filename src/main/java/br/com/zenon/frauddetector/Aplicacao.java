package br.com.zenon.frauddetector;

import br.com.zenon.fraud.security.MethodGuard;

public class Aplicacao {

    public static void main(String[] args) {
        MethodGuard.requireNoArguments(args);
        System.out.println("Zenón Bank — fraud detector");
        System.out.println("Java " + Runtime.version());
    }
}
