/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package holidayreservationsystemclient;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import ws.partner.PartnerWebService_Service;

/**
 *
 * @author Toh Seaw Teng
 */
public class MainApp {
    private ws.partner.Partner partner;
    private static PartnerWebService_Service service = new PartnerWebService_Service();
    private static List<String> roomTypeList = Arrays.asList("Deluxe Room", "Premier Room", "Family Room", "Junior Suite", "Grand Suite");

    public MainApp() {
    }
    
    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to Holiday Reservation System ***\n");
            System.out.println("1: Partner Login");
            System.out.println("3: Exit\n");
            response = 0;
            
            while(response < 1 || response > 2)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    this.partnerLogin();
                    
                    if (this.partner != null) {
                        reservationMenu();
                    }
                }
                else if (response == 2)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 2)
            {
                break;
            }
        }
    }
    
    public void partnerLogin() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n*** Holiday Reservation System : Partner Login ***\n");
        System.out.print("Enter Username> ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter Password> ");
        String password = scanner.nextLine().trim();
        
        try {
            this.partner = this.service.getPartnerWebServicePort().partnerLogin(username, password);
            System.out.println("Guest successfully login!");
        } catch (ws.partner.PartnerInvalidPasswordException_Exception | ws.partner.PartnerNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void reservationMenu() {
        
    }
}
