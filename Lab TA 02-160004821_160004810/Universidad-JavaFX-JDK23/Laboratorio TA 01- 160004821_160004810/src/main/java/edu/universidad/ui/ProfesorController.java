package edu.universidad.ui;

import edu.universidad.persistence.H2DB;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class ProfesorController {

    @FXML private TextField txtId;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTipo;

    @FXML private TableView<Row> tabla;
    @FXML private TableColumn<Row, Number> colId;
    @FXML private TableColumn<Row, String> colNombres;
    @FXML private TableColumn<Row, String> colApellidos;
    @FXML private TableColumn<Row, String> colEmail;
    @FXML private TableColumn<Row, String> colTipo;
    @FXML private Label lblEstado;

    private final ObservableList<Row> datos = FXCollections.observableArrayList();

    @FXML
    private void initialize(){
        colId.setCellValueFactory(c -> c.getValue().idProperty());
        colNombres.setCellValueFactory(c -> c.getValue().nombresProperty());
        colApellidos.setCellValueFactory(c -> c.getValue().apellidosProperty());
        colEmail.setCellValueFactory(c -> c.getValue().emailProperty());
        colTipo.setCellValueFactory(c -> c.getValue().tipoProperty());
        tabla.setItems(datos);

        tabla.getSelectionModel().selectedItemProperty().addListener((o,old,sel)->{
            if (sel!=null){
                txtId.setText(Double.toString(sel.getId()));
                txtNombres.setText(sel.getNombres());
                txtApellidos.setText(sel.getApellidos());
                txtEmail.setText(sel.getEmail());
                txtTipo.setText(sel.getTipo());
            }
        });

        recargar();
    }

    @FXML private void onCrearActualizar(ActionEvent e){
        Row r = leer(); if (r==null) return;
        String sql = "MERGE INTO PROFESOR (ID,NOMBRES,APELLIDOS,EMAIL,TIPOCONTRATO) KEY(ID) VALUES (?,?,?,?,?)";
        try (Connection con = H2DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)){
            ps.setDouble(1, r.getId());
            ps.setString(2, r.getNombres());
            ps.setString(3, r.getApellidos());
            ps.setString(4, r.getEmail());
            ps.setString(5, r.getTipo());
            ps.executeUpdate();
            info("Profesor guardado.");
            recargar(); onLimpiar(null);
        } catch (SQLException ex){ error("Error guardando profesor: " + ex.getMessage()); }
    }

    @FXML private void onEliminar(ActionEvent e){
        Row sel = tabla.getSelectionModel().getSelectedItem();
        if (sel==null){ error("Selecciona un profesor para eliminar."); return; }
        String sql = "DELETE FROM PROFESOR WHERE ID=?";
        try (Connection con = H2DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)){
            ps.setDouble(1, sel.getId());
            int n = ps.executeUpdate();
            if (n==0) info("No se eliminó ningún registro."); else info("Profesor eliminado.");
            recargar(); onLimpiar(null);
        } catch (SQLException ex){ error("Error eliminando profesor: " + ex.getMessage()); }
    }

    @FXML private void onLimpiar(ActionEvent e){
        txtId.clear(); txtNombres.clear(); txtApellidos.clear(); txtEmail.clear(); txtTipo.clear();
        tabla.getSelectionModel().clearSelection();
    }

    @FXML private void onRecargar(ActionEvent e){ recargar(); }

    private void recargar(){
        datos.clear();
        String sql = "SELECT ID,NOMBRES,APELLIDOS,EMAIL,TIPOCONTRATO FROM PROFESOR ORDER BY ID";
        try (Connection con = H2DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while (rs.next()){
                double id = rs.getDouble(1);
                String nom = rs.getString(2);
                String ape = rs.getString(3);
                String email = rs.getString(4);
                String tipo = rs.getString(5);
                datos.add(new Row(id, nom, ape, email, tipo));
            }
            estado("Datos recargados");
        } catch (SQLException ex){ error("Error listando profesores: " + ex.getMessage()); }
    }

    private Row leer(){
        double id;
        try { id = Double.parseDouble(txtId.getText().trim()); }
        catch(Exception ex){ error("El campo 'ID' debe ser numérico."); return null; }
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String email = txtEmail.getText().trim();
        String tipo = txtTipo.getText().trim();
        if (nombres.isBlank()){ error("El campo 'Nombres' es obligatorio."); return null; }
        if (apellidos.isBlank()){ error("El campo 'Apellidos' es obligatorio."); return null; }
        if (!email.isBlank() && !email.matches("^.+@.+\\..+$")){ error("Email inválido."); return null; }
        return new Row(id, nombres, apellidos, email, tipo);
    }

    private void info(String m){
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null); a.setTitle(null); a.setContentText(m);
        a.getDialogPane().setGraphic(null); a.showAndWait();
    }
    private void error(String m){
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null); a.setTitle(null); a.setContentText(m);
        a.getDialogPane().setGraphic(null); a.showAndWait();
    }
    private void estado(String m){ if (lblEstado!=null) lblEstado.setText(m); }

    public static class Row{
        private final DoubleProperty id = new SimpleDoubleProperty();
        private final StringProperty nombres = new SimpleStringProperty();
        private final StringProperty apellidos = new SimpleStringProperty();
        private final StringProperty email = new SimpleStringProperty();
        private final StringProperty tipo = new SimpleStringProperty();
        public Row(double id, String n, String a, String e, String t){
            this.id.set(id); this.nombres.set(n); this.apellidos.set(a); this.email.set(e); this.tipo.set(t);
        }
        public DoubleProperty idProperty(){ return id; }
        public StringProperty nombresProperty(){ return nombres; }
        public StringProperty apellidosProperty(){ return apellidos; }
        public StringProperty emailProperty(){ return email; }
        public StringProperty tipoProperty(){ return tipo; }
        public double getId(){ return id.get(); }
        public String getNombres(){ return nombres.get(); }
        public String getApellidos(){ return apellidos.get(); }
        public String getEmail(){ return email.get(); }
        public String getTipo(){ return tipo.get(); }
    }
}
