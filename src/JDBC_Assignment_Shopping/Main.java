package JDBC_Assignment_Shopping;
import java.sql.*;

public class Main {
    static final String DB_URL = "jdbc:mysql://localhost:3306/shopping";
    static final String USER = "root";
    static final String PASS = "Knoldus@25";
    // for suffix
    static final String SUFFIX = "_Pratibha";

    static Connection con;
    static PreparedStatement st;
    ResultSet rs;
    static Statement s;
    public static void main(String[] args) throws Exception {

        Class.forName("com.mysql.jdbc.Driver");
        // creating connection
        con = DriverManager.getConnection(DB_URL, USER, PASS);
;
        con.setAutoCommit(false);

        // creating obj to call each method to process records
        Main shopping = new Main();
        shopping.insertProductAndDisplay();
        shopping.insertAndDisplayCart();
        shopping.TotalAmount();
        shopping.totalCart();
        shopping.productSoldMost();
        shopping.productNotSold();

    }


    // method to create Cart Table
    static void createCartTable() throws SQLException {
        String queryCartTable = "DROP TABLE IF EXISTS cart";
        s = con.createStatement();
        s.executeUpdate(queryCartTable);
        queryCartTable = "CREATE TABLE cart (qty int NOT NULL, pid int, FOREIGN KEY (pid) REFERENCES products(pid))";
        s = con.createStatement();
        s.executeUpdate(queryCartTable);
    }

    // method to create Product Table
    static void createProductTable() throws SQLException {
        String query = "USE Shopping";
        s = con.createStatement();
        s.executeUpdate(query);
        String queryProductTable = "DROP TABLE IF EXISTS cart";
        s = con.createStatement();
        s.executeUpdate(queryProductTable);
        queryProductTable = "DROP TABLE IF EXISTS products";
        s = con.createStatement();
        s.executeUpdate(queryProductTable);
        queryProductTable = "CREATE TABLE products (pid int NOT NULL auto_increment, pname varchar(100) NOT NULL, price int NOT NULL, CONSTRAINT product_pk PRIMARY KEY (pid))";
        s = con.createStatement();
        s.executeUpdate(queryProductTable);
    }

    // method to insert product records with first name as suffix in products table
    public void insertProductAndDisplay() throws SQLException {
        createProductTable();
        String query = "USE Shopping";
        st = con.prepareStatement(query);

        // prepared batch records list
        int[] PID = new int[] {1, 2, 3, 4, 5, 6};
        String[] ProductName = new String[] {"Reynolds 405", "HB Draw Pencil", "Crayons Rainbow", "Notebook", "Draw Sheet", "Brush Air"};
        int[] Productprice = new int[]{8, 10, 12, 14, 16, 5};

        query = "INSERT INTO products VALUES (?,?,?)";

        st = con.prepareStatement(query);
        for (int i = 0; i < ProductName.length; i++) {
            st.setInt(1, PID[i]);
            st.setString(2,ProductName[i].concat(SUFFIX));
            st.setInt(3,Productprice[i]);
            st.addBatch();
        }

        try{
            st.executeBatch();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Commit the transaction to close the COPY command
        con.commit();


        String queryAll = "SELECT * FROM products";

        // stores all data of table to view in properly
        rs = st.executeQuery(queryAll);

        System.out.println("******************* Products Inserted with suffix ****************");
        while (rs.next()){
            // Retrieve by column name
            System.out.print("pID: " + rs.getInt("pid"));
            System.out.print(" pName: " + rs.getString("pname"));
            System.out.print(" Price: " + rs.getInt("price"));
            System.out.println("");
        }

    }

    // method to insert products qty in cart table and display cart
    public void insertAndDisplayCart() throws SQLException {
        createCartTable();
        s = con.createStatement();
        String query = "INSERT INTO cart (qty, pid) VALUES (2, (SELECT pid FROM products WHERE pid = 2))";
        s.executeUpdate(query);
        query = "INSERT INTO cart (qty, pid) VALUES (3, (SELECT pid FROM products WHERE pid = 5))";
        s.executeUpdate(query);
        query = "INSERT INTO cart (qty, pid) VALUES (1, (SELECT pid FROM products WHERE pid = 4))";
        s.executeUpdate(query);

        con.commit();


        String queryAll = "SELECT * FROM cart";
        rs = s.executeQuery(queryAll);

        System.out.println("******************* Cart ****************");
        while (rs.next()){
            System.out.print("pID: " + rs.getInt("pid"));
            System.out.print(" Qty: " + rs.getInt("qty"));
            System.out.println("");
        }


    }
    // method to show total amount of the product in cart
    public void TotalAmount() {
        try {
            String query="select products.pid, products.pname,cart.qty,cart.qty*products.price as Total from products,cart where products.pid= cart.pid";
            st = con.prepareStatement(query);
            rs = st.executeQuery();
            System.out.println("****************** Cart Items Total *****************");
            while (rs.next()) {
                System.out.print("pID: " + rs.getInt("pid"));
                System.out.print(" pName: " + rs.getString("pname"));
                System.out.print(" Qty: " + rs.getInt("qty"));
                System.out.print(" Total: " + rs.getString("Total"));
                System.out.println("");
            }

            System.out.println();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // method to show total checkout price in cart
    public void totalCart() throws SQLException {
        String query = "select SUM(qty * products.price) from Cart LEFT JOIN products ON cart.pid = products.pid";
        st = con.prepareStatement(query);
        rs = st.executeQuery(query);
        System.out.println("******************* Total Checkout Price ****************");
        while (rs.next()) {
            System.out.println("Total: " + rs.getInt(1));
        }

    }

    // method to show products that sold the most
    public void productSoldMost() throws SQLException {
        String query = "select products.pid, products.pname, cart.Qty from products,cart where products.pid= cart.pid order by Qty desc";

        st = con.prepareStatement(query);
        rs = st.executeQuery(query);

        System.out.println("******************* Products Most Sold ****************");
        while (rs.next()){
            System.out.print("pID: " + rs.getInt("pid"));
            System.out.print(" pName: " + rs.getString("pname"));
            System.out.print(" Qty: " + rs.getInt("qty"));
            System.out.println("");
        }

    }

    // method to show the products that does not sold
    public void productNotSold() throws SQLException {
        String query = "select * from products where pid not in (select pid from cart)";

        st = con.prepareStatement(query);
        rs = st.executeQuery(query);

        System.out.println("******************* Products Not Sold ****************");
        while (rs.next()){
            System.out.print("pID: " + rs.getInt("pid"));
            System.out.print(" pName: " + rs.getString("pname"));
            System.out.print(" Price: " + rs.getInt("price"));
            System.out.println("");
        }

    }

}
