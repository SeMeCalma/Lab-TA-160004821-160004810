package edu.universidad.ui;

import edu.universidad.model.Estudiante;
import edu.universidad.persistence.H2DB;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class EstudianteController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPromedio;
    @FXML private TextField txtSemestre;
    @FXML private CheckBox chkActivo;

    @FXML private TableView<Row> tabla;
    @FXML private TableColumn<Row, Number> colCodigo;
    @FXML private TableColumn<Row, String> colNombres;
    @FXML private TableColumn<Row, String> colApellidos;
    @FXML private TableColumn<Row, String> colEmail;
    @FXML private TableColumn<Row, Number> colPromedio;
    @FXML private TableColumn<Row, Number> colSemestre;
    @FXML private TableColumn<Row, Boolean> colActivo;

    @FXML private Label lblEstado;

    private final ObservableList<Row> datos = FXCollections.observableArrayList();

    @FXML
    private void initialize(){
        colCodigo.setCellValueFactory(c -> c.getValue().codigoProperty());
        colNombres.setCellValueFactory(c -> c.getValue().nombresProperty());
        colApellidos.setCellValueFactory(c -> c.getValue().apellidosProperty());
        colEmail.setCellValueFactory(c -> c.getValue().emailProperty());
        colPromedio.setCellValueFactory(c -> c.getValue().promedioProperty());
        colSemestre.setCellValueFactory(c -> c.getValue().semestreProperty());
        if (colActivo != null) { colActivo.setCellValueFactory(c -> c.getValue().activoProperty()); }

        tabla.setItems(datos);

        tabla.getSelectionModel().selectedItemProperty().addListener((o,old,sel)->{
            if (sel!=null){
                txtCodigo.setText(Double.toString(sel.getCodigo()));
                txtNombres.setText(sel.getNombres());
                txtApellidos.setText(sel.getApellidos());
                txtEmail.setText(sel.getEmail()==null? "": sel.getEmail());
                txtPromedio.setText(Double.toString(sel.getPromedio()));
                txtSemestre.setText(Integer.toString(sel.getSemestre()));
                if (chkActivo != null) chkActivo.setSelected(sel.isActivo());
            }
        });

        recargar();
    }

    @FXML private void onCrear(ActionEvent e){ onCrearActualizar(e); }

    @FXML private void onCrearActualizar(ActionEvent e){
        Row r = leer();
        if (r == null) return;

        String sql = "MERGE INTO ESTUDIANTE (CODIGO,NOMBRES,APELLIDOS,EMAIL,PROMEDIO,SEMESTRE,ACTIVO) KEY(CODIGO) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)){
            ps.setDouble(1, r.getCodigo());
            ps.setString(2, r.getNombres());
            ps.setString(3, r.getApellidos());
            ps.setString(4, r.getEmail());
            ps.setDouble(5, r.getPromedio());
            ps.setInt(6, r.getSemestre());
            ps.setBoolean(7, r.isActivo());
            ps.executeUpdate();
            info("Estudiante guardado.");
            recargar();
            onLimpiar(null);
        } catch (SQLException ex){
            error("Error guardando: " + ex.getMessage());
        }
    }

    @FXML private void onEliminar(ActionEvent e){
        Row sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null){ error("Selecciona una fila para eliminar."); return; }
        String sql = "DELETE FROM ESTUDIANTE WHERE CODIGO=?";
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)){
            ps.setDouble(1, sel.getCodigo());
            int n = ps.executeUpdate();
            if (n==0) info("No se eliminó ningún registro.");
            else info("Estudiante eliminado.");
            recargar();
            onLimpiar(null);
        } catch (SQLException ex){
            error("Error eliminando: "+ex.getMessage());
        }
    }

    @FXML private void onLimpiar(ActionEvent e){
        txtCodigo.clear(); txtNombres.clear(); txtApellidos.clear(); txtEmail.clear();
        txtPromedio.clear(); txtSemestre.clear(); if (chkActivo != null) chkActivo.setSelected(false);
        tabla.getSelectionModel().clearSelection();
    }

    @FXML private void onRecargar(ActionEvent e){ recargar(); }

    private void recargar(){
        datos.clear();
        String sql = "SELECT CODIGO,NOMBRES,APELLIDOS,EMAIL,PROMEDIO,SEMESTRE,ACTIVO FROM ESTUDIANTE";
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()){
            while (rs.next()){
                double codigo = rs.getDouble(1);
                String nombres = rs.getString(2);
                String apellidos = rs.getString(3);
                String email = rs.getString(4);
                double promedio = rs.getDouble(5);
                int semestre = rs.getInt(6);
                boolean activo = rs.getBoolean(7);
                datos.add(new Row(codigo, nombres, apellidos, email, promedio, semestre, activo));
            }
            estado("Datos recargados");
        } catch (SQLException ex){
            error("Error listando estudiantes: " + ex.getMessage());
        }
    }

    private Row leer(){
        String codTxt = txtCodigo.getText().trim();
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String email = txtEmail.getText().trim();
        String promTxt = txtPromedio.getText().trim();
        String semTxt  = txtSemestre.getText().trim();

        double codigo;
        try { codigo = Double.parseDouble(codTxt); }
        catch(Exception ex){ error("El campo 'Código' debe ser numérico (Ej. 10)."); return null; }

        if (nombres.isBlank()) { error("El campo 'Nombres' es obligatorio."); return null; }
        if (apellidos.isBlank()) { error("El campo 'Apellidos' es obligatorio."); return null; }
        if (!email.isBlank() && !email.matches("^.+@.+\\..+$")) { error("El campo 'Email' no tiene un formato válido."); return null; }

        double prom = 0.0;
        if (!promTxt.isBlank()) {
            try { prom = Double.parseDouble(promTxt); }
            catch(Exception ex){ error("El campo 'Promedio' debe ser numérico (Ej. 4.5)."); return null; }
            if (prom < 0 || prom > 5) { error("El 'Promedio' debe estar entre 0 y 5."); return null; }
        }

        int semestre;
        try { semestre = Integer.parseInt(semTxt); }
        catch(Exception ex){ error("El campo 'Semestre' debe ser un entero (1..12)."); return null; }
        if (semestre < 1 || semestre > 12) { error("El 'Semestre' debe estar entre 1 y 12."); return null; }

        boolean __activo = true; // al ocultar activo en UI, asumimos true
        return new Row(codigo, nombres, apellidos, email, prom, semestre, __activo);
}
    private void info(String m){
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null); a.setTitle(null);
        a.setContentText(m); a.getDialogPane().setGraphic(null);
        a.showAndWait();
    }
    private void error(String m){
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null); a.setTitle(null);
        a.setContentText(m); a.getDialogPane().setGraphic(null);
        a.showAndWait();
    }
    private void estado(String m){ if (lblEstado!=null) lblEstado.setText(m); }

    public static class Row {
        private final DoubleProperty codigo = new SimpleDoubleProperty();
        private final StringProperty nombres = new SimpleStringProperty();
        private final StringProperty apellidos = new SimpleStringProperty();
        private final StringProperty email = new SimpleStringProperty();
        private final DoubleProperty promedio = new SimpleDoubleProperty();
        private final IntegerProperty semestre = new SimpleIntegerProperty();
        private final BooleanProperty activo = new SimpleBooleanProperty();

        public Row(double codigo, String nombres, String apellidos, String email, double promedio, int semestre, boolean activo){
            this.codigo.set(codigo); this.nombres.set(nombres); this.apellidos.set(apellidos);
            this.email.set(email); this.promedio.set(promedio); this.semestre.set(semestre); this.activo.set(activo);
        }

        public Estudiante toEntity(){
            return new Estudiante(getCodigo(), getNombres(), getApellidos(), getEmail(), getCodigo(), null, isActivo(), getPromedio());
        }

        public DoubleProperty codigoProperty(){ return codigo; }
        public StringProperty nombresProperty(){ return nombres; }
        public StringProperty apellidosProperty(){ return apellidos; }
        public StringProperty emailProperty(){ return email; }
        public DoubleProperty promedioProperty(){ return promedio; }
        public IntegerProperty semestreProperty(){ return semestre; }
        public BooleanProperty activoProperty(){ return activo; }

        public double getCodigo(){ return codigo.get(); }
        public String getNombres(){ return nombres.get(); }
        public String getApellidos(){ return apellidos.get(); }
        public String getEmail(){ return email.get(); }
        public double getPromedio(){ return promedio.get(); }
        public int getSemestre(){ return semestre.get(); }
        public boolean isActivo(){ return activo.get(); }
    }
}
