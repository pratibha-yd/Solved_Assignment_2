package JDBC_Assignment;
import java.sql.*;
import java.util.Scanner;

public class Main {
    static final String DB_URL = "jdbc:mysql://localhost:3306";
    static final String USER = "root";
    static final String PASS = "Knoldus@25";
    // for suffix
    static final String SUFFIX = "_Pratibha";
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        // creating connection
        Connection con = DriverManager.getConnection(DB_URL, USER, PASS);

        //creating statement
        Statement st = con.createStatement();
        System.out.println("Do you already have Shopping Schema in your Data Base? Y/N");
        Scanner sc = new Scanner(System.in);
        String userReply = sc.nextLine();
        con.setAutoCommit(false);
        // user says N or n then create Schema
        if (userReply.equals("N") || userReply.equals("n"))
        {
            // query to create db
            String createDB = "CREATE DATABASE Shopping";
            // executing the query
            st.executeUpdate(createDB);
            System.out.println("Database has been created");
        }
        String sqlUseDB = "USE Shopping";
        st.executeUpdate(sqlUseDB);

        System.out.println("Do you already have Product Table in your Data Base? Y/N");
        userReply = sc.nextLine();

        // user says N or n then create table
        if (userReply.equals("N") || userReply.equals("n"))
        {
            // query to create Table
            String createTB = "create table products (pid int not null, pname varchar (255), price int not null, CONSTRAINT product_pk PRIMARY KEY (pid))";
            // executing the query
            st.executeUpdate(createTB);
            System.out.println("Table has been created");
        }

        // prepared batch records list
        int[] PID = new int[] {1, 2, 3, 4, 5, 6};
        String[] ProductName = new String[] {"Reynolds 405", "HB Draw Pencil", "Crayons Rainbow", "Notebook", "Draw Sheet", "Brush Air"};
        int[] Productprice = new int[]{8, 10, 12, 14, 16, 5};

        String insertRec = "INSERT INTO products VALUES (?,?,?)";

        PreparedStatement pstmt = con.prepareStatement(insertRec);
        for (int i = 0; i < ProductName.length; i++) {
            pstmt.setInt(1, PID[i]);
            pstmt.setString(2,ProductName[i].concat(SUFFIX));
            pstmt.setInt(3,Productprice[i]);
            pstmt.addBatch();
        }

        try{
            pstmt.executeBatch();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Commit the transaction to close the COPY command
        con.commit();

        String queryAll = "SELECT * FROM products";

        // stores all data of table to view in properly
        ResultSet rs = pstmt.executeQuery(queryAll);


        while (rs.next()){
            // Retrieve by column name
            System.out.println("pID: " + rs.getInt("pid"));
            System.out.println("pName: " + rs.getString("pname"));
            System.out.println("Price: " + rs.getInt("price"));
        }

        st.close();
        pstmt.close();
        con.close();

    }
}
