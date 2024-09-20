import java.io.*;
import java.util.*;

class BankAccount {
    String name;
    String userName;
    String password;
    String accountNo;
    float balance = 0f;
    int transactions = 0;
    String transactionHistory = "";

    // Method to register a user and save their details to a file
    public void register() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter Your Name: ");
        this.name = sc.nextLine();
        System.out.print("\nEnter Your Username: ");
        this.userName = sc.nextLine();
        System.out.print("\nEnter Your Password: ");
        this.password = sc.nextLine();
        System.out.print("\nEnter Your Account Number: ");
        this.accountNo = sc.nextLine();
        System.out.println("\nRegistration completed! Kindly login now.");

        // Save the user details to a file
        try (FileWriter fw = new FileWriter("users.txt", true)) {
            fw.write(userName + "," + password + "," + name + "," + accountNo + "," + balance + "\n");
        } catch (IOException e) {
            System.out.println("Error saving user data.");
        }
    }

    // Method to login by checking the file
    public boolean login() {
        boolean isLogin = false;
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter Your Username: ");
        String Username = sc.nextLine();
        System.out.print("\nEnter Your Password: ");
        String Password = sc.nextLine();

        // Check file for matching username and password
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equals(Username) && details[1].equals(Password)) {
                    this.userName = details[0];
                    this.password = details[1];
                    this.name = details[2];
                    this.accountNo = details[3];
                    this.balance = Float.parseFloat(details[4]);
                    System.out.println("\nLogin successful!!");
                    isLogin = true;
                    break;
                }
            }
            if (!isLogin) {
                System.out.println("\nUsername or Password is incorrect.");
            }
        } catch (IOException e) {
            System.out.println("Error reading user data.");
        }
        return isLogin;
    }

    // Method to update the user's balance in the file
    public void updateFile() {
        // Read the entire file content
        List<String> userData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equals(userName)) {
                    // Update the balance for the logged-in user
                    details[4] = String.valueOf(balance);
                    line = String.join(",", details);
                }
                userData.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }

        // Write the updated content back to the file
        try (FileWriter fw = new FileWriter("users.txt", false)) {
            for (String data : userData) {
                fw.write(data + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error updating user data.");
        }
    }

    public void withdraw() {
        System.out.print("\nEnter amount to withdraw: ");
        Scanner sc = new Scanner(System.in);
        float amount = sc.nextFloat();
        if (balance >= amount) {
            transactions++;
            balance -= amount;
            System.out.println("\nWithdraw Successful");
            String str = amount + " Rs Withdrawn\n";
            transactionHistory = transactionHistory.concat(str);
            updateFile();  // Save updated balance to file
        } else {
            System.out.println("\nInsufficient Balance");
        }
    }

    public void deposit() {
        System.out.print("\nEnter amount to deposit: ");
        Scanner sc = new Scanner(System.in);
        float amount = sc.nextFloat();
        if (amount <= 100000f) {
            transactions++;
            balance += amount;
            System.out.println("\nSuccessfully Deposited");
            String str = amount + " Rs deposited\n";
            transactionHistory = transactionHistory.concat(str);
            updateFile();  // Save updated balance to file
        } else {
            System.out.println("\nSorry...Limit is 100000.00");
        }
    }

    public void transfer() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter Recipient's Name: ");
        String recipient = sc.nextLine();
        System.out.print("\nEnter amount to transfer: ");
        float amount = sc.nextFloat();
        if (balance >= amount) {
            if (amount <= 50000f) {
                transactions++;
                balance -= amount;
                System.out.println("\nSuccessfully Transferred to " + recipient);
                String str = amount + " Rs transferred to " + recipient + "\n";
                transactionHistory = transactionHistory.concat(str);
                updateFile();  // Save updated balance to file
            } else {
                System.out.println("\nSorry...Limit is 50000.00");
            }
        } else {
            System.out.println("\nInsufficient Balance");
        }
    }

    public void checkBalance() {
        System.out.println("\n" + balance + " Rs");
    }

    public void transHistory() {
        if (transactions == 0) {
            System.out.println("\nNo transactions yet.");
        } else {
            System.out.println("\n" + transactionHistory);
        }
    }
}

public class AtmInterface {

    public static int takeIntegerInput(int limit) {
        int input = 0;
        boolean flag = false;

        while (!flag) {
            try {
                Scanner sc = new Scanner(System.in);
                input = sc.nextInt();
                flag = true;

                if (flag && (input > limit || input < 1)) {
                    System.out.println("Choose a number between 1 and " + limit);
                    flag = false;
                }
            } catch (Exception e) {
                System.out.println("Enter only integer values.");
                flag = false;
            }
        }
        return input;
    }

    public static void main(String[] args) {
        BankAccount b = null;
        System.out.println("\n**********WELCOME TO PNB ATM SYSTEM**********\n");

        while (true) {
            System.out.println("\n1. Register \n2. Login \n3. Exit");
            System.out.print("Enter Your Choice: ");
            int choice = takeIntegerInput(3);

            if (choice == 1) {
                b = new BankAccount();
                b.register();
            } else if (choice == 2) {
                if (b == null) {
                    b = new BankAccount();  // Ensure BankAccount object is created
                }
                if (b.login()) {
                    System.out.println("\n\n**********WELCOME BACK " + b.name + " **********\n");
                    boolean isFinished = false;
                    while (!isFinished) {
                        System.out.println("\n1. Withdraw \n2. Deposit \n3. Transfer \n4. Check Balance \n5. Transaction History \n6. Exit");
                        System.out.print("Enter Your Choice: ");
                        int c = takeIntegerInput(6);

                        switch (c) {
                            case 1:
                                b.withdraw();
                                break;
                            case 2:
                                b.deposit();
                                break;
                            case 3:
                                b.transfer();
                                break;
                            case 4:
                                b.checkBalance();
                                break;
                            case 5:
                                b.transHistory();
                                break;
                            case 6:
                                isFinished = true;
                                break;
                        }
                    }
                }
            } else {
                System.exit(0);
            }
        }
    }
}
