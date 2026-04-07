import java.sql.*;
import java.util.Scanner;

public class OnlineBusReservation {

    static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
    static final String USER = "system";
    static final String PASSWORD = "12345678";

    static Connection con;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);

            while (true) {
                System.out.println("\n===== LOGIN MENU =====");
                System.out.println("1. Admin Login");
                System.out.println("2. Customer Login");
                System.out.println("3. Customer Registration");
                System.out.println("4. Exit");

                int choice = sc.nextInt();
                switch (choice) {
                    case 1: adminLogin(); break;
                    case 2: customerLogin(); break;
                    case 3: registerCustomer(); break;
                    case 4: System.exit(0);
                    default: System.out.println("Invalid choice!");
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // ================= ADMIN LOGIN =================
    static void adminLogin() throws Exception {
        System.out.print("Username: ");
        sc.nextLine();
        String uname = sc.nextLine();

        System.out.print("Password: ");
        String pass = sc.nextLine();

        PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM users WHERE user_name=? AND password=? AND role='ADMIN'");
        ps.setString(1, uname);
        ps.setString(2, pass);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            System.out.println("Admin Login Successful!");
            adminMenu();
        } else {
            System.out.println("Invalid Admin Login!");
        }
    }

    // ================= CUSTOMER LOGIN =================
    static void customerLogin() throws Exception {
        System.out.print("Username: ");
        sc.nextLine();
        String uname = sc.nextLine();

        System.out.print("Password: ");
        String pass = sc.nextLine();

        PreparedStatement ps = con.prepareStatement(
                "SELECT user_id FROM users WHERE user_name=? AND password=? AND role='CUSTOMER'");
        ps.setString(1, uname);
        ps.setString(2, pass);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int userId = rs.getInt(1);
            System.out.println("Customer Login Successful!");
            customerMenu(userId);
        } else {
            System.out.println("Invalid Customer Login!");
        }
    }

    // ================= CUSTOMER REGISTRATION =================
    static void registerCustomer() throws Exception {
        System.out.println("\n===== CUSTOMER REGISTRATION =====");

        System.out.print("User ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Username: ");
        String name = sc.nextLine();

        System.out.print("Password: ");
        String pass = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Phone: ");
        String phone = sc.nextLine();

        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO users(user_id,user_name,password,email,phone,role) VALUES(?,?,?,?,?,?)");
        ps.setInt(1, id);
        ps.setString(2, name);
        ps.setString(3, pass);
        ps.setString(4, email);
        ps.setString(5, phone);
        ps.setString(6, "CUSTOMER");

        ps.executeUpdate();
        System.out.println("Registration Successful! Please Login.");
    }

    // ================= ADMIN MENU =================
    static void adminMenu() throws Exception {
        while (true) {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("1. View Buses");
            System.out.println("2. Add Bus");
            System.out.println("3. View Bookings");
            System.out.println("4. Update Booking");
            System.out.println("5. View Users");
            System.out.println("6. Exit");

            int ch = sc.nextInt();
            switch (ch) {
                case 1: viewBuses(); break;
                case 2: addBus(); break;
                case 3: viewBookings(); break;
                case 4: updateBooking(); break;
                case 5: viewUsers(); break;
                case 6: return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    // ================= CUSTOMER MENU =================
    static void customerMenu(int userId) throws Exception {
        while (true) {
            System.out.println("\n===== CUSTOMER MENU =====");
            System.out.println("1. View Buses");
            System.out.println("2. Book Ticket");
            System.out.println("3. View My Bookings");
            System.out.println("4. Cancel Booking");
            System.out.println("5. View My Details");
            System.out.println("6. Exit");

            int ch = sc.nextInt();
            switch (ch) {
                case 1: viewBuses(); break;
                case 2: bookTicket(userId); break;
                case 3: viewMyBookings(userId); break;
                case 4: cancelBooking(userId); break;
                case 5: viewMyDetails(userId); break;
                case 6: return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    // ================= COMMON METHODS =================
    static void viewBuses() throws Exception {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM bus");

        System.out.println("\nID | Name | Source | Destination | Seats | Price");
        while (rs.next()) {
            System.out.println(rs.getInt("bus_id") + " | "
                    + rs.getString("bus_name") + " | "
                    + rs.getString("source") + " | "
                    + rs.getString("destination") + " | "
                    + rs.getInt("total_seats") + " | "
                    + rs.getDouble("ticket_price"));
        }
    }

    static void addBus() throws Exception {
        System.out.print("Bus ID: ");
        int id = sc.nextInt(); sc.nextLine();

        System.out.print("Bus Name: ");
        String name = sc.nextLine();

        System.out.print("Source: ");
        String source = sc.nextLine();

        System.out.print("Destination: ");
        String dest = sc.nextLine();

        System.out.print("Total Seats: ");
        int seats = sc.nextInt();

        System.out.print("Ticket Price: ");
        double price = sc.nextDouble();

        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO bus(bus_id,bus_name,source,destination,total_seats,ticket_price) VALUES(?,?,?,?,?,?)");
        ps.setInt(1, id);
        ps.setString(2, name);
        ps.setString(3, source);
        ps.setString(4, dest);
        ps.setInt(5, seats);
        ps.setDouble(6, price);

        ps.executeUpdate();
        System.out.println("Bus Added Successfully!");
    }

    static void viewBookings() throws Exception {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM booking");

        System.out.println("\nBookID | BusID | UserID | Date | Seat | Amount");
        while (rs.next()) {
            System.out.println(rs.getInt("book_id") + " | "
                    + rs.getInt("bus_id") + " | "
                    + rs.getInt("user_id") + " | "
                    + rs.getDate("travel_date") + " | "
                    + rs.getInt("seat_number") + " | "
                    + rs.getDouble("total_amount"));
        }
    }

    static void updateBooking() throws Exception {
        System.out.print("Booking ID to Update: ");
        int id = sc.nextInt();

        System.out.print("New Seat Number: ");
        int seat = sc.nextInt();

        PreparedStatement ps = con.prepareStatement(
                "UPDATE booking SET seat_number=? WHERE book_id=?");
        ps.setInt(1, seat);
        ps.setInt(2, id);

        ps.executeUpdate();
        System.out.println("Booking Updated!");
    }

    static void viewUsers() throws Exception {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT user_id,user_name,role FROM users");

        System.out.println("\nID | Name | Role");
        while (rs.next()) {
            System.out.println(rs.getInt(1) + " | "
                    + rs.getString(2) + " | "
                    + rs.getString(3));
        }
    }

    // ================= CUSTOMER METHODS =================
    static void bookTicket(int userId) throws Exception {
        System.out.print("Booking ID: ");
        int bookId = sc.nextInt();

        System.out.print("Bus ID: ");
        int busId = sc.nextInt();

        System.out.print("Travel Date (YYYY-MM-DD): ");
        sc.nextLine();
        String date = sc.nextLine();

        System.out.print("Seat Number: ");
        int seat = sc.nextInt();

        // Check seat availability
        PreparedStatement psCheck = con.prepareStatement(
                "SELECT * FROM booking WHERE bus_id=? AND travel_date=TO_DATE(?, 'YYYY-MM-DD') AND seat_number=?");
        psCheck.setInt(1, busId);
        psCheck.setString(2, date);
        psCheck.setInt(3, seat);
        ResultSet rsCheck = psCheck.executeQuery();
        if(rsCheck.next()) {
            System.out.println("This seat is already booked! Choose another seat.");
            return;
        }

        // Get ticket price from bus table
        PreparedStatement psPrice = con.prepareStatement(
                "SELECT ticket_price FROM bus WHERE bus_id=?");
        psPrice.setInt(1, busId);
        ResultSet rsPrice = psPrice.executeQuery();
        double amount = 0;
        if(rsPrice.next()) {
            amount = rsPrice.getDouble(1);
        } else {
            System.out.println("Bus not found!");
            return;
        }

        // Insert booking
        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO booking(book_id,bus_id,user_id,travel_date,seat_number,total_amount) VALUES(?,?,?,?,?,?)");
        ps.setInt(1, bookId);
        ps.setInt(2, busId);
        ps.setInt(3, userId);
        ps.setDate(4, Date.valueOf(date));
        ps.setInt(5, seat);
        ps.setDouble(6, amount);

        ps.executeUpdate();
        System.out.println("Ticket Booked Successfully! Amount: " + amount);
    }

    static void viewMyBookings(int userId) throws Exception {
        PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM booking WHERE user_id=?");
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        System.out.println("\nBookID | BusID | Date | Seat | Amount");
        while (rs.next()) {
            System.out.println(rs.getInt("book_id") + " | "
                    + rs.getInt("bus_id") + " | "
                    + rs.getDate("travel_date") + " | "
                    + rs.getInt("seat_number") + " | "
                    + rs.getDouble("total_amount"));
        }
    }

    static void cancelBooking(int userId) throws Exception {
        System.out.print("Booking ID to Cancel: ");
        int id = sc.nextInt();

        PreparedStatement ps = con.prepareStatement(
                "DELETE FROM booking WHERE book_id=? AND user_id=?");
        ps.setInt(1, id);
        ps.setInt(2, userId);

        ps.executeUpdate();
        System.out.println("Booking Cancelled Successfully!");
    }

    static void viewMyDetails(int userId) throws Exception {
        PreparedStatement ps = con.prepareStatement(
                "SELECT user_id,user_name,email,phone FROM users WHERE user_id=?");
        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            System.out.println("ID: " + rs.getInt(1));
            System.out.println("Name: " + rs.getString(2));
            System.out.println("Email: " + rs.getString(3));
            System.out.println("Phone: " + rs.getString(4));
        }
    }
}