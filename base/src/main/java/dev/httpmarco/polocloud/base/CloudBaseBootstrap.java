package dev.httpmarco.polocloud.base;

public class CloudBaseBootstrap {

    public static void main(String[] args) {

        System.setProperty("startup", String.valueOf(System.currentTimeMillis()));

        new CloudBase();
    }
}
