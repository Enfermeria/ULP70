
package vistas;

import accesoadatos.AlumnoData;
import accesoadatos.AlumnoData.Ordenacion;
import accesoadatos.InscripcionData;
import accesoadatos.MateriaData;
import accesoadatos.Utils;
import entidades.Alumno;
import entidades.Inscripcion;
import entidades.Materia;
import java.awt.Color;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class GestionInscripcionesMateria extends javax.swing.JInternalFrame {
	DefaultTableModel modeloTablaMaterias, modeloTablaAlumnosInscriptos, modeloTablaAlumnosDisponibles;
	public static List<Materia> listaMaterias;
    public static List<Inscripcion> listaInscripciones; //lista de inscripciones de un alumno
    public static List<Alumno> listaAlumnosDisponibles;//lista de materias en las que NO está inscripto un alumno
	private final AlumnoData alumnoData;	
    private final MateriaData materiaData;
    private final InscripcionData inscripcionData;
	// private enum TipoEdicion {AGREGAR, MODIFICAR, BUSCAR};
	private MateriaData.Ordenacion ordenacion = MateriaData.Ordenacion.PORIDMATERIA; // defino el tipo de orden por defecto 
	private FiltroMaterias filtro = new FiltroMaterias();  //el filtro de búsqueda
	

	/**
	 * Creates new form GestionInscripciones
	 */
	public GestionInscripcionesMateria() {
		initComponents();
		alumnoData = new AlumnoData();
		materiaData = new MateriaData();
        inscripcionData = new InscripcionData();
        modeloTablaMaterias = (DefaultTableModel) tablaMaterias.getModel();
        modeloTablaAlumnosInscriptos = (DefaultTableModel) tablaAlumnosInscriptos.getModel();
        modeloTablaAlumnosDisponibles = (DefaultTableModel) tablaAlumnosDisponibles.getModel();
                
		cargarListaMaterias(); //carga la base de datos
		cargarTablaMaterias(); // cargo la tabla con los alumnos
	} // constructor

	/** carga la lista de alumnos de la BD */
	private void cargarListaMaterias(){ 
		if (filtro.estoyFiltrando) 
			listaMaterias = materiaData.getListaMateriasXCriterioDeBusqueda(filtro.id, filtro.anio, filtro.nombre, ordenacion);
		else
			listaMaterias = materiaData.getListaMaterias(ordenacion);
	}//cargarListaAlumnos
	
	
	/** carga materias de la lista a la tabla */
	private void cargarTablaMaterias(){ 
		//borro las filas de la tabla
		for (int fila = modeloTablaMaterias.getRowCount() -  1; fila >= 0; fila--)
			modeloTablaMaterias.removeRow(fila);
		
		//cargo los alumnos de listaMaterias a la tabla
		for (Materia materia : listaMaterias) {
			modeloTablaMaterias.addRow(new Object[] {
				materia.getIdmateria(),
				materia.getAnio(),
				materia.getNombre(),
				materia.getEstado() } 
			);
		}
		
		//como no hay fila seleccionada, deshabilito el botón Eliminar y Modificar
		if (tablaMaterias.getSelectedRow() == -1) {// si no hay alguna fila seleccionada
			btnInscribirse.setEnabled(false); // deshabilito el botón de Inscribir
			btnDesinscribirse.setEnabled(false); // deshabilito el botón de Desinscribir
			borrarTablaAlumnosInscriptosYDisponibles();   
		}else {//hay una fila seleccionada, cargamos y mostramos las tablas de materias inscriptas y disponibles.
			cargarListaAlumnos(filaTablaMaterias2IdMateria(tablaMaterias.getSelectedRow()));
			cargarTablaAlumnosInscriptosYDisponibles();
        }
	} //cargarTablaAlumnos
	
	
	
	
	/**Carga listaMateriasInscriptas a la tablaMateriasInscriptas y 
 listaAlumnosDisponibles a la tablaMateriasDisponibles
	 */
	private void cargarTablaAlumnosInscriptosYDisponibles(){
		//borro las filas de la tablaAlumnosInscriptas
		for (int fila = modeloTablaAlumnosInscriptos.getRowCount() -  1; fila >= 0; fila--)
			modeloTablaAlumnosInscriptos.removeRow(fila);
		
		//borro las filas de la tablaAlumnosDisponibles
		for (int fila = modeloTablaAlumnosDisponibles.getRowCount() -  1; fila >= 0; fila--)
			modeloTablaAlumnosDisponibles.removeRow(fila);
		
		//cargo los alumnos  de listaAlumnosInscriptas a la tablaAlumnosInscriptas
		for (Inscripcion inscripcion : listaInscripciones) {
			modeloTablaAlumnosInscriptos.addRow(new Object[] {
				inscripcion.getIdinscripcion(),
				inscripcion.getAlumno().getIdalumno(),
				inscripcion.getAlumno().getDni(),
				inscripcion.getAlumno().getApellido(),
				inscripcion.getAlumno().getNombre(),
				inscripcion.getNota() } 
			);
		}
	
		//cargo los alumnos  de listaAlumnosDisponibles a la tablaAlumnosDisponibles
		for (Alumno alumno : listaAlumnosDisponibles) {
			modeloTablaAlumnosDisponibles.addRow(new Object[] {
				alumno.getIdalumno(),
				alumno.getDni(),
				alumno.getApellido(),
				alumno.getNombre(),
				alumno.getEstado() } 
			);
		}

		//como no hay fila seleccionada en la tablaMateriasInscriptas, deshabilito el botón Desinscribirse
		if (tablaAlumnosInscriptos.getSelectedRow() == -1) // si no hay alguna fila seleccionada
			btnDesinscribirse.setEnabled(false); // deshabilito el botón de Desinscribir
		else //hay una fila seleccionada
			btnDesinscribirse.setEnabled(true); // deshabilito el botón de Desinscribir
        
		//como no hay fila seleccionada tablaMateriasDisponibles, deshabilito el botón Inscribirse
		if (tablaAlumnosDisponibles.getSelectedRow() == -1) // si no hay alguna fila seleccionada
			btnInscribirse.setEnabled(false); // deshabilito el botón de Inscribirse
		else //hay una fila seleccionada
			btnInscribirse.setEnabled(true); // deshabilito el botón de Inscribirse
	}//cargarTablaAlumnosInscriptosYDisponibles
	
	
	
	
	/**
	 * En base al idAlumno que nos pasan, cargamos la lista de materias 
	 * inscriptas y disponibles de ese alumno.
	 * @param idAlumno 
	 */
	private void cargarListaAlumnos(int idMateria){
		listaInscripciones = inscripcionData.getListaInscripcionesDelAlumno(idMateria);
		listaAlumnosDisponibles = inscripcionData.getListaAlumnosDisponiblesXMateria(idMateria);
	}// cargarListaMaterias

        
		
		
	/**
	 * como no hay ningun alumno seleccionado, borra los datos de las tablas de materias.
	 */
	private void borrarTablaAlumnosInscriptosYDisponibles(){
		//borro las filas de la tabla de materias inscriptas
		for (int fila = modeloTablaAlumnosInscriptos.getRowCount() -  1; fila >= 0; fila--)
			modeloTablaAlumnosInscriptos.removeRow(fila);
		//borro las filas de la tabla de materias disponibles
		for (int fila = modeloTablaAlumnosDisponibles.getRowCount() -  1; fila >= 0; fila--)
			modeloTablaAlumnosDisponibles.removeRow(fila);
	}// borrarTablaMaterias

		
	
		
	
	/**
	 * Busca la materia por id, por anio,  o por nombre (o por 
	 * combinación de dichos campos). 
	 * El criterio para usar un campo en la búsqueda es que no esté en blanco. 
	 * Es decir, si tiene datos, se buscará por ese dato. Por ejemplo, si puso 
	 * el id, buscará por id. Si puso el anio, buscará por anio. 
	 * Si puso el anio y Nombre, buscara por anio and nombre.
	 * 
	 * @return devuelve true sio pudo usar algún criterio de búsqueda
	 */
	private boolean buscarMateria(){ 
		// cargo los campos de texto id, anio y nombre para buscar por esos criterior
		int idAlumno, anio;
		String nombre;
		
		//idAlumno
		try {
			if (txtId.getText().isEmpty()) // si está vacío no se usa para buscar
				idAlumno = -1;
			else
				idAlumno = Integer.valueOf(txtId.getText()); //no vacío, participa del criterio de búsqueda
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "El Id debe ser un número válido", "Id no válido", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		//dni
		try {
			if (txtAnio.getText().isEmpty()) // si está vacío no se usa para buscar
				anio = -1;
			else
				anio = Integer.valueOf(txtAnio.getText()); // no vacío, participa del criterio de búsqueda
				
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "El Año debe ser un número válido", "Año no válido", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		//apellido y nombre
		nombre = txtNombre.getText();
		
		//testeo que hay al menos un criterio de búsqueda
		if ( idAlumno==-1 && anio==-1 && nombre.isEmpty()  )   {
			JOptionPane.showMessageDialog(this, "Debe ingresar algún criterio para buscar", "Ningun criterio de búsqueda", JOptionPane.ERROR_MESSAGE);
			return false;
		} else { //todo Ok. Buscar por alguno de los criterior de búsqueda
			filtro.id = idAlumno;
			filtro.anio = anio;
			filtro.nombre = nombre;
			filtro.estoyFiltrando = true;
			cargarListaMaterias();
			cargarTablaMaterias();
			setearFiltro();
			return true; // pudo buscar
		}
	} //buscarAlumno
	

	
		

	
	
	/** pongo los campos txtfield en blanco y deselecciono la fila de tabla */
	private void limpiarCampos(){
		//pongo los campos en blanco
		txtId.setText("");
		txtAnio.setText("");
		txtNombre.setText("");
	} // limpiarCampos




	/**
	 * cargo los datos de la fila indicada de la tabla a los campos de texto de la pantalla 
	 * @param numfila el número de fila a cargar a los campos
	 */
	private void filaTabla2Campos(int numfila){
		txtId.setText(tablaMaterias.getValueAt(numfila, 0)+"");
		txtAnio.setText(tablaMaterias.getValueAt(numfila, 1)+"");
		txtNombre.setText((String)tablaMaterias.getValueAt(numfila, 3));		
	} //filaTabla2Campos
        
        
    /**
	 * Devuelve el idMateria de la fila seleccionada de la tabla de materias
	 * @param numfila el número de fila a cargar a los campos
	 */
	private int filaTablaMaterias2IdMateria(int numfila){
		return (Integer)tablaMaterias.getValueAt(numfila, 0);			
	} //filaTabla2IdAlumno


    /**
	 * Devuelve el idAlumno de la fila seleccionada de la tabla de disponibles
	 * @param numfila el número de fila a cargar a los campos
	 */
	private int filaTablaAlumnosDisponibles2IdAlumnos(int numfila){
		return (Integer)tablaAlumnosDisponibles.getValueAt(numfila, 0);			
	} //filaTablaAlumnosDisponibles2IdAlumno
	
	
	
	/**
	 * Devuelve el idAlumno de la fila seleccionada de la tabla de inscriptos
	 * @param numfila el número de fila a cargar a los campos
	 */
	private int filaTablaAlumnosInscriptos2IdAlumno(int numfila){
		return (Integer)tablaAlumnosInscriptos.getValueAt(numfila, 1);			
	} //filaTablaAlummnosInscriptos2IdAlumno
	
	private int filaTablaAlumnosInscriptos2IdInscripcion(int numfila){
		return (Integer)tablaAlumnosInscriptos.getValueAt(numfila, 0);			
	} //filaTablaAlumnosInscriptos2IdInscripcion
	
	
	private double filaTablaAlumnosInscriptos2Nota(int numfila){
		return (Double)tablaAlumnosInscriptos.getValueAt(numfila, 5);			
	} //filaTablaAlumnosInscriptos2Nota
	
	

	/** 
	 * cambia titulo y color de panel de tabla de alumnos para reflejar que 
	 * está filtrada. Habilita btnResetearFiltro
	*/
	private void setearFiltro(){
			//cambio el titulo de la tabla y color panel de tabla de Alumnos para que muestre que está filtrado
			lblTituloTablaAlumnos.setText("Listado de materias filtradas por búsqueda");
			panelTablaAlumnos.setBackground(new Color(255, 51, 51));
			btnResetearFiltro.setEnabled(true);
			filtro.estoyFiltrando = true;
	} //setearFiltro
	
	
	/** 
	 * Restaur titulo y color de panel de tablaAlumnos para reflejar que 
	 * ya no está filtrada. Deshabilita btnResetearFiltro
	*/
	private void resetearFiltro(){
			//cambio el titulo de la tabla y color panel de tabla para que muestre que no está filtrado
			//cambio el titulo de la tabla y color panel de tabla para que muestre que está filtrado
			lblTituloTablaAlumnos.setText("Listado de materias");
			panelTablaAlumnos.setBackground(new Color(153, 153, 255));
			btnResetearFiltro.setEnabled(false);
			filtro.estoyFiltrando = false;
	} //setearFiltro
	
	
	
	
	
	
/*=====================================================================================================================*/	


	
	
	
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        botonera = new javax.swing.JPanel();
        btnBuscar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        cboxOrden = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        panelTablaAlumnos = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaMaterias = new javax.swing.JTable();
        lblTituloTablaAlumnos = new javax.swing.JLabel();
        btnResetearFiltro = new javax.swing.JButton();
        panelTablaMateriasInscriptas = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaAlumnosInscriptos = new javax.swing.JTable();
        lblTituloTabla1 = new javax.swing.JLabel();
        panelTablaMateriasDisponibles = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaAlumnosDisponibles = new javax.swing.JTable();
        lblTituloTabla2 = new javax.swing.JLabel();
        btnInscribirse = new javax.swing.JButton();
        btnDesinscribirse = new javax.swing.JButton();
        campos = new javax.swing.JPanel();
        txtId = new javax.swing.JTextField();
        txtAnio = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        botonera.setBackground(new java.awt.Color(153, 153, 255));
        botonera.setPreferredSize(new java.awt.Dimension(625, 70));

        btnBuscar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/lupa32x32.png"))); // NOI18N
        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        btnSalir.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/salir2_32x32.png"))); // NOI18N
        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        cboxOrden.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "por Id", "por Año", "por Nombre" }));
        cboxOrden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboxOrdenActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Ordenado");

        javax.swing.GroupLayout botoneraLayout = new javax.swing.GroupLayout(botonera);
        botonera.setLayout(botoneraLayout);
        botoneraLayout.setHorizontalGroup(
            botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(botoneraLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnBuscar)
                .addGap(62, 62, 62)
                .addGroup(botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(cboxOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 146, Short.MAX_VALUE)
                .addComponent(btnSalir)
                .addGap(19, 19, 19))
        );
        botoneraLayout.setVerticalGroup(
            botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(botoneraLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSalir)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, botoneraLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBuscar)
                    .addGroup(botoneraLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(3, 3, 3)
                        .addComponent(cboxOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
        );

        getContentPane().add(botonera, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 404, 520, 68));

        panelTablaAlumnos.setBackground(new java.awt.Color(153, 153, 255));

        tablaMaterias.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Año", "Nombre", "Activo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaMaterias.getTableHeader().setReorderingAllowed(false);
        tablaMaterias.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaMateriasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaMaterias);
        if (tablaMaterias.getColumnModel().getColumnCount() > 0) {
            tablaMaterias.getColumnModel().getColumn(0).setPreferredWidth(10);
            tablaMaterias.getColumnModel().getColumn(1).setPreferredWidth(10);
            tablaMaterias.getColumnModel().getColumn(2).setPreferredWidth(150);
            tablaMaterias.getColumnModel().getColumn(3).setPreferredWidth(10);
        }

        lblTituloTablaAlumnos.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTituloTablaAlumnos.setText("Listado de Materias");
        lblTituloTablaAlumnos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        btnResetearFiltro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnResetearFiltro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/restart16x16.png"))); // NOI18N
        btnResetearFiltro.setText("Resetear filtro");
        btnResetearFiltro.setEnabled(false);
        btnResetearFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetearFiltroActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelTablaAlumnosLayout = new javax.swing.GroupLayout(panelTablaAlumnos);
        panelTablaAlumnos.setLayout(panelTablaAlumnosLayout);
        panelTablaAlumnosLayout.setHorizontalGroup(
            panelTablaAlumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaAlumnosLayout.createSequentialGroup()
                .addGroup(panelTablaAlumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTablaAlumnosLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(lblTituloTablaAlumnos, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnResetearFiltro))
                    .addGroup(panelTablaAlumnosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );
        panelTablaAlumnosLayout.setVerticalGroup(
            panelTablaAlumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTablaAlumnosLayout.createSequentialGroup()
                .addGroup(panelTablaAlumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnResetearFiltro)
                    .addComponent(lblTituloTablaAlumnos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(panelTablaAlumnos, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 520, 328));

        panelTablaMateriasInscriptas.setBackground(new java.awt.Color(153, 153, 255));

        tablaAlumnosInscriptos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id inscrip", "Id alumno", "DNI", "Apellido", "Nombre", "Nota"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaAlumnosInscriptos.getTableHeader().setReorderingAllowed(false);
        tablaAlumnosInscriptos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaAlumnosInscriptosMouseClicked(evt);
            }
        });
        tablaAlumnosInscriptos.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tablaAlumnosInscriptosPropertyChange(evt);
            }
        });
        jScrollPane2.setViewportView(tablaAlumnosInscriptos);
        if (tablaAlumnosInscriptos.getColumnModel().getColumnCount() > 0) {
            tablaAlumnosInscriptos.getColumnModel().getColumn(0).setPreferredWidth(10);
            tablaAlumnosInscriptos.getColumnModel().getColumn(1).setPreferredWidth(10);
            tablaAlumnosInscriptos.getColumnModel().getColumn(2).setPreferredWidth(10);
            tablaAlumnosInscriptos.getColumnModel().getColumn(3).setPreferredWidth(100);
            tablaAlumnosInscriptos.getColumnModel().getColumn(4).setPreferredWidth(100);
            tablaAlumnosInscriptos.getColumnModel().getColumn(5).setPreferredWidth(10);
        }

        lblTituloTabla1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTituloTabla1.setText("Alumnos inscriptos");
        lblTituloTabla1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout panelTablaMateriasInscriptasLayout = new javax.swing.GroupLayout(panelTablaMateriasInscriptas);
        panelTablaMateriasInscriptas.setLayout(panelTablaMateriasInscriptasLayout);
        panelTablaMateriasInscriptasLayout.setHorizontalGroup(
            panelTablaMateriasInscriptasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaMateriasInscriptasLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblTituloTabla1, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                .addGap(155, 155, 155))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTablaMateriasInscriptasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelTablaMateriasInscriptasLayout.setVerticalGroup(
            panelTablaMateriasInscriptasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTablaMateriasInscriptasLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(lblTituloTabla1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(panelTablaMateriasInscriptas, new org.netbeans.lib.awtextra.AbsoluteConstraints(525, 0, 580, -1));

        panelTablaMateriasDisponibles.setBackground(new java.awt.Color(153, 153, 255));

        tablaAlumnosDisponibles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "DNI", "Apellido", "Nombre", "Activo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaAlumnosDisponibles.getTableHeader().setReorderingAllowed(false);
        tablaAlumnosDisponibles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaAlumnosDisponiblesMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tablaAlumnosDisponibles);
        if (tablaAlumnosDisponibles.getColumnModel().getColumnCount() > 0) {
            tablaAlumnosDisponibles.getColumnModel().getColumn(0).setPreferredWidth(10);
            tablaAlumnosDisponibles.getColumnModel().getColumn(1).setPreferredWidth(10);
            tablaAlumnosDisponibles.getColumnModel().getColumn(2).setPreferredWidth(100);
            tablaAlumnosDisponibles.getColumnModel().getColumn(3).setPreferredWidth(100);
            tablaAlumnosDisponibles.getColumnModel().getColumn(4).setPreferredWidth(10);
        }

        lblTituloTabla2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTituloTabla2.setText("Alumnos a inscribir");
        lblTituloTabla2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout panelTablaMateriasDisponiblesLayout = new javax.swing.GroupLayout(panelTablaMateriasDisponibles);
        panelTablaMateriasDisponibles.setLayout(panelTablaMateriasDisponiblesLayout);
        panelTablaMateriasDisponiblesLayout.setHorizontalGroup(
            panelTablaMateriasDisponiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaMateriasDisponiblesLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblTituloTabla2, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                .addGap(155, 155, 155))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTablaMateriasDisponiblesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3)
                .addContainerGap())
        );
        panelTablaMateriasDisponiblesLayout.setVerticalGroup(
            panelTablaMateriasDisponiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTablaMateriasDisponiblesLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(lblTituloTabla2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(panelTablaMateriasDisponibles, new org.netbeans.lib.awtextra.AbsoluteConstraints(525, 225, 580, -1));

        btnInscribirse.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnInscribirse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flecha_arriba16x16.png"))); // NOI18N
        btnInscribirse.setText("Inscribirse");
        btnInscribirse.setEnabled(false);
        btnInscribirse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInscribirseActionPerformed(evt);
            }
        });
        getContentPane().add(btnInscribirse, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 180, -1, -1));

        btnDesinscribirse.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnDesinscribirse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flecha_abajo16x16.png"))); // NOI18N
        btnDesinscribirse.setText("Desinscribirse");
        btnDesinscribirse.setEnabled(false);
        btnDesinscribirse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesinscribirseActionPerformed(evt);
            }
        });
        getContentPane().add(btnDesinscribirse, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 180, -1, -1));

        campos.setBackground(new java.awt.Color(153, 153, 255));

        txtId.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtId.setBorder(javax.swing.BorderFactory.createTitledBorder("Id"));

        txtAnio.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAnio.setBorder(javax.swing.BorderFactory.createTitledBorder("Año"));

        txtNombre.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNombre.setBorder(javax.swing.BorderFactory.createTitledBorder("Nombre"));

        javax.swing.GroupLayout camposLayout = new javax.swing.GroupLayout(campos);
        campos.setLayout(camposLayout);
        camposLayout.setHorizontalGroup(
            camposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(camposLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtAnio, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        camposLayout.setVerticalGroup(
            camposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(camposLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(camposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtId)
                    .addComponent(txtAnio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        getContentPane().add(campos, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 334, 520, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

	
	
	
    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        buscarMateria();
    }//GEN-LAST:event_btnBuscarActionPerformed

	
	
	
    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        dispose();//cierra la ventana
    }//GEN-LAST:event_btnSalirActionPerformed

	
	
	
    private void cboxOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboxOrdenActionPerformed
        if (cboxOrden.getSelectedIndex() == 0)
			ordenacion = MateriaData.Ordenacion.PORIDMATERIA;
        else if (cboxOrden.getSelectedIndex() == 1)
			ordenacion = MateriaData.Ordenacion.PORANIO;
        else if (cboxOrden.getSelectedIndex() == 2)
			ordenacion = MateriaData.Ordenacion.PORNOMBRE;
        else // por las dudas que no eligio uno correcto
			ordenacion = MateriaData.Ordenacion.PORIDMATERIA;
		
		cargarListaMaterias();
        cargarTablaMaterias();
    }//GEN-LAST:event_cboxOrdenActionPerformed

	
	
	
    private void tablaMateriasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMateriasMouseClicked
        //tabla.addRowSelectionInterval(filaTabla, filaTabla); //selecciono esa fila de la tabla
        if (tablaMaterias.getSelectedRow() != -1){ // si hay alguna fila seleccionada
			borrarTablaAlumnosInscriptosYDisponibles();
        }
        int numfila = tablaMaterias.getSelectedRow();
        if (numfila != -1) {
            
            //mostramos las tablas de alumnos de acuerdo la materia seleccionado
            int idMateria = filaTablaMaterias2IdMateria(numfila); // saco el idMateria de la fila seleccionadad de la tablaMaterias
			cargarListaAlumnos(idMateria);  // cargamos las listas de alumnos inscriptas y disponibles de esa materia
			cargarTablaAlumnosInscriptosYDisponibles();			// las mostramos en las respectivas tablas
        }
    }//GEN-LAST:event_tablaMateriasMouseClicked

	
	
	
    private void btnResetearFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetearFiltroActionPerformed
        resetearFiltro();
        cargarListaMaterias();
        cargarTablaMaterias();
        limpiarCampos();
    }//GEN-LAST:event_btnResetearFiltroActionPerformed

	
	
	
	
    private void tablaAlumnosInscriptosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaAlumnosInscriptosMouseClicked
        if (tablaAlumnosInscriptos.getSelectedRow() != -1){ // si hay alguna fila seleccionada
			btnDesinscribirse.setEnabled(false); // deshabilito botón Desinscribirse.
        }
        int numfila = tablaAlumnosInscriptos.getSelectedRow();
        if (numfila != -1) { //si hay alguna fila seleccionada en la tabla de materias disponibles
			btnDesinscribirse.setEnabled(true);
        } 
    }//GEN-LAST:event_tablaAlumnosInscriptosMouseClicked

	
	
	
    private void btnDesinscribirseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesinscribirseActionPerformed
		if (tablaAlumnosInscriptos.getSelectedRow() != -1){ // si hay alguna fila seleccionada
			btnDesinscribirse.setEnabled(false); // deshabilito botón Desinscribirse.
        }
        int numfilaInsc = tablaAlumnosInscriptos.getSelectedRow();
        if (numfilaInsc != -1) { //si hay alguna fila seleccionada en la tabla de materias disponibles
			int idInscripcion = filaTablaAlumnosInscriptos2IdInscripcion(numfilaInsc);//averiguamos el idMateria
			
		    int numfilaMateria = tablaMaterias.getSelectedRow();
			if (numfilaMateria != -1) {
				int idMateria = filaTablaMaterias2IdMateria(numfilaMateria);
				inscripcionData.bajaInscripcion(idInscripcion); // Lo inscribimos
				
				//actualizamos las listas y tablas de materias
				cargarListaAlumnos(idMateria);
				cargarTablaAlumnosInscriptosYDisponibles();
			}
		}
    }//GEN-LAST:event_btnDesinscribirseActionPerformed

	
	
	
	
    private void tablaAlumnosDisponiblesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaAlumnosDisponiblesMouseClicked
        //tabla.addRowSelectionInterval(filaTabla, filaTabla); //selecciono esa fila de la tabla
        if (tablaAlumnosDisponibles.getSelectedRow() != -1){ // si hay alguna fila seleccionada
			btnInscribirse.setEnabled(false); // deshabilito botón Inscribirse.
        }
        int numfila = tablaAlumnosDisponibles.getSelectedRow();
        if (numfila != -1) { //si hay alguna fila seleccionada en la tabla de materias disponibles
			btnInscribirse.setEnabled(true);
        } 	
    }//GEN-LAST:event_tablaAlumnosDisponiblesMouseClicked

	
	
	
	
	
    private void btnInscribirseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInscribirseActionPerformed
        if (tablaAlumnosDisponibles.getSelectedRow() != -1){ // si hay alguna fila seleccionada
			btnInscribirse.setEnabled(false); // deshabilito botón Inscribirse.
        }
        int numfilaDisp = tablaAlumnosDisponibles.getSelectedRow();
        if (numfilaDisp != -1) { //si hay alguna fila seleccionada en la tabla de materias disponibles
			int idMateria = filaTablaAlumnosDisponibles2IdAlumnos(numfilaDisp);//averiguamos el idMateria
			/**Hay un error al cargar la tabla de alumnos disponibles, muestra todos los alumnos,
                        incluso los que están inscriptos a la materia seleccionada. Al querer inscribir un alumno, se lo selecciona de 
                        la tabla inferior, pero lo que carga es el dato duplicado del alumno ya inscripto. Además sigue estando disponible
                        en la tabla inferior.*/
		    int numfilaAlumno = tablaMaterias.getSelectedRow();
			if (numfilaAlumno != -1) {
				int idAlumno = filaTablaMaterias2IdMateria(numfilaAlumno);
				inscripcionData.altaInscripcion(0.0, idAlumno, idMateria); // Lo inscribimos
				                      //  System.out.println("alumno a inscibir" + idAlumno);
//				actualizamos las listas y tablas de materias
				cargarListaAlumnos(idAlumno);
				cargarTablaAlumnosInscriptosYDisponibles();
         	}
		}
    }//GEN-LAST:event_btnInscribirseActionPerformed

    private void tablaAlumnosInscriptosPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tablaAlumnosInscriptosPropertyChange
        //supuestamente cambio la nota
		//System.out.println("CAMBIO LA NOTA!!!!!!!!!!");
		if (tablaAlumnosInscriptos.getEditingRow() == -1) { //si no hay nada editado devuelve -1
			// System.out.println("ERROR... NO HAY FILA Editandose");
			return;
        }
        int numfilaInsc = tablaAlumnosInscriptos.getEditingRow();
        if (numfilaInsc != -1) { //si hay alguna fila editandose en la tabla de materias disponibles
			int idInscripcion = filaTablaAlumnosInscriptos2IdInscripcion(numfilaInsc);//averiguamos el idinscripcion
			Inscripcion inscripcion = inscripcionData.getInscripcion(idInscripcion); // obtengo la inscripcion
			double nota = filaTablaAlumnosInscriptos2IdInscripcion(numfilaInsc);
			if (nota < 0.0 || nota > 10.0) // si la nota no está bien, mensaje de error y vuelve a la nota anterior
				JOptionPane.showMessageDialog(null, "Ingrese una nota válida (de 0 a 10)");
			else { //actualizo la nota
				inscripcion.setNota(nota); 
				inscripcionData.modificarInscripcion(inscripcion);
			}
			//actualizamos las listas y tablas de materias
			cargarListaAlumnos(inscripcion.getAlumno().getIdalumno());
			cargarTablaAlumnosInscriptosYDisponibles();
		}
    }//GEN-LAST:event_tablaAlumnosInscriptosPropertyChange

	
	
	
	
	
    
   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel botonera;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnDesinscribirse;
    private javax.swing.JButton btnInscribirse;
    private javax.swing.JButton btnResetearFiltro;
    private javax.swing.JButton btnSalir;
    private javax.swing.JPanel campos;
    private javax.swing.JComboBox<String> cboxOrden;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblTituloTabla1;
    private javax.swing.JLabel lblTituloTabla2;
    private javax.swing.JLabel lblTituloTablaAlumnos;
    private javax.swing.JPanel panelTablaAlumnos;
    private javax.swing.JPanel panelTablaMateriasDisponibles;
    private javax.swing.JPanel panelTablaMateriasInscriptas;
    private javax.swing.JTable tablaAlumnosDisponibles;
    private javax.swing.JTable tablaAlumnosInscriptos;
    private javax.swing.JTable tablaMaterias;
    private javax.swing.JTextField txtAnio;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}

