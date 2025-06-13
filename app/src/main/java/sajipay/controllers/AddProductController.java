package sajipay.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import sajipay.enums.BeverageSize;
import sajipay.helper.AlertHelper;
import sajipay.helper.FormHelper;
import sajipay.models.Beverage;
import sajipay.models.Food;
import sajipay.models.Management;
import sajipay.services.AuthService;
import sajipay.services.NavigationManager;

import java.net.URL;
import java.util.ResourceBundle;

public class AddProductController implements Initializable {

    // --- FXML Fields for Common Inputs ---
    @FXML
    private TextField nameField;
    @FXML
    private TextField descField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField costField;
    @FXML
    private TextField stockField;
    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private VBox dynamicFieldsContainer;
    @FXML
    private Button submitButton;

    // --- Dynamic Input Fields (instantiated in code) ---
    private TextField caloriesField;
    private CheckBox vegCheck;
    private CheckBox icedCheck;
    private ComboBox<BeverageSize> sizeCombo;
    private TextField sugarField;

    private final Management management = Management.getInstance();
    private final AuthService authService = AuthService.getInstance();
    private final NavigationManager navigationManager = NavigationManager.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeCombo.getItems().addAll("Food", "Beverage");
        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateFormForProductType(newVal));
    }

    private void updateFormForProductType(String productType) {
        dynamicFieldsContainer.getChildren().clear();
        if (productType == null) {
            submitButton.setDisable(true);
            return;
        }

        GridPane dynamicGrid = createDynamicGrid();

        if ("Food".equals(productType)) {
            caloriesField = new TextField();
            vegCheck = new CheckBox();
            dynamicGrid.add(new Label("Calories:"), 0, 0);
            dynamicGrid.add(caloriesField, 1, 0);
            dynamicGrid.add(new Label("Is Vegetarian:"), 0, 1);
            dynamicGrid.add(vegCheck, 1, 1);
        } else if ("Beverage".equals(productType)) {
            icedCheck = new CheckBox();
            sizeCombo = new ComboBox<>();
            sizeCombo.getItems().addAll(BeverageSize.values());
            sugarField = new TextField();
            dynamicGrid.add(new Label("Is Iced:"), 0, 0);
            dynamicGrid.add(icedCheck, 1, 0);
            dynamicGrid.add(new Label("Size:"), 0, 1);
            dynamicGrid.add(sizeCombo, 1, 1);
            dynamicGrid.add(new Label("Sugar Level (%):"), 0, 2);
            dynamicGrid.add(sugarField, 1, 2);
        }

        dynamicFieldsContainer.getChildren().add(dynamicGrid);
        submitButton.setDisable(false);
    }

    @FXML
    void handleSubmitButtonAction() {
        try {
            String type = typeCombo.getValue();
            if ("Food".equals(type)) {
                addFoodProduct();
            } else if ("Beverage".equals(type)) {
                addBeverageProduct();
            } else {
                throw new IllegalArgumentException("Please select a product type.");
            }
        } catch (Exception e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void addFoodProduct() {
        Food food = new Food();
        food.name = getRequiredTextField(nameField, "Name");
        food.description = getRequiredTextField(descField, "Description");
        food.price = parseDouble(priceField, "Price");
        food.setCost(parseDouble(costField, "Cost"));
        food.setStock(parseInt(stockField, "Stock"));
        food.calories = parseInt(caloriesField, "Calories");
        food.isVegetarian = vegCheck.isSelected();

        management.addProducts(food, authService.getCurrentUser());
        AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Food product '" + food.name + "' added.");
        clearAllFields();
    }

    private void addBeverageProduct() {
        Beverage beverage = new Beverage();
        beverage.name = getRequiredTextField(nameField, "Name");
        beverage.description = getRequiredTextField(descField, "Description");
        beverage.price = parseDouble(priceField, "Price");
        beverage.setCost(parseDouble(costField, "Cost"));
        beverage.setStock(parseInt(stockField, "Stock"));
        beverage.isIced = icedCheck.isSelected();
        beverage.size = sizeCombo.getValue();
        beverage.sugarLevel = parseInt(sugarField, "Sugar Level");

        if (beverage.size == null) {
            throw new IllegalArgumentException("Beverage size must be selected.");
        }

        management.addProducts(beverage, authService.getCurrentUser());
        AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success",
                "Beverage product '" + beverage.name + "' added.");
        clearAllFields();
    }

    @FXML
    void handleBackButtonAction() {
        // Kembali ke dasbor Chef
        navigationManager.navigateToChefDashboard();
    }

    // --- Helper & Validation Methods ---

    private GridPane createDynamicGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        return grid;
    }

    private String getRequiredTextField(TextField field, String name) {
        if (field.getText().trim().isEmpty())
            throw new IllegalArgumentException(name + " is required.");
        return field.getText().trim();
    }

    private double parseDouble(TextField field, String name) {
        try {
            return Double.parseDouble(getRequiredTextField(field, name));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid number for " + name + ".");
        }
    }

    private int parseInt(TextField field, String name) {
        try {
            return Integer.parseInt(getRequiredTextField(field, name));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid integer for " + name + ".");
        }
    }

    private void clearAllFields() {
        FormHelper.clearFields(nameField, descField, priceField, costField, stockField);
        typeCombo.getSelectionModel().clearSelection();
        dynamicFieldsContainer.getChildren().clear();
        submitButton.setDisable(true);
    }
}
