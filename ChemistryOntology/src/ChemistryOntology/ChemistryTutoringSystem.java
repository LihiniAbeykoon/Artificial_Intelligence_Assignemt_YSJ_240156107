package ChemistryOntology;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

public class ChemistryTutoringSystem extends JFrame {

   private JComboBox<String> topicComboBox;
   private JButton fetchButton, loginButton, registerButton, logoutButton;
   private JTextPane resultPane;
   private JScrollPane scrollPane;
   private JPanel loginPanel, registerPanel, mainPanel;
   private JTextField usernameField, registerUsernameField;
   private JPasswordField passwordField, registerPasswordField;
   private CardLayout cardLayout;
   private JPanel containerPanel;

   private static final String OWL_FILE = "Chemistry.owl";
   private static final String BASE_URI = "http://www.owl-ontologies.com/OntologyChemistry.owl#";
   private static final String USER_FILE = "users.txt";

   public ChemistryTutoringSystem() {
      initComponents();
      this.setLocationRelativeTo(null);
      this.setTitle("Chemistry Intelligent Tutoring System");
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setSize(600, 500);
   }

   private void initComponents() {
      cardLayout = new CardLayout();
      containerPanel = new JPanel(cardLayout);

      loginPanel = createLoginPanel();
      registerPanel = createRegisterPanel();
      mainPanel = createMainPanel();

      containerPanel.add(loginPanel, "LoginPanel");
      containerPanel.add(registerPanel, "RegisterPanel");
      containerPanel.add(mainPanel, "MainPanel");

      this.add(containerPanel);
   }

   private JPanel createLoginPanel() {
      JPanel panel = new JPanel(null);
      panel.setBackground(new Color(200, 230, 250));

      JLabel titleLabel = new JLabel("Login to Chemistry Tutor");
      titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
      titleLabel.setBounds(200, 50, 300, 30);
      panel.add(titleLabel);

      JLabel userLabel = new JLabel("Username:");
      userLabel.setBounds(150, 150, 100, 25);
      panel.add(userLabel);

      usernameField = new JTextField();
      usernameField.setBounds(250, 150, 200, 25);
      panel.add(usernameField);

      JLabel passLabel = new JLabel("Password:");
      passLabel.setBounds(150, 200, 100, 25);
      panel.add(passLabel);

      passwordField = new JPasswordField();
      passwordField.setBounds(250, 200, 200, 25);
      panel.add(passwordField);

      loginButton = new JButton("Login");
      loginButton.setBounds(250, 250, 100, 30);
      loginButton.addActionListener(e -> validateLogin());
      panel.add(loginButton);

      registerButton = new JButton("Register");
      registerButton.setBounds(370, 250, 100, 30);
      registerButton.addActionListener(e -> cardLayout.show(containerPanel, "RegisterPanel"));
      panel.add(registerButton);

      return panel;
   }

   private JPanel createRegisterPanel() {
      JPanel panel = new JPanel(null);
      panel.setBackground(new Color(230, 240, 250));

      JLabel titleLabel = new JLabel("Register New User");
      titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
      titleLabel.setBounds(200, 50, 300, 30);
      panel.add(titleLabel);

      JLabel userLabel = new JLabel("Username:");
      userLabel.setBounds(150, 150, 100, 25);
      panel.add(userLabel);

      registerUsernameField = new JTextField();
      registerUsernameField.setBounds(250, 150, 200, 25);
      panel.add(registerUsernameField);

      JLabel passLabel = new JLabel("Password:");
      passLabel.setBounds(150, 200, 100, 25);
      panel.add(passLabel);

      registerPasswordField = new JPasswordField();
      registerPasswordField.setBounds(250, 200, 200, 25);
      panel.add(registerPasswordField);

      JButton createButton = new JButton("Create Account");
      createButton.setBounds(250, 250, 150, 30);
      createButton.addActionListener(e -> registerUser());
      panel.add(createButton);

      return panel;
   }

   private JPanel createMainPanel() {
      JPanel panel = new JPanel(null);

      JLabel titleLabel = new JLabel("Chemistry Intelligent Tutoring System");
      titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
      titleLabel.setBounds(150, 20, 400, 30);
      panel.add(titleLabel);

      JLabel topicLabel = new JLabel("Select Chemistry Topic:");
      topicLabel.setBounds(100, 80, 200, 25);
      panel.add(topicLabel);

      topicComboBox = new JComboBox<>(new String[]{"AcidsAndBases", "PeriodicTable", "ChemicalReactions"});
      topicComboBox.setBounds(300, 80, 200, 25);
      panel.add(topicComboBox);

      fetchButton = new JButton("Fetch Example");
      fetchButton.setBounds(250, 130, 150, 30);
      fetchButton.addActionListener(evt -> fetchExamples());
      panel.add(fetchButton);

      resultPane = new JTextPane();
      scrollPane = new JScrollPane(resultPane);
      scrollPane.setBounds(100, 200, 400, 200);
      panel.add(scrollPane);

      return panel;
   }

   private void fetchExamples() {
      String selectedTopic = topicComboBox.getSelectedItem().toString();
      String query = "PREFIX chem:<" + BASE_URI + "> PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> "
              + "SELECT ?conceptLabel ?exampleLabel ?answer WHERE { "
              + "chem:" + selectedTopic + " chem:hasConcept ?concept . ?concept rdfs:label ?conceptLabel . "
              + "OPTIONAL { ?concept chem:hasExample ?example . ?example rdfs:label ?exampleLabel ; chem:hasAnswer ?answer } }";

      try {
         OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
         model.read(FileManager.get().open(OWL_FILE), BASE_URI);
         ResultSet results = QueryExecutionFactory.create(QueryFactory.create(query), model).execSelect();

         resultPane.setText("");
         while (results.hasNext()) {
            QuerySolution sol = results.nextSolution();
            resultPane.setText("Concept: " + sol.get("conceptLabel").toString()
                    + "\nExample: " + sol.get("exampleLabel").toString()
                    + "\nAnswer: " + sol.get("answer").toString());
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(() -> new ChemistryTutoringSystem().setVisible(true));
   }

   private void registerUser() {
      // Registration logic here
      String username = registerUsernameField.getText().trim();
      String password = new String(registerPasswordField.getPassword()).trim();

      // Input validation
      if (username.isEmpty() || password.isEmpty()) {
         JOptionPane.showMessageDialog(this, "Username and Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
         return;
      }

      try ( BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true));  Scanner scanner = new Scanner(new File(USER_FILE))) {

         // Check if the username already exists
         while (scanner.hasNextLine()) {
            String[] user = scanner.nextLine().split(",");
            if (user[0].equals(username)) {
               JOptionPane.showMessageDialog(this, "Username already exists. Please try another.", "Error", JOptionPane.ERROR_MESSAGE);
               return;
            }
         }

         // Write the new user credentials to the file
         writer.write(username + "," + password + "\n");
         JOptionPane.showMessageDialog(this, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
         cardLayout.show(containerPanel, "LoginPanel");
      } catch (IOException e) {
         JOptionPane.showMessageDialog(this, "Error saving user data.", "Error", JOptionPane.ERROR_MESSAGE);
      }
   }

   private void validateLogin() {
      // Login validation logic here
      String username = usernameField.getText().trim();
      String password = new String(passwordField.getPassword()).trim();

      if (username.isEmpty() || password.isEmpty()) {
         JOptionPane.showMessageDialog(this, "Username and Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
         return;
      }

      try ( Scanner scanner = new Scanner(new File(USER_FILE))) {
         while (scanner.hasNextLine()) {
            String[] user = scanner.nextLine().split(",");
            if (user[0].equals(username) && user[1].equals(password)) {
               JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
               cardLayout.show(containerPanel, "MainPanel");
               return;
            }
         }
         JOptionPane.showMessageDialog(this, "Invalid Username or Password.", "Error", JOptionPane.ERROR_MESSAGE);
      } catch (FileNotFoundException e) {
         JOptionPane.showMessageDialog(this, "User database not found. Please register first.", "Error", JOptionPane.ERROR_MESSAGE);
      }
   }
}
