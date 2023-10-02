/*
	Trabajo práctico trasversal de la Guía 5 del curso Desarrollo de Apps
	Universidad de La Punta en el marco del proyecto Argentina Programa 4.0

	Integrantes:
		John David Molina Velarde
		Leticia Mores
		Enrique Germán Martínez
		Carlos Eduardo Beltrán

	Esta es la ventana del CRUD de Inscrpciones organizadas por alumno.
	Permite hacer click en la lista de alumnos y muestra las materias
	en las que el alumno está inscripto y las materias disponibles para
	inscribirse. Además permite buscar por diversos campos y cambiar el
	orden de visualización.

	Luego, si hace click seleccionando una materia inscripta, permite 
	desinscribirse con el boton desinscribirse.
	Similarmente, si hace click seleccionando una materia disponible, permite
	inscribirse con el botón inscribirse.
 */
package vistas;

import accesoadatos.AlumnoData;
import accesoadatos.AlumnoData.OrdenacionAlumno;
import accesoadatos.InscripcionData;
import accesoadatos.MateriaData;
import accesoadatos.Utils;
import entidades.Alumno;
import entidades.Inscripcion;
import entidades.Materia;
import java.awt.Color;
import java.time.LocalDate;
import java.util.List;
import javax.swing.CellEditor;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class CrudInscripcionesXAlumno extends javax.swing.JInternalFrame {
	DefaultTableModel modeloTablaAlumnos, modeloTablaMateriasInscriptas, modeloTablaMateriasDisponibles;
	public static List<Alumno> listaAlumnos;
    public static List<Inscripcion> listaInscripciones; //lista de inscripciones de un alumno
    public static List<Materia> listaMateriasDisponibles;//lista de materias en las que NO está inscripto un alumno
	private final AlumnoData alumnoData;	// para conectarse a la bd
    private final MateriaData materiaData;
    private final InscripcionData inscripcionData;
	private OrdenacionAlumno ordenacion = OrdenacionAlumno.PORIDALUMNO; // defino el tipo de orden por defecto que mostrará 
	private FiltroAlumnos filtro = new FiltroAlumnos();  //el filtro de búsqueda
	

	/**
	 * Constructor. Hace las conexiones con la bd.
	 */
	public CrudInscripcionesXAlumno() {
		initComponents();
		alumnoData = new AlumnoData();
		materiaData = new MateriaData();
        inscripcionData = new InscripcionData();
        modeloTablaAlumnos = (DefaultTableModel) tablaAlumnos.getModel();
        modeloTablaMateriasInscriptas = (DefaultTableModel) tablaMateriasInscriptas.getModel();
        modeloTablaMateriasDisponibles = (DefaultTableModel) tablaMateriasDisponibles.getModel();
                
		cargarListaAlumnos(); //carga la base de datos
		cargarTablaAlumnos(); // cargo la tabla con los alumnos
	} // constructor

	
	
	/** carga la lista de alumnos de la BD */
	private void cargarListaAlumnos(){ 
		if (filtro.estoyFiltrando) 
			listaAlumnos = alumnoData.getListaAlumnosXCriterioDeBusqueda(filtro.id, filtro.dni, filtro.apellido, filtro.nombre, ordenacion);
		else
			listaAlumnos = alumnoData.getListaAlumnos(ordenacion);
	}//cargarListaAlumnos
	
	
	
	/** carga alumnos de la lista a la tabla */
	private void cargarTablaAlumnos(){ 
		//borro las filas de la tabla
		for (int fila = modeloTablaAlumnos.getRowCount() -  1; fila >= 0; fila--)
			modeloTablaAlumnos.removeRow(fila);
		
		//cargo los alumnos de listaAlumnos a la tabla
		for (Alumno alumno : listaAlumnos) {
			modeloTablaAlumnos.addRow(new Object[] {
				alumno.getIdalumno(),
				alumno.getDni(),
				alumno.getApellido(),
				alumno.getNombre(),
				alumno.getFechaNacimiento(),
				alumno.getEstado() } 
			);
		}
		
		//como no hay fila seleccionada, deshabilito el botón Eliminar y Modificar
		if (tablaAlumnos.getSelectedRow() == -1) {// si no hay alguna fila seleccionada
			btnInscribirse.setEnabled(false); // deshabilito el botón de Inscribir
			btnDesinscribirse.setEnabled(false); // deshabilito el botón de Desinscribir
			borrarTablaMaterias();   
		}else {//hay una fila seleccionada, cargamos y mostramos las tablas de materias inscriptas y disponibles.
			cargarListaMaterias(filaTablaAlumnos2IdAlumno(tablaAlumnos.getSelectedRow()));
			cargarTablaMaterias();
        }
	} //cargarTablaAlumnos
	
	
	
	
	/**
	 * Carga listaMateriasInscriptas a la tablaMateriasInscriptas y 
	 * listaMateriasDisponibles a la tablaMateriasDisponibles
	 */
	private void cargarTablaMaterias(){
		// Si estaba editando nota, que deje de hacerlo, cancelamos la edicion.
		CellEditor cellEditor = tablaMateriasInscriptas.getCellEditor();
		if (cellEditor != null) {
			if (cellEditor.getCellEditorValue() != null) {
				cellEditor.stopCellEditing();
			} else {
				cellEditor.cancelCellEditing();
			}
		}
		
		//borro las filas de la tablaMateriasInscriptas
		for (int fila = modeloTablaMateriasInscriptas.getRowCount() -  1; fila >= 0; fila--)
			modeloTablaMateriasInscriptas.removeRow(fila);
		
		//borro las filas de la tablaMateriasDisponibles
		for (int fila = modeloTablaMateriasDisponibles.getRowCount() -  1; fila >= 0; fila--)
			modeloTablaMateriasDisponibles.removeRow(fila);
		
		//cargo las materias  de listaMateriasInscriptas a la tablaMateriasInscriptas
		for (Inscripcion inscripcion : listaInscripciones) {
			modeloTablaMateriasInscriptas.addRow(new Object[] {
				inscripcion.getIdinscripcion(),
				inscripcion.getMateria().getIdmateria(),
				inscripcion.getMateria().getAnio(),
				inscripcion.getMateria().getNombre(),
				inscripcion.getNota() } 
			);
		}
	
		//cargo las materias  de listaMateriasDisponibles a la tablaMateriasDisponibles
		for (Materia materia : listaMateriasDisponibles) {
			modeloTablaMateriasDisponibles.addRow(new Object[] {
				materia.getIdmateria(),
				materia.getAnio(),
				materia.getNombre(),
				materia.getEstado() } 
			);
		}

		//como no hay fila seleccionada en la tablaMateriasInscriptas, deshabilito el botón Desinscribirse
		if (tablaMateriasInscriptas.getSelectedRow() == -1) // si no hay alguna fila seleccionada
			btnDesinscribirse.setEnabled(false); // deshabilito el botón de Desinscribir
		else //hay una fila seleccionada
			btnDesinscribirse.setEnabled(true); // deshabilito el botón de Desinscribir
        
		//como no hay fila seleccionada tablaMateriasDisponibles, deshabilito el botón Inscribirse
		if (tablaMateriasDisponibles.getSelectedRow() == -1) // si no hay alguna fila seleccionada
			btnInscribirse.setEnabled(false); // deshabilito el botón de Inscribirse
		else //hay una fila seleccionada
			btnInscribirse.setEnabled(true); // deshabilito el botón de Inscribirse
	}//cargarTablaMaterias
	
	
	
	
	/**
	 * En base al idAlumno que nos pasan, cargamos la lista de materias 
	 * inscriptas y disponibles de ese alumno.
	 * @param idAlumno 
	 */
	private void cargarListaMaterias(int idAlumno){
		listaInscripciones = inscripcionData.getListaInscripcionesDelAlumno(idAlumno);
		listaMateriasDisponibles = inscripcionData.getListaMateriasDisponiblesXAlumno(idAlumno);
	}// cargarListaMaterias

        
		
		
	/**
	 * como no hay ningun alumno seleccionado, borra los datos de las tablas de materias.
	 */
	private void borrarTablaMaterias(){
		//borro las filas de la tabla de materias inscriptas
		for (int fila = modeloTablaMateriasInscriptas.getRowCount() -  1; fila >= 0; fila--)
	modeloTablaMateriasInscriptas.removeRow(fila);
		//borro las filas de la tabla de materias disponibles
		for (int fila = modeloTablaMateriasDisponibles.getRowCount() -  1; fila >= 0; fila--)
	modeloTablaMateriasDisponibles.removeRow(fila);
	}// borrarTablaMaterias

		
	
		
	
	/**
	 * Busca al alumno por id, por dni, por apellido o por nombre (o por 
	 * combinación de dichos campos). 
	 * El criterio para usar un campo en la búsqueda es que no esté en blanco. 
	 * Es decir, si tiene datos, se buscará por ese dato. Por ejemplo, si puso 
	 * el id, buscará por id. Si puso el dni, buscará por dni. 
	 * Si puso el dni y Apellido, buscara por dni and apellido.
	 * 
	 * @return devuelve true sio pudo usar algún criterio de búsqueda
	 */
	private boolean buscarAlumno(){ 
		// cargo los campos de texto id, dni, apellido y nombre para buscar por esos criterior
		int idAlumno, dni;
		String apellido, nombre;
		
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
			if (txtDni.getText().isEmpty()) // si está vacío no se usa para buscar
				dni = -1;
			else
				dni = Integer.valueOf(txtDni.getText()); // no vacío, participa del criterio de búsqueda
				
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "El DNI debe ser un número válido", "DNI no válido", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		//apellido y nombre
		apellido = txtApellido.getText();
		nombre = txtNombre.getText();
		
		//testeo que hay al menos un criterio de búsqueda
		if ( idAlumno==-1 && dni==-1 && apellido.isEmpty() && nombre.isEmpty()  )   {
			JOptionPane.showMessageDialog(this, "Debe ingresar algún criterio para buscar", "Ningun criterio de búsqueda", JOptionPane.ERROR_MESSAGE);
			return false;
		} else { //todo Ok. Buscar por alguno de los criterior de búsqueda
			filtro.id = idAlumno;
			filtro.dni = dni;
			filtro.apellido = apellido;
			filtro.nombre = nombre;
			filtro.estoyFiltrando = true;
			cargarListaAlumnos();
			cargarTablaAlumnos();
			setearFiltro();
			return true; // pudo buscar
		}
	} //buscarAlumno
	

	
		

	
	
	/** pongo los campos txtfield en blanco y deselecciono la fila de tabla */
	private void limpiarCampos(){
		//pongo los campos en blanco
		txtId.setText("");
		txtDni.setText("");
		txtApellido.setText("");
		txtNombre.setText("");
		// tablaAlumnos.removeRowSelectionInterval(0, tablaAlumnos.getRowCount()-1); //des-selecciono las filas de la tabla
	} // limpiarCampos




	/**
	 * cargo los datos de la fila indicada de la tabla a los campos de texto de la pantalla 
	 * @param numfila el número de fila a cargar a los campos
	 */
	private void filaTabla2Campos(int numfila){
		txtId.setText(tablaAlumnos.getValueAt(numfila, 0)+"");
		txtDni.setText(tablaAlumnos.getValueAt(numfila, 1)+"");
		txtApellido.setText((String)tablaAlumnos.getValueAt(numfila, 2));
		txtNombre.setText((String)tablaAlumnos.getValueAt(numfila, 3));		
	} //filaTabla2Campos
        
        
	
	
    /**
	 * Devuelve el idAlumno de la fila seleccionada de la tabla de alumnos
	 * @param numfila el número de fila a cargar a los campos
	 */
	private int filaTablaAlumnos2IdAlumno(int numfila){
		return (Integer)tablaAlumnos.getValueAt(numfila, 0);			
	} //filaTabla2IdAlumno

	
	
    /**
	 * Devuelve el idMateria de la fila seleccionada de la tabla de disponibles
	 * @param numfila el número de fila a cargar a los campos
	 */
	private int filaTablaDisponibles2IdMateria(int numfila){
		return (Integer)tablaMateriasDisponibles.getValueAt(numfila, 0);			
	} //filaTablaDisponibles2IdMateria
	
	
	
	/**
	 * Devuelve el idMateria de la fila seleccionada de la tabla de inscriptos
	 * @param numfila el número de fila a cargar a los campos
	 */
	private int filaTablaInscriptas2IdMateria(int numfila){
		return (Integer)tablaMateriasInscriptas.getValueAt(numfila, 1);			
	} //filaTablaInscriptas2IdMateria
	
	
	
	/**
	 * Devuelve el idInscripcion de la fila seleccionada de la tabla de inscriptos
	 * @param numfila es el número de fila a cargar
	 * @return 
	 */
	private int filaTablaInscriptas2IdInscripcion(int numfila){
		return (Integer)tablaMateriasInscriptas.getValueAt(numfila, 0);			
	} //filaTablaInscriptas2IdMateria
	
	
	
	/**
	 * Devuelve la nota de la fila seleccionada de la tabla de inscriptos
	 * @param numfila es el numero de fila seleccionado
	 * @return 
	 */
	private double filaTablaInscriptas2Nota(int numfila){
		return (Double)tablaMateriasInscriptas.getValueAt(numfila, 4);			
	} //filaTablaInscriptas2IdMateria
	
	

	/** 
	 * cambia titulo y color de panel de tabla de alumnos para reflejar que 
	 * está filtrada. Habilita btnResetearFiltro
	*/
	private void setearFiltro(){
			//cambio el titulo de la tabla y color panel de tabla de Alumnos para que muestre que está filtrado
			lblTituloTablaAlumnos.setText("Listado de alumnos filtrado por búsqueda");
			panelTablaAlumnos.setBackground(new Color(255, 51, 51));
			btnResetearFiltro.setEnabled(true);
			filtro.estoyFiltrando = true;
	} //setearFiltro
	
	
	
	/** 
	 * Restaura el titulo y color de panel de tablaAlumnos para reflejar que 
	 * ya no está filtrada. Deshabilita btnResetearFiltro
	*/
	private void resetearFiltro(){
			//cambio el titulo de la tabla y color panel de tabla para que muestre que no está filtrado
			//cambio el titulo de la tabla y color panel de tabla para que muestre que está filtrado
			lblTituloTablaAlumnos.setText("Listado de alumnos");
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
        tablaAlumnos = new javax.swing.JTable();
        lblTituloTablaAlumnos = new javax.swing.JLabel();
        btnResetearFiltro = new javax.swing.JButton();
        panelTablaMateriasInscriptas = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaMateriasInscriptas = new javax.swing.JTable();
        lblTituloTabla1 = new javax.swing.JLabel();
        panelTablaMateriasDisponibles = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaMateriasDisponibles = new javax.swing.JTable();
        lblTituloTabla2 = new javax.swing.JLabel();
        btnInscribirse = new javax.swing.JButton();
        btnDesinscribirse = new javax.swing.JButton();
        campos = new javax.swing.JPanel();
        txtId = new javax.swing.JTextField();
        txtDni = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        txtApellido = new javax.swing.JTextField();

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

        cboxOrden.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "por Id", "por DNI", "por Apellido y nombre" }));
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 212, Short.MAX_VALUE)
                .addComponent(btnSalir)
                .addContainerGap())
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

        getContentPane().add(botonera, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 404, 625, 68));

        panelTablaAlumnos.setBackground(new java.awt.Color(153, 153, 255));

        tablaAlumnos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "DNI", "Apellido", "Nombre", "Nacimiento", "Activo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaAlumnos.getTableHeader().setReorderingAllowed(false);
        tablaAlumnos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaAlumnosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaAlumnos);

        lblTituloTablaAlumnos.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTituloTablaAlumnos.setText("Listado de Alumnos");
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
                .addContainerGap()
                .addGroup(panelTablaAlumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
                    .addGroup(panelTablaAlumnosLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(lblTituloTablaAlumnos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnResetearFiltro)))
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

        getContentPane().add(panelTablaAlumnos, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 625, 328));

        panelTablaMateriasInscriptas.setBackground(new java.awt.Color(153, 153, 255));

        tablaMateriasInscriptas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id inscrip", "Id materia", "Año", "Nombre", "Nota"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaMateriasInscriptas.getTableHeader().setReorderingAllowed(false);
        tablaMateriasInscriptas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaMateriasInscriptasMouseClicked(evt);
            }
        });
        tablaMateriasInscriptas.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tablaMateriasInscriptasPropertyChange(evt);
            }
        });
        jScrollPane2.setViewportView(tablaMateriasInscriptas);
        if (tablaMateriasInscriptas.getColumnModel().getColumnCount() > 0) {
            tablaMateriasInscriptas.getColumnModel().getColumn(0).setPreferredWidth(15);
            tablaMateriasInscriptas.getColumnModel().getColumn(1).setPreferredWidth(10);
            tablaMateriasInscriptas.getColumnModel().getColumn(2).setPreferredWidth(10);
            tablaMateriasInscriptas.getColumnModel().getColumn(3).setPreferredWidth(150);
            tablaMateriasInscriptas.getColumnModel().getColumn(4).setPreferredWidth(10);
        }

        lblTituloTabla1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTituloTabla1.setText("Materias en curso");
        lblTituloTabla1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout panelTablaMateriasInscriptasLayout = new javax.swing.GroupLayout(panelTablaMateriasInscriptas);
        panelTablaMateriasInscriptas.setLayout(panelTablaMateriasInscriptasLayout);
        panelTablaMateriasInscriptasLayout.setHorizontalGroup(
            panelTablaMateriasInscriptasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaMateriasInscriptasLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblTituloTabla1, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
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

        getContentPane().add(panelTablaMateriasInscriptas, new org.netbeans.lib.awtextra.AbsoluteConstraints(635, 0, 470, -1));

        panelTablaMateriasDisponibles.setBackground(new java.awt.Color(153, 153, 255));

        tablaMateriasDisponibles.setModel(new javax.swing.table.DefaultTableModel(
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
                true, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaMateriasDisponibles.getTableHeader().setReorderingAllowed(false);
        tablaMateriasDisponibles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaMateriasDisponiblesMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tablaMateriasDisponibles);
        if (tablaMateriasDisponibles.getColumnModel().getColumnCount() > 0) {
            tablaMateriasDisponibles.getColumnModel().getColumn(0).setPreferredWidth(10);
            tablaMateriasDisponibles.getColumnModel().getColumn(1).setPreferredWidth(10);
            tablaMateriasDisponibles.getColumnModel().getColumn(2).setPreferredWidth(150);
            tablaMateriasDisponibles.getColumnModel().getColumn(3).setPreferredWidth(10);
        }

        lblTituloTabla2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTituloTabla2.setText("Materias disponibles");
        lblTituloTabla2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout panelTablaMateriasDisponiblesLayout = new javax.swing.GroupLayout(panelTablaMateriasDisponibles);
        panelTablaMateriasDisponibles.setLayout(panelTablaMateriasDisponiblesLayout);
        panelTablaMateriasDisponiblesLayout.setHorizontalGroup(
            panelTablaMateriasDisponiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaMateriasDisponiblesLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblTituloTabla2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(155, 155, 155))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTablaMateriasDisponiblesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        getContentPane().add(panelTablaMateriasDisponibles, new org.netbeans.lib.awtextra.AbsoluteConstraints(635, 225, 470, -1));

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

        txtDni.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDni.setBorder(javax.swing.BorderFactory.createTitledBorder("DNI"));

        txtNombre.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNombre.setBorder(javax.swing.BorderFactory.createTitledBorder("Nombre"));

        txtApellido.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtApellido.setBorder(javax.swing.BorderFactory.createTitledBorder("Apellido"));

        javax.swing.GroupLayout camposLayout = new javax.swing.GroupLayout(campos);
        campos.setLayout(camposLayout);
        camposLayout.setHorizontalGroup(
            camposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(camposLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        camposLayout.setVerticalGroup(
            camposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(camposLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(camposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtId)
                    .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        getContentPane().add(campos, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 334, 625, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

	
	
	/**
	 * Método llamado al hacer click en el botón de Buscar
	 * @param evt 
	 */
    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        buscarAlumno();
    }//GEN-LAST:event_btnBuscarActionPerformed

	
	
	/**
	 * Método llamado al hacer click en el botón Salir. Cierra la ventana.
	 * @param evt 
	 */
    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        dispose();//cierra la ventana
    }//GEN-LAST:event_btnSalirActionPerformed

	
	
	/**
	 * Método llamado al seleccionar un ordenamiento. Permite elegir en qué 
	 * orden se muestran los alumnos de la tabla.
	 * @param evt 
	 */
    private void cboxOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboxOrdenActionPerformed
        if (cboxOrden.getSelectedIndex() == 0)
			ordenacion = OrdenacionAlumno.PORIDALUMNO;
        else if (cboxOrden.getSelectedIndex() == 1)
			ordenacion = OrdenacionAlumno.PORDNI;
        else if (cboxOrden.getSelectedIndex() == 2)
			ordenacion = OrdenacionAlumno.PORAPYNO;
        else // por las dudas que no eligio uno correcto
			ordenacion = OrdenacionAlumno.PORIDALUMNO;
		
		cargarListaAlumnos();
        cargarTablaAlumnos();
    }//GEN-LAST:event_cboxOrdenActionPerformed

	
	
	
	/**
	 * Al hacer click sobre un alumno de la tabla de alumnos para seleccionarlo.
	 * Muestra las tablas de materias en las que está inscripto y la tabla de
	 * materias disponibles para inscribirse.
	 * @param evt 
	 */
    private void tablaAlumnosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaAlumnosMouseClicked
        //tabla.addRowSelectionInterval(filaTabla, filaTabla); //selecciono esa fila de la tabla
        if (tablaAlumnos.getSelectedRow() != -1){ // si hay alguna fila seleccionada
			borrarTablaMaterias();
        }
        int numfila = tablaAlumnos.getSelectedRow();
        if (numfila != -1) {
            
            //mostramos las tablas de materias de acuerdo al alumno seleccionado
            int idAlumno = filaTablaAlumnos2IdAlumno(numfila); // saco el idAlumno de la fila seleccionadad de la tablaAlumnos
			cargarListaMaterias(idAlumno);  // cargamos las listas de materias inscriptas y disponibles de ese alumno
			cargarTablaMaterias();			// las mostramos en las respectivas tablas
        }
    }//GEN-LAST:event_tablaAlumnosMouseClicked

	
	
	/**
	 * Saca el filtro de búsqueda, mostrando todos los alumnos. También cambia
	 * el título y color de la tabla de alumnos para reflejar que ya no se está
	 * usando el filtro.
	 * @param evt 
	 */
    private void btnResetearFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetearFiltroActionPerformed
        resetearFiltro();
        cargarListaAlumnos();
        cargarTablaAlumnos();
        limpiarCampos();
    }//GEN-LAST:event_btnResetearFiltroActionPerformed

	
	
	
	
	/**
	 * Cuando se selecciona una materia de la tabla de materias inscriptas.
	 * Habilita el botón desinscribirse.
	 * @param evt 
	 */
    private void tablaMateriasInscriptasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMateriasInscriptasMouseClicked
        if (tablaMateriasInscriptas.getSelectedRow() != -1){ // si hay alguna fila seleccionada
			btnDesinscribirse.setEnabled(false); // deshabilito botón Desinscribirse.
        }
        int numfila = tablaMateriasInscriptas.getSelectedRow();
        if (numfila != -1) { //si hay alguna fila seleccionada en la tabla de materias disponibles
			btnDesinscribirse.setEnabled(true);
        } 
    }//GEN-LAST:event_tablaMateriasInscriptasMouseClicked

	
	
	
	/**
	 * Método llamado al hacer click en el botón desinscribirse. Saca a esa 
	 * materia seleccionada de la lista de materias en las que el alumno está
	 * inscripto.
	 * @param evt 
	 */
    private void btnDesinscribirseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesinscribirseActionPerformed
		if (tablaMateriasInscriptas.getSelectedRow() != -1){ // si hay alguna fila seleccionada
			btnDesinscribirse.setEnabled(false); // deshabilito botón Desinscribirse.
        }
        int numfilaInsc = tablaMateriasInscriptas.getSelectedRow();
        if (numfilaInsc != -1) { //si hay alguna fila seleccionada en la tabla de materias disponibles
			int idInscripcion = filaTablaInscriptas2IdInscripcion(numfilaInsc);//averiguamos el idMateria
			
		    int numfilaAlumno = tablaAlumnos.getSelectedRow();
			if (numfilaAlumno != -1) {
				int idAlumno = filaTablaAlumnos2IdAlumno(numfilaAlumno);
				inscripcionData.bajaInscripcion(idInscripcion); // Lo inscribimos
				
				//actualizamos las listas y tablas de materias
				cargarListaMaterias(idAlumno);
				cargarTablaMaterias();
			}
		}
    }//GEN-LAST:event_btnDesinscribirseActionPerformed

	
	
	
	/**
	 * Cuando se selecciona una materia de la tabla de materias disponibles.
	 * Habilita el botón inscribirse.
	 * @param evt 
	 */
    private void tablaMateriasDisponiblesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMateriasDisponiblesMouseClicked
        //tabla.addRowSelectionInterval(filaTabla, filaTabla); //selecciono esa fila de la tabla
        if (tablaMateriasDisponibles.getSelectedRow() != -1){ // si hay alguna fila seleccionada
			btnInscribirse.setEnabled(false); // deshabilito botón Inscribirse.
        }
        int numfila = tablaMateriasDisponibles.getSelectedRow();
        if (numfila != -1) { //si hay alguna fila seleccionada en la tabla de materias disponibles
			btnInscribirse.setEnabled(true);
        } 	
    }//GEN-LAST:event_tablaMateriasDisponiblesMouseClicked

	
	
	
	
	/**
	 * Método llamado al hacer click en el botón inscribirse. Inscribe al alumno
	 * en esa materia seleccionada de la lista de materias disponibles.
	 * @param evt 
	 */
    private void btnInscribirseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInscribirseActionPerformed
        if (tablaMateriasDisponibles.getSelectedRow() != -1){ // si hay alguna fila seleccionada
			btnInscribirse.setEnabled(false); // deshabilito botón Inscribirse.
        }
        int numfilaDisp = tablaMateriasDisponibles.getSelectedRow();
        if (numfilaDisp != -1) { //si hay alguna fila seleccionada en la tabla de materias disponibles
			int idMateria = filaTablaDisponibles2IdMateria(numfilaDisp);//averiguamos el idMateria
			
		    int numfilaAlumno = tablaAlumnos.getSelectedRow();
			if (numfilaAlumno != -1) {
				int idAlumno = filaTablaAlumnos2IdAlumno(numfilaAlumno);
				inscripcionData.altaInscripcion(0.0, idAlumno, idMateria); // Lo inscribimos
				
				//actualizamos las listas y tablas de materias
				cargarListaMaterias(idAlumno);
				cargarTablaMaterias();
         	}
		}
    }//GEN-LAST:event_btnInscribirseActionPerformed

	
	/**
	 * Este método se llama cuando cambió la nota de la fila seleccionada de la
	 * tabla de materias en las que el alumno está inscripto. Usa esa nota para
	 * actualizar la bd.
	 * @param evt 
	 */
    private void tablaMateriasInscriptasPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tablaMateriasInscriptasPropertyChange
        //supuestamente cambio la nota
		//System.out.println("CAMBIO LA NOTA!!!!!!!!!!");
		if (tablaMateriasInscriptas.getEditingRow() == -1) { //si no hay nada editado devuelve -1
			// System.out.println("ERROR... NO HAY FILA Editandose");
			return;
        }
        int numfilaInsc = tablaMateriasInscriptas.getEditingRow();
        if (numfilaInsc != -1) { //si hay alguna fila editandose en la tabla de materias disponibles
			int idInscripcion = filaTablaInscriptas2IdInscripcion(numfilaInsc);//averiguamos el idinscripcion
			Inscripcion inscripcion = inscripcionData.getInscripcion(idInscripcion); // obtengo la inscripcion
			double nota = filaTablaInscriptas2Nota(numfilaInsc);
			if (nota < 0.0 || nota > 10.0) // si la nota no está bien, mensaje de error y vuelve a la nota anterior
				JOptionPane.showMessageDialog(null, "Ingrese una nota válida (de 0 a 10)");
			else { //actualizo la nota
				inscripcion.setNota(nota); 
				inscripcionData.modificarInscripcion(inscripcion);
			}
			//actualizamos las listas y tablas de materias
			cargarListaMaterias(inscripcion.getAlumno().getIdalumno());
			cargarTablaMaterias();
		}
    }//GEN-LAST:event_tablaMateriasInscriptasPropertyChange

	
	
	
	
	
    
   

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
    private javax.swing.JTable tablaAlumnos;
    private javax.swing.JTable tablaMateriasDisponibles;
    private javax.swing.JTable tablaMateriasInscriptas;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}

