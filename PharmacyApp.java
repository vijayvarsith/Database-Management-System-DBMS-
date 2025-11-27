package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;

public class PharmacyApp extends JFrame {

    private Connection conn;

    public PharmacyApp() {
        // Connect to DB
        conn = getConnection();

        setTitle("💊 Pharmacy Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Global font for emoji support
        UIManager.put("defaultFont", new Font("Segoe UI Emoji", Font.PLAIN, 13));

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("\uD83D\uDC8A Medicines", medicinePanel());
        tabs.add("\uD83D\uDC64 Customers", customerPanel());
        tabs.add("\uD83D\uDCB5 Sales", salesPanel());

        add(tabs, BorderLayout.CENTER);
        setVisible(true);
    }

    // ✅ Database Connection
    private Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/pharmacydb";
            String user = "root";
            String pass = "root"; // change this
            return DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
            System.exit(1);
            return null;
        }
    }

    // ✅ Medicines Module
    private JPanel medicinePanel() {
        JPanel base = new JPanel(new BorderLayout(15, 15));
        base.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Name", "Company", "Price", "Quantity", "Expiry"}, 0);
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel card = createCardPanel();
        card.add(scroll, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setOpaque(false);
        JTextField name = new JTextField(), company = new JTextField(), price = new JTextField(),
                qty = new JTextField(), expiry = new JTextField();
        JButton addBtn = createStyledButton("Add", new Color(0x1E90FF));
        JButton deleteBtn = createStyledButton("Delete Selected", new Color(0xE81123));

        form.add(new JLabel("Name:")); form.add(name);
        form.add(new JLabel("Company:")); form.add(company);
        form.add(new JLabel("Price:")); form.add(price);
        form.add(new JLabel("Quantity:")); form.add(qty);
        form.add(new JLabel("Expiry (YYYY-MM-DD):")); form.add(expiry);
        form.add(addBtn); form.add(deleteBtn);

        card.add(form, BorderLayout.SOUTH);
        base.add(card, BorderLayout.CENTER);

        loadMedicines(model);

        addBtn.addActionListener(e -> {
            try {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO medicines (med_name, company, price, quantity, expiry_date) VALUES (?, ?, ?, ?, ?)");
                ps.setString(1, name.getText());
                ps.setString(2, company.getText());
                ps.setDouble(3, Double.parseDouble(price.getText()));
                ps.setInt(4, Integer.parseInt(qty.getText()));
                ps.setString(5, expiry.getText());
                ps.executeUpdate();
                loadMedicines(model);
                JOptionPane.showMessageDialog(this, "Medicine Added!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) model.getValueAt(row, 0);
                try {
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM medicines WHERE med_id = ?");
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    loadMedicines(model);
                    JOptionPane.showMessageDialog(this, "Deleted Successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        return base;
    }

    private void loadMedicines(DefaultTableModel model) {
        model.setRowCount(0);
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM medicines")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("med_id"),
                        rs.getString("med_name"),
                        rs.getString("company"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getString("expiry_date")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Customers Module
    private JPanel customerPanel() {
        JPanel base = new JPanel(new BorderLayout(15, 15));
        base.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Name", "Phone", "Address"}, 0);
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setShowGrid(false);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel card = createCardPanel();
        card.add(scroll, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.setOpaque(false);
        JTextField name = new JTextField(), phone = new JTextField(), address = new JTextField();
        JButton addBtn = createStyledButton("Add", new Color(0x00B894));
        JButton deleteBtn = createStyledButton("Delete Selected", new Color(0xE81123));

        form.add(new JLabel("Name:")); form.add(name);
        form.add(new JLabel("Phone:")); form.add(phone);
        form.add(new JLabel("Address:")); form.add(address);
        form.add(addBtn); form.add(deleteBtn);

        card.add(form, BorderLayout.SOUTH);
        base.add(card, BorderLayout.CENTER);

        loadCustomers(model);

        addBtn.addActionListener(e -> {
            try {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO customers (cust_name, phone, address) VALUES (?, ?, ?)");
                ps.setString(1, name.getText());
                ps.setString(2, phone.getText());
                ps.setString(3, address.getText());
                ps.executeUpdate();
                loadCustomers(model);
                JOptionPane.showMessageDialog(this, "Customer Added!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) model.getValueAt(row, 0);
                try {
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM customers WHERE cust_id = ?");
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    loadCustomers(model);
                    JOptionPane.showMessageDialog(this, "Deleted Successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        return base;
    }

    private void loadCustomers(DefaultTableModel model) {
        model.setRowCount(0);
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM customers")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("cust_id"),
                        rs.getString("cust_name"),
                        rs.getString("phone"),
                        rs.getString("address")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Sales Placeholder
    private JPanel salesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("💰 Sales functionality coming soon...", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    // ✅ Helper Methods
    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));

        // Add simple hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });

        return btn;
    }

    private JPanel createCardPanel() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(new Color(45, 45, 45));
        return card;
    }

    // ✅ Main
    public static void main(String[] args) {
        FlatAnimatedLafChange.showSnapshot();
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            UIManager.put("Component.arc", 15);
            UIManager.put("Button.arc", 20);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("defaultFont", new Font("Segoe UI Emoji", Font.PLAIN, 13));
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        FlatAnimatedLafChange.hideSnapshotWithAnimation();

        SwingUtilities.invokeLater(PharmacyApp::new);
    }
}
