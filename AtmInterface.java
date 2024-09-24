import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class BankAccount {
    String name;
    String userName;
    String password;
    String accountNo;
    float balance = 0f;
    int transactions = 0;

    // Method to check if the username already exists in the file
    public boolean isUsernameExists(String username) {
        boolean exists = false;
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equals(username)) {
                    exists = true;
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user data.");
        }
        return exists;
    }

    // Method to check if the generated account number already exists in the file
    public boolean isAccountNoExists(String accountNo) {
        boolean exists = false;
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details[3].equals(accountNo)) {
                    exists = true;
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user data.");
        }
        return exists;
    }

    // Method to generate a unique random account number
    public String generateAccountNo() {
        Random rand = new Random();
        String accountNo;
        do {
            accountNo = String.format("%09d", rand.nextInt(1000000000));  // Generate a 9-digit random number
        } while (isAccountNoExists(accountNo));  // Ensure the account number is unique
        return accountNo;
    }

    // Utility method to validate password with the required conditions
    public boolean isValidPassword(String password) {
        // At least 8 characters, at least one digit, one special character, one uppercase, and one lowercase character
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";
        return password.matches(regex);
    }

    // Method to register a user and save their details to a file
    public void register() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter Your Name: ");
        this.name = sc.nextLine();
    
        // Check if the username already exists, if so, prompt the user to enter a new one
        while (true) {
            System.out.print("\nEnter Your Username: ");
            this.userName = sc.nextLine();
    
            if (isUsernameExists(this.userName)) {
                System.out.println("\nUsername already exists. Please choose a different username.");
            } else {
                break;
            }
        }

        
    
        // Loop to ensure valid password
        while (true) {
            System.out.print("\nEnter Your Password: ");
            this.password = sc.nextLine();
    
            if (isValidPassword(this.password)) {
                break;
            } else {
                System.out.println("\nPassword must meet the criteria:\n" +
                        "- At least 8 characters\n" +
                        "- At least 1 number\n" +
                        "- At least 1 special character\n" +
                        "- At least 1 uppercase and 1 lowercase character");
            }
        }
    
        // Generate a unique account number
        this.accountNo = generateAccountNo();
        System.out.println("\nYour account number is: " + this.accountNo);
        System.out.println("Registration completed! Kindly login now.");
    
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

    // Method to update the user's balance or password in the file
    public void updateFile() {
        // Read the entire file content
        List<String> userData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equals(userName)) {
                    // Update the balance for the logged-in user
                    details[1] = password; // Update password if changed
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

    // Utility method to get current date and time as a string
    private String getCurrentDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    // Method to append transaction history to the user's individual file
    private void appendTransactionToFile(String transaction) {
        String filename = userName + "_transactions.txt";  // Create a unique filename for each user
        try (FileWriter fw = new FileWriter(filename, true)) {
            fw.write(transaction + "\n");
        } catch (IOException e) {
            System.out.println("Error saving transaction history.");
        }
    }

    // Method to change password
    public void changePassword() {
    Scanner sc = new Scanner(System.in);
    System.out.print("\nEnter your current password: ");
    String currentPassword = sc.nextLine();

    if (this.password.equals(currentPassword)) {
        while (true) {
            System.out.print("\nEnter your new password: ");
            String newPassword = sc.nextLine();

            if (!newPassword.equals(this.password)) {
                if (isValidPassword(newPassword)) {
                    this.password = newPassword;
                    updateFile();  // Save updated password to file
                    System.out.println("\nPassword changed successfully.");
                    break;
                } else {
                    System.out.println("\nPassword must meet the criteria:\n" +
                            "- At least 8 characters\n" +
                            "- At least 1 number\n" +
                            "- At least 1 special character\n" +
                            "- At least 1 uppercase and 1 lowercase character");
                }
            } else {
                System.out.println("\nNew password cannot be the same as the current password.");
            }
        }
    } else {
        System.out.println("\nCurrent password is incorrect.");
    }
}

    // Updated Withdraw Method with Amount Validation and Date/Time
    public void withdraw() {
        System.out.print("\nEnter amount to withdraw: ");
        Scanner sc = new Scanner(System.in);
        float amount = sc.nextFloat();
        if (amount <= 0) {
            System.out.println("\nInvalid amount. Please enter a positive amount.");
            return;
        }
        if (balance >= amount) {
            transactions++;
            balance -= amount;
            System.out.println("\nWithdraw Successful");

            // Prepare transaction details with date and time
            String transaction = getCurrentDateTime() + ": " + amount + " Rs Withdrawn";
            appendTransactionToFile(transaction);  // Save transaction to file
            updateFile();  // Save updated balance to file
        } else {
            System.out.println("\nInsufficient Balance");
        }
    }

    // Updated Deposit Method with Amount Validation and Date/Time
    public void deposit() {
        System.out.print("\nEnter amount to deposit: ");
        Scanner sc = new Scanner(System.in);
        float amount = sc.nextFloat();
        if (amount <= 0) {
            System.out.println("\nInvalid amount. Please enter a positive amount.");
            return;
        }
        if (amount <= 100000f) {
            transactions++;
            balance += amount;
            System.out.println("\nSuccessfully Deposited");

            // Prepare transaction details with date and time
            String transaction = getCurrentDateTime() + ": " + amount + " Rs Deposited";
            appendTransactionToFile(transaction);  // Save transaction to file
            updateFile();  // Save updated balance to file
        } else {
            System.out.println("\nSorry...Limit is 100000.00");
        }
    }

    // Updated Transfer Method with Amount Validation and Date/Time
    public void transfer() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter Recipient's Name: ");
        String recipient = sc.nextLine();
        System.out.print("\nEnter amount to transfer: ");
        float amount = sc.nextFloat();
        if (amount <= 0) {
            System.out.println("\nInvalid amount. Please enter a positive amount.");
            return;
        }
        if (balance >= amount && amount <= 50000f) {
            transactions++;
            balance -= amount;
            System.out.println("\nSuccessfully Transferred to " + recipient);

            // Prepare transaction details with date and time
            String transaction = getCurrentDateTime() + ": " + amount + " Rs Transferred to " + recipient;
            appendTransactionToFile(transaction);  // Save transaction to file
            updateFile();  // Save updated balance to file
        } else if (amount > 50000f) {
            System.out.println("\nSorry...Limit is 50000.00");
        } else {
            System.out.println("\nInsufficient Balance");
        }
    }

    // Method to display transaction history from the user's individual file
    public void transHistory() {
        String filename = userName + "_transactions.txt";  // Use the unique filename for each user
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("\nNo transactions recorded yet.");
        }
    }

    public void checkBalance() {
        System.out.println("\n" + "Available Balance: Rs " + balance);
    }
}

public class newF {

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
                        System.out.println("\n1. Withdraw \n2. Deposit \n3. Transfer \n4. Check Balance \n5. Transaction History \n6. Change Password \n7. Exit");
                        System.out.print("Enter Your Choice: ");
                        int c = takeIntegerInput(7);

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
                                b.changePassword();
                                break;
                            case 7:
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
