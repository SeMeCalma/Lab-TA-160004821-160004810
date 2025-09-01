package edu.universidad.ui;

import edu.universidad.persistence.H2DB;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import edu.universidad.ui.Bus;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.sql.*;

public class CursoController {

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtSemestre;
    @FXML private CheckBox chkActivo;
    @FXML private TextField txtProfesorId;

    @FXML private TableView<Row> tabla;
    @FXML private TableColumn<Row, Number> colId;
    @FXML private TableColumn<Row, String> colNombre;
    @FXML private TableColumn<Row, Number> colSemestre;
    @FXML private TableColumn<Row, Boolean> colActivo;
    @FXML private TableColumn<Row, Number> colProfesor;
    @FXML private TableColumn<Row, Number> colInscritos;

    @FXML private Label lblEstado;

    private final ObservableList<Row> datos = FXCollections.observableArrayList();

    @FXML
    private void initialize(){
        colId.setCellValueFactory(c -> c.getValue().idProperty());
        colNombre.setCellValueFactory(c -> c.getValue().nombreProperty());
        colSemestre.setCellValueFactory(c -> c.getValue().semestreProperty());
        colActivo.setCellValueFactory(c -> c.getValue().activoProperty());
        colProfesor.setCellValueFactory(c -> new ReadOnlyObjectWrapper<Number>(c.getValue().getProfesorId()));
        colInscritos.setCellValueFactory(c -> new ReadOnlyObjectWrapper<Number>(c.getValue().getInscritos()));

        tabla.setItems(datos);

        tabla.getSelectionModel().selectedItemProperty().addListener((o,old,sel)->{
            if (sel!=null){
                txtId.setText(Integer.toString(sel.getId()));
                txtNombre.setText(sel.getNombre());
                txtSemestre.setText(Integer.toString(sel.getSemestre()));
                chkActivo.setSelected(sel.isActivo());
                txtProfesorId.setText(sel.getProfesorId()==null? "" : Double.toString(sel.getProfesorId()));
            }
        });

        recargar();
    }

    @FXML private void onCrearActualizar(ActionEvent e){
        Row r = leer();
        if (r == null) return;
        if (r.getProfesorId()!=null && !existeProfesor(r.getProfesorId())){
            error("No existe un profesor con ID " + r.getProfesorId() + ". Crea el profesor primero o deja vacío el campo.");
            return;
        }

        String sql = "MERGE INTO CURSO (ID,NOMBRE,ACTIVO,SEMESTRE,PROFESOR_ID) KEY(ID) VALUES (?,?,?,?,?)";
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, r.getId());
            ps.setString(2, r.getNombre());
            ps.setBoolean(3, r.isActivo());
            ps.setInt(4, r.getSemestre());
            if (r.getProfesorId()==null) ps.setNull(5, Types.DOUBLE); else ps.setDouble(5, r.getProfesorId());
            ps.executeUpdate();
            info("Curso guardado.");
            recargar();
            Bus.bumpCursos();
            onLimpiar(null);
        } catch (SQLException ex){
            error("Error guardando curso: " + ex.getMessage());
        }
    }

    @FXML private void onEliminar(ActionEvent e){
        Row sel = tabla.getSelectionModel().getSelectedItem();
        if (sel==null){ error("Selecciona un curso para eliminar."); return; }
        String sql = "DELETE FROM CURSO WHERE ID=?";
        try (Connection con = H2DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, sel.getId());
            int n = ps.executeUpdate();
            if (n==0) info("No se eliminó ningún registro.");
            else info("Curso eliminado.");
            recargar(); onLimpiar(null);
            Bus.bumpCursos();
        } catch (SQLException ex){
            error("Error eliminando curso: " + ex.getMessage());
        }
    }

    @FXML private void onAsignarProfesor(ActionEvent e){
        Row sel = tabla.getSelectionModel().getSelectedItem();
        if (sel==null){ error("Selecciona un curso de la tabla."); return; }
        String s = txtProfesorId.getText().trim();
        Double prof = null;
        if (!s.isBlank()) {
            try { prof = Double.parseDouble(s); }
            catch(Exception ex){ error("El campo 'Profesor ID' debe ser numérico o vacío para quitar la asignación."); return; }
        }
        String sql = "UPDATE CURSO SET PROFESOR_ID=? WHERE ID=?";
        try (Connection con = H2DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)){
            if (prof==null) ps.setNull(1, Types.DOUBLE); else ps.setDouble(1, prof);
            ps.setInt(2, sel.getId());
            ps.executeUpdate();
            info(prof==null? "Profesor desasignado." : ("Profesor "+prof+" asignado."));
            recargar();
        } catch (SQLException ex){
            error("Error asignando profesor: " + ex.getMessage());
        }
    }

    @FXML private void onRecargar(ActionEvent e){ recargar(); }

    @FXML private void onLimpiar(ActionEvent e){
        txtId.clear(); txtNombre.clear(); txtSemestre.clear(); chkActivo.setSelected(false); txtProfesorId.clear();
        tabla.getSelectionModel().clearSelection();
    }

    private void recargar(){
        datos.clear();
        String sql = "SELECT c.ID,c.NOMBRE,c.SEMESTRE,c.ACTIVO,c.PROFESOR_ID," +
                     " (SELECT COUNT(*) FROM INSCRIPCION i WHERE i.CURSO_ID=c.ID) AS INSCRITOS " +
                     "FROM CURSO c ORDER BY c.ID";
        try (Connection con = H2DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while (rs.next()){
                int id = rs.getInt(1);
                String nombre = rs.getString(2);
                int sem = rs.getInt(3);
                boolean act = rs.getBoolean(4);
                Double pro = rs.getObject(5)==null ? null : rs.getDouble(5);
                int ins = rs.getInt(6);
                datos.add(new Row(id, nombre, sem, act, pro, ins));
            }
            estado("Datos recargados");
        } catch (SQLException ex){
            error("Error listando cursos: " + ex.getMessage());
        }
    }

    private Row leer(){
        int id;
        try { id = Integer.parseInt(txtId.getText().trim()); }
        catch(Exception ex){ error("El campo 'ID' debe ser entero."); return null; }

        String nombre = txtNombre.getText().trim();
        if (nombre.isBlank()){ error("El campo 'Nombre' es obligatorio."); return null; }

        int semestre;
        try { semestre = Integer.parseInt(txtSemestre.getText().trim()); }
        catch(Exception ex){ error("El campo 'Semestre' debe ser entero (1-12)."); return null; }
        if (semestre<1 || semestre>12){ error("El 'Semestre' debe estar entre 1 y 12."); return null; }

        Double profesor = null;
        String s = txtProfesorId.getText().trim();
        if (!s.isBlank()){
            try { profesor = Double.parseDouble(s); }
            catch(Exception ex){ error("El campo 'Profesor ID' debe ser numérico o quedar vacío."); return null; }
        }

        boolean activo = chkActivo!=null && chkActivo.isSelected();
        return new Row(id, nombre, semestre, activo, profesor, 0);
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

    private boolean existeProfesor(double id){
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT 1 FROM PROFESOR WHERE ID=?")){
            ps.setDouble(1, id);
            try (ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        } catch (SQLException ex){
            error("No se pudo verificar el profesor: " + ex.getMessage());
            return false;
        }
    }
public static class Row{
        private final IntegerProperty id = new SimpleIntegerProperty();
        private final StringProperty nombre = new SimpleStringProperty();
        private final IntegerProperty semestre = new SimpleIntegerProperty();
        private final BooleanProperty activo = new SimpleBooleanProperty();
        private final ObjectProperty<Double> profesorId = new SimpleObjectProperty<>();
        private final IntegerProperty inscritos = new SimpleIntegerProperty();

        public Row(int id, String nombre, int semestre, boolean activo, Double profesorId, int inscritos){
            this.id.set(id); this.nombre.set(nombre); this.semestre.set(semestre); this.activo.set(activo);
            this.profesorId.set(profesorId); this.inscritos.set(inscritos);
        }

        public IntegerProperty idProperty(){ return id; }
        public StringProperty nombreProperty(){ return nombre; }
        public IntegerProperty semestreProperty(){ return semestre; }
        public BooleanProperty activoProperty(){ return activo; }
        public ObjectProperty<Double> profesorIdProperty(){ return profesorId; }
        public IntegerProperty inscritosProperty(){ return inscritos; }

        public int getId(){ return id.get(); }
        public String getNombre(){ return nombre.get(); }
        public int getSemestre(){ return semestre.get(); }
        public boolean isActivo(){ return activo.get(); }
        public Double getProfesorId(){ return profesorId.get(); }
        public int getInscritos(){ return inscritos.get(); }
    }
}
