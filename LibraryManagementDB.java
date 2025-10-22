import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class LibraryManagementDB extends JFrame implements ActionListener {

    private JTextField textField1, textField2, textField3, textField4, textField5, textField6, textField7;
    private JButton addButton, viewButton, editButton, deleteButton, issueButton, returnButton, clearButton, exitButton;
    // New button variable added here
    private JButton viewIssuedButton; 
    private Connection conn;

    public LibraryManagementDB() {
        setTitle("📚 Library Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(1000, 650);
        setLocationRelativeTo(null);

        // Nimbus look
        try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); } catch(Exception ignored){}

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(30, 144, 255));
        JLabel titleLabel = new JLabel("Library Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2, true),
                "Book Management", 0, 0,
                new Font("Segoe UI", Font.BOLD, 22), Color.DARK_GRAY
        ));
        add(mainPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel[] labels = {new JLabel("Book ID:"), new JLabel("Title:"), new JLabel("Author:"),
                new JLabel("Publisher:"), new JLabel("Year:"), new JLabel("ISBN:"), new JLabel("Copies:")};

        JTextField[] fields = {
                textField1 = new JTextField(),
                textField2 = new JTextField(),
                textField3 = new JTextField(),
                textField4 = new JTextField(),
                textField5 = new JTextField(),
                textField6 = new JTextField(),
                textField7 = new JTextField()
        };

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font textFont = new Font("Segoe UI", Font.PLAIN, 16);
        for(int i=0;i<labels.length;i++){
            gbc.gridx=0; gbc.gridy=i;
            labels[i].setFont(labelFont);
            mainPanel.add(labels[i], gbc);
            gbc.gridx=1;
            fields[i].setFont(textFont);
            mainPanel.add(fields[i], gbc);
        }

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        addButton = new JButton("Add Book");
        viewButton = new JButton("View Books");
        editButton = new JButton("Edit Book");
        deleteButton = new JButton("Delete Book");
        issueButton = new JButton("Issue Book");
        returnButton = new JButton("Return Book");
        clearButton = new JButton("Clear Fields");
        exitButton = new JButton("Exit");
        // New button initialized here
        viewIssuedButton = new JButton("View Issued Books");

        JButton[] buttons = {addButton, viewButton, editButton, deleteButton, issueButton, returnButton, clearButton, exitButton, viewIssuedButton};
        for(JButton b : buttons){
            b.setBackground(new Color(0,123,255));
            b.setForeground(Color.WHITE);
            b.setFont(new Font("Segoe UI", Font.BOLD, 16));
            b.setFocusPainted(false);
            b.setBorder(BorderFactory.createEmptyBorder(10,15,10,15));
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            b.addActionListener(this);
            buttonPanel.add(b);
        }

        add(buttonPanel, BorderLayout.SOUTH);

        connectDatabase();
        setVisible(true);
    }

    private void connectDatabase(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/librarydb","root","Diya@2006");
            System.out.println("✅ Connected to Database");
        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"❌ DB Connection Failed: "+e.getMessage());
        }
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource()==addButton) addBook();
        else if(e.getSource()==viewButton) viewBooks();
        else if(e.getSource()==editButton) editBook();
        else if(e.getSource()==deleteButton) deleteBook();
        else if(e.getSource()==issueButton) issueBook();
        else if(e.getSource()==returnButton) returnBook();
        else if(e.getSource()==clearButton) clearFields();
        else if(e.getSource()==exitButton) System.exit(0);
        // New button action added here
        else if(e.getSource()==viewIssuedButton) viewIssuedBooks();
    }

    private void addBook(){
        try{
            String sql="INSERT INTO books VALUES(?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,textField1.getText());
            ps.setString(2,textField2.getText());
            ps.setString(3,textField3.getText());
            ps.setString(4,textField4.getText());
            ps.setString(5,textField5.getText());
            ps.setString(6,textField6.getText());
            ps.setInt(7,Integer.parseInt(textField7.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"✅ Book Added Successfully");
            clearFields();
        }catch(SQLIntegrityConstraintViolationException ex){
            JOptionPane.showMessageDialog(this,"⚠ Book ID Already Exists!");
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,"❌ Error: "+ex.getMessage());
        }
    }

    private void viewBooks(){
        try{
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM books");
            String[] columns = {"Book ID","Title","Author","Publisher","Year","ISBN","Copies"};
            DefaultTableModel model = new DefaultTableModel(columns,0);
            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getString("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        rs.getString("year_of_publication"),
                        rs.getString("isbn"),
                        rs.getInt("copies")
                });
            }
            JTable table = new JTable(model);
            table.setRowHeight(25);
            table.setFont(new Font("Segoe UI",Font.PLAIN,14));
            table.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,16));

            JFrame frame = new JFrame("📚 All Books");
            frame.setSize(900,400);
            frame.setLocationRelativeTo(this);
            frame.add(new JScrollPane(table));
            frame.setVisible(true);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,"❌ Error Viewing Books: "+ex.getMessage());
        }
    }

    private void editBook(){
        String id = JOptionPane.showInputDialog("Enter Book ID to edit:");
        if(id==null) return;
        try{
            String sql="UPDATE books SET title=?, author=?, publisher=?, year_of_publication=?, isbn=?, copies=? WHERE book_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,textField2.getText());
            ps.setString(2,textField3.getText());
            ps.setString(3,textField4.getText());
            ps.setString(4,textField5.getText());
            ps.setString(5,textField6.getText());
            ps.setInt(6,Integer.parseInt(textField7.getText()));
            ps.setString(7,id);
            int rows = ps.executeUpdate();
            if(rows>0) JOptionPane.showMessageDialog(this,"✏ Book Updated Successfully!");
            else JOptionPane.showMessageDialog(this,"⚠ Book ID Not Found!");
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,"❌ Error Editing Book: "+ex.getMessage());
        }
    }

    private void deleteBook(){
        String id = JOptionPane.showInputDialog("Enter Book ID to delete:");
        if(id==null) return;
        try{
            String sql="DELETE FROM books WHERE book_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,id);
            int rows = ps.executeUpdate();
            if(rows>0) JOptionPane.showMessageDialog(this,"🗑 Book Deleted Successfully!");
            else JOptionPane.showMessageDialog(this,"⚠ Book ID Not Found!");
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,"❌ Error Deleting Book: "+ex.getMessage());
        }
    }

    private void issueBook(){
        String bookID = JOptionPane.showInputDialog("Enter Book ID to issue:");
        String student = JOptionPane.showInputDialog("Enter Student Name:");
        if(bookID==null || student==null) return;
        try{
            PreparedStatement check = conn.prepareStatement("SELECT copies FROM books WHERE book_id=?");
            check.setString(1,bookID);
            ResultSet rs = check.executeQuery();
            if(rs.next()){
                int copies = rs.getInt("copies");
                if(copies>0){
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO issued_books(book_id,student_name,issue_date) VALUES(?,?,?)");
                    ps.setString(1,bookID);
                    ps.setString(2,student);
                    ps.setDate(3,java.sql.Date.valueOf(LocalDate.now()));
                    ps.executeUpdate();

                    PreparedStatement update = conn.prepareStatement("UPDATE books SET copies=copies-1 WHERE book_id=?");
                    update.setString(1,bookID);
                    update.executeUpdate();

                    JOptionPane.showMessageDialog(this,"✅ Book Issued Successfully!");
                }else JOptionPane.showMessageDialog(this,"⚠ No Copies Available!");
            }else JOptionPane.showMessageDialog(this,"⚠ Book Not Found!");
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,"❌ Error Issuing Book: "+ex.getMessage());
        }
    }

    private void returnBook(){
        String bookID = JOptionPane.showInputDialog("Enter Book ID to return:");
        String student = JOptionPane.showInputDialog("Enter Student Name:");
        if(bookID==null || student==null) return;
        try{
            PreparedStatement ps = conn.prepareStatement("UPDATE issued_books SET return_date=? WHERE book_id=? AND student_name=? AND return_date IS NULL");
            ps.setDate(1,java.sql.Date.valueOf(LocalDate.now()));
            ps.setString(2,bookID);
            ps.setString(3,student);
            int rows = ps.executeUpdate();
            if(rows>0){
                PreparedStatement update = conn.prepareStatement("UPDATE books SET copies=copies+1 WHERE book_id=?");
                update.setString(1,bookID);
                update.executeUpdate();
                JOptionPane.showMessageDialog(this,"✅ Book Returned Successfully!");
            }else JOptionPane.showMessageDialog(this,"⚠ No Matching Issue Record Found!");
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,"❌ Error Returning Book: "+ex.getMessage());
        }
    }

    private void clearFields(){
        for(JTextField t : new JTextField[]{textField1,textField2,textField3,textField4,textField5,textField6,textField7})
            t.setText("");
    }
    
    // New method added to view issued books
    private void viewIssuedBooks(){
        try{
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM issued_books WHERE return_date IS NULL");
            String[] columns = {"ID", "Book ID", "Student Name", "Issue Date"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);

            while(rs.next()){
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("book_id"),
                    rs.getString("student_name"),
                    rs.getDate("issue_date")
                });
            }
            
            JTable table = new JTable(model);
            table.setRowHeight(25);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));

            JFrame frame = new JFrame("📚 Issued Books");
            frame.setSize(700, 400);
            frame.setLocationRelativeTo(this);
            frame.add(new JScrollPane(table));
            frame.setVisible(true);

        } catch(Exception ex){
            JOptionPane.showMessageDialog(this, "❌ Error Viewing Issued Books: " + ex.getMessage());
        }
    }

    public static void main(String[] args){
        new LibraryManagementDB();
    }
}
//javac -cp ".;mysql-connector-j-9.4.0.jar" LibraryManagementDB.java(1st command to compile)
//java -cp ".;mysql-connector-j-9.4.0.jar" LibraryManagementDB(2nd command)