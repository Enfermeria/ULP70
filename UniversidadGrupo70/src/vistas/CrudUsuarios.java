
package vistas;

import accesoadatos.UsuarioData;
import accesoadatos.UsuarioData.OrdenacionUsuario;
import accesoadatos.Utils;
import java.awt.Color;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import entidades.Usuario;

/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class CrudUsuarios extends javax.swing.JInternalFrame {
	DefaultTableModel modeloTabla;
	public static List<Usuario> listaUsuarios;
	private final UsuarioData usuarioData;	
	private enum TipoEdicion {AGREGAR, MODIFICAR, BUSCAR};
	private TipoEdicion tipoEdicion = TipoEdicion.AGREGAR; //para que el boton guardar sepa que estoy queriendo hacer:
														   // Si con los campos voy a agregar, modificar o buscar un usuario
	private OrdenacionUsuario ordenacion = OrdenacionUsuario.PORIDUSUARIO; // defino el tipo de orden por defecto 
	private FiltroUsuarios filtro = new FiltroUsuarios();  //el filtro de búsqueda
	

	
	public CrudUsuarios() { //constructor
		initComponents();
		usuarioData = new UsuarioData(); 
		modeloTabla = (DefaultTableModel) tablaUsuarios.getModel();
		cargarListaUsuarios(); //carga la base de datos
		cargarTabla(); // cargo la tabla con los usuarios
	} //GestionUsuarios

	
	/** carga la lista de usuarios de la BD */
	private void cargarListaUsuarios(){ 
		if (filtro.estoyFiltrando) 
			listaUsuarios = usuarioData.getListaUsuariosXCriterioDeBusqueda(filtro.id, filtro.dni, filtro.apellido, filtro.nombre, ordenacion);
		else
			listaUsuarios = usuarioData.getListaUsuarios(ordenacion);
	}
	
	
	/** carga usuarios de la lista a la tabla */
	private void cargarTabla(){ 
		//borro las filas de la tabla
		for (int fila = modeloTabla.getRowCount() -  1; fila >= 0; fila--)
			modeloTabla.removeRow(fila);
		
		//cargo los usuarios de listaUsuarios a la tabla
		for (Usuario usuario : listaUsuarios) {
			modeloTabla.addRow(new Object[] {
				usuario.getIdusuario(),
				usuario.getDni(),
				usuario.getApellido(),
				usuario.getNombre() } 
			);
		}
		
		//como no hay fila seleccionada, deshabilito el botón Eliminar y Modificar
		if (tablaUsuarios.getSelectedRow() == -1) {// si no hay alguna fila seleccionada
			btnEliminar.setEnabled(false); // deshabilito el botón de eliminar
			btnModificar.setEnabled(false); // deshabilito el botón de Modificar
		}
	} //cargarTabla
	
	
	/** 
	 * Elimina al usuario seleccionado de la lista y la bd. 
	 * @return Devuelve true si pudo eliminarlo
	 */
	private boolean eliminarUsuario(){ 
		int fila = tablaUsuarios.getSelectedRow();
        if (fila != -1) { // Si hay alguna fila seleccionada
			int idUsuario = Integer.parseInt(txtId.getText());
			if (usuarioData.bajaUsuario(idUsuario)){ 
				listaUsuarios.remove(fila);
				return true;
			} else
				return false;
            //tabla.removeRowSelectionInterval(0, tabla.getRowCount()-1); //des-selecciono las filas de la tabla
        } else {
			JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario para eliminar", "Ningun usuario seleccionado", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	} //eliminarUsuario
	
	
	/**
	 * si no hay errores en los campos, agrega un usuario con dichos campos. 
	 * @return Devuelve true si pudo agregarlo
	 */
	private boolean agregarUsuario(){
		Usuario usuario = campos2Usuario();
		if ( usuario != null ) {
			if ( usuarioData.altaUsuario(usuario) ) {// si pudo dar de alta al usuario
				cargarListaUsuarios();
				cargarTabla();
				return true;
			} else {
				JOptionPane.showMessageDialog(this, "Debe completar correctamente todos los datos del usuario para agregarlo y sin dni duplicado", "No se puede agregar", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} else {
			// si usuario es null, no pudo transformarlo a usuario. Sigo editando
			return false;
		}
	} //agregarUsuario

	
	/** si no hay errores en los campos, modifica un usuario con dichos campos */
	private void modificarUsuario() {
		Usuario usuario = campos2Usuario();
		if ( usuario != null ) {
			if ( usuarioData.modificarUsuario(usuario) )  {// si pudo  modificar al usuario
				cargarListaUsuarios();
				cargarTabla();
			} else 
				JOptionPane.showMessageDialog(this, "Debe completar correctamente todos los datos del usuario para modificarlo y sin dni duplicado", "No se puede agregar", JOptionPane.ERROR_MESSAGE);			
		} else {
			// si usuario es null, no pudo transformarlo a usuario. Sigo editando
		}	
	} //modificarUsuario
	
	
	
	/**
	 * Busca al usuario por id, por dni, por apellido o por nombre (o por 
	 * combinación de dichos campos). 
	 * El criterio para usar un campo en la búsqueda es que no esté en blanco. 
	 * Es decir, si tiene datos, se buscará por ese dato. Por ejemplo, si puso 
	 * el id, buscará por id. Si puso el dni, buscará por dni. 
	 * Si puso el dni y Apellido, buscara por dni and apellido.
	 * 
	 * @return devuelve true sio pudo usar algún criterio de búsqueda
	 */
	private boolean buscarUsuario(){ 
		// cargo los campos de texto id, dni, apellido y nombre para buscar por esos criterior
		int idUsuario, dni;
		String apellido, nombre;
		
		//idUsuario
		try {
			if (txtId.getText().isEmpty()) // si está vacío no se usa para buscar
				idUsuario = -1;
			else
				idUsuario = Integer.valueOf(txtId.getText()); //no vacío, participa del criterio de búsqueda
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
		if ( idUsuario==-1 && dni==-1 && apellido.isEmpty() && nombre.isEmpty()  )   {
			JOptionPane.showMessageDialog(this, "Debe ingresar algún criterio para buscar", "Ningun criterio de búsqueda", JOptionPane.ERROR_MESSAGE);
			return false;
		} else { //todo Ok. Buscar por alguno de los criterior de búsqueda
			filtro.id = idUsuario;
			filtro.dni = dni;
			filtro.apellido = apellido;
			filtro.nombre = nombre;
			filtro.estoyFiltrando = true;
			cargarListaUsuarios();
			cargarTabla();
			return true; // pudo buscar
		}
	} //buscarUsuario
	

	
	
	
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
		tablaUsuarios.setEnabled(false);
		
		//Habilito los botones guardar y cancelar
		btnGuardar.setEnabled(true); // este botón es el que realmente se encargará de agregegar el usuario
		btnCancelar.setEnabled(true);
		
		//Habilito los campos para poder editar
		txtDni.setEditable(true);
		txtApellido.setEditable(true);
		txtNombre.setEditable(true);
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
		tablaUsuarios.setEnabled(true);
		
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
	} //deshabilitoParaEditar

	
	
	
	
	/** pongo los campos txtfield en blanco y deselecciono la fila de tabla */
	private void limpiarCampos(){
		//pongo los campos en blanco
		txtId.setText("");
		txtDni.setText("");
		txtApellido.setText("");
		txtNombre.setText("");
		tablaUsuarios.removeRowSelectionInterval(0, tablaUsuarios.getRowCount()-1); //des-selecciono las filas de la tabla
	} // limpiarCampos




	/**
	 * cargo los datos de la fila indicada de la tabla a los campos de texto de la pantalla 
	 * @param numfila el número de fila a cargar a los campos
	 */
	private void filaTabla2Campos(int numfila){
		txtId.setText(tablaUsuarios.getValueAt(numfila, 0)+"");
		txtDni.setText(tablaUsuarios.getValueAt(numfila, 1)+"");
		txtApellido.setText((String)tablaUsuarios.getValueAt(numfila, 2));
		txtNombre.setText((String)tablaUsuarios.getValueAt(numfila, 3));
	} //filaTabla2Campos


	
	
	/**
	 * Cargo los campos de texto de la pantalla a un objeto tipo Usuario
	 * @return El Usuario devuelto. Si hay algún error, devuelve null
	 */
	private Usuario campos2Usuario(){ 
		int idUsuario, dni;
		String apellido, nombre;
		LocalDate fechaNacimiento;
		boolean estado;
		
		//idUsuario
		try {
			if (txtId.getText().isEmpty()) // en el alta será un string vacío
				idUsuario = -1;
			else
				idUsuario = Integer.valueOf(txtId.getText()); // obtengo el identificador el usuario
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
		
		
		return new Usuario(idUsuario, nombre, apellido, dni);
	} // campos2Usuario
	
	
	
	/** cambia el icono y texto del btnGuardar a "Guardar" */
	private void botonGuardarComoGuardar(){ 
		btnGuardar.setText("Guardar");
		btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/guardar2_32x32.png")));
	}	

	
	/** cambia el icono y texto del btnGuardar guardar a "Buscar" */
	private void botonGuardarComoBuscar(){ 
		btnGuardar.setText(" Buscar ");
		btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/lupa4_32x32.png")));
	}	

	
	/** 
	 * cambia titulo y color de panel de tabla para reflejar que está filtrada.
	 * Habilita btnResetearFiltro
	*/
	private void setearFiltro(){
			//cambio el titulo de la tabla y color panel de tabla para que muestre que está filtrado
			lblTituloTabla.setText("Listado de usuarios filtrado por búsqueda");
			panelTabla.setBackground(new Color(255, 51, 51));
			btnResetearFiltro.setEnabled(true);
			filtro.estoyFiltrando = true;
	} //setearFiltro
	
	
	/** 
	 * Restaur titulo y color de panel de tabla para reflejar que ya no está filtrada.
	 * Deshabilita btnResetearFiltro
	*/
	private void resetearFiltro(){
			//cambio el titulo de la tabla y color panel de tabla para que muestre que no está filtrado
			//cambio el titulo de la tabla y color panel de tabla para que muestre que está filtrado
			lblTituloTabla.setText("Listado de usuarios");
			panelTabla.setBackground(new Color(153, 153, 255));
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

        panelDatos = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        txtDni = new javax.swing.JTextField();
        txtApellido = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        panelTabla = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaUsuarios = new javax.swing.JTable();
        lblTituloTabla = new javax.swing.JLabel();
        btnResetearFiltro = new javax.swing.JButton();
        botonera = new javax.swing.JPanel();
        btnAgregar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnBuscar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        cboxOrden = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();

        setClosable(true);
        setTitle("Gestión de usuarios ULP");
        setToolTipText("");

        panelDatos.setBackground(new java.awt.Color(153, 153, 255));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Gestión de Usuarios");

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

        btnGuardar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/guardar2_32x32.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.setEnabled(false);
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
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

        javax.swing.GroupLayout panelDatosLayout = new javax.swing.GroupLayout(panelDatos);
        panelDatos.setLayout(panelDatosLayout);
        panelDatosLayout.setHorizontalGroup(
            panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosLayout.createSequentialGroup()
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDatosLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(panelDatosLayout.createSequentialGroup()
                                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(16, 16, 16)
                                    .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(panelDatosLayout.createSequentialGroup()
                                .addComponent(btnGuardar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCancelar))))
                    .addGroup(panelDatosLayout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(jLabel1)))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        panelDatosLayout.setVerticalGroup(
            panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtId)
                    .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(72, 72, 72)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnCancelar))
                .addContainerGap())
        );

        panelTabla.setBackground(new java.awt.Color(153, 153, 255));

        tablaUsuarios.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaUsuarios.getTableHeader().setReorderingAllowed(false);
        tablaUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaUsuariosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaUsuarios);
        if (tablaUsuarios.getColumnModel().getColumnCount() > 0) {
            tablaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(8);
            tablaUsuarios.getColumnModel().getColumn(1).setPreferredWidth(30);
            tablaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(80);
            tablaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(80);
            tablaUsuarios.getColumnModel().getColumn(4).setPreferredWidth(20);
            tablaUsuarios.getColumnModel().getColumn(5).setPreferredWidth(10);
        }

        lblTituloTabla.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTituloTabla.setText("Listado de Usuarios");
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

        javax.swing.GroupLayout panelTablaLayout = new javax.swing.GroupLayout(panelTabla);
        panelTabla.setLayout(panelTablaLayout);
        panelTablaLayout.setHorizontalGroup(
            panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
                    .addGroup(panelTablaLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(lblTituloTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnResetearFiltro)))
                .addContainerGap())
        );
        panelTablaLayout.setVerticalGroup(
            panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTablaLayout.createSequentialGroup()
                .addGroup(panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnResetearFiltro)
                    .addComponent(lblTituloTabla))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        botonera.setBackground(new java.awt.Color(153, 153, 255));

        btnAgregar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/agregarAlumno32x32.png"))); // NOI18N
        btnAgregar.setText("Agregar");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        btnModificar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/modificar32x32.png"))); // NOI18N
        btnModificar.setText("Modificar");
        btnModificar.setEnabled(false);
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
            }
        });

        btnEliminar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/borrar1_32x32.png"))); // NOI18N
        btnEliminar.setText("Eliminar");
        btnEliminar.setEnabled(false);
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

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

        javax.swing.GroupLayout botoneraLayout = new javax.swing.GroupLayout(botonera);
        botonera.setLayout(botoneraLayout);
        botoneraLayout.setHorizontalGroup(
            botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(botoneraLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(btnAgregar)
                .addGap(18, 18, 18)
                .addComponent(btnModificar)
                .addGap(18, 18, 18)
                .addComponent(btnEliminar)
                .addGap(18, 18, 18)
                .addComponent(btnBuscar)
                .addGap(18, 18, 18)
                .addGroup(botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(cboxOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSalir)
                .addContainerGap())
        );
        botoneraLayout.setVerticalGroup(
            botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, botoneraLayout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAgregar)
                    .addComponent(btnModificar)
                    .addComponent(btnEliminar)
                    .addComponent(btnBuscar)
                    .addComponent(btnSalir)
                    .addComponent(cboxOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(botoneraLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(botonera, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelDatos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botonera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents




	/** Cierra la ventana (termina CrudUsuarios */
    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        dispose();//cierra la ventana
    }//GEN-LAST:event_btnSalirActionPerformed

	


	
	/** al hacer clik en una fila de la tabla, queda seleccionado un usuario.
	 * Entonces habilita los botones de eliminar y modificar
	 * @param evt 
	 */
    private void tablaUsuariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaUsuariosMouseClicked
        //tabla.addRowSelectionInterval(filaTabla, filaTabla); //selecciono esa fila de la tabla
        if (tablaUsuarios.getSelectedRow() != -1){ // si hay alguna fila seleccionada
		}
		int numfila = tablaUsuarios.getSelectedRow();
		if (numfila != -1) {			
			btnEliminar.setEnabled(true); // habilito el botón de eliminar
			btnModificar.setEnabled(true); // habilito el botón de modificar
			
			filaTabla2Campos(numfila); // cargo los campos de texto de la pantalla con datos de la fila seccionada de la tabla
		}
    }//GEN-LAST:event_tablaUsuariosMouseClicked




	/** permite editar en los campos, habilita boton de guardar/cancelar y deshabilita otros botones.
	    El alta verdadera lo realiza el botón de guardar (si no eligió cancelar) */
    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        tipoEdicion = TipoEdicion.AGREGAR;  //para que el boton guardar sepa que estoy queriendo agregar un usuario
		limpiarCampos(); //Pongo todos los campos de texto en blanco
		habilitoParaEditar();
    }//GEN-LAST:event_btnAgregarActionPerformed

	
	
	
	
	/** 
	 * Elimina el usuario seleccionado de la tabla. 
	 * Como no queda ninguno seleccionado, deshabilito botones btnModificar y btnEliminar
	 */
    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
		if ( eliminarUsuario() ) { // si pudo eliminar
			limpiarCampos(); //Pongo todos los campos de texto en blanco
			btnModificar.setEnabled(false); //deshabilito botón modificar
			btnEliminar.setEnabled(false);  //deshabilito botón eliminar
			cargarListaUsuarios();
			cargarTabla();
		}
    }//GEN-LAST:event_btnEliminarActionPerformed


	
	/** con los campos de texto de la pantalla hace un agregarUsuario, modificarUsuario o buscarUsuario
	    en base a la variable tipoEdicion, ya sea AGREGAR, MODIFICAR o BUSCAR */
    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        
		if ( tipoEdicion == TipoEdicion.AGREGAR ){ //agregar el usuario
			agregarUsuario();
			resetearFiltro();
		} else if ( tipoEdicion == TipoEdicion.MODIFICAR ) { // modificar el usuario
			modificarUsuario();
			resetearFiltro();
		} else { // tipoEdicion = BUSCAR: quiere buscar un usuario
			buscarUsuario();
			setearFiltro();
		}
		
		limpiarCampos();
		botonGuardarComoGuardar();//por si estaba buscando cambio icono y texto del btnGuardar a "Guardar"
		deshabilitoParaEditar();
    }//GEN-LAST:event_btnGuardarActionPerformed

	
	

	/** Cancela la edición de campos para agregar, modificar o buscar. */
    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
		limpiarCampos();
		botonGuardarComoGuardar(); //por si estaba buscando cambio icono y texto del btnGuardar a "Guardar"
		deshabilitoParaEditar();
		
    }//GEN-LAST:event_btnCancelarActionPerformed

	
	
	/** 
	 * Permite editar en los campos, habilita boton de guardar/cancelar y deshabilita otros botones.
	 * La modificación verdadera lo realiza el botón de guardar (si no eligió cancelar)
	 */ 
    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        tipoEdicion = TipoEdicion.MODIFICAR; //para que el boton guardar sepa que estoy queriendo modificar un usuario
		habilitoParaEditar();
    }//GEN-LAST:event_btnModificarActionPerformed

	
	
	/**
	 * Permite editar en los campos, cambia el botón guardar a buscar, 
	 * habilita boton de guardar/cancelar y deshabilita otros botones.
	 * La búsqueda verdadera lo realiza el botón de guardar (si no eligió cancelar)
	 */
    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        tipoEdicion = TipoEdicion.BUSCAR; //para que el boton guardar sepa que estoy queriendo buscar un usuario
		limpiarCampos();
		botonGuardarComoBuscar(); //cambio icono y texto del btnGuardar a "Buscar"
		habilitoParaBuscar();
    }//GEN-LAST:event_btnBuscarActionPerformed

	
	
	/** 
	 * Restaura la tabla a la lista total, pone los campos en blanco, 
	 * restaura el color de fondo del panel y deshabilita btnResetearFiltro
	 * @param evt 
	 */
    private void btnResetearFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetearFiltroActionPerformed
        resetearFiltro();
		cargarListaUsuarios();
		cargarTabla();
		limpiarCampos();
		botonGuardarComoGuardar();//por si estaba buscando cambio icono y texto del btnGuardar a "Guardar"
		deshabilitoParaEditar();
    }//GEN-LAST:event_btnResetearFiltroActionPerformed

	
	
    private void cboxOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboxOrdenActionPerformed
        if (cboxOrden.getSelectedIndex() == 0)
			ordenacion = OrdenacionUsuario.PORIDUSUARIO;
		else if (cboxOrden.getSelectedIndex() == 1)
			ordenacion = OrdenacionUsuario.PORDNI;
		else if (cboxOrden.getSelectedIndex() == 2)
			ordenacion = OrdenacionUsuario.PORAPYNO;
		else // por las dudas que no eligio uno correcto
			ordenacion = OrdenacionUsuario.PORIDUSUARIO;
		
		cargarListaUsuarios();
		cargarTabla();
		limpiarCampos();
		botonGuardarComoGuardar();
		deshabilitoParaEditar();
    }//GEN-LAST:event_cboxOrdenActionPerformed

    private void cboxOrdenPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cboxOrdenPropertyChange
        System.out.println("Cambio property de cboxOrden");
    }//GEN-LAST:event_cboxOrdenPropertyChange

	


	
	

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel botonera;
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnResetearFiltro;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox<String> cboxOrden;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTituloTabla;
    private javax.swing.JPanel panelDatos;
    private javax.swing.JPanel panelTabla;
    private javax.swing.JTable tablaUsuarios;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
} //GestionUsuarios

/**
 * Es una clase para agrupar y almacenar los datos con los que se filtra una búsqueda
 * @author John David Molina Velarde
 */
class FiltroUsuarios{
	int id;
	int dni;
	String apellido;
	String nombre;
	boolean estoyFiltrando;

	public FiltroUsuarios() { // constructor
		id = -1;
		dni = -1;
		apellido = "";
		nombre = "";
		estoyFiltrando = false;
	}
}
	
