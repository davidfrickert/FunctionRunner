package pt.ist.photon_graal;

import pt.ist.photon_graal.rest.HttpMain;

import java.io.IOException;

public class App {
    public static void main(String[] args) {
        try {
            HttpMain.main(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
