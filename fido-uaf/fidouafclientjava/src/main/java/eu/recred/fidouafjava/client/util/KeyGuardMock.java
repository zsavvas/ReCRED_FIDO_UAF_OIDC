package eu.recred.fidouafjava.client.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
/**
 * Created by sorin on 21.08.2016.
 */
public class KeyGuardMock implements Serializable {
    private static KeyGuardMock mInstance;
    private KeyStore mStore;

    private KeyGuardMock() {
        mStore = new KeyStore();
        try {
            loadState();
        } catch (IOException e) {
            // This should happen only for the first use.
            initPIN();
        } catch (ClassNotFoundException e) {
            initPIN();
        }
    }

    public static KeyGuardMock getInstance() {
        if (mInstance == null)
            mInstance = new KeyGuardMock();

        return mInstance;
    }

    private void initPIN() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("---KeyGuard Initialization---\n");

        System.out.println("Please configure PIN code for this device!");
        System.out.println("The PIN code must contain between 4 and 10 digits!");
        System.out.print("Enter PIN code: ");

        String pin;
        boolean ok = false;
        do {
            pin = scanner.nextLine();
            if (pin.matches("[0-9]{4,10}")) {
                System.out.print("Confirm PIN code: ");
                String secondPin = scanner.nextLine();
                if (!secondPin.matches("[0-9]{4,10}") || !pin.equals(secondPin)) {
                    System.out.println("This PIN code doesn`t match the first one!");
                    System.out.println("Repeating process!");
                    continue;
                } else ok = true;
            }
            else
                System.out.println("Invalid PIN code entered, please enter another one!");
        } while (!ok);

        setPIN(pin);
        try {
            saveState();
            System.out.println("PIN code stored!");
        } catch (IOException e) {
            System.out.println("Error storing PIN code!");
        }

        System.out.println("\n---KeyGuard Initialization---\n");
    }

    public boolean authenticate(String title) {
        Scanner scanner = new Scanner(System.in);

        if (title == null)
            System.out.println("---KeyGuard Authentication---\n");
        else
            System.out.println("---KeyGuard Consent for " + title + "---\n");

        int tries = 0;
        do {
            System.out.print("Enter PIN code: ");
            String pin = scanner.nextLine();
            if (!pin.matches("[0-9]{4,10}")) {
                System.out.println("Invalid PIN code, please try again!");
                System.out.println("The PIN code must contain between 4 and 10 digits!");
                continue;
            } else if (!checkPIN(pin)) {
                System.out.println("Invalid PIN code, please try again!");
                tries++;
                continue;
            } else return true;
        } while (tries < 3);

        System.out.println("Number of tries exceeded!");

        System.out.println("\n---KeyGuard Authentication---\n");

        return false;
    }

    public void reconfigPIN() {
        System.out.println("---KeyGuard Reconfiguration---\n");

        if (!authenticate(null)) {
            System.out.println("Not authorized to reconfigure PIN code!");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("Please configure PIN code for this device!");
        System.out.println("The PIN code must contain between 4 and 10 digits!");
        System.out.print("Enter PIN code: ");

        String pin;
        boolean ok = false;
        do {
            pin = scanner.nextLine();
            if (pin.matches("[0-9]{4,10}")) {
                System.out.print("Confirm PIN code: ");
                String secondPin = scanner.nextLine();
                if (!secondPin.matches("[0-9]{4,10}") || !pin.equals(secondPin)) {
                    System.out.println("This PIN code doesn`t match the first one!");
                    System.out.println("Repeating process!");
                    continue;
                } else ok = true;
            }
            else
                System.out.println("Invalid PIN code entered, please enter another one!");
        } while (!ok);

        setPIN(pin);
        try {
            saveState();
            System.out.println("PIN code stored!");
        } catch (IOException e) {
            System.out.println("Error storing PIN code!");
        }

        System.out.println("\n---KeyGuard Reconfiguration---\n");
    }

    private boolean checkPIN(String pin) {
        if (empty(pin))
            return false;

        if (pin.equals(mStore.mKeys.get("pin"))) return true;
        else return false;
    }

    private void setPIN(String pin) {
        if (!empty(pin))
            mStore.mKeys.put("pin", pin);
    }

    private void saveState() throws IOException {
        if (mStore.mKeys.isEmpty())
            return;

        try {
            FileOutputStream keyGuardFile = new FileOutputStream("keyGuard.ser");
            ObjectOutputStream out = new ObjectOutputStream(keyGuardFile);
            out.writeObject(mStore);
            out.close();
            keyGuardFile.close();
        } catch (IOException e) {
            //e.printStackTrace();
            throw e;
        }
    }

    private void loadState() throws IOException, ClassNotFoundException {
        try {
            FileInputStream keyGuardFile = new FileInputStream("keyGuard.ser");
            ObjectInputStream in = new ObjectInputStream(keyGuardFile);
            mStore = (KeyStore) in.readObject();
            in.close();
            keyGuardFile.close();
        } catch (IOException e) {
            // This should be thrown at first use of class.
            //e.printStackTrace();
            throw e;
        } catch (ClassNotFoundException e) {
            throw e;
        }
    }

    private boolean empty(String param) {
        return param == null || param.trim().isEmpty();
    }

    private class KeyStore implements Serializable {
        public Map<String, String> mKeys;

        public KeyStore() { mKeys = new HashMap<>(); }
    }
}
