package tracker;

import java.io.*;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class TrackerController implements Initializable, ControlledScreen {
    ArrayList<Receipt> receiptArray = new ArrayList<Receipt>();
    ArrayList<Receipt> loadedArray = new ArrayList<Receipt>();
    ArrayList<String> lastPeriod = new ArrayList<String>();
    ArrayList<String> periods = new ArrayList<String>();
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
    TextInputDialog dialog = new TextInputDialog();

    private ScreensController myController;
    private Main mainClass;

    @FXML ChoiceBox periodSelect;
    @FXML ChoiceBox payer;
    @FXML ChoiceBox category;
    @FXML ListView receiptList;
    @FXML TextField price;
    @FXML TextField tristanTotal;
    @FXML TextField anneTotal;
    @FXML Label lblCredit;
    @FXML Label lblPeriod;

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

        periodSelect.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> PeriodSwitch());

        Load();

    }

    public void setScreenParent(ScreensController screenParent){
        myController = screenParent;
    }

    @FXML public void Add(){
        if(periods.size() < 1){
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Please add a period before adding receipts.");
            alert.showAndWait();
            return;
        }

        String pa = String.valueOf(payer.getSelectionModel().getSelectedItem());
        String c = String.valueOf(category.getSelectionModel().getSelectedItem());
        double pr = Double.valueOf(price.getText());
        String per = String.valueOf(periodSelect.getSelectionModel().getSelectedItem());

        receiptArray.add(new Receipt(c, pa, pr, per));
        loadedArray.add(new Receipt(c, pa, pr, per));
        receiptList.getItems().add(Append(loadedArray.get(loadedArray.size() - 1).toString()));

        int count = 0;
        double tristan = 0.0;
        double anne = 0.0;
        while(count < loadedArray.size()){
            if(loadedArray.get(count).getPayer().equals("Tristan")){
                tristan = tristan + loadedArray.get(count).getPrice();
            }
            else if(loadedArray.get(count).getPayer().equals("Anne")){
                anne = anne + loadedArray.get(count).getPrice();
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
        String cat = loadedArray.get(selectionIndex).getCategory();
        String pa = loadedArray.get(selectionIndex).getPayer();
        Double pr = loadedArray.get(selectionIndex).getPrice();
        String per = loadedArray.get(selectionIndex).getPeriod();
        loadedArray.remove(selectionIndex);
        int count = 0;
        while(count < receiptArray.size()){
            if(receiptArray.get(count).getCategory().equals(cat) && receiptArray.get(count).getPayer().equals(pa) && receiptArray.get(count).getPrice().equals(pr) && receiptArray.get(count).getPeriod().equals(per)){
                receiptArray.remove(count);
                count = count - 1;
            }
            count = count + 1;
        }
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
        else if(pa.equals("Anne")){
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

        try{
            FileOutputStream fos= new FileOutputStream("lastperiod.ser");
            ObjectOutputStream oos= new ObjectOutputStream(fos);
            oos.writeObject(lastPeriod);
            oos.close();
            fos.close();
        }
        catch(IOException ioe){
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Failed to save the last period.");
            alert.showAndWait();
            ioe.printStackTrace();
        }

        try{
            FileOutputStream fos= new FileOutputStream("periods.ser");
            ObjectOutputStream oos= new ObjectOutputStream(fos);
            oos.writeObject(periods);
            oos.close();
            fos.close();
        }
        catch(IOException ioe){
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Failed to save periods.");
            alert.showAndWait();
            ioe.printStackTrace();
        }
    }

    public void Load(){
        //Begin loading from .ser
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
        try {
            FileInputStream fis = new FileInputStream("lastperiod.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            lastPeriod = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
        }
        catch(IOException ioe){
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load last period.");
            alert.showAndWait();
            ioe.printStackTrace();
            return;
        }
        catch(ClassNotFoundException c){
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load last period.");
            alert.showAndWait();
            System.out.println("Class not found");
            c.printStackTrace();
            return;
        }
        try {
            FileInputStream fis = new FileInputStream("periods.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            periods = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
        }
        catch(IOException ioe){
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load previous periods.");
            alert.showAndWait();
            ioe.printStackTrace();
            return;
        }
        catch(ClassNotFoundException c){
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load previous periods.");
            alert.showAndWait();
            System.out.println("Class not found");
            c.printStackTrace();
            return;
        }
        //End loading from .ser

        periodSelect.setItems(FXCollections.observableArrayList(periods));
        periodSelect.setValue(lastPeriod.get(0));

        int count = 0;
        receiptList.getItems().clear();
        while(count < receiptArray.size()){
            if(receiptArray.get(count).getPeriod().equals(lastPeriod.get(0))){
                String cat = receiptArray.get(count).getCategory();
                String pay = receiptArray.get(count).getPayer();
                Double pr = receiptArray.get(count).getPrice();
                String per = receiptArray.get(count).getPeriod();
                loadedArray.add(new Receipt(cat, pay, pr, per));
                receiptList.getItems().add(Append(receiptArray.get(count).toString()));
            }
            count = count + 1;
        }

        count = 0;
        double tristan = 0.0;
        double anne = 0.0;
        while(count < loadedArray.size()){
            if(loadedArray.get(count).getPayer().equals("Tristan")){
                tristan = tristan + loadedArray.get(count).getPrice();
            }
            else if(loadedArray.get(count).getPayer().equals("Anne")){
                anne = anne + loadedArray.get(count).getPrice();
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

    @FXML public void Clear(){
        confirm.setTitle("Clear Receipts");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to clear all of the receipts for this period? You will not be able to undo this.");
        confirm.setGraphic(null);

        ButtonType buttonTypeOne = new ButtonType("Continue");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirm.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.get() == buttonTypeCancel){
            return;
        }
        loadedArray.clear();
        int count = 0;
        String per = String.valueOf(periodSelect.getSelectionModel().getSelectedItem());
        while(count < receiptArray.size()){
            if(receiptArray.get(count).getPeriod().equals(per)){
                receiptArray.remove(count);
                count = count - 1;
            }
            count = count + 1;
        }
        receiptList.getItems().clear();
        tristanTotal.setText("0.00");
        anneTotal.setText("0.00");
        lblCredit.setText("Even totals; no credit");
    }


    @FXML public void NewPeriod(){
        String newPer;
        dialog.setTitle("New Period");
        dialog.setHeaderText("");
        dialog.setContentText("Please enter the name of the new period:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            newPer = result.get();
        }
        else{
            return;
        }
        periods.add(newPer);
        periodSelect.setItems(FXCollections.observableArrayList(periods));
        periodSelect.setValue(newPer);
        lastPeriod.clear();
        lastPeriod.add(newPer);
        PeriodSwitch();
    }

    @FXML public void PeriodSwitch(){
        receiptList.getItems().clear();
        loadedArray.clear();
        String per = String.valueOf(periodSelect.getSelectionModel().getSelectedItem());
        int count = 0;
        while(count < receiptArray.size()){
            if(receiptArray.get(count).getPeriod().equals(per)){
                String cat = receiptArray.get(count).getCategory();
                String pay = receiptArray.get(count).getPayer();
                Double pr = receiptArray.get(count).getPrice();
                loadedArray.add(new Receipt(cat, pay, pr, per));
                receiptList.getItems().add(Append(receiptArray.get(count).toString()));
            }
            count = count + 1;
        }

        count = 0;
        double tristan = 0.0;
        double anne = 0.0;
        while(count < loadedArray.size()){
            if(loadedArray.get(count).getPayer().equals("Tristan")){
                tristan = tristan + loadedArray.get(count).getPrice();
            }
            else if(loadedArray.get(count).getPayer().equals("Anne")){
                anne = anne + loadedArray.get(count).getPrice();
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
}
