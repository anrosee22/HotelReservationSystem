package HotelReservation;

//import com.sun.tools.javac.util.Convert;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
//import sun.util.calendar.BaseCalendar;
//import sun.util.calendar.LocalGregorianCalendar;
//import sun.java2d.pipe.SpanShapeRenderer;

//import javax.xml.soap.Text;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Locale;
import java.text.DateFormat;


public class MainController {

    public Button NewCustomer;
    public Button NewReservation;
    public Label CustomerName;
    public TextField txtCustomerName;
    public Label CustomerEmail;
    public TextField txtCustomerEmail;

    public AnchorPane rootPane;
    public Label CardNo;
    public Label CardExpiry;
    public TextField txtCardNo;
    public DatePicker txtCardExpiry;
    public Button AddCustomer;
    public TextField txtCustomerEmailR;
    public ChoiceBox txtNoPeople;
    //public TextField roomSelect;
    public Label txtroomPrice;
    public Button AddReservation;
    public DatePicker txtcheckIn;
    public DatePicker txtCheckout;
    public TextField extraName;
    public DatePicker extraDate;  //added extra date
    public TextField txtExtraNo;     //added extra Number
    public ComboBox extradescription;
    public TextField extraAmount;
    public Button CanceladdExtra;
    public Button addExtraDetails;
    public Label ReservationNum;
    public TextField txtfindReservation;
    public Button AddExtra;
    public Button findRoom;
    public Button DeleteExtras;
    public Button DeleteReservation;
    public Button SaveEditReservation;
    public TextField txtfindCustomer;
    public DatePicker txtCheckIn;
    public DatePicker txtcheckout;
    public TextField RegSetRoomNo;
    public TextField findroomSelect;
    public Button findRoomButton;
    public Button RegisterRoom;
    public ChoiceBox RegSetRoomType;
    public ChoiceBox editRoomSelect;
    public Button editFindRoom;
    public TextField RegPriceperNight;
    public ChoiceBox txtRoomType;
    public ChoiceBox roomSelect;
    public Label txtCustomerEmailRC;
    public Label txtRoomTypeC;
    public Label txtcheckInC;
    public Label roomSelectC;
    public Label roomprice;
    public Label Nights;
    public Label roompricetotal;
    public Label Extra1desc;
    public Label Extra1price;
    public Label Extra1date;
    public Label Extra2desc;
    public Label Extra2price;
    public Label Extra2date;
    public Label Extra3desc;
    public Label Extra3price;
    public Label Extra3date;
    public Label Extra4desc;
    public Label Extra4price;
    public Label Extra4date;
    public Label total;
    public Label Extra1ID;
    public Label Extra2ID;
    public Label Extra3ID;
    public Label Extra4ID;
    public ComboBox<Integer> findExtra;
    public Label txtCheckoutC;
    public Label updateRoomNo;
    public TextField findtxtRoomNo;
    public Label ExtraNum;
    public Label txtRoomNoC;
    public TextField txtCustomerCountry;
    public TextField txtCustomerTown;
    public TextField txtCustomerPostcode;
    public Label chargesCharge;
    public Label chargesPrice;

    PreparedStatement pst = null;
    Connection conn = null;
    ResultSet rs = null;
    static int Num = 0;
    private boolean next;
    static int currentReservation = -1;
    static int currentExtraNum = -1;


    //manage customers page
    public void ManageCustomers(javafx.event.ActionEvent event) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("ManageCustomers.fxml"));
        Scene Scene = new Scene(pane);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("Manage Customers");
        window.setScene(Scene);
        window.show();
    }

    //change to Customer Registration page
    public void NewCustomer(javafx.event.ActionEvent event) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("NewCustomer.fxml"));
        Scene CustomerScene = new Scene(pane);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("Customer Registration");
        window.setScene(CustomerScene);
        window.show();
    }

    //add customer details to database
    public void AddCustomer() {
        SqlController sc = new SqlController();
        //check null values are not entered
        if (txtCardExpiry.getValue() == null || txtCustomerName.getText().isEmpty() || txtCustomerCountry.getText().isEmpty() || txtCustomerTown.getText().isEmpty() || txtCustomerPostcode.getText().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("All fields must be filled!");
            a.show();
        }
        //check customer email is correct format
        else if (txtCustomerEmail.getText().contains("@") && txtCustomerEmail.getText().contains(".")) {
            try {
                //make sure card number is an int
                int card_number = Integer.parseInt(txtCardNo.getText());

                //check if date of card expiry is before hotel date
                if (java.sql.Date.valueOf(txtCardExpiry.getValue()).before(new Date())) {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Card expiry date must not be before Current Date!");
                    a.show();
                    txtCardExpiry.setValue(null);
                    //check card number length is valid
                } else if (txtCardNo.getLength() > 16) {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Card Number is invalid");
                    a.show();
                } else {
                    try {
                        //check that email has not already been registered as a customer
                        conn = sc.openConnection();
                        String sql = "SELECT * from customer WHERE customer_email=?";
                        pst = conn.prepareStatement(sql);
                        pst.setString(1, txtCustomerEmail.getText());
                        rs = pst.executeQuery();
                        if (rs.next()) {
                            Alert a = new Alert(Alert.AlertType.ERROR);
                            a.setContentText("Email is already in use!");
                            a.show();
                            txtCustomerEmail.clear();
                            sc.closeConnection();
                        } else {
                            // add customer to database
                            try {
                                conn = sc.openConnection();
                                String query = "INSERT INTO customer (customer_name,customer_address,card_number,customer_email,card_Expiry_Date) VALUES(?,?,?,?,?)";
                                pst = conn.prepareStatement(query);


                                pst.setString(1, txtCustomerName.getText()); //Customer Name txtCustomerName.getText()
                                pst.setString(2, formatAddress()); //Customer Address txtCustomerAddress.getText()
                                pst.setInt(3, Integer.parseInt(txtCardNo.getText())); //Card Number
                                pst.setString(4, txtCustomerEmail.getText()); //Customer email txtCustomerEmailR.getText()
                                pst.setDate(5, java.sql.Date.valueOf(txtCardExpiry.getValue())); //expiry date of card

                                //execute update
                                pst.executeUpdate();
                                sc.closeConnection();

                                Alert a = new Alert(Alert.AlertType.CONFIRMATION);
                                a.setContentText("Customer Added!");
                                if (a.showAndWait().isPresent() && a.getResult() == ButtonType.OK) {
                                    txtCustomerName.clear();
                                    //clear address
                                    txtCustomerCountry.clear();
                                    txtCustomerTown.clear();
                                    txtCustomerPostcode.clear();


                                    txtCardNo.clear();
                                    txtCardExpiry.setValue(null);
                                    txtCustomerEmail.clear();
                                }
                            } catch (SQLException e) {
                                System.out.print(e);
                                sc.closeConnection();
                            }
                        }
                    } catch (SQLException e) {
                        System.out.print(e);
                        sc.closeConnection();
                    }
                }
            } catch (NumberFormatException nfe) {   //copy
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Card number is invalid!");
                a.show();
            }
        } else {
            Alert j = new Alert(Alert.AlertType.ERROR);
            j.setContentText("Email is invalid!");
            j.show();
        }

    }

    //change to New Reservation Page
    public void NewReservation(javafx.event.ActionEvent event) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("NewReservation.fxml"));
        Scene CustomerScene = new Scene(pane);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(CustomerScene);
        window.setTitle("New Reservation");
        window.show();
    }

    //add reservation to database
    public void AddReservation() {
        SqlController sc = new SqlController();
        //random number for Reservation Number
        Random rand = new Random();
        int upperbound = 1000000;
        int int_random = rand.nextInt(upperbound);
        currentReservation = int_random; //this is used for finding the Current Reservation on other parts
        //checking that values entered are valid type
        try {
            //int Roomnum = (int) roomSelect.getValue();
            String email = txtCustomerEmailR.getText();
            LocalDate yesterday = LocalDate.now().minusDays(1);
            Date y = Date.from(yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant());
            try {
                conn = sc.openConnection();
                pst = conn.prepareStatement("SELECT * from customer WHERE customer_email=?");
                pst.setString(1, email);
                rs = pst.executeQuery();
                //if customer exists
                if (rs.next()) {
                    if (txtcheckIn.getValue() == null || txtCheckout.getValue() == null) {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setContentText("All fields must be filled!");
                        a.show();
                    }
                    //check check in date is not before current date
                    else if (java.sql.Date.valueOf(txtcheckIn.getValue()).before(y)) {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setContentText("Check-In date must be after the current Date!");
                        a.show();
                        txtcheckIn.setValue(LocalDate.now());
                        //check that check out date is not before check in date
                    } else if (java.sql.Date.valueOf(txtCheckout.getValue()).before(java.sql.Date.valueOf(txtcheckIn.getValue()))) {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setContentText("Check-Out date must be after the Check-In Date!");
                        a.show();
                        txtCheckout.setValue(LocalDate.now().plusDays(1));
                    }else if(txtCustomerEmailR.getText() == null){
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setContentText("All fields must be filled!");
                        a.show();
                    }
                    else {
                        try {
                            //checking that room number exists
                            pst = conn.prepareStatement("SELECT * from room WHERE roomno=?");
                            pst.setInt(1, Integer.parseInt(String.valueOf(roomSelect.getValue())));
                            rs = pst.executeQuery();
                            if (rs.next()) {
                                //checking that room number is available to be reserved on that date
                                pst = conn.prepareStatement("SELECT * from Reservation WHERE roomno=?");
                                pst.setInt(1, Integer.parseInt(String.valueOf(roomSelect.getValue())));
                                rs = pst.executeQuery();
                                //if room number is in use check that dates are different
                                if (rs.next()) {
                                    if (java.sql.Date.valueOf(txtCheckout.getValue()).after(rs.getDate("dateFrom"))
                                            && (java.sql.Date.valueOf(txtcheckIn.getValue()).before(rs.getDate("dateTo")))) {
                                        Alert a = new Alert(Alert.AlertType.ERROR);
                                        a.setContentText("Room No is Already Reserved for that date");
                                        a.show();
                                        sc.closeConnection();
                                        //if room number is used in different dates create reservation
                                    } else {
                                        try {
                                            //setting price of room
                                            pst = conn.prepareStatement("SELECT * from room WHERE roomno=?");
                                            pst.setInt(1, Integer.parseInt(String.valueOf(roomSelect.getValue())));
                                            rs = pst.executeQuery();
                                            if (rs.next()) {
                                                txtroomPrice.setText(String.valueOf(rs.getDouble("pricepernight")));
                                            }

                                            String query = "INSERT INTO Reservation (reservationno,roomno,roomType,dateFrom,dateTo,price,customer_email) VALUES(?,?,?,?,?,?,?)";
                                            pst = conn.prepareStatement(query);

                                            pst.setInt(1, int_random);
                                            pst.setInt(2, Integer.parseInt(String.valueOf(roomSelect.getValue())));
                                            pst.setString(3, txtRoomType.getValue().toString());
                                            pst.setDate(4, java.sql.Date.valueOf(txtcheckIn.getValue()));
                                            pst.setDate(5, java.sql.Date.valueOf(txtCheckout.getValue()));
                                            pst.setDouble(6, Double.parseDouble(txtroomPrice.getText()));
                                            pst.setString(7, email);
                                            pst.executeUpdate();
                                            sc.closeConnection();

                                            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
                                            a.setContentText("Reservation Added!    Reservation Number: " + int_random);
                                            if (a.showAndWait().isPresent() && a.getResult() == ButtonType.OK) {
                                                txtroomPrice.setText("");
                                                roomSelect.getItems().clear();
                                                txtRoomType.setValue("Single");
                                                txtcheckIn.setValue(LocalDate.now());
                                                txtCheckout.setValue(LocalDate.now().plusDays(1));
                                                txtCustomerEmailR.clear();
                                            }
                                        } catch (SQLException e) {
                                            System.out.print(e);
                                            sc.closeConnection();
                                        }
                                    }
                                    //if the room number has not been used in another reservation add the reservation
                                } else {
                                    try {
                                        //setting price of room
                                        pst = conn.prepareStatement("SELECT * from room WHERE roomno=?");
                                        pst.setInt(1, Integer.parseInt(String.valueOf(roomSelect.getValue())));
                                        rs = pst.executeQuery();
                                        if (rs.next()) {
                                            txtroomPrice.setText(String.valueOf(rs.getDouble("pricepernight")));
                                        }

                                        String query = "INSERT INTO Reservation (reservationno,roomno,roomType,dateFrom,dateTo,price,customer_email) VALUES(?,?,?,?,?,?,?)";
                                        pst = conn.prepareStatement(query);

                                        pst.setInt(1, int_random);
                                        pst.setInt(2, Integer.parseInt(String.valueOf(roomSelect.getValue())));
                                        pst.setString(3, txtRoomType.getValue().toString());
                                        pst.setDate(4, java.sql.Date.valueOf(txtcheckIn.getValue()));
                                        pst.setDate(5, java.sql.Date.valueOf(txtCheckout.getValue()));
                                        pst.setDouble(6, Double.parseDouble(txtroomPrice.getText()));
                                        pst.setString(7, email);
                                        pst.executeUpdate();
                                        sc.closeConnection();

                                        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
                                        a.setContentText("Reservation Added!    Reservation Number: " + int_random);
                                        if (a.showAndWait().isPresent() && a.getResult() == ButtonType.OK) {
                                            txtroomPrice.setText("");
                                            roomSelect.getItems().clear();
                                            txtRoomType.setValue("Single");
                                            txtcheckIn.setValue(LocalDate.now());
                                            txtCheckout.setValue(LocalDate.now().plusDays(1));
                                            txtCustomerEmailR.clear();
                                        }
                                    } catch (SQLException e) {
                                        System.out.print(e);
                                        sc.closeConnection();
                                    }
                                }
                            } else {
                                Alert a = new Alert(Alert.AlertType.ERROR);
                                a.setContentText("Room No is not Registered in database");
                                a.show();
                                sc.closeConnection();
                                roomSelect.getItems().clear();
                            }
                        } catch (SQLException e) {
                            System.out.print(e);
                            sc.closeConnection();
                        }
                    }
                } else {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Customer does not exist!");
                    a.show();
                    txtCustomerEmailR.clear();
                }
            } catch (SQLException e) {
                System.out.print(e);
                sc.closeConnection();
            }
        } catch (NumberFormatException nfe) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Invalid values entered!");
            a.show();
            roomSelect.getItems().clear();
        }
    }

    //go back to main page
    public void Back(javafx.event.ActionEvent event) throws IOException {
        Num = 0;
        Parent pane = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
        Scene mainPage = new Scene(pane);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(mainPage);
        window.show();
    }

    //view reservation (not checked in)
    public void ViewReservation(javafx.event.ActionEvent event) throws IOException {
        SqlController sc = null;
        sc = new SqlController();
        String parse = txtfindReservation.getText();
        try {
            Integer.parseInt(parse);
            String reservation_Number = txtfindReservation.getText();
            currentReservation = Integer.parseInt(reservation_Number);//needed for other parts
            try {
                conn = sc.openConnection();
                String query = "SELECT * FROM Reservation  WHERE reservationno = '" + reservation_Number + "'";
                System.out.println(query);
                Statement stmt = conn.createStatement();

                rs = stmt.executeQuery(query);
                //if reservation exists and has not been checked-in load page and data
                if (rs.next()) {
                    if (rs.getTimestamp("check_in") == null) {
                        //load edit reservation page
                        Parent pane = FXMLLoader.load(getClass().getResource("ViewReservation.fxml"));
                        ((Label) pane.lookup("#ReservationNum")).setText("Reservation  #" + txtfindReservation.getText());
                        Scene ViewReservation = new Scene(pane);
                        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        window.setScene(ViewReservation);
                        window.show();

                        SqlController sc1 = null;
                        try {

                            sc = new SqlController();
                            conn = sc.openConnection();
                            pst = conn.prepareStatement("SELECT * FROM Reservation WHERE reservationno = '" + reservation_Number + "'");
                            rs = pst.executeQuery();

                            if (rs.next()) {
                                //get details from database and display
                                String roomno = rs.getString(2);
                                String roomType = rs.getString(3);
                                String dateFrom = rs.getString(4);
                                String dateTo = rs.getString(5);
                                String price = rs.getString(6);
                                String customerEmail = rs.getString(7);
                                System.out.println(roomno);
                                List<Integer> roomNo = new ArrayList<>();
                                roomNo.add(Integer.valueOf(roomno));

                                //txtCustomerEmailR.setText(customerEmail);
                                ((TextField) pane.lookup("#txtCustomerEmailR")).setText(customerEmail);
                                //txtNoPeople.setValue(noPeople);

                                ((ChoiceBox) pane.lookup("#txtRoomType")).setValue(roomType);
                                //roomSelect.setText(String.valueOf(roomno));
                                ((ChoiceBox) pane.lookup("#editRoomSelect")).setItems(FXCollections.observableList(roomNo));
                                ((ChoiceBox) pane.lookup("#editRoomSelect")).setValue(Integer.valueOf(roomno));

                                //txtcheckIn.setValue(LocalDate.parse((CharSequence) dateFrom));
                                Date d1 = null;
                                Date d2 = null;
                                try {
                                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                                    d1 = sdf1.parse(dateFrom);
                                    d2 = sdf1.parse(dateTo);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                ((DatePicker) pane.lookup("#txtCheckIn")).setValue(Instant.ofEpochMilli(d1.getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
                                //txtCheckout.setValue(LocalDate.parse((CharSequence) dateTo));
                                ((DatePicker) pane.lookup("#txtcheckout")).setValue(Instant.ofEpochMilli(d2.getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
                                //txtroomPrice.setText(String.valueOf(price));
                                ((Label) pane.lookup("#txtroomPrice")).setText(price);
                            }


                        } catch (SQLException e) {
                            System.out.print(e);
                            sc.closeConnection();
                        }


                        //if checked in load checked-in reservation page
                    } else if (rs.getTimestamp("check_in") != null && rs.getTimestamp("check_out") == null) {
                        Parent pane = FXMLLoader.load(getClass().getResource("ViewCheckedInReservation.fxml"));
                        ((Label) pane.lookup("#ReservationNum")).setText("Reservation  #" + txtfindReservation.getText());
                        Scene ViewCheckedInReservation = new Scene(pane);
                        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        window.setScene(ViewCheckedInReservation);
                        window.setTitle(" ");
                        window.show();

                        try {

                            sc = new SqlController();
                            conn = sc.openConnection();
                            pst = conn.prepareStatement("SELECT * FROM Reservation WHERE reservationno = '" + reservation_Number + "'");
                            rs = pst.executeQuery();

                            if (rs.next()) {
                                //get details from database and display
                                String roomno = rs.getString(2);
                                String roomType = rs.getString(3);
                                String dateFrom = rs.getString(4);
                                String dateTo = rs.getString(5);
                                String price = rs.getString(6);
                                String customerEmail = rs.getString(7);
                                String check_in = rs.getTimestamp("check_in").toString();
                                String check_out = rs.getDate("dateTo").toString();

                                //txtCustomerEmailR.setText(customerEmail);
                                ((Label) pane.lookup("#txtCustomerEmailRC")).setText(customerEmail);
                                //txtRoomType.setValue(noPeople);
                                ((Label) pane.lookup("#txtRoomTypeC")).setText(roomType);
                                //roomSelect.setText(String.valueOf(roomno));
                                ((Label) pane.lookup("#txtRoomNoC")).setText(roomno);
                                //txtcheckIn.setValue(LocalDate.parse((CharSequence) dateFrom));


                                ((Label) pane.lookup("#txtcheckInC")).setText(check_in.substring(0, check_in.length() - 5));
                                //txtCheckout.setValue(LocalDate.parse((CharSequence) dateTo));
                                ((Label) pane.lookup("#txtCheckoutC")).setText(check_out);
                                //txtroomPrice.setText(String.valueOf(price));
                                ((Label) pane.lookup("#txtroomPrice")).setText(price);
                            }


                        } catch (SQLException e) {
                            System.out.print(e);
                            sc.closeConnection();
                        }

                    } else {
                        //display checked-out reservation page
                        Parent pane = FXMLLoader.load(getClass().getResource("ViewCheckedOutRes.fxml"));
                        ((Label) pane.lookup("#ReservationNum")).setText("Reservation  #" + reservation_Number);
                        Scene ViewCheckedInReservation = new Scene(pane);
                        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        window.setScene(ViewCheckedInReservation);
                        window.setTitle(" ");
                        window.show();

                        try {
                            sc = new SqlController();
                            conn = sc.openConnection();
                            pst = conn.prepareStatement("SELECT * FROM Reservation WHERE reservationno = '" + reservation_Number + "'");
                            rs = pst.executeQuery();

                            if (rs.next()) {
                                //get details from database and display
                                String roomno = rs.getString(2);
                                String roomType = rs.getString(3);
                                String customerEmail = rs.getString(7);
                                String check_in = rs.getTimestamp("check_in").toString();
                                String check_out = rs.getTimestamp("check_out").toString();

                                //txtCustomerEmailR.setText(customerEmail);
                                ((Label) pane.lookup("#txtCustomerEmailRC")).setText(customerEmail);
                                //txtNoPeople.setValue(noPeople);
                                ((Label) pane.lookup("#txtRoomTypeC")).setText(roomType);
                                //roomSelect.setText(String.valueOf(roomno));
                                ((Label) pane.lookup("#txtRoomNoC")).setText(roomno);
                                //txtcheckIn.setValue(LocalDate.parse((CharSequence) dateFrom));


                                ((Label) pane.lookup("#txtcheckInC")).setText(check_in.substring(0, check_in.length() - 5));
                                //txtCheckout.setValue(LocalDate.parse((CharSequence) dateTo));
                                ((Label) pane.lookup("#txtCheckoutC")).setText(check_out.substring(0, check_out.length() - 5));
                                //txtroomPrice.setText(String.valueOf(price));
                            }


                        } catch (SQLException e) {
                            System.out.print(e);
                            sc.closeConnection();
                        }
                    }


                } else {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Reservation Number does not exist!");
                    a.show();
                    sc.closeConnection();
                }
            } catch (SQLException e) {
                System.out.print(e);
                sc.closeConnection();
            }
        } catch (NumberFormatException nfe) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Reservation Number does not exist!");
            a.show();

        }
    }

    public void CheckIn(javafx.event.ActionEvent event) throws IOException {
        //add check-in date to reservation
        SqlController sc = null;
        int reservation_Number = Integer.parseInt(ReservationNum.getText().replaceAll("[^0-9]", ""));
        System.out.print(reservation_Number);
        currentReservation = reservation_Number;
        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            sc = new SqlController();
            conn = sc.openConnection();
            pst = conn.prepareStatement("Select dateFrom From Reservation WHERE reservationno = '" + reservation_Number + "'");
            //pst.setInt(1, reservation_Number);
            rs = pst.executeQuery();
            if (rs.next()) {
                if (today.before(rs.getDate(1))) {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("You can not check in before booking date!");
                    a.show();
                } else {

                    try {
                        sc = new SqlController();
                        conn = sc.openConnection();
                        pst = conn.prepareStatement("UPDATE Reservation SET check_in = '" + formatter.format(today) + "' WHERE reservationno = '" + reservation_Number + "'");
                        pst.executeUpdate();

                    } catch (SQLException e) {
                        System.out.print(e);
                        sc.closeConnection();
                    }

                    //display checked-in reservation page
                    Parent pane = FXMLLoader.load(getClass().getResource("ViewCheckedInReservation.fxml"));
                    ((Label) pane.lookup("#ReservationNum")).setText("Reservation  #" + reservation_Number);
                    Scene ViewCheckedInReservation = new Scene(pane);
                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    window.setScene(ViewCheckedInReservation);
                    window.setTitle(" ");
                    window.show();

                    try {
                        sc = new SqlController();
                        conn = sc.openConnection();
                        pst = conn.prepareStatement("SELECT * FROM Reservation WHERE reservationno = '" + reservation_Number + "'");
                        rs = pst.executeQuery();

                        if (rs.next()) {
                            //get details from database and display
                            String roomno = rs.getString(2);
                            String roomType = rs.getString(3);
                            String dateFrom = rs.getString(4);
                            String dateTo = rs.getString(5);
                            String price = rs.getString(6);
                            String customerEmail = rs.getString(7);
                            String check_in = rs.getTimestamp("check_in").toString();
                            String check_out = rs.getDate("dateTo").toString();

                            //txtCustomerEmailR.setText(customerEmail);
                            ((Label) pane.lookup("#txtCustomerEmailRC")).setText(customerEmail);
                            //txtNoPeople.setValue(noPeople);
                            ((Label) pane.lookup("#txtRoomTypeC")).setText(roomType);
                            //roomSelect.setText(String.valueOf(roomno));
                            ((Label) pane.lookup("#txtRoomNoC")).setText(roomno);
                            //txtcheckIn.setValue(LocalDate.parse((CharSequence) dateFrom));


                            ((Label) pane.lookup("#txtcheckInC")).setText(check_in.substring(0, check_in.length() - 5));
                            //txtCheckout.setValue(LocalDate.parse((CharSequence) dateTo));
                            ((Label) pane.lookup("#txtCheckoutC")).setText(check_out);
                            //txtroomPrice.setText(String.valueOf(price));
                            ((Label) pane.lookup("#txtroomPrice")).setText(price);
                        }


                    } catch (SQLException e) {
                        System.out.print(e);
                        sc.closeConnection();
                    }
                }
            } else {

            }
        } catch (SQLException e) {
            System.out.print(e);
            sc.closeConnection();
        }
    }

    public void SaveEditReservation(javafx.event.ActionEvent event) throws IOException {
        SqlController sc = null;
        Parent pane = FXMLLoader.load(getClass().getResource("ViewReservation.fxml"));
        sc = new SqlController();
        String email = txtCustomerEmailR.getText();
        try {
            conn = sc.openConnection();
            pst = conn.prepareStatement("SELECT * from customer WHERE customer_email=?");
            pst.setString(1, email);
            rs = pst.executeQuery();
            if (rs.next()) {
                if (java.sql.Date.valueOf(txtcheckout.getValue()).before(java.sql.Date.valueOf(txtCheckIn.getValue()))) {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Check-Out date must be after the Check-In Date!");
                    a.show();
                }
                try {
                    conn = sc.openConnection();
                    String number = editRoomSelect.getValue().toString();
                    String type = txtRoomType.getValue().toString();
                    pst = conn.prepareStatement("SELECT pricepernight FROM room WHERE roomno = ?");
                    pst.setInt(1, Integer.parseInt(number));
                    System.out.println(pst.toString());
                    rs = pst.executeQuery();
                    if (rs.next()) {
                        txtroomPrice.setText(rs.getString(1));


                        try {
                            String str = ReservationNum.getText();
                            String str1 = str.substring(14);

                            conn = sc.openConnection();
                            pst = conn.prepareStatement("UPDATE Reservation SET roomno = ? , roomType = ? , dateFrom = ? , dateTo = ? , price = ? WHERE reservationno = ?");
                            pst.setInt(1, Integer.parseInt(number));
                            pst.setString(2, type);
                            pst.setDate(3, java.sql.Date.valueOf(txtCheckIn.getValue()));
                            pst.setDate(4, java.sql.Date.valueOf(txtcheckout.getValue()));
                            pst.setDouble(5, Double.parseDouble(txtroomPrice.getText()));
                            pst.setInt(6, Integer.parseInt(str1));
                            Statement stmt = conn.createStatement();

                            pst.executeUpdate();
                            sc.closeConnection();
                            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
                            a.setContentText("Reservation has been amended!");
                            a.show();

                        } catch (SQLException e) {
                            System.out.print(e);
                            sc.closeConnection();
                        }
                    } else {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setContentText("Room does not exist!");
                        a.show();
                        editRoomSelect.getItems().clear();
                    }
                } catch (SQLException e) {
                    System.out.print(e);
                    sc.closeConnection();
                }
            } else {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Customer does not exist!");
                a.show();
            }
        } catch (SQLException e) {
            System.out.print(e);
            sc.closeConnection();
        }

    }


    public void DeleteReservation(javafx.event.ActionEvent event) throws IOException {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setContentText("Confirm Deletion");
        if (a.showAndWait().isPresent() && a.getResult() == ButtonType.OK) {
            SqlController sc = null;
            sc = new SqlController();
            String str = ReservationNum.getText();
            String str1 = str.substring(14);
            try {
                conn = sc.openConnection();
                String sql = "DELETE FROM Reservation WHERE reservationno = '" + str1 + "'";
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(sql);
                Back(event);
                sc.closeConnection();

            } catch (SQLException e) {
                System.out.print(e);
                sc.closeConnection();
            }
        } else {
            a.close();
        }


    }


    public void AddExtra(javafx.event.ActionEvent event) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("AddExtra.fxml"));
        String[] chargeList = getCharges();
        if (chargeList.length == 0) { // if we don't get anything back, something went wrong and we close
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Unable to retrieve charge types!");
            a.show();
        } else {
            ((ComboBox<String>) pane.lookup("#extradescription")).setItems(FXCollections.observableList(Arrays.asList(chargeList)));
            ((ComboBox<String>) pane.lookup("#extradescription")).getSelectionModel().select(chargeList[0]);
        }
        Scene AddExtraPage = new Scene(pane);
        Stage popup = new Stage();
        popup.setScene(AddExtraPage);
        popup.show();
    }

    public void DeleteExtra(javafx.event.ActionEvent event) throws IOException { //delete extra
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setContentText("Confirm Deletion");
        if (a.showAndWait().isPresent() && a.getResult() == ButtonType.OK) {
            SqlController sc = new SqlController();
            try {
                conn = sc.openConnection();
                String sql = "DELETE FROM extra WHERE extrano = '" + findExtra.getValue() + "'";
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                System.out.print(e);
                sc.closeConnection();
            }
            Alert b = new Alert(Alert.AlertType.CONFIRMATION);
            b.setContentText("Extra deleted");
            if (b.showAndWait().isPresent() && b.getResult() == ButtonType.OK) {
                Stage popup = (Stage) ((Node) event.getSource()).getScene().getWindow();
                popup.close();
                Parent pane = FXMLLoader.load(getClass().getResource("EditExtras.fxml"));
                SqlController sqlController = new SqlController();


                try {
                    //clear all panes
                    ((Label) pane.lookup("#Extra1ID")).setText("");
                    ((Label) pane.lookup("#Extra1desc")).setText("");
                    ((Label) pane.lookup("#Extra1price")).setText("");
                    ((Label) pane.lookup("#Extra1date")).setText("");
                    ((Label) pane.lookup("#Extra2ID")).setText("");
                    ((Label) pane.lookup("#Extra2desc")).setText("");
                    ((Label) pane.lookup("#Extra2price")).setText("");
                    ((Label) pane.lookup("#Extra2date")).setText("");
                    ((Label) pane.lookup("#Extra3ID")).setText("");
                    ((Label) pane.lookup("#Extra3desc")).setText("");
                    ((Label) pane.lookup("#Extra3price")).setText("");
                    ((Label) pane.lookup("#Extra3date")).setText("");
                    ((Label) pane.lookup("#Extra4ID")).setText("");
                    ((Label) pane.lookup("#Extra4desc")).setText("");
                    ((Label) pane.lookup("#Extra4price")).setText("");
                    ((Label) pane.lookup("#Extra4date")).setText("");


                    conn = sqlController.openConnection();
                    String extrasQuery = "SELECT extrano, description, amount, date FROM extra WHERE reservationno = " + currentReservation + ";";
                    String reservationQuery = "SELECT price, DATEDIFF(dateTo, dateFrom) as days FROM Reservation where reservationno = " + currentReservation + ";";
                    PreparedStatement extrasStatement = conn.prepareStatement(extrasQuery);
                    PreparedStatement reservationStatement = conn.prepareStatement(reservationQuery);
                    rs = extrasStatement.executeQuery();
                    int rowCount = 1; //for the loop
                    //System.out.println(currentExtraNum); //testing current Extra Num
                    while (rs.next() && rowCount < 5) {
                        String extraId = String.format("#Extra%d", rowCount++);
                        ((Label) pane.lookup(String.format("%sID", extraId))).setText(rs.getString("extrano")); //adding the ExtraNum
                        ((Label) pane.lookup(String.format("%sdesc", extraId))).setText(rs.getString("description"));
                        ((Label) pane.lookup(String.format("%sprice", extraId))).setText(rs.getString("amount"));
                        ((Label) pane.lookup(String.format("%sdate", extraId))).setText(rs.getString("date"));
                    }
                    rs = reservationStatement.executeQuery();
                    rs.next();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                Integer[] extraList = getExtras(currentReservation);
                if (extraList.length == 0) { // if we don't get anything back, something went wrong and we close
                    Alert c = new Alert(Alert.AlertType.ERROR);
                    c.setContentText("No Extras Found!");
                    c.show();

                } else {
                    ((ComboBox<Integer>) pane.lookup("#findExtra")).setItems(FXCollections.observableList(Arrays.asList(extraList)));
                    ((ComboBox<Integer>) pane.lookup("#findExtra")).getSelectionModel().select(extraList[0]);
                    Scene editextras = new Scene(pane);
                    Stage editextra = new Stage();
                    editextra.setScene(editextras);
                    editextra.show();
                }
            }
        } else {
            a.close();
        }
    }

    public void CanceladdExtra(javafx.event.ActionEvent event) throws IOException {
        Stage popup = (Stage) ((Node) event.getSource()).getScene().getWindow();
        popup.close();
    }

    //save extras to database
    public void addExtraDetails(ActionEvent actionEvent) {
        SqlController sc = new SqlController();
        // set to -1 initially, so if a value is not presented it will be caught
        double amount = -1.0;
        try {
            amount = Double.parseDouble(extraAmount.getText());
        } catch (NumberFormatException e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Amount needs to be a positive numeric value.");
            a.show();
            sc.closeConnection();
            return;
        }
        if (amount < 0) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Amount needs to be greater than 0.");
            a.show();
            sc.closeConnection();
            return;
        }
        if (java.sql.Date.valueOf(extraDate.getValue()).before(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()))) {  //checks if date is before current date
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Extras Date must not be before Current Date!");
            a.show();
            extraDate.setValue(null);
            //sets value to null after
            sc.closeConnection();
        } else {
            try {
                conn = sc.openConnection();                                 //reservationno below (call reservationNo)
                String query = "INSERT INTO extra (description,amount,date,reservationno) VALUES(?,?,?,?)"; //needs to grab past reservation number as there is no text field for it.
                pst = conn.prepareStatement(query);


                pst.setString(1, (String) extradescription.getValue()); //Extra description
                pst.setDouble(2, Double.parseDouble(extraAmount.getText())); //Extra amount
                pst.setDate(3, java.sql.Date.valueOf(extraDate.getValue())); // Date
                pst.setInt(4, currentReservation); // Reservation No
                System.out.println(currentReservation);

                //execute update
                pst.executeUpdate();
                Alert a = new Alert(Alert.AlertType.CONFIRMATION);
                a.setContentText("Extras Added!");
                a.show();
                sc.closeConnection();
            } catch (SQLException e) {
                System.out.print(e);
                sc.closeConnection();
            }

        }
    }

    public void NewCustomerNewR(javafx.event.ActionEvent event) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("NewCustomerR.fxml"));
        Scene CustomerScene = new Scene(pane);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("Customer Registration");
        window.setScene(CustomerScene);
        window.show();
    }

    public void BacktoR(javafx.event.ActionEvent event) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("NewReservation.fxml"));
        ((TextField) pane.lookup("#txtCustomerEmailR")).setText(txtCustomerEmail.getText());
        Scene CustomerScene = new Scene(pane);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(CustomerScene);
        window.setTitle("New Reservation");
        window.show();
    }

    public void AddCustomerR(javafx.event.ActionEvent event) {
        SqlController sc = new SqlController();
        //check null values are not entered
        if (txtCardExpiry.getValue() == null || txtCustomerName.getText().isEmpty() || txtCustomerCountry.getText().isEmpty() || txtCustomerTown.getText().isEmpty() || txtCustomerPostcode.getText().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("All fields must be filled!");
            a.show();
        }
        //check customer email is correct format
        else if (txtCustomerEmail.getText().contains("@") && txtCustomerEmail.getText().contains(".")) {
            try {
                //make sure card number is an int
                int card_number = Integer.parseInt(txtCardNo.getText());

                //check if date of card expiry is before hotel date
                if (java.sql.Date.valueOf(txtCardExpiry.getValue()).before(new Date())) {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Card expiry date must not be before Current Date!");
                    a.show();
                    txtCardExpiry.setValue(null);
                    //check card number length is valid
                } else if (txtCardNo.getLength() > 16) {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Card Number is invalid");
                    a.show();
                } else {
                    try {
                        //check that email has not already been registered as a customer
                        conn = sc.openConnection();
                        String sql = "SELECT * from customer WHERE customer_email=?";
                        pst = conn.prepareStatement(sql);
                        pst.setString(1, txtCustomerEmail.getText());
                        rs = pst.executeQuery();
                        if (rs.next()) {
                            Alert a = new Alert(Alert.AlertType.ERROR);
                            a.setContentText("Email is already in use!");
                            a.show();
                            txtCustomerEmail.clear();
                            sc.closeConnection();
                        } else {
                            // add customer to database
                            try {
                                conn = sc.openConnection();
                                String query = "INSERT INTO customer (customer_name,customer_address,card_number,customer_email,card_Expiry_Date) VALUES(?,?,?,?,?)";
                                pst = conn.prepareStatement(query);

                                pst.setString(1, txtCustomerName.getText()); //Customer Name txtCustomerName.getText()
                                pst.setString(2, formatAddress()); //Customer Address txtCustomerAddress.getText()
                                pst.setInt(3, Integer.parseInt(txtCardNo.getText())); //Card Number
                                pst.setString(4, txtCustomerEmail.getText()); //Customer email txtCustomerEmailR.getText()
                                pst.setDate(5, java.sql.Date.valueOf(txtCardExpiry.getValue())); //expiry date of card

                                //execute update
                                pst.executeUpdate();
                                sc.closeConnection();

                                Alert a = new Alert(Alert.AlertType.CONFIRMATION);
                                a.setContentText("Customer Added!");
                                if (a.showAndWait().isPresent() && a.getResult() == ButtonType.OK) {
                                    BacktoR(event);
                                }
                            } catch (SQLException | IOException e) {
                                System.out.print(e);
                                sc.closeConnection();
                            }
                        }
                    } catch (SQLException e) {
                        System.out.print(e);
                        sc.closeConnection();
                    }
                }
            } catch (NumberFormatException nfe) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Card number is invalid!");
                a.show();
            }
        } else {
            Alert j = new Alert(Alert.AlertType.ERROR);
            j.setContentText("Email is invalid!");
            j.show();
        }

    }

    public void ViewCustomer(javafx.event.ActionEvent event) throws IOException {
        SqlController sqlController = new SqlController();
        try {
            conn = sqlController.openConnection();
            pst = conn.prepareStatement("SELECT * FROM customer WHERE customer_email = '" + txtfindCustomer.getText() + "'");
            rs = pst.executeQuery();
            if (!rs.next()) { //for if else cut statement
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Customer email does not exist!");
                a.show();
                sqlController.closeConnection();
                throw new IllegalArgumentException("Unable to find customer email.");
            }

            Parent pane = FXMLLoader.load(getClass().getResource("ViewCustomer.fxml"));

            //set string values
            //get details from database and display
            String customer_name = rs.getString(1);
            String[] customer_address = rs.getString(2).split(",");
            String card_number = rs.getString(3);
            String customer_email = rs.getString(4);
            String card_Expiry_Date = rs.getString(5);

            ((TextField) pane.lookup("#txtCustomerName")).setText(customer_name); //fill customer details
            ((TextField) pane.lookup("#txtCustomerEmail")).setText(txtfindCustomer.getText()); //adds email to text field
            ((TextField) pane.lookup("#txtCustomerCountry")).setText(customer_address[0]);
            ((TextField) pane.lookup("#txtCustomerTown")).setText(customer_address[1]);
            ((TextField) pane.lookup("#txtCustomerPostcode")).setText(customer_address[2]);
            ((TextField) pane.lookup("#txtCardNo")).setText(card_number);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); //TODO remove redundant code
            DatePicker datePicker = (DatePicker) pane.lookup("#txtCardExpiry");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date result = df.parse(card_Expiry_Date);
            ((DatePicker) pane.lookup("#txtCardExpiry")).setValue(result.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());


            Scene CustomerScene = new Scene(pane);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setTitle("View Customer");
            window.setScene(CustomerScene);
            window.show();
        } catch (SQLException | ParseException e) {
            System.out.print(e);
            sqlController.closeConnection();
        } catch (IllegalArgumentException e) {
            System.out.println("Customer email not found.");
        }

    }

    public void SaveEditCustomer(javafx.event.ActionEvent event) throws IOException {
        SqlController sc = null;
        Parent pane = FXMLLoader.load(getClass().getResource("ViewCustomer.fxml"));
        sc = new SqlController();
        String email = txtCustomerEmail.getText();
        if (txtCardExpiry.getValue() == null || txtCustomerName.getText().isEmpty() || txtCustomerCountry.getText().isEmpty() || txtCustomerTown.getText().isEmpty() || txtCustomerPostcode.getText().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("All fields must be filled!");
            a.show();
        }

        if (txtCustomerEmail.getText().contains("@") && txtCustomerEmail.getText().contains(".")) {  //email error handler

            try {
                conn = sc.openConnection();
                pst = conn.prepareStatement("SELECT * from customer WHERE customer_email=?");
                pst.setString(1, email);
                rs = pst.executeQuery();
                if (rs.next()) {
                    if (java.sql.Date.valueOf(txtCardExpiry.getValue()).before(new Date())) {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setContentText("Card Expiry date must be after the current date!");
                        a.show();
                        return;
                    }
                    Long.parseLong(txtCardNo.getText());
                    if(txtCardNo.getText().length() < 16) {
                        String str = txtCustomerEmail.getText();
                        pst = conn.prepareStatement("SELECT * from customer WHERE customer_email=?");
                        pst.setString(1,str);
                        rs = pst.executeQuery();
                        if(rs.next()){
                            Alert a = new Alert(Alert.AlertType.ERROR);
                            a.setContentText("Customer email is already in use!");
                            a.show();
                            return;
                        }


                        conn = sc.openConnection();
                        String sql = "UPDATE customer SET customer_name = '" + txtCustomerName.getText() + "' , customer_address = '" + formatAddress() +
                                "' , card_number = '" + txtCardNo.getText() + "' , customer_email = '" + txtCustomerEmail.getText() + "' , card_Expiry_Date = '"
                                + java.sql.Date.valueOf(txtCardExpiry.getValue())
                                + "' WHERE customer_email = '" + str + "'";
                        Statement stmt = conn.createStatement();

                        stmt.executeUpdate(sql);
                        sc.closeConnection();
                        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
                        a.setContentText("Customer has been amended!");
                        a.show();

                    }
                    else {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setContentText("Card Number Must be less than 16 digits");
                        a.show();
                    }
                } else {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Email does not exist!");
                    a.show();
                    //.clear();
                }
            }catch(SQLException | NumberFormatException e){
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText(e.getMessage());
                a.show();
                System.out.print(e);
                sc.closeConnection();
            }
        }

    }




    public void DeleteCustomer(ActionEvent actionEvent) throws IOException { //delete customer
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setContentText("Confirm Deletion");
        if (a.showAndWait().isPresent() && a.getResult() == ButtonType.OK) {
            SqlController sc = null;
            sc = new SqlController();
            String str = txtCustomerEmail.getText();

            try {
                conn = sc.openConnection();
                String sql = "DELETE FROM customer WHERE customer_email = '" + str + "'";
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(sql);
                Alert b = new Alert(Alert.AlertType.CONFIRMATION);
                b.setContentText("Customer Deleted");

            } catch (SQLException e) {
                System.out.print(e);
                sc.closeConnection();
            }
        } else {
            a.close();
        }
    }

    public void ManageRooms(javafx.event.ActionEvent event) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("ManageRooms.fxml"));
        Scene CustomerScene = new Scene(pane);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("Manage Rooms");
        window.setScene(CustomerScene);
        window.show();
    }

    //view room
    public void findRoom(javafx.event.ActionEvent event) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("viewRoom.fxml"));
        SqlController sc = null;
        sc = new SqlController();
        String roomNo = findtxtRoomNo.getText();
        try {
            conn = sc.openConnection();
            String query = "SELECT * FROM room  WHERE roomno = '" + roomNo + "'";
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            //if room number exists
            if (rs.next()) {

                int roomNumberView = rs.getInt("roomno");
                ((Label) pane.lookup("#viewRoomNum")).setText("Room     #" + roomNumberView);
                ((Label) pane.lookup("#updateRoomNo")).setText(roomNo);
                String roomType = rs.getString("roomtype");
                ((ChoiceBox) pane.lookup("#RegSetRoomType")).setValue(roomType);

                Double pricepernight = rs.getDouble("pricepernight");
                ((TextField) pane.lookup("#RegPriceperNight")).setText(pricepernight.toString());

                Scene CustomerScene = new Scene(pane);
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setTitle("View Room");
                window.setScene(CustomerScene);
                window.show();
            } else {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Room does not exist!");
                a.show();
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //display register new room page
    public void RegisterRoom(javafx.event.ActionEvent event) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("Rooms.fxml"));
        Scene CustomerScene = new Scene(pane);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("Register Rooms");
        window.setScene(CustomerScene);
        window.show();
    }

    //add room to database
    public void AddRoom() {
        SqlController sc = new SqlController();
        try {
            Double PriceperNight = Double.parseDouble(RegPriceperNight.getText());
            int RoomNum = Integer.parseInt(RegSetRoomNo.getText());
            if (PriceperNight instanceof Double) {
                try {
                    conn = sc.openConnection();
                    String sql = "SELECT * from room WHERE roomno=?";
                    pst = conn.prepareStatement(sql);
                    pst.setString(1, RegSetRoomNo.getText());
                    rs = pst.executeQuery();

                    if (rs.next()) {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setContentText("Room Number is already taken!");
                        a.show();
                        RegSetRoomNo.clear();
                        RegSetRoomType.setValue("Single");
                        RegPriceperNight.clear();
                        sc.closeConnection();
                    } else {
                        try {
                            String query = "INSERT INTO room (roomno, roomtype, pricepernight) VALUES(?,?,?)";
                            pst = conn.prepareStatement(query);

                            pst.setInt(1, Integer.parseInt(RegSetRoomNo.getText()));
                            pst.setString(2, RegSetRoomType.getValue().toString());
                            pst.setDouble(3, Double.parseDouble(RegPriceperNight.getText()));

                            pst.executeUpdate();
                            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
                            a.setContentText("Room Added!");
                            a.show();
                            RegSetRoomNo.clear();
                            RegSetRoomType.setValue("Single");
                            RegPriceperNight.clear();
                        } catch (SQLException e) {
                            System.out.print(e);
                            sc.closeConnection();
                        }
                        sc.closeConnection();
                        System.out.println("Room Added!");
                    }
                } catch (SQLException e) {
                    System.out.print(e);
                    sc.closeConnection();
                }
            }
        } catch (NumberFormatException nfe) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Invalid values entered!");
            a.show();
            RegSetRoomNo.clear();
            RegSetRoomType.setValue("Single");
            RegPriceperNight.clear();
        }
    }

    public void CheckOut(javafx.event.ActionEvent event) throws IOException {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setContentText("Confirm Check-Out");
        if (a.showAndWait().isPresent() && a.getResult() == ButtonType.OK) {
            SqlController sc = null;
            int reservation_Number = Integer.parseInt(ReservationNum.getText().substring(14));
            currentReservation = reservation_Number;
            Date today = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                sc = new SqlController();
                conn = sc.openConnection();
                pst = conn.prepareStatement("UPDATE Reservation SET check_out = '" + formatter.format(today) + "' WHERE reservationno = '" + reservation_Number + "'");
                pst.executeUpdate();

            } catch (SQLException e) {
                System.out.print(e);
                sc.closeConnection();
            }
            //display checked-out reservation page
            Parent pane = FXMLLoader.load(getClass().getResource("ViewCheckedOutRes.fxml"));
            ((Label) pane.lookup("#ReservationNum")).setText("Reservation  #" + reservation_Number);
            Scene ViewCheckedInReservation = new Scene(pane);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(ViewCheckedInReservation);
            window.setTitle(" ");
            window.show();

            try {
                sc = new SqlController();
                conn = sc.openConnection();
                pst = conn.prepareStatement("SELECT * FROM Reservation WHERE reservationno = '" + reservation_Number + "'");
                rs = pst.executeQuery();

                if (rs.next()) {
                    //get details from database and display
                    String roomno = rs.getString(2);
                    String roomType = rs.getString(3);
                    String customerEmail = rs.getString(7);
                    String check_in = rs.getTimestamp("check_in").toString();
                    String check_out = rs.getTimestamp("check_out").toString();

                    //txtCustomerEmailR.setText(customerEmail);
                    ((Label) pane.lookup("#txtCustomerEmailRC")).setText(customerEmail);
                    //txtNoPeople.setValue(noPeople);
                    ((Label) pane.lookup("#txtRoomTypeC")).setText(roomType);
                    //roomSelect.setText(String.valueOf(roomno));
                    ((Label) pane.lookup("#txtRoomNoC")).setText(roomno);
                    //txtcheckIn.setValue(LocalDate.parse((CharSequence) dateFrom));


                    ((Label) pane.lookup("#txtcheckInC")).setText(check_in.substring(0, check_in.length() - 5));
                    //txtCheckout.setValue(LocalDate.parse((CharSequence) dateTo));
                    ((Label) pane.lookup("#txtCheckoutC")).setText(check_out.substring(0, check_out.length() - 5));
                    //txtroomPrice.setText(String.valueOf(price));
                }


            } catch (SQLException e) {
                System.out.print(e);
                sc.closeConnection();
            }
            //display bill
            pane = FXMLLoader.load(getClass().getResource("FinalBill.fxml"));

            SqlController sqlController = new SqlController();

            try {
                //clear exsisting panes
                ((Label) pane.lookup("#Extra1desc")).setText("");
                ((Label) pane.lookup("#Extra1price")).setText("");
                ((Label) pane.lookup("#Extra1date")).setText("");
                ((Label) pane.lookup("#Extra2desc")).setText("");
                ((Label) pane.lookup("#Extra2price")).setText("");
                ((Label) pane.lookup("#Extra2date")).setText("");
                ((Label) pane.lookup("#Extra3desc")).setText("");
                ((Label) pane.lookup("#Extra3price")).setText("");
                ((Label) pane.lookup("#Extra3date")).setText("");
                ((Label) pane.lookup("#Extra4desc")).setText("");
                ((Label) pane.lookup("#Extra4price")).setText("");
                ((Label) pane.lookup("#Extra4date")).setText("");

                conn = sqlController.openConnection();
                String extrasQuery = "SELECT description, amount, date FROM extra WHERE reservationno = " + currentReservation + ";";
                String reservationQuery = "SELECT price, DATEDIFF(check_out, check_in) as days FROM Reservation where reservationno = " + currentReservation + ";"; //changed dateFrom to check_in
                PreparedStatement extrasStatement = conn.prepareStatement(extrasQuery);
                PreparedStatement reservationStatement = conn.prepareStatement(reservationQuery);
                rs = extrasStatement.executeQuery();

                double totalPrice = 0.0;
                int rowCount = 1; //for the loop (Might need to be changed to 2 for second loop)
                while (rs.next() && rowCount < 5) {
                    String extraId = String.format("#Extra%d", rowCount++);
                    ((Label) pane.lookup(String.format("%sdesc", extraId))).setText(rs.getString("description"));
                    ((Label) pane.lookup(String.format("%sprice", extraId))).setText(rs.getString("amount"));
                    ((Label) pane.lookup(String.format("%sdate", extraId))).setText(rs.getString("date"));
                    totalPrice += rs.getDouble("amount");
                }

                rs = reservationStatement.executeQuery();
                while (rs.next()) {
                    int numDays = rs.getInt("days");
                    double price = rs.getDouble("price");
                    totalPrice += numDays * price;
                    ((Label) pane.lookup("#roomprice")).setText(String.valueOf(price));
                    ((Label) pane.lookup("#Nights")).setText(String.valueOf(numDays));
                    ((Label) pane.lookup("#roompricetotal")).setText(String.valueOf(price * numDays));


                    //making other unused panes empty text fields

                    ((Label) pane.lookup("#total")).setText(String.valueOf(totalPrice));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Scene Bill = new Scene(pane);
            Stage popup = new Stage();
            popup.setScene(Bill);
            popup.show();
        }else{
            a.close();
        }
    }


    public void DeleteRoom(javafx.event.ActionEvent event) throws IOException {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setContentText("Confirm Deletion");
        if (a.showAndWait().isPresent() && a.getResult() == ButtonType.OK) {
            SqlController sc = new SqlController();
            Integer RoomNum = Integer.parseInt(updateRoomNo.getText());
            try {
                conn = sc.openConnection();
                String sql = "DELETE FROM room WHERE roomno = '" + RoomNum + "'";
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(sql);

                Alert b = new Alert(Alert.AlertType.CONFIRMATION);
                b.setContentText("Room has been deleted!");
                b.show();

            } catch (SQLException e) {
                System.out.print(e);
                sc.closeConnection();
            }
        } else {
            a.close();
        }
        Parent pane = FXMLLoader.load(getClass().getResource("ManageRooms.fxml"));
        Scene CustomerScene = new Scene(pane);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("Manage Rooms");
        window.setScene(CustomerScene);
        window.show();
    }

    public void SaveEditRoom(ActionEvent actionEvent) {
        SqlController sc = new SqlController();
        conn = sc.openConnection();
        try {
            Double PriceperNight = Double.parseDouble(RegPriceperNight.getText());
            Integer RoomNum = Integer.parseInt(updateRoomNo.getText());
            String roomtype = RegSetRoomType.getValue().toString();
            if (PriceperNight instanceof Double) {
                try {
                    String sql = "UPDATE room SET roomType = '" + roomtype + "' , pricepernight = '" + PriceperNight + "' WHERE roomno = '" + RoomNum + "'";
                    Statement stmt = conn.createStatement();

                    stmt.executeUpdate(sql);
                    sc.closeConnection();
                    Alert a = new Alert(Alert.AlertType.CONFIRMATION);
                    a.setContentText("Reservation has been amended!");
                    a.show();

                } catch (SQLException e) {
                    System.out.print(e);
                    sc.closeConnection();
                }
            } else {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Invalid values entered!");
                a.show();
                RegSetRoomNo.clear();
                RegSetRoomType.setValue("Single");
                RegPriceperNight.clear();
            }
        }catch (NumberFormatException nfe) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Invalid values entered!");
            a.show();
            RegPriceperNight.clear();
        }
    }


    public void BacktoMR(javafx.event.ActionEvent event) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("ManageRooms.fxml"));
        Scene CustomerScene = new Scene(pane);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("Manage Rooms");
        window.setScene(CustomerScene);
        window.show();
    }

    public void BacktoCR(javafx.event.ActionEvent event) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("ManageCustomers.fxml"));
        Scene Scene = new Scene(pane);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("Manage Customers");
        window.setScene(Scene);
        window.show();
    }

    public void EditExtras(ActionEvent event) throws IOException {
        Stage popup = (Stage) ((Node) event.getSource()).getScene().getWindow();
        popup.close();
        Parent pane = FXMLLoader.load(getClass().getResource("EditExtras.fxml"));
        SqlController sqlController = new SqlController();


        try {
            //clear all panes
            ((Label) pane.lookup("#Extra1ID")).setText("");
            ((Label) pane.lookup("#Extra1desc")).setText("");
            ((Label) pane.lookup("#Extra1price")).setText("");
            ((Label) pane.lookup("#Extra1date")).setText("");
            ((Label) pane.lookup("#Extra2ID")).setText("");
            ((Label) pane.lookup("#Extra2desc")).setText("");
            ((Label) pane.lookup("#Extra2price")).setText("");
            ((Label) pane.lookup("#Extra2date")).setText("");
            ((Label) pane.lookup("#Extra3ID")).setText("");
            ((Label) pane.lookup("#Extra3desc")).setText("");
            ((Label) pane.lookup("#Extra3price")).setText("");
            ((Label) pane.lookup("#Extra3date")).setText("");
            ((Label) pane.lookup("#Extra4ID")).setText("");
            ((Label) pane.lookup("#Extra4desc")).setText("");
            ((Label) pane.lookup("#Extra4price")).setText("");
            ((Label) pane.lookup("#Extra4date")).setText("");


            conn = sqlController.openConnection();
            String extrasQuery = "SELECT extrano, description, amount, date FROM extra WHERE reservationno = " + currentReservation + ";";
            String reservationQuery = "SELECT price, DATEDIFF(dateTo, dateFrom) as days FROM Reservation where reservationno = " + currentReservation + ";";
            PreparedStatement extrasStatement = conn.prepareStatement(extrasQuery);
            PreparedStatement reservationStatement = conn.prepareStatement(reservationQuery);
            rs = extrasStatement.executeQuery();
            int rowCount = 1; //for the loop
            //System.out.println(currentExtraNum); //testing current Extra Num
            while (rs.next() && rowCount < 5) {
                String extraId = String.format("#Extra%d", rowCount++);
                ((Label) pane.lookup(String.format("%sID", extraId))).setText(rs.getString("extrano")); //adding the ExtraNum
                ((Label) pane.lookup(String.format("%sdesc", extraId))).setText(rs.getString("description"));
                ((Label) pane.lookup(String.format("%sprice", extraId))).setText(rs.getString("amount"));
                ((Label) pane.lookup(String.format("%sdate", extraId))).setText(rs.getString("date"));
            }
            rs = reservationStatement.executeQuery();
            rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Integer[] extraList = getExtras(currentReservation);
        if (extraList.length == 0) { // if we don't get anything back, something went wrong and we close
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("No Extras Found!");
            a.show();

        } else {
            ((ComboBox<Integer>) pane.lookup("#findExtra")).setItems(FXCollections.observableList(Arrays.asList(extraList)));
            ((ComboBox<Integer>) pane.lookup("#findExtra")).getSelectionModel().select(extraList[0]);
            Scene editextras = new Scene(pane);
            Stage editextra = new Stage();
            editextra.setScene(editextras);
            editextra.show();
        }


    }

    public void Editextra(ActionEvent event) throws IOException {
        Stage popup = (Stage) ((Node) event.getSource()).getScene().getWindow();
        popup.close();
        SqlController sqlController = new SqlController();

        try {
            System.out.println(findExtra.getValue());
            conn = sqlController.openConnection();
            pst = conn.prepareStatement("SELECT * FROM extra WHERE extrano = " + findExtra.getValue());
            rs = pst.executeQuery();
            if (!rs.next()) { //for if else cut statement
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Extra Number does not exist!");
                a.show();
                sqlController.closeConnection();
                throw new IllegalArgumentException("Unable to find Extra Number.");
            }

            Parent pane = FXMLLoader.load(getClass().getResource("EditExtra.fxml"));
            //set string values
            //get details from database and display
            // String extra_number = rs.getString(1);
            String date = rs.getString("date");
            String description = rs.getString("description");
            double amount = rs.getDouble("amount");
            //String card_Expiry_Date = rs.getString(5);

            //((TextField) pane.lookup("#extraDate")).setText(Date.toString()); //fill customer details
            ((Label) pane.lookup("#ExtraNum")).setText(String.format("Extra: %s", findExtra.getValue()));
            ((TextField) pane.lookup("#extraAmount")).setText(String.valueOf(amount)); //adds email to text field
            String[] chargeList = getCharges();
            if (chargeList.length == 0) { // if we don't get anything back, something went wrong and we close
                sqlController.closeConnection();
            } else {
                for (int i = 0; i < chargeList.length; i++) {
                    if (description.equals(chargeList[i])) {
                        ((ComboBox<String>) pane.lookup("#extradescription")).setItems(FXCollections.observableList(Arrays.asList(chargeList)));
                        ((ComboBox<String>) pane.lookup("#extradescription")).getSelectionModel().select(i);
                        break;
                    }
                }
            }
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

            Date result = df.parse(date);

            ((DatePicker) pane.lookup("#extraDate")).setValue(result.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            Scene editextra = new Scene(pane);
            Stage extrap = new Stage();
            extrap.setScene(editextra);
            extrap.show();
            currentExtraNum = findExtra.getValue();
        } catch (SQLException | ParseException e) {
            e.printStackTrace();

            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please input a valid value!");
            a.show();
            sqlController.closeConnection();


            sqlController.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please input a valid value!");
            a.show();
            sqlController.closeConnection();
        }
    }

    public void CancelExtraEdit(javafx.event.ActionEvent event) throws IOException {
        Stage popup = (Stage) ((Node) event.getSource()).getScene().getWindow();
        popup.close();

        Parent pane = FXMLLoader.load(getClass().getResource("EditExtras.fxml"));
        SqlController sqlController = new SqlController();

        try {
            //clear all panes
            ((Label) pane.lookup("#Extra1ID")).setText("");
            ((Label) pane.lookup("#Extra1desc")).setText("");
            ((Label) pane.lookup("#Extra1price")).setText("");
            ((Label) pane.lookup("#Extra1date")).setText("");
            ((Label) pane.lookup("#Extra2ID")).setText("");
            ((Label) pane.lookup("#Extra2desc")).setText("");
            ((Label) pane.lookup("#Extra2price")).setText("");
            ((Label) pane.lookup("#Extra2date")).setText("");
            ((Label) pane.lookup("#Extra3ID")).setText("");
            ((Label) pane.lookup("#Extra3desc")).setText("");
            ((Label) pane.lookup("#Extra3price")).setText("");
            ((Label) pane.lookup("#Extra3date")).setText("");
            ((Label) pane.lookup("#Extra4ID")).setText("");
            ((Label) pane.lookup("#Extra4desc")).setText("");
            ((Label) pane.lookup("#Extra4price")).setText("");
            ((Label) pane.lookup("#Extra4date")).setText("");


            conn = sqlController.openConnection();
            String extrasQuery = "SELECT extrano, description, amount, date FROM extra WHERE reservationno = " + currentReservation + ";";
            String reservationQuery = "SELECT price, DATEDIFF(dateTo, dateFrom) as days FROM Reservation where reservationno = " + currentReservation + ";";
            PreparedStatement extrasStatement = conn.prepareStatement(extrasQuery);
            PreparedStatement reservationStatement = conn.prepareStatement(reservationQuery);
            rs = extrasStatement.executeQuery();
            int rowCount = 1; //for the loop
            //System.out.println(currentExtraNum); //testing current Extra Num
            while (rs.next() && rowCount < 5) {
                String extraId = String.format("#Extra%d", rowCount++);
                ((Label) pane.lookup(String.format("%sID", extraId))).setText(rs.getString("extrano")); //adding the ExtraNum
                ((Label) pane.lookup(String.format("%sdesc", extraId))).setText(rs.getString("description"));
                ((Label) pane.lookup(String.format("%sprice", extraId))).setText(rs.getString("amount"));
                ((Label) pane.lookup(String.format("%sdate", extraId))).setText(rs.getString("date"));
            }
            rs = reservationStatement.executeQuery();
            rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Integer[] extraList = getExtras(currentReservation);
        if (extraList.length == 0) { // if we don't get anything back, something went wrong and we close
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("No Extras Found!");
            a.show();

        } else {
            ((ComboBox<Integer>) pane.lookup("#findExtra")).setItems(FXCollections.observableList(Arrays.asList(extraList)));
            ((ComboBox<Integer>) pane.lookup("#findExtra")).getSelectionModel().select(extraList[0]);
            Scene editextras = new Scene(pane);
            Stage editextra = new Stage();
            editextra.setScene(editextras);
            editextra.show();
        }
    }

    //load extra details to page and save to database
    public void saveEditExtra(javafx.event.ActionEvent event) throws IOException { //working on  extra edited
        SqlController sc = new SqlController();
        conn = sc.openConnection();
        if (extradescription.getValue() == null || extraAmount.getText().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("All fields must be filled!");
            a.show();
            return;
        }

        Double amount = Double.parseDouble(extraAmount.getText());
        if (amount instanceof Double) {
            try {
                String sql = "UPDATE extra SET description = '" + (String) extradescription.getValue() +
                        "' , amount = '" + amount + "' , date = '" + java.sql.Date.valueOf(extraDate.getValue()) + "' WHERE extrano = '" + currentExtraNum + "'";

                Statement stmt = conn.createStatement();
                //String charges = "Select * FROM charges WHERE Charge = '" + chargesCharge.getText() +"'";
                stmt.executeUpdate(sql);
                sc.closeConnection();
                Alert a = new Alert(Alert.AlertType.CONFIRMATION);
                a.setContentText("Extras has been amended!");
                if (a.showAndWait().isPresent() && a.getResult() == ButtonType.OK) {
                    Stage popup = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    popup.close();

                    Parent pane = FXMLLoader.load(getClass().getResource("EditExtras.fxml"));
                    SqlController sqlController = new SqlController();


                    try {
                        //clear all panes
                        ((Label) pane.lookup("#Extra1ID")).setText("");
                        ((Label) pane.lookup("#Extra1desc")).setText("");
                        ((Label) pane.lookup("#Extra1price")).setText("");
                        ((Label) pane.lookup("#Extra1date")).setText("");
                        ((Label) pane.lookup("#Extra2ID")).setText("");
                        ((Label) pane.lookup("#Extra2desc")).setText("");
                        ((Label) pane.lookup("#Extra2price")).setText("");
                        ((Label) pane.lookup("#Extra2date")).setText("");
                        ((Label) pane.lookup("#Extra3ID")).setText("");
                        ((Label) pane.lookup("#Extra3desc")).setText("");
                        ((Label) pane.lookup("#Extra3price")).setText("");
                        ((Label) pane.lookup("#Extra3date")).setText("");
                        ((Label) pane.lookup("#Extra4ID")).setText("");
                        ((Label) pane.lookup("#Extra4desc")).setText("");
                        ((Label) pane.lookup("#Extra4price")).setText("");
                        ((Label) pane.lookup("#Extra4date")).setText("");


                        conn = sqlController.openConnection();
                        String extrasQuery = "SELECT extrano, description, amount, date FROM extra WHERE reservationno = " + currentReservation + ";";
                        String reservationQuery = "SELECT price, DATEDIFF(dateTo, dateFrom) as days FROM Reservation where reservationno = " + currentReservation + ";";
                        PreparedStatement extrasStatement = conn.prepareStatement(extrasQuery);
                        PreparedStatement reservationStatement = conn.prepareStatement(reservationQuery);
                        rs = extrasStatement.executeQuery();
                        int rowCount = 1; //for the loop
                        //System.out.println(currentExtraNum); //testing current Extra Num
                        while (rs.next() && rowCount < 5) {
                            String extraId = String.format("#Extra%d", rowCount++);
                            ((Label) pane.lookup(String.format("%sID", extraId))).setText(rs.getString("extrano")); //adding the ExtraNum
                            ((Label) pane.lookup(String.format("%sdesc", extraId))).setText(rs.getString("description"));
                            ((Label) pane.lookup(String.format("%sprice", extraId))).setText(rs.getString("amount"));
                            ((Label) pane.lookup(String.format("%sdate", extraId))).setText(rs.getString("date"));
                        }
                        rs = reservationStatement.executeQuery();
                        rs.next();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    Integer[] extraList = getExtras(currentReservation);
                    if (extraList.length == 0) { // if we don't get anything back, something went wrong and we close
                        Alert b = new Alert(Alert.AlertType.ERROR);
                        b.setContentText("No Extras Found!");
                        b.show();

                    } else {
                        ((ComboBox<Integer>) pane.lookup("#findExtra")).setItems(FXCollections.observableList(Arrays.asList(extraList)));
                        ((ComboBox<Integer>) pane.lookup("#findExtra")).getSelectionModel().select(extraList[0]);
                        Scene editextras = new Scene(pane);
                        Stage editextra = new Stage();
                        editextra.setScene(editextras);
                        editextra.show();
                    }
                }
            } catch (SQLException e) {
                System.out.print(e);
                sc.closeConnection();
            }
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Invalid values entered!");
            a.show();
            //clear values if invalid
        }

    }

    public void findRoomButton(ActionEvent event) {
        SqlController sc = null;
        sc = new SqlController();
        ResultSet results = null;
        String roomType = (String) txtRoomType.getValue();
        List<Integer> roomNoList = new ArrayList<>();
        List<Integer> roomNoResvList = new ArrayList<>();
        boolean hasReservations = false;
        List<Integer> comparedList = new ArrayList<>();


        try {
            //Get all roomnumbers that compile with roomtype criteria
            conn = sc.openConnection();
            pst = conn.prepareStatement("SELECT roomno from room WHERE roomtype=?");
            pst.setString(1, roomType);
            results = pst.executeQuery();

            //populate List with roomno
            while (results.next()) {
                roomNoList.add(results.getInt("roomno"));

            }
            if (!roomNoList.isEmpty()) {
                pst = conn.prepareStatement("SELECT roomno FROM Reservation WHERE dateFrom BETWEEN ? AND ? AND dateTo BETWEEN ? AND ? AND roomType = ? ");
                //Array roomNoArray = conn.createArrayOf("Int", roomNoList.toArray());
                pst.setDate(1, java.sql.Date.valueOf(txtcheckIn.getValue()));
                pst.setDate(2, java.sql.Date.valueOf(txtCheckout.getValue()));
                pst.setDate(3, java.sql.Date.valueOf(txtcheckIn.getValue()));
                pst.setDate(4, java.sql.Date.valueOf(txtCheckout.getValue()));
                pst.setString(5, roomType);
                System.out.println(pst.toString());
                results = pst.executeQuery();

                if (results.isBeforeFirst()) {


                    while (results.next()) {
                        int i = 0;
                        System.out.println("Looped this list for - " + i + " Times");
                        //get list of values

                        roomNoResvList.add(results.getInt("roomno"));
                        System.out.println(roomNoResvList);
                    }
                    hasReservations = true;
                }


                if (hasReservations) {

                    for (Integer roomno : roomNoList) {
                        boolean roomCheck = false;
                        for (Integer resv : roomNoResvList) {
                            if (roomno != resv) {
                                roomCheck = true;
                            }
                        }

                        if (roomCheck == false) {
                            comparedList.add(roomno);
                            System.out.println(comparedList);

                        }

                    }
                    System.out.println(comparedList);
                    roomSelect.setItems(FXCollections.observableList(comparedList));
                    if(comparedList.isEmpty())
                    {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setContentText("No Matching room types available!");
                        a.show();
                        conn.close();
                    }

                } else {
                    //use results from first query
                    System.out.println("" + roomNoList.size());
                    roomSelect.setItems(FXCollections.observableList(roomNoList));

                }
                conn.close();

            } else {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("No Matching room types available!");
                a.show();
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            sc.closeConnection();
        }
        try {
            //Get all roomnumbers that compile with roomtype criteria
            conn = sc.openConnection();

            pst = conn.prepareStatement("SELECT pricepernight from room WHERE roomType=?");
            pst.setString(1, roomType);

            System.out.println(pst.toString());
            results = pst.executeQuery();
            if(results.next()) {
                String price = results.getString(1);

                txtroomPrice.setText(price);

            }
            }catch(SQLException e){
            System.out.print(e);
            sc.closeConnection();

        }

    }

    public void PreviewBill(javafx.event.ActionEvent event) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("Bill.fxml"));

        SqlController sqlController = new SqlController();

        try {
            //clear exsisting panes
            ((Label) pane.lookup("#Extra1desc")).setText("");
            ((Label) pane.lookup("#Extra1price")).setText("");
            ((Label) pane.lookup("#Extra1date")).setText("");
            ((Label) pane.lookup("#Extra2desc")).setText("");
            ((Label) pane.lookup("#Extra2price")).setText("");
            ((Label) pane.lookup("#Extra2date")).setText("");
            ((Label) pane.lookup("#Extra3desc")).setText("");
            ((Label) pane.lookup("#Extra3price")).setText("");
            ((Label) pane.lookup("#Extra3date")).setText("");
            ((Label) pane.lookup("#Extra4desc")).setText("");
            ((Label) pane.lookup("#Extra4price")).setText("");
            ((Label) pane.lookup("#Extra4date")).setText("");

            conn = sqlController.openConnection();
            String extrasQuery = "SELECT description, amount, date FROM extra WHERE reservationno = " + currentReservation + ";";
            String reservationQuery = "SELECT price, DATEDIFF(dateTo, check_in) as days FROM Reservation where reservationno = " + currentReservation + ";"; //changed dateFrom to check_in
            PreparedStatement extrasStatement = conn.prepareStatement(extrasQuery);
            PreparedStatement reservationStatement = conn.prepareStatement(reservationQuery);
            rs = extrasStatement.executeQuery();

            double totalPrice = 0.0;
            int rowCount = 1; //for the loop (Might need to be changed to 2 for second loop)
            while (rs.next() && rowCount < 5) {
                String extraId = String.format("#Extra%d", rowCount++);
                ((Label) pane.lookup(String.format("%sdesc", extraId))).setText(rs.getString("description"));
                ((Label) pane.lookup(String.format("%sprice", extraId))).setText(rs.getString("amount"));
                ((Label) pane.lookup(String.format("%sdate", extraId))).setText(rs.getString("date"));
                totalPrice += rs.getDouble("amount");
            }

            rs = reservationStatement.executeQuery();
            while (rs.next()) {
                int numDays = rs.getInt("days");
                double price = rs.getDouble("price");
                totalPrice += numDays * price;
                ((Label) pane.lookup("#roomprice")).setText(String.valueOf(price));
                ((Label) pane.lookup("#Nights")).setText(String.valueOf(numDays));
                ((Label) pane.lookup("#roompricetotal")).setText(String.valueOf(price * numDays));


                //making other unused panes empty text fields

                ((Label) pane.lookup("#total")).setText(String.valueOf(totalPrice));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Scene Bill = new Scene(pane);
        Stage popup = new Stage();
        popup.setScene(Bill);
        popup.show();
    }

    public void ShowBill(javafx.event.ActionEvent event) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("FinalBill.fxml"));

        SqlController sqlController = new SqlController();

        try {
            //clear exsisting panes
            ((Label) pane.lookup("#Extra1desc")).setText("");
            ((Label) pane.lookup("#Extra1price")).setText("");
            ((Label) pane.lookup("#Extra1date")).setText("");
            ((Label) pane.lookup("#Extra2desc")).setText("");
            ((Label) pane.lookup("#Extra2price")).setText("");
            ((Label) pane.lookup("#Extra2date")).setText("");
            ((Label) pane.lookup("#Extra3desc")).setText("");
            ((Label) pane.lookup("#Extra3price")).setText("");
            ((Label) pane.lookup("#Extra3date")).setText("");
            ((Label) pane.lookup("#Extra4desc")).setText("");
            ((Label) pane.lookup("#Extra4price")).setText("");
            ((Label) pane.lookup("#Extra4date")).setText("");

            conn = sqlController.openConnection();
            String extrasQuery = "SELECT description, amount, date FROM extra WHERE reservationno = " + currentReservation + ";";
            String reservationQuery = "SELECT price, DATEDIFF(check_out, check_in) as days FROM Reservation where reservationno = " + currentReservation + ";"; //changed dateFrom to check_in
            PreparedStatement extrasStatement = conn.prepareStatement(extrasQuery);
            PreparedStatement reservationStatement = conn.prepareStatement(reservationQuery);
            rs = extrasStatement.executeQuery();

            double totalPrice = 0.0;
            int rowCount = 1; //for the loop (Might need to be changed to 2 for second loop)
            while (rs.next() && rowCount < 5) {
                String extraId = String.format("#Extra%d", rowCount++);
                ((Label) pane.lookup(String.format("%sdesc", extraId))).setText(rs.getString("description"));
                ((Label) pane.lookup(String.format("%sprice", extraId))).setText(rs.getString("amount"));
                ((Label) pane.lookup(String.format("%sdate", extraId))).setText(rs.getString("date"));
                totalPrice += rs.getDouble("amount");
            }

            rs = reservationStatement.executeQuery();
            while (rs.next()) {
                int numDays = rs.getInt("days");
                double price = rs.getDouble("price");
                totalPrice += numDays * price;
                ((Label) pane.lookup("#roomprice")).setText(String.valueOf(price));
                ((Label) pane.lookup("#Nights")).setText(String.valueOf(numDays));
                ((Label) pane.lookup("#roompricetotal")).setText(String.valueOf(price * numDays));


                //making other unused panes empty text fields

                ((Label) pane.lookup("#total")).setText(String.valueOf(totalPrice));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Scene Bill = new Scene(pane);
        Stage popup = new Stage();
        popup.setScene(Bill);
        popup.show();
    }
    public void editFindRoom (ActionEvent event)  {
        SqlController sc = null;
        sc = new SqlController();
        ResultSet results = null;
        String roomType = (String) txtRoomType.getValue();
        List<Integer> roomNoList = new ArrayList<>();
        List<Integer> roomNoResvList = new ArrayList<>();
        boolean hasReservations = false;
        List<Integer> comparedList = new ArrayList<>();


        try {
            //Get all roomnumbers that compile with roomtype criteria
            conn = sc.openConnection();
            pst = conn.prepareStatement("SELECT roomno from room WHERE roomtype=?");
            pst.setString(1, roomType);
            results = pst.executeQuery();

            //populate List with roomno
            while (results.next()) {
                roomNoList.add(results.getInt("roomno"));

            }
            if (!roomNoList.isEmpty()) {
                pst = conn.prepareStatement("SELECT roomno FROM Reservation WHERE dateFrom BETWEEN ? AND ? AND dateTo BETWEEN ? AND ? AND roomType = ? ");
                //Array roomNoArray = conn.createArrayOf("Int", roomNoList.toArray());
                pst.setDate(1, java.sql.Date.valueOf(txtCheckIn.getValue()));
                pst.setDate(2, java.sql.Date.valueOf(txtcheckout.getValue()));
                pst.setDate(3, java.sql.Date.valueOf(txtCheckIn.getValue()));
                pst.setDate(4, java.sql.Date.valueOf(txtcheckout.getValue()));
                pst.setString(5, roomType);
                System.out.println(pst.toString());
                results = pst.executeQuery();

                if (results.isBeforeFirst()) {


                    while (results.next()) {
                        int i = 0;
                        System.out.println("Looped this list for - " + i + " Times");
                        //get list of values

                        roomNoResvList.add(results.getInt("roomno"));
                    }
                    hasReservations = true;
                }


                if (hasReservations) {

                    for (Integer roomno : roomNoList) {
                        boolean roomCheck = false;
                        for (Integer resv : roomNoResvList) {
                            if (roomno != resv) {
                                roomCheck = true;
                            }
                        }

                        if (roomCheck) {
                            comparedList.add(roomno);
                            System.out.println(comparedList);
                        }
                    }
                    System.out.println(comparedList.size());
                    editRoomSelect.setItems(FXCollections.observableList(comparedList));

                } else {
                    //use results from first query
                    System.out.println("" + roomNoList.size());
                    editRoomSelect.setItems(FXCollections.observableList(roomNoList));
                }
                conn.close();

            } else {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("No Matching room types available!");
                a.show();
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            sc.closeConnection();
        }

    }


    public String formatAddress() {
        return String.format("%s,%s,%s",txtCustomerCountry.getText(),txtCustomerTown.getText(),txtCustomerPostcode.getText());
    }

    public String[] getCharges() {
        SqlController sc = new SqlController();
        try {
            conn = sc.openConnection();
            pst = conn.prepareStatement("SELECT * from charges");
            ResultSet results = pst.executeQuery();
            List<String> chargeList = new ArrayList<>();

            while (results.next()) {
                chargeList.add(results.getString("Charge"));
            }
            return chargeList.toArray(new String[0]);
        }
        catch (Exception e){
            e.printStackTrace();
            return new String[0];
        }
    }

    public Integer[] getExtras(int reservationNumber) {
        SqlController sc = new SqlController();
        try {
            conn = sc.openConnection();
            pst = conn.prepareStatement("SELECT extrano FROM extra WHERE reservationno = " + reservationNumber);
            ResultSet results = pst.executeQuery();
            List<Integer> extraList = new ArrayList<>();

            while (results.next()) {
                extraList.add(results.getInt("extrano"));
            }
            return extraList.toArray(new Integer[0]);
        }
        catch (Exception e){
            e.printStackTrace();
            return new Integer[0];
        }
    }

}
