package eu.recred.fidouafjava;

import eu.recred.fidouafjava.client.mvp.presenters.RPClientPresenter;
import eu.recred.fidouafjava.client.util.Preferences;

/**
 * Created by sorin.teican on 8/22/2016.
 */
public class Main {

    private static void usage() {
        System.out.println("\n\n***Usage***");
        System.out.println("fidouafcli op param");
        System.out.println("op = register || authenticate || transaction || dereg");
        System.out.println("param = username");
        System.out.println("***Usage***\n");
    }

    public static void main(String[] args) {
        Preferences prefs;
        RPClientPresenter rpClientPresenter;

        int EXIT_CODE = 0;

        if (args.length == 2) {
            prefs = Preferences.getInstance();
            rpClientPresenter = new RPClientPresenter();

            switch (args[0]) {
                case "register":
                    if (!rpClientPresenter.register(args[1])) {
                        System.out.println("Registration failed!");
                        EXIT_CODE = 1;
                    }
                    break;
                case "authenticate":
                    if (!rpClientPresenter.authenticate(args[1])) {
                        System.out.println("Authentication failed!");
                        EXIT_CODE = 1;
                    }
                    break;
                case "transaction":
                    if (!rpClientPresenter.transaction(args[1])) {
                        System.out.println("Transcation failed!");
                        EXIT_CODE = 1;
                    }
                    break;
                case "dereg":
                    if (!rpClientPresenter.dereg(args[1])) {
                        System.out.println("Dereg failed!");
                        EXIT_CODE = 1;
                    }
                    break;
                default:
                    System.err.println("Invalid operation!");
                    usage();
            }
        } else {
            switch (args.length) {
                case 0:
                    System.err.println("Missing operation");
                    break;
                case 1:
                    System.err.println("Missing operation params!");
                    break;
                default:
                    break;
            }
            usage();
        }

        System.exit(EXIT_CODE);
    }
}
