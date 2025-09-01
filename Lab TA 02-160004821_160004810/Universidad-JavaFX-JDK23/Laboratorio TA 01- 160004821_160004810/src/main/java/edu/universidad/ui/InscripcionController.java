package edu.universidad.ui;

import javafx.animation.PauseTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import edu.universidad.persistence.H2DB;

public class InscripcionController {

    @FXML private TextField txtCursoId;
    @FXML private TextField txtCodigoEst;
    @FXML private TextField txtAnio;
    @FXML private TextField txtSemestre;
    @FXML private Label lblCursoActivo;
    @FXML private Label lblEstado;

    @FXML private TableView<Row> tabla;
    @FXML private TableColumn<Row, Number> colCurso;
    @FXML private TableColumn<Row, Number> colCodigo;
    @FXML private TableColumn<Row, Number> colAnio;
    @FXML private TableColumn<Row, Number> colSemestre;

    private final ObservableList<Row> datos = FXCollections.observableArrayList();

    private final Map<Integer, MetaCurso> cursoCache = new HashMap<>();
    private long cacheStamp = 0;
    private final PauseTransition debounce = new PauseTransition(Duration.millis(180));

    @FXML private void initialize(){
        colCurso.setCellValueFactory(c -> c.getValue().cursoIdProperty());
        colCodigo.setCellValueFactory(c -> c.getValue().codigoProperty());
        colAnio.setCellValueFactory(c -> c.getValue().anioProperty());
        colSemestre.setCellValueFactory(c -> c.getValue().semestreProperty());
        tabla.setItems(datos);

        if (txtSemestre != null) txtSemestre.setEditable(false);
        if (lblCursoActivo != null) lblCursoActivo.setText("—");

        tabla.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Row> o, Row oldSel, Row sel) -> {
            if (sel != null) {
                txtCursoId.setText(Integer.toString(sel.getCursoId()));
                txtCodigoEst.setText(Double.toString(sel.getCodigo()));
                txtAnio.setText(Integer.toString(sel.getAnio()));
                txtSemestre.setText(Integer.toString(sel.getSemestre()));
            }
        });

        txtCursoId.textProperty().addListener((o, ov, nv) -> {
            debounce.stop();
            debounce.setOnFinished(ev -> updateCursoInfo());
            debounce.playFromStart();
        });

        try {
            Bus.cursosVersionProperty().addListener((o, ov, nv) -> {                // invalida caché
                cacheStamp++;
                cursoCache.clear();
                updateCursoInfo();
            });
        } catch (Throwable ignored) { /* si no existe el Bus, continuar */ }

        recargar(null);
    }

    @FXML private void onInscribir(ActionEvent e){ crearInscripcion(); }
    @FXML private void onDesinscribir(ActionEvent e){ eliminarInscripcion(); }
    @FXML private void onActualizar(ActionEvent e){ crearInscripcion(); }
    @FXML private void onLimpiar(ActionEvent e){ limpiarFormulario(); }

    private void crearInscripcion(){
        Row r = leer(); if (r == null) return;

        // Validación de semestre consistente
        Integer sc = getSemestreCurso(r.getCursoId());
        if (sc == null) { error("El curso " + r.getCursoId() + " no existe."); return; }
        Integer se = getSemestreEst(r.getCodigo());
        if (se == null) { error("El estudiante " + r.getCodigo() + " no existe."); return; }
        if (!sc.equals(se)) { error("Semestre distinto: curso=" + sc + " vs estudiante=" + se); return; }

        Boolean activo = getActivoCurso(r.getCursoId());
        if (activo == null) { error("El curso " + r.getCursoId() + " no existe."); return; }
        if (!activo) { error("El curso " + r.getCursoId() + " está INACTIVO. No es posible inscribir."); return; }

        String sql = "MERGE INTO INSCRIPCION (CURSO_ID,ESTUDIANTE_CODIGO,ANIO,SEMESTRE) " +
                     "KEY(CURSO_ID,ESTUDIANTE_CODIGO) VALUES (?,?,?,?)";
        try (Connection con = H2DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, r.getCursoId());
            ps.setDouble(2, r.getCodigo());
            ps.setInt(3, r.getAnio());
            ps.setInt(4, r.getSemestre());
            ps.executeUpdate();
            estado("Inscripción guardada");
            recargar(null);
        } catch (SQLException ex){
            error("Error guardando inscripción: " + ex.getMessage());
        }
    }

    private void eliminarInscripcion(){
        Row r = leer(); if (r == null) return;
        String sql = "DELETE FROM INSCRIPCION WHERE CURSO_ID=? AND ESTUDIANTE_CODIGO=?";
        try (Connection con = H2DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, r.getCursoId());
            ps.setDouble(2, r.getCodigo());
            int n = ps.executeUpdate();
            estado(n > 0 ? "Inscripción eliminada" : "No había inscripción");
            recargar(null);
        } catch (SQLException ex){
            error("Error desinscribiendo: " + ex.getMessage());
        }
    }

    private void limpiarFormulario(){
        txtCursoId.clear(); txtCodigoEst.clear(); txtAnio.clear(); txtSemestre.clear();
        tabla.getSelectionModel().clearSelection();
    }

    private void recargar(ActionEvent e){
        datos.clear();
        String sql = "SELECT CURSO_ID,ESTUDIANTE_CODIGO,ANIO,SEMESTRE FROM INSCRIPCION ORDER BY CURSO_ID,ESTUDIANTE_CODIGO";
        try (Connection con = H2DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while (rs.next()){
                datos.add(new Row(rs.getInt(1), rs.getDouble(2), rs.getInt(3), rs.getInt(4)));
            }
            estado("Datos recargados");
        } catch (SQLException ex){
            error("Error listando inscripciones: " + ex.getMessage());
        }
    }

    private Row leer(){
        int curso; double codigo; int anio; int semestre;
        try { curso = Integer.parseInt(txtCursoId.getText().trim()); }
        catch (Exception ex) { error("El campo 'Curso ID' debe ser entero."); return null; }
        try { codigo = Double.parseDouble(txtCodigoEst.getText().trim()); }
        catch (Exception ex) { error("El campo 'Código Est.' debe ser numérico."); return null; }
        try { anio = Integer.parseInt(txtAnio.getText().trim()); }
        catch (Exception ex) { error("El campo 'Año' debe ser entero."); return null; }
        try { semestre = Integer.parseInt(txtSemestre.getText().trim()); }
        catch (Exception ex) { error("El 'Semestre' del curso no está definido."); return null; }
        return new Row(curso, codigo, anio, semestre);
    }

    private Integer getSemestreCurso(int cursoId){
        MetaCurso m = metaDesdeCacheOCargar(cursoId);
        return m == null ? null : m.semestre;
    }

    private Boolean getActivoCurso(int cursoId){
        MetaCurso m = metaDesdeCacheOCargar(cursoId);
        return m == null ? null : m.activo;
    }

    private Integer getSemestreEst(double codigo){
        String sql = "SELECT SEMESTRE FROM ESTUDIANTE WHERE CODIGO=?";
        try (Connection con = H2DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)){
            ps.setDouble(1, codigo);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) return rs.getInt(1);
                return null;
            }
        } catch (SQLException ex){
            error("Error consultando estudiante: " + ex.getMessage());
            return null;
        }
    }

    private void updateCursoInfo(){
        String s = txtCursoId.getText() == null ? "" : txtCursoId.getText().trim();
        if (s.isEmpty()) {
            if (lblCursoActivo != null) lblCursoActivo.setText("—");
            if (txtSemestre != null) txtSemestre.setText("");
            return;
        }
        try {
            int id = Integer.parseInt(s);
            MetaCurso meta = metaDesdeCacheOCargar(id);
            if (meta == null) {
                if (lblCursoActivo != null) lblCursoActivo.setText("No existe");
                if (txtSemestre != null) txtSemestre.setText("");
                return;
            }
            if (txtSemestre != null) txtSemestre.setText(Integer.toString(meta.semestre));
            if (lblCursoActivo != null) lblCursoActivo.setText(meta.activo ? "ACTIVO" : "INACTIVO");
        } catch (NumberFormatException nfe){
            if (lblCursoActivo != null) lblCursoActivo.setText("—");
            if (txtSemestre != null) txtSemestre.setText("");
        } catch (Exception ex){
            if (lblCursoActivo != null) lblCursoActivo.setText("Error");
        }
    }

    private static class MetaCurso {
        final boolean activo;
        final int semestre;
        MetaCurso(boolean a, int s){ this.activo = a; this.semestre = s; }
    }

    private MetaCurso metaDesdeCacheOCargar(int id){
        MetaCurso m = cursoCache.get(id);
        if (m != null) return m;
        String sql = "SELECT ACTIVO, SEMESTRE FROM CURSO WHERE ID=?";
        try (Connection con = H2DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()){
                if (!rs.next()) return null;
                MetaCurso nuevo = new MetaCurso(rs.getBoolean(1), rs.getInt(2));
                cursoCache.put(id, nuevo);
                return nuevo;
            }
        } catch (SQLException ex){
            error("Error consultando curso: " + ex.getMessage());
            return null;
        }
    }

    private void info(String m){
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Información"); a.setHeaderText(null); a.setContentText(m);
        a.getDialogPane().setGraphic(null); a.showAndWait();
    }
    private void error(String m){
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error"); a.setHeaderText(null); a.setContentText(m);
        a.getDialogPane().setGraphic(null); a.showAndWait();
    }
    private void estado(String m){ if (lblEstado != null) lblEstado.setText(m); }

    public static class Row {
        private final IntegerProperty cursoId = new SimpleIntegerProperty();
        private final DoubleProperty codigo = new SimpleDoubleProperty();
        private final IntegerProperty anio = new SimpleIntegerProperty();
        private final IntegerProperty semestre = new SimpleIntegerProperty();
        public Row(int cursoId, double codigo, int anio, int semestre){
            this.cursoId.set(cursoId); this.codigo.set(codigo); this.anio.set(anio); this.semestre.set(semestre);
        }
        public IntegerProperty cursoIdProperty(){ return cursoId; }
        public DoubleProperty codigoProperty(){ return codigo; }
        public IntegerProperty anioProperty(){ return anio; }
        public IntegerProperty semestreProperty(){ return semestre; }
        public int getCursoId(){ return cursoId.get(); }
        public double getCodigo(){ return codigo.get(); }
        public int getAnio(){ return anio.get(); }
        public int getSemestre(){ return semestre.get(); }
    }
}
