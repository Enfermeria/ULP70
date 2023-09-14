
package vistas;

import accesoadatos.AlumnoData;
import accesoadatos.AlumnoData.Ordenacion;
import accesoadatos.Utils;
import entidades.Alumno;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class GestionInscripciones extends javax.swing.JInternalFrame {
	DefaultTableModel modeloTablaAlumnos;
	public static List<Alumno> listaAlumnos;
	private final AlumnoData alumnoData;	
	// private enum TipoEdicion {AGREGAR, MODIFICAR, BUSCAR};
	private Ordenacion ordenacion = Ordenacion.PORIDALUMNO; // defino el tipo de orden por defecto 
	private Filtro filtro = new Filtro();  //el filtro de búsqueda
	

	/**
	 * Creates new form GestionInscripciones
	 */
	public GestionInscripciones() {
		initComponents();
		alumnoData = new AlumnoData(); 
		modeloTablaAlumnos = (DefaultTableModel) tablaAlumnos.getModel();
		cargarListaAlumnos(); //carga la base de datos
		cargarTablaAlumnos(); // cargo la tabla con los alumnos
	} // constructor

		/** carga la lista de alumnos de la BD */
	private void cargarListaAlumnos(){ 
		if (filtro.estoyFiltrando) 
			listaAlumnos = alumnoData.getListaAlumnosXCriterioDeBusqueda(filtro.id, filtro.dni, filtro.apellido, filtro.nombre, ordenacion);
		else
			listaAlumnos = alumnoData.getListaAlumnos(ordenacion);
	}
	
	
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
		}
	} //cargarTablaAlumnos
	
	
	
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
			return true; // pudo buscar
		}
	} //buscarAlumno
	

	
	
	
	/** deshabilito todos los botones y tabla, habilito guardar/cancelar */
	private void habilitoParaBuscar(){ 
		habilitoParaEditar();
		txtId.setEditable(true);
	} //habilitoParaBuscar

	
		
	
	/** deshabilito todos los botones y tabla, habilito guardar/cancelar */
	private void habilitoParaEditar(){ 
		// deshabilito todos los botones (menos salir)
		btnAgregar.setEnabled(false);
		btnModificar.setEnabled(false); //deshabilito botón modificar
		btnEliminar.setEnabled(false);  //deshabilito botón eliminar
		btnBuscar.setEnabled(false);
		cboxOrden.setEnabled(false);
		
		//Deshabilito la Tabla para que no pueda hacer click
		tablaAlumnos.setEnabled(false);
		
		//Habilito los botones guardar y cancelar
		btnGuardar.setEnabled(true); // este botón es el que realmente se encargará de agregegar el alumno
		btnCancelar.setEnabled(true);
		
		//Habilito los campos para poder editar
		txtDni.setEditable(true);
		txtApellido.setEditable(true);
		txtNombre.setEditable(true);
		jdcFechaNacimiento.setEnabled(true);
		checkboxEstado.setEnabled(true);
	} //habilitoParaEditar

	
	
	
	/** habilito todos los botones y tabla, deshabilito guardar/cancelar y modificar */
	private void deshabilitoParaEditar(){ 
		limpiarCampos(); //Pongo todos los campos de texto en blanco
		// habilito todos los botones (menos salir)
		btnAgregar.setEnabled(true);
		btnBuscar.setEnabled(true);
		cboxOrden.setEnabled(true);
		
		//sigo deshabilitando los botones modificar y eliminar porque no hay una fila seleccionada.
		btnModificar.setEnabled(false); //deshabilito botón modificar
		btnEliminar.setEnabled(false);  //deshabilito botón eliminar
		
		//Habilito la Tabla para que pueda hacer click
		tablaAlumnos.setEnabled(true);
		
		//Deshabilito el boton guardar 
		btnGuardar.setEnabled(false);  
		botonGuardarComoGuardar(); //por si estaba buscando cambio icono y texto del btnGuardar a "Guardar"
		
		//deshabilito el boton cancelar
		btnCancelar.setEnabled(false);

		//deshabilito los campos para poder que no pueda editar
		txtId.setEditable(false);
		txtDni.setEditable(false);
		txtApellido.setEditable(false);
		txtNombre.setEditable(false);
		jdcFechaNacimiento.setEnabled(false); //AVERIGUAR COMO HACER SETEDITABLE(FALSE), ASI NO QUEDA COLOR DISMINUIDO
		checkboxEstado.setEnabled(false);	  //AVERIGUAR COMO HACER SETEDITABLE(FALSE), ASI NO QUEDA COLOR DISMINUIDO
	} //deshabilitoParaEditar

	
	
	
	
	/** pongo los campos txtfield en blanco y deselecciono la fila de tabla */
	private void limpiarCampos(){
		//pongo los campos en blanco
		txtId.setText("");
		txtDni.setText("");
		txtApellido.setText("");
		txtNombre.setText("");
		jdcFechaNacimiento.setDate(null);
		checkboxEstado.setSelected(false);
		tablaAlumnos.removeRowSelectionInterval(0, tablaAlumnos.getRowCount()-1); //des-selecciono las filas de la tabla
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
		jdcFechaNacimiento.setDate(Utils.localDate2Date((LocalDate)tablaAlumnos.getValueAt(numfila, 4)));
		checkboxEstado.setSelected((Boolean)tablaAlumnos.getValueAt(numfila, 5));
	} //filaTabla2Campos


	
	
	/**
	 * Cargo los campos de texto de la pantalla a un objeto tipo Alumno
	 * @return El Alumno devuelto. Si hay algún error, devuelve null
	 */
	private Alumno campos2Alumno(){ 
		int idAlumno, dni;
		String apellido, nombre;
		LocalDate fechaNacimiento;
		boolean estado;
		
		//idAlumno
		try {
			if (txtId.getText().isEmpty()) // en el alta será un string vacío
				idAlumno = -1;
			else
				idAlumno = Integer.valueOf(txtId.getText()); // obtengo el identificador el alumno
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "El Id debe ser un número válido", "Id no válido", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		//dni
		try {
			dni = Integer.valueOf(txtDni.getText());
				
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "El DNI debe ser un número válido", "DNI no válido", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		//apellido y nombre
		apellido = txtApellido.getText();
		nombre = txtNombre.getText();
		
		//fechaNacimiento
		if (jdcFechaNacimiento.getDate() != null)
			fechaNacimiento = Utils.date2LocalDate(jdcFechaNacimiento.getDate());
		else {
			JOptionPane.showMessageDialog(this, "La fecha de nacimiento debe ser una fecha válida", "Nacimiento no válido", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		//estado
		estado = checkboxEstado.isSelected(); 
		
		return new Alumno(idAlumno, dni, apellido, nombre, fechaNacimiento, estado);
	} // campos2Alumno
	
	
	
	
	
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
        btnBuscar2 = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        panelTablaAlumnos = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaAlumnos = new javax.swing.JTable();
        lblTituloTabla = new javax.swing.JLabel();
        btnResetearFiltro = new javax.swing.JButton();
        panelTablaMateriasInscriptas = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaMaterias = new javax.swing.JTable();
        lblTituloTabla1 = new javax.swing.JLabel();
        panelTablaMateriasDisponibles = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaMaterias1 = new javax.swing.JTable();
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
        cboxOrden.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cboxOrdenPropertyChange(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Ordenado");

        btnBuscar2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnBuscar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/guardar2_32x32.png"))); // NOI18N
        btnBuscar2.setText("Buscar");
        btnBuscar2.setEnabled(false);
        btnBuscar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscar2ActionPerformed(evt);
            }
        });

        btnCancelar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/cancelar32x32.png"))); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.setEnabled(false);
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout botoneraLayout = new javax.swing.GroupLayout(botonera);
        botonera.setLayout(botoneraLayout);
        botoneraLayout.setHorizontalGroup(
            botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(botoneraLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnBuscar2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnBuscar)
                .addGap(18, 18, 18)
                .addGroup(botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(cboxOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSalir)
                .addContainerGap())
        );
        botoneraLayout.setVerticalGroup(
            botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, botoneraLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(botoneraLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnBuscar2)
                                .addComponent(btnCancelar))
                            .addGroup(botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnBuscar)
                                .addComponent(cboxOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel2)
                    .addComponent(btnSalir))
                .addGap(11, 11, 11))
        );

        getContentPane().add(botonera, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 404, -1, -1));

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

        lblTituloTabla.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTituloTabla.setText("Listado de Alumnos");
        lblTituloTabla.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

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
                        .addComponent(lblTituloTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnResetearFiltro)))
                .addContainerGap())
        );
        panelTablaAlumnosLayout.setVerticalGroup(
            panelTablaAlumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTablaAlumnosLayout.createSequentialGroup()
                .addGroup(panelTablaAlumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnResetearFiltro)
                    .addComponent(lblTituloTabla))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(panelTablaAlumnos, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 625, 328));

        panelTablaMateriasInscriptas.setBackground(new java.awt.Color(153, 153, 255));

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
        jScrollPane2.setViewportView(tablaMaterias);

        lblTituloTabla1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTituloTabla1.setText("Materias en curso");
        lblTituloTabla1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout panelTablaMateriasInscriptasLayout = new javax.swing.GroupLayout(panelTablaMateriasInscriptas);
        panelTablaMateriasInscriptas.setLayout(panelTablaMateriasInscriptasLayout);
        panelTablaMateriasInscriptasLayout.setHorizontalGroup(
            panelTablaMateriasInscriptasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaMateriasInscriptasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTablaMateriasInscriptasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(panelTablaMateriasInscriptasLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(lblTituloTabla1, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                        .addGap(145, 145, 145)))
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

        getContentPane().add(panelTablaMateriasInscriptas, new org.netbeans.lib.awtextra.AbsoluteConstraints(635, 0, -1, -1));

        panelTablaMateriasDisponibles.setBackground(new java.awt.Color(153, 153, 255));

        tablaMaterias1.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaMaterias1.getTableHeader().setReorderingAllowed(false);
        tablaMaterias1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaMaterias1MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tablaMaterias1);

        lblTituloTabla2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTituloTabla2.setText("Materias disponibles");
        lblTituloTabla2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout panelTablaMateriasDisponiblesLayout = new javax.swing.GroupLayout(panelTablaMateriasDisponibles);
        panelTablaMateriasDisponibles.setLayout(panelTablaMateriasDisponiblesLayout);
        panelTablaMateriasDisponiblesLayout.setHorizontalGroup(
            panelTablaMateriasDisponiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaMateriasDisponiblesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTablaMateriasDisponiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(panelTablaMateriasDisponiblesLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(lblTituloTabla2, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                        .addGap(145, 145, 145)))
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

        getContentPane().add(panelTablaMateriasDisponibles, new org.netbeans.lib.awtextra.AbsoluteConstraints(635, 225, 385, -1));

        btnInscribirse.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnInscribirse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flecha_arriba16x16.png"))); // NOI18N
        btnInscribirse.setText("Inscribirse");
        btnInscribirse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInscribirseActionPerformed(evt);
            }
        });
        getContentPane().add(btnInscribirse, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 180, -1, -1));

        btnDesinscribirse.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnDesinscribirse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flecha_abajo16x16.png"))); // NOI18N
        btnDesinscribirse.setText("Desinscribirse");
        btnDesinscribirse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesinscribirseActionPerformed(evt);
            }
        });
        getContentPane().add(btnDesinscribirse, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 180, -1, -1));

        campos.setBackground(new java.awt.Color(153, 153, 255));

        txtId.setEditable(false);
        txtId.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtId.setBorder(javax.swing.BorderFactory.createTitledBorder("Id"));

        txtDni.setEditable(false);
        txtDni.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDni.setBorder(javax.swing.BorderFactory.createTitledBorder("DNI"));

        txtNombre.setEditable(false);
        txtNombre.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNombre.setBorder(javax.swing.BorderFactory.createTitledBorder("Nombre"));

        txtApellido.setEditable(false);
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

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        limpiarCampos();
        botonGuardarComoBuscar(); //cambio icono y texto del btnGuardar a "Buscar"
        habilitoParaBuscar();
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        dispose();//cierra la ventana
    }//GEN-LAST:event_btnSalirActionPerformed

    private void cboxOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboxOrdenActionPerformed
        if (cboxOrden.getSelectedIndex() == 0)
        ordenacion = Ordenacion.PORIDALUMNO;
        else if (cboxOrden.getSelectedIndex() == 1)
        ordenacion = Ordenacion.PORDNI;
        else if (cboxOrden.getSelectedIndex() == 2)
        ordenacion = Ordenacion.PORAPYNO;
        else // por las dudas que no eligio uno correcto
        ordenacion = OrdenacionbtnInscribirse     cargarListaAlumnos();
        cargarTabla();
        limpiarCampos();
        botonGuardarComoGuardar();
        deshabilitoParaEditar();
    }//GEN-LAST:event_cboxOrdenActionPerformed

    private void cboxOrdenPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cboxOrdenPropertyChange
        System.out.println("Cambio property de cboxOrden");
    }//GEN-LAST:event_cboxOrdenPropertyChange

    private void tablaAlumnosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaAlumnosMouseClicked
        //tabla.addRowSelectionInterval(filaTabla, filaTabla); //selecciono esa fila de la tabla
        if (tablaAlumnos.getSelectedRow() != -1){ // si hay alguna fila seleccionada
        }
        int numfila = tablaAlumnos.getSelectedRow();
        if (numfila != -1) {
            btnEliminar.setEnabled(true); // habilito el botón de eliminar
            btnModificar.setEnabled(true); // habilito el botón de modificar

            filaTabla2Campos(numfila); // cargo los campos de texto de la pantalla con datos de la fila seccionada de la tabla
        }
    }//GEN-LAST:event_tablaAlumnosMouseClicked

    private void btnResetearFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetearFiltroActionPerformed
        resetearFiltro();
        cargarListaAlumnos();
        cargarTabla();
        limpiarCampos();
        botonGuardarComoGuardar();//por si estaba buscando cambio icono y texto del btnGuardar a "Guardar"
        deshabilitoParaEditar();
    }//GEN-LAST:event_btnResetearFiltroActionPerformed

    private void tablaMateriasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMateriasMouseClicked
        //tabla.addRowSelectionInterval(filaTabla, filaTabla); //selecciono esa fila de la tabla
        if (tablaMaterias.getSelectedRow() != -1){ // si hay alguna fila seleccionada
        }
        int numfila = tablaMaterias.getSelectedRow();
        if (numfila != -1) {
            btnEliminar.setEnabled(true); // habilito el botón de eliminar
            btnModificar.setEnabled(true); // habilito el botón de modificar

            filaTabla2Campos(numfila); // cargo los campos de texto de la pantalla con datos de la fila seccionada de la tabla
        }
    }//GEN-LAST:event_tablaMateriasMouseClicked

    private void btnDesinscribirseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesinscribirseActionPerformed
        resetearFiltro();
        cargarListaMaterias();
        cargarTabla();
        limpiarCampos();
        botonGuardarComoGuardar();//por si estaba buscando cambio icono y texto del btnGuardar a "Guardar"
        deshabilitoParaEditar();
    }//GEN-LAST:event_btnDesinscribirseActionPerformed

    private void tablaMaterias1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMaterias1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tablaMaterias1MouseClicked

    private void btnInscribirseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInscribirseActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnInscribirseActionPerformed

    private void btnBuscar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscar2ActionPerformed

        if ( tipoEdicion == TipoEdicion.AGREGAR ){ //agregar el alumno
            agregarAlumno();
            resetearFiltro();
        } else if ( tipoEdicion == TipoEdicion.MODIFICAR ) { // modificar el alumno
            modificarAlumno();
            resetearFiltro();
        } else { // tipoEdicion = BUSCAR: quiere buscar un alumno
            buscarAlumno();
            setearFiltro();
        }

        limpiarCampos();
        botonGuardarComoGuardar();//por si estaba buscando cambio icono y texto del btnGuardar a "Guardar"
        deshabilitoParaEditar();
    }//GEN-LAST:event_btnBuscar2ActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        limpiarCampos();
        botonGuardarComoGuardar(); //por si estaba buscando cambio icono y texto del btnGuardar a "Guardar"
        deshabilitoParaEditar();

    }//GEN-LAST:event_btnCancelarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel botonera;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnBuscar2;
    private javax.swing.JButton btnCancelar;
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
    private javax.swing.JLabel lblTituloTabla;
    private javax.swing.JLabel lblTituloTabla1;
    private javax.swing.JLabel lblTituloTabla2;
    private javax.swing.JPanel panelTablaAlumnos;
    private javax.swing.JPanel panelTablaMateriasDisponibles;
    private javax.swing.JPanel panelTablaMateriasInscriptas;
    private javax.swing.JTable tablaAlumnos;
    private javax.swing.JTable tablaMaterias;
    private javax.swing.JTable tablaMaterias1;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}
