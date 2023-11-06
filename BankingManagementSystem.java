/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package bankingmanagementsystem;
import java.sql.*;
import java.util.Scanner;

public class BankingManagementSystem {
    
    public static final String url="jdbc:mysql://localhost:3306/BankingManagementSystem";
    
    public static final String username="root";
    public static final String password="Rakesh@123";
            
    public static void main(String[] args) throws ClassNotFoundException,SQLException {
    try{
     Class.forName("com.mysql.cj.jdbc.Driver");
    }catch(ClassNotFoundException e){
      System.out.println(e.getMessage());
    }
    try{
    Connection connection=DriverManager.getConnection(url,username,password);
    Scanner scanner=new Scanner(System.in);
    User user=new User(connection,scanner);
    Accounts accounts=new Accounts(connection,scanner);
    AccountManager accountmanager = new AccountManager(connection,scanner);
    
    String email;
    long account_number;
    
    while(true){
    System.out.println("...WELCOME TO BANKING SYSTEM...");
    System.out.println(); 
    System.out.println("1. Register");
    System.out.println("2. Login");
    System.out.println("3. Exit");
    System.out.println("Enter Your Choice...^_^");
        int choice1=scanner.nextInt();
        
        switch(choice1){
            case 1:
                 user.register();
                // System.out.print("\033[H\033[23");
                // System.out.flush();
                 break;
            case 2:
                email=user.login();
                if(email!=null){
                               System.out.println();
                               System.out.println("User Logged In!");
                                if(!accounts.account_exist(email)){
                                                                  System.out.println();
                                                                  System.out.println("1. Open a new Bank Account");
                                                                  System.out.println("2. Exit");
                                                                        if(scanner.nextInt()==1){
                                                                            account_number=accounts.open_account(email);
                                                                            System.out.println("Account created successfully");
                                                                            System.out.println("Your Account Number is:"+ account_number);
                                                                            }
                                                                        else{
                                                                             break;
                                                                            }
                                                                  }
                                account_number=accounts.getAccountNumber(email);
                                int choice2=0;
                                while(choice2 != 5){
                                    System.out.println();
                                    System.out.println("1. Debit money");
                                    System.out.println("2. Credit money");
                                    System.out.println("3. Transfer money");
                                    System.out.println("4. check balance");
                                    System.out.println("5. Log out.");
                                    System.out.println("Enter your choice ..");
                                    
                                    choice2=scanner.nextInt();
                                    switch(choice2){
                                        case 1:
                                            accountmanager.debit_money(account_number);
                                            break;
                                        case 2:
                                            accountmanager.credit_money(account_number);
                                            break;
                                        case 3:
                                            accountmanager.transfer_money(account_number);
                                            break;
                                        case 4:    
                                            accountmanager.getBalance(account_number);
                                            break;
                                        case 5:
                                            break;
                                        default:
                                            System.out.println("Enter valid Choice..");
                                            
                                                }
                                    
                                }//end of while block
                               }
                        else{
                          System.out.println("Incorrect Email or Password..");
                            }
            case 3:
                System.out.println("THANK YOU FOR USING BANKING SYSTEM..");
                System.out.println("Exiting system..");
                return;
            case 4:
                System.out.println("Enter Valid Choice...");
                break;
        }//switch block.
    }//while block
     
    }catch(SQLException e){
        e.printStackTrace();
    }
    
    }
    
}




//-------------------------------------------------------------------------------------------------


class Accounts{
    private Connection connection; 
    private Scanner scanner;
    public Accounts(Connection connection,Scanner scanner){
    this.connection=connection;
    this.scanner=scanner;
    }
    public long open_account(String email){
    if(!account_exist(email)){
       String open_account_query="INSERT INTO Accounts(account_number,full_name,email,balance,security_pin)VALUES(?,?,?,?)";
       scanner.nextLine();
       System.out.println("Enter Full Name : ");
       String full_name=scanner.nextLine();
       System.out.println("Enter Initial Amount : ");
       double balance=scanner.nextDouble();
       scanner.nextLine();
       System.out.println("Enter Security Pin : ");
       String security_pin=scanner.nextLine();
       try{
       long account_number=generateAccountNumber();    
       PreparedStatement preparedStatement=connection.prepareStatement(open_account_query);
       
       preparedStatement.setLong(1,account_number);
       preparedStatement.setString(2,full_name);
       
       preparedStatement.setDouble(3,balance);
       preparedStatement.setString(4, email);
       preparedStatement.setString(5,security_pin);
       
       int rowsAffected = preparedStatement.executeUpdate();
       if(rowsAffected>0){
       return account_number;
         }else{
           throw new RuntimeException("Account creation failed.");
         }
       }//try block end.
       catch(SQLException e){
       e.printStackTrace();
       }
       
      }
    throw new RuntimeException("Account Already Exist..");
    
    }//End of open_account()method.
    
    
    public long getAccountNumber(String email){
    String query = "SELECT account_number from Accounts WHERE email = ? ";
    
    try{
      PreparedStatement preparedStatement=connection.prepareStatement(query);
      preparedStatement.setString(1, email);
      ResultSet resultSet = preparedStatement.executeQuery();
      
      if(resultSet.next()){
      return resultSet.getLong("account_number");
      
      }
    }catch(SQLException e){
     e.printStackTrace();
    }
        
       throw new RuntimeException("Account Number does not Exist..."); 
    }//end of getAccountNumber() method
    
    
    private long  generateAccountNumber(){
        try{
          Statement statement=connection.createStatement();
          ResultSet resultSet=statement.executeQuery("SELECT account_number from Accounts ORDER by account_number DESC LIMIT 1");
          if(resultSet.next()){
          long last_account_number=resultSet.getLong("account_number");
          return last_account_number+1;
          }else{
           return 10000100;
          }
        }catch(SQLException e){
         e.printStackTrace();
        }
        return 10000100;
    }//end of generateAccount()method;
    
    public boolean account_exist(String email){
       String query = "SELECT account_number from Accounts WHERE email = ? ";
       try{
         
           PreparedStatement preparedStatement = connection.prepareStatement(query);
           preparedStatement.setString(1,email);
           ResultSet resultSet = preparedStatement.executeQuery();
           
           if(resultSet.next()){
           return true;
           }else{
           return false;
           }
       }catch(SQLException e){
        e.printStackTrace();
       }
       return false;
    }//end of account_exist()method;
    
}//end of Accounts class;


//-----------------------------------------------------------------------------------------------


class User{

    private Connection connection;
    private Scanner scanner;
    
    public User(Connection connection,Scanner scanner){
    this.connection=connection;
    this.scanner=scanner;
    
    }
    
    public void register(){
    scanner.nextLine();
    System.out.println("Full Name: ");
    String full_name=scanner.nextLine();
     System.out.print("Email : ");
     String email=scanner.nextLine();
      System.out.print("Password : ");
      String password=scanner.nextLine();
      
      if(user_exist(email)){
       System.out.println("User Already Exist for this email id..");
       return;
      }
      String register_query="INSERT INTO User(full_name,email,password)VALUES(?,?,?)";
      try{
       PreparedStatement preparedStatement=connection.prepareStatement(register_query);
       preparedStatement.setString(1,full_name);
       preparedStatement.setString(2, email);
       preparedStatement.setString(3,password);
       int affectedRows=preparedStatement.executeUpdate();
       
       if(affectedRows>0){
       System.out.println("Registraction Successfull.");
       }else{
       System.out.println("Registraction failed.");
       }
      }//try block
      catch(SQLException e){
      e.printStackTrace();
      }
    }//end of register();
    
    public String login(){
    //nextLine(); is a method in the java Scanner class that returns a line of text 
    //that is read from the scanner object.
    scanner.nextLine();
    System.out.println("Email :");
    String email=scanner.nextLine();
    
    System.out.println("password :");
    String password=scanner.nextLine();
    String login_query="SELECT * FROM User WHERE email = ? AND password = ?";
    
    try{
     PreparedStatement preparedStatement=connection.prepareStatement(login_query);
       
       preparedStatement.setString(1, email);
       preparedStatement.setString(2,password);
       ResultSet resultSet=preparedStatement.executeQuery();
       if(resultSet.next()){
       return email;
       }
       else{
       return null;
       }
    }//end of try block.
    catch(SQLException e){
    e.printStackTrace();
    }
    return null;
    }//end of login() method 
    
    
    
    public boolean user_exist(String email){
     
     String query="SELECT * FROM user WHERE email= ? ";
     
     try{
      PreparedStatement preparedStatement = connection.prepareStatement(email);
        preparedStatement.setString(1, email);
   
       ResultSet resultSet=preparedStatement.executeQuery();
       if(resultSet.next()){
       return true;
       }else{
       return false;
       }
    }
     catch(SQLException e){
        e.printStackTrace();
        }
     return false;
        
    }
}


//---------------------------------------------------------------------------------------------


class AccountManager{
      private Connection connection;
      private Scanner scanner;
      
      AccountManager(Connection connection,Scanner scanner){
      this.connection=connection;
      this.scanner=scanner;
      }
      
      public void credit_money(long account_number)throws SQLException{
             scanner.nextLine();
             System.out.println("Enter Amount : ");
             double amount=scanner.nextDouble();
             scanner.nextLine();
             System.out.println("Enter the security pin ...:->");
             String security_pin=scanner.nextLine();
             
             try{
               connection.setAutoCommit(false);
               if(account_number!=0){
                    PreparedStatement prepareStatement=connection.prepareStatement("SELECT *from Accounts WHERE account_number = ? and security_pin= ? ");
               
                                                
                    prepareStatement.setLong(2,account_number);
                    prepareStatement.setString(2,security_pin);
                    ResultSet resultSet=prepareStatement.executeQuery();
              
               if(resultSet.next()){
                   String credit_query="UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";
                   PreparedStatement preparedStatement1 = connection.prepareStatement(credit_query);
                   preparedStatement1.setDouble(1,amount);
                   preparedStatement1.setLong(2,account_number);
                   int rowsAffected=preparedStatement1.executeUpdate();
                   
                   if(rowsAffected>0){
                   System.out.println("Rs."+amount+"credited successfully..");
                   connection.commit();
                   connection.setAutoCommit(true);
                   return;
                   }//3rd if block
                   else{
                    System.out.println("Transition Failed...");
                    connection.rollback();
                    connection.setAutoCommit(true);
                   }
                  }//2nd if block 
               else{
                  System.out.println("Invalid security pin...");
               }
              
              }//first if block
            }//try block
             catch(SQLException e){
             e.printStackTrace();
             }
             connection.setAutoCommit(true);
      }//end of credit_money() method..
    
      
      public void debit_money(long account_number)throws SQLException{
             scanner.nextLine();
             System.out.println("Enter Amount : ");
             double amount=scanner.nextDouble();
             scanner.nextLine();
             System.out.println("Enter Security pin : ");
             String security_pin=scanner.nextLine();
             
             try{
                connection.setAutoCommit(false);
                          if(account_number!=0){
                          PreparedStatement preparedStatement=connection.prepareStatement("SELECT * FROM Accounts WHERE account_number =? and security_pin = ? ");
                          preparedStatement.setLong(1, account_number);
                          preparedStatement.setString(2, security_pin);
                          ResultSet resultSet=preparedStatement.executeQuery();
                                if(resultSet.next()){
                                   double current_balance=resultSet.getDouble("balance");
                                          if(amount<=current_balance){
                                             String credit_query="UPDATE Accounts SET balance - ? WHERE account_number=?";
                                            PreparedStatement preparedStatement1 = connection.prepareStatement(credit_query);
                                             preparedStatement1.setDouble(1,amount);
                                             preparedStatement1.setLong(2,account_number);
                                             int rowsAffected = preparedStatement1.executeUpdate();
                                                 if(rowsAffected>0){
                                                    System.out.println("RS."+amount+"debited Successfully..");
                                                    connection.commit();
                                                    connection.setAutoCommit(true);
                                                 }  else{
                                                        System.out.println("Transition failed..");
                                                        connection.rollback();
                                                        connection.setAutoCommit(true);
                                                        }
                                             
                                          }//3rd if block
                                          else{
                                              System.out.println("Insufficient balance..");
                                              }
                                   
                                }//2nd if block
                                else{
                                System.out.println("Invalid PIN.");
                                }
                          
                          
                          }//1st if block   
             }catch(SQLException e){
              e.printStackTrace();
             }
             connection.setAutoCommit(true);
          
      //change in line no 66 ..67..76
      }//end of debit_money() method..
      
      //--------------------------------------------
      public void transfer_money(long sender_account_number)throws SQLException{
           scanner.nextLine();
           System.out.println("Enter receiver account number : ");
           long receiver_account_number = scanner.nextLong();
           System.out.println("Enter Amount : ");
           double amount = scanner.nextDouble();
           scanner.nextLine();
           System.out.println("Enter security pin : ");
           String security_pin=scanner.nextLine();
           
           try{
              connection.setAutoCommit(false);
              if(sender_account_number!=0 && receiver_account_number!=0){
                  PreparedStatement preparedStatement=connection.prepareStatement("SELECT * from Accounts WHERE account_number = ? AND security_pin = ?");
                  preparedStatement.setLong(1, sender_account_number);
                  preparedStatement.setString(2,security_pin);
                  ResultSet resultSet=preparedStatement.executeQuery();
                  
                  if(resultSet.next()){
                     double current_balance=resultSet.getDouble("balance");
                           if(amount<=current_balance){
                                    String debit_query = "UPDATE Accounts SET balance - ? WHERE account_number = ? ";
                                    String credit_query = "UPDATE Accounts SET balance + ? WHERE account_number = ?";
                                    PreparedStatement creditPreparedStatement = connection.prepareStatement(credit_query);
                                    PreparedStatement debitPreparedStatement = connection.prepareStatement(debit_query);
                                    creditPreparedStatement.setDouble(1, amount);
                                    creditPreparedStatement.setLong(2,receiver_account_number);
                                    debitPreparedStatement.setDouble(1, amount);
                                    debitPreparedStatement.setLong(2, sender_account_number);
                                    int rowsAffected1=debitPreparedStatement.executeUpdate();
                                    int rowsAffected2=creditPreparedStatement.executeUpdate();
                                    
                                    if(rowsAffected1>0 && rowsAffected2>0){
                                               System.out.println("Transition Successful..");
                                               System.out.println("Rs."+amount+"Ttansferred successfully..");
                                               connection.commit();
                                               connection.setAutoCommit(true);
                                               
                                    }//4th if block
                                       else{
                                           System.out.println("Transition failed");
                                           connection.rollback();
                                           connection.setAutoCommit(true);
                                            }
                           }//3rd if block
                           else{
                               System.out.println("Insufficient Balance!");
                               
                               }
                  }//2nd if block
                  else{
                      System.out.println("Invalid balance.."); 
                      }
              }//1st if block
              
           }
           catch(SQLException e){
             e.printStackTrace();
           }
           connection.setAutoCommit(true);
           
      }//end of transfer_money() method
      
      //---------------------------------------------
      
    
      public void getBalance(long account_number){
            scanner.nextLine();
            System.out.println("Enter the security pin : ");
            String security_pin=scanner.nextLine();
            try{
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT balance FROM Accounts WHERE account_number = ? AND security_pin = ?");
            preparedStatement.setLong(1,account_number);
            preparedStatement.setString(2,security_pin);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if(resultSet.next()){
               double balance=resultSet.getDouble("balance");
               System.out.println(balance+"balance");
            }else{
             System.out.println("Invalid pin..");
            }
            }catch(SQLException e){
             e.printStackTrace();
            }
            
          
      }//end of getBalance()method..
      
}//End of AccountManager class

//---------------------------------------------------------------------------------------------
