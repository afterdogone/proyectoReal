package proyectoreal;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import proyectoreal.controlador.ControladorENEL;
import proyectoreal.interfaz.InterfazENEL;
import proyectoreal.modelo.Cliente;
import proyectoreal.modelo.DetalleVenta;
import proyectoreal.modelo.Nomina;
import proyectoreal.modelo.Servicio;
import proyectoreal.modelo.Trabajador;
import proyectoreal.modelo.Venta;

/**
 * Ventana principal del sistema POS y Gestión Operativa de
 * ENEL Servicios de Limpieza (4 pestañas en un JTabbedPane).
 *
 * @author EliLe
 */
public class ProyectoFinal extends javax.swing.JFrame {

    // Venta en curso: funciona como el carrito. Se reinicia despues de cobrar.
    private Venta objVenta = new Venta();

    // Modelos de las tablas (se crean en inicializar())
    private DefaultTableModel modeloCarrito;
    private DefaultTableModel modeloVentas;
    private DefaultTableModel modeloClientes;
    private DefaultTableModel modeloTrabajadores;
    private DefaultTableModel modeloNomina;
    private DefaultTableModel modeloCatalogo;

    public ProyectoFinal() {
        initComponents();
        inicializar();
    }

    /** Prepara la ventana: titulos, tablas, combos y datos precargados. */
    private void inicializar() {
        setTitle(InterfazENEL.TITULO_VENTANA);
        setLocationRelativeTo(null);

        // Titulos de las pestañas
        tabPrincipal.setTitleAt(0, InterfazENEL.TAB_VENTA);
        tabPrincipal.setTitleAt(1, InterfazENEL.TAB_CLIENTES);
        tabPrincipal.setTitleAt(2, InterfazENEL.TAB_NOMINA);
        tabPrincipal.setTitleAt(3, InterfazENEL.TAB_CATALOGO);

        // Tablas con los encabezados de InterfazENEL
        modeloCarrito = crearModelo(InterfazENEL.ENCABEZADO_CARRITO);
        modeloVentas = crearModelo(InterfazENEL.ENCABEZADO_VENTAS);
        modeloClientes = crearModelo(InterfazENEL.ENCABEZADO_CLIENTES);
        modeloTrabajadores = crearModelo(InterfazENEL.ENCABEZADO_TRABAJADORES);
        modeloNomina = crearModelo(InterfazENEL.ENCABEZADO_NOMINA);
        modeloCatalogo = crearModelo(InterfazENEL.ENCABEZADO_CATALOGO);
        tblCarrito.setModel(modeloCarrito);
        tblVentas.setModel(modeloVentas);
        tblClientes.setModel(modeloClientes);
        tblTrabajadores.setModel(modeloTrabajadores);
        tblNomina.setModel(modeloNomina);
        tblCatalogo.setModel(modeloCatalogo);

        // Combos con opciones fijas
        cmbTipoCliente.setModel(new DefaultComboBoxModel<>(InterfazENEL.TIPOS_CLIENTE));
        cmbPuesto.setModel(new DefaultComboBoxModel<>(InterfazENEL.PUESTOS));
        cmbMetodoPago.setModel(new DefaultComboBoxModel<>(InterfazENEL.METODOS_PAGO));
        cmbTipoCatalogo.setModel(new DefaultComboBoxModel<>(InterfazENEL.TIPOS_CATALOGO));

        // Ajustes de los campos
        txaTicket.setEditable(false);
        txtIdCatalogo.setEditable(false);
        txtStockCatalogo.setEnabled(false);   // el primer tipo es "Servicio" (sin stock)
        txtHorasExtra.setText("0");
        txtDescuentos.setText("0");

        // Llenar tablas y combos con los datos del controlador
        cargarTablaCarrito();
        cargarTablaVentas();
        cargarTablaClientes();
        cargarTablaTrabajadores();
        cargarTablaNomina();
        cargarTablaCatalogo();
    }

    // ===================== METODOS DE APOYO =====================

    /** Modelo de tabla cuyas celdas no se pueden editar con doble clic. */
    private DefaultTableModel crearModelo(String[] encabezados) {
        return new DefaultTableModel(encabezados, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, InterfazENEL.TITULO_ERROR, JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarAviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, InterfazENEL.TITULO_AVISO, JOptionPane.INFORMATION_MESSAGE);
    }

    /** Pregunta "¿Seguro...?" y regresa true si el usuario contesta Sí. */
    private boolean confirmarEliminar() {
        int respuesta = JOptionPane.showConfirmDialog(this, InterfazENEL.MSG_CONFIRMAR_ELIMINAR,
                InterfazENEL.TITULO_CONFIRMAR, JOptionPane.YES_NO_OPTION);
        return respuesta == JOptionPane.YES_OPTION;
    }

    /** Lee un numero con decimales (>= 0). Si no es valido lanza NumberFormatException con un mensaje claro. */
    private double leerNumero(JTextField txt, String campo) {
        double valor;
        try {
            valor = Double.parseDouble(txt.getText().trim());
        } catch (NumberFormatException e) {
            throw new NumberFormatException(InterfazENEL.MSG_NUMERO_INVALIDO + campo);
        }
        if (valor < 0 || Double.isNaN(valor) || Double.isInfinite(valor)) {
            throw new NumberFormatException(InterfazENEL.MSG_NUMERO_INVALIDO + campo);
        }
        return valor;
    }

    /** Lee un numero entero (>= 0). Si no es valido lanza NumberFormatException con un mensaje claro. */
    private int leerEntero(JTextField txt, String campo) {
        int valor;
        try {
            valor = Integer.parseInt(txt.getText().trim());
        } catch (NumberFormatException e) {
            throw new NumberFormatException(InterfazENEL.MSG_NUMERO_INVALIDO + campo);
        }
        if (valor < 0) {
            throw new NumberFormatException(InterfazENEL.MSG_NUMERO_INVALIDO + campo);
        }
        return valor;
    }

    // ===================== PESTAÑA 1: PUNTO DE VENTA =====================

    private void cargarTablaCarrito() {
        modeloCarrito.setRowCount(0);
        for (DetalleVenta objDetalle : objVenta.getDetalles()) {
            modeloCarrito.addRow(new Object[]{
                objDetalle.getServicio().getNombre(),
                objDetalle.getCantidad(),
                ControladorENEL.dinero(objDetalle.getPrecioUnitario()),
                ControladorENEL.dinero(objDetalle.getSubtotal())
            });
        }
        actualizarTotales();
    }

    private void cargarTablaVentas() {
        modeloVentas.setRowCount(0);
        for (Venta objVentaHecha : ControladorENEL.getVentas()) {
            modeloVentas.addRow(new Object[]{
                objVentaHecha.getFolio(),
                objVentaHecha.getFecha(),
                objVentaHecha.getNombreCliente(),
                objVentaHecha.getMetodoPago(),
                ControladorENEL.dinero(objVentaHecha.getTotal())
            });
        }
    }

    /** Primer renglon "Publico General" y despues los clientes registrados. */
    private void cargarComboClientes() {
        cmbClienteVenta.removeAllItems();
        cmbClienteVenta.addItem(InterfazENEL.PUBLICO_GENERAL);
        for (Cliente objCliente : ControladorENEL.getClientes()) {
            cmbClienteVenta.addItem(objCliente.toString());
        }
    }

    private void cargarComboServicios() {
        cmbServicioVenta.removeAllItems();
        for (Servicio objServicio : ControladorENEL.getServicios()) {
            cmbServicioVenta.addItem(objServicio.toString() + " - " + ControladorENEL.dinero(objServicio.getPrecio()));
        }
    }

    private void actualizarTotales() {
        lblSubtotal.setText(ControladorENEL.dinero(objVenta.getSubtotal()));
        lblIva.setText(ControladorENEL.dinero(objVenta.getIva()));
        lblTotal.setText(ControladorENEL.dinero(objVenta.getTotal()));
        actualizarCambio();
    }

    /** Cambio = recibido - total (si aun no alcanza, muestra $0.00). */
    private void actualizarCambio() {
        double cambio = 0;
        try {
            double recibido = Double.parseDouble(txtMontoRecibido.getText().trim());
            cambio = Math.max(0, recibido - objVenta.getTotal());
        } catch (NumberFormatException e) {
            cambio = 0;   // campo vacio o con letras
        }
        lblCambio.setText(ControladorENEL.dinero(cambio));
    }

    // ===================== PESTAÑA 2: CLIENTES =====================

    private void cargarTablaClientes() {
        modeloClientes.setRowCount(0);
        for (Cliente objCliente : ControladorENEL.getClientes()) {
            modeloClientes.addRow(new Object[]{
                objCliente.getId(),
                objCliente.getNombre(),
                objCliente.getTelefono(),
                objCliente.getDireccion(),
                objCliente.getTipoServicio()
            });
        }
        cargarComboClientes();   // el combo del POS siempre igual que la tabla
    }

    /** Lee y valida el formulario. Regresa null (y avisa) si algo esta mal. */
    private Cliente leerFormularioCliente() {
        int id;
        try {
            id = leerEntero(txtIdCliente, "ID");
        } catch (NumberFormatException ex) {
            mostrarError(ex.getMessage());
            return null;
        }
        String nombre = txtNombreCliente.getText().trim();
        String telefono = txtTelefonoCliente.getText().trim();
        String direccion = txtDireccionCliente.getText().trim();
        if (nombre.isEmpty() || telefono.isEmpty() || direccion.isEmpty()) {
            mostrarError(InterfazENEL.MSG_CAMPOS_VACIOS);
            return null;
        }
        if (!telefono.matches("\\d{10}")) {
            mostrarError(InterfazENEL.MSG_TELEFONO_INVALIDO);
            return null;
        }
        String tipo = (String) cmbTipoCliente.getSelectedItem();
        return new Cliente(id, nombre, telefono, direccion, tipo);
    }

    private void mostrarCliente(Cliente objCliente) {
        txtIdCliente.setText(String.valueOf(objCliente.getId()));
        txtNombreCliente.setText(objCliente.getNombre());
        txtTelefonoCliente.setText(objCliente.getTelefono());
        txtDireccionCliente.setText(objCliente.getDireccion());
        cmbTipoCliente.setSelectedItem(objCliente.getTipoServicio());
    }

    private void limpiarCliente() {
        txtIdCliente.setText("");
        txtNombreCliente.setText("");
        txtTelefonoCliente.setText("");
        txtDireccionCliente.setText("");
        cmbTipoCliente.setSelectedIndex(0);
        tblClientes.clearSelection();
    }

    // ===================== PESTAÑA 3: TRABAJADORES Y NOMINA =====================

    private void cargarTablaTrabajadores() {
        modeloTrabajadores.setRowCount(0);
        cmbTrabajadorNomina.removeAllItems();
        for (Trabajador objTrabajador : ControladorENEL.getTrabajadores()) {
            modeloTrabajadores.addRow(new Object[]{
                objTrabajador.getId(),
                objTrabajador.getNombre(),
                objTrabajador.getPuesto(),
                ControladorENEL.dinero(objTrabajador.getSueldoDia())
            });
            cmbTrabajadorNomina.addItem(objTrabajador.toString());
        }
    }

    private void cargarTablaNomina() {
        modeloNomina.setRowCount(0);
        for (Nomina objNomina : ControladorENEL.getNominas()) {
            modeloNomina.addRow(new Object[]{
                objNomina.getFolio(),
                objNomina.getFecha(),
                objNomina.getNombreTrabajador(),
                objNomina.getDiasTrabajados(),
                objNomina.getHorasExtra(),
                ControladorENEL.dinero(objNomina.getSueldoBase()),
                ControladorENEL.dinero(objNomina.getPagoHorasExtra()),
                ControladorENEL.dinero(objNomina.getDescuentos()),
                ControladorENEL.dinero(objNomina.getTotalNeto())
            });
        }
    }

    private void limpiarTrabajador() {
        txtIdTrabajador.setText("");
        txtNombreTrabajador.setText("");
        txtSueldoDia.setText("");
        cmbPuesto.setSelectedIndex(0);
    }

    // ===================== PESTAÑA 4: CATALOGO E INVENTARIO =====================

    private void cargarTablaCatalogo() {
        modeloCatalogo.setRowCount(0);
        for (Servicio objServicio : ControladorENEL.getServicios()) {
            modeloCatalogo.addRow(new Object[]{
                objServicio.getId(),
                objServicio.getTipo(),
                objServicio.getNombre(),
                ControladorENEL.dinero(objServicio.getPrecio()),
                objServicio.getStockTexto()
            });
        }
        cargarComboServicios();   // el combo del POS siempre igual que el catalogo
    }

    private void limpiarCatalogo() {
        txtIdCatalogo.setText("");
        txtNombreCatalogo.setText("");
        txtPrecioCatalogo.setText("");
        txtStockCatalogo.setText("");
        cmbTipoCatalogo.setSelectedIndex(0);
        tblCatalogo.clearSelection();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabPrincipal = new javax.swing.JTabbedPane();
        pnlVenta = new javax.swing.JPanel();
        lblClienteVenta = new javax.swing.JLabel();
        cmbClienteVenta = new javax.swing.JComboBox<>();
        lblServicioVenta = new javax.swing.JLabel();
        cmbServicioVenta = new javax.swing.JComboBox<>();
        lblCantidad = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        btnAgregarCarrito = new javax.swing.JButton();
        btnQuitarCarrito = new javax.swing.JButton();
        lblSubtotalTexto = new javax.swing.JLabel();
        lblSubtotal = new javax.swing.JLabel();
        lblIvaTexto = new javax.swing.JLabel();
        lblIva = new javax.swing.JLabel();
        lblTotalTexto = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        lblMetodoPago = new javax.swing.JLabel();
        cmbMetodoPago = new javax.swing.JComboBox<>();
        lblMontoRecibido = new javax.swing.JLabel();
        txtMontoRecibido = new javax.swing.JTextField();
        lblCambioTexto = new javax.swing.JLabel();
        lblCambio = new javax.swing.JLabel();
        btnCobrar = new javax.swing.JButton();
        scrCarrito = new javax.swing.JScrollPane();
        tblCarrito = new javax.swing.JTable();
        lblTituloTicket = new javax.swing.JLabel();
        scrTicket = new javax.swing.JScrollPane();
        txaTicket = new javax.swing.JTextArea();
        lblTituloVentas = new javax.swing.JLabel();
        scrVentas = new javax.swing.JScrollPane();
        tblVentas = new javax.swing.JTable();
        pnlClientes = new javax.swing.JPanel();
        lblIdCliente = new javax.swing.JLabel();
        txtIdCliente = new javax.swing.JTextField();
        lblNombreCliente = new javax.swing.JLabel();
        txtNombreCliente = new javax.swing.JTextField();
        lblTelefonoCliente = new javax.swing.JLabel();
        txtTelefonoCliente = new javax.swing.JTextField();
        lblDireccionCliente = new javax.swing.JLabel();
        txtDireccionCliente = new javax.swing.JTextField();
        lblTipoCliente = new javax.swing.JLabel();
        cmbTipoCliente = new javax.swing.JComboBox<>();
        btnAgregarCliente = new javax.swing.JButton();
        btnBuscarCliente = new javax.swing.JButton();
        btnEditarCliente = new javax.swing.JButton();
        btnEliminarCliente = new javax.swing.JButton();
        btnLimpiarCliente = new javax.swing.JButton();
        scrClientes = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        pnlNomina = new javax.swing.JPanel();
        lblTituloRegistro = new javax.swing.JLabel();
        lblIdTrabajador = new javax.swing.JLabel();
        txtIdTrabajador = new javax.swing.JTextField();
        lblNombreTrabajador = new javax.swing.JLabel();
        txtNombreTrabajador = new javax.swing.JTextField();
        lblPuesto = new javax.swing.JLabel();
        cmbPuesto = new javax.swing.JComboBox<>();
        lblSueldoDia = new javax.swing.JLabel();
        txtSueldoDia = new javax.swing.JTextField();
        btnAgregarTrabajador = new javax.swing.JButton();
        btnEliminarTrabajador = new javax.swing.JButton();
        scrTrabajadores = new javax.swing.JScrollPane();
        tblTrabajadores = new javax.swing.JTable();
        lblTituloNomina = new javax.swing.JLabel();
        lblTrabajadorNomina = new javax.swing.JLabel();
        cmbTrabajadorNomina = new javax.swing.JComboBox<>();
        lblDiasTrabajados = new javax.swing.JLabel();
        txtDiasTrabajados = new javax.swing.JTextField();
        lblHorasExtra = new javax.swing.JLabel();
        txtHorasExtra = new javax.swing.JTextField();
        lblDescuentos = new javax.swing.JLabel();
        txtDescuentos = new javax.swing.JTextField();
        btnCalcularPago = new javax.swing.JButton();
        scrNomina = new javax.swing.JScrollPane();
        tblNomina = new javax.swing.JTable();
        pnlCatalogo = new javax.swing.JPanel();
        lblIdCatalogo = new javax.swing.JLabel();
        txtIdCatalogo = new javax.swing.JTextField();
        lblTipoCatalogo = new javax.swing.JLabel();
        cmbTipoCatalogo = new javax.swing.JComboBox<>();
        lblNombreCatalogo = new javax.swing.JLabel();
        txtNombreCatalogo = new javax.swing.JTextField();
        lblPrecioCatalogo = new javax.swing.JLabel();
        txtPrecioCatalogo = new javax.swing.JTextField();
        lblStockCatalogo = new javax.swing.JLabel();
        txtStockCatalogo = new javax.swing.JTextField();
        btnAgregarCatalogo = new javax.swing.JButton();
        btnEditarCatalogo = new javax.swing.JButton();
        btnEliminarCatalogo = new javax.swing.JButton();
        btnLimpiarCatalogo = new javax.swing.JButton();
        scrCatalogo = new javax.swing.JScrollPane();
        tblCatalogo = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblClienteVenta.setText("Cliente:");

        lblServicioVenta.setText("Servicio / producto:");

        lblCantidad.setText("Cantidad / m²:");

        btnAgregarCarrito.setText("Agregar al carrito");
        btnAgregarCarrito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarCarritoActionPerformed(evt);
            }
        });

        btnQuitarCarrito.setText("Quitar seleccionado");
        btnQuitarCarrito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitarCarritoActionPerformed(evt);
            }
        });

        lblSubtotalTexto.setText("Subtotal:");

        lblSubtotal.setText("$0.00");

        lblIvaTexto.setText("IVA (16%):");

        lblIva.setText("$0.00");

        lblTotalTexto.setText("Total:");

        lblTotal.setText("$0.00");

        lblMetodoPago.setText("Método de pago:");

        cmbMetodoPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMetodoPagoActionPerformed(evt);
            }
        });

        lblMontoRecibido.setText("Monto recibido:");

        txtMontoRecibido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMontoRecibidoKeyReleased(evt);
            }
        });

        lblCambioTexto.setText("Cambio:");

        lblCambio.setText("$0.00");

        btnCobrar.setText("Cobrar");
        btnCobrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCobrarActionPerformed(evt);
            }
        });

        scrCarrito.setViewportView(tblCarrito);

        lblTituloTicket.setText("Ticket de la última venta");

        txaTicket.setColumns(20);
        txaTicket.setRows(5);
        txaTicket.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        scrTicket.setViewportView(txaTicket);

        lblTituloVentas.setText("Ventas registradas");

        scrVentas.setViewportView(tblVentas);

        javax.swing.GroupLayout pnlVentaLayout = new javax.swing.GroupLayout(pnlVenta);
        pnlVenta.setLayout(pnlVentaLayout);
        pnlVentaLayout.setHorizontalGroup(
            pnlVentaLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(pnlVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlVentaLayout.createSequentialGroup()
                    .addGroup(pnlVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblClienteVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblServicioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(cmbClienteVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbServicioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(pnlVentaLayout.createSequentialGroup()
                    .addComponent(btnAgregarCarrito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnQuitarCarrito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(scrCarrito, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                .addGroup(pnlVentaLayout.createSequentialGroup()
                    .addGroup(pnlVentaLayout.createSequentialGroup()
                        .addGroup(pnlVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSubtotalTexto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblIvaTexto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTotalTexto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblIva, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(40, 40, 40)
                    .addGroup(pnlVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlVentaLayout.createSequentialGroup()
                            .addGroup(pnlVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblMontoRecibido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblCambioTexto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(pnlVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(cmbMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtMontoRecibido, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblCambio, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(btnCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(pnlVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lblTituloTicket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(scrTicket, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                .addComponent(lblTituloVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(scrVentas, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE))
            .addContainerGap()
        );
        pnlVentaLayout.setVerticalGroup(
            pnlVentaLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(pnlVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlVentaLayout.createSequentialGroup()
                    .addGroup(pnlVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblClienteVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbClienteVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblServicioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbServicioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnAgregarCarrito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnQuitarCarrito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrCarrito, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(pnlVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlVentaLayout.createSequentialGroup()
                            .addGroup(pnlVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblSubtotalTexto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(pnlVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblIvaTexto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblIva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(pnlVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblTotalTexto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(pnlVentaLayout.createSequentialGroup()
                            .addGroup(pnlVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cmbMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(pnlVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblMontoRecibido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtMontoRecibido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(pnlVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblCambioTexto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblCambio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btnCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(pnlVentaLayout.createSequentialGroup()
                    .addComponent(lblTituloTicket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrTicket, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(lblTituloVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrVentas, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)))
            .addContainerGap()
        );

        tabPrincipal.addTab("Punto de Venta", pnlVenta);

        lblIdCliente.setText("ID:");

        lblNombreCliente.setText("Nombre / Empresa:");

        lblTelefonoCliente.setText("Teléfono:");

        lblDireccionCliente.setText("Dirección:");

        lblTipoCliente.setText("Tipo de servicio:");

        btnAgregarCliente.setText("Agregar");
        btnAgregarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarClienteActionPerformed(evt);
            }
        });

        btnBuscarCliente.setText("Buscar");
        btnBuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarClienteActionPerformed(evt);
            }
        });

        btnEditarCliente.setText("Editar");
        btnEditarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarClienteActionPerformed(evt);
            }
        });

        btnEliminarCliente.setText("Eliminar");
        btnEliminarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarClienteActionPerformed(evt);
            }
        });

        btnLimpiarCliente.setText("Limpiar");
        btnLimpiarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarClienteActionPerformed(evt);
            }
        });

        tblClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblClientesMouseClicked(evt);
            }
        });
        scrClientes.setViewportView(tblClientes);

        javax.swing.GroupLayout pnlClientesLayout = new javax.swing.GroupLayout(pnlClientes);
        pnlClientes.setLayout(pnlClientesLayout);
        pnlClientesLayout.setHorizontalGroup(
            pnlClientesLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(pnlClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlClientesLayout.createSequentialGroup()
                    .addGroup(pnlClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblIdCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblTelefonoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblDireccionCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblTipoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtIdCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTelefonoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDireccionCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbTipoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(pnlClientesLayout.createSequentialGroup()
                    .addComponent(btnAgregarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnEditarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnEliminarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnLimpiarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(scrClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE))
            .addContainerGap()
        );
        pnlClientesLayout.setVerticalGroup(
            pnlClientesLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(pnlClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lblIdCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtIdCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(pnlClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lblNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(pnlClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lblTelefonoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtTelefonoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(pnlClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lblDireccionCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtDireccionCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(pnlClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lblTipoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(cmbTipoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(pnlClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnAgregarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnEditarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnEliminarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnLimpiarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(scrClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
            .addContainerGap()
        );

        tabPrincipal.addTab("Clientes", pnlClientes);

        lblTituloRegistro.setText("Registro de personal");

        lblIdTrabajador.setText("ID:");

        lblNombreTrabajador.setText("Nombre:");

        lblPuesto.setText("Puesto:");

        lblSueldoDia.setText("Sueldo por día:");

        btnAgregarTrabajador.setText("Registrar trabajador");
        btnAgregarTrabajador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarTrabajadorActionPerformed(evt);
            }
        });

        btnEliminarTrabajador.setText("Eliminar seleccionado");
        btnEliminarTrabajador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarTrabajadorActionPerformed(evt);
            }
        });

        scrTrabajadores.setViewportView(tblTrabajadores);

        lblTituloNomina.setText("Cálculo de nómina");

        lblTrabajadorNomina.setText("Trabajador:");

        lblDiasTrabajados.setText("Días trabajados:");

        lblHorasExtra.setText("Horas extra:");

        lblDescuentos.setText("Descuentos ($):");

        btnCalcularPago.setText("Calcular y Registrar Pago");
        btnCalcularPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcularPagoActionPerformed(evt);
            }
        });

        scrNomina.setViewportView(tblNomina);

        javax.swing.GroupLayout pnlNominaLayout = new javax.swing.GroupLayout(pnlNomina);
        pnlNomina.setLayout(pnlNominaLayout);
        pnlNominaLayout.setHorizontalGroup(
            pnlNominaLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(pnlNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lblTituloRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(pnlNominaLayout.createSequentialGroup()
                    .addGroup(pnlNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblIdTrabajador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblNombreTrabajador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblPuesto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblSueldoDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtIdTrabajador, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtNombreTrabajador, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbPuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtSueldoDia, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(pnlNominaLayout.createSequentialGroup()
                    .addComponent(btnAgregarTrabajador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnEliminarTrabajador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(scrTrabajadores, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(pnlNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lblTituloNomina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(pnlNominaLayout.createSequentialGroup()
                    .addGroup(pnlNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblTrabajadorNomina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblDiasTrabajados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblHorasExtra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblDescuentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(cmbTrabajadorNomina, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDiasTrabajados, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtHorasExtra, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDescuentos, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addComponent(btnCalcularPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(scrNomina, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE))
            .addContainerGap()
        );
        pnlNominaLayout.setVerticalGroup(
            pnlNominaLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(pnlNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlNominaLayout.createSequentialGroup()
                    .addComponent(lblTituloRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblIdTrabajador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtIdTrabajador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblNombreTrabajador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtNombreTrabajador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblPuesto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbPuesto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblSueldoDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtSueldoDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(pnlNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnAgregarTrabajador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnEliminarTrabajador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(scrTrabajadores, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                .addGroup(pnlNominaLayout.createSequentialGroup()
                    .addComponent(lblTituloNomina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblTrabajadorNomina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbTrabajadorNomina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblDiasTrabajados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDiasTrabajados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblHorasExtra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtHorasExtra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblDescuentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDescuentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(btnCalcularPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(scrNomina, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)))
            .addContainerGap()
        );

        tabPrincipal.addTab("Trabajadores y Nómina", pnlNomina);

        lblIdCatalogo.setText("ID:");

        lblTipoCatalogo.setText("Tipo:");

        cmbTipoCatalogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTipoCatalogoActionPerformed(evt);
            }
        });

        lblNombreCatalogo.setText("Nombre:");

        lblPrecioCatalogo.setText("Precio base:");

        lblStockCatalogo.setText("Stock (insumos):");

        btnAgregarCatalogo.setText("Agregar");
        btnAgregarCatalogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarCatalogoActionPerformed(evt);
            }
        });

        btnEditarCatalogo.setText("Editar");
        btnEditarCatalogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarCatalogoActionPerformed(evt);
            }
        });

        btnEliminarCatalogo.setText("Eliminar");
        btnEliminarCatalogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarCatalogoActionPerformed(evt);
            }
        });

        btnLimpiarCatalogo.setText("Limpiar");
        btnLimpiarCatalogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarCatalogoActionPerformed(evt);
            }
        });

        tblCatalogo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCatalogoMouseClicked(evt);
            }
        });
        scrCatalogo.setViewportView(tblCatalogo);

        javax.swing.GroupLayout pnlCatalogoLayout = new javax.swing.GroupLayout(pnlCatalogo);
        pnlCatalogo.setLayout(pnlCatalogoLayout);
        pnlCatalogoLayout.setHorizontalGroup(
            pnlCatalogoLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(pnlCatalogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlCatalogoLayout.createSequentialGroup()
                    .addGroup(pnlCatalogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblIdCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblTipoCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblNombreCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblPrecioCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblStockCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlCatalogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtIdCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbTipoCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtNombreCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtPrecioCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtStockCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(pnlCatalogoLayout.createSequentialGroup()
                    .addComponent(btnAgregarCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnEditarCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnEliminarCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnLimpiarCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(scrCatalogo, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE))
            .addContainerGap()
        );
        pnlCatalogoLayout.setVerticalGroup(
            pnlCatalogoLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(pnlCatalogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lblIdCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtIdCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(pnlCatalogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lblTipoCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(cmbTipoCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(pnlCatalogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lblNombreCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtNombreCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(pnlCatalogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lblPrecioCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtPrecioCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(pnlCatalogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lblStockCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtStockCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(pnlCatalogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnAgregarCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnEditarCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnEliminarCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnLimpiarCatalogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(scrCatalogo, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
            .addContainerGap()
        );

        tabPrincipal.addTab("Servicios e Inventario", pnlCatalogo);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 1020, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarCarritoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarCarritoActionPerformed
        int indice = cmbServicioVenta.getSelectedIndex();
        if (indice < 0) {
            mostrarError(InterfazENEL.MSG_SELECCIONA_OPCION);
            return;
        }
        Servicio objServicio = ControladorENEL.getServicios().get(indice);

        double cantidad;
        try {
            cantidad = leerNumero(txtCantidad, "Cantidad");
        } catch (NumberFormatException ex) {
            mostrarError(ex.getMessage());
            return;
        }
        if (cantidad <= 0) {
            mostrarError(InterfazENEL.MSG_CANTIDAD_INVALIDA);
            return;
        }

        // Los insumos van por pieza y no pueden pasar del stock disponible
        if (objServicio.esInsumo()) {
            if (cantidad != Math.floor(cantidad)) {
                mostrarError(InterfazENEL.MSG_PIEZA_ENTERA);
                return;
            }
            if (cantidad + objVenta.cantidadDe(objServicio) > objServicio.getStock()) {
                mostrarError(InterfazENEL.MSG_STOCK_INSUFICIENTE + objServicio.getNombre()
                        + " (disponibles: " + objServicio.getStock() + ")");
                return;
            }
        }

        objVenta.agregarDetalle(new DetalleVenta(objServicio, cantidad));
        cargarTablaCarrito();
        txtCantidad.setText("");
    }//GEN-LAST:event_btnAgregarCarritoActionPerformed

    private void btnQuitarCarritoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarCarritoActionPerformed
        int fila = tblCarrito.getSelectedRow();
        if (fila < 0) {
            mostrarError(InterfazENEL.MSG_SELECCIONA_FILA);
            return;
        }
        objVenta.quitarDetalle(fila);
        cargarTablaCarrito();
    }//GEN-LAST:event_btnQuitarCarritoActionPerformed

    private void cmbMetodoPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMetodoPagoActionPerformed
        // Solo con efectivo se escribe el monto recibido; con tarjeta o transferencia se paga exacto
        boolean esEfectivo = InterfazENEL.METODO_EFECTIVO.equals(cmbMetodoPago.getSelectedItem());
        txtMontoRecibido.setEnabled(esEfectivo);
        if (!esEfectivo) {
            txtMontoRecibido.setText("");
        }
        actualizarCambio();
    }//GEN-LAST:event_cmbMetodoPagoActionPerformed

    private void txtMontoRecibidoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMontoRecibidoKeyReleased
        actualizarCambio();
    }//GEN-LAST:event_txtMontoRecibidoKeyReleased

    private void btnCobrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCobrarActionPerformed
        if (objVenta.estaVacia()) {
            mostrarError(InterfazENEL.MSG_CARRITO_VACIO);
            return;
        }
        String faltante = ControladorENEL.validarStock(objVenta);
        if (faltante != null) {
            mostrarError(InterfazENEL.MSG_STOCK_INSUFICIENTE + faltante);
            return;
        }

        // Monto recibido: en efectivo se valida; con otro metodo se cobra exacto
        String metodoPago = (String) cmbMetodoPago.getSelectedItem();
        double montoRecibido = objVenta.getTotal();
        if (InterfazENEL.METODO_EFECTIVO.equals(metodoPago)) {
            try {
                montoRecibido = leerNumero(txtMontoRecibido, "Monto recibido");
            } catch (NumberFormatException ex) {
                mostrarError(ex.getMessage());
                return;
            }
            if (montoRecibido < objVenta.getTotal()) {
                mostrarError(InterfazENEL.MSG_PAGO_INSUFICIENTE);
                return;
            }
        }

        // Cliente: el renglon 0 es "Publico General"; los demas son la lista de clientes
        int indiceCliente = cmbClienteVenta.getSelectedIndex();
        if (indiceCliente > 0) {
            Cliente objCliente = ControladorENEL.getClientes().get(indiceCliente - 1);
            objVenta.setNombreCliente(objCliente.getNombre());
        } else {
            objVenta.setNombreCliente(InterfazENEL.PUBLICO_GENERAL);
        }

        // Guardar, descontar stock y mostrar el ticket
        ControladorENEL.registrarVenta(objVenta, metodoPago, montoRecibido);
        txaTicket.setText(objVenta.generarTicket());
        txaTicket.setCaretPosition(0);
        mostrarAviso(InterfazENEL.MSG_VENTA_REGISTRADA);

        // Empezar una venta nueva
        objVenta = new Venta();
        txtMontoRecibido.setText("");
        cmbClienteVenta.setSelectedIndex(0);
        cargarTablaCarrito();
        cargarTablaVentas();
        cargarTablaCatalogo();   // el stock cambio
    }//GEN-LAST:event_btnCobrarActionPerformed

    private void btnAgregarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarClienteActionPerformed
        Cliente objCliente = leerFormularioCliente();
        if (objCliente == null) {
            return;
        }
        if (!ControladorENEL.agregarCliente(objCliente)) {
            mostrarError(InterfazENEL.MSG_ID_REPETIDO);
            return;
        }
        cargarTablaClientes();
        limpiarCliente();
        mostrarAviso(InterfazENEL.MSG_GUARDADO);
    }//GEN-LAST:event_btnAgregarClienteActionPerformed

    private void btnBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarClienteActionPerformed
        int id;
        try {
            id = leerEntero(txtIdCliente, "ID");
        } catch (NumberFormatException ex) {
            mostrarError(ex.getMessage());
            return;
        }
        Cliente objCliente = ControladorENEL.buscarCliente(id);
        if (objCliente == null) {
            mostrarError(InterfazENEL.MSG_NO_ENCONTRADO);
            return;
        }
        mostrarCliente(objCliente);
    }//GEN-LAST:event_btnBuscarClienteActionPerformed

    private void btnEditarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarClienteActionPerformed
        Cliente objCliente = leerFormularioCliente();
        if (objCliente == null) {
            return;
        }
        if (!ControladorENEL.editarCliente(objCliente)) {
            mostrarError(InterfazENEL.MSG_NO_ENCONTRADO);
            return;
        }
        cargarTablaClientes();
        limpiarCliente();
        mostrarAviso(InterfazENEL.MSG_ACTUALIZADO);
    }//GEN-LAST:event_btnEditarClienteActionPerformed

    private void btnEliminarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarClienteActionPerformed
        int id;
        try {
            id = leerEntero(txtIdCliente, "ID");
        } catch (NumberFormatException ex) {
            mostrarError(ex.getMessage());
            return;
        }
        if (ControladorENEL.buscarCliente(id) == null) {
            mostrarError(InterfazENEL.MSG_NO_ENCONTRADO);
            return;
        }
        if (confirmarEliminar()) {
            ControladorENEL.eliminarCliente(id);
            cargarTablaClientes();
            limpiarCliente();
            mostrarAviso(InterfazENEL.MSG_ELIMINADO);
        }
    }//GEN-LAST:event_btnEliminarClienteActionPerformed

    private void btnLimpiarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarClienteActionPerformed
        limpiarCliente();
    }//GEN-LAST:event_btnLimpiarClienteActionPerformed

    private void tblClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClientesMouseClicked
        // Al dar clic en un renglon, sus datos pasan al formulario
        int fila = tblClientes.getSelectedRow();
        if (fila >= 0) {
            mostrarCliente(ControladorENEL.getClientes().get(fila));
        }
    }//GEN-LAST:event_tblClientesMouseClicked

    private void btnAgregarTrabajadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarTrabajadorActionPerformed
        int id;
        double sueldoDia;
        try {
            id = leerEntero(txtIdTrabajador, "ID");
            sueldoDia = leerNumero(txtSueldoDia, "Sueldo por día");
        } catch (NumberFormatException ex) {
            mostrarError(ex.getMessage());
            return;
        }
        String nombre = txtNombreTrabajador.getText().trim();
        if (nombre.isEmpty() || sueldoDia == 0) {
            mostrarError(InterfazENEL.MSG_CAMPOS_VACIOS);
            return;
        }
        String puesto = (String) cmbPuesto.getSelectedItem();
        Trabajador objTrabajador = new Trabajador(id, nombre, puesto, sueldoDia);
        if (!ControladorENEL.agregarTrabajador(objTrabajador)) {
            mostrarError(InterfazENEL.MSG_ID_REPETIDO);
            return;
        }
        cargarTablaTrabajadores();
        limpiarTrabajador();
        mostrarAviso(InterfazENEL.MSG_GUARDADO);
    }//GEN-LAST:event_btnAgregarTrabajadorActionPerformed

    private void btnEliminarTrabajadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarTrabajadorActionPerformed
        int fila = tblTrabajadores.getSelectedRow();
        if (fila < 0) {
            mostrarError(InterfazENEL.MSG_SELECCIONA_FILA);
            return;
        }
        if (confirmarEliminar()) {
            Trabajador objTrabajador = ControladorENEL.getTrabajadores().get(fila);
            ControladorENEL.eliminarTrabajador(objTrabajador.getId());
            cargarTablaTrabajadores();
            mostrarAviso(InterfazENEL.MSG_ELIMINADO);
        }
    }//GEN-LAST:event_btnEliminarTrabajadorActionPerformed

    private void btnCalcularPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcularPagoActionPerformed
        int indice = cmbTrabajadorNomina.getSelectedIndex();
        if (indice < 0) {
            mostrarError(InterfazENEL.MSG_SELECCIONA_OPCION);
            return;
        }
        Trabajador objTrabajador = ControladorENEL.getTrabajadores().get(indice);

        int dias;
        double horasExtra;
        double descuentos;
        try {
            dias = leerEntero(txtDiasTrabajados, "Días trabajados");
            horasExtra = leerNumero(txtHorasExtra, "Horas extra");
            descuentos = leerNumero(txtDescuentos, "Descuentos");
        } catch (NumberFormatException ex) {
            mostrarError(ex.getMessage());
            return;
        }
        if (dias > 7) {
            mostrarError(InterfazENEL.MSG_DIAS_INVALIDOS);
            return;
        }

        // Se calcula primero para revisar que el neto no quede negativo
        Nomina objNomina = new Nomina(objTrabajador, dias, horasExtra, descuentos);
        if (objNomina.getTotalNeto() < 0) {
            mostrarError(InterfazENEL.MSG_NETO_NEGATIVO);
            return;
        }
        ControladorENEL.registrarNomina(objNomina);
        cargarTablaNomina();

        txtDiasTrabajados.setText("");
        txtHorasExtra.setText("0");
        txtDescuentos.setText("0");
        mostrarAviso(InterfazENEL.MSG_NOMINA_REGISTRADA + ControladorENEL.dinero(objNomina.getTotalNeto()));
    }//GEN-LAST:event_btnCalcularPagoActionPerformed

    private void cmbTipoCatalogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTipoCatalogoActionPerformed
        // El stock solo aplica a los insumos
        boolean esInsumo = InterfazENEL.TIPO_INSUMO.equals(cmbTipoCatalogo.getSelectedItem());
        txtStockCatalogo.setEnabled(esInsumo);
        if (!esInsumo) {
            txtStockCatalogo.setText("");
        }
    }//GEN-LAST:event_cmbTipoCatalogoActionPerformed

    private void btnAgregarCatalogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarCatalogoActionPerformed
        String nombre = txtNombreCatalogo.getText().trim();
        String tipo = (String) cmbTipoCatalogo.getSelectedItem();
        double precio;
        int stock = 0;
        try {
            precio = leerNumero(txtPrecioCatalogo, "Precio");
            if (InterfazENEL.TIPO_INSUMO.equals(tipo)) {
                stock = leerEntero(txtStockCatalogo, "Stock");
            }
        } catch (NumberFormatException ex) {
            mostrarError(ex.getMessage());
            return;
        }
        if (nombre.isEmpty() || precio == 0) {
            mostrarError(InterfazENEL.MSG_CAMPOS_VACIOS);
            return;
        }
        ControladorENEL.agregarServicio(nombre, tipo, precio, stock);
        cargarTablaCatalogo();
        limpiarCatalogo();
        mostrarAviso(InterfazENEL.MSG_GUARDADO);
    }//GEN-LAST:event_btnAgregarCatalogoActionPerformed

    private void btnEditarCatalogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarCatalogoActionPerformed
        if (txtIdCatalogo.getText().isEmpty()) {
            mostrarError(InterfazENEL.MSG_SELECCIONA_FILA);
            return;
        }
        int id = Integer.parseInt(txtIdCatalogo.getText());
        String nombre = txtNombreCatalogo.getText().trim();
        String tipo = (String) cmbTipoCatalogo.getSelectedItem();
        double precio;
        int stock = 0;
        try {
            precio = leerNumero(txtPrecioCatalogo, "Precio");
            if (InterfazENEL.TIPO_INSUMO.equals(tipo)) {
                stock = leerEntero(txtStockCatalogo, "Stock");
            }
        } catch (NumberFormatException ex) {
            mostrarError(ex.getMessage());
            return;
        }
        if (nombre.isEmpty() || precio == 0) {
            mostrarError(InterfazENEL.MSG_CAMPOS_VACIOS);
            return;
        }
        ControladorENEL.editarServicio(id, nombre, tipo, precio, stock);
        cargarTablaCatalogo();
        limpiarCatalogo();
        mostrarAviso(InterfazENEL.MSG_ACTUALIZADO);
    }//GEN-LAST:event_btnEditarCatalogoActionPerformed

    private void btnEliminarCatalogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarCatalogoActionPerformed
        if (txtIdCatalogo.getText().isEmpty()) {
            mostrarError(InterfazENEL.MSG_SELECCIONA_FILA);
            return;
        }
        if (confirmarEliminar()) {
            ControladorENEL.eliminarServicio(Integer.parseInt(txtIdCatalogo.getText()));
            cargarTablaCatalogo();
            limpiarCatalogo();
            mostrarAviso(InterfazENEL.MSG_ELIMINADO);
        }
    }//GEN-LAST:event_btnEliminarCatalogoActionPerformed

    private void btnLimpiarCatalogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarCatalogoActionPerformed
        limpiarCatalogo();
    }//GEN-LAST:event_btnLimpiarCatalogoActionPerformed

    private void tblCatalogoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCatalogoMouseClicked
        int fila = tblCatalogo.getSelectedRow();
        if (fila < 0) {
            return;
        }
        Servicio objServicio = ControladorENEL.getServicios().get(fila);
        txtIdCatalogo.setText(String.valueOf(objServicio.getId()));
        txtNombreCatalogo.setText(objServicio.getNombre());
        txtPrecioCatalogo.setText(String.valueOf(objServicio.getPrecio()));
        cmbTipoCatalogo.setSelectedItem(objServicio.getTipo());   // esto activa o desactiva el stock
        txtStockCatalogo.setText(objServicio.esInsumo() ? String.valueOf(objServicio.getStock()) : "");
    }//GEN-LAST:event_tblCatalogoMouseClicked

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
            java.util.logging.Logger.getLogger(ProyectoFinal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProyectoFinal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProyectoFinal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProyectoFinal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ProyectoFinal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarCarrito;
    private javax.swing.JButton btnAgregarCatalogo;
    private javax.swing.JButton btnAgregarCliente;
    private javax.swing.JButton btnAgregarTrabajador;
    private javax.swing.JButton btnBuscarCliente;
    private javax.swing.JButton btnCalcularPago;
    private javax.swing.JButton btnCobrar;
    private javax.swing.JButton btnEditarCatalogo;
    private javax.swing.JButton btnEditarCliente;
    private javax.swing.JButton btnEliminarCatalogo;
    private javax.swing.JButton btnEliminarCliente;
    private javax.swing.JButton btnEliminarTrabajador;
    private javax.swing.JButton btnLimpiarCatalogo;
    private javax.swing.JButton btnLimpiarCliente;
    private javax.swing.JButton btnQuitarCarrito;
    private javax.swing.JComboBox<String> cmbClienteVenta;
    private javax.swing.JComboBox<String> cmbMetodoPago;
    private javax.swing.JComboBox<String> cmbPuesto;
    private javax.swing.JComboBox<String> cmbServicioVenta;
    private javax.swing.JComboBox<String> cmbTipoCatalogo;
    private javax.swing.JComboBox<String> cmbTipoCliente;
    private javax.swing.JComboBox<String> cmbTrabajadorNomina;
    private javax.swing.JLabel lblCambio;
    private javax.swing.JLabel lblCambioTexto;
    private javax.swing.JLabel lblCantidad;
    private javax.swing.JLabel lblClienteVenta;
    private javax.swing.JLabel lblDescuentos;
    private javax.swing.JLabel lblDiasTrabajados;
    private javax.swing.JLabel lblDireccionCliente;
    private javax.swing.JLabel lblHorasExtra;
    private javax.swing.JLabel lblIdCatalogo;
    private javax.swing.JLabel lblIdCliente;
    private javax.swing.JLabel lblIdTrabajador;
    private javax.swing.JLabel lblIva;
    private javax.swing.JLabel lblIvaTexto;
    private javax.swing.JLabel lblMetodoPago;
    private javax.swing.JLabel lblMontoRecibido;
    private javax.swing.JLabel lblNombreCatalogo;
    private javax.swing.JLabel lblNombreCliente;
    private javax.swing.JLabel lblNombreTrabajador;
    private javax.swing.JLabel lblPrecioCatalogo;
    private javax.swing.JLabel lblPuesto;
    private javax.swing.JLabel lblServicioVenta;
    private javax.swing.JLabel lblStockCatalogo;
    private javax.swing.JLabel lblSubtotal;
    private javax.swing.JLabel lblSubtotalTexto;
    private javax.swing.JLabel lblSueldoDia;
    private javax.swing.JLabel lblTelefonoCliente;
    private javax.swing.JLabel lblTipoCatalogo;
    private javax.swing.JLabel lblTipoCliente;
    private javax.swing.JLabel lblTituloNomina;
    private javax.swing.JLabel lblTituloRegistro;
    private javax.swing.JLabel lblTituloTicket;
    private javax.swing.JLabel lblTituloVentas;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalTexto;
    private javax.swing.JLabel lblTrabajadorNomina;
    private javax.swing.JPanel pnlCatalogo;
    private javax.swing.JPanel pnlClientes;
    private javax.swing.JPanel pnlNomina;
    private javax.swing.JPanel pnlVenta;
    private javax.swing.JScrollPane scrCarrito;
    private javax.swing.JScrollPane scrCatalogo;
    private javax.swing.JScrollPane scrClientes;
    private javax.swing.JScrollPane scrNomina;
    private javax.swing.JScrollPane scrTicket;
    private javax.swing.JScrollPane scrTrabajadores;
    private javax.swing.JScrollPane scrVentas;
    private javax.swing.JTabbedPane tabPrincipal;
    private javax.swing.JTable tblCarrito;
    private javax.swing.JTable tblCatalogo;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTable tblNomina;
    private javax.swing.JTable tblTrabajadores;
    private javax.swing.JTable tblVentas;
    private javax.swing.JTextArea txaTicket;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtDescuentos;
    private javax.swing.JTextField txtDiasTrabajados;
    private javax.swing.JTextField txtDireccionCliente;
    private javax.swing.JTextField txtHorasExtra;
    private javax.swing.JTextField txtIdCatalogo;
    private javax.swing.JTextField txtIdCliente;
    private javax.swing.JTextField txtIdTrabajador;
    private javax.swing.JTextField txtMontoRecibido;
    private javax.swing.JTextField txtNombreCatalogo;
    private javax.swing.JTextField txtNombreCliente;
    private javax.swing.JTextField txtNombreTrabajador;
    private javax.swing.JTextField txtPrecioCatalogo;
    private javax.swing.JTextField txtStockCatalogo;
    private javax.swing.JTextField txtSueldoDia;
    private javax.swing.JTextField txtTelefonoCliente;
    // End of variables declaration//GEN-END:variables
}
