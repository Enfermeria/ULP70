
package vistas;

import accesoadatos.AlumnoData;
import accesoadatos.InscripcionData;
import accesoadatos.MateriaData;
import entidades.Alumno;
import entidades.Inscripcion;
import entidades.Materia;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.List;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class AutogestionAlumnos extends javax.swing.JFrame {
    DefaultTableModel modeloTablaMateriasInscriptas, modeloTablaMateriasDisponibles;
    public static List<Inscripcion> listaInscripciones; //lista de inscripciones de un alumno
    public static List<Materia> listaMateriasDisponibles;//lista de materias en las que NO está inscripto un alumno
    private final AlumnoData alumnoData;	
    private final MateriaData materiaData;
    private final InscripcionData inscripcionData;
	// private enum TipoEdicion {AGREGAR, MODIFICAR, BUSCAR};
	
    private Alumno alumno;   
   
    public AutogestionAlumnos(Alumno alumno) {
	initComponents();
        this.alumno= alumno;
		alumnoData = new AlumnoData();
		materiaData = new MateriaData();
        inscripcionData = new InscripcionData();
        modeloTablaMateriasInscriptas = (DefaultTableModel) tablaMateriasInscriptas.getModel();
        modeloTablaMateriasDisponibles = (DefaultTableModel) tablaMateriasDisponibles.getModel();
	
		//cargo los campos con el alumno que me pasaron
		txtId.setText("" + alumno.getIdalumno());
		txtDni.setText("" + alumno.getDni());
		txtApellido.setText(alumno.getApellido());
		txtNombre.setText(alumno.getNombre());
		//jdcFechaNacimiento.setDate(new Date(76, 8, 15)); 
		//checkboxEstado.setSelected(true);

		//cargo las materias inscriptas y disponibles de ese alumno
		cargarListaMaterias(alumno.getIdalumno());
		cargarTablaMaterias();
	} // constructor

	
	
	
	/**Carga listaMateriasInscriptas a la tablaMateriasInscriptas y 
	 * listaMateriasDisponibles a la tablaMateriasDisponibles
	 */
	private void cargarTablaMaterias(){
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
	 * Devuelve el idAlumno de la fila seleccionada de la tabla de alumnos
	 * @param numfila el número de fila a cargar a los campos
	 */
	private int txtIdAlumno2IdAlumno(){
		return Integer.valueOf(txtId.getText()); // obtengo el identificador el alumno		
	} //txtIdAlumno2IdAlumno


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
	
	private int filaTablaInscriptas2IdInscripcion(int numfila){
		return (Integer)tablaMateriasInscriptas.getValueAt(numfila, 0);			
	} //filaTablaInscriptas2IdMateria
	
	
	private double filaTablaInscriptas2Nota(int numfila){
		return (Double)tablaMateriasInscriptas.getValueAt(numfila, 4);			
	} //filaTablaInscriptas2IdMateria
	
	
	
	
	/**
	 * Para poder poner el ícono de la ULP en la ventana
	 * @return 
	 */	
	@Override
	public Image getIconImage() { // defino el icono del jFrame
		Image retValue = Toolkit.getDefaultToolkit().
				getImage(ClassLoader.getSystemResource("imagenes/ulp_32x32.png")); //icono de la ULP
		return retValue;
	}
	
	
	
/*=====================================================================================================================*/	


	

	
	
	
	
	
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        panelDatos = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        txtDni = new javax.swing.JTextField();
        txtApellido = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        lblFoto = new javax.swing.JLabel();
        btnSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImage(getIconImage());

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
                false, false, false, false, false
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
        jScrollPane2.setViewportView(tablaMateriasInscriptas);

        lblTituloTabla1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTituloTabla1.setText("Materias que cursa");
        lblTituloTabla1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout panelTablaMateriasInscriptasLayout = new javax.swing.GroupLayout(panelTablaMateriasInscriptas);
        panelTablaMateriasInscriptas.setLayout(panelTablaMateriasInscriptasLayout);
        panelTablaMateriasInscriptasLayout.setHorizontalGroup(
            panelTablaMateriasInscriptasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaMateriasInscriptasLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblTituloTabla1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(155, 155, 155))
            .addGroup(panelTablaMateriasInscriptasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelTablaMateriasInscriptasLayout.setVerticalGroup(
            panelTablaMateriasInscriptasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTablaMateriasInscriptasLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(lblTituloTabla1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

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
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnInscribirse.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnInscribirse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flecha_arriba16x16.png"))); // NOI18N
        btnInscribirse.setText("Inscribirse");
        btnInscribirse.setEnabled(false);
        btnInscribirse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInscribirseActionPerformed(evt);
            }
        });

        btnDesinscribirse.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnDesinscribirse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flecha_abajo16x16.png"))); // NOI18N
        btnDesinscribirse.setText("Desinscribirse");
        btnDesinscribirse.setEnabled(false);
        btnDesinscribirse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesinscribirseActionPerformed(evt);
            }
        });

        panelDatos.setBackground(new java.awt.Color(153, 153, 255));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Autogestión de Alumnos");

        txtId.setEditable(false);
        txtId.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtId.setBorder(javax.swing.BorderFactory.createTitledBorder("Id"));

        txtDni.setEditable(false);
        txtDni.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDni.setBorder(javax.swing.BorderFactory.createTitledBorder("DNI"));

        txtApellido.setEditable(false);
        txtApellido.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtApellido.setBorder(javax.swing.BorderFactory.createTitledBorder("Apellido"));

        txtNombre.setEditable(false);
        txtNombre.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNombre.setBorder(javax.swing.BorderFactory.createTitledBorder("Nombre"));

        lblFoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alumno148x149.png"))); // NOI18N

        javax.swing.GroupLayout panelDatosLayout = new javax.swing.GroupLayout(panelDatos);
        panelDatos.setLayout(panelDatosLayout);
        panelDatosLayout.setHorizontalGroup(
            panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosLayout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosLayout.createSequentialGroup()
                        .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelDatosLayout.createSequentialGroup()
                                .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(16, 16, 16)
                                .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(19, 19, 19))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosLayout.createSequentialGroup()
                        .addComponent(lblFoto)
                        .addGap(67, 67, 67))))
            .addGroup(panelDatosLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelDatosLayout.setVerticalGroup(
            panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosLayout.createSequentialGroup()
                .addContainerGap(43, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(lblFoto)
                .addGap(18, 18, 18)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtId)
                    .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55))
        );

        btnSalir.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/salir2_16x16.png"))); // NOI18N
        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(btnInscribirse)
                        .addGap(52, 52, 52)
                        .addComponent(btnDesinscribirse)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSalir)
                        .addGap(36, 36, 36))
                    .addComponent(panelTablaMateriasInscriptas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelTablaMateriasDisponibles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelDatos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelTablaMateriasInscriptas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnInscribirse)
                            .addComponent(btnDesinscribirse)
                            .addComponent(btnSalir))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelTablaMateriasDisponibles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	
	
	
	
	
    private void tablaMateriasInscriptasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMateriasInscriptasMouseClicked
        if (tablaMateriasInscriptas.getSelectedRow() == -1){ // si no hay alguna fila seleccionada
            btnDesinscribirse.setEnabled(false); // deshabilito botón Desinscribirse.
        }else{
            btnDesinscribirse.setEnabled(true);
        }
    }//GEN-LAST:event_tablaMateriasInscriptasMouseClicked

	
	
	
	
    private void tablaMateriasDisponiblesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMateriasDisponiblesMouseClicked
        if (tablaMateriasDisponibles.getSelectedRow() == -1){ // si no hay alguna fila seleccionada
            btnInscribirse.setEnabled(false); // deshabilito botón Inscribirse.
        } else {
		    btnInscribirse.setEnabled(true);
        }
    }//GEN-LAST:event_tablaMateriasDisponiblesMouseClicked

    private void btnInscribirseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInscribirseActionPerformed
        if (tablaMateriasDisponibles.getSelectedRow() != -1){ // si hay alguna fila seleccionada
            btnInscribirse.setEnabled(false); // deshabilito botón Inscribirse.
        }
        int numfilaDisp = tablaMateriasDisponibles.getSelectedRow();
        if (numfilaDisp != -1) { //si hay alguna fila seleccionada en la tabla de materias disponibles
            int idMateria = filaTablaDisponibles2IdMateria(numfilaDisp);//averiguamos el idMateria

            inscripcionData.altaInscripcion(0.0, alumno.getIdalumno(), idMateria); // Lo inscribimos

            //actualizamos las listas y tablas de materias
            cargarListaMaterias(alumno.getIdalumno());
            cargarTablaMaterias();
        }
    }//GEN-LAST:event_btnInscribirseActionPerformed

    private void btnDesinscribirseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesinscribirseActionPerformed
        if (tablaMateriasInscriptas.getSelectedRow() != -1){ // si hay alguna fila seleccionada
            btnDesinscribirse.setEnabled(false); // deshabilito botón Desinscribirse.
        }
        int numfilaInsc = tablaMateriasInscriptas.getSelectedRow();
        if (numfilaInsc != -1) { //si hay alguna fila seleccionada en la tabla de materias inscriptas
            int idInscripcion = filaTablaInscriptas2IdInscripcion(numfilaInsc);//averiguamos el idInscripcion

            inscripcionData.bajaInscripcion(idInscripcion); // Lo desinscribimos

            //actualizamos las listas y tablas de materias
            cargarListaMaterias(alumno.getIdalumno());
            cargarTablaMaterias();
        }
    }//GEN-LAST:event_btnDesinscribirseActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        dispose(); // cierra la ventana
    }//GEN-LAST:event_btnSalirActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
		/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(AutogestionAlumnos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(AutogestionAlumnos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(AutogestionAlumnos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(AutogestionAlumnos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the form */
//		java.awt.EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				new AutogestionAlumnos().setVisible(true);
//			}
//		});
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDesinscribirse;
    private javax.swing.JButton btnInscribirse;
    private javax.swing.JButton btnSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblFoto;
    private javax.swing.JLabel lblTituloTabla1;
    private javax.swing.JLabel lblTituloTabla2;
    private javax.swing.JPanel panelDatos;
    private javax.swing.JPanel panelTablaMateriasDisponibles;
    private javax.swing.JPanel panelTablaMateriasInscriptas;
    private javax.swing.JTable tablaMateriasDisponibles;
    private javax.swing.JTable tablaMateriasInscriptas;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}
