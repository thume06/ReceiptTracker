package tracker;

import java.io.*;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class TrackerController implements Initializable, ControlledScreen {
    ArrayList<Receipt> receiptArray = new ArrayList<Receipt>();
    Alert alert = new Alert(Alert.AlertType.INFORMATION);

    private ScreensController myController;
    private Main mainClass;

    @FXML ChoiceBox payer;
    @FXML ChoiceBox location;
    @FXML ChoiceBox category;
    @FXML ListView receiptList;
    @FXML TextField price;
    @FXML TextField tristanTotal;
    @FXML TextField anneTotal;
    @FXML Label lblCredit;

    public void initialize(URL url, ResourceBundle rb) {
        mainClass = Main.getInstance();
        lblCredit.setText("Even totals; no credit");
        tristanTotal.setText("0.00");
        anneTotal.setText("0.00");
        payer.setItems(FXCollections.observableArrayList(
                "Tristan", "Anne", "Don't Count"));
        payer.setValue("Tristan");

        category.setItems(FXCollections.observableArrayList(
                "Groceries", "Gas", "Restaurants", "Other"));
        category.setValue("Groceries");

        location.setItems(FXCollections.observableArrayList(
                "Publix", "Winn Dixie","Other"));
        location.setValue("Publix");

        Load();
    }

    public void setScreenParent(ScreensController screenParent){
        myController = screenParent;
    }

    @FXML public void Add(){
        String pa = String.valueOf(payer.getSelectionModel().getSelectedItem());
        String c = String.valueOf(category.getSelectionModel().getSelectedItem());
        String l = String.valueOf(location.getSelectionModel().getSelectedItem());
        double pr = Double.valueOf(price.getText());

        receiptArray.add(new Receipt(c, pa, pr, l));
        receiptList.getItems().add(receiptArray.get(receiptArray.size() - 1).toString());

        int count = 0;
        double tristan = 0.0;
        double anne = 0.0;
        while(count < receiptArray.size()){
            if(receiptArray.get(count).getPayer().equals("Tristan")){
                tristan = tristan + receiptArray.get(count).getPrice();
            }
            else{
                anne = anne + receiptArray.get(count).getPrice();
            }
            count = count + 1;
        }
        tristanTotal.setText(Append(String.valueOf(tristan)));
        anneTotal.setText(Append(String.valueOf(anne)));

        double credit;
        double tristanCredit = tristan/2;
        double anneCredit = anne/2;
        if(tristan > anne){
            credit = tristanCredit - anneCredit;
            String cr = String.valueOf(credit);
            lblCredit.setText("Tristan has $" + Append(cr) + " credit");
        }
        else if(anne > tristan){
            credit = anneCredit - tristanCredit;
            String cr = String.valueOf(credit);
            lblCredit.setText("Anne has $" + Append(cr) + " credit");
        }
        else{
            lblCredit.setText("Even totals; no credit");
        }
    }

    @FXML public void Remove(){
        int selectionIndex =receiptList.getSelectionModel().getSelectedIndex();
        receiptList.getItems().remove(selectionIndex);
        receiptList.getSelectionModel().select(selectionIndex);
        String pa = receiptArray.get(selectionIndex).getPayer();
        Double pr = receiptArray.get(selectionIndex).getPrice();
        receiptArray.remove(selectionIndex);
        receiptList.getSelectionModel().clearSelection();

        Double tristan = Double.valueOf(tristanTotal.getText());
        Double anne = Double.valueOf(anneTotal.getText());
        Double tristanCredit = tristan/2;
        Double anneCredit = anne/2;
        Double credit;
        if(pa.equals("Tristan")){
            tristan = tristan - pr;
            tristanTotal.setText(Append(String.valueOf(tristan)));
            tristanCredit = tristan/2;
            if(tristan > anne){
                credit = tristanCredit - anneCredit;
                String cr = String.valueOf(credit);
                lblCredit.setText("Tristan has $" + Append(cr) + " credit");
            }
            else if (anne > tristan){
                credit = anneCredit - tristanCredit;
                String cr = String.valueOf(credit);
                lblCredit.setText("Anne has $" + Append(cr) + " credit");
            }
            else{
                lblCredit.setText("Even totals; no credit");
            }
        }
        else{
            anne = anne - pr;
            anneTotal.setText(Append(String.valueOf(anne)));
            anneCredit = anne/2;
            if(anne > tristan){
                credit = anneCredit - tristanCredit;
                String cr = String.valueOf(credit);
                lblCredit.setText("Anne has $" + Append(cr) + " credit");
            }
            else if(tristan > anne){
                credit = tristanCredit - anneCredit;
                String cr = String.valueOf(credit);
                lblCredit.setText("Tristan has $" + Append(cr) + " credit");
            }
            else{
                lblCredit.setText("Even totals; no credit");
            }
        }
    }

    public String Append(String s){
        String str = s;
        if(str.substring(Math.max(str.length() - 2, 0)).equals(".0")){
            str = (str + "0");
        }
        return str;
    }

    public void Save(){
        try{
            FileOutputStream fos= new FileOutputStream("receipts.ser");
            ObjectOutputStream oos= new ObjectOutputStream(fos);
            oos.writeObject(receiptArray);
            oos.close();
            fos.close();
        }
        catch(IOException ioe){
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Failed to save receipts.");
            alert.showAndWait();
            ioe.printStackTrace();
        }
    }

    public void Load(){
        try {
            FileInputStream fis = new FileInputStream("receipts.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            receiptArray = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
        }
        catch(IOException ioe){
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load previous receipts.");
            alert.showAndWait();
            ioe.printStackTrace();
            return;
        }
        catch(ClassNotFoundException c){
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load previous receipts.");
            alert.showAndWait();
            System.out.println("Class not found");
            c.printStackTrace();
            return;
        }

        receiptList.getItems().clear();
        int count = 0;
        while(count < receiptArray.size()){
            receiptList.getItems().add(receiptArray.get(count).toString());
            count = count + 1;
        }

        count = 0;
        double tristan = 0.0;
        double anne = 0.0;
        while(count < receiptArray.size()){
            if(receiptArray.get(count).getPayer().equals("Tristan")){
                tristan = tristan + receiptArray.get(count).getPrice();
            }
            else{
                anne = anne + receiptArray.get(count).getPrice();
            }
            count = count + 1;
        }
        tristanTotal.setText(Append(String.valueOf(tristan)));
        anneTotal.setText(Append(String.valueOf(anne)));

        double credit;
        double tristanCredit = tristan/2;
        double anneCredit = anne/2;
        if(tristan > anne){
            credit = tristanCredit - anneCredit;
            String cr = String.valueOf(credit);
            lblCredit.setText("Tristan has $" + Append(cr) + " credit");
        }
        else if(anne > tristan){
            credit = anneCredit - tristanCredit;
            String cr = String.valueOf(credit);
            lblCredit.setText("Anne has $" + Append(cr) + " credit");
        }
    }
}
